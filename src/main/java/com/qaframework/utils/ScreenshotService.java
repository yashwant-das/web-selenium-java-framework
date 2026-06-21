package com.qaframework.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Captures screenshots from WebDriver instances and saves them to disk.
 *
 * <p>Used by {@code AllureSeleniumListener} to attach screenshot base64 data to test failure
 * reports. It also supports manual step-based screenshots when {@code screenshot.on.step=true} is
 * configured.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class ScreenshotService {

  private static final Logger log = LoggerFactory.getLogger(ScreenshotService.class);
  private static final String SCREENSHOT_DIR = "target/screenshots";
  private static final DateTimeFormatter DATETIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

  /** Prevent instantiation; this is a utility class. */
  private ScreenshotService() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /**
   * Captures the current page as a PNG base64-encoded string.
   *
   * @param driver the WebDriver instance
   * @return the base64-encoded PNG data, or {@code null} if screenshot fails
   */
  public static String captureBase64(WebDriver driver) {
    byte[] bytes = captureBytes(driver);
    return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
  }

  /**
   * Captures the current page as a PNG byte array.
   *
   * @param driver the WebDriver instance
   * @return the PNG byte data, or {@code null} if screenshot fails
   */
  public static byte[] captureBytes(WebDriver driver) {
    try {
      return takeScreenshot(driver);
    } catch (Exception e) {
      log.error("Failed to capture screenshot bytes: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Captures a timestamped screenshot and saves it to disk.
   *
   * @param driver the WebDriver instance
   * @return the path to the saved screenshot file, or {@code null} if capture failed
   */
  public static Path captureAndSave(WebDriver driver) {
    try {
      byte[] bytes = takeScreenshot(driver);
      String timestamp = LocalDateTime.now().format(DATETIME_FORMAT);
      Path dir = Paths.get(SCREENSHOT_DIR);
      java.nio.file.Files.createDirectories(dir);
      Path path = dir.resolve("screenshot_" + timestamp + ".png");
      java.nio.file.Files.write(path, bytes);
      log.info("Screenshot saved to: {}", path);
      return path;
    } catch (Exception e) {
      log.error("Failed to save screenshot: {}", e.getMessage());
      return null;
    }
  }

  private static byte[] takeScreenshot(WebDriver driver) {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
  }
}
