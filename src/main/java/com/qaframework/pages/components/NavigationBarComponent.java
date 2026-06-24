package com.qaframework.pages.components;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Reusable component representing the top/header navigation area on pages.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class NavigationBarComponent extends BaseComponent {

  private static final By HEADER_TITLE = By.cssSelector("h1.heading");
  private static final By FORK_ME_GITHUB = By.cssSelector("a[href*='github.com'] img");

  /**
   * Constructs the NavigationBarComponent.
   *
   * @param driver the active WebDriver instance
   * @param root the root element of this component
   */
  public NavigationBarComponent(WebDriver driver, WebElement root) {
    super(driver, root);
  }

  /**
   * Gets the main header title text.
   *
   * @return the header title text string
   */
  @Step("Get header title")
  public String getHeaderTitle() {
    return root.findElement(HEADER_TITLE).getText();
  }

  /**
   * Returns whether the "Fork me on GitHub" image banner is visible.
   *
   * @return {@code true} if the image banner is displayed, {@code false} otherwise
   */
  @Step("Verify fork banner is displayed")
  public boolean isForkBannerDisplayed() {
    try {
      return root.findElement(FORK_ME_GITHUB).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
}
