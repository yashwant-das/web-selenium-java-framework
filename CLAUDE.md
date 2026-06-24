# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java test automation framework built with **TestNG 7.10** and **Selenium WebDriver 4.45.0**. Features Page Object Model, Component Object Pattern, parallel execution, retry logic, soft assertions, multi-environment config, Allure reporting, and REST API testing via RestAssured.

### Key Dependencies

| Layer       | Library            | Version   |
|-------------|-------------------|-----------|
| Browser     | selenium-java      | 4.45.0    |
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
| Run a single test            | `mvn test -Dtest=LoginSmokeTest#testValidLoginRedirectsToDashboard` |
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
src/
├── main/java/com/qaframework/
│    ├── api/
│    │    ├── ApiClient.java             ← REST client wrapper (RestAssured)
│    │    └── BaseApiClient.java         ← RestAssured HTTP client base
│    ├── config/
│    │    └── ConfigManager.java         ← Multi-env properties loader + env-var override
│    ├── driver/
│    │    ├── BrowserType.java           ← chrome / firefox / edge enum (Java 21 switch)
│    │    ├── DriverFactory.java         ← WebDriver instance creator (local + Grid)
│    │    └── DriverManager.java         ← ThreadLocal<WebDriver> holder
│    ├── pages/
│    │    ├── BasePage.java              ← Abstract POM base: waits, screenshots, actions
│    │    ├── LoginPage.java             ← /login page object
│    │    ├── DashboardPage.java         ← post-login dashboard page object
│    │    ├── SearchResultsPage.java     ← search results page object composed with NavComponent
│    │    └── components/
│    │         ├── BaseComponent.java    ← Reusable UI component base
│    │         ├── NavigationBarComponent.java ← Top header navigation bar component
│    │         └── AlertDialogComponent.java   ← JavaScript alert dialog component
│    └── utils/
│         ├── DateUtils.java             ← LocalDate formatting, relative dates, quarters
│         ├── FileUtils.java             ← file read/write/cleanup + Jackson serialization
│         ├── JsonUtils.java             ← OBJECT_MAPPER + JSON <-> Map/array conversions
│         ├── ScreenshotService.java     ← base64/png screenshot capture for Allure attachments
│         └── WaitManager.java           ← WebDriverWait wrappers (visibility, clickability, text, URL)
└── test/java/com/qaframework/
     ├── assertions/
     │    └── SoftAssertionHelper.java   ← Soft assertions helper wrapping AssertJ
     ├── base/
     │    └── BaseTest.java              ← @BeforeMethod/@AfterMethod lifecycle + soft assertions
     ├── data/
     │    ├── TestDataLoader.java        ← JSON files test data loader
     │    ├── TestDataProvider.java      ← TestNG Data Providers (inline & JSON-based)
     │    ├── factory/
     │    │    ├── UserDataFactory.java  ← Java Faker test user generator
     │    │    └── ProductDataFactory.java ← Java Faker test product generator
     │    └── model/
     │         ├── UserData.java         ← User Java record model
     │         └── ProductData.java      ← Product Java record model
     ├── listeners/
     │    ├── AllureSeleniumListener.java ← Custom listener for screenshots & environment info
     │    ├── RetryAnalyzerListener.java  ← Global Annotation Transformer for retry logic
     │    └── EnvironmentWriter.java     ← Helper writing allure environment metadata
     ├── retry/
     │    └── RetryAnalyzer.java         ← IRetryAnalyzer for flaky test retries
     └── tests/
          ├── smoke/
          │    └── LoginSmokeTest.java   ← login flow smoke tests
          └── regression/
               ├── LoginRegressionTest.java ← parameterized regression tests
               └── SearchRegressionTest.java ← API + UI hybrid tests
```

### Driver Layer (`driver/`)

- `DriverManager` holds a `ThreadLocal<WebDriver>` — never accessible outside the thread.
- `DriverFactory.create()` uses `BrowserType` enum switch expression + config options via Selenium Manager.
- Supports Grid 4: if `remote.url` is set, swaps local driver for `RemoteWebDriver`.
- BiDi readiness: when `bidi.enabled=true`, Chrome options enable BiDi.

### Config Layer (`config/`)

Properties located in `src/test/resources/config/{env}.properties`. ConfigManager resolves values with env-var override priority. ConcurrentHashMap caching is utilized for high performance and safety.

### Page Object Model (`pages/`)

- `BasePage` provides `getDriver()`, click(), sendKeys(), getText(), isDisplayed(), and JS alerts.
- Composed component pattern utilizes concrete widgets extending `BaseComponent`.
- LoginPage, DashboardPage, SearchResultsPage extend `BasePage` and encapsulate locators. All page interactions are `@Step`-annotated.

### Test Framework

- `BaseTest` with `@BeforeMethod(alwaysRun = true)` (driver creation, setup, and navigation to `base.url`) and `@AfterMethod(alwaysRun = true)` (soft assertions evaluation + driver quit).
- `RetryAnalyzerListener` implements `IAnnotationTransformer` to wire the `RetryAnalyzer` to all tests automatically.
- `AllureSeleniumListener` captures screenshot bytes, page source, and browser info on failure.

### Quality Gates

- **Spotless**: Google Java Format + remove unused imports
- **Checkstyle**: 120-char max line length, Javadoc on public methods in `main/`, import ordering
- **PMD**: custom rules enforce no `Thread.sleep()` and no static `WebDriver` fields
- **Enforcer**: requires JDK 21+ and Maven 3.9+
