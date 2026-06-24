package com.qaframework.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enterprise API client wrapper for RestAssured specification.
 *
 * <p>Provides a fluent builder pattern to configure headers, base URI, etc., and provides common
 * HTTP method wrappers.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class ApiClient {

  private static final Logger log = LoggerFactory.getLogger(ApiClient.class);
  private final RequestSpecification spec;

  private ApiClient(Builder builder) {
    RequestSpecBuilder specBuilder = new RequestSpecBuilder();
    if (builder.baseUri != null) {
      specBuilder.setBaseUri(builder.baseUri);
    }
    builder.headers.forEach(specBuilder::addHeader);
    specBuilder.setContentType(io.restassured.http.ContentType.JSON);
    this.spec = specBuilder.build();
  }

  /**
   * Starts a builder for ApiClient.
   *
   * @return a new Builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a GET request.
   *
   * @param path target endpoint path
   * @return the HTTP response
   */
  public Response get(String path) {
    log.info("API GET request to: {}", path);
    return RestAssured.given().spec(spec).when().get(path);
  }

  /**
   * Performs a POST request.
   *
   * @param path target endpoint path
   * @param body request body payload
   * @return the HTTP response
   */
  public Response post(String path, Object body) {
    log.info("API POST request to: {}", path);
    return RestAssured.given().spec(spec).body(body).when().post(path);
  }

  /**
   * Performs a PUT request.
   *
   * @param path target endpoint path
   * @param body request body payload
   * @return the HTTP response
   */
  public Response put(String path, Object body) {
    log.info("API PUT request to: {}", path);
    return RestAssured.given().spec(spec).body(body).when().put(path);
  }

  /**
   * Performs a DELETE request.
   *
   * @param path target endpoint path
   * @return the HTTP response
   */
  public Response delete(String path) {
    log.info("API DELETE request to: {}", path);
    return RestAssured.given().spec(spec).when().delete(path);
  }

  /** Builder class for constructing {@link ApiClient} instances. */
  public static class Builder {
    private String baseUri;
    private final Map<String, String> headers = new HashMap<>();

    /**
     * Sets the base URI.
     *
     * @param baseUri the base URL
     * @return the builder instance
     */
    public Builder baseUri(String baseUri) {
      this.baseUri = baseUri;
      return this;
    }

    /**
     * Adds an HTTP header.
     *
     * @param key the header name
     * @param value the header value
     * @return the builder instance
     */
    public Builder header(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    /**
     * Builds a configured ApiClient.
     *
     * @return the built ApiClient instance
     */
    public ApiClient build() {
      return new ApiClient(this);
    }
  }
}
