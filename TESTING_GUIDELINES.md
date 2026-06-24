# QA Automation Testing Guidelines

This document establishes the test implementation standards for the QA automation suite.

## Test Naming Convention

We follow a descriptive naming pattern:
`should_[expectedBehavior]_when_[condition]`

*Examples:*
- `should_redirectToDashboard_when_loginWithValidCredentials`
- `should_displayErrorMessage_when_loginWithInvalidCredentials`
- `should_showRequiredError_when_usernameFieldIsEmpty`

---

## Suite Groups Taxonomy

Every `@Test` annotation must be assigned to one or more execution groups:

- **`smoke`**: High-priority tests verifying critical paths (e.g. login, checkout). Total run time must remain under 5 minutes.
- **`regression`**: The full functional test suite.
- **`slow`**: Tests that take a long time to run (e.g. file uploads, complex multi-step forms) and are excluded from parallel threads.
- **`critical`**: High-impact core flows.

---

## Assertion Philosophy

We use **AssertJ** for all assertions:

### Hard Assertions
Use standard `assertThat()` when checking prerequisite states that, if they fail, make the rest of the test execution irrelevant.
```java
// If the field is missing, the test must fail-fast
assertThat(loginPage.isUsernameFieldDisplayed()).isTrue();
```

### Soft Assertions
Use `softly()` when performing multiple independent checks (e.g., verifying multiple fields in a table or dashboard layout). All soft assertion failures will accumulate and report at the end of the test.
```java
softly().assertThat(dashboard.getHeader()).contains("Secure Area");
softly().assertThat(dashboard.isSuccessMessageDisplayed()).isTrue();
```
*Note: `softly().assertAll()` is automatically executed by `BaseTest` in the `@AfterMethod` hook. Do not call it manually.*

---

## Wait Strategy Guide

Our `WaitManager` wrapper provides explicit synchronization. Use the correct method based on action intent:

1. **`waitForClickable`**: Use before clicking buttons, links, or text areas.
2. **`waitForVisible`**: Use before retrieving element text, checking visibility, or performing layout validations.
3. **`waitForInvisible`**: Use to wait for loading spinners, overlays, or modal dialogs to disappear.
4. **`waitForTextPresent`**: Use when expecting dynamic AJAX content updates.

Do NOT mix implicit and explicit waits, as this introduces unpredictable execution lags.

---

## Test Data Management Rules

- **No Hardcoded Credentials**: Never write credentials or URLs directly in Java classes. Use `ConfigManager` or retrieve values from secret environment variables.
- **Dynamic Factories**: Use `UserDataFactory` or `ProductDataFactory` to generate random user details (name, email, age) using Java Faker. This prevents data contamination during parallel runs.
- **Environment Isolation**: Always use `TestDataLoader` to fetch static JSON fixtures from the environment-specific directories (`src/test/resources/testdata/{env}/`).

---

## Allure Annotation Usage

Apply these annotations to tests to generate readable, trace-friendly execution reports:

### Class Level
- `@Epic`: Categorizes high-level business domains (e.g. `"Authentication"`).
- `@Feature`: Details specific sub-components (e.g. `"Login"`).

### Method Level
- `@Story`: Documents user stories (e.g. `"Valid Login Flow"`).
- `@Severity`: Assigns execution priority (`SeverityLevel.BLOCKER`, `CRITICAL`, `NORMAL`, `MINOR`, `TRIVIAL`).
- `@Description`: Multi-line explanation of the test criteria.
- `@TmsLink`: Direct link to test case management tickets (e.g. `"TMS-402"`).
- `@Issue`: Link to reported bug trackers (e.g. `"BUG-1202"`).
- `@Parameter`: Document values injected via `@DataProvider`.
