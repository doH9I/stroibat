// Модуль сотрудников BuildCRM

class EmployeesModule {
    constructor() {
        this.employees = [];
        this.currentEmployee = null;
        this.filters = {
            department: 'all',
            status: 'all',
            search: ''
        };
    }

    init() {
        this.loadEmployees();
        this.renderEmployees();
        this.bindEvents();
    }

    async loadEmployees() {
        try {
            this.employees = await app.loadData('employees');
            this.renderEmployees();
        } catch (error) {
            console.error('Ошибка загрузки сотрудников:', error);
            app.showNotification('Ошибка загрузки сотрудников', 'error');
        }
    }

    renderEmployees() {
        const container = document.getElementById('employees-section');
        if (!container) return;

        container.innerHTML = `
            <div class="employees-container">
                <!-- Заголовок и действия -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Управление сотрудниками</h2>
                        <p>Ведите учет персонала и управляйте кадрами</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="EmployeesModule.showCreateEmployee()">
                            <i class="fas fa-plus"></i> Новый сотрудник
                        </button>
                        <button class="btn btn-secondary" onclick="EmployeesModule.exportEmployees()">
                            <i class="fas fa-download"></i> Экспорт
                        </button>
                    </div>
                </div>

                <!-- Фильтры -->
                <div class="filters-section">
                    <div class="filters-grid">
                        <div class="filter-group">
                            <label>Отдел</label>
                            <select class="form-control" id="department-filter">
                                <option value="all">Все отделы</option>
                                <option value="Управление проектами">Управление проектами</option>
                                <option value="Проектирование">Проектирование</option>
                                <option value="Строительство">Строительство</option>
                                <option value="Бухгалтерия">Бухгалтерия</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>Статус</label>
                            <select class="form-control" id="status-filter">
                                <option value="all">Все статусы</option>
                                <option value="active">Активные</option>
                                <option value="inactive">Неактивные</option>
                                <option value="on_leave">В отпуске</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>Поиск</label>
                            <input type="text" class="form-control" id="search-filter" placeholder="Поиск по имени...">
                        </div>
                        <div class="filter-group">
                            <label>&nbsp;</label>
                            <button class="btn btn-secondary" onclick="EmployeesModule.clearFilters()">
                                <i class="fas fa-times"></i> Очистить
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Статистика -->
                <div class="stats-section">
                    ${this.renderStats()}
                </div>

                <!-- Список сотрудников -->
                <div class="employees-list">
                    ${this.renderEmployeesList()}
                </div>
            </div>
        `;

        this.bindFilterEvents();
    }

    renderStats() {
        const total = this.employees.length;
        const active = this.employees.filter(e => e.status === 'active').length;
        const onLeave = this.employees.filter(e => e.status === 'on_leave').length;
        const totalSalary = this.employees.reduce((sum, e) => sum + (e.salary || 0), 0);

        return `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${total}</div>
                        <div class="stat-label">Всего сотрудников</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon active">
                        <i class="fas fa-user-check"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${active}</div>
                        <div class="stat-label">Активные</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon leave">
                        <i class="fas fa-umbrella-beach"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${onLeave}</div>
                        <div class="stat-label">В отпуске</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon salary">
                        <i class="fas fa-ruble-sign"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${Utils.formatCurrency(totalSalary)}</div>
                        <div class="stat-label">Фонд оплаты труда</div>
                    </div>
                </div>
            </div>
        `;
    }

    renderEmployeesList() {
        const filteredEmployees = this.getFilteredEmployees();

        if (filteredEmployees.length === 0) {
            return `
                <div class="empty-state">
                    <i class="fas fa-users"></i>
                    <h3>Сотрудники не найдены</h3>
                    <p>Попробуйте изменить фильтры или добавить нового сотрудника</p>
                    <button class="btn btn-primary" onclick="EmployeesModule.showCreateEmployee()">
                        <i class="fas fa-plus"></i> Добавить сотрудника
                    </button>
                </div>
            `;
        }

        return `
            <div class="employees-grid">
                ${filteredEmployees.map(employee => this.renderEmployeeCard(employee)).join('')}
            </div>
        `;
    }

    renderEmployeeCard(employee) {
        const hireDate = new Date(employee.hireDate);
        const yearsWorked = Math.floor((new Date() - hireDate) / (1000 * 60 * 60 * 24 * 365));

        return `
            <div class="employee-card" data-employee-id="${employee.id}">
                <div class="employee-header">
                    <div class="employee-avatar">
                        <img src="${employee.avatar}" alt="${employee.name}" onerror="this.src='https://via.placeholder.com/60'">
                        <div class="employee-status status-${employee.status}"></div>
                    </div>
                    <div class="employee-actions">
                        <button class="btn-icon" onclick="EmployeesModule.showEmployeeDetails('${employee.id}')">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn-icon" onclick="EmployeesModule.editEmployee('${employee.id}')">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-icon" onclick="EmployeesModule.showTimesheet('${employee.id}')">
                            <i class="fas fa-clock"></i>
                        </button>
                        <button class="btn-icon" onclick="EmployeesModule.deleteEmployee('${employee.id}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
                
                <div class="employee-content">
                    <h3 class="employee-name">${employee.name}</h3>
                    <p class="employee-position">${employee.position}</p>
                    <p class="employee-department">
                        <i class="fas fa-building"></i> ${employee.department}
                    </p>
                    
                    <div class="employee-info">
                        <div class="info-item">
                            <i class="fas fa-envelope"></i>
                            <span>${employee.email}</span>
                        </div>
                        <div class="info-item">
                            <i class="fas fa-phone"></i>
                            <span>${employee.phone}</span>
                        </div>
                        <div class="info-item">
                            <i class="fas fa-calendar"></i>
                            <span>Работает ${yearsWorked} лет</span>
                        </div>
                    </div>
                </div>
                
                <div class="employee-salary">
                    <div class="salary-info">
                        <span class="salary-label">Зарплата:</span>
                        <span class="salary-value">${Utils.formatCurrency(employee.salary)}</span>
                    </div>
                </div>
                
                <div class="employee-footer">
                    <button class="btn btn-sm btn-primary" onclick="EmployeesModule.showEmployeeDetails('${employee.id}')">
                        <i class="fas fa-user"></i> Профиль
                    </button>
                    <button class="btn btn-sm btn-secondary" onclick="EmployeesModule.showTimesheet('${employee.id}')">
                        <i class="fas fa-clock"></i> Табель
                    </button>
                </div>
            </div>
        `;
    }

    getFilteredEmployees() {
        return this.employees.filter(employee => {
            const departmentMatch = this.filters.department === 'all' || employee.department === this.filters.department;
            const statusMatch = this.filters.status === 'all' || employee.status === this.filters.status;
            const searchMatch = !this.filters.search || 
                employee.name.toLowerCase().includes(this.filters.search.toLowerCase()) ||
                employee.position.toLowerCase().includes(this.filters.search.toLowerCase());
            
            return departmentMatch && statusMatch && searchMatch;
        });
    }

    bindEvents() {
        // События уже привязаны в renderEmployees
    }

    bindFilterEvents() {
        const departmentFilter = document.getElementById('department-filter');
        const statusFilter = document.getElementById('status-filter');
        const searchFilter = document.getElementById('search-filter');

        if (departmentFilter) {
            departmentFilter.addEventListener('change', (e) => {
                this.filters.department = e.target.value;
                this.renderEmployeesList();
            });
        }

        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                this.filters.status = e.target.value;
                this.renderEmployeesList();
            });
        }

        if (searchFilter) {
            searchFilter.addEventListener('input', Utils.debounce((e) => {
                this.filters.search = e.target.value;
                this.renderEmployeesList();
            }, 300));
        }
    }

    // Статические методы для модальных окон
    static showCreateEmployee() {
        const content = `
            <form id="create-employee-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="employee-name">ФИО *</label>
                        <input type="text" id="employee-name" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="employee-position">Должность *</label>
                        <input type="text" id="employee-position" class="form-control" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="employee-department">Отдел *</label>
                        <select id="employee-department" class="form-control" required>
                            <option value="">Выберите отдел</option>
                            <option value="Управление проектами">Управление проектами</option>
                            <option value="Проектирование">Проектирование</option>
                            <option value="Строительство">Строительство</option>
                            <option value="Бухгалтерия">Бухгалтерия</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="employee-status">Статус *</label>
                        <select id="employee-status" class="form-control" required>
                            <option value="active">Активный</option>
                            <option value="inactive">Неактивный</option>
                            <option value="on_leave">В отпуске</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="employee-email">Email *</label>
                        <input type="email" id="employee-email" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="employee-phone">Телефон *</label>
                        <input type="tel" id="employee-phone" class="form-control" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="employee-hire-date">Дата приема *</label>
                        <input type="date" id="employee-hire-date" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="employee-salary">Зарплата (₽) *</label>
                        <input type="number" id="employee-salary" class="form-control" required min="0">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="employee-avatar">Фото</label>
                    <input type="file" id="employee-avatar" class="form-control" accept="image/*">
                </div>
            </form>
        `;

        app.showModal('Добавить нового сотрудника', content, {
            buttons: [
                {
                    text: 'Отмена',
                    class: 'btn-secondary',
                    onClick: () => app.closeModal()
                },
                {
                    text: 'Добавить',
                    class: 'btn-primary',
                    onClick: () => EmployeesModule.createEmployee()
                }
            ]
        });
    }

    static async createEmployee() {
        const form = document.getElementById('create-employee-form');
        if (!form) return;

        const employeeData = {
            id: Utils.generateId(),
            name: document.getElementById('employee-name').value,
            position: document.getElementById('employee-position').value,
            department: document.getElementById('employee-department').value,
            status: document.getElementById('employee-status').value,
            email: document.getElementById('employee-email').value,
            phone: document.getElementById('employee-phone').value,
            hireDate: document.getElementById('employee-hire-date').value,
            salary: parseFloat(document.getElementById('employee-salary').value),
            avatar: 'https://via.placeholder.com/60',
            createdAt: new Date().toISOString()
        };

        try {
            EmployeesModule.employees.push(employeeData);
            EmployeesModule.renderEmployees();
            app.closeModal();
            app.showNotification('Сотрудник успешно добавлен', 'success');
        } catch (error) {
            app.showNotification('Ошибка добавления сотрудника', 'error');
        }
    }

    static showEmployeeDetails(employeeId) {
        const employee = EmployeesModule.employees.find(e => e.id === employeeId);
        if (!employee) return;

        const hireDate = new Date(employee.hireDate);
        const yearsWorked = Math.floor((new Date() - hireDate) / (1000 * 60 * 60 * 24 * 365));

        const content = `
            <div class="employee-details">
                <div class="employee-profile">
                    <div class="profile-header">
                        <img src="${employee.avatar}" alt="${employee.name}" class="profile-avatar">
                        <div class="profile-info">
                            <h3>${employee.name}</h3>
                            <p class="profile-position">${employee.position}</p>
                            <span class="status-badge status-${employee.status}">${EmployeesModule.getStatusText(employee.status)}</span>
                        </div>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h4>Основная информация</h4>
                    <div class="detail-grid">
                        <div class="detail-item">
                            <span class="detail-label">Должность:</span>
                            <span class="detail-value">${employee.position}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Отдел:</span>
                            <span class="detail-value">${employee.department}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Email:</span>
                            <span class="detail-value">${employee.email}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Телефон:</span>
                            <span class="detail-value">${employee.phone}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Дата приема:</span>
                            <span class="detail-value">${Utils.formatDate(employee.hireDate)}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Стаж работы:</span>
                            <span class="detail-value">${yearsWorked} лет</span>
                        </div>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h4>Финансовая информация</h4>
                    <div class="detail-grid">
                        <div class="detail-item">
                            <span class="detail-label">Зарплата:</span>
                            <span class="detail-value salary">${Utils.formatCurrency(employee.salary)}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Годовая зарплата:</span>
                            <span class="detail-value">${Utils.formatCurrency(employee.salary * 12)}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;

        app.showModal(`Сотрудник: ${employee.name}`, content, {
            buttons: [
                {
                    text: 'Закрыть',
                    class: 'btn-secondary',
                    onClick: () => app.closeModal()
                },
                {
                    text: 'Редактировать',
                    class: 'btn-primary',
                    onClick: () => {
                        app.closeModal();
                        EmployeesModule.editEmployee(employeeId);
                    }
                },
                {
                    text: 'Табель',
                    class: 'btn-success',
                    onClick: () => {
                        app.closeModal();
                        EmployeesModule.showTimesheet(employeeId);
                    }
                }
            ]
        });
    }

    static editEmployee(employeeId) {
        const employee = EmployeesModule.employees.find(e => e.id === employeeId);
        if (!employee) return;

        const content = `
            <form id="edit-employee-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="edit-employee-name">ФИО *</label>
                        <input type="text" id="edit-employee-name" class="form-control" value="${employee.name}" required>
                    </div>
                    <div class="form-group">
                        <label for="edit-employee-position">Должность *</label>
                        <input type="text" id="edit-employee-position" class="form-control" value="${employee.position}" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="edit-employee-department">Отдел *</label>
                        <select id="edit-employee-department" class="form-control" required>
                            <option value="Управление проектами" ${employee.department === 'Управление проектами' ? 'selected' : ''}>Управление проектами</option>
                            <option value="Проектирование" ${employee.department === 'Проектирование' ? 'selected' : ''}>Проектирование</option>
                            <option value="Строительство" ${employee.department === 'Строительство' ? 'selected' : ''}>Строительство</option>
                            <option value="Бухгалтерия" ${employee.department === 'Бухгалтерия' ? 'selected' : ''}>Бухгалтерия</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="edit-employee-status">Статус *</label>
                        <select id="edit-employee-status" class="form-control" required>
                            <option value="active" ${employee.status === 'active' ? 'selected' : ''}>Активный</option>
                            <option value="inactive" ${employee.status === 'inactive' ? 'selected' : ''}>Неактивный</option>
                            <option value="on_leave" ${employee.status === 'on_leave' ? 'selected' : ''}>В отпуске</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="edit-employee-email">Email *</label>
                        <input type="email" id="edit-employee-email" class="form-control" value="${employee.email}" required>
                    </div>
                    <div class="form-group">
                        <label for="edit-employee-phone">Телефон *</label>
                        <input type="tel" id="edit-employee-phone" class="form-control" value="${employee.phone}" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="edit-employee-hire-date">Дата приема *</label>
                        <input type="date" id="edit-employee-hire-date" class="form-control" value="${employee.hireDate}" required>
                    </div>
                    <div class="form-group">
                        <label for="edit-employee-salary">Зарплата (₽) *</label>
                        <input type="number" id="edit-employee-salary" class="form-control" value="${employee.salary}" required min="0">
                    </div>
                </div>
            </form>
        `;

        app.showModal('Редактировать сотрудника', content, {
            buttons: [
                {
                    text: 'Отмена',
                    class: 'btn-secondary',
                    onClick: () => app.closeModal()
                },
                {
                    text: 'Сохранить',
                    class: 'btn-primary',
                    onClick: () => EmployeesModule.updateEmployee(employeeId)
                }
            ]
        });
    }

    static updateEmployee(employeeId) {
        const employeeIndex = EmployeesModule.employees.findIndex(e => e.id === employeeId);
        if (employeeIndex === -1) return;

        const updatedEmployee = {
            ...EmployeesModule.employees[employeeIndex],
            name: document.getElementById('edit-employee-name').value,
            position: document.getElementById('edit-employee-position').value,
            department: document.getElementById('edit-employee-department').value,
            status: document.getElementById('edit-employee-status').value,
            email: document.getElementById('edit-employee-email').value,
            phone: document.getElementById('edit-employee-phone').value,
            hireDate: document.getElementById('edit-employee-hire-date').value,
            salary: parseFloat(document.getElementById('edit-employee-salary').value),
            updatedAt: new Date().toISOString()
        };

        EmployeesModule.employees[employeeIndex] = updatedEmployee;
        EmployeesModule.renderEmployees();
        app.closeModal();
        app.showNotification('Сотрудник обновлен', 'success');
    }

    static showTimesheet(employeeId) {
        const employee = EmployeesModule.employees.find(e => e.id === employeeId);
        if (!employee) return;

        // Генерируем демо данные табеля
        const timesheetData = [];
        const today = new Date();
        
        for (let i = 0; i < 30; i++) {
            const date = new Date(today);
            date.setDate(date.getDate() - i);
            
            timesheetData.push({
                date: date.toISOString().split('T')[0],
                hours: Math.floor(Math.random() * 4) + 6,
                project: ['Жилой комплекс "Солнечный"', 'Торговый центр "МегаМолл"', 'Завод по производству бетона'][Math.floor(Math.random() * 3)],
                task: ['Управление проектом', 'Проектирование', 'Строительные работы', 'Бухгалтерский учет'][Math.floor(Math.random() * 4)],
                status: ['approved', 'pending', 'approved'][Math.floor(Math.random() * 3)]
            });
        }

        const content = `
            <div class="timesheet-details">
                <div class="timesheet-header">
                    <h4>Табель рабочего времени</h4>
                    <p><strong>Сотрудник:</strong> ${employee.name}</p>
                    <p><strong>Период:</strong> ${Utils.formatDate(timesheetData[29].date)} - ${Utils.formatDate(timesheetData[0].date)}</p>
                </div>
                
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Дата</th>
                                <th>Часы</th>
                                <th>Проект</th>
                                <th>Задача</th>
                                <th>Статус</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${timesheetData.map(entry => `
                                <tr>
                                    <td>${Utils.formatDate(entry.date)}</td>
                                    <td>${entry.hours}</td>
                                    <td>${entry.project}</td>
                                    <td>${entry.task}</td>
                                    <td>
                                        <span class="status-badge status-${entry.status}">
                                            ${entry.status === 'approved' ? 'Утверждено' : 'Ожидает'}
                                        </span>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                        <tfoot>
                            <tr>
                                <td><strong>Итого:</strong></td>
                                <td><strong>${timesheetData.reduce((sum, entry) => sum + entry.hours, 0)}</strong></td>
                                <td colspan="3"></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        `;

        app.showModal(`Табель: ${employee.name}`, content, {
            buttons: [
                {
                    text: 'Закрыть',
                    class: 'btn-secondary',
                    onClick: () => app.closeModal()
                },
                {
                    text: 'Экспорт',
                    class: 'btn-primary',
                    onClick: () => EmployeesModule.exportTimesheet(employeeId, timesheetData)
                }
            ]
        });
    }

    static deleteEmployee(employeeId) {
        app.showModal('Удалить сотрудника', `
            <p>Вы уверены, что хотите удалить этого сотрудника? Это действие нельзя отменить.</p>
        `, {
            buttons: [
                { text: 'Отмена', class: 'btn-secondary', onClick: () => app.closeModal() },
                { 
                    text: 'Удалить', 
                    class: 'btn-danger', 
                    onClick: () => {
                        EmployeesModule.employees = EmployeesModule.employees.filter(e => e.id !== employeeId);
                        EmployeesModule.renderEmployees();
                        app.closeModal();
                        app.showNotification('Сотрудник удален', 'success');
                    }
                }
            ]
        });
    }

    static exportEmployees() {
        const employees = EmployeesModule.getFilteredEmployees();
        const exportData = employees.map(e => ({
            'ФИО': e.name,
            'Должность': e.position,
            'Отдел': e.department,
            'Email': e.email,
            'Телефон': e.phone,
            'Дата приема': Utils.formatDate(e.hireDate),
            'Зарплата': e.salary,
            'Статус': EmployeesModule.getStatusText(e.status)
        }));

        app.exportData(exportData, 'employees-export.csv', 'csv');
        app.showNotification('Сотрудники экспортированы', 'success');
    }

    static exportTimesheet(employeeId, timesheetData) {
        const employee = EmployeesModule.employees.find(e => e.id === employeeId);
        if (!employee) return;

        const exportData = timesheetData.map(entry => ({
            'Дата': Utils.formatDate(entry.date),
            'Часы': entry.hours,
            'Проект': entry.project,
            'Задача': entry.task,
            'Статус': entry.status === 'approved' ? 'Утверждено' : 'Ожидает'
        }));

        app.exportData(exportData, `timesheet-${employee.name}.csv`, 'csv');
        app.showNotification('Табель экспортирован', 'success');
    }

    static clearFilters() {
        EmployeesModule.filters = { department: 'all', status: 'all', search: '' };
        
        const departmentFilter = document.getElementById('department-filter');
        const statusFilter = document.getElementById('status-filter');
        const searchFilter = document.getElementById('search-filter');

        if (departmentFilter) departmentFilter.value = 'all';
        if (statusFilter) statusFilter.value = 'all';
        if (searchFilter) searchFilter.value = '';

        EmployeesModule.renderEmployeesList();
    }

    // Утилиты
    getStatusText(status) {
        const statuses = {
            active: 'Активный',
            inactive: 'Неактивный',
            on_leave: 'В отпуске'
        };
        return statuses[status] || status;
    }

    static getStatusText(status) {
        const statuses = {
            active: 'Активный',
            inactive: 'Неактивный',
            on_leave: 'В отпуске'
        };
        return statuses[status] || status;
    }

    // Поиск
    search(query) {
        this.filters.search = query;
        this.renderEmployeesList();
    }
}

// Инициализация модуля
window.EmployeesModule = new EmployeesModule();