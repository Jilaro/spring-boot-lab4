package ru.urabov.lab4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.urabov.lab4.model.Response;
import ru.urabov.lab4.util.DateTimeUtil;

import java.time.Instant;

/**
 * Записывает в ответ текущее системное время.
 */
@Slf4j
@Service
@Qualifier("ModifySystemTimeResponseService")
public class ModifySystemTimeResponseService implements ModifyResponseService {

    @Override
    public Response modify(Response response) {
        String newSystemTime = DateTimeUtil.getCustomFormat().format(Instant.now());
        log.info("Изменение response: systemTime {} -> {}", response.getSystemTime(), newSystemTime);
        response.setSystemTime(newSystemTime);
        return response;
    }
}
