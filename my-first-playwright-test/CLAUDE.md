# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Playwright for Java training project (Maven, Java 21) testing the public Practice Software Testing "toolshop" site. All code lives under `src/test/java/myfirstplaywrightpackage`; there is no `src/main`. Two test styles coexist: plain JUnit 5 tests and Cucumber scenarios. Both write Allure results.

This is a course exercise. Existing code reflects the course's step-by-step progression and uses mixed styles (fixture base class vs `@UsePlaywright`, Playwright vs AssertJ assertions). Leave it as is. The convention sections below apply to new code only.

## Build

```
mvn test-compile    # compile only, no browser needed
```

- The `pom.xml` targets Java 21 (`maven.compiler.source/target`), but the CI workflow sets up JDK 17. Check this before touching either one.
- Playwright downloads its browsers the first time `Playwright.create()` runs, so the first test run is slow.
- Test output goes to `target/`: `allure-results`, `cucumber-reports/cucumber.html` and `traces/*.zip`.

## Commands

Tests run through **Failsafe**, not Surefire (Surefire has `skipTests=true`), so `mvn test` runs nothing. Use `verify`.

```
mvn verify                                   # all tests (JUnit + Cucumber), then builds the Allure report
mvn verify -Dit.test=AddToCartTest           # one test class
mvn verify -Dit.test=AddToCartTest#methodName  # one test method
mvn verify -Dit.test=CucumberTests           # all Cucumber features
```

Failsafe only picks up `*Test.java` / `*Tests.java`, and attaches the AspectJ weaver as a `-javaagent` (needed by Allure `@Step`). CI (`.github/workflows/playwright-tests.yml`) runs `mvn verify` on every push/PR and publishes the Allure report to GitHub Pages.

## Architecture

- **Two parallel test stacks** share the same page objects: JUnit 5 (`toolshop/**`) and Cucumber (`cucumber/**` plus `src/test/resources/features`). Each has its own browser lifecycle code, so a change to browser setup usually has to be made in more than one place.
- **API setup**: `login/UserAPIClient` uses `page.request()` to register users via the site's REST API and throws `IllegalStateException` if the status is not 201. Tests use it instead of the registration UI.
- **Domain**: `toolshop/domain/User` and `Address` are records serialised straight to the API JSON, so `User` uses snake_case component names (`first_name`). Use `User.randomUser()` for unique data and `withPassword(...)` for variants.
- **Mocking**: `PlaywrightRestAPITest` uses `page.route(...)` with canned bodies from `MockSearchResponses`.
- **Parallel execution** is on for both: JUnit runs test classes concurrently with methods on the same thread, and Cucumber runs 4 threads (`junit-platform.properties`).

## Test Framework Stack

| Concern | Library |
|---|---|
| Browser automation | Playwright for Java 1.61.0 (Chromium, headless) |
| Test runner | JUnit 5.11.3, with the JUnit Platform Suite for Cucumber |
| BDD | Cucumber 7.21.1 (`cucumber-java`, `cucumber-junit-platform-engine`) |
| Assertions | AssertJ for most tests; `PlaywrightAssertions.assertThat` in `PlaywrightRestAPITest` |
| Test data | Datafaker (`User.randomUser()`) |
| Reporting | Allure 2.35.x (`allure-jupiter`, `allure-cucumber7-jvm`, AspectJ weaver agent), plus the Cucumber HTML report |
| Runner plugin | Failsafe 3.6.0 (`integration-test` + `verify`) |

## Test Base Classes and Fixtures

There are two ways to get a `Page` in JUnit tests. Pick one per class.

1. **Inherit `fixtures/PlaywrightTestCase`**. It keeps `Playwright` and `Browser` in `ThreadLocal`s, opens a new `BrowserContext` and `Page` before each test, takes an Allure screenshot and closes the context after each test, and closes the browser in `@AfterAll`. It exposes the protected fields `page` and `browserContext`.
2. **Use `@UsePlaywright(HeadlessChromeOptions.class)`**. Playwright's JUnit extension injects `Page` and `BrowserContext` as method parameters. `HeadlessChromeOptions` sets headless mode, the Chromium args and the `data-test` test-id attribute. Implement `fixtures/TakeFinalScreenshot` to get the end-of-test screenshot. `AddToCartTest` also starts and stops tracing in its own `@BeforeEach`/`@AfterEach`.

Cucumber fixtures are separate (see below). `fixtures/ScreenshotManager.takeScreenshot(page, name)` attaches a full-page screenshot to Allure and is used by all three paths and by some page objects.

## Page Objects

Page objects live in `toolshop/catalog/pageobjects`, `login/LoginPage` and `contact/ContactForm`. They take a `Page` in the constructor, hold no state beyond it, return plain data or records (`CartLineItem`, `ProductSummary`) rather than locators, and leave assertions to the tests. The JUnit and Cucumber stacks share them. Some existing methods combine several interactions (`LoginPage.loginAs(user)`).

Conventions for new page objects:
- The constructor takes a single Playwright `Page` parameter.
- One class per page or major component (e.g. `NavBar`, `SearchComponent`).
- Each method does one interaction and has a descriptive name, such as `enterEmail()`, `clickSubmit()` or `getValidationError()`. Do not use generic names like `doAction()` or `fillForm()`, and do not copy the combined style of `loginAs`.
- Only add methods that the test being written calls. Do not add speculative methods.
- Do not rewrite existing page objects unless asked.

## Test Suite Layers

```
src/test/
├── java/myfirstplaywrightpackage/
│   ├── HeadlessChromeOptions.java        # config: options for @UsePlaywright
│   ├── domain/                           # data: Cucumber table rows (ProductSummary)
│   ├── toolshop/
│   │   ├── fixtures/                     # fixtures: PlaywrightTestCase, ScreenshotManager, TakeFinalScreenshot
│   │   ├── domain/                       # data: User, Address
│   │   ├── MockSearchResponses.java      # data: canned API bodies
│   │   ├── PlaywrightRestAPITest.java    # tests: mocking and API calls
│   │   ├── catalog/
│   │   │   ├── pageobjects/              # page objects: NavBar, SearchComponent, ProductList, ProductDetails, CheckoutCart, CartLineItem
│   │   │   ├── workflows/                # (planned, optional) workflow classes: only for multi-step actions across page objects
│   │   │   └── *Test.java                # tests: SearchForProductsTest, AddToCartTest
│   │   ├── contact/                      # ContactForm (page object) + ContactFormTest
│   │   └── login/                        # LoginPage + UserAPIClient (API) + LoginWithRegisteredUserTest, RegisterUserAPITest
│   └── cucumber/
│       ├── CucumberTests.java            # runner: JUnit Platform Suite
│       └── stepdefinitions/              # glue + fixtures: PlaywrightCucumberFixtures, ScenarioTracingFeatures, ProductCatalogStepDefinitions
└── resources/
    ├── features/catalog/                 # Gherkin features
    ├── allure.properties                 # results go to target/allure-results
    └── junit-platform.properties         # parallel execution settings
```

Layers, from the bottom up: config and fixtures (browser and page lifecycle), data (domain records and mock bodies), page objects and API client (UI and REST actions), then tests or step definitions (assertions). Workflow classes (planned) are an optional layer between page objects and tests, used only for multi-step business actions that span several page objects; tests call page objects directly for simple interactions. Tests may use anything below them. Page objects never depend on workflows or tests.

| Area | Tests | Style | Page objects used |
|---|---|---|---|
| Catalog | `SearchForProductsTest` (`@Nested` groups per behaviour) | UI; `PlaywrightTestCase` | `SearchComponent`, `ProductList` |
| Catalog | `AddToCartTest` | UI; `@UsePlaywright` | `SearchComponent`, `ProductList`, `ProductDetails`, `NavBar`, `CheckoutCart` (returns `CartLineItem`) |
| Contact | `ContactFormTest` (includes a `@ParameterizedTest` for mandatory fields) | UI; `PlaywrightTestCase` | `ContactForm` |
| Login | `LoginWithRegisteredUserTest` | UI after API user setup; `PlaywrightTestCase` | `LoginPage` (plus `UserAPIClient` for setup) |
| Login | `RegisterUserAPITest` | API only; bare `@UsePlaywright`, no `Page` or browser | None |
| Mocked and API | `PlaywrightRestAPITest` (`MockingAPIResponses` and `MakingAPICalls` nested groups) | Route mocking and API calls; `PlaywrightTestCase` | None |
| Catalog (BDD) | `CucumberTests` running `features/catalog/product_catalog.feature` | Cucumber; via `ProductCatalogStepDefinitions` | `NavBar`, `SearchComponent`, `ProductList` |

- Each test class is independent and can run in any order or in parallel with the others. Each test creates its own user or starts from the home page.
- Allure grouping: `@Feature` on the class (Product Catalog, Shopping Cart, Contacts), `@Story` on the nested class or test method.
- Tests are grouped by feature area in packages under `toolshop`, next to that area's page objects. `PlaywrightRestAPITest` sits at the `toolshop` root.

## Cucumber Integration

- `cucumber/CucumberTests` is a JUnit Platform Suite (`@IncludeEngines("cucumber")`) that selects the `features` classpath resource. Its glue package is `myfirstplaywrightpackage.cucumber.stepdefinitions`, and its plugins are Allure, `pretty` and the HTML report.
- `PlaywrightCucumberFixtures` is the Cucumber equivalent of `PlaywrightTestCase`. Its `@Before(order = 100)` creates the context and page, and step classes get the page with the static `getPage()`. Its `@After` takes a screenshot and closes the context.
- `ScenarioTracingFeatures` starts and stops a Playwright trace around every scenario and writes `target/traces/trace-<scenario-name>.zip`.
- Step definition classes build their page objects in a `@Before` hook (`ProductCatalogStepDefinitions.setUpPageObject`). Do this per scenario, because the page changes every scenario.
- Features live in `src/test/resources/features/**`. Tables map to records through `@DataTableType` (`ProductSummary`).

## Key Conventions

- When a test-id locator is needed, use `page.getByTestId(...)`. It targets `data-test` because each fixture sets the test-id attribute (see Site quirks). For new tests, the order in Locator Strategy decides when to reach for it.
- Never share a `Page`, `Browser` or `Playwright` between threads. Create them per thread or per test as the fixtures do.
- Use `@DisplayName` for readable names, and `@Feature`/`@Story` for the Allure hierarchy. Group related cases in `@Nested` classes. Mark page object actions that matter to the report with `@Step`.
- Keep test classes named `*Test` and the Cucumber runner `*Tests`, or Failsafe will skip them.
- Keep new trace output under `target/traces/`, which CI collects.
- Tests hit the live site (`https://practicesoftwaretesting.com` and `https://api.practicesoftwaretesting.com`), so product names and prices in assertions can drift.

## Conventions for New Code

1. **New test classes use the `@UsePlaywright(HeadlessChromeOptions.class)` extension pattern**, with `Page` (and `BrowserContext` where needed) injected as method parameters. Do not use the older `PlaywrightTestCase` base class for new tests; it stays for the existing tests only.
2. **New test classes implement `TakeFinalScreenshot` and `WithTracing`.**
   - `TakeFinalScreenshot` exists in `toolshop/fixtures`.
   - `WithTracing` does **not exist yet**. Tracing is currently written inline in `AddToCartTest` (`@BeforeEach` start, `@AfterEach` stop to `target/traces/trace-<display-name>.zip`). Create `WithTracing` in `toolshop/fixtures` by extracting that code, rather than copying the inline version into new tests.

`AddToCartTest` is the closest existing example of the target pattern.

## Locator Strategy

Locator priority for new tests: `getByRole` → `getByLabel` → `getByText` → `getByTestId` → CSS. Use XPath only as a last resort. `getByTestId()` matches `data-test` attributes.

Existing page objects lean on `getByTestId` (and `LoginPage` also uses `getByPlaceholder`). Apply this order to new code rather than rewriting the existing page objects.

## Synchronisation

- Rely on Playwright's built-in auto-waiting.
- Do not use `Thread.sleep()`.
- If an explicit wait is needed, use Playwright's `waitForSelector()` or assertion timeouts.
- If a test is flaky, fix the root cause rather than adding sleeps.

## Assertions

- Use AssertJ for all assertions.
- Prefer descriptive assertions such as `assertThat(title).contains("expected")` over `assertTrue()` or `assertEquals()`.

## Workflow Classes

- Use workflow classes for multi-step business actions that span multiple page objects.
- Annotate workflow methods with `@Step` (`io.qameta.allure.Step`) for Allure reporting.
- Workflow methods read like business language, such as `registerNewUser()` or `addProductToCart()`.
- Simple single-page interactions stay in the page object.

Workflow classes live in `toolshop/catalog/workflows`, at the same level as `toolshop/catalog/pageobjects`. None exist yet and the directory has not been created. Use them for future tests only and do not refactor existing tests to use them. Steps like searching for a product and adding it to the cart are currently chained directly in the tests (see `AddToCartTest`).

## Site quirks

- The site uses a `data-test` attribute. The fixtures call `selectors().setTestIdAttribute("data-test")`. If `getByTestId` fails, check that this was set on the `Playwright` instance in use, or use a CSS locator.
- The site periodically wipes registered accounts near the top of each hour, so register-then-login tests can fail around :00.
- The address form auto-fills street/city/state from the postcode. Tests must wait for that lookup response before continuing.
