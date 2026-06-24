package com.qaframework.base;

import com.qaframework.assertions.SoftAssertionHelper;
import com.qaframework.config.ConfigManager;
import com.qaframework.driver.DriverFactory;
import com.qaframework.driver.DriverManager;
import com.qaframework.listeners.AllureSeleniumListener;
import com.qaframework.listeners.RetryAnalyzerListener;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Base test class providing WebDriver lifecycle management for all TestNG tests.
 *
 * <p>Every automated test should extend this class to inherit:
 *
 * <ul>
 *   <li>{@code @BeforeMethod}: driver creation, implicit wait setup, config reload, and base.url
 *       navigation
 *   <li>{@code @AfterMethod}: soft assertion verification and driver cleanup
 * </ul>
 *
 * <h2>Parallel Execution</h2>
 *
 * <p>This class is thread-safe. Each test thread gets its own WebDriver instance via {@link
 * DriverManager}, which wraps a {@code ThreadLocal<WebDriver>}.
 *
 * @author QA Framework Team
 * @since 1.0
 */
@Listeners({
  AllureSeleniumListener.class,
  RetryAnalyzerListener.class,
  io.qameta.allure.testng.AllureTestNg.class
})
public abstract class BaseTest {

  protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

  private static final ThreadLocal<SoftAssertionHelper> softAssertion =
      ThreadLocal.withInitial(SoftAssertionHelper::new);

  /** Returns the current thread's WebDriver instance. */
  protected WebDriver getDriver() {
    return DriverManager.getDriver();
  }

  /** Captures the current browser page as a base64 PNG string. */
  protected String captureScreenshot() {
    return com.qaframework.utils.ScreenshotService.captureBase64(getDriver());
  }

  /** Returns a configured WebDriverWait with default timeouts from ConfigManager. */
  protected WebDriverWait wait(WebDriver driver) {
    return new WebDriverWait(
        driver, Duration.ofSeconds(com.qaframework.utils.WaitManager.getDefaultExplicitTimeout()));
  }

  /** Returns the SoftAssertionHelper instance for the current thread. */
  protected SoftAssertionHelper softly() {
    return softAssertion.get();
  }

  /**
   * Navigates to a path relative to the base.url config.
   *
   * @param path the relative path (e.g. "/login")
   */
  protected void navigateTo(String path) {
    String baseUrl =
        ConfigManager.getInstance().get("base.url", "https://the-internet.herokuapp.com");
    String url = baseUrl + path;
    log.info("Navigating to URL: {}", url);
    getDriver().get(url);
  }

  // ──────────────────────────────────────────────
  // Test lifecycle
  // ──────────────────────────────────────────────

  /**
   * Creates a new WebDriver instance before each test method.
   *
   * <p>Resolution order:
   *
   * <ol>
   *   <li>Reload ConfigManager from the active environment (system property "env")
   *   <li>Create driver via {@code DriverFactory.create()}
   *   <li>Set implicit wait timeout on the driver
   *   <li>Initialize a fresh thread-local SoftAssertionHelper
   *   <li>Navigate to base.url
   * </ol>
   */
  @BeforeMethod(alwaysRun = true)
  public void setUp(ITestContext context) {
    // Reload config from active environment (passed as -Denv=qa or similar)
    String env = System.getProperty("env", "local");
    log.info("Setting up test with environment: {}", env);
    ConfigManager.getInstance().load(env);

    // Create WebDriver
    WebDriver driver = DriverFactory.create();
    DriverManager.set(driver);
    com.qaframework.listeners.EnvironmentWriter.setBrowserDetails(driver);

    // Set implicit wait
    int implicitSeconds = com.qaframework.utils.WaitManager.getDefaultImplicitTimeout();
    log.debug("Implicit wait: {}s", implicitSeconds);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitSeconds));

    // Reset soft assertion for this thread
    softAssertion.set(new SoftAssertionHelper());

    // Navigate to base.url
    String baseUrl =
        ConfigManager.getInstance().get("base.url", "https://the-internet.herokuapp.com");
    log.info("Navigating to base URL: {}", baseUrl);
    driver.get(baseUrl);

    log.info("WebDriver created for thread: {}", Thread.currentThread().getName());
  }

  /**
   * Quits the WebDriver after each test method.
   *
   * <p>Invokes soft assertions assertAll and cleans up the thread-local state.
   */
  @AfterMethod(alwaysRun = true)
  public void tearDown(ITestResult result) {
    String methodName = result.getName();
    log.info("Tearing down test: {}", methodName);

    try {
      // Capture screenshot if test failed or soft assertions have errors
      if ((result.getStatus() == ITestResult.FAILURE || !softly().errorsCollected().isEmpty())
          && DriverManager.hasDriver()) {
        log.warn(
            "Test failed or soft assertions failed: {} — capturing screenshot for Allure",
            methodName);
        try {
          WebDriver driver = getDriver();
          byte[] screenshotBytes = com.qaframework.utils.ScreenshotService.captureBytes(driver);
          io.qameta.allure.Allure.addAttachment(
              "Failure Screenshot",
              "image/png",
              new java.io.ByteArrayInputStream(screenshotBytes),
              "png");

          String pageSource = driver.getPageSource();
          io.qameta.allure.Allure.addAttachment(
              "Page Source",
              "text/html",
              new java.io.ByteArrayInputStream(
                  pageSource.getBytes(java.nio.charset.StandardCharsets.UTF_8)),
              "html");

          if (driver instanceof org.openqa.selenium.HasCapabilities hasCaps) {
            org.openqa.selenium.Capabilities caps = hasCaps.getCapabilities();
            String browserInfo =
                String.format(
                    "Browser: %s%nVersion: %s%nPlatform: %s%nCapabilities: %s",
                    caps.getBrowserName(),
                    caps.getBrowserVersion(),
                    caps.getPlatformName(),
                    caps.asMap());
            io.qameta.allure.Allure.addAttachment(
                "Browser Info",
                "text/plain",
                new java.io.ByteArrayInputStream(
                    browserInfo.getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                "txt");
          }
        } catch (Exception e) {
          log.error("Failed to capture and attach failure screenshot to Allure", e);
        }
      }

      softly().assertAll();
    } finally {
      softAssertion.remove();
      // Quit driver
      if (DriverManager.hasDriver()) {
        DriverManager.quitDriver();
        log.info("WebDriver quit for test: {}", methodName);
      }
    }
  }
}
