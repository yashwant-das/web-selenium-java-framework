package com.qaframework.listeners;

import com.qaframework.driver.DriverManager;
import com.qaframework.utils.ScreenshotService;
import io.qameta.allure.Attachment;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener that integrates with Allure reporting.
 *
 * <p>It captures screenshots, page source, and browser details on test failure, and generates the
 * allure-results/environment.properties metadata on suite completion.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class AllureSeleniumListener implements ITestListener, ISuiteListener {

  private static final Logger log = LoggerFactory.getLogger(AllureSeleniumListener.class);

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
    log.warn("Test failed: {} — capturing context for Allure", result.getName());
    if (DriverManager.hasDriver()) {
      WebDriver driver = DriverManager.getDriver();
      try {
        // Also save screenshot to local target/screenshots directory
        ScreenshotService.captureAndSave(driver);

        // Attach to Allure Report
        saveScreenshot(driver);
        savePageSource(driver);
        saveBrowserInfo(driver);
      } catch (Exception e) {
        log.error("Failed to attach failure evidence to Allure report", e);
      }
    }
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    log.warn("Test skipped: {}", result.getName());
  }

  /**
   * Captures and returns browser screenshot bytes for Allure attachment.
   *
   * @param driver the active WebDriver instance
   * @return byte array of the screenshot PNG image
   */
  @Attachment(value = "Failure Screenshot", type = "image/png")
  public byte[] saveScreenshot(WebDriver driver) {
    return ScreenshotService.captureBytes(driver);
  }

  /**
   * Captures and returns current page source for Allure attachment.
   *
   * @param driver the active WebDriver instance
   * @return string containing the full HTML page source
   */
  @Attachment(value = "Page Source", type = "text/html")
  public String savePageSource(WebDriver driver) {
    return driver.getPageSource();
  }

  /**
   * Captures and returns browser capability info for Allure attachment.
   *
   * @param driver the active WebDriver instance
   * @return string containing browser capability information
   */
  @Attachment(value = "Browser Info", type = "text/plain")
  public String saveBrowserInfo(WebDriver driver) {
    if (driver instanceof HasCapabilities hasCaps) {
      Capabilities caps = hasCaps.getCapabilities();
      return String.format(
          "Browser: %s%nVersion: %s%nPlatform: %s%nCapabilities: %s",
          caps.getBrowserName(), caps.getBrowserVersion(), caps.getPlatformName(), caps.asMap());
    }
    return "Browser: Unknown (Driver is not HasCapabilities)";
  }

  @Override
  public void onStart(ISuite suite) {
    log.info("Test suite started: {}", suite.getName());
  }

  @Override
  public void onFinish(ISuite suite) {
    log.info("Test suite finished: {}", suite.getName());
    WebDriver driver = null;
    if (DriverManager.hasDriver()) {
      driver = DriverManager.getDriver();
    }
    EnvironmentWriter.writeEnvironmentProperties(driver);
  }

  @Override
  public void onFinish(ITestContext context) {
    log.info("Test context finished: {}", context.getName());
  }
}
