package com.qaframework.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Test data model representing product information for search and verification.
 *
 * @author QA Framework Team
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductData(String name, String category, double price) {}
