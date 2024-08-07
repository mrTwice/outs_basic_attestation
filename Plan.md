# План работы по созданию веб-сервера и контейнера сервлетов

## Функционал веб-сервера

### 1. Написать простой веб-сервер на ServerSocket
- [x] **Создать базовый веб-сервер**
  - [x] Создать класс `WebServer`, который будет слушать на определенном порту и принимать соединения.
  - [x] Реализовать метод `start()`, который будет открывать `ServerSocket` и ожидать подключения клиентов.
- [x] **Добавить чтение настроек из файла**
  - [ ] Использовать библиотеку для работы с YAML файлами, например, SnakeYAML.
  - [x] Написать метод `loadConfiguration()`, который будет читать настройки из файла.
  - [x] Пример настройки: порт сервера.
- [x] **Добавить установку порта из VMoptions**
  - [x] Реализовать возможность установки порта через параметры запуска JVM, например, `-Dport=8080`.
  - [x] В методе `start()`, если параметр передан, использовать его для установки порта.

### 2. Добавить многопоточную обработку запросов
- [x] **Реализовать многопоточность**
  - [x] Использовать пул потоков (например, `Executors.newFixedThreadPool()`) для обработки входящих соединений.
  - [x] Создать класс `ConnectionHandler`, который будет реализовывать `Runnable` и обрабатывать запросы.

### 3. Реализовать простой парсер HTTP
- [x] **Создать класс `HttpParser`**
  - [ ] Написать метод `parse(InputStream input)` для чтения данных из `InputStream` и создания объекта `HttpRequest`.
  - [x] Реализовать парсинг первой строки запроса (метод, путь, версия).
  - [x] Реализовать парсинг заголовков и тела запроса.
- [x] **Добавить использование ThreadLocal для парсинга запроса**
  - [x] Использовать `ThreadLocal` для хранения текущего парсера запроса в потоке, чтобы избежать проблем с многопоточностью.

### 4. Реализовать классы для работы с HTTP
- [x] **Создать класс `HttpRequest`**
  - [x] Создать поля для хранения метода (GET, POST и т.д.), пути, версии, заголовков и тела запроса.
  - [x] Написать геттеры и сеттеры для всех полей.
- [x] **Создать класс `HttpResponse`**
  - [x] Создать поля для хранения статуса (200, 404 и т.д.), заголовков и тела ответа.
  - [x] Написать методы для формирования строки HTTP-ответа, например, `toString()` для преобразования ответа в строку.
  - [x] Реализовать методы для добавления заголовков и установки тела ответа.

### 5. Передать поступивший запрос в контейнер сервлетов
- [x] **Интегрировать контейнер сервлетов**
  - [x] Создать интерфейс `RequestHandler` и определить метод `HttpResponse execute(HttpRequest request);` который будет реализовываться контейнером сервлетов.

## Функционал контейнера сервлетов

### 6. Реализовать функционал контейнера сервлетов
- [ ] **Описать базовые классы**
  - [ ] Создать базовые классы для контейнера сервлетов, такие как `Servlet`, `HttpServlet`, `ServletRequest`, `ServletResponse`.
  - [ ] Реализовать интерфейс `Servlet`, который будет иметь методы `init()`, `service()`, `destroy()`.
  - [x] Создать абстрактный класс `HttpServlet`, который будет реализовывать интерфейс `Servlet` и добавлять методы для обработки HTTP-методов (doGet, doPost и т.д.).
- [ ] **Добавить ServletContext**
  - [x] Реализовать класс `ServletContext` для хранения общих данных, доступных всем сервлетам (например, инициализационные параметры).
  - [ ] Реализовать методы для добавления и получения атрибутов.
- [x] **Реализовать автоматическую регистрацию сервлетов в контейнере**
  - [x] Создать класс `ServletDispatcher`, который будет хранить карту сопоставления путей запросов с сервлетами.
  - [x] Написать метод `public static Map<String, Method> scanAndRegisterServlets(String packageName) throws Exception` для регистрации сервлетов.
  - [x]  Добавить механизм для автоматической регистрации сервлетов (например, через чтение конфигурационного файла или сканирование пакетов).
  - [x] Реализовать автоматическую регистрацию методов сервлетов как доступных путей.
- [x] **Добавить обработку поступивших запросов**
  - [x] В классе `ServletDispatcher` реализовать метод `public HttpResponse execute(HttpRequest request)`.
  - [x] В методе `execute` сопоставлять путь запроса с зарегистрированными сервлетами и вызывать соответствующий метод сервлета (doGet, doPost и т.д.).
  - [x] Обрабатывать случаи, когда запрашиваемый путь не зарегистрирован (возвращать статус 404).

## Дополнительные шаги

### 7. Централизованное логирование и обработка ошибок
- **Создать интерфейс `InvocationHandler`**
  - Реализовать класс `ServletInvocationHandler` для перехвата вызовов методов сервлетов.
  - Написать логирование начала и окончания выполнения методов.
  - Написать обработку и логирование ошибок.
- **Создать класс `ProxyFactory`**
  - Написать метод для создания прокси-объектов для сервлетов.
- **Обновить `ServletContainer` для использования прокси**
  - Обернуть сервлеты в прокси при регистрации.

### 8. Документация и комментарии
- **Создать документацию**
  - Написать документацию для всех классов и методов.
  - Добавить комментарии в коде для упрощения понимания и поддержки кода в будущем.

### 9. Тестирование и отладка
- **Провести тестирование и отладку**
  - Написать тестовые сервлеты и проверить работу сервера.
  - Написать логирование и обработку ошибок для проверки корректности работы прокси.
  - Провести тестирование многопоточности и корректной обработки запросов.

## Пример использования: Создание сервиса для работы с пользователями по REST

### 10. Создать сервис для работы с пользователями
- **Создать базовый сервлет**
  - Создать класс `UserServlet`, наследующий `HttpServlet`.
  - Реализовать методы doGet, doPost, doPut и doDelete для обработки соответствующих HTTP-запросов.
- **Реализовать методы сервлета**
  - **Get**
    - Написать код для получения списка пользователей или конкретного пользователя по ID.
  - **Post**
    - Написать код для создания нового пользователя.
  - **Put**
    - Написать код для обновления существующего пользователя.
  - **Delete**
    - Написать код для удаления пользователя.
- **Регистрация сервлета**
  - Добавить регистрацию `UserServlet` в контейнер сервлетов.
  - Обеспечить, чтобы методы сервлета автоматически регистрировались как доступные пути для запросов.

        