
# На основе OpenJDK
FROM openjdk:8-jdk-alpine

# Создаём образ для хранения данных Spring'a
VOLUME /tmp

# Копируем приложение
COPY build/libs/onlineshop-1.0-SNAPSHOT.jar app.jar

# Запускаем с production конфигами
ENTRYPOINT [
    "java", "-jar", "/app.jar",
    "-Dspring.config.location=classpath:config-production.properties,classpath:application-production.properties"
]

