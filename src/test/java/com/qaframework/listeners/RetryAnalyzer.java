package com.qaframework.listeners;

import com.qaframework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer that retries failed tests up to a configurable number of attempts.
 *
 * <p>Used by annotating test methods with {@code @Test(retryAnalyzer = RetryAnalyzer.class)}.
 * Retries are limited to transient failures (flaky network, timing issues). Permanent failures
 * (assertion errors in stable code) should be fixed, not retried.
 *
 * <h2>Configuration</h2>
 *
 * <p>Default maximum retries: 3 (4 total attempts including the original). Override via properties
 * file or system environment variable:
 *
 * <pre>
 *   retry.count=3
 * </pre>
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * @Test(retryAnalyzer = RetryAnalyzer.class)
 * public void testFlakyFeature() {
 *     // Will retry up to the configured count on failure
 * }
 * }</pre>
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class RetryAnalyzer implements IRetryAnalyzer {

  private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
  private int attempt;

  /** Constructs a new RetryAnalyzer with initial attempt count. */
  public RetryAnalyzer() {
    this.attempt = 0;
  }

  private int getMaxRetries() {
    return ConfigManager.getInstance().getInt("retry.count", 3);
  }

  /**
   * Returns {@code true} if the test should be retried.
   *
   * @param result the current test result
   * @return {@code true} if attempt count is below the configured max retries
   */
  @Override
  public boolean retry(ITestResult result) {
    int maxRetries = getMaxRetries();
    if (attempt < maxRetries) {
      attempt++;
      log.warn(
          "Retrying test '{}' — retry attempt {} of {}", result.getName(), attempt, maxRetries);
      return true;
    }
    return false;
  }
}
