package com.SE.final_project.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IitbHighlightsDocument(String source, String fetchedAt, List<IitbHighlightItem> items) {
}
