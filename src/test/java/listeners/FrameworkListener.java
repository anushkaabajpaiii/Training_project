package listeners;

import factory.DriverFactory;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.LoggerUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FrameworkListener implements ITestListener, ISuiteListener {

    private static final Logger logger = LoggerUtil.getLogger(FrameworkListener.class);
    private static final Path ALLURE_RESULTS = Path.of("allure-results");

    @Override
    public void onStart(ISuite suite) {
        try {
            Files.createDirectories(ALLURE_RESULTS);
            writeEnvironmentProperties();
            writeCategoriesJson();
            logger.info("Allure environment and categories metadata generated");
        } catch (IOException e) {
            logger.error("Could not generate Allure metadata", e);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: {}", result.getName());
        attachScreenshot("PASS - " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: {}", result.getName(), result.getThrowable());
        attachScreenshot("FAIL - " + result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: {}", result.getName());
    }

    private void attachScreenshot(String attachmentName) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            logger.warn("Screenshot skipped because WebDriver is not available");
            return;
        }

        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(attachmentName, "image/png",
                    new ByteArrayInputStream(screenshot), ".png");
        } catch (Exception e) {
            logger.error("Could not attach screenshot '{}'", attachmentName, e);
        }
    }

    private void writeEnvironmentProperties() throws IOException {
        Properties environment = new Properties();
        environment.setProperty("Browser", "Chrome");
        environment.setProperty("Environment", "QA");
        environment.setProperty("OS", System.getProperty("os.name"));
        environment.setProperty("Java", System.getProperty("java.version"));

        try (var output = Files.newOutputStream(ALLURE_RESULTS.resolve("environment.properties"))) {
            environment.store(output, "Allure environment");
        }
    }

    private void writeCategoriesJson() throws IOException {
        String categories = """
                [
                  {
                    "name": "UI Failure",
                    "matchedStatuses": ["failed"],
                    "messageRegex": ".*(ElementClickInterceptedException|TimeoutException|StaleElementReferenceException).*"
                  },
                  {
                    "name": "Locator Failure",
                    "matchedStatuses": ["failed"],
                    "messageRegex": ".*(NoSuchElementException|Unable to locate element|no such element).*"
                  },
                  {
                    "name": "Assertion Failure",
                    "matchedStatuses": ["failed"],
                    "traceRegex": ".*(AssertionError|Assert).*"
                  }
                ]
                """;

        Files.writeString(ALLURE_RESULTS.resolve("categories.json"), categories, StandardCharsets.UTF_8);
    }
}
