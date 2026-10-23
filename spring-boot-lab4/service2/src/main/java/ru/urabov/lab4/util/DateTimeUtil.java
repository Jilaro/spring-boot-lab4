package ru.urabov.lab4.util;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Утилитный класс для работы с датой и временем.
 */
public final class DateTimeUtil {

    // Формат времени как в примере ответа: 2023-10-07T07:47:56.503Z
    private static final DateTimeFormatter CUSTOM_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    private DateTimeUtil() {
        // объект утилитного класса не создаётся
    }

    // DateTimeFormatter неизменяемый и потокобезопасный, поэтому один объект на всё приложение
    public static DateTimeFormatter getCustomFormat() {
        return CUSTOM_FORMAT;
    }
}
