# Construction CRM System

Современная CRM система для строительных компаний, разработанная на Java с использованием Spring Boot. Система предоставляет полный функционал для управления проектами, сотрудниками, сметами и табелями рабочего времени.

## 🚀 Возможности системы

### 👥 Управление персоналом
- Полное управление сотрудниками с ролями и правами доступа
- Табели рабочего времени с детальным учетом
- Отслеживание активности и производительности
- Система ролей: Администратор, Менеджер, Инженер, Рабочий, Бухгалтер, Клиент

### 🏗️ Управление проектами
- Создание и управление строительными проектами
- Отслеживание прогресса и статуса проектов
- Планирование задач и назначение исполнителей
- Финансовый контроль и анализ рентабельности
- Типы проектов: Жилые, Коммерческие, Промышленные, Инфраструктурные, Реконструкция

### 📊 Сметы и финансы
- Создание детальных смет с материалами и работами
- Автоматический расчет стоимости и НДС
- Управление поставщиками и ценами
- Экспорт смет в Excel и PDF
- Контроль бюджета и фактических затрат

### 📈 Аналитика и отчеты
- Дашборд с ключевыми метриками
- Анализ производительности проектов
- Отчеты по сотрудникам и времени работы
- Финансовая аналитика и прогнозирование
- Интеграция с внешними системами

### 🔐 Безопасность
- JWT аутентификация
- Ролевая система доступа
- Шифрование паролей
- Аудит действий пользователей

## 🛠️ Технологический стек

### Backend
- **Java 17** - основной язык разработки
- **Spring Boot 3.2.0** - фреймворк приложения
- **Spring Security** - безопасность и аутентификация
- **Spring Data JPA** - работа с базой данных
- **H2 Database** - встроенная база данных (для разработки)
- **PostgreSQL** - продакшн база данных
- **JWT** - токены аутентификации
- **Maven** - управление зависимостями

### Frontend (планируется)
- **React** - пользовательский интерфейс
- **TypeScript** - типизированный JavaScript
- **Material-UI** - компоненты интерфейса
- **Redux** - управление состоянием
- **Chart.js** - графики и диаграммы

## 📋 Требования

- Java 17 или выше
- Maven 3.6+
- PostgreSQL (для продакшена)
- Node.js 16+ (для frontend)

## 🚀 Быстрый старт

### 1. Клонирование репозитория
```bash
git clone <repository-url>
cd construction-crm
```

### 2. Запуск приложения
```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: http://localhost:8080

### 3. Доступ к H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## 👤 Тестовые пользователи

Система автоматически создает тестовых пользователей при первом запуске:

| Логин | Пароль | Роль | Описание |
|-------|--------|------|----------|
| admin | admin123 | Администратор | Полный доступ к системе |
| manager | manager123 | Менеджер | Управление проектами |
| engineer | engineer123 | Инженер | Технические задачи |
| worker | worker123 | Рабочий | Базовый доступ |
| accountant | accountant123 | Бухгалтер | Финансовые операции |

## 🧪 Тестирование API

### Простой тест
```bash
# Проверка здоровья приложения
curl http://localhost:8080/api/actuator/health

# Тестовый endpoint
curl http://localhost:8080/api/test/hello
```

### Полный тест API
```bash
# Запуск тестового скрипта
chmod +x test-api.sh
./test-api.sh
```

## 📚 API Документация

### Аутентификация
```
POST /api/auth/login
POST /api/auth/register
```

### Пользователи
```
GET    /api/users                    - Список пользователей
GET    /api/users/{id}              - Получить пользователя
POST   /api/users                   - Создать пользователя
PUT    /api/users/{id}              - Обновить пользователя
DELETE /api/users/{id}              - Удалить пользователя
```

### Проекты
```
GET    /api/projects                - Список проектов
GET    /api/projects/{id}           - Получить проект
POST   /api/projects                - Создать проект
PUT    /api/projects/{id}           - Обновить проект
DELETE /api/projects/{id}           - Удалить проект
```

### Сметы
```
GET    /api/estimates               - Список смет
GET    /api/estimates/{id}          - Получить смету
POST   /api/estimates               - Создать смету
PUT    /api/estimates/{id}          - Обновить смету
DELETE /api/estimates/{id}          - Удалить смету
```

### Табели
```
GET    /api/timesheets              - Список табелей
GET    /api/timesheets/{id}         - Получить табель
POST   /api/timesheets              - Создать табель
PUT    /api/timesheets/{id}         - Обновить табель
DELETE /api/timesheets/{id}         - Удалить табель
```

## 🏗️ Архитектура

### Структура проекта
```
src/main/java/com/construction/crm/
├── config/          # Конфигурации
├── controller/      # REST контроллеры
├── entity/          # JPA сущности
├── repository/      # Репозитории данных
├── service/         # Бизнес-логика
├── security/        # Безопасность
└── util/           # Утилиты
```

### Основные сущности
- **User** - пользователи системы
- **Role** - роли и права доступа
- **Project** - строительные проекты
- **Estimate** - сметы
- **EstimateItem** - элементы смет
- **TimeSheet** - табели рабочего времени
- **ProjectTask** - задачи проектов
- **ProjectDocument** - документы проектов
- **ProjectProgress** - прогресс проектов
- **EstimateDocument** - документы смет

## 🔧 Конфигурация

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

jwt:
  secret: your-secret-key-here
  expiration: 86400000
```

## 📊 Мониторинг

### Actuator endpoints
- `/api/actuator/health` - состояние приложения
- `/api/actuator/info` - информация о приложении
- `/api/actuator/metrics` - метрики системы

## 🚀 Развертывание

### Docker (планируется)
```bash
docker build -t construction-crm .
docker run -p 8080:8080 construction-crm
```

### Продакшн
1. Настройте PostgreSQL
2. Обновите `application.yml`
3. Запустите с профилем `prod`
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

## 📞 Поддержка

Для вопросов и поддержки:
- Email: support@construction-crm.com
- Issues: GitHub Issues
- Документация: Wiki проекта

## 🎯 Roadmap

### Версия 1.1
- [ ] React frontend
- [ ] Мобильное приложение
- [ ] Интеграция с 1С
- [ ] API для внешних систем

### Версия 1.2
- [ ] ИИ для прогнозирования
- [ ] Интеграция с BIM
- [ ] Модуль закупок
- [ ] Система уведомлений

### Версия 2.0
- [ ] Микросервисная архитектура
- [ ] Kubernetes развертывание
- [ ] Масштабируемость
- [ ] Интеграция с IoT

## 🎉 Статус проекта

✅ **Backend API** - Завершен
✅ **База данных** - Настроена
✅ **Аутентификация** - Реализована
✅ **Безопасность** - Настроена
✅ **Документация** - Создана

🔄 **Frontend** - В разработке
🔄 **Тесты** - В разработке
🔄 **Деплой** - В разработке

---

**Construction CRM System** - современное решение для строительных компаний, обеспечивающее полный контроль над проектами, персоналом и финансами.

## 🚀 Быстрый старт для разработчиков

1. **Клонируйте репозиторий**
```bash
git clone <repository-url>
cd construction-crm
```

2. **Запустите приложение**
```bash
mvn spring-boot:run
```

3. **Проверьте работу**
```bash
curl http://localhost:8080/api/test/hello
```

4. **Откройте в браузере**
- Приложение: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

Система готова к использованию! 🎉