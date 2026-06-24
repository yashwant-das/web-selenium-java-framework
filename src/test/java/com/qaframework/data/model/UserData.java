package com.qaframework.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Test data model representing user credentials and metadata.
 *
 * @author QA Framework Team
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserData(
    int id, String name, String username, String password, String email, String role) {}
