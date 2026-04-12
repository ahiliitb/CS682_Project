#!/usr/bin/env python3
"""Log in (or register then log in) and exercise dashboard sections (buy/sell, pooling, library, …)."""

from __future__ import annotations

import argparse
import logging
import random
import sys
import time
from typing import Callable
from urllib.parse import urljoin

from selenium import webdriver
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select, WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager

logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
logger = logging.getLogger(__name__)

ITEM_NAMES = ("table", "chair", "book")

FLOW_ORDER = ("buysell", "pooling", "library", "lostfound", "auction", "teams", "ratings", "stats")

FLOW_ALIASES = {"polling": "pooling"}


def pause_visible(headless: bool) -> None:
    if not headless:
        time.sleep(random.uniform(1.0, 2.0))


def build_driver(headless: bool) -> webdriver.Chrome:
    options = webdriver.ChromeOptions()
    if headless:
        options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1400,1200")

    service = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=service, options=options)


def parse_flows(arg: str) -> list[str]:
    raw = [p.strip().lower() for p in arg.split(",") if p.strip()]
    if not raw:
        return list(FLOW_ORDER)
    out: list[str] = []
    for p in raw:
        p = FLOW_ALIASES.get(p, p)
        if p == "all":
            return list(FLOW_ORDER)
        out.append(p)
    unknown = [f for f in out if f not in FLOW_ORDER]
    if unknown:
        raise SystemExit(f"Unknown flow(s): {unknown}. Use: {', '.join(FLOW_ORDER)} or all")
    return out


def credential_defaults(account: int) -> tuple[str, str, str]:
    """Username, password, and @iitb.ac.in email for ahilkhan1, ahilkhan2, …"""
    base = f"ahilkhan{account}"
    return base, base, f"{base}@iitb.ac.in"


def default_register_email(explicit: str | None, account: int) -> str:
    if explicit and explicit.strip():
        return explicit.strip()
    return f"ahilkhan{account}@iitb.ac.in"


def logout_if_logged_in(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    """If a session already exists, log out so registration can run."""
    driver.get(base)
    pause_visible(headless)

    on_dashboard = "dashboard" in driver.current_url
    logout_btn = driver.find_elements(By.CSS_SELECTOR, "button.btn-logout")
    logout_form_btn = driver.find_elements(By.XPATH, "//form[contains(@action,'logout')]//button[@type='submit']")

    if not on_dashboard and not logout_btn and not logout_form_btn:
        return

    if not logout_btn and logout_form_btn:
        logout_btn = logout_form_btn

    if not logout_btn:
        return

    logger.info("Already signed in; logging out before registration.")
    logout_btn[0].click()
    try:
        WebDriverWait(driver, 15).until(
            EC.any_of(
                EC.url_contains("/login"),
                EC.presence_of_element_located((By.ID, "username")),
            )
        )
    except TimeoutException:
        logger.warning("Logout: unexpected page after logout: %s", driver.current_url)
    pause_visible(headless)


def register_account(
    driver: webdriver.Chrome,
    wait: WebDriverWait,
    base: str,
    username: str,
    email: str,
    password: str,
    headless: bool,
) -> bool:
    register_url = urljoin(base, "register")
    logger.info("Opening registration: %s (user=%s email=%s)", register_url, username, email)
    driver.get(register_url)
    pause_visible(headless)

    wait.until(EC.presence_of_element_located((By.ID, "username")))
    driver.find_element(By.ID, "username").clear()
    driver.find_element(By.ID, "username").send_keys(username)
    driver.find_element(By.ID, "email").clear()
    driver.find_element(By.ID, "email").send_keys(email)
    driver.find_element(By.ID, "password").clear()
    driver.find_element(By.ID, "password").send_keys(password)
    driver.find_element(By.ID, "confirmPassword").clear()
    driver.find_element(By.ID, "confirmPassword").send_keys(password)
    pause_visible(headless)

    driver.find_element(By.XPATH, "//form[contains(@action,'signup')]//button[@type='submit']").click()

    outcome = WebDriverWait(driver, 20)
    try:
        outcome.until(
            EC.any_of(
                EC.url_contains("/login"),
                EC.presence_of_element_located((By.CSS_SELECTOR, ".message.error")),
            )
        )
    except TimeoutException:
        logger.error("Registration: timeout. URL=%s", driver.current_url)
        return False

    err = driver.find_elements(By.CSS_SELECTOR, ".message.error")
    if err:
        logger.error("Registration failed: %s", err[0].text)
        return False

    if "/login" not in driver.current_url:
        logger.error("Registration: expected redirect to /login, got %s", driver.current_url)
        return False

    logger.info("Account created; on login page.")
    pause_visible(headless)
    return True


def wait_flash(driver: webdriver.Chrome, wait: WebDriverWait, short: bool = False) -> None:
    w = WebDriverWait(driver, 12 if short else 20)
    try:
        w.until(
            EC.any_of(
                EC.presence_of_element_located((By.CSS_SELECTOR, ".flash-message.success")),
                EC.presence_of_element_located((By.CSS_SELECTOR, ".flash-message.error")),
                EC.presence_of_element_located((By.CSS_SELECTOR, ".message.error")),
            )
        )
    except TimeoutException:
        logger.warning("No flash message (timeout). URL=%s", driver.current_url)
        return
    err = driver.find_elements(By.CSS_SELECTOR, ".flash-message.error")
    if err:
        logger.warning("Page message: %s", err[0].text)
    else:
        ok = driver.find_elements(By.CSS_SELECTOR, ".flash-message.success")
        if ok:
            logger.info("Success: %s", ok[0].text[:200])


def login(driver: webdriver.Chrome, wait: WebDriverWait, base: str, username: str, password: str, headless: bool) -> bool:
    login_url = urljoin(base, "login")
    logger.info("Opening login: %s", login_url)
    driver.get(login_url)
    pause_visible(headless)

    wait.until(EC.presence_of_element_located((By.ID, "username"))).clear()
    driver.find_element(By.ID, "username").send_keys(username)
    driver.find_element(By.ID, "password").clear()
    driver.find_element(By.ID, "password").send_keys(password)
    pause_visible(headless)

    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()

    try:
        wait.until(EC.url_contains("/dashboard"))
    except Exception:
        err = driver.find_elements(By.CSS_SELECTOR, ".message.error")
        msg = err[0].text if err else "(no error message element)"
        logger.error("Login failed. URL=%s — %s", driver.current_url, msg)
        return False
    logger.info("Logged in: %s", driver.current_url)
    pause_visible(headless)
    return True


def flow_buysell(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard"))
    pause_visible(headless)
    item_name = random.choice(ITEM_NAMES)
    price = round(random.uniform(1.0, 500.0), 2)
    wait.until(EC.presence_of_element_located((By.ID, "listingName")))
    driver.find_element(By.ID, "listingName").clear()
    driver.find_element(By.ID, "listingName").send_keys(item_name)
    driver.find_element(By.ID, "listingPrice").clear()
    driver.find_element(By.ID, "listingPrice").send_keys(str(price))
    pause_visible(headless)
    listing_form = driver.find_element(By.ID, "listingName").find_element(By.XPATH, "./ancestor::form")
    listing_form.find_element(By.CSS_SELECTOR, "button.btn-primary[type='submit']").click()
    wait_flash(driver, wait)
    logger.info("Buy/Sell: listed %r at Rs. %s", item_name, price)


def flow_pooling(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard/pooling"))
    pause_visible(headless)
    wait.until(EC.presence_of_element_located((By.ID, "poolTitle")))
    driver.find_element(By.ID, "poolTitle").send_keys(f"Campus trip {random.randint(100, 999)}")
    driver.find_element(By.ID, "poolDeparture").send_keys(random.choice(("Fri 6 PM", "Sat 9 AM", "Sun 4 PM")))
    driver.find_element(By.ID, "poolOrigin").send_keys(random.choice(("Main gate", "Hostel 3", "Library circle")))
    driver.find_element(By.ID, "poolDestination").send_keys(random.choice(("Airport", "Andheri", "Powai")))
    driver.find_element(By.ID, "poolDetails").send_keys("Selenium test — seats and timing flexible.")
    pause_visible(headless)
    driver.find_element(By.XPATH, "//button[contains(.,'Post Pooling Request')]").click()
    wait_flash(driver, wait)
    logger.info("Pooling: request posted")


def flow_library(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard/library"))
    pause_visible(headless)
    wait.until(EC.presence_of_element_located((By.ID, "bookTitle")))
    books = ("Introduction to Algorithms", "Computer Networks", "Operating Systems Concepts")
    driver.find_element(By.ID, "bookTitle").send_keys(random.choice(books))
    driver.find_element(By.ID, "bookAuthor").send_keys(random.choice(("A. Author", "B. Kumar", "Campus Press")))
    driver.find_element(By.ID, "bookCourse").send_keys(random.choice(("CS682", "CS213", "MA105")))
    driver.find_element(By.ID, "bookCondition").send_keys("Good — selenium test listing.")
    pause_visible(headless)
    driver.find_element(By.XPATH, "//button[contains(.,'Add Book')]").click()
    wait_flash(driver, wait)
    logger.info("Library: book added")


def flow_lostfound(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard/lost-found"))
    pause_visible(headless)
    wait.until(EC.presence_of_element_located((By.ID, "lfType")))
    Select(driver.find_element(By.ID, "lfType")).select_by_value(random.choice(("LOST", "FOUND")))
    driver.find_element(By.ID, "lfTitle").send_keys(f"Water bottle #{random.randint(100, 999)}")
    driver.find_element(By.ID, "lfLocation").send_keys(random.choice(("Gym", "LHC", "Y point")))
    driver.find_element(By.ID, "lfDescription").send_keys("Blue cap, minor scratches — selenium test.")
    pause_visible(headless)
    driver.find_element(By.XPATH, "//button[contains(.,'Post Report')]").click()
    wait_flash(driver, wait)
    logger.info("Lost & Found: report posted")


def flow_auction(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard/auction"))
    pause_visible(headless)
    wait.until(EC.presence_of_element_located((By.ID, "auctionName")))
    driver.find_element(By.ID, "auctionName").send_keys(random.choice(("Calculator", "Desk lamp", "USB hub")))
    driver.find_element(By.ID, "auctionPrice").clear()
    driver.find_element(By.ID, "auctionPrice").send_keys(str(round(random.uniform(50.0, 500.0), 2)))
    driver.find_element(By.ID, "auctionDuration").clear()
    driver.find_element(By.ID, "auctionDuration").send_keys(str(random.randint(24, 72)))
    driver.find_element(By.ID, "auctionDescription").send_keys("Selenium test auction.")
    pause_visible(headless)
    driver.find_element(By.XPATH, "//button[contains(.,'Create Auction')]").click()
    wait_flash(driver, wait)
    logger.info("Auction: listing created")


def flow_teams(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard/teams"))
    pause_visible(headless)
    wait.until(EC.presence_of_element_located((By.ID, "name")))
    unique = int(time.time()) % 100000
    driver.find_element(By.ID, "name").send_keys(f"Study group {unique}")
    driver.find_element(By.ID, "courseCode").send_keys("CS682")
    driver.find_element(By.ID, "maxSize").clear()
    driver.find_element(By.ID, "maxSize").send_keys(str(random.randint(4, 12)))
    driver.find_element(By.ID, "description").send_keys("Automated team for demo — selenium.")
    pause_visible(headless)
    driver.find_element(By.XPATH, "//button[contains(.,'Create Team')]").click()
    wait_flash(driver, wait)
    logger.info("Teams: team created")


def flow_ratings(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard/ratings"))
    pause_visible(headless)
    if not driver.find_elements(By.ID, "rateeUsername"):
        logger.info("Ratings: no sellers to rate (complete a buy first). Skipping submit.")
        return
    wait.until(EC.presence_of_element_located((By.ID, "rateeUsername")))
    sel_user = Select(driver.find_element(By.ID, "rateeUsername"))
    values = [o.get_attribute("value") or "" for o in sel_user.options]
    real = [v for v in values if v]
    if not real:
        logger.info("Ratings: dropdown empty. Skipping.")
        return
    sel_user.select_by_value(real[0])
    Select(driver.find_element(By.ID, "score")).select_by_value(str(random.randint(3, 5)))
    driver.find_element(By.ID, "comment").send_keys("Selenium test rating — thanks!")
    pause_visible(headless)
    driver.find_element(By.XPATH, "//button[contains(.,'Submit Rating')]").click()
    wait_flash(driver, wait)
    logger.info("Ratings: submitted for %s", real[0])


def flow_stats(driver: webdriver.Chrome, wait: WebDriverWait, base: str, headless: bool) -> None:
    driver.get(urljoin(base, "dashboard/stats"))
    pause_visible(headless)
    try:
        wait.until(EC.presence_of_element_located((By.XPATH, "//h2[contains(.,'Statistics')]")))
    except TimeoutException:
        logger.warning("Stats: no Statistics heading (non-admin users see an access message on dashboard).")
    logger.info("Stats: visited.")


FLOW_RUNNERS: dict[str, Callable[[webdriver.Chrome, WebDriverWait, str, bool], None]] = {
    "buysell": flow_buysell,
    "pooling": flow_pooling,
    "library": flow_library,
    "lostfound": flow_lostfound,
    "auction": flow_auction,
    "teams": flow_teams,
    "ratings": flow_ratings,
    "stats": flow_stats,
}


def main() -> int:
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=(
            "Defaults: --account N sets username and password to ahilkhanN and (for --register) email to ahilkhanN@iitb.ac.in. "
            "With --register, an existing session is logged out first. "
            "Flows (--flows): "
            + ", ".join(FLOW_ORDER)
            + " (alias: polling→pooling). Example: --register --account 2 --flows all"
        ),
    )
    parser.add_argument(
        "--base-url",
        default="http://51.20.53.218:8501/",
        help="CampusConnect base URL",
    )
    parser.add_argument(
        "--account",
        type=int,
        default=1,
        metavar="N",
        help="Use username/password ahilkhanN and email ahilkhanN@iitb.ac.in when those options are omitted (default: 1).",
    )
    parser.add_argument(
        "--username",
        default=None,
        help="Override username (default: ahilkhanN from --account).",
    )
    parser.add_argument(
        "--password",
        default=None,
        help="Override password (default: same as username, ahilkhanN).",
    )
    parser.add_argument(
        "--email",
        default=None,
        help="With --register: IIT Bombay email. Default: ahilkhanN@iitb.ac.in from --account.",
    )
    auth = parser.add_mutually_exclusive_group()
    auth.add_argument(
        "--login",
        action="store_true",
        help="Sign in with existing account (default if neither --login nor --register is given).",
    )
    auth.add_argument(
        "--register",
        action="store_true",
        help="Create account with --username / --password, then sign in and run flows.",
    )
    parser.add_argument(
        "--flows",
        default="all",
        help=f"Comma-separated flows or 'all'. Valid: {', '.join(FLOW_ORDER)}, all",
    )
    parser.add_argument(
        "--headless",
        action="store_true",
        help="Run Chrome in headless mode",
    )
    args = parser.parse_args()

    base = args.base_url.rstrip("/") + "/"
    flows = parse_flows(args.flows)

    du, dp, _de = credential_defaults(args.account)
    username = args.username if args.username is not None else du
    password = args.password if args.password is not None else dp

    driver = build_driver(headless=args.headless)
    try:
        driver.set_page_load_timeout(60)
        wait = WebDriverWait(driver, 30)

        if args.register:
            logout_if_logged_in(driver, wait, base, args.headless)
            reg_email = default_register_email(args.email, args.account)
            if not reg_email.lower().endswith("@iitb.ac.in"):
                logger.error("Registration requires an @iitb.ac.in email (got %s)", reg_email)
                return 1
            if not register_account(driver, wait, base, username, reg_email, password, args.headless):
                return 1

        if not login(driver, wait, base, username, password, args.headless):
            return 1

        for name in flows:
            logger.info("=== Flow: %s ===", name)
            FLOW_RUNNERS[name](driver, wait, base, args.headless)
            pause_visible(args.headless)

        return 0
    finally:
        pause_visible(args.headless)
        driver.quit()


if __name__ == "__main__":
    sys.exit(main())
