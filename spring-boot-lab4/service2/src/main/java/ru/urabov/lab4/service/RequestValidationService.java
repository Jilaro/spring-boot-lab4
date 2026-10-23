package ru.urabov.lab4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.urabov.lab4.exception.UnsupportedCodeException;
import ru.urabov.lab4.exception.ValidationFailedException;
import ru.urabov.lab4.model.Request;

import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestValidationService implements ValidationService {

    private static final String UNSUPPORTED_UID = "123";

    @Override
    public void isValid(BindingResult bindingResult) throws ValidationFailedException {
        if (bindingResult.hasErrors()) {
            // Каждую ошибку из bindingResult выводим отдельной строкой
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.error("Ошибка в поле {}: {} (передано: {})",
                        error.getField(), error.getDefaultMessage(), error.getRejectedValue());
            }
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + " - " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            log.error("Выброшено ValidationFailedException: {}", errors);
            throw new ValidationFailedException(errors);
        }
        log.info("Валидация пройдена");
    }

    @Override
    public void checkUid(Request request) throws UnsupportedCodeException {
        if (UNSUPPORTED_UID.equals(request.getUid())) {
            log.error("Выброшено UnsupportedCodeException: uid = {}", request.getUid());
            throw new UnsupportedCodeException("uid = " + UNSUPPORTED_UID + " не поддерживается");
        }
        log.info("uid {} поддерживается", request.getUid());
    }
}
