package com.qaframework.tests.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import com.qaframework.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

/**
 * Diagnostic test class to verify that the framework captures screenshots on test failures and
 * correctly attaches them to the Allure report.
 */
@Epic("Diagnostics")
@Feature("Screenshots")
public class ScreenshotVerificationTest extends BaseTest {

  /** Deliberately fails to trigger a screenshot capture and verify its attachment in Allure. */
  @Test(description = "Verification test that deliberately fails to test screenshot attachments")
  @Story("Screenshot Verification")
  @Severity(SeverityLevel.MINOR)
  @Description(
      "Deliberately fails a test assertion after navigating to the login page to verify that "
          + "AllureSeleniumListener correctly captures and attaches browser screenshots on failure.")
  public void testFailureScreenshotAttachment() {
    log.info("Running diagnostic test to verify screenshot generation...");
    navigateTo("/login");

    // Deliberate failure to trigger failure screenshot
    assertThat(false)
        .withFailMessage("Deliberate diagnostic failure to verify Allure screenshot attachment")
        .isTrue();
  }
}
