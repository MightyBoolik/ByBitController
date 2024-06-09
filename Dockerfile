# Используем официальный образ OpenJDK
FROM openjdk:11-jdk-slim

# Создаем рабочую директорию
WORKDIR /app

# Копируем файл pom.xml и исходный код в контейнер
COPY pom.xml .
COPY src ./src

# Скачиваем зависимости и собираем проект
RUN ./mvnw dependency:resolve
RUN ./mvnw package

# Указываем команду для запуска приложения
CMD ["java", "-jar", "target/ByBitController-1.0-SNAPSHOT.jar"]

# Открываем порт
EXPOSE 8080
