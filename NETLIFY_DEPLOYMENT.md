# 🚀 Развертывание Construction CRM на Netlify

## Важное примечание

**Netlify** предназначен для развертывания **фронтенд приложений** (статических сайтов). Наш проект состоит из двух частей:

1. **Backend (Python/FastAPI)** - нужно развернуть отдельно (Heroku, Digital Ocean, AWS и т.д.)
2. **Frontend (HTML/CSS/JS)** - развертывается на Netlify

## 📁 Структура для Netlify

Для Netlify используется папка `public/`:
- `public/index.html` - основной интерфейс
- `public/css/style.css` - стили
- `public/js/app.js` - JavaScript логика
- `public/_redirects` - настройки маршрутизации
- `netlify.toml` - конфигурация Netlify

## 🔧 Шаги развертывания

### 1. Подготовка backend

**Сначала разверните backend API** на одной из платформ:

#### Heroku (рекомендуется)
```bash
# Установите Heroku CLI
# Создайте приложение
heroku create your-construction-crm-api

# Добавьте переменные окружения
heroku config:set SECRET_KEY=your-super-secret-key
heroku config:set DB_HOST=your-db-host
heroku config:set DB_USER=your-db-user
heroku config:set DB_PASSWORD=your-db-password
heroku config:set DB_NAME=your-db-name

# Разверните
git push heroku main
```

#### Railway
```bash
# Установите Railway CLI
npm install -g @railway/cli

# Войдите и создайте проект
railway login
railway init
railway up
```

#### Digital Ocean App Platform
1. Подключите GitHub репозиторий
2. Выберите Python/FastAPI
3. Настройте переменные окружения
4. Разверните

### 2. Настройка frontend для работы с backend

Отредактируйте `public/js/app.js`:

```javascript
// Замените localhost на URL вашего backend
this.apiUrl = localStorage.getItem('apiUrl') || 'https://your-api-url.herokuapp.com';
```

Или обновите `netlify.toml`:

```toml
[[redirects]]
  from = "/api/*"
  to = "https://your-construction-crm-api.herokuapp.com/api/:splat"
  status = 200
  force = true
```

### 3. Развертывание на Netlify

#### Метод 1: Drag & Drop
1. Перейдите на [netlify.com](https://netlify.com)
2. Перетащите папку `public` в область развертывания
3. Ваш сайт будет доступен по сгенерированному URL

#### Метод 2: Git деплой (рекомендуется)
1. Загрузите код в GitHub репозиторий
2. Подключите репозиторий к Netlify
3. Настройте:
   - **Publish directory**: `public`
   - **Build command**: оставьте пустым или `echo "Static site ready"`

#### Метод 3: Netlify CLI
```bash
# Установите Netlify CLI
npm install -g netlify-cli

# Войдите в аккаунт
netlify login

# Разверните
netlify deploy --dir=public --prod
```

## ⚙️ Конфигурация после развертывания

### 1. Настройте домен (опционально)
- В панели Netlify перейдите в Domain settings
- Добавьте custom domain
- Настройте DNS записи

### 2. Настройте HTTPS
- Netlify автоматически предоставляет SSL сертификат
- Принудительно включите HTTPS в настройках

### 3. Настройте переменные окружения
В Netlify Dashboard → Site settings → Environment variables:
- `API_URL` = URL вашего backend API

## 🔗 URL структура

После развертывания у вас будет:
- **Frontend**: `https://your-site.netlify.app`
- **Backend**: `https://your-api.herokuapp.com`

## 📋 Чек-лист развертывания

- [ ] Backend развернут и работает
- [ ] База данных настроена и доступна
- [ ] В frontend указан правильный API URL
- [ ] Протестирован вход в систему
- [ ] API документация доступна (`/docs`)
- [ ] CORS настроен для вашего Netlify домена

## 🐛 Возможные проблемы и решения

### CORS ошибки
Добавьте ваш Netlify домен в настройки CORS backend:

```python
# app/main.py
app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://your-site.netlify.app"],  # Ваш Netlify URL
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

### 404 ошибки на маршрутах
Убедитесь, что файл `public/_redirects` содержит:
```
/* /index.html 200
```

### API недоступен
1. Проверьте, что backend запущен
2. Проверьте URL в `app.js`
3. Проверьте настройки Netlify redirects

## 🔄 Автоматическое развертывание

Настройте автоматический деплой:

1. **GitHub Actions** для backend
2. **Netlify** автоматически пересобирает при изменении frontend кода

### GitHub Actions для backend (.github/workflows/deploy.yml):
```yaml
name: Deploy to Heroku
on:
  push:
    branches: [ main ]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - uses: akhileshns/heroku-deploy@v3.12.12
      with:
        heroku_api_key: ${{secrets.HEROKU_API_KEY}}
        heroku_app_name: "your-construction-crm-api"
        heroku_email: "your-email@example.com"
```

## 📊 Мониторинг

После развертывания рекомендуется настроить:

1. **Мониторинг uptime** (UptimeRobot, Pingdom)
2. **Логирование ошибок** (Sentry)
3. **Аналитика** (Google Analytics)

## 💡 Рекомендации по продакшну

1. **Безопасность**:
   - Смените все дефолтные пароли
   - Используйте сильные секретные ключи
   - Настройте HTTPS

2. **Производительность**:
   - Включите CDN для статических файлов
   - Настройте кеширование
   - Оптимизируйте изображения

3. **Резервное копирование**:
   - Настройте автоматические бэкапы БД
   - Используйте версионирование

## 🆘 Поддержка

Если возникли проблемы:

1. Проверьте логи Netlify: Site → Functions → View logs
2. Проверьте логи backend: Heroku → View logs
3. Используйте Developer Tools браузера для отладки
4. Создайте Issue в GitHub репозитории

---

**Успешного развертывания! 🚀**