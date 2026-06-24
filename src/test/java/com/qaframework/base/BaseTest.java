package com.qaframework.base;

import com.qaframework.config.ConfigManager;
import com.qaframework.driver.DriverFactory;
import com.qaframework.driver.DriverManager;
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
 *   <li>{@code @BeforeMethod}: driver creation, implicit wait setup, config reload
 *   <li>{@code @AfterMethod}: driver cleanup with screenshot capture on failure
 * </ul>
 *
 * <h2>Parallel Execution</h2>
 *
 * <p>This class is thread-safe. Each test thread gets its own WebDriver instance via {@link
 * DriverManager}, which wraps a {@code ThreadLocal<WebDriver>}.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * public class LoginTest extends BaseTest {
 *     @Test
 *     public void testValidLogin() {
 *         LoginPage loginPage = new LoginPage();
 *         DashboardPage dashboard = loginPage.login("admin", "admin");
 *         Assert.assertTrue(dashboard.isHeaderDisplayed(), "Dashboard should be visible");
 *     }
 * }
 * }</pre>
 *
 * @author QA Framework Team
 * @since 1.0
 */
@Listeners(com.qaframework.listeners.ScreenshotListener.class)
public abstract class BaseTest {

  protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

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
   * </ol>
   */
  @BeforeMethod
  public void setUp(ITestContext context) {
    // Reload config from active environment (passed as -Denv=qa or similar)
    String env = System.getProperty("env", "local");
    log.info("Setting up test with environment: {}", env);
    ConfigManager.getInstance().load(env);

    // Create WebDriver
    WebDriver driver = DriverFactory.create();
    DriverManager.set(driver);

    // Set implicit wait
    int implicitSeconds = com.qaframework.utils.WaitManager.getDefaultImplicitTimeout();
    log.debug("Implicit wait: {}s", implicitSeconds);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitSeconds));

    log.info("WebDriver created for thread: {}", Thread.currentThread().getName());
  }

  /**
   * Quits the WebDriver after each test method.
   *
   * <p>If the test failed, takes a screenshot before closing the browser.
   */
  @AfterMethod
  public void tearDown(ITestResult result) {
    String methodName = result.getName();
    log.info("Tearing down test: {}", methodName);

    // Quit driver
    if (DriverManager.hasDriver()) {
      DriverManager.quitDriver();
      log.info("WebDriver quit for test: {}", methodName);
    }
  }
}
