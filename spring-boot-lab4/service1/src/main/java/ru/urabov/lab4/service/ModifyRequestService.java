package ru.urabov.lab4.service;

import ru.urabov.lab4.model.Request;

/**
 * Изменение request перед пересылкой в Сервис 2.
 */
public interface ModifyRequestService {

    void modify(Request request);
}
