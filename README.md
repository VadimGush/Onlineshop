# Онлайн магазин
Реализация онлайн магазина в рамках стажировки в компании Thumbtack

## Сборка и запуск
Для начала собираем .jar файл:
```bash
gradle jar
```

Затем производим запуск:
```bash
java -jar /build/classes/libs/onlineshop-1.0-SNAPSHOT.jar
```

Сервер будет запущен на localhost по порту 8080

## Состояние
Список поддерживаемых методов

| Method | URI | Status |
| ------ | --- | ------ |
| POST | /api/admins       | no |
| POST | /api/clients      | no |
| POST | /api/sessions     | no |
| DELETE | /api/sessions   | no |
| GET | /api/accounts      | no |
| GET | /api/clients       | no |
| PUT | /api/admins        | no |
| PUT | /api/clients       | no |
| POST | /api/categories   | no |
| GET | /api/categories/   | no |
| PUT | /api/categories/   | no |
| DELETE | /api/categories/ | no |
| GET | /api/categories     | no |
| POST | /api/products      | no |
| PUT | /api/products/      | no |
| DELETE | /api/products/   | no |
| GET | /api/products/      | no |
| GET | /api/products       | no |
| PUT | /api/deposits       | no |
| GET | /api/deposits       | no |
| POST | /api/purchases     | no |
| POST | /api/baskets       | no |
| DELETE | /api/baskets/    | no |
| PUT | /api/baskets        | no |
| GET | /api/baskets        | no |
| POST | /api/purchases/baskets     | no |
| GET | /api/purchases/     | no |
| GET | /api/settings     | almost |
| POST | /api/debug/clear   | no |