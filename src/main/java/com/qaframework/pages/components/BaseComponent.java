package com.qaframework.pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Abstract base class for all reusable UI components (Component Object Pattern).
 *
 * <p>A component represents a self-contained section of a page (e.g., a header navigation bar, a
 * dynamic table, or modal alert dialog) that exists on one or more pages.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public abstract class BaseComponent {

  protected final WebDriver driver;
  protected final WebElement root;

  /**
   * Constructs a component.
   *
   * @param driver the active WebDriver instance
   * @param root the root element of this component
   */
  protected BaseComponent(WebDriver driver, WebElement root) {
    this.driver = driver;
    this.root = root;
  }
}
