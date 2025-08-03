// Construction CRM Frontend Application
class ConstructionCRM {
    constructor() {
        this.apiUrl = localStorage.getItem('apiUrl') || 'http://localhost:8000';
        this.token = localStorage.getItem('authToken');
        this.user = null;
        
        this.init();
    }

    async init() {
        // Check if user is already logged in
        if (this.token) {
            try {
                await this.getCurrentUser();
                this.showDashboard();
            } catch (error) {
                console.error('Auto-login failed:', error);
                this.logout();
            }
        }

        // Initialize event listeners
        this.initEventListeners();
    }

    initEventListeners() {
        // Login form submission
        document.getElementById('loginForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.login();
        });

        // Enter key in login form
        document.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && document.getElementById('loginModal')?.classList.contains('show')) {
                this.login();
            }
        });
    }

    // API Request helper
    async apiRequest(endpoint, options = {}) {
        const url = `${this.apiUrl}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            
            return await response.text();
        } catch (error) {
            console.error('API Request failed:', error);
            throw error;
        }
    }

    // Authentication methods
    async login() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const apiUrl = document.getElementById('apiUrl').value;

        if (!username || !password) {
            this.showAlert('Пожалуйста, заполните все поля', 'danger');
            return;
        }

        try {
            this.apiUrl = apiUrl;
            localStorage.setItem('apiUrl', apiUrl);

            const formData = new FormData();
            formData.append('username', username);
            formData.append('password', password);

            const response = await fetch(`${this.apiUrl}/api/v1/auth/login`, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error('Неверные учетные данные');
            }

            const data = await response.json();
            this.token = data.access_token;
            localStorage.setItem('authToken', this.token);

            // Get user info
            await this.getCurrentUser();

            // Hide login modal and show dashboard
            const loginModal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
            if (loginModal) {
                loginModal.hide();
            }

            this.showDashboard();
            this.showAlert('Вход выполнен успешно!', 'success');

        } catch (error) {
            console.error('Login error:', error);
            this.showAlert('Ошибка входа: ' + error.message, 'danger');
        }
    }

    async getCurrentUser() {
        try {
            const user = await this.apiRequest('/api/v1/auth/me');
            this.user = user;
            document.getElementById('userName').textContent = user.full_name;
            return user;
        } catch (error) {
            throw new Error('Не удалось получить информацию о пользователе');
        }
    }

    logout() {
        this.token = null;
        this.user = null;
        localStorage.removeItem('authToken');
        
        document.getElementById('welcomeScreen').style.display = 'block';
        document.getElementById('dashboardScreen').style.display = 'none';
        document.getElementById('userMenu').style.display = 'none';
        document.getElementById('loginBtn').style.display = 'block';
        
        this.showAlert('Вы вышли из системы', 'info');
    }

    // UI Methods
    showLogin() {
        const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
        loginModal.show();
    }

    showDashboard() {
        document.getElementById('welcomeScreen').style.display = 'none';
        document.getElementById('dashboardScreen').style.display = 'block';
        document.getElementById('userMenu').style.display = 'block';
        document.getElementById('loginBtn').style.display = 'none';
        
        // Load dashboard data
        this.loadDashboardData();
    }

    showSection(sectionName) {
        // Hide all content sections
        const sections = document.querySelectorAll('.content-section');
        sections.forEach(section => section.style.display = 'none');
        
        // Show selected section
        document.getElementById(`${sectionName}-content`).style.display = 'block';
        
        // Update sidebar active state
        const navLinks = document.querySelectorAll('.sidebar .nav-link');
        navLinks.forEach(link => link.classList.remove('active'));
        event.target.classList.add('active');
        
        // Load section data
        this.loadSectionData(sectionName);
    }

    async loadDashboardData() {
        try {
            // Load basic statistics (mock data for now)
            document.getElementById('totalProjects').textContent = '12';
            document.getElementById('totalClients').textContent = '8';
            document.getElementById('totalEstimates').textContent = '15';
            document.getElementById('totalRevenue').textContent = '2.5M ₽';
            
            // In a real application, you would fetch this data from the API
            // const stats = await this.apiRequest('/api/v1/dashboard/stats');
            
        } catch (error) {
            console.error('Error loading dashboard data:', error);
            this.showAlert('Ошибка загрузки данных дашборда', 'danger');
        }
    }

    async loadSectionData(sectionName) {
        try {
            switch (sectionName) {
                case 'projects':
                    await this.loadProjects();
                    break;
                case 'clients':
                    await this.loadClients();
                    break;
                case 'estimates':
                    await this.loadEstimates();
                    break;
                case 'materials':
                    await this.loadMaterials();
                    break;
            }
        } catch (error) {
            console.error(`Error loading ${sectionName} data:`, error);
            this.showAlert(`Ошибка загрузки данных раздела ${sectionName}`, 'danger');
        }
    }

    async loadProjects() {
        const tableBody = document.getElementById('projectsTable');
        
        try {
            // Mock data for now
            const projects = [
                {
                    id: 1,
                    name: 'Строительство офисного здания',
                    client: 'ООО "Пример"',
                    status: 'in_progress',
                    budget: '5,000,000 ₽',
                    manager: 'Иван Петров'
                },
                {
                    id: 2,
                    name: 'Реконструкция склада',
                    client: 'ЗАО "Логистика+"',
                    status: 'planning',
                    budget: '2,500,000 ₽',
                    manager: 'Мария Сидорова'
                }
            ];

            // In a real application: const projects = await this.apiRequest('/api/v1/projects');
            
            tableBody.innerHTML = projects.map(project => `
                <tr>
                    <td>${project.name}</td>
                    <td>${project.client}</td>
                    <td><span class="badge bg-${this.getStatusColor(project.status)}">${this.getStatusText(project.status)}</span></td>
                    <td>${project.budget}</td>
                    <td>${project.manager}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="app.editProject(${project.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="app.deleteProject(${project.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
            
        } catch (error) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-danger">
                        <i class="fas fa-exclamation-triangle"></i> Ошибка загрузки данных
                    </td>
                </tr>
            `;
        }
    }

    async loadClients() {
        const tableBody = document.getElementById('clientsTable');
        
        try {
            // Mock data for now
            const clients = [
                {
                    id: 1,
                    company_name: 'ООО "Пример"',
                    contact_person: 'Иван Иванов',
                    email: 'client@example.com',
                    phone: '+7 (999) 123-45-67',
                    status: 'active'
                }
            ];

            // In a real application: const clients = await this.apiRequest('/api/v1/clients');
            
            tableBody.innerHTML = clients.map(client => `
                <tr>
                    <td>${client.company_name || '-'}</td>
                    <td>${client.contact_person}</td>
                    <td>${client.email}</td>
                    <td>${client.phone || '-'}</td>
                    <td><span class="badge bg-${this.getStatusColor(client.status)}">${this.getStatusText(client.status)}</span></td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="app.editClient(${client.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="app.deleteClient(${client.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
            
        } catch (error) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-danger">
                        <i class="fas fa-exclamation-triangle"></i> Ошибка загрузки данных
                    </td>
                </tr>
            `;
        }
    }

    // Helper methods
    getStatusColor(status) {
        const colors = {
            'active': 'success',
            'inactive': 'secondary',
            'planning': 'warning',
            'in_progress': 'primary',
            'completed': 'success',
            'on_hold': 'warning',
            'cancelled': 'danger'
        };
        return colors[status] || 'secondary';
    }

    getStatusText(status) {
        const texts = {
            'active': 'Активный',
            'inactive': 'Неактивный',
            'planning': 'Планирование',
            'in_progress': 'В работе',
            'completed': 'Завершен',
            'on_hold': 'Приостановлен',
            'cancelled': 'Отменен'
        };
        return texts[status] || status;
    }

    showAlert(message, type = 'info') {
        // Remove existing alerts
        const existingAlerts = document.querySelectorAll('.alert-notification');
        existingAlerts.forEach(alert => alert.remove());

        // Create new alert
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-notification position-fixed`;
        alert.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alert.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <span>${message}</span>
                <button type="button" class="btn-close" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
        `;

        document.body.appendChild(alert);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (alert.parentElement) {
                alert.remove();
            }
        }, 5000);
    }

    showApiDocs() {
        window.open(`${this.apiUrl}/docs`, '_blank');
    }

    // Placeholder methods for CRUD operations
    createProject() {
        this.showAlert('Функция создания проекта в разработке', 'info');
    }

    editProject(id) {
        this.showAlert(`Редактирование проекта #${id} в разработке`, 'info');
    }

    deleteProject(id) {
        if (confirm('Вы уверены, что хотите удалить этот проект?')) {
            this.showAlert(`Удаление проекта #${id} в разработке`, 'info');
        }
    }

    createClient() {
        this.showAlert('Функция создания клиента в разработке', 'info');
    }

    editClient(id) {
        this.showAlert(`Редактирование клиента #${id} в разработке`, 'info');
    }

    deleteClient(id) {
        if (confirm('Вы уверены, что хотите удалить этого клиента?')) {
            this.showAlert(`Удаление клиента #${id} в разработке`, 'info');
        }
    }
}

// Global functions for HTML onclick events
function showLogin() {
    app.showLogin();
}

function login() {
    app.login();
}

function logout() {
    app.logout();
}

function showSection(sectionName) {
    app.showSection(sectionName);
}

function showApiDocs() {
    app.showApiDocs();
}

function createProject() {
    app.createProject();
}

function createClient() {
    app.createClient();
}

// Initialize the application
const app = new ConstructionCRM();

// Health check on load
window.addEventListener('load', async () => {
    try {
        const response = await fetch(`${app.apiUrl}/health`);
        if (response.ok) {
            console.log('API server is running');
        }
    } catch (error) {
        console.warn('API server not accessible:', error);
        app.showAlert('API сервер недоступен. Проверьте подключение.', 'warning');
    }
});