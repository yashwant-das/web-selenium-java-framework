package com.qaframework.pages;

import com.qaframework.pages.components.NavigationBarComponent;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing search results page, demonstrating component composition by embedding
 * the {@link NavigationBarComponent}.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class SearchResultsPage extends BasePage {

  private static final By RESULTS_CONTAINER = By.cssSelector("div#content");
  private static final By HEADER_ROOT = By.tagName("body");

  private final NavigationBarComponent navigationBar;

  /** Constructs the SearchResultsPage and instantiates its component objects. */
  public SearchResultsPage() {
    log.info("SearchResultsPage constructed");
    WebElement headerElement = getDriver().findElement(HEADER_ROOT);
    this.navigationBar = new NavigationBarComponent(getDriver(), headerElement);
  }

  /**
   * Gets the composed navigation bar component.
   *
   * @return the NavigationBarComponent instance
   */
  public NavigationBarComponent getNavigationBar() {
    return navigationBar;
  }

  /**
   * Verifies if the search results container is displayed.
   *
   * @return {@code true} if results container is visible, {@code false} otherwise
   */
  @Step("Verify results container is displayed")
  public boolean isResultsContainerDisplayed() {
    return isDisplayed(RESULTS_CONTAINER);
  }
}
