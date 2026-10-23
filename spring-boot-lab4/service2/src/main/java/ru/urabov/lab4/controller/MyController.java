package ru.urabov.lab4.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.urabov.lab4.exception.UnsupportedCodeException;
import ru.urabov.lab4.exception.ValidationFailedException;
import ru.urabov.lab4.model.Codes;
import ru.urabov.lab4.model.ErrorCodes;
import ru.urabov.lab4.model.ErrorMessages;
import ru.urabov.lab4.model.Request;
import ru.urabov.lab4.model.Response;
import ru.urabov.lab4.service.ModifyResponseService;
import ru.urabov.lab4.service.ValidationService;
import ru.urabov.lab4.util.DateTimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Slf4j
@RestController
public class MyController {

    private final ValidationService validationService;
    private final ModifyResponseService modifyResponseService;

    // @Qualifier выбирает, какая из двух реализаций ModifyResponseService будет внедрена
    public MyController(ValidationService validationService,
                        @Qualifier("ModifyOperationUidResponseService")
                        ModifyResponseService modifyResponseService) {
        this.validationService = validationService;
        this.modifyResponseService = modifyResponseService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request,
                                             BindingResult bindingResult,
                                             @RequestHeader(value = "X-Service1-Received-At", required = false)
                                             String service1ReceivedAt) {
        Instant receivedAt = Instant.now();
        log.info("Получен request от Сервиса 1: {}", request);
        logElapsedTime(request, service1ReceivedAt, receivedAt);

        Response response = Response.builder()
                .uid(request.getUid())
                .operationUid(request.getOperationUid())
                .systemTime(DateTimeUtil.getCustomFormat().format(Instant.now()))
                .code(Codes.SUCCESS)
                .errorCode(ErrorCodes.EMPTY)
                .errorMessage(ErrorMessages.EMPTY)
                .build();
        log.info("Создан response: {}", response);

        try {
            validationService.isValid(bindingResult);
            validationService.checkUid(request);
        } catch (ValidationFailedException e) {
            log.error("Запрос {} не прошёл валидацию: {}", request.getUid(), e.getMessage());
            return failed(response, ErrorCodes.VALIDATION_EXCEPTION, ErrorMessages.VALIDATION, HttpStatus.BAD_REQUEST);
        } catch (UnsupportedCodeException e) {
            log.error("Запрос {} не поддерживается: {}", request.getUid(), e.getMessage());
            return failed(response, ErrorCodes.UNSUPPORTED_EXCEPTION, ErrorMessages.UNSUPPORTED, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обработке запроса {}", request.getUid(), e);
            return failed(response, ErrorCodes.UNKNOWN_EXCEPTION, ErrorMessages.UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        modifyResponseService.modify(response);
        log.info("Отправлен response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Тело запроса не удалось прочитать: некорректный JSON или неизвестное значение systemName
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleNotReadable(HttpMessageNotReadableException e) {
        log.error("Не удалось прочитать request: {}", e.getMostSpecificCause().getMessage());
        Response response = Response.builder()
                .uid("")
                .operationUid("")
                .systemTime(DateTimeUtil.getCustomFormat().format(Instant.now()))
                .build();
        return failed(response, ErrorCodes.VALIDATION_EXCEPTION, ErrorMessages.VALIDATION, HttpStatus.BAD_REQUEST);
    }

    // Выводит в лог, сколько времени прошло до получения request Сервисом 2
    private void logElapsedTime(Request request, String service1ReceivedAt, Instant receivedAt) {
        if (service1ReceivedAt != null) {
            long ms = Duration.between(Instant.parse(service1ReceivedAt), receivedAt).toMillis();
            log.info("С момента получения request Сервисом 1 прошло {} мс", ms);
        } else {
            log.info("Request пришёл не от Сервиса 1: заголовка X-Service1-Received-At нет");
        }
        // systemTime заполняет Postman в момент нажатия Send
        try {
            long ms = Duration.between(Instant.parse(request.getSystemTime()), receivedAt).toMillis();
            log.info("С момента нажатия Send в Postman (systemTime) прошло {} мс", ms);
        } catch (DateTimeParseException | NullPointerException e) {
            log.info("systemTime = {} не является временем в формате ISO-8601", request.getSystemTime());
        }
    }

    // Заполняет ответ данными об ошибке
    private ResponseEntity<Response> failed(Response response, ErrorCodes errorCode,
                                            ErrorMessages errorMessage, HttpStatus status) {
        response.setCode(Codes.FAILED);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        log.info("Отправлен response с ошибкой: {}", response);
        return new ResponseEntity<>(response, status);
    }
}
