// Система авторизации BuildCRM

class Auth {
    constructor() {
        this.isAuthenticated = false;
        this.currentUser = null;
        this.init();
    }

    init() {
        this.checkAuthStatus();
        this.bindEvents();
    }

    bindEvents() {
        const loginForm = document.getElementById('login-form');
        const logoutBtn = document.getElementById('logout-btn');

        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.handleLogout());
        }
    }

    checkAuthStatus() {
        const token = Utils.storage.get('auth_token');
        const user = Utils.storage.get('current_user');

        if (token && user) {
            this.isAuthenticated = true;
            this.currentUser = user;
            this.showApp();
        } else {
            this.showAuth();
        }
    }

    async handleLogin(e) {
        e.preventDefault();
        
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        if (!this.validateLoginForm(email, password)) {
            return;
        }

        try {
            await this.login(email, password);
        } catch (error) {
            this.showNotification('Ошибка авторизации', 'error');
        }
    }

    validateLoginForm(email, password) {
        if (!email || !password) {
            this.showNotification('Заполните все поля', 'error');
            return false;
        }

        if (!Utils.isValidEmail(email)) {
            this.showNotification('Введите корректный email', 'error');
            return false;
        }

        return true;
    }

    async login(email, password) {
        // Демо авторизация
        if (email === 'admin@buildcrm.com' && password === 'admin123') {
            const user = {
                id: '1',
                name: 'Администратор',
                email: email,
                role: 'admin',
                avatar: 'https://via.placeholder.com/40',
                permissions: ['all']
            };

            const token = Utils.generateId();
            
            Utils.storage.set('auth_token', token);
            Utils.storage.set('current_user', user);
            
            this.isAuthenticated = true;
            this.currentUser = user;
            
            // Перенаправляем на дашборд
            window.location.href = 'dashboard.html';
        } else {
            throw new Error('Неверные учетные данные');
        }
    }

    handleLogout() {
        this.logout();
        this.showNotification('Вы вышли из системы', 'info');
    }

    logout() {
        Utils.storage.remove('auth_token');
        Utils.storage.remove('current_user');
        
        this.isAuthenticated = false;
        this.currentUser = null;
        
        this.showAuth();
    }

    showAuth() {
        document.getElementById('auth-container').classList.remove('hidden');
        document.getElementById('app-container').classList.add('hidden');
    }

    showApp() {
        document.getElementById('auth-container').classList.add('hidden');
        document.getElementById('app-container').classList.remove('hidden');
    }

    updateUserInfo() {
        if (!this.currentUser) return;

        const userNameElement = document.querySelector('.user-name');
        const userRoleElement = document.querySelector('.user-role');
        const userAvatarElement = document.querySelector('.user-avatar');

        if (userNameElement) {
            userNameElement.textContent = this.currentUser.name;
        }

        if (userRoleElement) {
            const roleNames = {
                'admin': 'Руководитель',
                'manager': 'Менеджер',
                'engineer': 'Инженер',
                'worker': 'Рабочий',
                'accountant': 'Бухгалтер'
            };
            userRoleElement.textContent = roleNames[this.currentUser.role] || this.currentUser.role;
        }

        if (userAvatarElement && this.currentUser.avatar) {
            userAvatarElement.src = this.currentUser.avatar;
        }
    }

    showNotification(message, type = 'info') {
        const container = document.getElementById('notifications-container');
        const notification = document.createElement('div');
        
        notification.className = `notification ${type}`;
        notification.textContent = message;
        
        container.appendChild(notification);
        
        setTimeout(() => {
            notification.remove();
        }, 3000);
    }

    // Проверка прав доступа
    hasPermission(permission) {
        if (!this.currentUser) return false;
        
        if (this.currentUser.permissions.includes('all')) {
            return true;
        }
        
        return this.currentUser.permissions.includes(permission);
    }

    // Получение текущего пользователя
    getCurrentUser() {
        return this.currentUser;
    }

    // Проверка аутентификации
    isLoggedIn() {
        return this.isAuthenticated;
    }
}

// Инициализация системы авторизации
const auth = new Auth();
window.auth = auth;