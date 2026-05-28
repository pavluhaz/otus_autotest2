package ru.otus.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.otus.model.RegistrationData;

public class RegistrationFormPage {

    private static final By USERNAME_INPUT = By.id("username");
    private static final By EMAIL_INPUT = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By CONFIRM_PASSWORD_INPUT = By.id("confirm_password");
    private static final By BIRTHDATE_INPUT = By.id("birthdate");
    private static final By LANGUAGE_LEVEL_SELECT = By.id("language_level");
    private static final By SUBMIT_BUTTON = By.cssSelector("input[type='submit'], button[type='submit']");
    private static final By OUTPUT_BLOCK = By.id("output");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final List<String> languageLevelResultVariants = new ArrayList<>();

    public RegistrationFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public RegistrationFormPage open(String url) {
        driver.get(url);
        return this;
    }

    public RegistrationFormPage fillForm(RegistrationData data) {
        type(USERNAME_INPUT, data.username());
        type(EMAIL_INPUT, data.email());
        type(PASSWORD_INPUT, data.password());
        type(CONFIRM_PASSWORD_INPUT, data.confirmPassword());
        setDate(data.birthdate());
        selectLanguageLevel(data.languageLevel());
        return this;
    }

    public RegistrationFormPage submit() {
        visible(SUBMIT_BUTTON).click();
        return this;
    }

    public List<String> languageLevelResultVariants() {
        return languageLevelResultVariants;
    }

    public String resultText() {
        return wait.until(driver -> {
            String text = driver.findElement(OUTPUT_BLOCK).getText();
            return text == null || text.isBlank() ? null : text;
        });
    }

    private void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }

    private void setDate(String isoDate) {
        WebElement element = visible(BIRTHDATE_INPUT);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element,
                isoDate
        );

        if (!isoDate.equals(element.getAttribute("value"))) {
            throw new IllegalStateException("Дата рождения не установилась в поле input[type=date]");
        }
    }

    private void selectLanguageLevel(String languageLevel) {
        Select select = new Select(visible(LANGUAGE_LEVEL_SELECT));
        select.selectByVisibleText(languageLevel);

        WebElement selectedOption = select.getFirstSelectedOption();
        languageLevelResultVariants.clear();
        languageLevelResultVariants.add(languageLevel);
        languageLevelResultVariants.add(selectedOption.getText());
        languageLevelResultVariants.add(selectedOption.getAttribute("value"));
    }

    private WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
