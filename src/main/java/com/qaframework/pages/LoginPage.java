package com.qaframework.pages;

import com.qaframework.config.ConfigManager;
import com.qaframework.utils.WaitManager;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for the login page at {@code /login}.
 *
 * <p>The-internet.herokuapp.com's login form uses basic authentication with username/password
 * fields and a flash message for success/failure feedback.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * LoginPage loginPage = new LoginPage();
 * loginPage.login("admin", "admin");
 * DashboardPage dashboard = loginPage.getDashboardPage();
 * assertThat(dashboard.isFlashesDisplayed()).isTrue();
 * }</pre>
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class LoginPage extends BasePage {

  private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

  // ──────────────────────────────────────────────
  // Locators
  // ──────────────────────────────────────────────

  /** Username input field */
  private static final By USERNAME_FIELD = By.cssSelector("#username");

  /** Password input field */
  private static final By PASSWORD_FIELD = By.cssSelector("#password");

  /** Login submit button */
  private static final By LOGIN_BUTTON = By.cssSelector("button.radius");

  /** Flash success message */
  private static final By FLASH_SUCCESS = By.cssSelector("div.flash.success");

  /** Flash error message */
  private static final By FLASH_ERROR = By.cssSelector("div.flash.error");

  /** Flash message container (any type) */
  private static final By FLASH_MESSAGE = By.cssSelector("div.flash");

  // ──────────────────────────────────────────────
  // Constructor
  // ──────────────────────────────────────────────

  /** Constructs the login page and navigates to {@code /login}. */
  public LoginPage() {
    String baseUrl =
        ConfigManager.getInstance().get("base.url", "https://the-internet.herokuapp.com");
    String targetUrl = baseUrl + "/login";
    String currentUrl = getDriver().getCurrentUrl();
    if (currentUrl == null
        || !currentUrl.replaceAll("/$", "").equalsIgnoreCase(targetUrl.replaceAll("/$", ""))) {
      navigateTo("/login");
    } else {
      log.info("Already on LoginPage ({}), skipping navigation", currentUrl);
    }
    log.info("LoginPage loaded");
  }

  // ──────────────────────────────────────────────
  // Actions
  // ──────────────────────────────────────────────

  /**
   * Logs in with the given credentials.
   *
   * @param username the login username
   * @param password the login password
   * @return a populated {@code DashboardPage} instance
   */
  public DashboardPage login(String username, String password) {
    log.info("Logging in as: {}", username);
    submitCredentials(username, password);
    WaitManager.waitForUrlContains(getDriver(), "/secure");
    return new DashboardPage();
  }

  /**
   * Submits credentials expected to fail and returns the current login page.
   *
   * @param username the login username
   * @param password the login password
   * @return the current {@code LoginPage}
   */
  public LoginPage loginExpectingFailure(String username, String password) {
    log.info("Submitting invalid login as: {}", username);
    submitCredentials(username, password);
    return this;
  }

  private void submitCredentials(String username, String password) {
    sendKeys(USERNAME_FIELD, username);
    sendKeys(PASSWORD_FIELD, password);
    click(LOGIN_BUTTON);
  }

  // ──────────────────────────────────────────────
  // Assertions / Getters
  // ──────────────────────────────────────────────

  /** Returns whether the username field is displayed. */
  public boolean isUsernameFieldDisplayed() {
    return isDisplayed(USERNAME_FIELD);
  }

  /** Returns whether the password field is displayed. */
  public boolean isPasswordFieldDisplayed() {
    return isDisplayed(PASSWORD_FIELD);
  }

  /** Returns whether the login button is displayed. */
  public boolean isLoginButtonDisplayed() {
    return isDisplayed(LOGIN_BUTTON);
  }

  /** Returns whether the flash success message is displayed. */
  public boolean isSuccessMessageDisplayed() {
    return isDisplayed(FLASH_SUCCESS);
  }

  /** Returns whether the flash error message is displayed. */
  public boolean isErrorMessageDisplayed() {
    return isDisplayed(FLASH_ERROR);
  }

  /** Returns the flash message text (success, error, or empty). */
  public String getFlashMessageText() {
    try {
      return getText(FLASH_MESSAGE);
    } catch (Exception e) {
      log.debug("No flash message found: {}", e.getMessage());
      return "";
    }
  }

  /**
   * Returns whether the error flash message contains the expected text.
   *
   * @param text the expected substring text
   * @return {@code true} if text is present, {@code false} otherwise
   */
  public boolean isErrorMessageContaining(String text) {
    try {
      return getText(FLASH_ERROR).contains(text);
    } catch (Exception e) {
      return false;
    }
  }
}
