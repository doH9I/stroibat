// Модуль табеля рабочего времени BuildCRM

class TimesheetModule {
    constructor() {
        this.timesheet = [];
        this.filters = {
            employee: 'all',
            project: 'all',
            dateFrom: '',
            dateTo: ''
        };
    }

    init() {
        this.loadTimesheet();
        this.renderTimesheet();
        this.bindEvents();
    }

    async loadTimesheet() {
        try {
            this.timesheet = await app.loadData('timesheet');
            this.renderTimesheet();
        } catch (error) {
            console.error('Ошибка загрузки табеля:', error);
            app.showNotification('Ошибка загрузки табеля', 'error');
        }
    }

    renderTimesheet() {
        const container = document.getElementById('timesheet-section');
        if (!container) return;

        container.innerHTML = `
            <div class="timesheet-container">
                <!-- Заголовок и действия -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Табель рабочего времени</h2>
                        <p>Учет рабочего времени сотрудников</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="TimesheetModule.showAddEntry()">
                            <i class="fas fa-plus"></i> Добавить запись
                        </button>
                        <button class="btn btn-secondary" onclick="TimesheetModule.exportTimesheet()">
                            <i class="fas fa-download"></i> Экспорт
                        </button>
                    </div>
                </div>

                <!-- Фильтры -->
                <div class="filters-section">
                    <div class="filters-grid">
                        <div class="filter-group">
                            <label>Сотрудник</label>
                            <select class="form-control" id="employee-filter">
                                <option value="all">Все сотрудники</option>
                                <option value="1">Иванов Иван Иванович</option>
                                <option value="2">Петрова Анна Сергеевна</option>
                                <option value="3">Сидоров Алексей Петрович</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>Проект</label>
                            <select class="form-control" id="project-filter">
                                <option value="all">Все проекты</option>
                                <option value="1">Жилой комплекс "Солнечный"</option>
                                <option value="2">Торговый центр "МегаМолл"</option>
                                <option value="3">Завод по производству бетона</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>Дата с</label>
                            <input type="date" class="form-control" id="date-from-filter">
                        </div>
                        <div class="filter-group">
                            <label>Дата по</label>
                            <input type="date" class="form-control" id="date-to-filter">
                        </div>
                        <div class="filter-group">
                            <label>&nbsp;</label>
                            <button class="btn btn-secondary" onclick="TimesheetModule.clearFilters()">
                                <i class="fas fa-times"></i> Очистить
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Статистика -->
                <div class="stats-section">
                    ${this.renderStats()}
                </div>

                <!-- Таблица табеля -->
                <div class="timesheet-table">
                    ${this.renderTimesheetTable()}
                </div>
            </div>
        `;

        this.bindFilterEvents();
    }

    renderStats() {
        const totalHours = this.timesheet.reduce((sum, entry) => sum + (entry.hours || 0), 0);
        const totalEntries = this.timesheet.length;
        const approvedEntries = this.timesheet.filter(entry => entry.status === 'approved').length;
        const averageHours = totalEntries > 0 ? Math.round(totalHours / totalEntries) : 0;

        return `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${totalHours}</div>
                        <div class="stat-label">Всего часов</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-list"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${totalEntries}</div>
                        <div class="stat-label">Записей</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon approved">
                        <i class="fas fa-check"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${approvedEntries}</div>
                        <div class="stat-label">Утверждено</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-chart-line"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${averageHours}</div>
                        <div class="stat-label">Среднее часов/день</div>
                    </div>
                </div>
            </div>
        `;
    }

    renderTimesheetTable() {
        const filteredTimesheet = this.getFilteredTimesheet();

        if (filteredTimesheet.length === 0) {
            return `
                <div class="empty-state">
                    <i class="fas fa-clock"></i>
                    <h3>Записи не найдены</h3>
                    <p>Попробуйте изменить фильтры или добавить новую запись</p>
                    <button class="btn btn-primary" onclick="TimesheetModule.showAddEntry()">
                        <i class="fas fa-plus"></i> Добавить запись
                    </button>
                </div>
            `;
        }

        return `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Дата</th>
                            <th>Сотрудник</th>
                            <th>Проект</th>
                            <th>Задача</th>
                            <th>Часы</th>
                            <th>Статус</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${filteredTimesheet.map(entry => `
                            <tr>
                                <td>${Utils.formatDate(entry.date)}</td>
                                <td>${entry.employeeName}</td>
                                <td>${entry.projectName}</td>
                                <td>${entry.task}</td>
                                <td>${entry.hours}</td>
                                <td>
                                    <span class="status-badge status-${entry.status}">
                                        ${entry.status === 'approved' ? 'Утверждено' : 'Ожидает'}
                                    </span>
                                </td>
                                <td>
                                    <div class="table-actions">
                                        <button class="btn-icon" onclick="TimesheetModule.editEntry('${entry.id}')">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon" onclick="TimesheetModule.deleteEntry('${entry.id}')">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }

    getFilteredTimesheet() {
        return this.timesheet.filter(entry => {
            const employeeMatch = this.filters.employee === 'all' || entry.employeeId === this.filters.employee;
            const projectMatch = this.filters.project === 'all' || entry.projectId === this.filters.project;
            const dateFromMatch = !this.filters.dateFrom || entry.date >= this.filters.dateFrom;
            const dateToMatch = !this.filters.dateTo || entry.date <= this.filters.dateTo;
            
            return employeeMatch && projectMatch && dateFromMatch && dateToMatch;
        });
    }

    bindEvents() {
        // События уже привязаны в renderTimesheet
    }

    bindFilterEvents() {
        const employeeFilter = document.getElementById('employee-filter');
        const projectFilter = document.getElementById('project-filter');
        const dateFromFilter = document.getElementById('date-from-filter');
        const dateToFilter = document.getElementById('date-to-filter');

        if (employeeFilter) {
            employeeFilter.addEventListener('change', (e) => {
                this.filters.employee = e.target.value;
                this.renderTimesheetTable();
            });
        }

        if (projectFilter) {
            projectFilter.addEventListener('change', (e) => {
                this.filters.project = e.target.value;
                this.renderTimesheetTable();
            });
        }

        if (dateFromFilter) {
            dateFromFilter.addEventListener('change', (e) => {
                this.filters.dateFrom = e.target.value;
                this.renderTimesheetTable();
            });
        }

        if (dateToFilter) {
            dateToFilter.addEventListener('change', (e) => {
                this.filters.dateTo = e.target.value;
                this.renderTimesheetTable();
            });
        }
    }

    // Статические методы
    static showAddEntry() {
        const content = `
            <form id="add-timesheet-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="entry-date">Дата *</label>
                        <input type="date" id="entry-date" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="entry-employee">Сотрудник *</label>
                        <select id="entry-employee" class="form-control" required>
                            <option value="">Выберите сотрудника</option>
                            <option value="1">Иванов Иван Иванович</option>
                            <option value="2">Петрова Анна Сергеевна</option>
                            <option value="3">Сидоров Алексей Петрович</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="entry-project">Проект *</label>
                        <select id="entry-project" class="form-control" required>
                            <option value="">Выберите проект</option>
                            <option value="1">Жилой комплекс "Солнечный"</option>
                            <option value="2">Торговый центр "МегаМолл"</option>
                            <option value="3">Завод по производству бетона</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="entry-hours">Часы *</label>
                        <input type="number" id="entry-hours" class="form-control" required min="0" max="24" step="0.5">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="entry-task">Задача *</label>
                    <input type="text" id="entry-task" class="form-control" required>
                </div>
            </form>
        `;

        app.showModal('Добавить запись в табель', content, {
            buttons: [
                {
                    text: 'Отмена',
                    class: 'btn-secondary',
                    onClick: () => app.closeModal()
                },
                {
                    text: 'Добавить',
                    class: 'btn-primary',
                    onClick: () => TimesheetModule.addEntry()
                }
            ]
        });
    }

    static addEntry() {
        const entryData = {
            id: Utils.generateId(),
            date: document.getElementById('entry-date').value,
            employeeId: document.getElementById('entry-employee').value,
            employeeName: document.getElementById('entry-employee').options[document.getElementById('entry-employee').selectedIndex].text,
            projectId: document.getElementById('entry-project').value,
            projectName: document.getElementById('entry-project').options[document.getElementById('entry-project').selectedIndex].text,
            task: document.getElementById('entry-task').value,
            hours: parseFloat(document.getElementById('entry-hours').value),
            status: 'pending',
            createdAt: new Date().toISOString()
        };

        TimesheetModule.timesheet.push(entryData);
        TimesheetModule.renderTimesheet();
        app.closeModal();
        app.showNotification('Запись добавлена', 'success');
    }

    static editEntry(entryId) {
        // Реализация редактирования записи
        app.showModal('Редактировать запись', 'Форма редактирования', {
            buttons: [
                { text: 'Отмена', class: 'btn-secondary', onClick: () => app.closeModal() },
                { text: 'Сохранить', class: 'btn-primary', onClick: () => app.closeModal() }
            ]
        });
    }

    static deleteEntry(entryId) {
        app.showModal('Удалить запись', `
            <p>Вы уверены, что хотите удалить эту запись? Это действие нельзя отменить.</p>
        `, {
            buttons: [
                { text: 'Отмена', class: 'btn-secondary', onClick: () => app.closeModal() },
                { 
                    text: 'Удалить', 
                    class: 'btn-danger', 
                    onClick: () => {
                        TimesheetModule.timesheet = TimesheetModule.timesheet.filter(e => e.id !== entryId);
                        TimesheetModule.renderTimesheet();
                        app.closeModal();
                        app.showNotification('Запись удалена', 'success');
                    }
                }
            ]
        });
    }

    static exportTimesheet() {
        const timesheet = TimesheetModule.getFilteredTimesheet();
        const exportData = timesheet.map(entry => ({
            'Дата': Utils.formatDate(entry.date),
            'Сотрудник': entry.employeeName,
            'Проект': entry.projectName,
            'Задача': entry.task,
            'Часы': entry.hours,
            'Статус': entry.status === 'approved' ? 'Утверждено' : 'Ожидает'
        }));

        app.exportData(exportData, 'timesheet-export.csv', 'csv');
        app.showNotification('Табель экспортирован', 'success');
    }

    static clearFilters() {
        TimesheetModule.filters = { employee: 'all', project: 'all', dateFrom: '', dateTo: '' };
        
        const employeeFilter = document.getElementById('employee-filter');
        const projectFilter = document.getElementById('project-filter');
        const dateFromFilter = document.getElementById('date-from-filter');
        const dateToFilter = document.getElementById('date-to-filter');

        if (employeeFilter) employeeFilter.value = 'all';
        if (projectFilter) projectFilter.value = 'all';
        if (dateFromFilter) dateFromFilter.value = '';
        if (dateToFilter) dateToFilter.value = '';

        TimesheetModule.renderTimesheetTable();
    }

    // Поиск
    search(query) {
        // Реализация поиска по табелю
        console.log('Поиск по табелю:', query);
    }
}

// Инициализация модуля
window.TimesheetModule = new TimesheetModule();