package ru.otus.model;

public record RegistrationData(
        String username,
        String email,
        String password,
        String confirmPassword,
        String birthdate,
        String languageLevel
) {
}
