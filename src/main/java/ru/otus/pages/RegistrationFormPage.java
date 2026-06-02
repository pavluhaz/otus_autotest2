package ru.otus.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.otus.model.RegistrationData;

public class RegistrationFormPage {

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "confirm_password")
    private WebElement confirmPasswordInput;

    @FindBy(id = "birthdate")
    private WebElement birthdateInput;

    @FindBy(id = "language_level")
    private WebElement languageLevelSelect;

    @FindBy(css = "input[type='submit'], button[type='submit']")
    private WebElement submitButton;

    @FindBy(id = "output")
    private WebElement outputBlock;

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final List<String> languageLevelResultVariants = new ArrayList<>();

    public RegistrationFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public RegistrationFormPage open(String url) {
        driver.get(url);
        return this;
    }

    public RegistrationFormPage fillForm(RegistrationData data) {
        type(usernameInput, data.username());
        type(emailInput, data.email());
        type(passwordInput, data.password());
        type(confirmPasswordInput, data.confirmPassword());
        setDate(data.birthdate());
        selectLanguageLevel(data.languageLevel());
        return this;
    }

    public RegistrationFormPage submit() {
        visible(submitButton).click();
        return this;
    }

    public List<String> languageLevelResultVariants() {
        return languageLevelResultVariants;
    }

    public String resultText() {
        return wait.until(driver -> {
            String text = outputBlock.getText();
            return text == null || text.isBlank() ? null : text;
        });
    }

    private void type(WebElement element, String text) {
        WebElement visibleElement = visible(element);
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    private void setDate(String isoDate) {
        WebElement element = visible(birthdateInput);

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
        Select select = new Select(visible(languageLevelSelect));
        select.selectByVisibleText(languageLevel);

        WebElement selectedOption = select.getFirstSelectedOption();

        languageLevelResultVariants.clear();
        languageLevelResultVariants.add(languageLevel);
        languageLevelResultVariants.add(selectedOption.getText());
        languageLevelResultVariants.add(selectedOption.getAttribute("value"));
    }

    private WebElement visible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
}