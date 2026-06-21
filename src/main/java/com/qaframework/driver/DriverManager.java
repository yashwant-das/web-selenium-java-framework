package com.qaframework.driver;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds a thread-local reference to the WebDriver instance.
 *
 * <p>All test classes must interact through {@link #getDriver()} rather than instantiating
 * WebDriver directly. Each test class (extending {@code BaseTest}) should:
 *
 * <ol>
 *   <li>Create driver in {@code @BeforeMethod}: {@code DriverManager.set(DriverFactory.create())}
 *   <li>Quit driver in {@code @AfterMethod}: {@code DriverManager.quitDriver()}
 * </ol>
 *
 * <p>This guarantees zero shared mutable state across threads, enabling safe parallel TestNG
 * execution.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class DriverManager {

  private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

  // Thread-local WebDriver — never accessible from other threads
  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  // Prevent instantiation
  private DriverManager() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /**
   * Returns the current thread's WebDriver instance.
   *
   * @throws IllegalStateException if no driver has been set for this thread
   * @return the active {@code WebDriver}
   */
  public static WebDriver getDriver() {
    WebDriver driver = DRIVER.get();
    if (driver == null) {
      throw new IllegalStateException(
          "WebDriver not initialized for thread '"
              + Thread.currentThread().getName()
              + "'. Call DriverManager.set(driver) in @BeforeMethod.");
    }
    return driver;
  }

  /**
   * Sets the WebDriver for the current thread.
   *
   * @param driver the WebDriver instance to associate with this thread
   */
  public static void set(WebDriver driver) {
    log.debug("Setting WebDriver for thread: {}", Thread.currentThread().getName());
    DRIVER.set(driver);
  }

  /**
   * Quits the current thread's WebDriver and removes the reference.
   *
   * <p>Always call this in {@code @AfterMethod(alwaysRun = true)} to prevent driver leaks.
   */
  public static void quitDriver() {
    WebDriver driver = DRIVER.get();
    if (driver != null) {
      try {
        driver.quit();
        log.debug("Quit driver for thread: {}", Thread.currentThread().getName());
      } catch (Exception e) {
        // Log but don't rethrow — we don't want quit failures to mask test results
        log.warn(
            "Failed to quit driver for thread '{}': {}",
            Thread.currentThread().getName(),
            e.getMessage());
      } finally {
        DRIVER.remove();
      }
    }
  }

  /**
   * Returns whether a WebDriver has been set for the current thread.
   *
   * @return {@code true} if a driver is currently bound to this thread
   */
  public static boolean hasDriver() {
    return DRIVER.get() != null;
  }

  /**
   * Removes the current thread's WebDriver reference without quitting.
   *
   * <p>Use sparingly — typically only in tear-down where you want to detach the driver without
   * closing the browser session.
   */
  public static void removeDriver() {
    DRIVER.remove();
  }

  /**
   * Returns the name of the current thread for logging and debugging.
   *
   * @return the current thread's name, or "unknown" if the thread is null
   */
  public static String currentThreadName() {
    return Thread.currentThread().getName();
  }
}
