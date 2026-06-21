package com.qaframework.driver;

import java.util.Locale;

/**
 * Supported browser types for automated testing.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public enum BrowserType {

  /** Google Chrome */
  CHROME("chrome"),

  /** Mozilla Firefox */
  FIREFOX("firefox"),

  /** Microsoft Edge (Chromium-based) */
  EDGE("edge");

  private final String browserName;

  BrowserType(String browserName) {
    this.browserName = browserName;
  }

  /**
   * Returns the underlying browser name string used by WebDriver.
   *
   * @return the browser name (e.g. {@code "chrome"})
   */
  public String getBrowserName() {
    return browserName;
  }

  /**
   * Returns whether this browser supports headless mode.
   *
   * <p>All browsers in this enum support headless execution.
   *
   * @return {@code true} for all known browsers
   */
  public boolean supportsHeadless() {
    return true;
  }

  /**
   * Parses a string into a {@code BrowserType}. Case-insensitive.
   *
   * @param name the browser name string
   * @return the matching {@code BrowserType}
   * @throws IllegalArgumentException if no matching enum constant exists
   */
  public static BrowserType fromString(String name) {
    return switch (name != null ? name.trim().toLowerCase(Locale.ROOT) : "chrome") {
      case "chrome" -> CHROME;
      case "firefox" -> FIREFOX;
      case "edge" -> EDGE;
      default ->
          throw new IllegalArgumentException(
              "Unsupported browser: '" + name + "'. Use chrome, firefox, or edge.");
    };
  }
}
