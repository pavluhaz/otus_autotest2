package ru.otus.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import ru.otus.model.RegistrationData;

public class RegistrationResultChecker {

    private final String resultText;

    public RegistrationResultChecker(String resultText) {
        this.resultText = resultText;
    }

    public boolean resultIsNotBlank() {
        return resultText != null && !resultText.isBlank();
    }

    public boolean containsUsername(RegistrationData data) {
        return contains(data.username());
    }

    public boolean containsEmail(RegistrationData data) {
        return contains(data.email());
    }

    public boolean containsBirthdate(RegistrationData data) {
        LocalDate date = LocalDate.parse(data.birthdate());

        List<String> dateVariants = List.of(
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                date.format(DateTimeFormatter.ofPattern("d.M.yyyy")),
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                date.format(DateTimeFormatter.ofPattern("d/M/yyyy")),
                date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                date.format(DateTimeFormatter.ofPattern("M/d/yyyy"))
        );

        return containsAny(dateVariants);
    }

    public boolean containsLanguageLevel(List<String> languageLevelVariants) {
        return containsAny(languageLevelVariants);
    }

    private boolean contains(String expectedText) {
        return normalize(resultText).contains(normalize(expectedText));
    }

    private boolean containsAny(List<String> expectedVariants) {
        String normalizedResult = normalize(resultText);

        return expectedVariants.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(this::normalize)
                .anyMatch(normalizedResult::contains);
    }

    private String normalize(String text) {
        return text == null
                ? ""
                : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
