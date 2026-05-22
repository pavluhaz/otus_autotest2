package tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FirstAutoTest {

    private static final Logger logger = LogManager.getLogger(FirstAutoTest.class);
    private static final String DEFAULT_FORM_URL = "https://otus.home.kartushin.su/form.html";
    private static final String DEFAULT_REMOTE_URL = "http://localhost:4444";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() throws Exception {
        String remoteUrl = property("remote.url", DEFAULT_REMOTE_URL);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");

        if (Boolean.parseBoolean(property("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        driver = new RemoteWebDriver(URI.create(remoteUrl).toURL(), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(property("form.url", DEFAULT_FORM_URL));
        logger.info("Открыта форма регистрации через RemoteWebDriver: {}", remoteUrl);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Браузер закрыт");
        }
    }

    @Test
    @DisplayName("Заполнение формы и проверка результата")
    public void shouldFillFormAndShowEnteredData() {
        String username = requiredProperty("username");
        String email = requiredProperty("email");
        String password = requiredProperty("password");
        String confirmPassword = property("confirmPassword", password);
        String birthdate = property("birthdate", "1997-07-08");
        String languageLevel = property("languageLevel", "Продвинутый");

        assertEquals(password, confirmPassword, "Пароль и подтверждение пароля должны совпадать");

        logger.info("Заполняем форму: username={}, email={}, birthdate={}, languageLevel={}",
                username, email, birthdate, languageLevel);

        type(By.id("username"), username);
        type(By.id("email"), email);
        type(By.id("password"), password);
        type(By.id("confirm_password"), confirmPassword);
        setDate(By.id("birthdate"), birthdate);

        Select languageSelect = new Select(visible(By.id("language_level")));
        languageSelect.selectByVisibleText(languageLevel);

        WebElement selectedLanguageOption = languageSelect.getFirstSelectedOption();
        String selectedLanguageText = selectedLanguageOption.getText();
        String selectedLanguageValue = selectedLanguageOption.getAttribute("value");

        List<String> languageVariants = new ArrayList<>();
        languageVariants.add(languageLevel);
        languageVariants.add(selectedLanguageText);
        languageVariants.add(selectedLanguageValue);

        visible(By.cssSelector("input[type='submit'], button[type='submit']")).click();

        String result = waitForNotEmptyText(By.id("output"));
        logger.info("Результат после отправки формы: {}", result);
        System.out.println("\n=== РЕЗУЛЬТАТ ФОРМЫ ===\n" + result + "\n======================\n");

        assertAll("Проверка результата после отправки формы",
                () -> assertFalse(result.isBlank(), "Блок результата не должен быть пустым"),
                () -> assertContains(result, username, "В результате нет username"),
                () -> assertContains(result, email, "В результате нет email"),
                () -> assertDateContains(result, birthdate, "В результате нет даты рождения"),
                () -> assertContainsAny(result, languageVariants, "В результате нет уровня языка")
        );
    }

    private void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }

    private void setDate(By locator, String isoDate) {
        WebElement element = visible(locator);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element,
                isoDate
        );

        assertEquals(isoDate, element.getAttribute("value"),
                "Дата рождения не установилась в поле input[type=date]");
    }

    private WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private String waitForNotEmptyText(By locator) {
        return wait.until(driver -> {
            String text = driver.findElement(locator).getText();
            return text == null || text.isBlank() ? null : text;
        });
    }

    private void assertContains(String actualText, String expectedText, String message) {
        assertTrue(normalize(actualText).contains(normalize(expectedText)), message);
    }

    private void assertContainsAny(String actualText, List<String> expectedVariants, String message) {
        String normalizedActual = normalize(actualText);
        boolean found = expectedVariants.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(this::normalize)
                .anyMatch(normalizedActual::contains);

        assertTrue(found, message + ". Ожидали один из вариантов: " + expectedVariants + ". Фактический текст: " + actualText);
    }

    private void assertDateContains(String actualText, String isoDate, String message) {
        LocalDate date = LocalDate.parse(isoDate);
        List<String> dateVariants = new ArrayList<>();

        dateVariants.add(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateVariants.add(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        dateVariants.add(date.format(DateTimeFormatter.ofPattern("d.M.yyyy")));
        dateVariants.add(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateVariants.add(date.format(DateTimeFormatter.ofPattern("d/M/yyyy")));
        dateVariants.add(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        dateVariants.add(date.format(DateTimeFormatter.ofPattern("M/d/yyyy")));

        assertContainsAny(actualText, dateVariants, message);
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Не передан обязательный параметр -D" + name);
        }
        return value.trim();
    }

    private String property(String name, String defaultValue) {
        return System.getProperty(name, defaultValue).trim();
    }
}
