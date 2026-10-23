package ru.urabov.lab4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.urabov.lab4.model.Request;

/**
 * Дополнительное задание: записывает в поле source, откуда пришёл request.
 */
@Slf4j
@Service
@Order(2)
public class ModifySourceRequestService implements ModifyRequestService {

    private static final String NEW_SOURCE = "service1";

    @Override
    public void modify(Request request) {
        // Сохраняем исходный source, чтобы Сервис 2 видел и его
        String newSource = request.getSource() == null
                ? NEW_SOURCE
                : NEW_SOURCE + " (" + request.getSource() + ")";
        log.info("Изменение request: source {} -> {}", request.getSource(), newSource);
        request.setSource(newSource);
    }
}
