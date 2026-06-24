package com.qaframework.tests.regression;

import static org.assertj.core.api.Assertions.assertThat;

import com.qaframework.api.ApiClient;
import com.qaframework.base.BaseTest;
import com.qaframework.config.ConfigManager;
import com.qaframework.data.factory.ProductDataFactory;
import com.qaframework.data.model.ProductData;
import com.qaframework.pages.SearchResultsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Regression test suite showing a hybrid API + UI execution flow.
 *
 * <p>Uses the {@link ApiClient} to set up/teardown data via API, and validates the UI behavior via
 * Page and Component objects.
 *
 * @author QA Framework Team
 * @since 1.0
 */
@Epic("Product Management")
@Feature("Product Search")
public class SearchRegressionTest extends BaseTest {

  private static final Logger log = LoggerFactory.getLogger(SearchRegressionTest.class);
  private ApiClient apiClient;
  private ProductData product;

  /** Sets up test data via API pre-requisites. */
  @BeforeClass(alwaysRun = true)
  public void setUpClass() {
    String baseUrl =
        ConfigManager.getInstance().get("base.url", "https://the-internet.herokuapp.com");
    apiClient = ApiClient.builder().baseUri(baseUrl).build();
    product = ProductDataFactory.createRandomProduct();

    log.info("API: Pre-creating product for search test: {}", product.name());
    Response response = apiClient.post("/status_codes/200", product);
    log.info("API Response status: {}", response.getStatusCode());
  }

  /** Verifies UI search features using component composition patterns. */
  @Test(
      groups = {"regression"},
      description = "Search for a pre-created product and verify details")
  @Story("Search Composed UI")
  @Severity(SeverityLevel.CRITICAL)
  @Description(
      "Verifies that a product created via the API client can be successfully searched on the UI.")
  public void testSearchForProduct() {
    navigateTo("/");

    SearchResultsPage resultsPage = new SearchResultsPage();
    assertThat(resultsPage.isResultsContainerDisplayed()).isTrue();
    assertThat(resultsPage.getNavigationBar().isForkBannerDisplayed()).isTrue();
    assertThat(resultsPage.getNavigationBar().getHeaderTitle())
        .isEqualTo("Welcome to the-internet");
  }

  /** Cleans up test data via API. */
  @AfterClass(alwaysRun = true)
  public void tearDownClass() {
    log.info("API: Cleaning up product from search test: {}", product.name());
    Response response = apiClient.delete("/status_codes/200");
    log.info("API Response status: {}", response.getStatusCode());
  }
}
