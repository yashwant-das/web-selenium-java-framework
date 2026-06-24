# Claude Code Prompt: web-selenium-java-framework

## Role: Principal QA Architect

## Target: 2026-ready, reference-grade, enterprise Selenium 4 + TestNG framework

---

You are a **Principal QA Architect** with 15+ years of experience designing enterprise test automation platforms.
Your task is to create **`web-selenium-java-framework`** — a production-grade, reference-quality Selenium 4 + TestNG framework in Java 21 that large QA teams can fork, extend, and ship to production immediately.

This is not a tutorial skeleton. Every class, configuration, and decision must reflect **real-world enterprise engineering standards circa 2026**.

---

## Project Identity

- **Repo name:** `web-selenium-java-framework`
- **Group ID:** `com.qaframework`
- **Artifact ID:** `web-selenium-java-framework`
- **Java version:** 21 (use records, sealed classes, text blocks, pattern matching where appropriate)
- **Build tool:** Maven
- **Description:** Java test automation framework built with TestNG and Selenium WebDriver. Features Page Object Model, Component Object Pattern, WebDriver BiDi support, parallel execution, Allure reports, retry logic, and multi-environment configuration.

---

## Technology Stack (Exact Versions)

Use these versions. Do not guess — use what is specified:

```xml
<!-- Core -->
<selenium.version>4.45.0</selenium.version>
<testng.version>7.10.2</testng.version>

<!-- Reporting -->
<allure.version>2.29.1</allure.version>
<aspectj.version>1.9.22.1</aspectj.version>

<!-- Assertions -->
<assertj.version>3.26.3</assertj.version>

<!-- Logging -->
<slf4j.version>2.0.16</slf4j.version>
<logback.version>1.5.12</logback.version>

<!-- Data -->
<jackson.version>2.18.2</jackson.version>
<javafaker.version>1.0.2</javafaker.version>
<rest-assured.version>5.5.0</rest-assured.version>

<!-- Utilities -->
<commons-io.version>2.17.0</commons-io.version>
<commons-lang3.version>3.17.0</commons-lang3.version>

<!-- Code quality -->
<spotless.version>2.43.0</spotless.version>
<checkstyle.version>10.20.1</checkstyle.version>
<pmd.version>7.6.0</pmd.version>
<maven.enforcer.version>3.5.0</maven.enforcer.version>

<!-- Maven plugins -->
<maven.compiler.version>3.13.0</maven.compiler.version>
<maven.surefire.version>3.5.2</maven.surefire.version>
<allure.maven.version>2.13.0</allure.maven.version>
```

**Selenium Manager is the default driver manager** (bundled in Selenium 4.6+). Do NOT add WebDriverManager as a dependency. Only add it as a comment placeholder: `<!-- WebDriverManager: add io.github.bonigarcia:webdrivermanager if Selenium Manager is insufficient -->`.

---

## Architecture Requirements

### 1. Layered Architecture

Strict separation of concerns across these layers (bottom to top):

```text
┌─────────────────────────────────────────────┐
│           Test Layer                        │  ← @Test, @Epic/@Feature/@Story, TestNG XML
│  BaseTest · Smoke/Regression tests          │
├─────────────────────────────────────────────┤
│           Page Object Layer                 │  ← POM + Component Object Pattern
│  BasePage · Pages · Components · Fluent API │
├─────────────────────────────────────────────┤
│           Driver Layer                      │  ← ThreadLocal, DriverFactory, BiDi-ready
│  DriverFactory · DriverManager              │
├─────────────────────────────────────────────┤
│      Config          │    Test Data         │  ← Profiles, env vars, JSON fixtures
│  ConfigManager       │  DataFactory+Faker   │
├─────────────────────────────────────────────┤
│           Utility Layer                     │  ← Cross-cutting concerns
│  WaitManager · ScreenshotService            │
│  RetryAnalyzer · SoftAssertionHelper        │
│  ApiClient · JsonUtils · DateUtils          │
├─────────────────────────────────────────────┤
│           Reporting + Observability         │  ← Allure, SLF4J+Logback, listeners
│  AllureListener · ScreenshotAttachment      │
│  EnvironmentWriter · BrowserInfoAttachment  │
└─────────────────────────────────────────────┘
```

### 2. Driver Management

- **Thread-safe** `ThreadLocal<WebDriver>` — zero shared mutable state.
- `DriverFactory` creates drivers by type. `DriverManager` holds the `ThreadLocal` reference.
- Supported browsers: **Chrome, Firefox, Edge** (local and headless).
- Remote execution via **Selenium Grid 4** using `RemoteWebDriver` + `URL` from config.
- Headless mode controlled by `browser.headless=true/false` in config.
- **WebDriver BiDi readiness:** `ChromeOptions.enableBiDi()` should be an opt-in toggle (`bidi.enabled=false` by default). Document why in comments: BiDi is the future of Selenium (replacing CDP) and this framework is architected to adopt it incrementally.
- `DriverFactory` must use a `switch` expression (Java 21) over browser type, not if-else chains.
- All `Options` objects should set `--no-sandbox`, `--disable-dev-shm-usage`, `--window-size=1920,1080` for CI stability.

### 3. Configuration

- `ConfigManager` loads from `src/test/resources/config/{env}.properties`.
- Environments: `local`, `qa`, `sit`, `uat`, `prod`.
- Active environment selected via Maven property: `-Denv=qa` (default: `local`).
- **Secrets override:** `ConfigManager.get(key)` checks `System.getenv(key.toUpperCase().replace('.','_'))` first, then falls back to the properties file. Document this pattern explicitly so credentials never go in config files.
- Properties to include in every env file:
  - `base.url`
  - `browser` (chrome/firefox/edge)
  - `browser.headless`
  - `implicit.wait.seconds`
  - `explicit.wait.seconds`
  - `page.load.timeout.seconds`
  - `remote.url` (empty for local)
  - `retry.count` (default 1)
  - `bidi.enabled` (default false)
  - `screenshot.on.failure` (default true)
  - `screenshot.on.step` (default false)

### 4. Page Object Model + Component Object Pattern

**BasePage:**

- Constructor: `BasePage(WebDriver driver)` only. Inject `WebDriverWait` from `WaitManager`.
- `PageFactory.initElements(driver, this)` is **explicitly forbidden** — it hides lazy loading issues. Use `By` locators or `WebElement` direct instantiation with custom waits.
- Provide: `waitForElement(By)`, `click(By)`, `type(By, String)`, `getText(By)`, `isDisplayed(By)`, `scrollToElement(By)`.
- All page interactions must be `@Step`-annotated for Allure visibility.
- Return `void` from action methods that don't chain. Return `this` or the next page type for Fluent API chains.

**Component Objects:**

- Create an abstract `BaseComponent(WebDriver driver, WebElement root)` for reusable UI components.
- Implement at least 2 concrete components:
  - `NavigationBarComponent` — header navigation actions
  - `AlertDialogComponent` — reusable alert/modal handling

**Page Objects (sample):**

- `LoginPage` — with Fluent API: `loginPage.enterUsername("u").enterPassword("p").clickLogin()`
- `DashboardPage` — after login landing page
- `SearchResultsPage` — demonstrates component composition

**Anti-patterns to actively avoid and document in FRAMEWORK_ARCHITECTURE.md:**

- `Thread.sleep()` anywhere
- `driver.findElement()` without explicit waits
- Static `WebDriver` references
- PageFactory with `@FindBy` annotations
- Catching `NoSuchElementException` and returning `false`
- Business logic in Page Objects

### 5. Reporting

**Allure integration (allure-testng + BOM):**

- Use `allure-bom` for version management.
- `AllureSeleniumListener` implements `ITestListener`:
  - `onTestFailure`: attach screenshot, attach browser info, attach page source.
  - `onTestStart`: log test name + environment.
  - `onFinish`: write `allure-results/environment.properties`.
- `EnvironmentWriter` writes environment.properties with: `Browser`, `Browser.Version`, `Environment`, `BaseURL`, `OS`, `Java.Version`, `Selenium.Version`, `TestNG.Version`.
- Every page method annotated with `@Step("{method name}")`  using Allure's method reference syntax.
- Tests annotated with `@Epic`, `@Feature`, `@Story`, `@Severity`, `@Description`, `@TmsLink`, `@Issue`.
- `ScreenshotService.captureBase64()` returns `byte[]` for attachment.
- `@Attachment(value = "Screenshot", type = "image/png")`.

**AspectJ configuration** (required for `@Step` and `@Attachment` to work):

- Include `aspectjweaver` as `-javaagent` in Surefire `argLine`.
- Use the `allure.maven` plugin for report generation.

### 6. Parallel Execution

- TestNG parallel execution via `testng-regression.xml`: `parallel="methods" thread-count="4"`.
- `testng-smoke.xml`: `parallel="tests" thread-count="2"`.
- `DriverManager` uses `ThreadLocal<WebDriver>` — verify that `getDriver()` and `quitDriver()` are always called symmetrically.
- No `static` mutable fields anywhere in the framework. This is a hard rule.
- `ConfigManager` is effectively immutable after initialization (load once, read many).

### 7. Retry Logic

- `RetryAnalyzer` implements `IRetryAnalyzer`.
- Max retry count from `ConfigManager.getInt("retry.count", 1)`.
- `RetryAnalyzerListener` implements `IAnnotationTransformer` — applies `RetryAnalyzer` to all `@Test` methods automatically (no per-test annotation needed).
- Log each retry attempt: `WARN Retrying test '{}', attempt {}/{}`.
- Retried tests reported in Allure with `@Flaky` label.

### 8. Soft Assertions

- `SoftAssertionHelper` wraps AssertJ's `SoftAssertions`.
- Usage pattern in BaseTest: `softly().assertThat(x).isEqualTo(y)` — assertions accumulate.
- `softly().assertAll()` called automatically in `@AfterMethod` via BaseTest.
- Hard assertions (`assertThat()` from AssertJ) still available for critical checks that should fail fast.

### 9. Wait Strategy

`WaitManager`:

- `waitForVisible(WebDriver driver, By locator, int seconds)` → `WebElement`
- `waitForClickable(WebDriver driver, By locator, int seconds)` → `WebElement`
- `waitForInvisible(WebDriver driver, By locator, int seconds)` → `boolean`
- `waitForText(WebDriver driver, By locator, String text, int seconds)` → `boolean`
- `waitForUrlContains(WebDriver driver, String fragment, int seconds)` → `boolean`
- `waitForAttributeContains(WebDriver driver, By locator, String attr, String value, int seconds)` → `boolean`
- Default timeouts from `ConfigManager`. All methods also accept explicit timeout overrides.
- **No `FluentWait` wrapper classes** — use `WebDriverWait` directly.

### 10. API Client (Optional Module)

`ApiClient` (in `src/main/java/com/qaframework/api/`):

- Wraps RestAssured `RequestSpecification`.
- `ApiClient.builder().baseUri(url).header(k,v).build()`.
- Methods: `get(path)`, `post(path, body)`, `put(path, body)`, `delete(path)`, `extractAs(Class<T>)`.
- Used in test setup/teardown only (not in Page Objects).
- `@BeforeClass` example: create a test user via API before UI login tests.
- `@AfterClass` example: delete the test user via API after the test class completes.

### 11. Test Data

- JSON files under `src/test/resources/testdata/{env}/`.
- `TestDataLoader` reads JSON via Jackson `ObjectMapper`. Generic: `T load(String file, Class<T> clazz)`.
- POJOs as Java records: `record UserData(String username, String password, String email) {}`.
- `UserDataFactory`, `ProductDataFactory` generate dynamic test data using Java Faker.
- `DataProvider` methods in `TestDataProvider` class for parameterized tests.

### 12. TestNG Organization + Groups

**TestNG XML suite files:**

`testng-regression.xml`:

```xml
<suite name="Regression Suite" parallel="methods" thread-count="4">
  <listeners>
    <listener class-name="com.qaframework.listeners.AllureSeleniumListener"/>
    <listener class-name="com.qaframework.listeners.RetryAnalyzerListener"/>
  </listeners>
  <test name="Regression Tests">
    <groups>
      <run><include name="regression"/></run>
    </groups>
    <packages>
      <package name="com.qaframework.tests"/>
    </packages>
  </test>
</suite>
```

`testng-smoke.xml`:

```xml
<suite name="Smoke Suite" parallel="tests" thread-count="2">
  <!-- same listeners -->
  <test name="Smoke Tests">
    <groups><run><include name="smoke"/></run></groups>
    ...
  </test>
</suite>
```

**Groups taxonomy:**

- `@Test(groups = {"smoke"})` — critical path, < 5 min total
- `@Test(groups = {"regression"})` — full suite
- `@Test(groups = {"smoke", "regression"})` — both (login, homepage)
- `@Test(groups = {"slow"})` — excluded from parallel runs
- `@Test(groups = {"critical"})` — zero-tolerance, highest severity

**Maven profiles:**

```xml
<profile>
  <id>smoke</id>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <configuration>
          <suiteXmlFiles>
            <suiteXmlFile>src/test/resources/testng-smoke.xml</suiteXmlFile>
          </suiteXmlFiles>
        </configuration>
      </plugin>
    </plugins>
  </build>
</profile>
<profile>
  <id>regression</id>
  <!-- analogous for testng-regression.xml -->
</profile>
```

Run commands:

- `mvn test -Psmoke -Denv=qa`
- `mvn test -Pregression -Denv=sit -Dbrowser=firefox`

### 13. Code Quality

All four tools must be wired into the `validate` or `verify` phase and must **break the build** on violation:

**Spotless (Google Java Format):**

```xml
<plugin>
  <groupId>com.diffplug.spotless</groupId>
  <artifactId>spotless-maven-plugin</artifactId>
  <version>${spotless.version}</version>
  <configuration>
    <java>
      <googleJavaFormat>
        <version>1.22.0</version>
        <style>GOOGLE</style>
      </googleJavaFormat>
      <removeUnusedImports/>
      <trimTrailingWhitespace/>
      <endWithNewline/>
    </java>
  </configuration>
  <executions>
    <execution>
      <goals><goal>check</goal></goals>
      <phase>validate</phase>
    </execution>
  </executions>
</plugin>
```

**Checkstyle** (Google checks):

- Max line length: 120
- Javadoc required on public methods in `main/` only
- Import order enforced

**PMD:**

- Rulesets: `java-bestpractices`, `java-design`, `java-errorprone`
- Exclude generated sources
- Fail on `PRIORITY <= 2`

**Maven Enforcer:**

```xml
<rules>
  <requireJavaVersion><version>[21,)</version></requireJavaVersion>
  <requireMavenVersion><version>[3.9,)</version></requireMavenVersion>
  <banDuplicatePomDependencyVersions/>
  <dependencyConvergence/>
</rules>
```

### 14. Logging

`logback.xml` in `src/test/resources/`:

```xml
<configuration>
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>target/logs/test-execution.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>target/logs/test-execution.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>7</maxHistory>
    </rollingPolicy>
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
  </root>
  <logger name="com.qaframework" level="DEBUG"/>
  <logger name="org.openqa.selenium" level="WARN"/>
  <logger name="io.restassured" level="WARN"/>
</configuration>
```

Logging rules:

- `log.debug()` for driver lifecycle events
- `log.info()` for test milestones and navigation
- `log.warn()` for retry attempts and slow operations
- `log.error()` for failures with full stack traces
- Never log passwords or tokens — `ConfigManager.get("password")` should mask output

### 15. CI/CD — GitHub Actions

**`.github/workflows/test.yml`** — multi-job matrix with Allure publish:

```yaml
name: Selenium Test Suite

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]
  schedule:
    - cron: '0 2 * * *'   # nightly regression
  workflow_dispatch:
    inputs:
      env:
        description: 'Target environment'
        required: true
        default: 'qa'
        type: choice
        options: [local, qa, sit, uat]
      browser:
        description: 'Browser'
        required: true
        default: 'chrome'
        type: choice
        options: [chrome, firefox, edge]
      suite:
        description: 'Test suite'
        required: true
        default: 'smoke'
        type: choice
        options: [smoke, regression]

jobs:
  test:
    name: ${{ matrix.browser }} / ${{ matrix.suite }}
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false
      matrix:
        browser: [chrome, firefox]
        suite: [smoke]
        include:
          - browser: chrome
            suite: regression

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Cache Maven dependencies
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

      - name: Install Chrome
        if: matrix.browser == 'chrome'
        uses: browser-actions/setup-chrome@v1

      - name: Install Firefox
        if: matrix.browser == 'firefox'
        uses: browser-actions/setup-firefox@v1

      - name: Run tests
        env:
          BASE_URL: ${{ secrets.QA_BASE_URL }}
          TEST_USERNAME: ${{ secrets.TEST_USERNAME }}
          TEST_PASSWORD: ${{ secrets.TEST_PASSWORD }}
        run: |
          mvn test \
            -P${{ matrix.suite }} \
            -Denv=qa \
            -Dbrowser=${{ matrix.browser }} \
            -Dbrowser.headless=true \
            -Dmaven.test.failure.ignore=true

      - name: Generate Allure report
        if: always()
        run: mvn allure:report -q

      - name: Upload Allure results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: allure-results-${{ matrix.browser }}-${{ matrix.suite }}
          path: target/allure-results/
          retention-days: 30

      - name: Upload Allure report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: allure-report-${{ matrix.browser }}-${{ matrix.suite }}
          path: target/site/allure-maven-plugin/
          retention-days: 30

      - name: Publish Allure report to GitHub Pages
        if: github.ref == 'refs/heads/main' && matrix.browser == 'chrome' && matrix.suite == 'regression'
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: target/site/allure-maven-plugin
          destination_dir: allure-report

  code-quality:
    name: Code Quality
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      - name: Spotless check
        run: mvn spotless:check -q
      - name: Checkstyle
        run: mvn checkstyle:check -q
      - name: PMD
        run: mvn pmd:check -q
      - name: Enforcer
        run: mvn enforcer:enforce -q
```

---

## Complete Folder Structure

Generate this exact structure with all files populated (not empty stubs):

```text
web-selenium-java-framework/
├── .github/
│   └── workflows/
│       └── test.yml
├── .gitignore
├── checkstyle.xml
├── pmd-ruleset.xml
├── pom.xml
├── allure.properties                  # → src/test/resources/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/qaframework/
│   │           ├── api/
│   │           │   └── ApiClient.java
│   │           ├── config/
│   │           │   └── ConfigManager.java
│   │           ├── driver/
│   │           │   ├── BrowserType.java          # enum: CHROME, FIREFOX, EDGE
│   │           │   ├── DriverFactory.java
│   │           │   └── DriverManager.java
│   │           ├── pages/
│   │           │   ├── BasePage.java
│   │           │   ├── LoginPage.java
│   │           │   ├── DashboardPage.java
│   │           │   └── components/
│   │           │       ├── BaseComponent.java
│   │           │       ├── NavigationBarComponent.java
│   │           │       └── AlertDialogComponent.java
│   │           └── utils/
│   │               ├── DateUtils.java
│   │               ├── FileUtils.java
│   │               ├── JsonUtils.java
│   │               ├── ScreenshotService.java
│   │               └── WaitManager.java
│   │
│   └── test/
│       ├── java/
│       │   └── com/qaframework/
│       │       ├── assertions/
│       │       │   └── SoftAssertionHelper.java
│       │       ├── data/
│       │       │   ├── TestDataLoader.java
│       │       │   ├── TestDataProvider.java
│       │       │   ├── factory/
│       │       │   │   ├── UserDataFactory.java
│       │       │   │   └── ProductDataFactory.java
│       │       │   └── model/
│       │       │       ├── UserData.java          # Java record
│       │       │       └── ProductData.java       # Java record
│       │       ├── listeners/
│       │       │   ├── AllureSeleniumListener.java
│       │       │   ├── RetryAnalyzerListener.java  # IAnnotationTransformer
│       │       │   └── EnvironmentWriter.java
│       │       ├── retry/
│       │       │   └── RetryAnalyzer.java
│       │       ├── utils/
│       │       │   └── DateUtilsTest.java
│       │       └── tests/
│       │           ├── BaseTest.java
│       │           ├── smoke/
│       │           │   └── LoginSmokeTest.java
│       │           └── regression/
│       │               ├── LoginRegressionTest.java
│       │               └── SearchRegressionTest.java
│       │
│       └── resources/
│           ├── allure.properties
│           ├── logback.xml
│           ├── config/
│           │   ├── local.properties
│           │   ├── qa.properties
│           │   ├── sit.properties
│           │   ├── uat.properties
│           │   └── prod.properties
│           ├── testdata/
│           │   ├── local/
│           │   │   └── users.json
│           │   └── qa/
│           │       └── users.json
│           └── suites/
│               ├── testng-smoke.xml
│               └── testng-regression.xml
│
├── docs/
│   └── diagrams/                      # Mermaid source files
│       ├── architecture.mmd
│       ├── driver-lifecycle.mmd
│       └── test-execution-flow.mmd
│
├── README.md
├── FRAMEWORK_ARCHITECTURE.md
├── CONTRIBUTING.md
├── TESTING_GUIDELINES.md
└── ROADMAP.md
```

---

## Key Class Implementations

### ConfigManager.java

```java
// Must be thread-safe singleton. Load once at class initialization.
// Pattern: System.getenv() → properties file → default value
// Method signatures:
//   String get(String key)
//   String get(String key, String defaultValue)
//   int getInt(String key, int defaultValue)
//   boolean getBoolean(String key, boolean defaultValue)
//   long getLong(String key, long defaultValue)
```

### DriverFactory.java

```java
// Use Java 21 switch expression over BrowserType enum.
// Support: local (direct), headless, remote (RemoteWebDriver).
// BiDi toggle: if ConfigManager.getBoolean("bidi.enabled"), call options.enableBiDi().
// All Options must include stability flags for CI.
// Use Selenium Manager (no explicit driver path setting).
```

### BaseTest.java

```java
@Listeners({AllureSeleniumListener.class, RetryAnalyzerListener.class})
public abstract class BaseTest {
    // @BeforeMethod: init driver, set implicit wait, navigate to base.url
    // @AfterMethod: softly().assertAll(), quit driver
    // protected WebDriver getDriver()
    // protected SoftAssertionHelper softly()
    // protected void navigateTo(String path) — relative path from base.url
    // Allure.addAttachment for page context on failure
}
```

### BasePage.java

```java
// MINIMAL: driver, logger, explicit wait (from WaitManager).
// No PageFactory. No @FindBy.
// All interactions through By locators with explicit waits.
// @Step on every public action method.
// Protected helper: waitAndClick(By), waitAndType(By, String), waitAndGetText(By).
// scrollIntoView(By) using JavascriptExecutor.
```

### RetryAnalyzer.java

```java
// IRetryAnalyzer implementation.
// Counter is per-instance (thread-safe by TestNG design).
// Max retries from ConfigManager.getInt("retry.count", 1).
// Log: WARN level with test name and attempt number.
```

### AllureSeleniumListener.java

```java
// ITestListener implementation.
// onTestFailure:
//   - Allure.addAttachment("Screenshot", "image/png", screenshot bytes)
//   - Allure.addAttachment("Page Source", "text/html", driver.getPageSource())
//   - Allure.addAttachment("Browser Info", "text/plain", browserInfo string)
// onFinish: write allure-results/environment.properties
// environmentWriter writes:
//   Browser=Chrome 131
//   Environment=qa
//   Base.URL=https://...
//   OS=Linux
//   Java.Version=21.x.x
//   Selenium.Version=4.45.0
```

---

## Sample Tests — What to Demonstrate

### LoginSmokeTest.java

- Groups: `{"smoke", "regression"}`
- `@Epic("Authentication")`, `@Feature("Login")`, `@Story("Valid Login")`
- `@Severity(SeverityLevel.BLOCKER)`
- One test: valid login with dynamic user from `UserDataFactory`
- One test: invalid login with inline data
- Demonstrate: Fluent page API, Allure steps, AssertJ hard assertion

### LoginRegressionTest.java

- Groups: `{"regression"}`
- `@DataProvider` from `TestDataProvider` with multiple credential sets
- Parameterized test: 3 rows (valid, invalid, empty)
- Demonstrate: Soft assertions accumulating failures, Allure `@Parameter`

### SearchRegressionTest.java

- Groups: `{"regression"}`
- Pre-condition via `ApiClient`: create product via API in `@BeforeClass`
- Search for the product via UI
- Verify result using `SearchResultsPage` + `NavigationBarComponent`
- Clean up via `ApiClient` in `@AfterClass`
- Demonstrate: UI+API hybrid test, component composition

---

## Documentation Requirements

### README.md

Include:

- Framework overview badge row (Java 21, Selenium 4, TestNG, Allure, CI status)
- Prerequisites section
- Quick start (clone → run smoke in 3 commands)
- Project structure tree (abbreviated)
- Running tests table: command, profile, browser, env
- Configuration reference table
- CI/CD section with GitHub Actions badge
- Reporting section with sample screenshot/command
- Contributing link
- License

### FRAMEWORK_ARCHITECTURE.md

Include:

- Mermaid architecture diagram (layered)
- Mermaid sequence diagram: test execution lifecycle
- Mermaid flowchart: driver lifecycle (create → use → quit)
- Design decisions section:
  - Why no PageFactory (lazy element issues)
  - Why ThreadLocal (parallel safety)
  - Why Java records for test data models
  - Why explicit waits everywhere (reliability)
  - Why BiDi-ready (future-proofing: CDP is being deprecated)
  - Why Selenium Manager over WebDriverManager (bundled, zero config)
- Anti-patterns catalogue: 8 patterns with explanation of why each is harmful
- Extension guide: how to add a new browser, new environment, new page

### CONTRIBUTING.md

Include:

- Branch strategy (feature/fix/chore prefix)
- Commit convention (Conventional Commits)
- PR checklist (Spotless ✓, Checkstyle ✓, tests pass ✓, docs updated ✓)
- How to add a page object (step-by-step template)
- How to add a test (annotate, group, data provider)
- Code review standards

### TESTING_GUIDELINES.md

Include:

- Test naming convention: `should_[expectedBehavior]_when_[condition]`
- Groups usage guide
- Assertion philosophy: hard vs soft
- Wait strategy guide: when to use which wait method
- Test data rules: no hardcoded credentials, use factories
- Allure annotation usage guide (which annotations on which element)
- What makes a good smoke test vs regression test

### ROADMAP.md

```text
Phase 1 (Current): Core framework — Selenium 4 + TestNG + Allure + CI
Phase 2 (Q3 2026): API bridge — REST Assured ApiClient + UI+API hybrid tests
Phase 3 (Q4 2026): Visual regression — Playwright screenshot comparison or Applitools Eyes hook
Phase 4 (2027): AI-assisted selector healing — self-healing locator strategy via embedding similarity
Phase 5 (2027): WebDriver BiDi full adoption — network interception, console event streaming, full migration from CDP
```

---

## Quality Gates

The build **must pass all of these** before being considered complete:

1. `mvn spotless:check` — zero formatting violations
2. `mvn checkstyle:check` — zero style violations
3. `mvn pmd:check` — zero priority 1-2 violations
4. `mvn enforcer:enforce` — Java 21+, Maven 3.9+, no duplicate deps
5. `mvn test -Psmoke -Dbrowser=chrome -Dbrowser.headless=true` — smoke suite passes
6. `mvn allure:report` — report generates without error
7. No `Thread.sleep()` in any source file (enforced by PMD custom rule or Checkstyle regex)
8. No `static` `WebDriver` references (enforced by PMD)

---

## 2026 Readiness Checklist

Ensure the framework reflects these 2026 standards:

- [ ] Java 21 features used: records, switch expressions, text blocks, `var` where appropriate
- [ ] Selenium Manager as default (no external driver manager library)
- [ ] WebDriver BiDi opt-in toggle with architecture notes on CDP deprecation path
- [ ] GitHub Actions with `actions/checkout@v4`, `actions/setup-java@v4`, `actions/cache@v4`, `actions/upload-artifact@v4`
- [ ] Allure BOM for version management (not individual dependency version pins)
- [ ] AspectJ wired correctly via `argLine` in Surefire (required for `@Step` to appear in reports)
- [ ] Secrets via environment variables — zero credentials in committed files
- [ ] `--no-sandbox` and `--disable-dev-shm-usage` in Chrome options (CI stability)
- [ ] `.gitignore` excludes: `target/`, `allure-results/`, `*.log`, `.allure/`, `node/`, `.env`
- [ ] All config properties have sensible defaults so the framework runs out of the box on `local`
- [ ] `environment.properties` written to `allure-results/` every run
- [ ] `retry.count=1` in all envs except `prod` where it should be `0`
- [ ] SLF4J + Logback (not Log4j) — Log4j2 has had critical CVEs; Logback is the safe default

---

## Final Checklist Before Delivery

- [ ] Every Java class has a package declaration and logger (`private static final Logger log = LoggerFactory.getLogger(X.class)`)
- [ ] Every public method in `main/` has a Javadoc comment
- [ ] All config property files populated with realistic values (not all empty)
- [ ] `testdata/local/users.json` and `testdata/qa/users.json` populated with sample data
- [ ] Mermaid diagrams in `docs/diagrams/` are valid and render in GitHub
- [ ] `pom.xml` has no `<version>` tags for Allure artifacts (use BOM + `allure-bom`)
- [ ] README has runnable commands — test them mentally before writing
- [ ] No TODOs or placeholder comments in delivered code — every class is production-ready
- [ ] `DriverManager.quitDriver()` called in a `finally` block or TestNG `@AfterMethod(alwaysRun = true)`
- [ ] `SoftAssertionHelper.assertAll()` called in `@AfterMethod(alwaysRun = true)` in BaseTest

---

## Start

Begin by generating the complete `pom.xml` first, then the folder structure, then each class in dependency order (bottom-up: utils → config → driver → pages → listeners → tests → docs).

Do not generate stub classes. Every class must be fully implemented with real logic, real locators (using `https://the-internet.herokuapp.com` as the sample application), real configuration values, and real Allure annotations.

The sample application for all page objects and tests is: **`https://the-internet.herokuapp.com`**

- `LoginPage` targets: `/login`
- `DashboardPage` targets: after login
- `SearchResultsPage` can target: `/dynamic_loading` or `/tables` as a search analogue

This framework must be immediately forkable by a QA team and runnable without any modifications beyond cloning and setting environment variables.
