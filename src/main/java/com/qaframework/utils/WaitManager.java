package com.qaframework.utils;

import com.qaframework.config.ConfigManager;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized wait strategy using explicit {@code WebDriverWait} directly.
 *
 * <p>All wait methods delegate to {@code WebDriverWait} with conditions from {@link
 * ExpectedConditions}. Default timeouts are loaded from {@link ConfigManager}. All public methods
 * accept an explicit timeout override parameter for tests that need longer or shorter waits.
 *
 * <h2>Design Decisions</h2>
 *
 * <ul>
 *   <li><b>No {@code FluentWait} wrapper classes</b>: {@code WebDriverWait} is sufficient and
 *       reduces indirection.
 *   <li><b>No implicit waits on the driver</b>: All waits are explicit, controlled by this class
 *       only.
 *   <li><b>Default timeouts from config</b>: Changed via properties file; no hard-coded values in
 *       source.
 * </ul>
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class WaitManager {

  private static final Logger log = LoggerFactory.getLogger(WaitManager.class);
  private static final ConfigManager config = ConfigManager.getInstance();

  /** Prevent instantiation; this is a utility class. */
  private WaitManager() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  // ──────────────────────────────────────────────
  // Visibility waits
  // ──────────────────────────────────────────────

  /**
   * Waits for an element to be visible on the page.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @return the located WebElement once visible
   */
  public static WebElement waitForVisible(WebDriver driver, By locator) {
    return waitFor(
        driver,
        locator,
        Duration.ofSeconds(getDefaultExplicitTimeout()),
        ExpectedConditions.visibilityOfElementLocated(locator));
  }

  /**
   * Waits for an element to be visible on the page within a custom timeout.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @param seconds maximum wait time in seconds
   * @return the located WebElement once visible
   */
  public static WebElement waitForVisible(WebDriver driver, By locator, int seconds) {
    Duration timeout = Duration.ofSeconds(seconds);
    return waitFor(
        driver, locator, timeout, ExpectedConditions.visibilityOfElementLocated(locator));
  }

  // ──────────────────────────────────────────────
  // Clickability waits
  // ──────────────────────────────────────────────

  /**
   * Waits for an element to be clickable (visible AND enabled).
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @return the located WebElement once clickable
   */
  public static WebElement waitForClickable(WebDriver driver, By locator) {
    return waitFor(
        driver,
        locator,
        Duration.ofSeconds(getDefaultExplicitTimeout()),
        ExpectedConditions.elementToBeClickable(locator));
  }

  /**
   * Waits for an element to be clickable within a custom timeout.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @param seconds maximum wait time in seconds
   * @return the located WebElement once clickable
   */
  public static WebElement waitForClickable(WebDriver driver, By locator, int seconds) {
    Duration timeout = Duration.ofSeconds(seconds);
    return waitFor(driver, locator, timeout, ExpectedConditions.elementToBeClickable(locator));
  }

  // ──────────────────────────────────────────────
  // Invisibility waits
  // ──────────────────────────────────────────────

  /**
   * Waits for an element to become invisible (not present or not displayed).
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @return {@code true} if element is no longer visible, {@code false} if it remains visible
   */
  public static boolean waitForInvisible(WebDriver driver, By locator) {
    return waitForCondition(
        driver,
        Duration.ofSeconds(getDefaultExplicitTimeout()),
        ExpectedConditions.invisibilityOfElementLocated(locator));
  }

  /**
   * Waits for an element to become invisible within a custom timeout.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @param seconds maximum wait time in seconds
   * @return {@code true} if element is no longer visible, {@code false} if it remains visible
   */
  public static boolean waitForInvisible(WebDriver driver, By locator, int seconds) {
    Duration timeout = Duration.ofSeconds(seconds);
    return waitForCondition(
        driver, timeout, ExpectedConditions.invisibilityOfElementLocated(locator));
  }

  // ──────────────────────────────────────────────
  // Text waits
  // ──────────────────────────────────────────────

  /**
   * Waits for an element to contain specific text.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @param text the expected text content
   * @return {@code true} if text is found, {@code false} if timeout
   */
  public static boolean waitForText(WebDriver driver, By locator, String text) {
    return waitForCondition(
        driver,
        Duration.ofSeconds(getDefaultExplicitTimeout()),
        ExpectedConditions.textToBePresentInElementLocated(locator, text));
  }

  /**
   * Waits for an element to contain specific text within a custom timeout.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @param text the expected text content
   * @param seconds maximum wait time in seconds
   * @return {@code true} if text is found, {@code false} if timeout
   */
  public static boolean waitForText(WebDriver driver, By locator, String text, int seconds) {
    Duration timeout = Duration.ofSeconds(seconds);
    return waitForCondition(
        driver, timeout, ExpectedConditions.textToBePresentInElementLocated(locator, text));
  }

  // ──────────────────────────────────────────────
  // URL waits
  // ──────────────────────────────────────────────

  /**
   * Waits for the current URL to contain a specific fragment.
   *
   * @param driver the WebDriver instance
   * @param fragment the URL fragment to wait for
   * @return {@code true} if URL contains the fragment, {@code false} if timeout
   */
  public static boolean waitForUrlContains(WebDriver driver, String fragment) {
    return waitForCondition(
        driver,
        Duration.ofSeconds(getDefaultExplicitTimeout()),
        ExpectedConditions.urlContains(fragment));
  }

  /**
   * Waits for the current URL to contain a specific fragment within a custom timeout.
   *
   * @param driver the WebDriver instance
   * @param fragment the URL fragment to wait for
   * @param seconds maximum wait time in seconds
   * @return {@code true} if URL contains the fragment, {@code false} if timeout
   */
  public static boolean waitForUrlContains(WebDriver driver, String fragment, int seconds) {
    Duration timeout = Duration.ofSeconds(seconds);
    return waitForCondition(driver, timeout, ExpectedConditions.urlContains(fragment));
  }

  // ──────────────────────────────────────────────
  // Attribute waits
  // ──────────────────────────────────────────────

  /**
   * Waits for an element attribute to contain a specific value.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @param attr the attribute name
   * @param value the expected attribute value fragment
   * @return {@code true} if attribute contains the value, {@code false} if timeout
   */
  public static boolean waitForAttributeContains(
      WebDriver driver, By locator, String attr, String value) {
    return waitForCondition(
        driver,
        Duration.ofSeconds(getDefaultExplicitTimeout()),
        ExpectedConditions.attributeContains(locator, attr, value));
  }

  /**
   * Waits for an element attribute to contain a specific value within a custom timeout.
   *
   * @param driver the WebDriver instance
   * @param locator the By locator to wait for
   * @param attr the attribute name
   * @param value the expected attribute value fragment
   * @param seconds maximum wait time in seconds
   * @return {@code true} if attribute contains the value, {@code false} if timeout
   */
  public static boolean waitForAttributeContains(
      WebDriver driver, By locator, String attr, String value, int seconds) {
    Duration timeout = Duration.ofSeconds(seconds);
    return waitForCondition(
        driver, timeout, ExpectedConditions.attributeContains(locator, attr, value));
  }

  // ──────────────────────────────────────────────
  // Internal helpers
  // ──────────────────────────────────────────────

  /** Returns the default explicit wait timeout in seconds from configuration. */
  public static int getDefaultExplicitTimeout() {
    return config.getInt("explicit.wait.seconds", 15);
  }

  /** Returns the default implicit wait timeout in seconds from configuration. */
  public static int getDefaultImplicitTimeout() {
    return config.getInt("implicit.wait.seconds", 10);
  }

  private static <T> WebElement waitFor(
      WebDriver driver, By locator, Duration timeout, ExpectedCondition<T> condition) {
    WebDriverWait wait = new WebDriverWait(driver, timeout);
    try {
      log.debug("Waiting for element: {} (timeout={}s)", locator, timeout.getSeconds());
      return (WebElement) wait.until(condition);
    } catch (org.openqa.selenium.TimeoutException e) {
      log.error("Timeout waiting for element {}: {}", locator, e.getMessage());
      throw e;
    }
  }

  private static boolean waitForCondition(
      WebDriver driver, Duration timeout, ExpectedCondition<Boolean> condition) {
    WebDriverWait wait = new WebDriverWait(driver, timeout);
    try {
      log.debug("Waiting for condition with timeout={}s", timeout.getSeconds());
      return Boolean.TRUE.equals(wait.until(condition));
    } catch (org.openqa.selenium.TimeoutException e) {
      return false;
    }
  }
}
