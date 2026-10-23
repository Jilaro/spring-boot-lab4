package ru.urabov.lab4.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответное сообщение (response).
 * Поля code, errorCode и errorMessage хранятся в виде перечислений.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private String uid;
    private String operationUid;
    private String systemTime;
    private Codes code;
    private ErrorCodes errorCode;
    private ErrorMessages errorMessage;
}
