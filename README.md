# RouteWise

## Назначение

Backend-сервис для планирования маршрутов между городами.

## Текущие возможности

- Запуск Spring Boot приложения и проверка состояния через `GET /actuator/health`.
- Создание и просмотр городов через `POST /api/cities` и `GET /api/cities`.
- Создание и просмотр направленных дорог через `POST /api/roads` и `GET /api/roads`.
- Поиск кратчайшего маршрута через `GET /api/routes/shortest`.
- Хранение городов и дорог в PostgreSQL с созданием схемы через Flyway.
- Проверка названий городов и длины дорог.

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
