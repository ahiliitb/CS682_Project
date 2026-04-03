#!/usr/bin/env python3
"""Fetch Latest News from IIT Bombay public site; write JSON for CampusConnect static data."""

from __future__ import annotations

import json
import logging
import re
import sys
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import urljoin

import yaml
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager

logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
logger = logging.getLogger(__name__)

SCRIPT_DIR = Path(__file__).resolve().parent


def strip_text(s: str | None) -> str:
    if not s:
        return ""
    t = re.sub(r"\s+", " ", s).strip()
    return t


def load_config() -> dict:
    cfg_path = SCRIPT_DIR / "config.yaml"
    with open(cfg_path, encoding="utf-8") as f:
        return yaml.safe_load(f)


def main() -> int:
    cfg = load_config()
    url = cfg["url"]
    container = cfg["container_selector"]
    item_sel = cfg["item_selector"]
    title_sel = cfg["title_selector"]
    url_sel = cfg["url_selector"]
    date_sel = cfg["date_selector"]
    max_items = int(cfg.get("max_items", 10))
    out_rel = cfg["output_relative"]
    out_path = (SCRIPT_DIR / out_rel).resolve()

    options = webdriver.ChromeOptions()
    options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1400,2000")

    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    items: list[dict[str, str]] = []
    try:
        driver.set_page_load_timeout(60)
        logger.info("Loading %s", url)
        driver.get(url)
        wait = WebDriverWait(driver, 30)
        wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, container)))
        root = driver.find_element(By.CSS_SELECTOR, container)
        rows = root.find_elements(By.CSS_SELECTOR, item_sel)
        for row in rows[:max_items]:
            try:
                title_el = row.find_element(By.CSS_SELECTOR, title_sel)
                title = strip_text(title_el.text)
                link_el = row.find_element(By.CSS_SELECTOR, url_sel)
                href = strip_text(link_el.get_attribute("href") or "")
                if href and not href.startswith("http"):
                    href = urljoin(url, href)
                date_txt = ""
                try:
                    date_el = row.find_element(By.CSS_SELECTOR, date_sel)
                    date_txt = strip_text(date_el.text)
                except Exception:
                    pass
                if title and href:
                    items.append({"title": title, "url": href, "date": date_txt})
            except Exception as e:
                logger.warning("Skip row: %s", e)
    finally:
        driver.quit()

    payload = {
        "source": url,
        "fetchedAt": datetime.now(timezone.utc).isoformat(),
        "items": items,
    }
    out_path.parent.mkdir(parents=True, exist_ok=True)
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(payload, f, ensure_ascii=False, indent=2)
        f.write("\n")
    logger.info("Wrote %d items to %s", len(items), out_path)
    return 0 if items else 1


if __name__ == "__main__":
    sys.exit(main())
