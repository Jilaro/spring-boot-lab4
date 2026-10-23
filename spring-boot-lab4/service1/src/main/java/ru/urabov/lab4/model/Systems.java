package ru.urabov.lab4.model;

/**
 * Системы-отправители (поле systemName входящего сообщения).
 * В JSON передаётся имя константы: "ERP", "CRM", "WMS" или "SERVICE1".
 */
public enum Systems {

    ERP("Enterprise Resource Planning"),
    CRM("Customer Relationship Management"),
    WMS("Warehouse Management System"),
    // Сервис 1 указывает себя отправителем, когда пересылает request в Сервис 2
    SERVICE1("Service 1");

    private final String description;

    Systems(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
