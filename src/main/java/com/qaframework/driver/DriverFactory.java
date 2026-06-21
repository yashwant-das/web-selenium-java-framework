package com.qaframework.driver;

import com.qaframework.config.ConfigManager;
import java.net.URL;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates WebDriver instances based on browser type and environment configuration.
 *
 * <p>Supports:
 *
 * <ul>
 *   <li>Local execution via Selenium Manager (bundled in Selenium 4.6+ — no explicit driver path
 *       needed)
 *   <li>Headless mode for CI environments
 *   <li>Remote WebDriver for Selenium Grid 4 execution
 * </ul>
 *
 * <h2>WebDriver BiDi Readiness</h2>
 *
 * <p>The framework is architected to adopt WebDriver BiDi incrementally. When {@code
 * bidi.enabled=true}, Chrome drivers enable the BiDi protocol via {@code
 * ChromeOptions.enableBiDi()}. This will eventually replace CDP as the primary browser automation
 * protocol — see FRAMEWORK_ARCHITECTURE.md for details.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class DriverFactory {

  private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
  private static final ConfigManager config = ConfigManager.getInstance();

  // Prevent instantiation
  private DriverFactory() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /**
   * Creates a WebDriver instance for the configured browser.
   *
   * <p>Resolution order:
   *
   * <ol>
   *   <li>If {@code remote.url} is non-empty, returns {@code RemoteWebDriver}
   *   <li>Otherwise, uses Selenium Manager to create a local driver
   * </ol>
   *
   * @return the configured WebDriver instance
   */
  public static WebDriver create() {
    String browserName = config.get("browser", "chrome");
    BrowserType browser = BrowserType.fromString(browserName);

    log.info(
        "Creating driver for browser: {}, headless: {}",
        browser,
        config.getBoolean("browser.headless", false));

    String remoteUrl = config.get("remote.url");
    if (remoteUrl != null && !remoteUrl.isEmpty()) {
      log.info("Switching to RemoteWebDriver for URL: {}", remoteUrl);
      return createRemote(browser, remoteUrl);
    }

    return switch (browser) {
      case CHROME -> new ChromeDriver(chromeOptions());
      case FIREFOX -> new FirefoxDriver(firefoxOptions());
      case EDGE -> new EdgeDriver(edgeOptions());
    };
  }

  private static WebDriver createRemote(BrowserType browser, String remoteUrl) {
    Capabilities capabilities =
        switch (browser) {
          case CHROME -> chromeOptions();
          case FIREFOX -> firefoxOptions();
          case EDGE -> edgeOptions();
        };

    try {
      return new RemoteWebDriver(new URL(remoteUrl), capabilities);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to create RemoteWebDriver for URL: " + remoteUrl, e);
    }
  }

  private static ChromeOptions chromeOptions() {
    ChromeOptions options = new ChromeOptions();
    addCommonCapabilities(options);
    options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
    if (config.getBoolean("browser.headless", false)) {
      options.addArguments("--headless=new");
      log.info("Chrome running in headless mode (--headless=new)");
    }
    if (config.getBoolean("bidi.enabled", false)) {
      options.enableBiDi();
      log.info("WebDriver BiDi enabled for Chrome");
    }
    options.addArguments("--window-size=1920,1080");
    return options;
  }

  private static FirefoxOptions firefoxOptions() {
    FirefoxOptions options = new FirefoxOptions();
    addCommonCapabilities(options);
    if (config.getBoolean("browser.headless", false)) {
      options.addArguments("--headless");
      log.info("Firefox running in headless mode (--headless)");
    }
    options.addArguments("--width=1920", "--height=1080");
    return options;
  }

  private static EdgeOptions edgeOptions() {
    EdgeOptions options = new EdgeOptions();
    addCommonCapabilities(options);
    options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
    if (config.getBoolean("browser.headless", false)) {
      options.addArguments("--headless=new");
      log.info("Edge running in headless mode (--headless=new)");
    }
    options.addArguments("--window-size=1920,1080");
    return options;
  }

  /**
   * Adds capabilities common to all browsers for CI environments.
   *
   * <p>These capabilities ensure consistent behavior in test environments.
   *
   * @param options browser capabilities to update
   */
  private static void addCommonCapabilities(MutableCapabilities options) {
    options.setCapability("acceptInsecureCerts", true);
  }
}
