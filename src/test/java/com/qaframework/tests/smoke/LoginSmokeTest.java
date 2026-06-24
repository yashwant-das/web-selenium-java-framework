package com.qaframework.tests.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import com.qaframework.base.BaseTest;
import com.qaframework.data.factory.UserDataFactory;
import com.qaframework.data.model.UserData;
import com.qaframework.pages.DashboardPage;
import com.qaframework.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

/**
 * Smoke test suite validating critical authentication paths.
 *
 * @author QA Framework Team
 * @since 1.0
 */
@Epic("Authentication")
@Feature("Login")
public class LoginSmokeTest extends BaseTest {

  /** Verifies successful login with a valid admin user generated via data factory. */
  @Test(
      groups = {"smoke", "regression"},
      description = "Valid login with dynamic credentials redirects to dashboard")
  @Story("Valid Login")
  @Severity(SeverityLevel.BLOCKER)
  @Description(
      "Verifies that a valid user can login successfully and view the secure dashboard area.")
  @TmsLink("TMS-101")
  @Issue("BUG-404")
  public void testValidLoginRedirectsToDashboard() {
    UserData admin = UserDataFactory.createAdminUser();

    LoginPage loginPage = new LoginPage();
    assertThat(loginPage.isUsernameFieldDisplayed()).isTrue();
    assertThat(loginPage.isPasswordFieldDisplayed()).isTrue();

    DashboardPage dashboard = loginPage.login(admin.username(), admin.password());
    assertThat(dashboard.isHeaderDisplayed()).isTrue();
    assertThat(dashboard.getHeader()).contains("Secure Area");
    assertThat(dashboard.isSuccessMessageDisplayed()).isTrue();

    LoginPage loggedOut = dashboard.logOut();
    assertThat(loggedOut.isUsernameFieldDisplayed()).isTrue();
  }

  /** Verifies that submitting invalid credentials fails gracefully and displays error. */
  @Test(
      groups = {"smoke", "regression"},
      description = "Invalid login displays error message")
  @Story("Invalid Login")
  @Severity(SeverityLevel.CRITICAL)
  @Description(
      "Verifies that entering invalid credentials shows an error message and keeps user on login page.")
  @TmsLink("TMS-102")
  public void testInvalidLoginShowsError() {
    LoginPage loginPage = new LoginPage();
    loginPage.loginExpectingFailure("invalidUser", "invalidPassword");
    assertThat(loginPage.isErrorMessageDisplayed()).isTrue();
    assertThat(loginPage.isErrorMessageContaining("Your username is invalid!")).isTrue();
    assertThat(loginPage.isUsernameFieldDisplayed()).isTrue();
  }
}
