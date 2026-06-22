package ru.otus.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public WebDriver createChromeDriver(boolean headless, boolean startMaximized) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        if (startMaximized) {
            options.addArguments("--start-maximized");
        }

        return new ChromeDriver(options);
    }
}