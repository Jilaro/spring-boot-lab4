package ru.urabov.lab4.exception;

/**
 * Выбрасывается, если входящее сообщение не прошло валидацию.
 */
public class ValidationFailedException extends Exception {

    public ValidationFailedException(String message) {
        super(message);
    }
}
