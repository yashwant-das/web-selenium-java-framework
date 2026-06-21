package com.qaframework.tests;

import com.qaframework.base.BaseTest;
import com.qaframework.pages.DashboardPage;
import com.qaframework.pages.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for the login functionality on the-internet.herokuapp.com.
 *
 * <p>Validates the core user journey: navigate to login → enter credentials → view dashboard → log
 * out.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class LoginTest extends BaseTest {

  private static final Logger log = LoggerFactory.getLogger(LoginTest.class);

  /** Valid username for the-internet.herokuapp.com. */
  private static final String VALID_USERNAME = "tomsmith";

  /** Valid password for the-internet.herokuapp.com (changed in tests). */
  private static final String VALID_PASSWORD = "SuperSecretPassword!"; // placeholder

  // ──────────────────────────────────────────────
  // Positive tests
  // ──────────────────────────────────────────────

  /** Verifies successful login with valid credentials. */
  @Test(description = "Valid login redirects to dashboard")
  public void testValidLoginRedirectsToDashboard() {
    LoginPage loginPage = new LoginPage();
    Assert.assertTrue(loginPage.isUsernameFieldDisplayed(), "Username field should be visible");
    Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), "Password field should be visible");
    DashboardPage dashboard = loginPage.login("tomsmith", "SuperSecretPassword!");
    Assert.assertTrue(
        dashboard.isHeaderDisplayed(), "Dashboard header should be displayed after login");
  }

  /** Verifies the dashboard shows the welcome message. */
  @Test(description = "Dashboard displays welcome message after successful login")
  public void testDashboardWelcomeMessage() {
    LoginPage loginPage = new LoginPage();
    DashboardPage dashboard = loginPage.login("tomsmith", "SuperSecretPassword!");
    String headerText = dashboard.getHeader();
    Assert.assertTrue(
        headerText.contains("Secure Area"),
        "Dashboard should show a welcome message: " + headerText);
  }

  // ──────────────────────────────────────────────
  // Negative tests
  // ──────────────────────────────────────────────

  /** Verifies login failure with invalid credentials. */
  @Test(description = "Invalid credentials display error message")
  public void testInvalidCredentialsShowError() {
    LoginPage loginPage = new LoginPage();
    loginPage.loginExpectingFailure("invalid", "invalid");
    Assert.assertTrue(
        loginPage.isErrorMessageContaining("Your username is invalid!"),
        "Invalid credentials should show username error");
    Assert.assertTrue(
        loginPage.isUsernameFieldDisplayed(), "Username field should remain visible on failure");
  }

  /** Verifies the logout link works correctly. */
  @Test(description = "Valid logout redirects to login page")
  public void testLogoutReturnsToLoginPage() {
    LoginPage loginPage = new LoginPage();
    DashboardPage dashboard = loginPage.login("tomsmith", "SuperSecretPassword!");
    LoginPage loggedOut = dashboard.logOut();
    Assert.assertTrue(
        loggedOut.isUsernameFieldDisplayed(), "Username field should be visible after logout");
  }

  // ──────────────────────────────────────────────
  // Utility / Debug tests
  // ──────────────────────────────────────────────

  /** Captures a screenshot of the login page for debugging. */
  @Test(description = "Captures login page screenshot")
  public void testScreenshotLogin() {
    new LoginPage();
    String base64 = captureScreenshot();
    Assert.assertNotNull(base64, "Screenshot should be captured");
    log.info("Screenshot captured, length: {}", base64.length());
  }
}
