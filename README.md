# RouteWise

## Назначение

Backend-сервис для планирования маршрутов между городами.

## Текущие возможности

- Запуск Spring Boot приложения.
- Проверка состояния через `GET /actuator/health`.

## Запуск

Требуются JDK 17 и Maven 3.6.3 или новее.

```bash
mvn spring-boot:run
```

Проверка состояния приложения:

```bash
curl http://localhost:8080/actuator/health
```

Запуск тестов:

```bash
mvn verify
```
