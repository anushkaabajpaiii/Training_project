package pages;

import factory.DriverFactory;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import utils.ConfigReader;
import utils.LoggerUtil;
import utils.WaitUtil;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class TopicsPage {

    private static final Logger logger = LoggerUtil.getLogger(TopicsPage.class);

    private WebDriver driver;

    public TopicsPage() {

        this.driver = DriverFactory.getDriver();

        PageFactory.initElements(driver, this);
    }

    public TopicsPage(WebDriver driver) {

        this.driver = driver;

        PageFactory.initElements(driver, this);
    }

    private static final List<By> SEARCH_INPUT_LOCATORS = List.of(
            By.cssSelector("input.QueryBuilder-Input"),
            By.cssSelector("input[name='q']"),
            By.cssSelector("#query-builder-test"),
            By.cssSelector("input[placeholder*='Search']"),
            By.cssSelector("input[aria-label*='Search']")
    );

    private static final List<By> SEARCH_TRIGGER_LOCATORS = List.of(
            By.xpath("//button[contains(.,'Type') and contains(.,'search')]"),
            By.xpath("//button[contains(@aria-label,'Search')]"),
            By.xpath("//input[contains(@placeholder,'search') or contains(@placeholder,'Search')]")
    );

    /**
     * Search for a topic through the visible GitHub search box, then load repository results.
     */
    public void searchTopic(String topic) {
        String searchKeyword = topic.trim();
        String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);

        try {
            logger.info("[STEP] Opening GitHub Topics page for visible search flow");
            driver.get(ConfigReader.getInstance().getProperty("baseUrl"));
            WaitUtil.waitForPageReady();

            WebElement searchBox = openVisibleSearchBox();
            logger.info("[ACTION] Typing into GitHub search box: {}", searchKeyword);
            searchBox.clear();
            typeSlowly(searchBox, searchKeyword);

            Thread.sleep(700);
            logger.info("[ACTION] Pressing ENTER to submit search");
            searchBox.sendKeys(Keys.ENTER);

            WaitUtil.waitForUrlContains("/search");
            WaitUtil.waitForPageReady();
            openRepositoryResultsIfNeeded(encodedKeyword);
            logger.info("[SUCCESS] Visible search completed for: {}", searchKeyword);

        } catch (Exception e) {
            logger.warn("[FALLBACK] Visible search failed, using direct repository search URL for: {}", searchKeyword);
            logger.error("[ERROR] Reason: {}", e.getMessage());
            try {
                driver.get("https://github.com/search?q=" + encodedKeyword + "&type=repositories");
                WaitUtil.waitForPageReady();
                WaitUtil.waitForUrlContains("/search");
                logger.info("[SUCCESS] Repository search loaded via fallback URL: {}", searchKeyword);
            } catch (Exception fallbackException) {
                throw new IllegalStateException("Could not load GitHub search page for: " + searchKeyword,
                        fallbackException);
            }
        }
    }

    private WebElement openVisibleSearchBox() {
        clickSearchTriggerIfPresent();

        try {
            new Actions(driver).sendKeys("/").perform();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement searchBox = findDisplayedSearchInput();
        WaitUtil.waitForElementClickable(searchBox);
        searchBox.click();
        return searchBox;
    }

    private void clickSearchTriggerIfPresent() {
        for (By locator : SEARCH_TRIGGER_LOCATORS) {
            List<WebElement> triggers = driver.findElements(locator);
            for (WebElement trigger : triggers) {
                if (trigger.isDisplayed() && trigger.isEnabled()) {
                    trigger.click();
                    return;
                }
            }
        }
    }

    private WebElement findDisplayedSearchInput() {
        long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();

        while (System.nanoTime() < deadline) {
            for (By locator : SEARCH_INPUT_LOCATORS) {
                List<WebElement> inputs = driver.findElements(locator);
                for (WebElement input : inputs) {
                    if (input.isDisplayed() && input.isEnabled()) {
                        return input;
                    }
                }
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new IllegalStateException("Visible GitHub search input was not available.");
    }

    private void openRepositoryResultsIfNeeded(String encodedKeyword) {
        if (driver.getCurrentUrl().contains("type=repositories")) {
            return;
        }

        List<WebElement> repositoryTabs = driver.findElements(
                By.xpath("//a[contains(@href,'type=repositories') or contains(@href,'type=Repositories')]"));
        if (!repositoryTabs.isEmpty()) {
            repositoryTabs.get(0).click();
            WaitUtil.waitForPageReady();
            return;
        }

        driver.get("https://github.com/search?q=" + encodedKeyword + "&type=repositories");
        WaitUtil.waitForPageReady();
    }

    private void typeSlowly(WebElement element, String text) throws InterruptedException {
        for (char ch : text.toCharArray()) {
            element.sendKeys(String.valueOf(ch));
            Thread.sleep(180);
        }
    }

    public void openJavaTopic() {
        searchTopic("Java");
    }

    public void openPythonTopic() {
        searchTopic("Python");
    }

    public boolean isTopicPageLoaded() {

        return driver.getCurrentUrl()
                .contains("/topics");
    }

    public void open() {
        driver.get(
                ConfigReader.getInstance()
                        .getProperty("baseUrl"));
    }
}
