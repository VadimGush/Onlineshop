# Онлайн магазин
Реализация онлайн магазина в рамках стажировки в компании Thumbtack

## Сборка и запуск

### База данных
Логин, пароль пользователя и имя базы данных можно указать ``main/resources/config.properties``.

Для тестирования системы используйте отдельную базу данных и укажите её настройки в 
``test/resources/config-test.properties``.

**Внимание**: база данных должна иметь кодировку UTF-8. Установить данную кодировку
можно использовав команду ниже (MySQL):
```sql
alter database `<database_name>` character set utf8 collate utf8_general_ci
```

### Сервер
Для начала собираем .jar файл:
```bash
gradle jar
```

Затем производим запуск:
```bash
java -jar /build/classes/libs/onlineshop-1.0-SNAPSHOT.jar
```

Сервер будет запущен на localhost по порту 8888 (иной порт можно указать в ``src/resources/config.properties``)

