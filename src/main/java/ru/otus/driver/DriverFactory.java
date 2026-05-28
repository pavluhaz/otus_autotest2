package ru.otus.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");

        if (headless) {
            options.addArguments("--headless=new");
        }

        return new ChromeDriver(options);
    }
}
