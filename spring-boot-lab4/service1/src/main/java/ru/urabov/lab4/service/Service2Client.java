package ru.urabov.lab4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.urabov.lab4.model.Request;

import java.time.Instant;

/**
 * Пересылает модифицированный request в Сервис 2.
 */
@Slf4j
@Service
public class Service2Client {

    // Заголовок, в котором Сервис 2 получает время приёма request Сервисом 1
    public static final String RECEIVED_AT_HEADER = "X-Service1-Received-At";

    private final RestClient restClient = RestClient.create();
    private final String service2Url;

    public Service2Client(@Value("${service2.url}") String service2Url) {
        this.service2Url = service2Url;
    }

    public String send(Request request, Instant receivedAt) {
        log.info("Отправка request в Сервис 2 ({}): {}", service2Url, request);
        // Ответ Сервиса 2 читаем как строку: Сервису 1 он нужен только для лога
        String response = restClient.post()
                .uri(service2Url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(RECEIVED_AT_HEADER, receivedAt.toString())
                .body(request)
                .retrieve()
                .body(String.class);
        log.info("Получен ответ от Сервиса 2: {}", response);
        return response;
    }
}
