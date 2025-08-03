// Модуль аналитики BuildCRM

class AnalyticsModule {
    constructor() {
        this.data = {};
        this.charts = {};
    }

    init() {
        this.loadAnalyticsData();
        this.renderAnalytics();
    }

    async loadAnalyticsData() {
        try {
            this.data = await app.loadData('analytics');
        } catch (error) {
            console.error('Ошибка загрузки аналитики:', error);
        }
    }

    renderAnalytics() {
        const container = document.getElementById('analytics-section');
        if (!container) return;

        container.innerHTML = `
            <div class="analytics-container">
                <!-- Заголовок -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Аналитика и отчеты</h2>
                        <p>Детальная аналитика по проектам и финансовым показателям</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="AnalyticsModule.exportReport()">
                            <i class="fas fa-download"></i> Экспорт отчета
                        </button>
                    </div>
                </div>

                <!-- Ключевые показатели -->
                <div class="kpi-section">
                    ${this.renderKPIs()}
                </div>

                <!-- Графики -->
                <div class="charts-section">
                    <div class="chart-row">
                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Динамика доходов</h3>
                            </div>
                            <div class="chart-container">
                                <canvas id="revenue-chart"></canvas>
                            </div>
                        </div>
                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Распределение проектов</h3>
                            </div>
                            <div class="chart-container">
                                <canvas id="projects-distribution-chart"></canvas>
                            </div>
                        </div>
                    </div>
                    
                    <div class="chart-row">
                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Эффективность сотрудников</h3>
                            </div>
                            <div class="chart-container">
                                <canvas id="efficiency-chart"></canvas>
                            </div>
                        </div>
                        <div class="chart-card">
                            <div class="chart-header">
                                <h3>Загрузка ресурсов</h3>
                            </div>
                            <div class="chart-container">
                                <canvas id="resources-chart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Таблицы данных -->
                <div class="tables-section">
                    <div class="table-row">
                        <div class="table-card">
                            <div class="table-header">
                                <h3>Топ проектов по доходности</h3>
                            </div>
                            <div class="table-container">
                                ${this.renderTopProjectsTable()}
                            </div>
                        </div>
                        <div class="table-card">
                            <div class="table-header">
                                <h3>Активность сотрудников</h3>
                            </div>
                            <div class="table-container">
                                ${this.renderEmployeeActivityTable()}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        this.initCharts();
    }

    renderKPIs() {
        const data = this.data || {};
        
        return `
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-icon">
                        <i class="fas fa-chart-line"></i>
                    </div>
                    <div class="kpi-content">
                        <div class="kpi-value">${Utils.formatCurrency(data.revenue?.current || 0)}</div>
                        <div class="kpi-label">Общий доход</div>
                        <div class="kpi-change positive">
                            <i class="fas fa-arrow-up"></i> +${data.revenue?.growth || 0}%
                        </div>
                    </div>
                </div>
                
                <div class="kpi-card">
                    <div class="kpi-icon">
                        <i class="fas fa-project-diagram"></i>
                    </div>
                    <div class="kpi-content">
                        <div class="kpi-value">${data.projects?.total || 0}</div>
                        <div class="kpi-label">Активных проектов</div>
                        <div class="kpi-change positive">
                            <i class="fas fa-arrow-up"></i> +${data.projects?.active || 0}
                        </div>
                    </div>
                </div>
                
                <div class="kpi-card">
                    <div class="kpi-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="kpi-content">
                        <div class="kpi-value">${data.employees?.total || 0}</div>
                        <div class="kpi-label">Сотрудников</div>
                        <div class="kpi-change positive">
                            <i class="fas fa-arrow-up"></i> +${data.employees?.active || 0}
                        </div>
                    </div>
                </div>
                
                <div class="kpi-card">
                    <div class="kpi-icon">
                        <i class="fas fa-tachometer-alt"></i>
                    </div>
                    <div class="kpi-content">
                        <div class="kpi-value">${data.efficiency?.average || 0}%</div>
                        <div class="kpi-label">Эффективность</div>
                        <div class="kpi-change positive">
                            <i class="fas fa-arrow-up"></i> +${data.efficiency?.trend || 0}%
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    renderTopProjectsTable() {
        const projects = [
            { name: 'Жилой комплекс "Солнечный"', revenue: 15000000, profit: 3000000, efficiency: 85 },
            { name: 'Торговый центр "МегаМолл"', revenue: 25000000, profit: 5000000, efficiency: 92 },
            { name: 'Завод по производству бетона', revenue: 8000000, profit: 1600000, efficiency: 78 }
        ];

        return `
            <table class="table">
                <thead>
                    <tr>
                        <th>Проект</th>
                        <th>Доход</th>
                        <th>Прибыль</th>
                        <th>Эффективность</th>
                    </tr>
                </thead>
                <tbody>
                    ${projects.map(project => `
                        <tr>
                            <td>${project.name}</td>
                            <td>${Utils.formatCurrency(project.revenue)}</td>
                            <td>${Utils.formatCurrency(project.profit)}</td>
                            <td>
                                <div class="efficiency-bar">
                                    <div class="efficiency-fill" style="width: ${project.efficiency}%"></div>
                                    <span>${project.efficiency}%</span>
                                </div>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    renderEmployeeActivityTable() {
        const employees = [
            { name: 'Иванов И.И.', hours: 168, projects: 3, efficiency: 95 },
            { name: 'Петрова А.С.', hours: 152, projects: 2, efficiency: 88 },
            { name: 'Сидоров А.П.', hours: 160, projects: 4, efficiency: 82 }
        ];

        return `
            <table class="table">
                <thead>
                    <tr>
                        <th>Сотрудник</th>
                        <th>Часов</th>
                        <th>Проектов</th>
                        <th>Эффективность</th>
                    </tr>
                </thead>
                <tbody>
                    ${employees.map(employee => `
                        <tr>
                            <td>${employee.name}</td>
                            <td>${employee.hours}</td>
                            <td>${employee.projects}</td>
                            <td>
                                <div class="efficiency-bar">
                                    <div class="efficiency-fill" style="width: ${employee.efficiency}%"></div>
                                    <span>${employee.efficiency}%</span>
                                </div>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    initCharts() {
        this.initRevenueChart();
        this.initProjectsDistributionChart();
        this.initEfficiencyChart();
        this.initResourcesChart();
    }

    initRevenueChart() {
        const ctx = document.getElementById('revenue-chart');
        if (!ctx) return;

        const labels = ['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн'];
        const data = [12000000, 15000000, 18000000, 22000000, 25000000, 30000000];

        this.charts.revenue = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Доход',
                    data: data,
                    borderColor: '#667eea',
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
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
                        display: false
                    }
                }
            }
        });
    }

    initProjectsDistributionChart() {
        const ctx = document.getElementById('projects-distribution-chart');
        if (!ctx) return;

        this.charts.projectsDistribution = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Жилые', 'Коммерческие', 'Промышленные', 'Инфраструктура'],
                datasets: [{
                    data: [40, 30, 20, 10],
                    backgroundColor: ['#667eea', '#10b981', '#f59e0b', '#ef4444'],
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

    initEfficiencyChart() {
        const ctx = document.getElementById('efficiency-chart');
        if (!ctx) return;

        const labels = ['Иванов И.И.', 'Петрова А.С.', 'Сидоров А.П.'];
        const data = [95, 88, 82];

        this.charts.efficiency = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Эффективность (%)',
                    data: data,
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

    initResourcesChart() {
        const ctx = document.getElementById('resources-chart');
        if (!ctx) return;

        const labels = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];
        const data = [85, 90, 88, 92, 87, 45, 30];

        this.charts.resources = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Загрузка (%)',
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

    static exportReport() {
        const reportData = {
            'Общий доход': Utils.formatCurrency(30000000),
            'Активных проектов': 15,
            'Сотрудников': 45,
            'Эффективность': '87.5%'
        };

        app.exportData([reportData], 'analytics-report.csv', 'csv');
        app.showNotification('Отчет экспортирован', 'success');
    }

    // Поиск
    search(query) {
        console.log('Поиск по аналитике:', query);
    }
}

// Инициализация модуля
window.AnalyticsModule = new AnalyticsModule();