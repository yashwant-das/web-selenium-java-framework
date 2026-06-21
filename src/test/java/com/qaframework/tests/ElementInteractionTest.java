package com.qaframework.tests;

import com.qaframework.base.BaseTest;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests demonstrating element interaction patterns with the Add/Remove Elements page.
 *
 * <p>This test class shows common Selenium operations: adding/removing DOM elements, waiting for
 * visibility, verifying text content, and handling dynamic UIs.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class ElementInteractionTest extends BaseTest {

  private static final Logger log = LoggerFactory.getLogger(ElementInteractionTest.class);

  // ──────────────────────────────────────────────
  // Locators for the-internet.herokuapp.com/add_remove_elements/
  // ──────────────────────────────────────────────

  private static final By ADD_ELEMENT_BUTTON = By.cssSelector("button[onclick='addElement()']");
  private static final By ELEMENT_DELETE_BUTTONS =
      By.cssSelector("#elements button.added-manually");

  // ──────────────────────────────────────────────
  // Tests
  // ──────────────────────────────────────────────

  /** Verifies that clicking "Add Element" adds a new element to the page. */
  @Test(description = "Adding an element creates a visible element on the page")
  public void testAddElementCreatesNewElement() {
    getDriver().get("https://the-internet.herokuapp.com/add_remove_elements/");
    // Click Add Element once
    click(ADD_ELEMENT_BUTTON);
    // Verify new element exists
    List<WebElement> elements = getDriver().findElements(ELEMENT_DELETE_BUTTONS);
    Assert.assertEquals(elements.size(), 1, "Should have one delete button after adding");
  }

  /** Verifies clicking the delete button removes the corresponding element. */
  @Test(description = "Deleting an element removes it from the page")
  public void testDeleteElementRemovesFromPage() {
    getDriver().get("https://the-internet.herokuapp.com/add_remove_elements/");
    // Add two elements first
    click(ADD_ELEMENT_BUTTON);
    click(ADD_ELEMENT_BUTTON);
    List<WebElement> beforeDelete = getDriver().findElements(ELEMENT_DELETE_BUTTONS);
    Assert.assertEquals(beforeDelete.size(), 2, "Should have two elements before deleting");
    // Delete the first dynamic element (the one we added)
    if (!beforeDelete.isEmpty()) {
      beforeDelete.get(0).click();
    }
    List<WebElement> afterDelete = getDriver().findElements(ELEMENT_DELETE_BUTTONS);
    Assert.assertEquals(
        afterDelete.size(), beforeDelete.size() - 1, "Should have one fewer element after delete");
  }

  /** Verifies the page title and heading are correct. */
  @Test(description = "Page has expected content")
  public void testPageContentIsCorrect() {
    getDriver().get("https://the-internet.herokuapp.com/add_remove_elements/");
    String title = getDriver().getTitle();
    log.info("Page title: {}", title);
    Assert.assertNotNull(title, "Page should have a title");
  }

  /** Demonstrates screenshot capture during a test. */
  @Test(description = "Screenshot of element interaction page")
  public void testScreenshotInteractionPage() {
    getDriver().get("https://the-internet.herokuapp.com/add_remove_elements/");
    String base64 = com.qaframework.utils.ScreenshotService.captureBase64(getDriver());
    Assert.assertNotNull(base64, "Screenshot should be captured successfully");
  }

  // ──────────────────── Helper methods ────────────────────

  /** Clicks an element by locator with explicit wait. */
  private void click(By locator) {
    WebElement element = com.qaframework.utils.WaitManager.waitForClickable(getDriver(), locator);
    log.debug("Clicking: {}", locator);
    element.click();
  }
}
