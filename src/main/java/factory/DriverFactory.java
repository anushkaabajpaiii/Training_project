package factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import utils.ConfigReader;
import utils.LoggerUtil;

import java.time.Duration;

public class DriverFactory {

    private static final Logger logger = LoggerUtil.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driver =
            new ThreadLocal<>();
    private static volatile boolean chromeDriverReady = false;
    private static volatile boolean firefoxDriverReady = false;
    private static volatile boolean edgeDriverReady = false;

    public static void initDriver() {

        String browser =
                ConfigReader.getInstance()
                        .getProperty("browser");

        switch (browser.toLowerCase()) {

            case "firefox":

                setupFirefoxDriver();
                driver.set(new FirefoxDriver());
                break;

            case "edge":

                setupEdgeDriver();
                driver.set(new EdgeDriver());
                break;

            default:

                setupChromeDriver();
                driver.set(new ChromeDriver());

        }

        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        getDriver().manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        logger.info("Initialized {} browser for thread {}", browser, Thread.currentThread().getName());
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {

        if(driver.get() != null) {
            driver.get().quit();
            driver.remove();
            logger.info("Browser closed for thread {}", Thread.currentThread().getName());
        }
    }

    private static synchronized void setupChromeDriver() {
        if (!chromeDriverReady) {
            WebDriverManager.chromedriver().setup();
            chromeDriverReady = true;
        }
    }

    private static synchronized void setupFirefoxDriver() {
        if (!firefoxDriverReady) {
            WebDriverManager.firefoxdriver().setup();
            firefoxDriverReady = true;
        }
    }

    private static synchronized void setupEdgeDriver() {
        if (!edgeDriverReady) {
            WebDriverManager.edgedriver().setup();
            edgeDriverReady = true;
        }
    }
}
