# web-selenium-java-framework

Java test automation framework built with **TestNG** and **Selenium WebDriver**. Features Page Object Model, parallel execution, Allure reports, retry logic, and multienvironment configuration.

## Architecture Overview

```
src/
├── main/java/com/qaframework/
│   ├── api/
│   │   └── BaseApiClient.java        ← RestAssured HTTP client base
│   ├── config/
│   │   └── ConfigManager.java        ← Multi-env properties loader + env-var override
│   ├── driver/
│   │   ├── BrowserType.java          ← chrome / firefox / edge enum
│   │   ├── DriverFactory.java        ← WebDriver instance creator (local + Grid)
│   │   └── DriverManager.java        ← ThreadLocal<WebDriver> holder
│   └── utils/
│       ├── DateUtils.java            ← LocalDate formatting, relative dates, quarters
│       ├── FileUtils.java            ← file read/write/cleanup helpers with Jackson
│       ├── JsonUtils.java            ← OBJECT_MAPPER + JSON <-> Map/array conversions
│       ├── ScreenshotService.java    ← base64/png screenshot capture for Allure attachments
│       └── WaitManager.java          ← WebDriverWait wrappers (visibility, clickability, text, URL)
└── test/java/com/qaframework/
    ├── base/
    │   └── BaseTest.java             ← @BeforeMethod/@AfterMethod lifecycle + screenshot on failure
    ├── listeners/
    │   ├── RetryAnalyzer.java        ← IRetryAnalyzer for flaky test retries
    │   └── ScreenshotListener.java   ← ITestListener capturing screenshots on failure
    ├── pages/
    │   ├── BasePage.java             ← Abstract POM base with waits, screenshots, actions
    │   ├── LoginPage.java            ← /login page object
    │   └── DashboardPage.java        ← post-login dashboard page object
    └── tests/
        ├── LoginTest.java            ← login flow smoke tests
        └── ElementInteractionTest.java ← dynamic DOM element tests
```

### Design Principles

1. **Zero shared mutable state** — `DriverManager` wraps `ThreadLocal<WebDriver>`; every parallel thread gets its own browser.
2. **Explicit waits only** — no implicit waits on the driver directly; all waits flow through `WaitManager`.
3. **Page Object Model** — each page class encapsulates locators and interactions; test code speaks in domain language.
4. **Multienvironment config** — properties files per env, with OS environment variable override for secrets. Lock-free `ConfigManager` prevents thread contention.

## Quick Start

```bash
# Build (requires JDK 21+, Maven 3.9+)
mvn clean compile

# Run smoke tests (critical paths)
mvn test -P smoke

# Run full regression suite
mvn test -P regression

# Generate Allure report
mvn allure:serve
```

## Environment Configuration

Each environment lives in `src/test/resources/config/`:

| File    | Purpose                            | Retries |
|---------|------------------------------------|---------|
| `local` | Developer machine, no headless     | 1       |
| `qa`    | QA staging, headless Chrome        | 1       |
| `sit`   | Integration tests                  | 2       |
| `uat`   | User acceptance                    | 2       |
| `prod`  | Read-only production (no retries!) | 0       |

Override any property via OS environment variables:
```bash
# Overrides BASE_URL from the properties file
BASE_URL=https://qa.example.com mvn test -P smoke
```

## Parallel Execution

TestNG runs tests in parallel with the configured thread count. The suite XML files define the strategy:

- **Smoke**: `parallel="classes"`, 2 threads — fast path validation
- **Regression**: `parallel="methods"`, 4 threads — full coverage

## Retry Logic

Annotate flaky test methods with `RetryAnalyzer`:
```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testFlakyFeature() { ... }
```

Retries up to 3 times by default. Override via system property: `-Dtest.retry.max=5`.

## Reporting

Allure reports are generated in `target/allure-results/`. View interactively with:
```bash
mvn allure:serve
```

Screenshots captured on test failure appear in the Allure "Attachments" section and also as PNG files in `target/screenshots/`.

## Code Quality Plugins

| Plugin     | Phase    | Purpose                                                |
|------------|----------|--------------------------------------------------------|
| Spotless   | validate | Google Java Format enforcement                         |
| Checkstyle | validate | Style guide + suppressions                             |
| PMD        | validate | Static analysis (no Thread.sleep, no static WebDriver) |
| Enforcer   | verify   | JDK 21+, Maven 3.9+ requirement                        |

## Framework Commands

```bash
mvn clean compile                  # Build source + test classes
mvn test -P smoke                  # Run smoke suite
mvn test -P regression             # Run full regression
mvn allure:serve                   # Launch Allure report (port 8080)
mvn spotless:apply                 # Auto-format code
```
