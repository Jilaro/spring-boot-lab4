package ru.urabov.lab4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.urabov.lab4.model.Request;
import ru.urabov.lab4.model.Systems;

/**
 * Записывает в request систему-отправителя: теперь это Сервис 1.
 */
@Slf4j
@Service
@Order(1)
public class ModifySystemNameRequestService implements ModifyRequestService {

    @Override
    public void modify(Request request) {
        log.info("Изменение request: systemName {} -> {}", request.getSystemName(), Systems.SERVICE1);
        request.setSystemName(Systems.SERVICE1);
    }
}
