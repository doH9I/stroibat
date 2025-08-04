// Модуль дашборда BuildCRM

class DashboardModule {
    constructor() {
        this.data = {};
        this.charts = {};
    }

    init() {
        this.loadDashboardData();
        this.renderDashboard();
    }

    async loadDashboardData() {
        try {
            const [projects, employees, estimates, analytics] = await Promise.all([
                app.loadData('projects'),
                app.loadData('employees'),
                app.loadData('estimates'),
                app.loadData('analytics')
            ]);

            this.data = {
                projects,
                employees,
                estimates,
                analytics
            };
        } catch (error) {
            console.error('Ошибка загрузки данных дашборда:', error);
        }
    }

    renderDashboard() {
        const container = document.getElementById('dashboard-section');
        if (!container) return;

        container.innerHTML = `
            <div class="dashboard-container">
                <!-- Ключевые метрики -->
                <div class="metrics-grid">
                    ${this.renderMetrics()}
                </div>

                <!-- Графики и аналитика -->
                <div class="charts-grid">
                    <div class="chart-row">
                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Статус проектов</h3>
                                <div class="chart-actions">
                                    <button class="btn btn-sm btn-secondary" onclick="DashboardModule.exportChart('projects-status')">
                                        <i class="fas fa-download"></i> Экспорт
                                    </button>
                                </div>
                            </div>
                            <div class="chart-container">
                                <canvas id="projects-status-chart"></canvas>
                            </div>
                        </div>

                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Финансовые показатели</h3>
                                <div class="chart-actions">
                                    <button class="btn btn-sm btn-secondary" onclick="DashboardModule.exportChart('financial')">
                                        <i class="fas fa-download"></i> Экспорт
                                    </button>
                                </div>
                            </div>
                            <div class="chart-container">
                                <canvas id="financial-chart"></canvas>
                            </div>
                        </div>
                    </div>

                    <div class="chart-row">
                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Прогресс проектов</h3>
                                <div class="chart-actions">
                                    <button class="btn btn-sm btn-secondary" onclick="DashboardModule.exportChart('progress')">
                                        <i class="fas fa-download"></i> Экспорт
                                    </button>
                                </div>
                            </div>
                            <div class="chart-container">
                                <canvas id="progress-chart"></canvas>
                            </div>
                        </div>

                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Активность сотрудников</h3>
                                <div class="chart-actions">
                                    <button class="btn btn-sm btn-secondary" onclick="DashboardModule.exportChart('activity')">
                                        <i class="fas fa-download"></i> Экспорт
                                    </button>
                                </div>
                            </div>
                            <div class="chart-container">
                                <canvas id="activity-chart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Последние активности -->
                <div class="activity-section">
                    <div class="section-header">
                        <h3>Последние активности</h3>
                        <button class="btn btn-primary" onclick="DashboardModule.showAllActivities()">
                            <i class="fas fa-list"></i> Все активности
                        </button>
                    </div>
                    <div class="activity-list">
                        ${this.renderRecentActivities()}
                    </div>
                </div>

                <!-- Быстрые действия -->
                <div class="quick-actions">
                    <div class="section-header">
                        <h3>Быстрые действия</h3>
                    </div>
                    <div class="actions-grid">
                        ${this.renderQuickActions()}
                    </div>
                </div>
            </div>
        `;

        this.initCharts();
    }

    renderMetrics() {
        const analytics = this.data.analytics || {};
        const projects = this.data.projects || [];
        const employees = this.data.employees || [];

        const totalRevenue = projects.reduce((sum, project) => sum + (project.spent || 0), 0);
        const activeProjects = projects.filter(p => p.status === 'active').length;
        const totalEmployees = employees.length;

        return `
            <div class="metric-card">
                <div class="metric-icon">
                    <i class="fas fa-chart-line"></i>
                </div>
                <div class="metric-content">
                    <h4>Общая выручка</h4>
                    <div class="metric-value">${Utils.formatCurrency(totalRevenue)}</div>
                    <div class="metric-change positive">
                        <i class="fas fa-arrow-up"></i> +${analytics.revenue?.growth || 0}%
                    </div>
                </div>
            </div>

            <div class="metric-card">
                <div class="metric-icon">
                    <i class="fas fa-project-diagram"></i>
                </div>
                <div class="metric-content">
                    <h4>Активные проекты</h4>
                    <div class="metric-value">${activeProjects}</div>
                    <div class="metric-change positive">
                        <i class="fas fa-arrow-up"></i> +2 за месяц
                    </div>
                </div>
            </div>

            <div class="metric-card">
                <div class="metric-icon">
                    <i class="fas fa-users"></i>
                </div>
                <div class="metric-content">
                    <h4>Сотрудники</h4>
                    <div class="metric-value">${totalEmployees}</div>
                    <div class="metric-change positive">
                        <i class="fas fa-arrow-up"></i> +3 за месяц
                    </div>
                </div>
            </div>

            <div class="metric-card">
                <div class="metric-icon">
                    <i class="fas fa-tachometer-alt"></i>
                </div>
                <div class="metric-content">
                    <h4>Эффективность</h4>
                    <div class="metric-value">${analytics.efficiency?.average || 0}%</div>
                    <div class="metric-change positive">
                        <i class="fas fa-arrow-up"></i> +${analytics.efficiency?.trend || 0}%
                    </div>
                </div>
            </div>
        `;
    }

    renderRecentActivities() {
        const activities = this.generateRecentActivities();
        
        return activities.map(activity => `
            <div class="activity-item">
                <div class="activity-icon ${activity.type}">
                    <i class="${activity.icon}"></i>
                </div>
                <div class="activity-content">
                    <div class="activity-title">${activity.title}</div>
                    <div class="activity-time">${activity.time}</div>
                </div>
                <div class="activity-status ${activity.status}">
                    <span class="status-dot"></span>
                </div>
            </div>
        `).join('');
    }

    renderQuickActions() {
        const actions = [
            {
                title: 'Новый проект',
                icon: 'fas fa-plus',
                action: () => app.navigateToSection('projects'),
                color: '#667eea'
            },
            {
                title: 'Создать смету',
                icon: 'fas fa-calculator',
                action: () => app.navigateToSection('estimates'),
                color: '#10b981'
            },
            {
                title: 'Добавить сотрудника',
                icon: 'fas fa-user-plus',
                action: () => app.navigateToSection('employees'),
                color: '#f59e0b'
            },
            {
                title: 'Отчеты',
                icon: 'fas fa-file-alt',
                action: () => app.navigateToSection('reports'),
                color: '#ef4444'
            }
        ];

        return actions.map(action => `
            <div class="action-card" onclick="${action.action.toString()}()" style="--action-color: ${action.color}">
                <div class="action-icon">
                    <i class="${action.icon}"></i>
                </div>
                <div class="action-title">${action.title}</div>
            </div>
        `).join('');
    }

    generateRecentActivities() {
        return [
            {
                type: 'project',
                icon: 'fas fa-project-diagram',
                title: 'Проект "Жилой комплекс Солнечный" обновлен',
                time: '2 минуты назад',
                status: 'completed'
            },
            {
                type: 'estimate',
                icon: 'fas fa-calculator',
                title: 'Создана новая смета для проекта "МегаМолл"',
                time: '15 минут назад',
                status: 'pending'
            },
            {
                type: 'employee',
                icon: 'fas fa-user',
                title: 'Сотрудник Петрова А.С. отметилась в табеле',
                time: '1 час назад',
                status: 'completed'
            },
            {
                type: 'financial',
                icon: 'fas fa-chart-line',
                title: 'Получен платеж по проекту "Завод бетона"',
                time: '3 часа назад',
                status: 'completed'
            },
            {
                type: 'notification',
                icon: 'fas fa-bell',
                title: 'Напоминание: дедлайн проекта "Солнечный" через 5 дней',
                time: '5 часов назад',
                status: 'warning'
            }
        ];
    }

    initCharts() {
        this.initProjectsStatusChart();
        this.initFinancialChart();
        this.initProgressChart();
        this.initActivityChart();
    }

    initProjectsStatusChart() {
        const ctx = document.getElementById('projects-status-chart');
        if (!ctx) return;

        const projects = this.data.projects || [];
        const statusCounts = {
            active: projects.filter(p => p.status === 'active').length,
            pending: projects.filter(p => p.status === 'pending').length,
            completed: projects.filter(p => p.status === 'completed').length,
            cancelled: projects.filter(p => p.status === 'cancelled').length
        };

        this.charts.projectsStatus = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Активные', 'Ожидающие', 'Завершенные', 'Отмененные'],
                datasets: [{
                    data: [statusCounts.active, statusCounts.pending, statusCounts.completed, statusCounts.cancelled],
                    backgroundColor: ['#10b981', '#f59e0b', '#3b82f6', '#ef4444'],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    initFinancialChart() {
        const ctx = document.getElementById('financial-chart');
        if (!ctx) return;

        const projects = this.data.projects || [];
        const labels = projects.map(p => p.name.substring(0, 15) + '...');
        const budgets = projects.map(p => p.budget || 0);
        const spent = projects.map(p => p.spent || 0);

        this.charts.financial = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Бюджет',
                    data: budgets,
                    backgroundColor: '#3b82f6',
                    borderColor: '#3b82f6',
                    borderWidth: 1
                }, {
                    label: 'Потрачено',
                    data: spent,
                    backgroundColor: '#10b981',
                    borderColor: '#10b981',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return Utils.formatCurrency(value);
                            }
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top'
                    }
                }
            }
        });
    }

    initProgressChart() {
        const ctx = document.getElementById('progress-chart');
        if (!ctx) return;

        const projects = this.data.projects || [];
        const labels = projects.map(p => p.name.substring(0, 15) + '...');
        const progress = projects.map(p => p.progress || 0);

        this.charts.progress = new Chart(ctx, {
            type: 'horizontalBar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Прогресс (%)',
                    data: progress,
                    backgroundColor: '#667eea',
                    borderColor: '#667eea',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    x: {
                        beginAtZero: true,
                        max: 100
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }

    initActivityChart() {
        const ctx = document.getElementById('activity-chart');
        if (!ctx) return;

        // Генерируем данные активности за последние 7 дней
        const labels = [];
        const data = [];
        const today = new Date();

        for (let i = 6; i >= 0; i--) {
            const date = new Date(today);
            date.setDate(date.getDate() - i);
            labels.push(date.toLocaleDateString('ru-RU', { weekday: 'short' }));
            data.push(Math.floor(Math.random() * 20) + 10);
        }

        this.charts.activity = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Активность',
                    data: data,
                    borderColor: '#f59e0b',
                    backgroundColor: 'rgba(245, 158, 11, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }

    // Статические методы для экспорта
    static exportChart(chartType) {
        const chart = DashboardModule.charts[chartType];
        if (chart) {
            const canvas = chart.canvas;
            const link = document.createElement('a');
            link.download = `${chartType}-chart.png`;
            link.href = canvas.toDataURL();
            link.click();
        }
    }

    static showAllActivities() {
        app.showModal('Все активности', `
            <div class="activities-modal">
                <div class="activity-filters">
                    <select class="form-control">
                        <option>Все типы</option>
                        <option>Проекты</option>
                        <option>Сметы</option>
                        <option>Сотрудники</option>
                        <option>Финансы</option>
                    </select>
                    <input type="date" class="form-control" value="${new Date().toISOString().split('T')[0]}">
                </div>
                <div class="activities-list">
                    ${DashboardModule.generateAllActivities()}
                </div>
            </div>
        `);
    }

    static generateAllActivities() {
        const activities = [];
        for (let i = 0; i < 20; i++) {
            activities.push({
                type: ['project', 'estimate', 'employee', 'financial', 'notification'][Math.floor(Math.random() * 5)],
                icon: ['fas fa-project-diagram', 'fas fa-calculator', 'fas fa-user', 'fas fa-chart-line', 'fas fa-bell'][Math.floor(Math.random() * 5)],
                title: `Активность ${i + 1}`,
                time: `${Math.floor(Math.random() * 24)} часов назад`,
                status: ['completed', 'pending', 'warning'][Math.floor(Math.random() * 3)]
            });
        }

        return activities.map(activity => `
            <div class="activity-item">
                <div class="activity-icon ${activity.type}">
                    <i class="${activity.icon}"></i>
                </div>
                <div class="activity-content">
                    <div class="activity-title">${activity.title}</div>
                    <div class="activity-time">${activity.time}</div>
                </div>
                <div class="activity-status ${activity.status}">
                    <span class="status-dot"></span>
                </div>
            </div>
        `).join('');
    }

    // Поиск
    search(query) {
        // Реализация поиска по дашборду
        console.log('Поиск по дашборду:', query);
    }
}

// Инициализация модуля
window.DashboardModule = new DashboardModule();