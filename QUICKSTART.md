# 🚀 Быстрый старт Construction CRM

## Запуск с Docker (Рекомендуется)

1. **Клонирование и настройка**
   ```bash
   git clone https://github.com/your-username/construction-crm.git
   cd construction-crm
   cp .env.example .env
   ```

2. **Запуск**
   ```bash
   docker-compose up -d
   ```

3. **Доступ к системе**
   - API: http://localhost:8000
   - Документация: http://localhost:8000/docs
   - Веб-интерфейс: http://localhost

## Локальный запуск

1. **Подготовка**
   ```bash
   python -m venv venv
   source venv/bin/activate  # Linux/Mac
   # или venv\Scripts\activate для Windows
   pip install -r requirements.txt
   ```

2. **Настройка БД**
   ```bash
   # Создайте базу данных MySQL
   mysql -u root -p -e "CREATE DATABASE construction_crm;"
   
   # Настройте .env файл
   cp .env.example .env
   # Отредактируйте настройки БД в .env
   ```

3. **Инициализация**
   ```bash
   python init_db.py
   ```

4. **Запуск**
   ```bash
   python run.py
   ```

## Первый вход

- **URL:** http://localhost:8000/docs
- **Логин:** admin
- **Пароль:** password

⚠️ **Обязательно смените пароль после первого входа!**

## Проверка работы

```bash
# Проверка здоровья системы
curl http://localhost:8000/health

# Тестирование API
curl -X POST "http://localhost:8000/api/v1/auth/login" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "username=admin&password=password"
```

## Что дальше?

1. Изучите [API документацию](http://localhost:8000/docs)
2. Прочитайте полный [README.md](README.md)
3. Настройте параметры компании
4. Добавьте пользователей и клиентов
5. Создайте первый проект

## Возникли проблемы?

- Проверьте логи: `docker-compose logs` или файлы в папке `logs/`
- Убедитесь, что MySQL запущен и доступен
- Проверьте настройки в `.env` файле
- Создайте [Issue](https://github.com/your-username/construction-crm/issues) если проблема не решается

---
Happy coding! 🏗️