# Инструкции по развертыванию Construction CRM

## 🚀 Быстрый старт

### 1. Локальная установка

```bash
# Клонирование репозитория
git clone https://github.com/your-username/construction-crm.git
cd construction-crm

# Установка зависимостей
composer install

# Настройка базы данных
mysql -u root -p
CREATE DATABASE construction_crm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;

# Импорт схемы
mysql -u root -p construction_crm < database/schema.sql

# Настройка окружения
cp .env.example .env
# Отредактируйте .env файл с вашими настройками

# Настройка прав доступа
chmod 755 uploads uploads/exports uploads/documents logs
chmod 644 .env

# Запуск веб-сервера
php -S localhost:8000
```

### 2. Развертывание на Netlify

1. **Подключение репозитория**
   - Создайте аккаунт на [Netlify](https://netlify.com)
   - Подключите ваш GitHub репозиторий
   - Выберите ветку для развертывания

2. **Настройка переменных окружения**
   В настройках сайта Netlify добавьте переменные:
   ```
   DB_HOST=your-database-host
   DB_NAME=construction_crm
   DB_USER=your-database-user
   DB_PASS=your-database-password
   APP_URL=https://your-site.netlify.app
   APP_DEBUG=false
   ```

3. **Настройка сборки**
   - Build command: `composer install --no-dev --optimize-autoloader`
   - Publish directory: `.`
   - Node version: `18` (если требуется)

4. **Настройка базы данных**
   Рекомендуется использовать внешний хостинг БД:
   - [PlanetScale](https://planetscale.com) (MySQL)
   - [Railway](https://railway.app) (PostgreSQL)
   - [Supabase](https://supabase.com) (PostgreSQL)

## 🔧 Подробная настройка

### Требования к серверу

- **PHP**: 8.0 или выше
- **MySQL**: 5.7 или выше / MariaDB 10.2 или выше
- **Веб-сервер**: Apache 2.4+ или Nginx 1.18+
- **Расширения PHP**:
  - PDO
  - PDO_MySQL
  - JSON
  - MBString
  - OpenSSL

### Настройка Apache

```apache
<VirtualHost *:80>
    ServerName your-domain.com
    DocumentRoot /var/www/construction-crm
    
    <Directory /var/www/construction-crm>
        AllowOverride All
        Require all granted
    </Directory>
    
    ErrorLog ${APACHE_LOG_DIR}/construction-crm_error.log
    CustomLog ${APACHE_LOG_DIR}/construction-crm_access.log combined
</VirtualHost>
```

### Настройка Nginx

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/construction-crm;
    index index.php;

    location / {
        try_files $uri $uri/ /index.php?$query_string;
    }

    location ~ \.php$ {
        fastcgi_pass unix:/var/run/php/php8.1-fpm.sock;
        fastcgi_index index.php;
        fastcgi_param SCRIPT_FILENAME $realpath_root$fastcgi_script_name;
        include fastcgi_params;
    }

    location ~ /\.ht {
        deny all;
    }
}
```

### Настройка базы данных

1. **Создание базы данных**
```sql
CREATE DATABASE construction_crm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'crm_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON construction_crm.* TO 'crm_user'@'localhost';
FLUSH PRIVILEGES;
```

2. **Импорт схемы**
```bash
mysql -u crm_user -p construction_crm < database/schema.sql
```

### Настройка SSL (HTTPS)

#### Let's Encrypt (Apache)
```bash
sudo apt install certbot python3-certbot-apache
sudo certbot --apache -d your-domain.com
```

#### Let's Encrypt (Nginx)
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

## 🔒 Безопасность

### Настройка файрвола
```bash
# UFW (Ubuntu)
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# iptables (CentOS/RHEL)
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

### Настройка прав доступа
```bash
# Владелец файлов
sudo chown -R www-data:www-data /var/www/construction-crm

# Права доступа
sudo find /var/www/construction-crm -type d -exec chmod 755 {} \;
sudo find /var/www/construction-crm -type f -exec chmod 644 {} \;

# Запись в папки
sudo chmod -R 755 /var/www/construction-crm/uploads
sudo chmod -R 755 /var/www/construction-crm/logs
```

### Защита конфиденциальных файлов
```apache
# .htaccess
<Files ".env">
    Order allow,deny
    Deny from all
</Files>

<Files "composer.json">
    Order allow,deny
    Deny from all
</Files>
```

## 📊 Мониторинг и логирование

### Настройка логирования
```php
// В .env файле
APP_DEBUG=false
LOG_LEVEL=error

// Создание папки для логов
mkdir -p logs
chmod 755 logs
```

### Мониторинг производительности
```bash
# Установка инструментов мониторинга
sudo apt install htop iotop nethogs

# Мониторинг логов
tail -f /var/log/apache2/error.log
tail -f logs/error.log
```

## 🔄 Обновления

### Автоматическое обновление
```bash
#!/bin/bash
# update-crm.sh

cd /var/www/construction-crm
git pull origin main
composer install --no-dev --optimize-autoloader
php artisan migrate
chown -R www-data:www-data .
chmod -R 755 uploads logs
```

### Резервное копирование
```bash
#!/bin/bash
# backup-crm.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/construction-crm"

# Резервная копия базы данных
mysqldump -u crm_user -p construction_crm > $BACKUP_DIR/db_$DATE.sql

# Резервная копия файлов
tar -czf $BACKUP_DIR/files_$DATE.tar.gz /var/www/construction-crm

# Удаление старых резервных копий (старше 30 дней)
find $BACKUP_DIR -name "*.sql" -mtime +30 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete
```

## 🐛 Устранение неполадок

### Частые проблемы

1. **Ошибка подключения к БД**
   - Проверьте настройки в .env
   - Убедитесь, что MySQL запущен
   - Проверьте права доступа пользователя БД

2. **Ошибка 500**
   - Проверьте логи ошибок
   - Убедитесь, что mod_rewrite включен
   - Проверьте права доступа к файлам

3. **Медленная работа**
   - Включите кеширование
   - Оптимизируйте запросы к БД
   - Проверьте настройки PHP

### Команды диагностики
```bash
# Проверка PHP
php -v
php -m | grep -E "(pdo|mysql|json|mbstring)"

# Проверка MySQL
mysql -u crm_user -p -e "SHOW DATABASES;"

# Проверка прав доступа
ls -la /var/www/construction-crm/
ls -la /var/www/construction-crm/uploads/

# Проверка логов
tail -n 50 /var/log/apache2/error.log
tail -n 50 logs/error.log
```

## 📞 Поддержка

### Полезные ссылки
- [Документация PHP](https://www.php.net/docs.php)
- [Документация MySQL](https://dev.mysql.com/doc/)
- [Документация Apache](https://httpd.apache.org/docs/)
- [Документация Nginx](https://nginx.org/en/docs/)

### Контакты
- Email: support@construction-crm.com
- GitHub Issues: [Создать issue](https://github.com/your-username/construction-crm/issues)
- Документация: [docs/](docs/)

---

**Construction CRM** - профессиональное решение для управления строительными проектами