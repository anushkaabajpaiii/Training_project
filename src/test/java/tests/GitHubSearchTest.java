package tests;

import org.testng.Assert;
import org.testng.annotations.*;

import factory.DriverFactory;
import org.apache.logging.log4j.Logger;
import pages.HomePage;
import pages.TopicsPage;
import utils.LoggerUtil;

public class GitHubSearchTest {

    private static final Logger logger = LoggerUtil.getLogger(GitHubSearchTest.class);

    HomePage homePage;
    TopicsPage topicsPage;

    @BeforeMethod
    public void setup() {

        DriverFactory.initDriver();

        homePage = new HomePage();

        topicsPage = new TopicsPage();
    }

    @Test

    public void verifyTopicsPageLoads() {

        homePage.launchApplication();

        Assert.assertTrue(
                topicsPage.isTopicPageLoaded());

        logger.info("GitHub Topics loaded successfully");
    }

    @Test
    @Parameters("topic")

    public void openTopicSearchResults(String topic) {

        homePage.launchApplication();

        topicsPage.searchTopic(topic);

        Assert.assertTrue(
                DriverFactory.getDriver()
                        .getCurrentUrl()
                        .toLowerCase()
                        .contains(topic.toLowerCase()));

        logger.info("{} repository search opened successfully", topic);
    }

    @AfterMethod

    public void tearDown() {

        DriverFactory.quitDriver();
    }
}
