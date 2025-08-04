// Модуль смет BuildCRM

class EstimatesModule {
    constructor() {
        this.estimates = [];
        this.currentEstimate = null;
        this.filters = {
            status: 'all',
            project: 'all',
            search: ''
        };
    }

    init() {
        this.loadEstimates();
        this.renderEstimates();
        this.bindEvents();
    }

    async loadEstimates() {
        try {
            this.estimates = await app.loadData('estimates');
            this.renderEstimates();
        } catch (error) {
            console.error('Ошибка загрузки смет:', error);
            app.showNotification('Ошибка загрузки смет', 'error');
        }
    }

    renderEstimates() {
        const container = document.getElementById('estimates-section');
        if (!container) return;

        container.innerHTML = `
            <div class="estimates-container">
                <!-- Заголовок и действия -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Управление сметами</h2>
                        <p>Создавайте и управляйте сметами для строительных проектов</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="EstimatesModule.showCreateEstimate()">
                            <i class="fas fa-plus"></i> Новая смета
                        </button>
                        <button class="btn btn-secondary" onclick="EstimatesModule.exportEstimates()">
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
                                <option value="draft">Черновик</option>
                                <option value="pending">На рассмотрении</option>
                                <option value="approved">Утверждена</option>
                                <option value="rejected">Отклонена</option>
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
                            <label>Поиск</label>
                            <input type="text" class="form-control" id="search-filter" placeholder="Поиск по названию...">
                        </div>
                        <div class="filter-group">
                            <label>&nbsp;</label>
                            <button class="btn btn-secondary" onclick="EstimatesModule.clearFilters()">
                                <i class="fas fa-times"></i> Очистить
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Статистика -->
                <div class="stats-section">
                    ${this.renderStats()}
                </div>

                <!-- Список смет -->
                <div class="estimates-list">
                    ${this.renderEstimatesList()}
                </div>
            </div>
        `;

        this.bindFilterEvents();
    }

    renderStats() {
        const total = this.estimates.length;
        const approved = this.estimates.filter(e => e.status === 'approved').length;
        const pending = this.estimates.filter(e => e.status === 'pending').length;
        const totalAmount = this.estimates.reduce((sum, e) => sum + (e.totalAmount || 0), 0);

        return `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-calculator"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${total}</div>
                        <div class="stat-label">Всего смет</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon approved">
                        <i class="fas fa-check"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${approved}</div>
                        <div class="stat-label">Утвержденные</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon pending">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${pending}</div>
                        <div class="stat-label">На рассмотрении</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon budget">
                        <i class="fas fa-ruble-sign"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">${Utils.formatCurrency(totalAmount)}</div>
                        <div class="stat-label">Общая сумма</div>
                    </div>
                </div>
            </div>
        `;
    }

    renderEstimatesList() {
        const filteredEstimates = this.getFilteredEstimates();

        if (filteredEstimates.length === 0) {
            return `
                <div class="empty-state">
                    <i class="fas fa-calculator"></i>
                    <h3>Сметы не найдены</h3>
                    <p>Попробуйте изменить фильтры или создать новую смету</p>
                    <button class="btn btn-primary" onclick="EstimatesModule.showCreateEstimate()">
                        <i class="fas fa-plus"></i> Создать смету
                    </button>
                </div>
            `;
        }

        return `
            <div class="estimates-grid">
                ${filteredEstimates.map(estimate => this.renderEstimateCard(estimate)).join('')}
            </div>
        `;
    }

    renderEstimateCard(estimate) {
        const statusColors = {
            draft: '#6b7280',
            pending: '#f59e0b',
            approved: '#10b981',
            rejected: '#ef4444'
        };

        return `
            <div class="estimate-card" data-estimate-id="${estimate.id}">
                <div class="estimate-header">
                    <div class="estimate-status">
                        <span class="status-badge status-${estimate.status}" style="border-left: 4px solid ${statusColors[estimate.status]}">
                            ${this.getStatusText(estimate.status)}
                        </span>
                    </div>
                    <div class="estimate-actions">
                        <button class="btn-icon" onclick="EstimatesModule.showEstimateDetails('${estimate.id}')">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn-icon" onclick="EstimatesModule.editEstimate('${estimate.id}')">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-icon" onclick="EstimatesModule.duplicateEstimate('${estimate.id}')">
                            <i class="fas fa-copy"></i>
                        </button>
                        <button class="btn-icon" onclick="EstimatesModule.deleteEstimate('${estimate.id}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
                
                <div class="estimate-content">
                    <h3 class="estimate-title">${estimate.name}</h3>
                    <p class="estimate-project">${estimate.projectName}</p>
                    
                    <div class="estimate-info">
                        <div class="info-item">
                            <i class="fas fa-user"></i>
                            <span>${estimate.createdBy}</span>
                        </div>
                        <div class="info-item">
                            <i class="fas fa-calendar"></i>
                            <span>${Utils.formatDate(estimate.createdAt)}</span>
                        </div>
                    </div>
                </div>
                
                <div class="estimate-summary">
                    <div class="summary-item">
                        <span class="summary-label">Позиций:</span>
                        <span class="summary-value">${estimate.items?.length || 0}</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">Сумма:</span>
                        <span class="summary-value total">${Utils.formatCurrency(estimate.totalAmount)}</span>
                    </div>
                </div>
                
                <div class="estimate-footer">
                    <button class="btn btn-sm btn-primary" onclick="EstimatesModule.printEstimate('${estimate.id}')">
                        <i class="fas fa-print"></i> Печать
                    </button>
                    <button class="btn btn-sm btn-secondary" onclick="EstimatesModule.exportEstimate('${estimate.id}')">
                        <i class="fas fa-download"></i> PDF
                    </button>
                </div>
            </div>
        `;
    }

    getFilteredEstimates() {
        return this.estimates.filter(estimate => {
            const statusMatch = this.filters.status === 'all' || estimate.status === this.filters.status;
            const projectMatch = this.filters.project === 'all' || estimate.projectId === this.filters.project;
            const searchMatch = !this.filters.search || 
                estimate.name.toLowerCase().includes(this.filters.search.toLowerCase()) ||
                estimate.projectName.toLowerCase().includes(this.filters.search.toLowerCase());
            
            return statusMatch && projectMatch && searchMatch;
        });
    }

    bindEvents() {
        // События уже привязаны в renderEstimates
    }

    bindFilterEvents() {
        const statusFilter = document.getElementById('status-filter');
        const projectFilter = document.getElementById('project-filter');
        const searchFilter = document.getElementById('search-filter');

        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                this.filters.status = e.target.value;
                this.renderEstimatesList();
            });
        }

        if (projectFilter) {
            projectFilter.addEventListener('change', (e) => {
                this.filters.project = e.target.value;
                this.renderEstimatesList();
            });
        }

        if (searchFilter) {
            searchFilter.addEventListener('input', Utils.debounce((e) => {
                this.filters.search = e.target.value;
                this.renderEstimatesList();
            }, 300));
        }
    }

    // Статические методы для модальных окон
    static showCreateEstimate() {
        const content = `
            <form id="create-estimate-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="estimate-name">Название сметы *</label>
                        <input type="text" id="estimate-name" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="estimate-project">Проект *</label>
                        <select id="estimate-project" class="form-control" required>
                            <option value="">Выберите проект</option>
                            <option value="1">Жилой комплекс "Солнечный"</option>
                            <option value="2">Торговый центр "МегаМолл"</option>
                            <option value="3">Завод по производству бетона</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="estimate-description">Описание</label>
                    <textarea id="estimate-description" class="form-control" rows="3"></textarea>
                </div>
                
                <div class="form-group">
                    <label>Позиции сметы</label>
                    <div id="estimate-items" class="estimate-items">
                        <div class="estimate-item">
                            <div class="item-row">
                                <input type="text" class="form-control" placeholder="Наименование работы/материала" required>
                                <input type="number" class="form-control" placeholder="Кол-во" required min="0" step="0.01">
                                <select class="form-control">
                                    <option value="шт">шт</option>
                                    <option value="м²">м²</option>
                                    <option value="м³">м³</option>
                                    <option value="м">м</option>
                                    <option value="кг">кг</option>
                                    <option value="т">т</option>
                                </select>
                                <input type="number" class="form-control" placeholder="Цена за ед." required min="0" step="0.01">
                                <input type="number" class="form-control" placeholder="Сумма" readonly>
                                <button type="button" class="btn btn-sm btn-danger" onclick="this.parentElement.parentElement.remove()">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-sm btn-secondary" onclick="EstimatesModule.addEstimateItem()">
                        <i class="fas fa-plus"></i> Добавить позицию
                    </button>
                </div>
                
                <div class="estimate-total">
                    <div class="total-row">
                        <span>Итого:</span>
                        <span id="estimate-total-amount">0 ₽</span>
                    </div>
                </div>
            </form>
        `;

        app.showModal('Создать новую смету', content, {
            buttons: [
                {
                    text: 'Отмена',
                    class: 'btn-secondary',
                    onClick: () => app.closeModal()
                },
                {
                    text: 'Создать',
                    class: 'btn-primary',
                    onClick: () => EstimatesModule.createEstimate()
                }
            ]
        });

        // Привязываем события для расчета сумм
        EstimatesModule.bindEstimateCalculationEvents();
    }

    static addEstimateItem() {
        const itemsContainer = document.getElementById('estimate-items');
        const newItem = document.createElement('div');
        newItem.className = 'estimate-item';
        newItem.innerHTML = `
            <div class="item-row">
                <input type="text" class="form-control" placeholder="Наименование работы/материала" required>
                <input type="number" class="form-control quantity" placeholder="Кол-во" required min="0" step="0.01">
                <select class="form-control">
                    <option value="шт">шт</option>
                    <option value="м²">м²</option>
                    <option value="м³">м³</option>
                    <option value="м">м</option>
                    <option value="кг">кг</option>
                    <option value="т">т</option>
                </select>
                <input type="number" class="form-control price" placeholder="Цена за ед." required min="0" step="0.01">
                <input type="number" class="form-control amount" placeholder="Сумма" readonly>
                <button type="button" class="btn btn-sm btn-danger" onclick="this.parentElement.parentElement.remove(); EstimatesModule.calculateEstimateTotal()">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `;
        itemsContainer.appendChild(newItem);
    }

    static bindEstimateCalculationEvents() {
        const itemsContainer = document.getElementById('estimate-items');
        if (!itemsContainer) return;

        itemsContainer.addEventListener('input', (e) => {
            if (e.target.classList.contains('quantity') || e.target.classList.contains('price')) {
                EstimatesModule.calculateItemAmount(e.target);
                EstimatesModule.calculateEstimateTotal();
            }
        });
    }

    static calculateItemAmount(input) {
        const row = input.closest('.item-row');
        const quantity = parseFloat(row.querySelector('.quantity')?.value) || 0;
        const price = parseFloat(row.querySelector('.price')?.value) || 0;
        const amount = quantity * price;
        
        const amountInput = row.querySelector('.amount');
        if (amountInput) {
            amountInput.value = amount.toFixed(2);
        }
    }

    static calculateEstimateTotal() {
        const amounts = Array.from(document.querySelectorAll('.amount')).map(input => parseFloat(input.value) || 0);
        const total = amounts.reduce((sum, amount) => sum + amount, 0);
        
        const totalElement = document.getElementById('estimate-total-amount');
        if (totalElement) {
            totalElement.textContent = Utils.formatCurrency(total);
        }
    }

    static async createEstimate() {
        const form = document.getElementById('create-estimate-form');
        if (!form) return;

        const items = Array.from(document.querySelectorAll('.estimate-item')).map(item => {
            const row = item.querySelector('.item-row');
            return {
                name: row.querySelector('input[type="text"]').value,
                quantity: parseFloat(row.querySelector('.quantity').value) || 0,
                unit: row.querySelector('select').value,
                price: parseFloat(row.querySelector('.price').value) || 0,
                amount: parseFloat(row.querySelector('.amount').value) || 0
            };
        });

        const estimateData = {
            id: Utils.generateId(),
            name: document.getElementById('estimate-name').value,
            projectId: document.getElementById('estimate-project').value,
            projectName: document.getElementById('estimate-project').options[document.getElementById('estimate-project').selectedIndex].text,
            description: document.getElementById('estimate-description').value,
            items: items,
            totalAmount: items.reduce((sum, item) => sum + item.amount, 0),
            status: 'draft',
            createdBy: auth.getCurrentUser()?.name || 'Пользователь',
            createdAt: new Date().toISOString()
        };

        try {
            EstimatesModule.estimates.push(estimateData);
            EstimatesModule.renderEstimates();
            app.closeModal();
            app.showNotification('Смета успешно создана', 'success');
        } catch (error) {
            app.showNotification('Ошибка создания сметы', 'error');
        }
    }

    static showEstimateDetails(estimateId) {
        const estimate = EstimatesModule.estimates.find(e => e.id === estimateId);
        if (!estimate) return;

        const content = `
            <div class="estimate-details">
                <div class="detail-section">
                    <h4>Основная информация</h4>
                    <div class="detail-grid">
                        <div class="detail-item">
                            <span class="detail-label">Название:</span>
                            <span class="detail-value">${estimate.name}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Проект:</span>
                            <span class="detail-value">${estimate.projectName}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Статус:</span>
                            <span class="detail-value">
                                <span class="status-badge status-${estimate.status}">${EstimatesModule.getStatusText(estimate.status)}</span>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Создал:</span>
                            <span class="detail-value">${estimate.createdBy}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Дата создания:</span>
                            <span class="detail-value">${Utils.formatDate(estimate.createdAt)}</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Общая сумма:</span>
                            <span class="detail-value total">${Utils.formatCurrency(estimate.totalAmount)}</span>
                        </div>
                    </div>
                </div>
                
                ${estimate.description ? `
                    <div class="detail-section">
                        <h4>Описание</h4>
                        <p>${estimate.description}</p>
                    </div>
                ` : ''}
                
                <div class="detail-section">
                    <h4>Позиции сметы</h4>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>№</th>
                                    <th>Наименование</th>
                                    <th>Количество</th>
                                    <th>Ед. изм.</th>
                                    <th>Цена за ед.</th>
                                    <th>Сумма</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${estimate.items.map((item, index) => `
                                    <tr>
                                        <td>${index + 1}</td>
                                        <td>${item.name}</td>
                                        <td>${item.quantity}</td>
                                        <td>${item.unit}</td>
                                        <td>${Utils.formatCurrency(item.price)}</td>
                                        <td>${Utils.formatCurrency(item.amount)}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="5" class="text-right"><strong>Итого:</strong></td>
                                    <td><strong>${Utils.formatCurrency(estimate.totalAmount)}</strong></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        `;

        app.showModal(`Смета: ${estimate.name}`, content, {
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
                        EstimatesModule.editEstimate(estimateId);
                    }
                },
                {
                    text: 'Печать',
                    class: 'btn-success',
                    onClick: () => EstimatesModule.printEstimate(estimateId)
                }
            ]
        });
    }

    static editEstimate(estimateId) {
        // Реализация редактирования сметы
        app.showModal('Редактировать смету', 'Форма редактирования', {
            buttons: [
                { text: 'Отмена', class: 'btn-secondary', onClick: () => app.closeModal() },
                { text: 'Сохранить', class: 'btn-primary', onClick: () => app.closeModal() }
            ]
        });
    }

    static duplicateEstimate(estimateId) {
        const estimate = EstimatesModule.estimates.find(e => e.id === estimateId);
        if (!estimate) return;

        const duplicatedEstimate = {
            ...estimate,
            id: Utils.generateId(),
            name: `${estimate.name} (копия)`,
            status: 'draft',
            createdAt: new Date().toISOString()
        };

        EstimatesModule.estimates.push(duplicatedEstimate);
        EstimatesModule.renderEstimates();
        app.showNotification('Смета скопирована', 'success');
    }

    static deleteEstimate(estimateId) {
        app.showModal('Удалить смету', `
            <p>Вы уверены, что хотите удалить эту смету? Это действие нельзя отменить.</p>
        `, {
            buttons: [
                { text: 'Отмена', class: 'btn-secondary', onClick: () => app.closeModal() },
                { 
                    text: 'Удалить', 
                    class: 'btn-danger', 
                    onClick: () => {
                        EstimatesModule.estimates = EstimatesModule.estimates.filter(e => e.id !== estimateId);
                        EstimatesModule.renderEstimates();
                        app.closeModal();
                        app.showNotification('Смета удалена', 'success');
                    }
                }
            ]
        });
    }

    static printEstimate(estimateId) {
        const estimate = EstimatesModule.estimates.find(e => e.id === estimateId);
        if (!estimate) return;

        const printContent = `
            <div class="print-estimate">
                <h2>Смета: ${estimate.name}</h2>
                <p><strong>Проект:</strong> ${estimate.projectName}</p>
                <p><strong>Дата:</strong> ${Utils.formatDate(estimate.createdAt)}</p>
                
                <table class="print-table">
                    <thead>
                        <tr>
                            <th>№</th>
                            <th>Наименование</th>
                            <th>Количество</th>
                            <th>Ед. изм.</th>
                            <th>Цена за ед.</th>
                            <th>Сумма</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${estimate.items.map((item, index) => `
                            <tr>
                                <td>${index + 1}</td>
                                <td>${item.name}</td>
                                <td>${item.quantity}</td>
                                <td>${item.unit}</td>
                                <td>${Utils.formatCurrency(item.price)}</td>
                                <td>${Utils.formatCurrency(item.amount)}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="5"><strong>Итого:</strong></td>
                            <td><strong>${Utils.formatCurrency(estimate.totalAmount)}</strong></td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        `;

        Utils.downloadFile(printContent, `estimate-${estimate.name}.html`, 'text/html');
        app.showNotification('Смета отправлена на печать', 'success');
    }

    static exportEstimate(estimateId) {
        const estimate = EstimatesModule.estimates.find(e => e.id === estimateId);
        if (!estimate) return;

        const exportData = [{
            'Название': estimate.name,
            'Проект': estimate.projectName,
            'Статус': EstimatesModule.getStatusText(estimate.status),
            'Сумма': estimate.totalAmount,
            'Дата создания': Utils.formatDate(estimate.createdAt)
        }];

        app.exportData(exportData, `estimate-${estimate.name}.csv`, 'csv');
        app.showNotification('Смета экспортирована', 'success');
    }

    static exportEstimates() {
        const estimates = EstimatesModule.getFilteredEstimates();
        const exportData = estimates.map(e => ({
            'Название': e.name,
            'Проект': e.projectName,
            'Статус': EstimatesModule.getStatusText(e.status),
            'Сумма': e.totalAmount,
            'Создал': e.createdBy,
            'Дата создания': Utils.formatDate(e.createdAt)
        }));

        app.exportData(exportData, 'estimates-export.csv', 'csv');
        app.showNotification('Сметы экспортированы', 'success');
    }

    static clearFilters() {
        EstimatesModule.filters = { status: 'all', project: 'all', search: '' };
        
        const statusFilter = document.getElementById('status-filter');
        const projectFilter = document.getElementById('project-filter');
        const searchFilter = document.getElementById('search-filter');

        if (statusFilter) statusFilter.value = 'all';
        if (projectFilter) projectFilter.value = 'all';
        if (searchFilter) searchFilter.value = '';

        EstimatesModule.renderEstimatesList();
    }

    // Утилиты
    getStatusText(status) {
        const statuses = {
            draft: 'Черновик',
            pending: 'На рассмотрении',
            approved: 'Утверждена',
            rejected: 'Отклонена'
        };
        return statuses[status] || status;
    }

    static getStatusText(status) {
        const statuses = {
            draft: 'Черновик',
            pending: 'На рассмотрении',
            approved: 'Утверждена',
            rejected: 'Отклонена'
        };
        return statuses[status] || status;
    }

    // Поиск
    search(query) {
        this.filters.search = query;
        this.renderEstimatesList();
    }
}

// Инициализация модуля
window.EstimatesModule = new EstimatesModule();