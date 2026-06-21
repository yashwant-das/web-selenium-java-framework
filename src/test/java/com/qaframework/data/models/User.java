package com.qaframework.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a User in the system.
 *
 * <p>Mapped from src/test/resources/fixtures/users.json.
 *
 * @param id the unique identifier
 * @param name the full name
 * @param username the login username
 * @param role the system role (e.g. admin, user)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(int id, String name, String username, String role) {}
