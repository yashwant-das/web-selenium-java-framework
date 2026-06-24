package com.qaframework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for the dashboard/homepage after successful login.
 *
 * <p>Represents the post-login state on the-internet.herokuapp.com's application. Used primarily
 * for assertion checks (e.g., "Welcome" message is displayed) and navigation to sub-pages.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * DashboardPage dashboard = loginPage.login("admin", "admin");
 * assertThat(dashboard.getFlashesText()).isEqualTo("Logged in into the page, with a welcome message.");
 * }</pre>
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class DashboardPage extends BasePage {

  private static final Logger log = LoggerFactory.getLogger(DashboardPage.class);

  // ──────────────────────────────────────────────
  // Locators
  // ──────────────────────────────────────────────

  /** Secure area header text. */
  private static final By DASHBOARD_HEADER = By.cssSelector("div.example h2");

  /** Flash success message on dashboard */
  private static final By FLASH_SUCCESS = By.cssSelector("div.flash.success");

  /** Log Out link */
  private static final By LOGOUT_LINK = By.cssSelector("a.button.secondary.radius");

  // ──────────────────────────────────────────────
  // Constructor
  // ──────────────────────────────────────────────

  /**
   * Constructs the dashboard page. The constructor does not navigate — the page is assumed to be
   * already loaded after successful login.
   */
  public DashboardPage() {
    log.info("DashboardPage constructed");
  }

  // ──────────────────────────────────────────────
  // Assertions / Getters
  // ──────────────────────────────────────────────

  /**
   * Returns the dashboard header text.
   *
   * @return the header text string
   */
  @Step("Get dashboard header text")
  public String getHeader() {
    return getText(DASHBOARD_HEADER);
  }

  /**
   * Returns whether the success flash is displayed.
   *
   * @return {@code true} if displayed, {@code false} otherwise
   */
  @Step("Verify success message is displayed")
  public boolean isSuccessMessageDisplayed() {
    return isDisplayed(FLASH_SUCCESS);
  }

  /**
   * Returns the flash message text if displayed, empty string otherwise.
   *
   * @return the flash text string or empty string
   */
  @Step("Get flash message text")
  public String getFlashText() {
    try {
      return getText(FLASH_SUCCESS);
    } catch (Exception e) {
      log.debug("No success flash found: {}", e.getMessage());
      return "";
    }
  }

  /**
   * Returns whether the dashboard header is displayed.
   *
   * @return {@code true} if displayed, {@code false} otherwise
   */
  @Step("Verify dashboard header is displayed")
  public boolean isHeaderDisplayed() {
    return isDisplayed(DASHBOARD_HEADER);
  }

  // ──────────────────────────────────────────────
  // Navigation
  // ──────────────────────────────────────────────

  /**
   * Clicks "Log out" and navigates to the login page.
   *
   * @return a new LoginPage instance
   */
  @Step("Log out from dashboard")
  public LoginPage logOut() {
    log.info("Logging out from dashboard");
    click(LOGOUT_LINK);
    return new LoginPage();
  }
}
