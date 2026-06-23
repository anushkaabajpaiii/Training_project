package tests;

import factory.DriverFactory;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import pages.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.LoggerUtil;

import java.io.ByteArrayInputStream;

public class RepositoryAnalyticsTest {

    private static final Logger logger = LoggerUtil.getLogger(RepositoryAnalyticsTest.class);

    HomePage homePage;

    TopicsPage topicsPage;

    RepositoryPage repositoryPage;

    @BeforeMethod

    public void setup() {

        DriverFactory.initDriver();

        homePage = new HomePage();

        topicsPage = new TopicsPage();

        repositoryPage = new RepositoryPage();
    }

    @Test
    @Parameters("topic")

    public void extractRepositoryData(String topic) {

        logger.info("================================================================================");
        logger.info("GitHub Repository Analytics Test Started for topic: {}", topic);
        logger.info("================================================================================");

        homePage.launchApplication();

                topicsPage.open();
                topicsPage.searchTopic(topic);

                repositoryPage.openFirstRepository();

                // Demo: scroll and capture screenshots for Allure
                try {
                        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
                        js.executeScript("window.scrollBy(0,600)");
                        Thread.sleep(1500);
                        byte[] shot1 = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
                        Allure.addAttachment("topic-scroll-1", new ByteArrayInputStream(shot1));

                        js.executeScript("window.scrollBy(0,600)");
                        Thread.sleep(1500);
                        byte[] shot2 = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
                        Allure.addAttachment("topic-scroll-2", new ByteArrayInputStream(shot2));
                } catch (Exception e) {
                        logger.warn("Screenshot/scroll step failed: {}", e.getMessage());
                }

        logger.info("[EXTRACTION] Repository Information:");
        logger.info("Current URL = {}", DriverFactory.getDriver().getCurrentUrl());
        logger.info("Page Title = {}", DriverFactory.getDriver().getTitle());

        logger.info("Repository Name : {}", repositoryPage.getRepositoryName());
        logger.info("Stars : {}", repositoryPage.getStars());
        logger.info("Forks : {}", repositoryPage.getForks());
        logger.info("Language : {}", repositoryPage.getLanguage());
        logger.info("Description : {}", repositoryPage.getDescription());

        logger.info("Test completed successfully for topic: {}", topic);
        logger.info("================================================================================");
    }

    @AfterMethod

    public void tearDown() {

        DriverFactory.quitDriver();
    }
}
