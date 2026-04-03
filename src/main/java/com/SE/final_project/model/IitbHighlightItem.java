package com.SE.final_project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IitbHighlightItem(String title, String url, String date) {
}
