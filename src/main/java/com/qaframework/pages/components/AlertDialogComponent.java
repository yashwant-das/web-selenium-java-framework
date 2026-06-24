package com.qaframework.pages.components;

import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Reusable component for handling standard JavaScript alert/confirm/prompt dialogs.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class AlertDialogComponent extends BaseComponent {

  private final WebDriverWait wait;

  /**
   * Constructs the AlertDialogComponent.
   *
   * @param driver the active WebDriver instance
   * @param root the root element of this component (can be null for JS alerts)
   */
  public AlertDialogComponent(WebDriver driver, WebElement root) {
    super(driver, root);
    this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));
  }

  private Alert waitForAlert() {
    return wait.until(ExpectedConditions.alertIsPresent());
  }

  /** Accepts the active alert dialog. */
  @Step("Accept alert dialog")
  public void accept() {
    waitForAlert().accept();
  }

  /** Dismisses the active alert dialog. */
  @Step("Dismiss alert dialog")
  public void dismiss() {
    waitForAlert().dismiss();
  }

  /**
   * Gets the text content of the alert dialog.
   *
   * @return the alert text string
   */
  @Step("Get text from alert dialog")
  public String getText() {
    return waitForAlert().getText();
  }

  /**
   * Sends text input to the prompt alert dialog.
   *
   * @param text the input string to send to the prompt
   */
  @Step("Send text to alert dialog: {text}")
  public void sendKeys(String text) {
    waitForAlert().sendKeys(text);
  }
}
