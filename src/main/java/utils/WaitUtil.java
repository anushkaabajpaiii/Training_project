package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import factory.DriverFactory;

import java.time.Duration;

public class WaitUtil {

    private static final int TIMEOUT = 20;

    public static void waitForElement(WebElement element) {
        new WebDriverWait(
                DriverFactory.getDriver(),
                Duration.ofSeconds(TIMEOUT))
                .until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForElementClickable(WebElement element) {
        new WebDriverWait(
                DriverFactory.getDriver(),
                Duration.ofSeconds(TIMEOUT))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForElementLocated(By locator) {
        return new WebDriverWait(
                DriverFactory.getDriver(),
                Duration.ofSeconds(TIMEOUT))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void waitForUrlContains(String text) {
        new WebDriverWait(
                DriverFactory.getDriver(),
                Duration.ofSeconds(TIMEOUT))
                .until(ExpectedConditions.urlContains(text));
    }

    public static void waitForPageReady() {
        new WebDriverWait(
                DriverFactory.getDriver(),
                Duration.ofSeconds(TIMEOUT))
                .until(driver -> "complete".equals(
                        ((JavascriptExecutor) driver)
                                .executeScript("return document.readyState")));
    }
}
