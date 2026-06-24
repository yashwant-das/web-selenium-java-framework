package com.qaframework.tests.regression;

import com.qaframework.base.BaseTest;
import com.qaframework.data.TestDataProvider;
import com.qaframework.data.model.UserData;
import com.qaframework.pages.DashboardPage;
import com.qaframework.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

/**
 * Regression test suite validating authentication flows using parameterized data inputs.
 *
 * @author QA Framework Team
 * @since 1.0
 */
@Epic("Authentication")
@Feature("Login")
public class LoginRegressionTest extends BaseTest {

  /**
   * Runs parameterized login tests checking valid, invalid, and empty credential flows.
   *
   * @param user the user payload injection
   * @param expectedSuccess whether login is expected to succeed
   * @param expectedError expected error message substring on failure
   */
  @Test(
      groups = {"regression"},
      description = "Data-driven login validation",
      dataProvider = "loginCredentials",
      dataProviderClass = TestDataProvider.class)
  @Story("Data-Driven Authentication")
  @Severity(SeverityLevel.NORMAL)
  @Description(
      "Verifies various combinations of valid, invalid, and empty login credentials using soft assertions.")
  public void testDataDrivenLogin(UserData user, boolean expectedSuccess, String expectedError) {

    LoginPage loginPage = new LoginPage();
    softly().assertThat(loginPage.isUsernameFieldDisplayed()).isTrue();
    softly().assertThat(loginPage.isPasswordFieldDisplayed()).isTrue();

    if (expectedSuccess) {
      DashboardPage dashboard = loginPage.login(user.username(), user.password());
      softly().assertThat(dashboard.isHeaderDisplayed()).isTrue();
      softly().assertThat(dashboard.getHeader()).contains("Secure Area");
      dashboard.logOut();
    } else {
      loginPage.loginExpectingFailure(user.username(), user.password());
      softly().assertThat(loginPage.isErrorMessageDisplayed()).isTrue();
      softly().assertThat(loginPage.isErrorMessageContaining(expectedError)).isTrue();
    }
  }
}
