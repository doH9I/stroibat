# ✅ Чек-лист развертывания Construction CRM

## 📋 Перед развертыванием

### Файлы конфигурации
- [x] `netlify.toml` - настройки Netlify
- [x] `package.json` - Node.js конфигурация
- [x] `.netlifyignore` - игнорирование Python файлов
- [x] `build.sh` - скрипт сборки

### Структура frontend
- [x] `public/index.html` - основной файл
- [x] `public/css/style.css` - стили
- [x] `public/js/app.js` - JavaScript логика
- [x] `public/_redirects` - маршрутизация SPA
- [x] `public/robots.txt` - SEO настройки

### Проверка кода
- [x] HTML валидный
- [x] CSS корректный
- [x] JavaScript без синтаксических ошибок
- [x] Все ссылки на CDN работают

## 🚀 Развертывание на Netlify

### Метод 1: Git Deploy
1. [ ] Загрузить код в GitHub
2. [ ] Подключить репозиторий к Netlify
3. [ ] Настроить Build settings:
   - Base directory: (пустое)
   - Build command: `./build.sh`
   - Publish directory: `public`
4. [ ] Нажать "Deploy site"

### Метод 2: Drag & Drop
1. [ ] Архивировать папку `public`
2. [ ] Перетащить на netlify.com
3. [ ] Дождаться развертывания

## ✅ После развертывания

### Проверка сайта
- [ ] Сайт открывается по URL
- [ ] CSS загружается корректно
- [ ] JavaScript работает
- [ ] Форма входа отображается
- [ ] Нет ошибок в консоли браузера
- [ ] Адаптивный дизайн работает

### Настройка домена (опционально)
- [ ] Добавить custom domain
- [ ] Настроить DNS записи
- [ ] Принудительно включить HTTPS

## ⚙️ Настройка Backend (отдельно)

### Развертывание API
- [ ] Выбрать платформу (Heroku/Railway/DigitalOcean)
- [ ] Развернуть Python/FastAPI backend
- [ ] Настроить базу данных
- [ ] Проверить работу API endpoints

### Подключение Frontend к Backend
- [ ] Получить URL развернутого API
- [ ] Обновить в `public/js/app.js`:
   ```javascript
   this.apiUrl = 'https://your-api-url.herokuapp.com';
   ```
- [ ] Настроить CORS в backend для Netlify домена
- [ ] Протестировать связь frontend-backend

## 🔧 Troubleshooting

### Если ошибка сборки
- [ ] Проверить, что `build.sh` исполняемый
- [ ] Убедиться, что все файлы в `public/` существуют
- [ ] Проверить синтаксис `netlify.toml`

### Если 404 ошибки
- [ ] Проверить наличие `public/_redirects`
- [ ] Убедиться, что `index.html` в корне `public/`

### Если проблемы с API
- [ ] Проверить, что backend развернут
- [ ] Проверить CORS настройки
- [ ] Использовать Developer Tools для отладки

## 📊 Финальная проверка

- [ ] ✅ Frontend успешно развернут на Netlify
- [ ] ✅ Интерфейс работает корректно  
- [ ] ✅ Backend развернут отдельно (если нужен)
- [ ] ✅ API подключен к frontend
- [ ] ✅ Тестовый вход в систему работает
- [ ] ✅ Документация обновлена

## 🎉 Готово к использованию!

После выполнения всех пунктов система готова к продакшн использованию.

### Доступ к системе
- **Frontend**: `https://your-site.netlify.app`
- **Backend**: `https://your-api.herokuapp.com`
- **API Docs**: `https://your-api.herokuapp.com/docs`

### Данные для входа
- **Логин**: admin
- **Пароль**: password

⚠️ **Не забудьте сменить пароль администратора!**