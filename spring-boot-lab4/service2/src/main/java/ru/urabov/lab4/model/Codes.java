package ru.urabov.lab4.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Результат обработки запроса (поле code).
 */
public enum Codes {

    SUCCESS("success"),
    FAILED("failed");

    private final String name;

    Codes(String name) {
        this.name = name;
    }

    // В JSON попадает строка name, а не имя константы
    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
