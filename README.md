# job4j_github_analysis
Spring Boot приложение, которое выполняет задачи по расписанию, анализируя данные с GitHub. 
Приложение регулярно собирает информацию о репозиториях и коммитах, а затем сохраняет эти данные в базу данных.

# Стек технологий:
Java 19  
PostgreSQL 16.2  
Spring Boot 2.7.6  
Maven 3.9.6  
Liquibase Maven Plugin 4.15.0  

# Требования к окружению
Microsoft Windows 11  
Java 19  
PostgreSQL 16

# Запуск проекта:
Создать локальную копию проекта клонированием из репозитория https://github.com/GitHubfilipich/job4j_github_analysis  
В PostgreSQL создать базу данных и в папке проекта в файле 
"...\src\main\resources\application.properties" указать её адрес (url), имя пользователя (username) и
пароль (password).  
В терминале в папке проекта выполнить скрипты создания БД командой
"mvn liquibase:update -Pproduction".  
Создать исполняемый файл проекта "job4j_cinema-1.0-SNAPSHOT.jar" в папке "target" проекта командой
"mvn clean package -Pproduction -DskipTests".  
Запустить исполняемый файл командой "java -jar target/job4j_github_analysis-1.0-SNAPSHOT.jar".  

# Взаимодействие с приложением:
Взаимодействие с приложением происходит через REST API.

# Контакты
https://github.com/GitHubfilipich