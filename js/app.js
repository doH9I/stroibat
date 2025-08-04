// Основное приложение BuildCRM

class App {
    constructor() {
        this.currentSection = 'dashboard';
        this.modules = {};
        this.init();
    }

    init() {
        this.bindEvents();
        this.loadInitialSection();
        this.initializeModules();
    }

    bindEvents() {
        // Навигация
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const section = e.target.closest('.nav-link').dataset.section;
                this.navigateToSection(section);
            });
        });

        // Переключение боковой панели
        const sidebarToggle = document.getElementById('sidebar-toggle');
        if (sidebarToggle) {
            sidebarToggle.addEventListener('click', () => this.toggleSidebar());
        }

        // Глобальный поиск
        const globalSearch = document.getElementById('global-search');
        if (globalSearch) {
            globalSearch.addEventListener('input', Utils.debounce((e) => {
                this.handleGlobalSearch(e.target.value);
            }, 300));
        }

        // Модальные окна
        this.bindModalEvents();

        // Обработка URL
        window.addEventListener('popstate', (e) => {
            this.handleUrlChange();
        });
    }

    bindModalEvents() {
        const modalContainer = document.getElementById('modal-container');
        const modalOverlay = modalContainer?.querySelector('.modal-overlay');
        const modalClose = modalContainer?.querySelector('.modal-close');

        if (modalOverlay) {
            modalOverlay.addEventListener('click', () => this.closeModal());
        }

        if (modalClose) {
            modalClose.addEventListener('click', () => this.closeModal());
        }

        // Закрытие по Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.closeModal();
            }
        });
    }

    loadInitialSection() {
        const urlParams = Utils.getUrlParams();
        const section = urlParams.section || 'dashboard';
        this.navigateToSection(section);
    }

    navigateToSection(section) {
        // Скрываем все секции
        document.querySelectorAll('.content-section').forEach(el => {
            el.classList.remove('active');
        });

        // Убираем активный класс со всех ссылок
        document.querySelectorAll('.nav-link').forEach(el => {
            el.classList.remove('active');
        });

        // Показываем нужную секцию
        const targetSection = document.getElementById(`${section}-section`);
        if (targetSection) {
            targetSection.classList.add('active');
        }

        // Активируем ссылку
        const activeLink = document.querySelector(`[data-section="${section}"]`);
        if (activeLink) {
            activeLink.classList.add('active');
        }

        // Обновляем заголовок
        this.updatePageTitle(section);

        // Загружаем модуль если нужно
        this.loadModule(section);

        // Обновляем URL
        Utils.setUrlParams({ section });

        this.currentSection = section;
    }

    updatePageTitle(section) {
        const titles = {
            dashboard: 'Дашборд',
            projects: 'Проекты',
            estimates: 'Сметы',
            employees: 'Сотрудники',
            timesheet: 'Табель',
            analytics: 'Аналитика',
            calendar: 'Календарь',
            reports: 'Отчеты',
            settings: 'Настройки'
        };

        const pageTitle = document.getElementById('page-title');
        if (pageTitle) {
            pageTitle.textContent = titles[section] || 'Страница';
        }
    }

    initializeModules() {
        // Инициализируем модули
        this.modules = {
            dashboard: window.DashboardModule,
            projects: window.ProjectsModule,
            estimates: window.EstimatesModule,
            employees: window.EmployeesModule,
            timesheet: window.TimesheetModule,
            analytics: window.AnalyticsModule,
            calendar: window.CalendarModule,
            reports: window.ReportsModule,
            settings: window.SettingsModule
        };
    }

    loadModule(section) {
        const module = this.modules[section];
        if (module && typeof module.init === 'function') {
            module.init();
        }
    }

    toggleSidebar() {
        const sidebar = document.querySelector('.sidebar');
        sidebar.classList.toggle('open');
    }

    handleGlobalSearch(query) {
        if (!query.trim()) return;

        // Поиск по всем модулям
        Object.keys(this.modules).forEach(section => {
            const module = this.modules[section];
            if (module && typeof module.search === 'function') {
                module.search(query);
            }
        });
    }

    handleUrlChange() {
        const urlParams = Utils.getUrlParams();
        const section = urlParams.section || 'dashboard';
        this.navigateToSection(section);
    }

    // Модальные окна
    showModal(title, content, options = {}) {
        const modalContainer = document.getElementById('modal-container');
        const modalTitle = document.getElementById('modal-title');
        const modalBody = document.getElementById('modal-body');

        if (modalTitle) modalTitle.textContent = title;
        if (modalBody) modalBody.innerHTML = content;

        modalContainer.classList.remove('hidden');

        // Добавляем кнопки если нужно
        if (options.buttons) {
            this.addModalButtons(options.buttons);
        }
    }

    closeModal() {
        const modalContainer = document.getElementById('modal-container');
        modalContainer.classList.add('hidden');
        
        // Очищаем содержимое
        const modalBody = document.getElementById('modal-body');
        if (modalBody) modalBody.innerHTML = '';
    }

    addModalButtons(buttons) {
        const modalBody = document.getElementById('modal-body');
        const buttonContainer = document.createElement('div');
        buttonContainer.className = 'modal-footer';
        
        buttons.forEach(button => {
            const btn = document.createElement('button');
            btn.className = `btn ${button.class || 'btn-secondary'}`;
            btn.textContent = button.text;
            btn.onclick = button.onClick;
            buttonContainer.appendChild(btn);
        });
        
        modalBody.appendChild(buttonContainer);
    }

    // Уведомления
    showNotification(message, type = 'info', duration = 3000) {
        const container = document.getElementById('notifications-container');
        const notification = document.createElement('div');
        
        notification.className = `notification ${type}`;
        notification.textContent = message;
        
        container.appendChild(notification);
        
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, duration);
    }

    // Загрузка данных
    async loadData(endpoint, options = {}) {
        try {
            // В реальном приложении здесь был бы API запрос
            // Для демо используем локальные данные
            return await this.getDemoData(endpoint, options);
        } catch (error) {
            this.showNotification('Ошибка загрузки данных', 'error');
            throw error;
        }
    }

    async getDemoData(endpoint, options = {}) {
        // Демо данные для разных эндпоинтов
        const demoData = {
            projects: this.getDemoProjects(),
            employees: this.getDemoEmployees(),
            estimates: this.getDemoEstimates(),
            timesheet: this.getDemoTimesheet(),
            analytics: this.getDemoAnalytics()
        };

        return new Promise((resolve) => {
            setTimeout(() => {
                resolve(demoData[endpoint] || []);
            }, 500); // Имитация задержки сети
        });
    }

    getDemoProjects() {
        return [
            {
                id: '1',
                name: 'Жилой комплекс "Солнечный"',
                type: 'residential',
                status: 'active',
                client: 'ООО "СтройИнвест"',
                manager: 'Иванов И.И.',
                budget: 15000000,
                spent: 8500000,
                startDate: '2024-01-15',
                endDate: '2024-12-31',
                progress: 65,
                location: 'Москва, ул. Ленина, 123'
            },
            {
                id: '2',
                name: 'Торговый центр "МегаМолл"',
                type: 'commercial',
                status: 'pending',
                client: 'АО "ТоргСтрой"',
                manager: 'Петров П.П.',
                budget: 25000000,
                spent: 5000000,
                startDate: '2024-03-01',
                endDate: '2025-06-30',
                progress: 20,
                location: 'Санкт-Петербург, пр. Невский, 456'
            },
            {
                id: '3',
                name: 'Завод по производству бетона',
                type: 'industrial',
                status: 'completed',
                client: 'ООО "ПромСтрой"',
                manager: 'Сидоров С.С.',
                budget: 8000000,
                spent: 8000000,
                startDate: '2023-06-01',
                endDate: '2024-02-28',
                progress: 100,
                location: 'Екатеринбург, ул. Промышленная, 789'
            }
        ];
    }

    getDemoEmployees() {
        return [
            {
                id: '1',
                name: 'Иванов Иван Иванович',
                position: 'Проектный менеджер',
                department: 'Управление проектами',
                email: 'ivanov@buildcrm.com',
                phone: '+7 (495) 123-45-67',
                hireDate: '2020-03-15',
                salary: 120000,
                status: 'active',
                avatar: 'https://via.placeholder.com/40'
            },
            {
                id: '2',
                name: 'Петрова Анна Сергеевна',
                position: 'Инженер-проектировщик',
                department: 'Проектирование',
                email: 'petrova@buildcrm.com',
                phone: '+7 (495) 234-56-78',
                hireDate: '2021-07-20',
                salary: 95000,
                status: 'active',
                avatar: 'https://via.placeholder.com/40'
            },
            {
                id: '3',
                name: 'Сидоров Алексей Петрович',
                position: 'Прораб',
                department: 'Строительство',
                email: 'sidorov@buildcrm.com',
                phone: '+7 (495) 345-67-89',
                hireDate: '2019-11-10',
                salary: 85000,
                status: 'active',
                avatar: 'https://via.placeholder.com/40'
            }
        ];
    }

    getDemoEstimates() {
        return [
            {
                id: '1',
                projectId: '1',
                projectName: 'Жилой комплекс "Солнечный"',
                name: 'Смета на фундаментные работы',
                totalAmount: 2500000,
                status: 'approved',
                createdBy: 'Иванов И.И.',
                createdAt: '2024-01-20',
                approvedAt: '2024-01-25',
                items: [
                    { name: 'Земляные работы', quantity: 1000, unit: 'м³', price: 500, amount: 500000 },
                    { name: 'Бетон М300', quantity: 800, unit: 'м³', price: 2500, amount: 2000000 }
                ]
            },
            {
                id: '2',
                projectId: '2',
                projectName: 'Торговый центр "МегаМолл"',
                name: 'Смета на отделочные работы',
                totalAmount: 5000000,
                status: 'pending',
                createdBy: 'Петров П.П.',
                createdAt: '2024-03-15',
                items: [
                    { name: 'Штукатурка стен', quantity: 5000, unit: 'м²', price: 300, amount: 1500000 },
                    { name: 'Покраска потолков', quantity: 3000, unit: 'м²', price: 200, amount: 600000 },
                    { name: 'Укладка плитки', quantity: 2000, unit: 'м²', price: 1450, amount: 2900000 }
                ]
            }
        ];
    }

    getDemoTimesheet() {
        const today = new Date();
        const timesheet = [];
        
        for (let i = 0; i < 30; i++) {
            const date = new Date(today);
            date.setDate(date.getDate() - i);
            
            timesheet.push({
                id: i + 1,
                employeeId: '1',
                employeeName: 'Иванов Иван Иванович',
                date: date.toISOString().split('T')[0],
                projectId: '1',
                projectName: 'Жилой комплекс "Солнечный"',
                hours: Math.floor(Math.random() * 4) + 6,
                task: 'Управление проектом',
                status: 'approved'
            });
        }
        
        return timesheet;
    }

    getDemoAnalytics() {
        return {
            projects: {
                total: 15,
                active: 8,
                completed: 5,
                pending: 2
            },
            revenue: {
                current: 45000000,
                previous: 38000000,
                growth: 18.4
            },
            employees: {
                total: 45,
                active: 42,
                onLeave: 3
            },
            efficiency: {
                average: 87.5,
                trend: 2.3
            }
        };
    }

    // Экспорт данных
    exportData(data, filename, format = 'csv') {
        if (format === 'csv') {
            Utils.exportToCSV(data, filename);
        } else if (format === 'excel') {
            Utils.exportToExcel(data, filename);
        }
    }

    // Печать
    printSection(sectionId) {
        Utils.printElement(sectionId);
    }
}

// Инициализация приложения
document.addEventListener('DOMContentLoaded', () => {
    window.app = new App();
});