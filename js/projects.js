// Модуль проектов BuildCRM

class ProjectsModule {
    constructor() {
        this.projects = [];
        this.currentProject = null;
        this.filters = {
            status: 'all',
            type: 'all',
            search: ''
        };
    }

    init() {
        this.loadProjects();
        this.renderProjects();
        this.bindEvents();
    }

    async loadProjects() {
        try {
            this.projects = await app.loadData('projects');
            this.renderProjects();
        } catch (error) {
            console.error('Ошибка загрузки проектов:', error);
            app.showNotification('Ошибка загрузки проектов', 'error');
        }
    }

    renderProjects() {
        const container = document.getElementById('projects-section');
        if (!container) return;

        container.innerHTML = `
            <div class="projects-container">
                <!-- Заголовок и действия -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Управление проектами</h2>
                        <p>Создавайте и управляйте строительными проектами</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="ProjectsModule.showCreateProject()">
                            <i class="fas fa-plus"></i> Новый проект
                        </button>
                        <button class="btn btn-secondary" onclick="ProjectsModule.exportProjects()">
                            <i class="fas fa-download"></i> Экспорт
                        </button>
                    </div>
                </div>

                <!-- Фильтры -->
                <div class="filters-section">
                    <div class="filters-grid">
                        <div class="filter-group">
                            <label>Статус</label>
                            <select class="form-control" id="status-filter">
                                <option value="all">Все статусы</option>
                                <option value="active">Активные</option>
                                <option value="pending">Ожидающие</option>
                                <option value="completed">Завершенные</option>
                                <option value="cancelled">Отмененные</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>Тип проекта</label>
                            <select class="form-control" id="type-filter">
                                <option value="all">Все типы</option>
                                <option value="residential">Жилые</option>
                                <option value="commercial">Коммерческие</option>
                                <option value="industrial">Промышленные</option>
                                <option value="infrastructure">Инфраструктура</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>Поиск</label>
                            <input type="text" class="form-control" id="search-filter" placeholder="Поиск по названию...">
                        </div>
                        <div class="filter-group">
                            <label>&nbsp;</label>
                            <button class="btn btn-secondary" onclick="ProjectsModule.clearFilters()">
                                <i class="fas fa-times"></i> Очистить
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Статистика -->
                <div class="stats-section">
                    ${this.renderStats()}
                </div>

                <!-- Список проектов -->
                <div class="projects-list">
                    ${this.renderProjectsList()}
                </div>
            </div>
        `;

        this.bindFilterEvents();
    }

    renderStats() {
        const total = this.projects.length;
        const active = this.projects.filter(p => p.status === 'active').length;
        const completed = this.projects.filter(p => p.status === 'completed').length;
        const totalBudget = this.projects.reduce((sum, p) => sum + (p.budget || 0), 0);

        return `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-project-diagram"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${total}</div>
                        <div class="stat-label">Всего проектов</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon active">
                        <i class="fas fa-play"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${active}</div>
                        <div class="stat-label">Активные</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon completed">
                        <i class="fas fa-check"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${completed}</div>
                        <div class="stat-label">Завершенные</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon budget">
                        <i class="fas fa-ruble-sign"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${Utils.formatCurrency(totalBudget)}</div>
                        <div class="stat-label">Общий бюджет</div>
                    </div>
                </div>
            </div>
        `;
    }

    renderProjectsList() {
        const filteredProjects = this.getFilteredProjects();

        if (filteredProjects.length === 0) {
            return `
                <div class="empty-state">
                    <i class="fas fa-project-diagram"></i>
                    <h3>Проекты не найдены</h3>
                    <p>Попробуйте изменить фильтры или создать новый проект</p>
                    <button class="btn btn-primary" onclick="ProjectsModule.showCreateProject()">
                        <i class="fas fa-plus"></i> Создать проект
                    </button>
                </div>
            `;
        }

        return `
            <div class="projects-grid">
                ${filteredProjects.map(project => this.renderProjectCard(project)).join('')}
            </div>
        `;
    }

    renderProjectCard(project) {
        const progress = project.progress || 0;
        const spentPercent = project.budget ? Math.round((project.spent / project.budget) * 100) : 0;
        const daysLeft = this.calculateDaysLeft(project.endDate);

        return `
            <div class="project-card" data-project-id="${project.id}">
                <div class="project-header">
                    <div class="project-status">
                        <span class="status-badge status-${project.status}">${this.getStatusText(project.status)}</span>
                    </div>
                    <div class="project-actions">
                        <button class="btn-icon" onclick="ProjectsModule.showProjectDetails('${project.id}')">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn-icon" onclick="ProjectsModule.editProject('${project.id}')">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-icon" onclick="ProjectsModule.deleteProject('${project.id}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
                
                <div class="project-content">
                    <h3 class="project-title">${project.name}</h3>
                    <p class="project-client">${project.client}</p>
                    <p class="project-location">
                        <i class="fas fa-map-marker-alt"></i> ${project.location}
                    </p>
                    
                    <div class="project-manager">
                        <i class="fas fa-user"></i> ${project.manager}
                    </div>
                </div>
                
                <div class="project-progress">
                    <div class="progress-header">
                        <span>Прогресс</span>
                        <span>${progress}%</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${progress}%"></div>
                    </div>
                </div>
                
                <div class="project-finance">
                    <div class="finance-item">
                        <span class="finance-label">Бюджет:</span>
                        <span class="finance-value">${Utils.formatCurrency(project.budget)}</span>
                    </div>
                    <div class="finance-item">
                        <span class="finance-label">Потрачено:</span>
                        <span class="finance-value">${Utils.formatCurrency(project.spent)} (${spentPercent}%)</span>
                    </div>
                </div>
                
                <div class="project-dates">
                    <div class="date-item">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Начало: ${Utils.formatDate(project.startDate)}</span>
                    </div>
                    <div class="date-item">
                        <i class="fas fa-calendar-check"></i>
                        <span>Завершение: ${Utils.formatDate(project.endDate)}</span>
                        ${daysLeft > 0 ? `<span class="days-left">(${daysLeft} дн.)</span>` : ''}
                    </div>
                </div>
            </div>
        `;
    }

    getFilteredProjects() {
        return this.projects.filter(project => {
            const statusMatch = this.filters.status === 'all' || project.status === this.filters.status;
            const typeMatch = this.filters.type === 'all' || project.type === this.filters.type;
            const searchMatch = !this.filters.search || 
                project.name.toLowerCase().includes(this.filters.search.toLowerCase()) ||
                project.client.toLowerCase().includes(this.filters.search.toLowerCase());
            
            return statusMatch && typeMatch && searchMatch;
        });
    }

    bindEvents() {
        // События уже привязаны в renderProjects
    }

    bindFilterEvents() {
        const statusFilter = document.getElementById('status-filter');
        const typeFilter = document.getElementById('type-filter');
        const searchFilter = document.getElementById('search-filter');

        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                this.filters.status = e.target.value;
                this.renderProjectsList();
            });
        }

        if (typeFilter) {
            typeFilter.addEventListener('change', (e) => {
                this.filters.type = e.target.value;
                this.renderProjectsList();
            });
        }

        if (searchFilter) {
            searchFilter.addEventListener('input', Utils.debounce((e) => {
                this.filters.search = e.target.value;
                this.renderProjectsList();
            }, 300));
        }
    }

    // Статические методы для модальных окон
    static showCreateProject() {
        const content = `
            <form id="create-project-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="project-name">Название проекта *</label>
                        <input type="text" id="project-name" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="project-type">Тип проекта *</label>
                        <select id="project-type" class="form-control" required>
                            <option value="">Выберите тип</option>
                            <option value="residential">Жилой</option>
                            <option value="commercial">Коммерческий</option>
                            <option value="industrial">Промышленный</option>
                            <option value="infrastructure">Инфраструктура</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="project-client">Клиент *</label>
                        <input type="text" id="project-client" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="project-manager">Менеджер проекта *</label>
                        <input type="text" id="project-manager" class="form-control" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="project-location">Местоположение *</label>
                    <input type="text" id="project-location" class="form-control" required>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="project-budget">Бюджет (₽) *</label>
                        <input type="number" id="project-budget" class="form-control" required min="0">
                    </div>
                    <div class="form-group">
                        <label for="project-status">Статус *</label>
                        <select id="project-status" class="form-control" required>
                            <option value="pending">Ожидающий</option>
                            <option value="active">Активный</option>
                            <option value="completed">Завершенный</option>
                            <option value="cancelled">Отмененный</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="project-start">Дата начала *</label>
                        <input type="date" id="project-start" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="project-end">Дата завершения *</label>
                        <input type="date" id="project-end" class="form-control" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="project-description">Описание</label>
                    <textarea id="project-description" class="form-control" rows="4"></textarea>
                </div>
            </form>
        `;

        app.showModal('Создать новый проект', content, {
            buttons: [
                {
                    text: 'Отмена',
                    class: 'btn-secondary',
                    onClick: () => app.closeModal()
                },
                {
                    text: 'Создать',
                    class: 'btn-primary',
                    onClick: () => ProjectsModule.createProject()
                }
            ]
        });
    }

    static async createProject() {
        const form = document.getElementById('create-project-form');
        if (!form) return;

        const formData = new FormData(form);
        const projectData = {
            id: Utils.generateId(),
            name: document.getElementById('project-name').value,
            type: document.getElementById('project-type').value,
            client: document.getElementById('project-client').value,
            manager: document.getElementById('project-manager').value,
            location: document.getElementById('project-location').value,
            budget: parseFloat(document.getElementById('project-budget').value),
            status: document.getElementById('project-status').value,
            startDate: document.getElementById('project-start').value,
            endDate: document.getElementById('project-end').value,
            description: document.getElementById('project-description').value,
            spent: 0,
            progress: 0,
            createdAt: new Date().toISOString()
        };

        try {
            // В реальном приложении здесь был бы API запрос
            ProjectsModule.projects.push(projectData);
            ProjectsModule.renderProjects();
            app.closeModal();
            app.showNotification('Проект успешно создан', 'success');
        } catch (error) {
            app.showNotification('Ошибка создания проекта', 'error');
        }
    }

    static showProjectDetails(projectId) {
        const project = ProjectsModule.projects.find(p => p.id === projectId);
        if (!project) return;

        const content = `
            <div class="project-details">
                <div class="detail-section">
                    <h4>Основная информация</h4>
                    <div class="detail-grid">
                        <div class="detail-item">
                            <span class="detail-label">Название:</span>
                            <span class="detail-value">${project.name}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Клиент:</span>
                            <span class="detail-value">${project.client}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Менеджер:</span>
                            <span class="detail-value">${project.manager}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Местоположение:</span>
                            <span class="detail-value">${project.location}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Статус:</span>
                            <span class="detail-value">
                                <span class="status-badge status-${project.status}">${ProjectsModule.getStatusText(project.status)}</span>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Тип:</span>
                            <span class="detail-value">${ProjectsModule.getTypeText(project.type)}</span>
                        </div>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h4>Финансы</h4>
                    <div class="detail-grid">
                        <div class="detail-item">
                            <span class="detail-label">Бюджет:</span>
                            <span class="detail-value">${Utils.formatCurrency(project.budget)}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Потрачено:</span>
                            <span class="detail-value">${Utils.formatCurrency(project.spent)}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Остаток:</span>
                            <span class="detail-value">${Utils.formatCurrency(project.budget - project.spent)}</span>
                        </div>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h4>Прогресс</h4>
                    <div class="progress-info">
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${project.progress}%"></div>
                        </div>
                        <span class="progress-text">${project.progress}% завершено</span>
                    </div>
                </div>
                
                ${project.description ? `
                    <div class="detail-section">
                        <h4>Описание</h4>
                        <p>${project.description}</p>
                    </div>
                ` : ''}
            </div>
        `;

        app.showModal(`Проект: ${project.name}`, content, {
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
                        ProjectsModule.editProject(projectId);
                    }
                }
            ]
        });
    }

    static editProject(projectId) {
        const project = ProjectsModule.projects.find(p => p.id === projectId);
        if (!project) return;

        // Аналогично createProject, но с предзаполненными данными
        app.showModal('Редактировать проект', 'Форма редактирования', {
            buttons: [
                { text: 'Отмена', class: 'btn-secondary', onClick: () => app.closeModal() },
                { text: 'Сохранить', class: 'btn-primary', onClick: () => app.closeModal() }
            ]
        });
    }

    static deleteProject(projectId) {
        app.showModal('Удалить проект', `
            <p>Вы уверены, что хотите удалить этот проект? Это действие нельзя отменить.</p>
        `, {
            buttons: [
                { text: 'Отмена', class: 'btn-secondary', onClick: () => app.closeModal() },
                { 
                    text: 'Удалить', 
                    class: 'btn-danger', 
                    onClick: () => {
                        ProjectsModule.projects = ProjectsModule.projects.filter(p => p.id !== projectId);
                        ProjectsModule.renderProjects();
                        app.closeModal();
                        app.showNotification('Проект удален', 'success');
                    }
                }
            ]
        });
    }

    static exportProjects() {
        const projects = ProjectsModule.getFilteredProjects();
        const exportData = projects.map(p => ({
            'Название': p.name,
            'Клиент': p.client,
            'Менеджер': p.manager,
            'Статус': ProjectsModule.getStatusText(p.status),
            'Бюджет': p.budget,
            'Потрачено': p.spent,
            'Прогресс': p.progress + '%',
            'Дата начала': Utils.formatDate(p.startDate),
            'Дата завершения': Utils.formatDate(p.endDate)
        }));

        app.exportData(exportData, 'projects-export.csv', 'csv');
        app.showNotification('Проекты экспортированы', 'success');
    }

    static clearFilters() {
        ProjectsModule.filters = { status: 'all', type: 'all', search: '' };
        
        const statusFilter = document.getElementById('status-filter');
        const typeFilter = document.getElementById('type-filter');
        const searchFilter = document.getElementById('search-filter');

        if (statusFilter) statusFilter.value = 'all';
        if (typeFilter) typeFilter.value = 'all';
        if (searchFilter) searchFilter.value = '';

        ProjectsModule.renderProjectsList();
    }

    // Утилиты
    getStatusText(status) {
        const statuses = {
            active: 'Активный',
            pending: 'Ожидающий',
            completed: 'Завершенный',
            cancelled: 'Отмененный'
        };
        return statuses[status] || status;
    }

    static getStatusText(status) {
        const statuses = {
            active: 'Активный',
            pending: 'Ожидающий',
            completed: 'Завершенный',
            cancelled: 'Отмененный'
        };
        return statuses[status] || status;
    }

    static getTypeText(type) {
        const types = {
            residential: 'Жилой',
            commercial: 'Коммерческий',
            industrial: 'Промышленный',
            infrastructure: 'Инфраструктура'
        };
        return types[type] || type;
    }

    calculateDaysLeft(endDate) {
        const end = new Date(endDate);
        const today = new Date();
        const diffTime = end - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays > 0 ? diffDays : 0;
    }

    // Поиск
    search(query) {
        this.filters.search = query;
        this.renderProjectsList();
    }
}

// Инициализация модуля
window.ProjectsModule = new ProjectsModule();