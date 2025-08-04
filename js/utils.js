// Утилиты для BuildCRM

class Utils {
    // Форматирование валюты
    static formatCurrency(amount, currency = 'RUB') {
        return new Intl.NumberFormat('ru-RU', {
            style: 'currency',
            currency: currency
        }).format(amount);
    }

    // Форматирование даты
    static formatDate(date, format = 'short') {
        const d = new Date(date);
        const options = {
            short: { day: '2-digit', month: '2-digit', year: 'numeric' },
            long: { day: '2-digit', month: 'long', year: 'numeric' },
            time: { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }
        };
        return d.toLocaleDateString('ru-RU', options[format]);
    }

    // Форматирование времени
    static formatTime(date) {
        return new Date(date).toLocaleTimeString('ru-RU', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // Генерация уникального ID
    static generateId() {
        return Date.now().toString(36) + Math.random().toString(36).substr(2);
    }

    // Валидация email
    static isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    // Валидация телефона
    static isValidPhone(phone) {
        const re = /^[\+]?[1-9][\d]{0,15}$/;
        return re.test(phone.replace(/\s/g, ''));
    }

    // Очистка строки от лишних пробелов
    static cleanString(str) {
        return str.trim().replace(/\s+/g, ' ');
    }

    // Клонирование объекта
    static deepClone(obj) {
        return JSON.parse(JSON.stringify(obj));
    }

    // Получение случайного элемента из массива
    static randomFromArray(array) {
        return array[Math.floor(Math.random() * array.length)];
    }

    // Генерация случайного цвета
    static randomColor() {
        const colors = [
            '#667eea', '#764ba2', '#f093fb', '#f5576c',
            '#4facfe', '#00f2fe', '#43e97b', '#38f9d7',
            '#fa709a', '#fee140', '#a8edea', '#fed6e3',
            '#ffecd2', '#fcb69f', '#ff9a9e', '#fecfef'
        ];
        return this.randomFromArray(colors);
    }

    // Вычисление процента
    static calculatePercentage(part, total) {
        if (total === 0) return 0;
        return Math.round((part / total) * 100);
    }

    // Форматирование размера файла
    static formatFileSize(bytes) {
        if (bytes === 0) return '0 Б';
        const k = 1024;
        const sizes = ['Б', 'КБ', 'МБ', 'ГБ'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    // Получение инициалов
    static getInitials(name) {
        return name
            .split(' ')
            .map(word => word.charAt(0))
            .join('')
            .toUpperCase()
            .slice(0, 2);
    }

    // Проверка на пустой объект
    static isEmptyObject(obj) {
        return Object.keys(obj).length === 0;
    }

    // Дебаунс функция
    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // Троттлинг функция
    static throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    }

    // Локальное хранилище
    static storage = {
        set: (key, value) => {
            try {
                localStorage.setItem(key, JSON.stringify(value));
            } catch (e) {
                console.error('Ошибка сохранения в localStorage:', e);
            }
        },
        get: (key, defaultValue = null) => {
            try {
                const item = localStorage.getItem(key);
                return item ? JSON.parse(item) : defaultValue;
            } catch (e) {
                console.error('Ошибка чтения из localStorage:', e);
                return defaultValue;
            }
        },
        remove: (key) => {
            try {
                localStorage.removeItem(key);
            } catch (e) {
                console.error('Ошибка удаления из localStorage:', e);
            }
        },
        clear: () => {
            try {
                localStorage.clear();
            } catch (e) {
                console.error('Ошибка очистки localStorage:', e);
            }
        }
    };

    // API запросы
    static async apiRequest(url, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        const config = { ...defaultOptions, ...options };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    }

    // Экспорт в CSV
    static exportToCSV(data, filename = 'export.csv') {
        if (!data || data.length === 0) return;

        const headers = Object.keys(data[0]);
        const csvContent = [
            headers.join(','),
            ...data.map(row => 
                headers.map(header => {
                    const cell = row[header] || '';
                    return `"${cell.toString().replace(/"/g, '""')}"`;
                }).join(',')
            )
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        
        if (link.download !== undefined) {
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', filename);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    }

    // Экспорт в Excel (простой формат)
    static exportToExcel(data, filename = 'export.xlsx') {
        // Простая реализация - можно заменить на библиотеку
        this.exportToCSV(data, filename.replace('.xlsx', '.csv'));
    }

    // Печать элемента
    static printElement(elementId) {
        const element = document.getElementById(elementId);
        if (!element) return;

        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <html>
                <head>
                    <title>Печать</title>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        @media print {
                            body { margin: 0; }
                        }
                    </style>
                </head>
                <body>
                    ${element.outerHTML}
                </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.print();
    }

    // Копирование в буфер обмена
    static async copyToClipboard(text) {
        try {
            await navigator.clipboard.writeText(text);
            return true;
        } catch (err) {
            console.error('Ошибка копирования в буфер обмена:', err);
            return false;
        }
    }

    // Загрузка файла
    static loadFile(input, callback) {
        const file = input.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (e) => callback(e.target.result, file);
        reader.readAsText(file);
    }

    // Скачивание файла
    static downloadFile(content, filename, type = 'text/plain') {
        const blob = new Blob([content], { type });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }

    // Получение параметров URL
    static getUrlParams() {
        const params = new URLSearchParams(window.location.search);
        const result = {};
        for (const [key, value] of params) {
            result[key] = value;
        }
        return result;
    }

    // Установка параметров URL
    static setUrlParams(params) {
        const url = new URL(window.location);
        Object.keys(params).forEach(key => {
            url.searchParams.set(key, params[key]);
        });
        window.history.pushState({}, '', url);
    }

    // Проверка поддержки функций браузера
    static browserSupport = {
        localStorage: (() => {
            try {
                localStorage.setItem('test', 'test');
                localStorage.removeItem('test');
                return true;
            } catch (e) {
                return false;
            }
        })(),
        clipboard: navigator.clipboard !== undefined,
        fileReader: window.FileReader !== undefined,
        webWorkers: window.Worker !== undefined
    };
}

// Глобальные константы
const CONSTANTS = {
    STATUSES: {
        ACTIVE: 'active',
        PENDING: 'pending',
        COMPLETED: 'completed',
        CANCELLED: 'cancelled'
    },
    PROJECT_TYPES: {
        RESIDENTIAL: 'residential',
        COMMERCIAL: 'commercial',
        INDUSTRIAL: 'industrial',
        INFRASTRUCTURE: 'infrastructure'
    },
    EMPLOYEE_ROLES: {
        MANAGER: 'manager',
        ENGINEER: 'engineer',
        WORKER: 'worker',
        DRIVER: 'driver',
        ACCOUNTANT: 'accountant'
    },
    CURRENCIES: {
        RUB: 'RUB',
        USD: 'USD',
        EUR: 'EUR'
    }
};

// Экспорт для использования в других модулях
window.Utils = Utils;
window.CONSTANTS = CONSTANTS;