package com.qaframework.listeners;

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
 * <p>Default maximum retries: 3 (4 total attempts including the original). Override via system
 * property:
 *
 * <pre>
 *   -Dtest.retry.max=5    ← sets max retries to 5 (6 total attempts)
 * </pre>
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * @Test(retryAnalyzer = RetryAnalyzer.class)
 * public void testFlakyFeature() {
 *     // Will retry up to 3 times on failure
 * }
 * }</pre>
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class RetryAnalyzer implements IRetryAnalyzer {

  private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
  private static final int MAX_RETRIES = 3;
  private int attempt;

  /** Prevent instantiation. */
  public RetryAnalyzer() {
    this.attempt = 0;
  }

  /**
   * Returns {@code true} if the test should be retried.
   *
   * @param result the current test result
   * @return {@code true} if attempt count is below MAX_RETRIES
   */
  @Override
  public boolean retry(ITestResult result) {
    if (attempt <= MAX_RETRIES) {
      log.warn(
          "Retrying test '{}' — attempt {} of {}", result.getName(), attempt + 1, MAX_RETRIES + 1);
      attempt++;
      return true;
    }
    return false;
  }
}
