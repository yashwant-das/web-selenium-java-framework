# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Project Overview

Java test automation framework built with **TestNG 7.10** and **Selenium WebDriver 4.27**. Features Page Object Model, parallel execution, retry logic, multi-environment config, Allure reporting, and REST API testing via RestAssured.

### Key Dependencies

| Layer       | Library            | Version   |
|-------------|-------------------|-----------|
| Browser     | selenium-java      | 4.27.0    |
| Test runner | testng             | 7.10.2    |
| API         | rest-assured       | 5.5.0     |
| JSON        | jackson-databind + jsr310 | 2.18.2 |
| Logging     | logback-classic    | 1.5.12    |
| Assertions  | assertj-core       | 3.26.3    |
| Data gen    | javafaker          | 1.0.2     |
| Quality     | spotless, checkstyle, pmd | — |

## Developing This Project

### Commands

| Task                        | Command                              |
|-----------------------------|-------------------------------------|
| Compile source               | `mvn compile`                         |
| Compile test classes         | `mvn test-compile`                    |
| Run smoke tests              | `mvn test -P smoke`                   |
| Run regression suite         | `mvn test -P regression`              |
| Run a single test            | `mvn test -Dtest=LoginTest#testValidLoginRedirectsToDashboard` |
| Generate Allure report       | `mvn allure:serve`                    |
| Auto-format code             | `mvn spotless:apply`                  |
| Full clean build             | `mvn clean verify`                    |

### Build Profiles

- **smoke** (`mvn test -P smoke`) — critical path tests, 2 parallel threads
- **regression** (`mvn test -P regression`) — full suite, 4 parallel threads

### Requirements

- JDK 21+ (enforced by maven-enforcer-plugin)
- Maven 3.9+

## Architecture

```
com.qaframework/
├── api/
│    └── BaseApiClient.java         ← RestAssured HTTP client base
├── config/
│    └── ConfigManager.java         ← Multi-env properties loader + env-var override
├── driver/
│    ├── BrowserType.java           ← chrome / firefox / edge enum (Java 21 switch)
│    ├── DriverFactory.java         ← WebDriver instance creator (local + Grid)
│    └── DriverManager.java         ← ThreadLocal<WebDriver> holder
├── pages/
│    ├── BasePage.java              ← Abstract POM base: waits, screenshots, actions
│    ├── LoginPage.java             ← /login page object
│    └── DashboardPage.java         ← post-login dashboard page object
├── tests/
│    ├── BaseTest.java              ← @BeforeMethod/@AfterMethod lifecycle
│    ├── RetryAnalyzer.java         ← IRetryAnalyzer for flaky test retries
│    ├── ScreenshotListener.java    ← ITestListener for screenshot on failure
│    ├── LoginTest.java             ← login flow smoke tests
│    └── ElementInteractionTest.java ← dynamic DOM element tests
└── utils/
     ├── DateUtils.java             ← LocalDate formatting, relative dates, quarters
     ├── FileUtils.java             ← file read/write/cleanup + Jackson serialization
     ├── JsonUtils.java             ← OBJECT_MAPPER + JSON <-> Map/array conversions
     ├── ScreenshotService.java     ← base64/png screenshot capture for Allure attachments
     └── WaitManager.java           ← WebDriverWait wrappers (visibility, clickability, text, URL)
```

### Driver Layer (`driver/`)

- `DriverManager` holds a `ThreadLocal<WebDriver>` — never accessible outside the thread.
- `DriverFactory.create()` uses `BrowserType.fromString()` + config to produce Chrome/Firefox/Edge via Selenium Manager (no explicit driver binaries needed in Selenium 4.6+).
- Supports Grid 4: if `remote.url` is set, swaps local driver for `RemoteWebDriver`.
- BiDi readiness: when `bidi.enabled=true`, Chrome drivers call `ChromeOptions.enableBiDi()`.

### Config Layer (`config/`)

Five environments in `src/test/resources/config/`: `{env}.properties`. ConfigManager resolves values with env-var override priority (e.g., `BASE_URL` → file value → hard-coded default). All accessors are `synchronized` for thread safety.

### Page Object Model (`pages/`)

- `BasePage` provides `getDriver()`, click(), sendKeys(), getText(), isDisplayed(), captureScreenshot(), and all JS alert handling.
- Each page class extends `BasePage` and encapsulates locators + domain methods.
- LoginPage returns DashboardPage after login; DashboardPage returns LoginPage on logout — forming a state machine.

### Test Framework (`tests/`)

- `BaseTest` with `@BeforeMethod` (driver creation, implicit wait setup) and `@AfterMethod` (driver quit + screenshot on failure).
- `RetryAnalyzer` implements `IRetryAnalyzer` — retries failed tests up to 3 times.
- `ScreenshotListener` implements both `ITestListener` and `ISuiteListener` — captures screenshots on every failure.

### Utils (`utils/`)

All utilities are static final classes with private constructors (no instantiation). WaitManager is the single point for explicit waits — no WebDriverWait should appear directly in test code or page objects.

## Adding Tests

1. Create a class extending `com.qaframework.tests.BaseTest`.
2. If testing a new page, add a corresponding class in `com.qaframework.pages` extending `BasePage`.
3. Register the test in the appropriate TestNG suite XML under `src/test/resources/suites/`.
4. Run with `mvn test -P smoke` or `mvn test -P regression`.

## File Locations

- Source:   `src/main/java/com/qaframework/`
- Tests:    `src/test/java/com/qaframework/`
- Config:   `src/test/resources/config/{env}.properties`
- Suites:   `src/test/resources/suites/testng-{smoke|regression}.xml`
- Fixtures: `src/test/resources/fixtures/`
- Reports:  `target/allure-results/`, `target/screenshots/`, `target/logs/`

## Quality Gates

All checks run in the `validate` phase via Maven plugins — the build fails before compilation if code style or analysis rules are violated:

- **Spotless**: Google Java Format + remove unused imports
- **Checkstyle**: 120-char max line length, Javadoc on public methods, import ordering
- **PMD**: custom rules enforce no `Thread.sleep()` and no static `WebDriver` fields
- **Enforcer**: requires JDK 21+ and Maven 3.9+
