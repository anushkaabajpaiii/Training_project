# GitHub Analytics Framework and run mvn test
# GitHub Repository Analytics Automation Framework

[![Java](https://img.shields.io/badge/Java-17-orange.svg)]()
[![Selenium](https://img.shields.io/badge/Selenium-4.21.0-green.svg)]()
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red.svg)]()
[![Allure](https://img.shields.io/badge/Allure-2.27.0-blue.svg)]()
[![Maven](https://img.shields.io/badge/Build-Maven-yellow.svg)]()

An enterprise-grade Selenium + TestNG automation framework that validates and extracts
repository analytics from **[github.com/topics](https://github.com/topics)** — a real, public,
no-login website. The framework demonstrates Page Object Model, Factory + Singleton design
patterns, ThreadLocal-based parallel execution, Allure reporting, Log4j2 logging, CSV/Excel
export, retry handling, and full CI/CD readiness (Jenkins + GitHub Actions).

---

## 1. Project Overview

| Item | Detail |
|---|---|
| **Application Under Test** | https://github.com/topics (public, no authentication) |
| **Language** | Java 17 |
| **Automation Tool** | Selenium 4 (WebDriver) |
| **Test Runner** | TestNG 7.10 |
| **Design Pattern** | Page Object Model + Factory + Singleton |
| **Reporting** | Allure Reports |
| **Logging** | Log4j2 |
| **Data Export** | CSV (OpenCSV) + Excel (Apache POI) |
| **Browsers Supported** | Chrome, Firefox, Edge |
| **Execution Mode** | Sequential and Parallel (ThreadLocal WebDriver) |
| **CI/CD** | Jenkins (Jenkinsfile) + GitHub Actions (automation.yml) |

---

## 2. Architecture Diagram (ASCII)

```
                          ┌────────────────────────────────────────┐
                          │              testng.xml                │
                          │   (Suite / Parallel / Thread-Count)     │
                          └───────────────────┬──────────────────────┘
                                              │
                  ┌───────────────────────────┼───────────────────────────┐
                  │                           │                           │
        ┌─────────▼─────────┐       ┌─────────▼─────────┐       ┌─────────▼─────────┐
        │ GitHubSearchTest   │       │RepositoryAnalytics │       │ParallelAnalyticsTest│
        │  (Scenario 1 & 2)  │       │Test (Scenario 3-6) │       │   (Scenario 7)      │
        └─────────┬─────────┘       └─────────┬─────────┘       └─────────┬─────────┘
                  │                           │                           │
                  └───────────────┬───────────┴───────────────┬───────────┘
                                  │                            │
                         ┌────────▼────────┐         ┌─────────▼─────────┐
                         │   BaseTest       │         │   DriverFactory   │
                         │ (Setup/Teardown) │◄────────┤ (ThreadLocal<WD>) │
                         └────────┬─────────┘         └─────────┬─────────┘
                                  │                              │
                  ┌───────────────┴───────────────┐    ┌─────────▼─────────┐
                  │           Page Objects          │    │  Cross-Browser    │
                  │  HomePage → TopicsPage →         │    │ Chrome/Firefox/Edge│
                  │      RepositoryPage              │    └────────────────────┘
                  └───────────────┬──────────────────┘
                                  │
                  ┌───────────────┴───────────────────────────────┐
                  │                 Utilities Layer                 │
                  │  ConfigReader (Singleton) │ WaitUtil             │
                  │  ScreenshotUtil           │ CSVUtil / ExcelUtil  │
                  │  LoggerUtil (Log4j2)      │ AllureEnvironmentWriter│
                  └───────────────┬───────────────────────────────┘
                                  │
                  ┌───────────────┴───────────────────────────────┐
                  │              Cross-Cutting Concerns             │
                  │  TestListener (ITestListener) │ RetryAnalyzer   │
                  │  RetryListener (auto-attach)  │ Allure Categories│
                  └─────────────────────────────────────────────────┘
                                  │
                  ┌───────────────┴───────────────────────────────┐
                  │                     Outputs                     │
                  │ test-output/screenshots │ test-output/data       │
                  │ test-output/logs        │ allure-results/-report │
                  └─────────────────────────────────────────────────┘
```

---

## 3. Technology Stack

- **Java 17**
- **Selenium 4.21.0** (Selenium Manager / WebDriverManager for driver binaries)
- **TestNG 7.10.2**
- **Maven** (build & dependency management)
- **WebDriverManager 5.9.1** (Bonigarcia)
- **Log4j2 2.23.1**
- **Allure TestNG 2.27.0**
- **Apache Commons IO 2.16.1** / **Commons Lang3 3.14.0**
- **OpenCSV 5.9**
- **Apache POI 5.2.5** (poi + poi-ooxml)
- **Jenkins** (Declarative Pipeline)
- **GitHub Actions**

---

## 4. Folder Structure

```
GitHubAnalyticsFramework/
├── pom.xml
├── testng.xml
├── testng-crossbrowser.xml
├── Jenkinsfile
├── .gitignore
├── README.md
├── .github/
│   └── workflows/
│       └── automation.yml
├── src/
│   ├── main/java/com/automation/
│   │   ├── base/
│   │   │   └── BaseTest.java
│   │   ├── constants/
│   │   │   └── FrameworkConstants.java
│   │   ├── factory/
│   │   │   └── DriverFactory.java
│   │   ├── listeners/
│   │   │   ├── TestListener.java
│   │   │   ├── RetryAnalyzer.java
│   │   │   └── RetryListener.java
│   │   ├── models/
│   │   │   └── RepositoryData.java
│   │   ├── pages/
│   │   │   ├── HomePage.java
│   │   │   ├── TopicsPage.java
│   │   │   └── RepositoryPage.java
│   │   └── utils/
│   │       ├── ConfigReader.java
│   │       ├── ScreenshotUtil.java
│   │       ├── CSVUtil.java
│   │       ├── ExcelUtil.java
│   │       ├── WaitUtil.java
│   │       ├── LoggerUtil.java
│   │       └── AllureEnvironmentWriter.java
│   └── test/
│       ├── java/com/automation/tests/
│       │   ├── GitHubSearchTest.java
│       │   ├── RepositoryAnalyticsTest.java
│       │   └── ParallelAnalyticsTest.java
│       └── resources/
│           ├── config.properties
│           ├── log4j2.xml
│           ├── allure.properties
│           └── allure-categories/
│               └── categories.json
├── test-output/
│   ├── screenshots/
│   │   ├── pass/     (auto-captured on every test PASS)
│   │   └── fail/     (auto-captured on every test FAILURE)
│   ├── data/
│   └── logs/
└── allure-results/
```

---

## 5. Test Scenarios Covered

| # | Scenario | Test Class |
|---|---|---|
| 1 | Open GitHub Topics, validate load, screenshot, log | `GitHubSearchTest` |
| 2 | Search "Java" topic, validate repos displayed, screenshot | `GitHubSearchTest` |
| 3 | Extract repo Name/Stars/Forks/Issues/Language/Description into POJO | `RepositoryAnalyticsTest` |
| 4 | Export extracted data to CSV + Excel under `test-output/data` | `RepositoryAnalyticsTest` |
| 5 | Scroll via `JavascriptExecutor`, screenshots before/after | `RepositoryAnalyticsTest` |
| 6 | Validate Stars > 0, Forks ≥ 0, Language not null, Name not empty | `RepositoryAnalyticsTest` |
| 7 | Parallel extraction for Java/Python/Selenium/Automation (thread-count=4, ThreadLocal driver) | `ParallelAnalyticsTest` |

---

## 6. Prerequisites

- JDK 17+
- Maven 3.8+
- Google Chrome / Mozilla Firefox / Microsoft Edge installed locally
- IntelliJ IDEA or Eclipse (for local import)

---

## 7. Execution Steps

### Clone / Import
Import the project into IntelliJ or Eclipse as a **Maven project** — dependencies resolve automatically from `pom.xml`.

### Run the full suite (default `testng.xml`)
```bash
mvn clean test
```

### Run with a specific browser
```bash
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

### Run headless
```bash
mvn clean test -Dheadless=true
```

### Run the cross-browser suite
```bash
mvn clean test -DsuiteXmlFile=testng-crossbrowser.xml
```

### Run a single test class (IDE or CLI)
```bash
mvn clean test -Dtest=RepositoryAnalyticsTest
```

---

## 8. Allure Reporting

### Generate results (already produced after `mvn clean test`)
Results land in `allure-results/`.

### View the report
```bash
allure serve allure-results
```

### Generate a static HTML report
```bash
mvn allure:report
```
Report is generated at `allure-report/index.html`.

> **Note:** Install the Allure CLI separately if `allure serve` is not recognized:
> `npm install -g allure-commandline` or via `scoop`/`brew`/`sdkman` depending on OS.

Allure report includes:
- `@Step`-level breakdown of each scenario
- Pass/fail screenshots as attachments
- Environment tab (`environment.properties`, auto-generated on suite start)
- Categorized failures (`categories.json` — locator issues, timeouts, assertion failures, etc.)

---

## 9. Logging

- Powered by **Log4j2**.
- Configuration: `src/test/resources/log4j2.xml`
- Output file: `test-output/logs/framework.log` (rolling, 10MB / daily)
- Logs: browser launch, navigation, clicks, data extraction values, failures, screenshot paths, test completion status.

---

## 10. Screenshot Framework

Screenshots are captured automatically by `TestListener` (`ITestListener`) on **every**
test outcome, plus manually inside test scenarios at key checkpoints. All screenshots
are saved under `test-output/screenshots/` with timestamp-based naming
(`<name>_yyyyMMdd_HHmmss_SSS.png`) and are also attached directly to the Allure report.

**Storage layout:**

| Folder | When it's written | Written by |
|---|---|---|
| `test-output/screenshots/pass/` | Automatically, every time a test **passes** | `TestListener.onTestSuccess()` → `ScreenshotUtil.capturePassScreenshot()` |
| `test-output/screenshots/fail/` | Automatically, every time a test **fails** | `TestListener.onTestFailure()` → `ScreenshotUtil.captureFailureScreenshot()` |
| `test-output/screenshots/` (root) | Manual, in-test checkpoints (e.g. before/after scroll, data-extracted) | Explicit `ScreenshotUtil.captureScreenshot(...)` calls inside test methods |

**On failure specifically:**
- `TestListener.onTestFailure()` fires immediately when a `@Test` method throws/fails
  (before `@AfterMethod` tears the driver down), guaranteeing the browser is still open
  and on the failing page when the screenshot is taken.
- The saved path is logged at `ERROR` level in `test-output/logs/framework.log`
  immediately under the failure reason — search the log for `"Failure screenshot saved at:"`
  to jump straight to the right file after a broken run.
- The same image is attached to the Allure report as `<testMethodName>_FAIL`, viewable
  on that test's page under **Attachments**.

---

## 11. Retry Mechanism

- `RetryAnalyzer` (implements `IRetryAnalyzer`) retries any failed test up to **2 times**.
- `RetryListener` (implements `IAnnotationTransformer`) auto-attaches the analyzer to
  every `@Test` method — no need to set `retryAnalyzer` manually on each test.
- Each retry attempt that ultimately fails still gets its own failure screenshot via
  `TestListener`, so every attempt is independently traceable.

---

## 12. Jenkins Setup

1. Install plugins: **Pipeline**, **Maven Integration**, **Allure Jenkins Plugin**, **JUnit Plugin**.
2. Configure a **JDK 17** and **Maven 3** tool installation in *Manage Jenkins → Tools*
   (must match the names `JDK17` and `Maven3` referenced in the `Jenkinsfile`, or update the `tools {}` block).
3. Create a new **Pipeline** job → point **Pipeline script from SCM** to this repository → script path `Jenkinsfile`.
4. Build with parameters: `BROWSER`, `SUITE_FILE`, `HEADLESS`.
5. Post-build, the **Allure Report** link appears on the job page (via the Allure Jenkins Plugin step in `post { always { allure(...) } }`).
6. Archived artifacts: screenshots, CSV/Excel data, logs, `allure-results/`, `allure-report/`.

---

## 13. GitHub Actions Setup

Workflow file: `.github/workflows/automation.yml`

- **Triggers:** `push`, `pull_request` on `main`/`master`/`develop`, plus manual `workflow_dispatch` with a browser input.
- **Steps:** checkout → JDK 17 setup → Chrome/Firefox setup → `mvn clean test` (headless) → `mvn allure:report` → upload artifacts (`allure-results`, `allure-report`, `screenshots`, `exported-data`, `framework-logs`, `surefire-reports`).
- View results under the **Actions** tab → workflow run → **Artifacts** section.
- To view the Allure HTML report from CI: download the `allure-report` artifact and open `index.html` locally, or wire up GitHub Pages deployment as a future enhancement.

No secrets or repository configuration are required — the target site requires no authentication.

---

## 14. Screenshots

Sample artifacts produced after a run:

**`test-output/screenshots/` (root — manual, in-test checkpoints):**
- `TopicsPage_Loaded_*.png`
- `GlobalSearch_Java_Results_*.png`
- `JavaTopic_Repositories_*.png`
- `RepositoryPage_DataExtracted_*.png`
- `BeforeScroll_*.png` / `AfterScroll_*.png`
- `Parallel_<topic>_Extracted_*.png`

**`test-output/screenshots/pass/` (auto-captured by `TestListener` on every pass):**
- `<testMethodName>_PASS_*.png`

**`test-output/screenshots/fail/` (auto-captured by `TestListener` on every failure):**
- `<testMethodName>_FAIL_*.png` — start here when triaging a failed run; the matching
  path is also printed at `ERROR` level in `framework.log` right under the failure reason.

---

## 15. Future Enhancements

- Integrate **Selenium Grid / Docker Selenium** for distributed cross-browser parallel runs.
- Add **API-level validation** (GitHub REST API) to cross-check UI-scraped star/fork counts.
- Publish Allure HTML report automatically to **GitHub Pages** post-CI.
- Add **Slack/Teams notifications** on pipeline failure via Jenkins/GitHub Actions.
- Extend data model to persist historical runs into a database for trend analysis.
- Add **visual regression testing** (e.g., Applitools or Percy) for the Topics page.
- Containerize the framework with a **Dockerfile** for fully isolated, reproducible runs.

---

## 16. Quick Command Reference

| Action | Command |
|---|---|
| Run full suite | `mvn clean test` |
| Run specific browser | `mvn clean test -Dbrowser=firefox` |
| Run headless | `mvn clean test -Dheadless=true` |
| Run cross-browser suite | `mvn clean test -DsuiteXmlFile=testng-crossbrowser.xml` |
| Generate Allure HTML | `mvn allure:report` |
| Serve Allure report | `allure serve allure-results` |
| Run single class | `mvn clean test -Dtest=ParallelAnalyticsTest` |

---

**Author:** Senior SDET Automation Framework Build
**License:** Internal / Educational use against public GitHub pages.
