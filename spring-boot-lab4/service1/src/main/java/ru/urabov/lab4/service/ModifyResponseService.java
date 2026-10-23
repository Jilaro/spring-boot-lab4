package ru.urabov.lab4.service;

import ru.urabov.lab4.model.Response;

/**
 * Изменение ответа перед отправкой клиенту.
 */
public interface ModifyResponseService {

    Response modify(Response response);
}
