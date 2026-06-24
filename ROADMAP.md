# QA Automation Framework Roadmap

This document outlines the evolutionary roadmap for the `web-selenium-java-framework`.

---

## Phase 1 (Current): Core Framework & Observability

- Upgrade to Selenium **4.45.0** and Java **21**.
- Reorganize Page Objects using Page Object Model and Component Object Patterns.
- Standardize on Checkstyle, Spotless, and PMD quality gates.
- Implement thread-safe parallel execution capabilities.
- Integrate custom Allure listeners for failure screenshot attachment and environment metadata reporting.

---

## Phase 2 (Q3 2026): API Bridge Integration

- Implement fluent `ApiClient` wrapping RestAssured for hybrid UI+API execution.
- Create helper utilities to set up browser cookies/session state directly via API calls (bypassing slow UI login paths).
- Standardize API assertions using RestAssured schema validation.

---

## Phase 3 (Q4 2026): Visual Regression Testing

- Integrate screenshot comparison capabilities (e.g. using Applitools Eyes, ResembleJS, or a native Java wrapper like AShot).
- Establish visual baseline management practices for cross-browser testing.
- Introduce visual verification hooks inside component lifecycle methods.

---

## Phase 4 (2027): AI-Assisted Selector Healing

- Develop a self-healing locator strategy utilizing AI models.
- When standard locators fail (e.g. `NoSuchElementException`), trigger a fallback mechanism that calculates semantic similarity embeddings on the page source to find the correct element.
- Automatically log proposed locator corrections to speed up maintenance cycles.

---

## Phase 5 (2027): WebDriver BiDi Full Adoption

- Complete transition from CDP (Chrome DevTools Protocol) to standard W3C WebDriver BiDi protocols.
- Introduce advanced capabilities:
  - Dynamic network traffic interception and mock response injection.
  - Client-side console error streaming and assertions.
  - Geo-location and device emulation via standardized protocols.
