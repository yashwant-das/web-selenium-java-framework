package com.qaframework.listeners;

import com.qaframework.config.ConfigManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility listener helper that generates the {@code environment.properties} file in the Allure
 * results directory.
 *
 * <p>This populates environment metadata on the Allure report dashboard.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class EnvironmentWriter {

  private static final Logger log = LoggerFactory.getLogger(EnvironmentWriter.class);

  private EnvironmentWriter() {
    // Private constructor to prevent instantiation
  }

  /**
   * Generates and writes target/allure-results/environment.properties.
   *
   * @param driver the active WebDriver instance to fetch capabilities from (optional)
   */
  public static void writeEnvironmentProperties(WebDriver driver) {
    Path allureResultsPath = Paths.get("target", "allure-results");
    try {
      if (!Files.exists(allureResultsPath)) {
        Files.createDirectories(allureResultsPath);
      }

      Properties props = new Properties();

      if (driver != null && driver instanceof HasCapabilities hasCaps) {
        Capabilities caps = hasCaps.getCapabilities();
        props.setProperty("Browser", caps.getBrowserName());
        props.setProperty("Browser.Version", caps.getBrowserVersion());
      } else {
        props.setProperty("Browser", ConfigManager.getInstance().get("browser", "chrome"));
        props.setProperty("Browser.Version", "Unknown");
      }

      props.setProperty("Environment", System.getProperty("env", "local"));
      props.setProperty("Base.URL", ConfigManager.getInstance().get("base.url", ""));
      props.setProperty("OS", System.getProperty("os.name"));
      props.setProperty("Java.Version", System.getProperty("java.version"));
      props.setProperty("Selenium.Version", "4.45.0");
      props.setProperty("TestNG.Version", "7.10.2");

      Path envFile = allureResultsPath.resolve("environment.properties");
      try (FileOutputStream fos = new FileOutputStream(envFile.toFile())) {
        props.store(fos, "Allure Environment Properties");
        log.info("Successfully wrote allure-results/environment.properties");
      }
    } catch (IOException e) {
      log.error("Failed to write environment.properties", e);
    }
  }
}
