# spring-boot-lab4

Лабораторная работа №4 по дисциплине «Технологии программирования».
Выполнил: Урабов Николай Михайлович, группа РИЗ-250213у.

Два REST-сервиса на Spring Boot 4.1.2 (Java 25, Maven, Lombok, Validation):

| Модуль | Порт | Назначение |
|--------|------|------------|
| `service1` | 8082 | Принимает request из Postman, проверяет его, меняет `systemName` и `source` и пересылает в Сервис 2 |
| `service2` | 8084 | Копия Сервиса 1 без пересылки: принимает request и пишет в лог, сколько времени он шёл |

```
Postman --request--> Сервис 1 (8082) --модифицированный request--> Сервис 2 (8084)
        <--response--
```

## Запуск

1. Открыть корневую папку `spring-boot-lab4` в IntelliJ IDEA.
2. Запустить `Service2Application`, затем `Service1Application`.
3. Импортировать в Postman коллекцию `postman/lab4.postman_collection.json` и отправить запрос 01.

Поле `systemTime` в запросе Postman заполняет сам через `{{$isoTimestamp}}` – время нажатия Send.
Сервис 1 передаёт время получения request в заголовке `X-Service1-Received-At`,
Сервис 2 выводит в лог оба интервала.
