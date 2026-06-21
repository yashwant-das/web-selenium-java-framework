package com.qaframework.listeners;

import java.nio.file.Path;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that captures screenshots on test failure and logs suite lifecycle events.
 *
 * <p>Implements both {@code ITestListener} (per-test screenshot) and {@code ISuiteListener} (suite
 * start/end logging). Always included via {@code @Listeners(ScreenshotListener.class)} on {@link
 * BaseTest}.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class ScreenshotListener implements ITestListener, ISuiteListener {

  private static final Logger log = LoggerFactory.getLogger(ScreenshotListener.class);

  /** Prevent instantiation. */
  public ScreenshotListener() {
    // no-op — registered via TestNG annotations
  }

  // ──────────────────────────────────────────────
  // ITestListener
  // ──────────────────────────────────────────────

  @Override
  public void onTestStart(ITestResult result) {
    log.info("Test started: {} (class: {})", result.getName(), result.getTestClass().getName());
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    log.info("Test passed: {}", result.getName());
  }

  @Override
  public void onTestFailure(ITestResult result) {
    log.warn("Test failed: {} — capturing screenshot", result.getName());
    try {
      WebDriver driver = com.qaframework.driver.DriverManager.getDriver();
      Path screenshot = com.qaframework.utils.ScreenshotService.captureAndSave(driver);
      log.info("Screenshot saved for failed test {} at {}", result.getName(), screenshot);
    } catch (IllegalStateException e) {
      // Driver may not exist if setUp() itself failed — that's OK
      log.warn("Could not capture screenshot — no driver available: {}", e.getMessage());
    }
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    log.warn(
        "Test skipped: {} (reason: {})",
        result.getName(),
        result.getThrowable() != null ? result.getThrowable().getMessage() : "unknown");
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    log.info("Test within success percentage: {}", result.getName());
  }

  // ──────────────────────────────────────────────
  // ISuiteListener
  // ──────────────────────────────────────────────

  @Override
  public void onStart(ISuite suite) {
    log.info("Test suite started: {}", suite.getName());
  }

  @Override
  public void onFinish(ISuite suite) {
    log.info("Test suite finished: {}", suite.getName());
  }

  // ──────────────────────────────────────────────
  // Unused ITestListener methods (required by interface)
  // ──────────────────────────────────────────────

  @Override
  public void onFinish(ITestContext context) {
    log.info("Test context finished: {}", context.getName());
  }
}
