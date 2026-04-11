package com.SE.final_project.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
// import tools.jackson.databind.ObjectMapper;
import com.SE.final_project.model.IitbHighlightItem;
import com.SE.final_project.model.IitbHighlightsDocument;

@Service
public class IitbHighlightsService {

    private static final Logger log = LoggerFactory.getLogger(IitbHighlightsService.class);
    private static final String RESOURCE_PATH = "static/data/iitb-highlights.json";

    private final ObjectMapper objectMapper;

    public IitbHighlightsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public IitbHighlightsDocument loadDocument() {
        ClassPathResource resource = new ClassPathResource(RESOURCE_PATH);
        if (!resource.exists()) {
            return new IitbHighlightsDocument(null, null, Collections.emptyList());
        }
        try {
            return objectMapper.readValue(resource.getInputStream(), IitbHighlightsDocument.class);
        } catch (IOException e) {
            log.warn("Could not read {}: {}", RESOURCE_PATH, e.getMessage());
            return new IitbHighlightsDocument(null, null, Collections.emptyList());
        }
    }

    public List<IitbHighlightItem> getItems() {
        IitbHighlightsDocument doc = loadDocument();
        if (doc.items() == null) {
            return Collections.emptyList();
        }
        return doc.items();
    }
}
