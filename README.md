# Construction CRM - Современная CRM система для строительных компаний (Python/FastAPI)

Современная веб-система управления строительными проектами, переписанная на Python с использованием FastAPI, SQLAlchemy и современных технологий. Полный функционал для ведения проектов, составления смет, учета рабочего времени и управления ресурсами.

## 🚀 Возможности системы

### 📊 Дашборд и аналитика
- Динамический дашборд с ключевыми метриками
- Интерактивные графики и диаграммы
- Статистика по проектам, сметам и времени
- Экспорт отчетов в различных форматах

### 🏗️ Управление проектами
- Создание и редактирование проектов
- Назначение менеджеров и исполнителей
- Отслеживание статуса и прогресса
- Управление бюджетом и затратами
- Привязка к клиентам и контрактам

### 📋 Сметы и расчеты
- Создание детальных смет
- Шаблоны смет для типовых работ
- Расчет материалов и трудозатрат
- Учет налогов и наценок
- Экспорт в PDF и Excel

### 👥 Управление персоналом
- Ведение базы сотрудников
- Учет рабочего времени
- Табели и отчеты по времени
- Система одобрения часов
- Роли и права доступа

### 📦 Управление ресурсами
- Складской учет материалов
- Управление оборудованием
- Контроль остатков
- Поставщики и цены
- Списание на проекты

## 🛠️ Технологии

### Backend
- **Python 3.11+** - Серверная логика
- **FastAPI** - Современный веб-фреймворк
- **SQLAlchemy** - ORM для работы с базой данных
- **Alembic** - Миграции базы данных
- **Pydantic** - Валидация данных
- **JWT** - Аутентификация и авторизация
- **MySQL** - База данных

### Дополнительные библиотеки
- **Uvicorn** - ASGI сервер
- **Passlib** - Хеширование паролей
- **Python-jose** - JWT токены
- **Pandas** - Обработка данных
- **ReportLab** - Генерация PDF
- **Pytest** - Тестирование

### Инфраструктура
- **Docker & Docker Compose** - Контейнеризация
- **Nginx** - Reverse proxy и статические файлы
- **Git** - Контроль версий

## 📁 Структура проекта

```
construction-crm/
├── app/                      # Основное приложение
│   ├── core/                # Ядро приложения
│   │   ├── config.py       # Конфигурация
│   │   ├── database.py     # Подключение к БД
│   │   ├── security.py     # Безопасность и JWT
│   │   └── auth.py         # Аутентификация
│   ├── models/             # SQLAlchemy модели
│   │   ├── user.py         # Модель пользователя
│   │   ├── client.py       # Модель клиента
│   │   ├── project.py      # Модель проекта
│   │   ├── material.py     # Модель материала
│   │   ├── estimate.py     # Модель сметы
│   │   └── ...             # Другие модели
│   ├── schemas/            # Pydantic схемы
│   │   ├── auth.py         # Схемы аутентификации
│   │   └── ...             # Другие схемы
│   ├── routes/             # API маршруты
│   │   ├── auth.py         # Аутентификация
│   │   └── ...             # Другие маршруты
│   ├── services/           # Бизнес-логика
│   └── main.py             # Основное приложение FastAPI
├── database/               # SQL схемы и миграции
├── static/                 # Статические файлы
├── tests/                  # Тесты
├── uploads/                # Загруженные файлы
├── migrations/             # Alembic миграции
├── Dockerfile             # Docker конфигурация
├── docker-compose.yml     # Docker Compose
├── requirements.txt       # Python зависимости
├── .env.example          # Пример переменных окружения
└── README.md             # Документация
```

## 🛠️ Установка и настройка

### Требования
- Python 3.11 или выше
- MySQL 8.0 или выше
- Docker и Docker Compose (рекомендуется)
- Git

### Быстрый запуск с Docker

1. **Клонирование репозитория**
   ```bash
   git clone https://github.com/your-username/construction-crm.git
   cd construction-crm
   ```

2. **Запуск с Docker Compose**
   ```bash
   # Скопируйте и настройте переменные окружения
   cp .env.example .env
   
   # Запустите все сервисы
   docker-compose up -d
   ```

3. **Доступ к системе**
   - API: `http://localhost:8000`
   - Документация: `http://localhost:8000/docs`
   - Веб-интерфейс: `http://localhost`

### Локальная разработка

1. **Подготовка окружения**
   ```bash
   # Создайте виртуальное окружение
   python -m venv venv
   source venv/bin/activate  # Linux/Mac
   # или
   venv\Scripts\activate  # Windows
   
   # Установите зависимости
   pip install -r requirements.txt
   ```

2. **Настройка базы данных**
   ```bash
   # Создайте базу данных MySQL
   mysql -u root -p
   CREATE DATABASE construction_crm;
   
   # Импортируйте схему
   mysql -u root -p construction_crm < database/schema.sql
   ```

3. **Конфигурация**
   ```bash
   # Скопируйте файл окружения
   cp .env.example .env
   
   # Отредактируйте .env файл
   nano .env
   ```

4. **Запуск сервера разработки**
   ```bash
   # Используя run.py
   python run.py
   
   # Или напрямую через uvicorn
   uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
   ```

5. **Доступ к системе**
   - API: `http://localhost:8000`
   - Документация: `http://localhost:8000/docs`
   - ReDoc: `http://localhost:8000/redoc`

## 🔧 Конфигурация

### Файл .env
```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=password
DB_NAME=construction_crm

# Application Configuration
SECRET_KEY=your-super-secret-key-change-this-in-production
DEBUG=True
ACCESS_TOKEN_EXPIRE_MINUTES=30

# File Upload Configuration
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=10485760

# Email Configuration (Optional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
```

## 👤 Данные для входа по умолчанию

- **Логин:** `admin`
- **Email:** `admin@construction-crm.com`
- **Пароль:** `password`

⚠️ **Важно:** Обязательно смените пароль администратора после первого входа!

## 🧪 Тестирование

```bash
# Запуск всех тестов
pytest

# Запуск с покрытием
pytest --cov=app

# Запуск конкретного теста
pytest tests/test_auth.py
```

## 🚀 Развертывание

### Продакшн с Docker

1. **Настройте переменные окружения для продакшна**
   ```bash
   cp .env.example .env.prod
   # Отредактируйте .env.prod
   ```

2. **Запустите в продакшн режиме**
   ```bash
   docker-compose -f docker-compose.yml --env-file .env.prod up -d
   ```

### Ручное развертывание

1. **Подготовка сервера**
   ```bash
   # Установите Python, MySQL, Nginx
   sudo apt update
   sudo apt install python3.11 python3.11-venv mysql-server nginx
   ```

2. **Настройка приложения**
   ```bash
   # Создайте пользователя и директорию
   sudo useradd -m crm
   sudo mkdir /opt/construction-crm
   sudo chown crm:crm /opt/construction-crm
   
   # Переключитесь на пользователя crm
   sudo su - crm
   cd /opt/construction-crm
   
   # Клонируйте репозиторий
   git clone https://github.com/your-username/construction-crm.git .
   
   # Создайте виртуальное окружение
   python3.11 -m venv venv
   source venv/bin/activate
   pip install -r requirements.txt
   ```

3. **Настройка systemd**
   ```bash
   sudo nano /etc/systemd/system/construction-crm.service
   ```
   
   ```ini
   [Unit]
   Description=Construction CRM
   After=network.target
   
   [Service]
   Type=exec
   User=crm
   Group=crm
   WorkingDirectory=/opt/construction-crm
   Environment=PATH=/opt/construction-crm/venv/bin
   ExecStart=/opt/construction-crm/venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000
   Restart=always
   
   [Install]
   WantedBy=multi-user.target
   ```

## 📖 API Документация

После запуска приложения доступна интерактивная документация:

- **Swagger UI:** `http://localhost:8000/docs`
- **ReDoc:** `http://localhost:8000/redoc`

### Основные эндпоинты

- `POST /api/v1/auth/login` - Аутентификация
- `GET /api/v1/auth/me` - Информация о текущем пользователе
- `GET /api/v1/projects` - Список проектов
- `POST /api/v1/projects` - Создание проекта
- `GET /api/v1/clients` - Список клиентов
- `GET /health` - Проверка здоровья приложения

## 🤝 Разработка

### Настройка среды разработки

1. **Установите pre-commit хуки**
   ```bash
   pip install pre-commit
   pre-commit install
   ```

2. **Форматирование кода**
   ```bash
   black app/
   isort app/
   ```

3. **Линтинг**
   ```bash
   flake8 app/
   ```

### Миграции базы данных

```bash
# Создание миграции
alembic revision --autogenerate -m "Описание изменений"

# Применение миграций
alembic upgrade head

# Откат миграции
alembic downgrade -1
```

## 📝 Лицензия

Этот проект распространяется под лицензией MIT. См. файл [LICENSE](LICENSE) для подробностей.

## 🆘 Поддержка

Если у вас возникли вопросы или проблемы:

1. Проверьте [документацию](docs/)
2. Создайте [Issue](https://github.com/your-username/construction-crm/issues)
3. Обратитесь к разработчикам

## 🔄 Миграция с PHP версии

Для миграции данных с предыдущей PHP версии:

1. Экспортируйте данные из старой системы
2. Используйте скрипты миграции в папке `migrations/`
3. Следуйте инструкциям в `MIGRATION.md`

---

**Construction CRM** - современное решение для управления строительными проектами! 🏗️