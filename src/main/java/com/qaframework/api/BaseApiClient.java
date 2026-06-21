package com.qaframework.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base API client providing a configured RestAssured specification.
 *
 * <p>All API test classes extend this to inherit a pre-configured {@code RequestSpecification} with
 * base URI, content type, and optional auth headers.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * public class UserApiTest extends BaseApiClient {
 *      @Test
 *     public void testGetUser() {
 *         Response response = get("/users/1");
 *         Assert.assertEquals(response.getStatusCode(), 200);
 *      }
 *  }
 * }</pre>
 *
 * <p>Base URL is resolved from {@code base.url} in ConfigManager — the same value used for browser
 * tests. This enables cross-layer assertions (API matches HTML rendered content).
 *
 * @author QA Framework Team
 * @since 1.0
 */
public abstract class BaseApiClient {

  private static final Logger log = LoggerFactory.getLogger(BaseApiClient.class);

  /** Returns the configured base URI from environment config. */
  protected String getBaseUrl() {
    return "https://the-internet.herokuapp.com";
  }

  /** Returns a pre-configured RequestSpecification. */
  protected RequestSpecification getSpec() {
    if (spec == null) {
      spec =
          new RequestSpecBuilder()
              .setBaseUri(getBaseUrl())
              .setContentType(io.restassured.http.ContentType.JSON)
              .build();
    }
    return spec;
  }

  /**
   * Sends a GET request with the base specification.
   *
   * @param path the path for the GET request
   * @return the response from the request
   */
  protected io.restassured.response.Response get(String path) {
    log.debug("GET {}", path);
    return RestAssured.given().spec(getSpec()).when().get(path).then().extract().response();
  }

  /**
   * Sends a POST request with JSON body.
   *
   * @param path the path for the POST request
   * @param body the JSON body to send
   * @return the response from the request
   */
  protected io.restassured.response.Response post(String path, Object body) {
    log.debug("POST {} (body={})", path, body);
    return RestAssured.given()
        .spec(getSpec())
        .body(body)
        .when()
        .post(path)
        .then()
        .extract()
        .response();
  }

  /**
   * Sends a DELETE request.
   *
   * @param path the path for the DELETE request
   * @return the response from the request
   */
  protected io.restassured.response.Response delete(String path) {
    log.debug("DELETE {}", path);
    return RestAssured.given().spec(getSpec()).when().delete(path).then().extract().response();
  }

  /**
   * Sends a PUT request with JSON body.
   *
   * @param path the path for the PUT request
   * @param body the JSON body to send
   * @return the response from the request
   */
  protected io.restassured.response.Response put(String path, Object body) {
    log.debug("PUT {} (body={})", path, body);
    return RestAssured.given()
        .spec(getSpec())
        .body(body)
        .when()
        .put(path)
        .then()
        .extract()
        .response();
  }

  private RequestSpecification spec;
}
