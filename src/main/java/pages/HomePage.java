package pages;

import factory.DriverFactory;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

public class HomePage {

    private WebDriver driver;

    public HomePage() {
        this.driver = DriverFactory.getDriver();
    }

    public void launchApplication() {

        driver.get(
                ConfigReader.getInstance()
                        .getProperty("baseUrl"));
    }

    public String getTitle() {
        return driver.getTitle();
    }
}