package ru.urabov.lab4.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Входящее сообщение (request).
 * Обязательные поля проверяются аннотациями Bean Validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    // Уникальный идентификатор сообщения, обязательный, до 32 символов
    @NotBlank
    @Size(max = 32)
    private String uid;

    // Уникальный идентификатор операции, обязательный, до 32 символов
    @NotBlank
    @Size(max = 32)
    private String operationUid;

    // Система отправителя, необязательное поле: ERP, CRM, WMS или SERVICE1
    private Systems systemName;

    // Время создания сообщения, обязательное
    @NotBlank
    private String systemTime;

    // Наименование ресурса, необязательное
    private String source;

    // Уникальный идентификатор коммуникации, от 1 до 100000
    @Min(1)
    @Max(100000)
    private int communicationId;

    // Необязательные числовые поля
    private int templateId;
    private int productCode;
    private int smsCode;

    // Собственный toString() вместо сгенерированного Lombok: одна строка для логов
    @Override
    public String toString() {
        return "Request{" +
                "uid='" + uid + '\'' +
                ", operationUid='" + operationUid + '\'' +
                ", systemName=" + systemName +
                ", systemTime='" + systemTime + '\'' +
                ", source='" + source + '\'' +
                ", communicationId=" + communicationId +
                ", templateId=" + templateId +
                ", productCode=" + productCode +
                ", smsCode=" + smsCode +
                '}';
    }
}
