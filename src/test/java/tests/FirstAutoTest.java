package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import ru.otus.config.TestConfig;
import ru.otus.driver.DriverFactory;
import ru.otus.model.RegistrationData;
import ru.otus.pages.RegistrationFormPage;
import ru.otus.utils.RegistrationResultChecker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FirstAutoTest {

    private WebDriver driver;
    private TestConfig config;
    private RegistrationFormPage registrationFormPage;

    @BeforeEach
    public void setUp() {
        config = new TestConfig();
        driver = DriverFactory.createChromeDriver(config.headless());
        registrationFormPage = new RegistrationFormPage(driver);
        registrationFormPage.open(config.formUrl());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Заполнение формы и проверка результата")
    public void shouldFillFormAndShowEnteredData() {
        RegistrationData registrationData = config.registrationData();

        assertEquals(
                registrationData.password(),
                registrationData.confirmPassword(),
                "Пароль и подтверждение пароля должны совпадать"
        );

        String resultText = registrationFormPage
                .fillForm(registrationData)
                .submit()
                .resultText();

        RegistrationResultChecker resultChecker = new RegistrationResultChecker(resultText);

        assertAll(
                "Проверка результата после отправки формы",
                () -> assertTrue(resultChecker.resultIsNotBlank(), "Блок результата не должен быть пустым"),
                () -> assertTrue(resultChecker.containsUsername(registrationData), "В результате нет username"),
                () -> assertTrue(resultChecker.containsEmail(registrationData), "В результате нет email"),
                () -> assertTrue(resultChecker.containsBirthdate(registrationData), "В результате нет даты рождения"),
                () -> assertTrue(resultChecker.containsLanguageLevel(registrationFormPage.languageLevelResultVariants()), "В результате нет уровня языка")
        );
    }
}