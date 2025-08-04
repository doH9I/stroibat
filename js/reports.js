// Модуль отчетов BuildCRM

class ReportsModule {
    constructor() {
        this.reports = [];
        this.currentReport = null;
    }

    init() {
        this.loadReports();
        this.renderReports();
    }

    async loadReports() {
        // Демо данные для отчетов
        this.reports = [
            {
                id: 1,
                name: 'Финансовый отчет за месяц',
                type: 'financial',
                description: 'Детальный анализ доходов и расходов',
                lastGenerated: '2024-01-15',
                status: 'completed'
            },
            {
                id: 2,
                name: 'Отчет по проектам',
                type: 'projects',
                description: 'Статус и прогресс всех проектов',
                lastGenerated: '2024-01-14',
                status: 'completed'
            },
            {
                id: 3,
                name: 'Отчет по сотрудникам',
                type: 'employees',
                description: 'Эффективность и загрузка сотрудников',
                lastGenerated: '2024-01-13',
                status: 'completed'
            },
            {
                id: 4,
                name: 'Отчет по временным затратам',
                type: 'timesheet',
                description: 'Анализ рабочего времени',
                lastGenerated: '2024-01-12',
                status: 'completed'
            }
        ];
    }

    renderReports() {
        const container = document.getElementById('reports-section');
        if (!container) return;

        container.innerHTML = `
            <div class="reports-container">
                <!-- Заголовок -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Отчеты</h2>
                        <p>Генерация и управление отчетами</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="ReportsModule.showGenerateReportModal()">
                            <i class="fas fa-plus"></i> Создать отчет
                        </button>
                    </div>
                </div>

                <!-- Статистика отчетов -->
                <div class="reports-stats">
                    ${this.renderReportsStats()}
                </div>

                <!-- Список отчетов -->
                <div class="reports-list">
                    <div class="reports-header">
                        <h3>Доступные отчеты</h3>
                        <div class="reports-filters">
                            <select onchange="ReportsModule.filterReports(this.value)">
                                <option value="">Все типы</option>
                                <option value="financial">Финансовые</option>
                                <option value="projects">Проекты</option>
                                <option value="employees">Сотрудники</option>
                                <option value="timesheet">Временные затраты</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="reports-grid">
                        ${this.renderReportsGrid()}
                    </div>
                </div>

                <!-- Шаблоны отчетов -->
                <div class="report-templates">
                    <h3>Шаблоны отчетов</h3>
                    <div class="templates-grid">
                        ${this.renderReportTemplates()}
                    </div>
                </div>
            </div>
        `;
    }

    renderReportsStats() {
        const stats = {
            total: this.reports.length,
            completed: this.reports.filter(r => r.status === 'completed').length,
            pending: this.reports.filter(r => r.status === 'pending').length,
            failed: this.reports.filter(r => r.status === 'failed').length
        };

        return `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-file-alt"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${stats.total}</div>
                        <div class="stat-label">Всего отчетов</div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon success">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${stats.completed}</div>
                        <div class="stat-label">Завершено</div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon warning">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${stats.pending}</div>
                        <div class="stat-label">В обработке</div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon danger">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${stats.failed}</div>
                        <div class="stat-label">Ошибки</div>
                    </div>
                </div>
            </div>
        `;
    }

    renderReportsGrid() {
        return this.reports.map(report => `
            <div class="report-card">
                <div class="report-header">
                    <div class="report-type ${report.type}">
                        <i class="fas ${this.getReportTypeIcon(report.type)}"></i>
                    </div>
                    <div class="report-status ${report.status}">
                        <span class="status-dot"></span>
                        ${this.getStatusText(report.status)}
                    </div>
                </div>
                
                <div class="report-content">
                    <h4>${report.name}</h4>
                    <p>${report.description}</p>
                    <div class="report-meta">
                        <span class="report-date">
                            <i class="fas fa-calendar"></i>
                            ${Utils.formatDate(report.lastGenerated)}
                        </span>
                    </div>
                </div>
                
                <div class="report-actions">
                    <button class="btn btn-sm btn-primary" onclick="ReportsModule.generateReport(${report.id})">
                        <i class="fas fa-download"></i> Скачать
                    </button>
                    <button class="btn btn-sm btn-secondary" onclick="ReportsModule.viewReport(${report.id})">
                        <i class="fas fa-eye"></i> Просмотр
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="ReportsModule.deleteReport(${report.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `).join('');
    }

    renderReportTemplates() {
        const templates = [
            {
                name: 'Ежемесячный финансовый отчет',
                type: 'financial',
                description: 'Автоматический отчет по финансам за месяц',
                icon: 'fas fa-chart-line'
            },
            {
                name: 'Отчет по эффективности проектов',
                type: 'projects',
                description: 'Анализ прогресса и эффективности проектов',
                icon: 'fas fa-project-diagram'
            },
            {
                name: 'Отчет по загрузке сотрудников',
                type: 'employees',
                description: 'Анализ рабочего времени и эффективности',
                icon: 'fas fa-users'
            },
            {
                name: 'Отчет по временным затратам',
                type: 'timesheet',
                description: 'Детальный анализ затраченного времени',
                icon: 'fas fa-clock'
            }
        ];

        return templates.map(template => `
            <div class="template-card">
                <div class="template-icon ${template.type}">
                    <i class="${template.icon}"></i>
                </div>
                <div class="template-content">
                    <h4>${template.name}</h4>
                    <p>${template.description}</p>
                </div>
                <div class="template-actions">
                    <button class="btn btn-sm btn-primary" onclick="ReportsModule.useTemplate('${template.type}')">
                        Использовать
                    </button>
                </div>
            </div>
        `).join('');
    }

    getReportTypeIcon(type) {
        const icons = {
            financial: 'fa-chart-line',
            projects: 'fa-project-diagram',
            employees: 'fa-users',
            timesheet: 'fa-clock'
        };
        return icons[type] || 'fa-file-alt';
    }

    getStatusText(status) {
        const statuses = {
            completed: 'Завершен',
            pending: 'В обработке',
            failed: 'Ошибка'
        };
        return statuses[status] || status;
    }

    static showGenerateReportModal() {
        const modalContent = `
            <div class="modal-header">
                <h3>Создать новый отчет</h3>
                <button class="btn btn-icon" onclick="app.closeModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">
                <form id="generate-report-form">
                    <div class="form-group">
                        <label>Название отчета</label>
                        <input type="text" name="name" required>
                    </div>
                    
                    <div class="form-group">
                        <label>Тип отчета</label>
                        <select name="type" required>
                            <option value="">Выберите тип</option>
                            <option value="financial">Финансовый отчет</option>
                            <option value="projects">Отчет по проектам</option>
                            <option value="employees">Отчет по сотрудникам</option>
                            <option value="timesheet">Отчет по временным затратам</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label>Описание</label>
                        <textarea name="description" rows="3"></textarea>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Период с</label>
                            <input type="date" name="dateFrom" required>
                        </div>
                        <div class="form-group">
                            <label>Период по</label>
                            <input type="date" name="dateTo" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Формат</label>
                        <select name="format" required>
                            <option value="pdf">PDF</option>
                            <option value="excel">Excel</option>
                            <option value="csv">CSV</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="app.closeModal()">Отмена</button>
                <button class="btn btn-primary" onclick="ReportsModule.generateNewReport()">Создать</button>
            </div>
        `;
        
        app.showModal(modalContent);
        
        // Установить текущую дату
        const dateToInput = document.querySelector('input[name="dateTo"]');
        const dateFromInput = document.querySelector('input[name="dateFrom"]');
        if (dateToInput && dateFromInput) {
            const today = new Date();
            const monthAgo = new Date();
            monthAgo.setMonth(monthAgo.getMonth() - 1);
            
            dateToInput.value = ReportsModule.formatDate(today);
            dateFromInput.value = ReportsModule.formatDate(monthAgo);
        }
    }

    static generateNewReport() {
        const form = document.getElementById('generate-report-form');
        const formData = new FormData(form);
        
        const report = {
            id: Date.now(),
            name: formData.get('name'),
            type: formData.get('type'),
            description: formData.get('description'),
            dateFrom: formData.get('dateFrom'),
            dateTo: formData.get('dateTo'),
            format: formData.get('format'),
            lastGenerated: ReportsModule.formatDate(new Date()),
            status: 'pending'
        };
        
        ReportsModule.reports.unshift(report);
        ReportsModule.renderReports();
        app.closeModal();
        app.showNotification('Отчет создан и добавлен в очередь', 'success');
        
        // Имитация генерации отчета
        setTimeout(() => {
            report.status = 'completed';
            ReportsModule.renderReports();
            app.showNotification('Отчет готов к скачиванию', 'success');
        }, 3000);
    }

    static generateReport(reportId) {
        const report = ReportsModule.reports.find(r => r.id === reportId);
        if (!report) return;
        
        app.showNotification('Скачивание отчета...', 'info');
        
        // Имитация скачивания
        setTimeout(() => {
            const data = {
                'Название отчета': report.name,
                'Тип': report.type,
                'Дата генерации': report.lastGenerated,
                'Статус': ReportsModule.getStatusText(report.status)
            };
            
            app.exportData([data], `${report.name}.csv`, 'csv');
            app.showNotification('Отчет скачан', 'success');
        }, 1000);
    }

    static viewReport(reportId) {
        const report = ReportsModule.reports.find(r => r.id === reportId);
        if (!report) return;
        
        app.showNotification('Просмотр отчета', 'info');
    }

    static deleteReport(reportId) {
        if (confirm('Удалить этот отчет?')) {
            ReportsModule.reports = ReportsModule.reports.filter(r => r.id !== reportId);
            ReportsModule.renderReports();
            app.showNotification('Отчет удален', 'success');
        }
    }

    static useTemplate(templateType) {
        const templates = {
            financial: {
                name: 'Ежемесячный финансовый отчет',
                description: 'Автоматический отчет по финансам за месяц'
            },
            projects: {
                name: 'Отчет по эффективности проектов',
                description: 'Анализ прогресса и эффективности проектов'
            },
            employees: {
                name: 'Отчет по загрузке сотрудников',
                description: 'Анализ рабочего времени и эффективности'
            },
            timesheet: {
                name: 'Отчет по временным затратам',
                description: 'Детальный анализ затраченного времени'
            }
        };
        
        const template = templates[templateType];
        if (!template) return;
        
        const report = {
            id: Date.now(),
            name: template.name,
            type: templateType,
            description: template.description,
            lastGenerated: ReportsModule.formatDate(new Date()),
            status: 'pending'
        };
        
        ReportsModule.reports.unshift(report);
        ReportsModule.renderReports();
        app.showNotification('Отчет создан из шаблона', 'success');
        
        // Имитация генерации
        setTimeout(() => {
            report.status = 'completed';
            ReportsModule.renderReports();
            app.showNotification('Отчет готов', 'success');
        }, 2000);
    }

    static filterReports(type) {
        console.log('Фильтрация отчетов по типу:', type);
    }

    static formatDate(date) {
        return date.toISOString().split('T')[0];
    }

    // Поиск
    search(query) {
        console.log('Поиск по отчетам:', query);
    }
}

// Инициализация модуля
window.ReportsModule = new ReportsModule();