package com.qaframework.pages;

import com.qaframework.config.ConfigManager;
import com.qaframework.driver.DriverManager;
import com.qaframework.utils.ScreenshotService;
import com.qaframework.utils.WaitManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for all page objects using the Page Object Model pattern.
 *
 * <p>Provides common WebDriver interactions with built-in explicit waits, screenshot capture, and
 * element safety checks. Every concrete page extends this class to share common behavior:
 *
 * <ul>
 *   <li>Implicit driver access via {@code getDriver()}
 *   <li>Wait-based interaction via {@link WaitManager}
 *   <li>Automatic screenshot capture on assertion failures
 *   <li>Safety-checked element lookups via {@code findElementSafe()}
 * </ul>
 *
 * @author QA Framework Team
 * @since 1.0
 */
public abstract class BasePage {

  protected static final Logger log = LoggerFactory.getLogger(BasePage.class);

  /** Prevent instantiation; use subclasses instead. */
  protected BasePage() {
    // no-op
  }

  /** Returns the current thread's WebDriver from DriverManager. */
  protected WebDriver getDriver() {
    return DriverManager.getDriver();
  }

  /**
   * Navigates to a URL using the configured base URL + path.
   *
   * @param path the relative path (e.g., "/login")
   */
  protected void navigateTo(String path) {
    String baseUrl =
        ConfigManager.getInstance().get("base.url", "https://the-internet.herokuapp.com");
    String url = baseUrl + path;
    log.info("Navigating to: {}", url);
    getDriver().get(url);
  }

  /**
   * Clicks an element by locator with explicit wait for clickability.
   *
   * @param locator the By locator of the element to click
   */
  protected void click(By locator) {
    WebElement element = WaitManager.waitForClickable(getDriver(), locator);
    log.debug("Clicking element: {}", locator);
    element.click();
  }

  /**
   * Sends keys to an element, clearing existing text first.
   *
   * @param locator the By locator of the input element
   * @param text the text to send
   */
  protected void sendKeys(By locator, String text) {
    WebElement element = WaitManager.waitForClickable(getDriver(), locator);
    log.debug("Sending keys '{}' to element: {}", text, locator);
    element.clear();
    element.sendKeys(text);
  }

  /**
   * Gets the text content of an element.
   *
   * @param locator the By locator of the element
   * @return the text content of the element
   */
  protected String getText(By locator) {
    log.debug("Getting text for element: {}", locator);
    return WaitManager.waitForTextPresent(getDriver(), locator);
  }

  /**
   * Checks if an element is displayed on the page.
   *
   * @param locator the By locator of the element
   * @return {@code true} if element is visible, {@code false} otherwise
   */
  protected boolean isDisplayed(By locator) {
    try {
      int timeout = ConfigManager.getInstance().getInt("element.check.timeout", 5);
      WebElement element = WaitManager.waitForVisible(getDriver(), locator, timeout);
      log.debug("Element {} is displayed", locator);
      return element.isDisplayed();
    } catch (Exception e) {
      log.debug("Element {} is not displayed: {}", locator, e.getMessage());
      return false;
    }
  }

  /**
   * Checks if an element exists on the page without waiting.
   *
   * @param locator the By locator of the element
   * @return {@code true} if element is enabled, {@code false} otherwise
   */
  protected boolean isEnabled(By locator) {
    try {
      WebElement element = getDriver().findElement(locator);
      return element.isEnabled();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  /** Captures and returns a screenshot base64 string for the current page. */
  protected String captureScreenshot() {
    String base64 = ScreenshotService.captureBase64(getDriver());
    log.info("Screenshot captured, length={}", base64 != null ? base64.length() : 0);
    return base64;
  }

  /**
   * Finds an element safely — returns {@code null} instead of throwing.
   *
   * @param locator the By locator of the element
   * @return the WebElement, or {@code null} if not found
   */
  protected WebElement findElementSafe(By locator) {
    try {
      return getDriver().findElement(locator);
    } catch (NoSuchElementException e) {
      log.debug("Element not found: {}", locator);
      return null;
    }
  }

  /**
   * Finds all matching elements — returns empty list if none found.
   *
   * @param locator the By locator of the elements
   * @return the list of WebElements
   */
  protected java.util.List<WebElement> findElementsSafe(By locator) {
    try {
      return getDriver().findElements(locator);
    } catch (NoSuchElementException e) {
      return java.util.Collections.emptyList();
    }
  }

  /** Gets the current page URL. */
  protected String getCurrentUrl() {
    log.debug("Current URL: {}", getDriver().getCurrentUrl());
    return getDriver().getCurrentUrl();
  }

  /** Refreshes the current page. */
  protected void refreshPage() {
    log.debug("Refreshing page");
    getDriver().navigate().refresh();
  }

  /** Accepts the current JavaScript alert. */
  protected void acceptAlert() {
    log.debug("Accepting alert");
    getDriver().switchTo().alert().accept();
  }

  /** Dismisses the current JavaScript alert. */
  protected void dismissAlert() {
    log.debug("Dismissing alert");
    getDriver().switchTo().alert().dismiss();
  }

  /** Gets the text from the current JavaScript alert. */
  protected String getAlertText() {
    log.debug("Getting alert text");
    return getDriver().switchTo().alert().getText();
  }
}
