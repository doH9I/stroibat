<?php
/**
 * Скрипт проверки и исправления ошибок в Construction CRM
 */

echo "🔍 Проверка Construction CRM на наличие ошибок...\n\n";

$errors = [];
$warnings = [];
$fixes = [];

// 1. Проверка PHP синтаксиса
echo "1. Проверка PHP синтаксиса...\n";
$phpFiles = glob('*.php');
$phpFiles = array_merge($phpFiles, glob('src/*.php'));
$phpFiles = array_merge($phpFiles, glob('config/*.php'));
$phpFiles = array_merge($phpFiles, glob('api/**/*.php'));

foreach ($phpFiles as $file) {
    $output = [];
    $returnCode = 0;
    exec("php -l $file 2>&1", $output, $returnCode);
    
    if ($returnCode !== 0) {
        $errors[] = "Синтаксическая ошибка в $file: " . implode("\n", $output);
    }
}

// 2. Проверка обязательных файлов
echo "2. Проверка обязательных файлов...\n";
$requiredFiles = [
    'composer.json',
    '.env.example',
    'config/database.php',
    'src/Auth.php',
    'src/Project.php',
    'src/Estimate.php',
    'src/TimeTracking.php',
    'src/Client.php',
    'src/Material.php',
    'index.php',
    'login.php',
    'logout.php',
    'projects.php',
    'includes/header.php',
    'includes/sidebar.php',
    'assets/css/style.css',
    'assets/js/dashboard.js',
    'database/schema.sql',
    'api/dashboard/projects-stats.php',
    'api/dashboard/hours-stats.php',
    'api/dashboard/stats.php',
    'netlify.toml',
    '.htaccess',
    'README.md'
];

foreach ($requiredFiles as $file) {
    if (!file_exists($file)) {
        $errors[] = "Отсутствует обязательный файл: $file";
    }
}

// 3. Проверка структуры директорий
echo "3. Проверка структуры директорий...\n";
$requiredDirs = [
    'src',
    'config',
    'includes',
    'assets/css',
    'assets/js',
    'api/dashboard',
    'database',
    'uploads',
    'uploads/exports',
    'uploads/documents',
    'logs'
];

foreach ($requiredDirs as $dir) {
    if (!is_dir($dir)) {
        $warnings[] = "Отсутствует директория: $dir";
        // Создаем директорию
        if (mkdir($dir, 0755, true)) {
            $fixes[] = "Создана директория: $dir";
        } else {
            $errors[] = "Не удалось создать директорию: $dir";
        }
    }
}

// 4. Проверка прав доступа
echo "4. Проверка прав доступа...\n";
$writableDirs = [
    'uploads',
    'uploads/exports',
    'uploads/documents',
    'logs'
];

foreach ($writableDirs as $dir) {
    if (is_dir($dir) && !is_writable($dir)) {
        $warnings[] = "Директория $dir не доступна для записи";
        if (chmod($dir, 0755)) {
            $fixes[] = "Исправлены права доступа для: $dir";
        } else {
            $errors[] = "Не удалось исправить права доступа для: $dir";
        }
    }
}

// 5. Проверка конфигурации базы данных
echo "5. Проверка конфигурации базы данных...\n";
if (file_exists('config/database.php')) {
    try {
        require_once 'config/database.php';
        $db = Database::getInstance();
        $testQuery = $db->fetch("SELECT 1 as test");
        if ($testQuery && $testQuery['test'] == 1) {
            echo "✓ Подключение к базе данных успешно\n";
        } else {
            $errors[] = "Ошибка подключения к базе данных";
        }
    } catch (Exception $e) {
        $errors[] = "Ошибка подключения к базе данных: " . $e->getMessage();
    }
}

// 6. Проверка composer.json
echo "6. Проверка composer.json...\n";
if (file_exists('composer.json')) {
    $composer = json_decode(file_get_contents('composer.json'), true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        $errors[] = "Ошибка в composer.json: " . json_last_error_msg();
    } else {
        if (!isset($composer['require']['php'])) {
            $warnings[] = "В composer.json не указана версия PHP";
        }
        if (!isset($composer['autoload'])) {
            $warnings[] = "В composer.json не настроен автозагрузчик";
        }
    }
}

// 7. Проверка .env файла
echo "7. Проверка .env файла...\n";
if (!file_exists('.env') && file_exists('.env.example')) {
    $warnings[] = "Файл .env не найден, но есть .env.example";
    if (copy('.env.example', '.env')) {
        $fixes[] = "Создан файл .env из .env.example";
    } else {
        $errors[] = "Не удалось создать .env файл";
    }
}

// 8. Проверка безопасности
echo "8. Проверка безопасности...\n";
$sensitiveFiles = [
    '.env',
    'composer.json',
    'composer.lock'
];

foreach ($sensitiveFiles as $file) {
    if (file_exists($file)) {
        $perms = fileperms($file);
        if (($perms & 0x0177) !== 0) {
            $warnings[] = "Файл $file имеет слишком открытые права доступа";
            if (chmod($file, 0644)) {
                $fixes[] = "Исправлены права доступа для: $file";
            }
        }
    }
}

// 9. Проверка CSS и JS файлов
echo "9. Проверка CSS и JS файлов...\n";
if (!file_exists('assets/css/style.css')) {
    $errors[] = "Отсутствует файл стилей: assets/css/style.css";
}

if (!file_exists('assets/js/dashboard.js')) {
    $errors[] = "Отсутствует файл JavaScript: assets/js/dashboard.js";
}

// 10. Проверка API endpoints
echo "10. Проверка API endpoints...\n";
$apiFiles = [
    'api/dashboard/projects-stats.php',
    'api/dashboard/hours-stats.php',
    'api/dashboard/stats.php'
];

foreach ($apiFiles as $file) {
    if (!file_exists($file)) {
        $errors[] = "Отсутствует API файл: $file";
    }
}

// Вывод результатов
echo "\n" . str_repeat("=", 50) . "\n";
echo "РЕЗУЛЬТАТЫ ПРОВЕРКИ\n";
echo str_repeat("=", 50) . "\n\n";

if (empty($errors) && empty($warnings)) {
    echo "✅ Все проверки пройдены успешно! Проект готов к развертыванию.\n\n";
} else {
    if (!empty($errors)) {
        echo "❌ КРИТИЧЕСКИЕ ОШИБКИ:\n";
        foreach ($errors as $error) {
            echo "  - $error\n";
        }
        echo "\n";
    }
    
    if (!empty($warnings)) {
        echo "⚠️  ПРЕДУПРЕЖДЕНИЯ:\n";
        foreach ($warnings as $warning) {
            echo "  - $warning\n";
        }
        echo "\n";
    }
    
    if (!empty($fixes)) {
        echo "🔧 ИСПРАВЛЕНИЯ:\n";
        foreach ($fixes as $fix) {
            echo "  - $fix\n";
        }
        echo "\n";
    }
}

// Рекомендации
echo "📋 РЕКОМЕНДАЦИИ:\n";
echo "1. Убедитесь, что база данных создана и настроена\n";
echo "2. Проверьте настройки в файле .env\n";
echo "3. Установите зависимости: composer install\n";
echo "4. Импортируйте схему БД: mysql -u root -p construction_crm < database/schema.sql\n";
echo "5. Настройте веб-сервер (Apache/Nginx)\n";
echo "6. Проверьте права доступа к папкам uploads/ и logs/\n\n";

// Проверка готовности к Netlify
echo "🚀 ГОТОВНОСТЬ К NETLIFY:\n";
$netlifyReady = true;

if (!file_exists('netlify.toml')) {
    echo "❌ Отсутствует netlify.toml\n";
    $netlifyReady = false;
}

if (!file_exists('.htaccess')) {
    echo "❌ Отсутствует .htaccess\n";
    $netlifyReady = false;
}

if (!file_exists('composer.json')) {
    echo "❌ Отсутствует composer.json\n";
    $netlifyReady = false;
}

if ($netlifyReady) {
    echo "✅ Проект готов к развертыванию на Netlify\n";
} else {
    echo "❌ Проект требует доработки для развертывания на Netlify\n";
}

echo "\n" . str_repeat("=", 50) . "\n";
echo "Проверка завершена!\n";
?>