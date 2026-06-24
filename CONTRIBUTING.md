# Contributing Guidelines

Welcome! This guide outlines branching standards, commit messages, pull request requirements, and coding conventions for contributing to the `web-selenium-java-framework`.

## Branching Standards

We follow a Git Flow naming convention:
- **`feature/`**: New automated tests or framework capabilities (e.g. `feature/add-api-logging`)
- **`fix/`**: Fixes for flaky tests, test failures, or framework bugs (e.g. `fix/stale-element-login`)
- **`chore/`**: Maintenance tasks, library upgrades, or config updates (e.g. `chore/upgrade-selenium`)

---

## Commit Conventions

We enforce [Conventional Commits](https://www.conventionalcommits.org/):
- **`feat:`** A new test class, page object, or framework feature.
- **`fix:`** A correction to a test locator, retry configuration, or bug.
- **`docs:`** Changes to README, architecture guide, or Javadoc.
- **`refactor:`** Code restructuring without functional behavior changes.
- **`chore:`** Updating dependencies, spotless formatting, or maven configuration.

*Example:* `feat(auth): add smoke test for SSO redirection flow`

---

## Pull Request Checklist

Before submitting a Pull Request, ensure that:
1. All code formatting rules are met locally:
   ```bash
   mvn spotless:apply
   ```
2. Static analysis checks compile and pass without violations:
   ```bash
   mvn clean validate
   ```
3. Regression tests run successfully in headless mode:
   ```bash
   BROWSER_HEADLESS=true mvn test -P regression
   ```
4. Documentation files are updated if new features or patterns are introduced.

---

## How to Add a Page Object

All Page Objects must extend `BasePage` and follow these templates:

```java
package com.qaframework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SamplePage extends BasePage {

  // 1. Locators (use By, never PageFactory annotations)
  private static final By SUBMIT_BUTTON = By.id("submit-btn");

  // 2. Constructors (never navigate inside constructors unless it is the entry page)
  public SamplePage() {
    log.info("SamplePage loaded");
  }

  // 3. Action Methods (Annotate with @Step, return page chain)
  @Step("Click the submit button")
  public ConfirmPage clickSubmit() {
    click(SUBMIT_BUTTON);
    return new ConfirmPage();
  }
}
```

---

## How to Add a Test

All tests must extend `BaseTest` to leverage correct WebDriver lifecycles:

```java
package com.qaframework.tests.regression;

import static org.assertj.core.api.Assertions.assertThat;
import com.qaframework.base.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Sample Category")
@Feature("Submission")
public class SampleTest extends BaseTest {

  @Test(groups = {"regression"}, description = "Verify form submission flow")
  @Story("Form Submission")
  @Severity(SeverityLevel.NORMAL)
  public void shouldSubmitFormSuccessfully() {
    // 1. Arrange
    navigateTo("/sample");
    
    // 2. Act
    SamplePage page = new SamplePage();
    ConfirmPage confirm = page.clickSubmit();
    
    // 3. Assert (Use softly() for multiple validations)
    softly().assertThat(confirm.getMessage()).isEqualTo("Success!");
  }
}
```

---

## Code Review Standards

- **Wait Philosophy**: No hardcoded `Thread.sleep()`. All element lookups and clicks must use explicit waits.
- **Thread Safety**: Never write static fields that hold state. Config managers should be read-only, and drivers should be retrieved thread-locally.
- **Clean Assertions**: Prefer AssertJ fluent assertion patterns over TestNG assertions for test-level checks.
