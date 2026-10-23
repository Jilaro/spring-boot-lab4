package ru.urabov.lab4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.urabov.lab4.model.Response;

import java.util.UUID;

/**
 * Записывает в ответ новый operationUid.
 */
@Slf4j
@Service
@Qualifier("ModifyOperationUidResponseService")
public class ModifyOperationUidResponseService implements ModifyResponseService {

    @Override
    public Response modify(Response response) {
        // UUID без дефисов занимает ровно 32 символа, как и ограничение на operationUid
        String newOperationUid = UUID.randomUUID().toString().replace("-", "");
        log.info("Изменение response: operationUid {} -> {}", response.getOperationUid(), newOperationUid);
        response.setOperationUid(newOperationUid);
        return response;
    }
}
