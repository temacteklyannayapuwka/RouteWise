# RouteWise

## Назначение

Backend-сервис для планирования маршрутов между городами.

## Текущие возможности

- Запуск Spring Boot приложения и проверка состояния через `GET /actuator/health`.
- Создание городов через `POST /api/cities`.
- Просмотр списка городов через `GET /api/cities`.
- Хранение городов в PostgreSQL с созданием схемы через Flyway.
- Проверка названия города: от 1 до 120 символов.

## Запуск

Требуются JDK 17, Maven 3.6.3 или новее и PostgreSQL 16.

Создайте пользователя и базу данных в PostgreSQL:

```sql
CREATE USER routewise WITH PASSWORD 'routewise';
CREATE DATABASE routewise OWNER routewise;
```

При необходимости задайте параметры подключения через `DB_URL`, `DB_USER` и `DB_PASSWORD`.

Запустите приложение:

```bash
mvn spring-boot:run
```

Тесты запускаются командой `mvn verify` и требуют доступный Docker для Testcontainers.
