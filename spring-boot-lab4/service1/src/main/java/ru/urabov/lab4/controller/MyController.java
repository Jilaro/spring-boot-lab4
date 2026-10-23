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
import org.springframework.web.bind.annotation.RestController;
import ru.urabov.lab4.exception.UnsupportedCodeException;
import ru.urabov.lab4.exception.ValidationFailedException;
import ru.urabov.lab4.model.Codes;
import ru.urabov.lab4.model.ErrorCodes;
import ru.urabov.lab4.model.ErrorMessages;
import ru.urabov.lab4.model.Request;
import ru.urabov.lab4.model.Response;
import ru.urabov.lab4.service.ModifyRequestService;
import ru.urabov.lab4.service.ModifyResponseService;
import ru.urabov.lab4.service.Service2Client;
import ru.urabov.lab4.service.ValidationService;
import ru.urabov.lab4.util.DateTimeUtil;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
public class MyController {

    private final ValidationService validationService;
    private final ModifyResponseService modifyResponseService;
    // Все реализации ModifyRequestService в порядке @Order: сначала systemName, затем source
    private final List<ModifyRequestService> modifyRequestServices;
    private final Service2Client service2Client;

    // @Qualifier выбирает, какая из двух реализаций ModifyResponseService будет внедрена
    public MyController(ValidationService validationService,
                        @Qualifier("ModifyOperationUidResponseService")
                        ModifyResponseService modifyResponseService,
                        List<ModifyRequestService> modifyRequestServices,
                        Service2Client service2Client) {
        this.validationService = validationService;
        this.modifyResponseService = modifyResponseService;
        this.modifyRequestServices = modifyRequestServices;
        this.service2Client = service2Client;
    }

    @PostMapping("/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request,
                                             BindingResult bindingResult) {
        // Момент получения request Сервисом 1, передаётся в Сервис 2 для расчёта времени
        Instant receivedAt = Instant.now();
        log.info("Получен request в {}: {}", receivedAt, request);

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

            modifyRequestServices.forEach(service -> service.modify(request));
            log.info("Модифицированный request: {}", request);
            service2Client.send(request, receivedAt);
        } catch (ValidationFailedException e) {
            log.error("Запрос {} не прошёл валидацию: {}", request.getUid(), e.getMessage());
            return failed(response, ErrorCodes.VALIDATION_EXCEPTION, ErrorMessages.VALIDATION, HttpStatus.BAD_REQUEST);
        } catch (UnsupportedCodeException e) {
            log.error("Запрос {} не поддерживается: {}", request.getUid(), e.getMessage());
            return failed(response, ErrorCodes.UNSUPPORTED_EXCEPTION, ErrorMessages.UNSUPPORTED, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Сюда попадает и ошибка связи с Сервисом 2
            log.error("Непредвиденная ошибка при обработке запроса {}: {}", request.getUid(), e.getMessage());
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
