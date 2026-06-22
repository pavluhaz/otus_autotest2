package ru.otus.config;

import ru.otus.model.RegistrationData;

public class TestConfig {

    public String formUrl() {
        String formUrl = System.getProperty("form.url");

        if (formUrl != null && !formUrl.isBlank()) {
            return formUrl.trim();
        }

        String baseUrl = property("base.url", "https://otus.home.kartushin.su");
        return removeTrailingSlash(baseUrl) + "/form.html";
    }

    public boolean headless() {
        return Boolean.parseBoolean(property("headless", "false"));
    }
    public boolean startMaximized() {
    return Boolean.parseBoolean(property("start.maximized", "false"));
    }
    
    

    public RegistrationData registrationData() {
        String password = property("password", "Qwerty123!");

        return new RegistrationData(
                property("username", "Ivan Ivanov"),
                property("email", "ivan.ivanov@example.com"),
                password,
                property("confirmPassword", password),
                property("birthdate", "1997-07-08"),
                property("languageLevel", "Продвинутый")
        );
    }

    private String property(String name, String defaultValue) {
        return System.getProperty(name, defaultValue).trim();
    }

    private String removeTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
