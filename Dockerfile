FROM ubuntu:latest

# устанавливаем рабочий каталог
WORKDIR var/miska-api/

# устанавливаем все необходимые зависимости
RUN apt-get update && apt-get install -y  \
    wget \
    dpkg \
    vim \
    curl \
    unzip \
    zip \
    iputils-ping

# устанавливаем openjdk и jre для запуска
RUN apt-get install -y openjdk-21-jre

# очищаем ненужный кеш
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

# копируем и распаковываем рахив с приложением
COPY ./build/libs/configs.zip .
RUN unzip configs.zip -d .

# запускаем приложенние
# CMD ["java", "-jar", "Miska-0.0.1.jar"]

# dev команда, чтобы докер не умирал после сборки
CMD ["tail", "-f", "/dev/null"]

# -Dfile.encoding="UTF-8" -Dsun.stdout.encoding="UTF-8" -Dsun.stderr.encoding="UTF-8" доп опции для кодировки
