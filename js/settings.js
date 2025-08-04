// Модуль настроек BuildCRM

class SettingsModule {
    constructor() {
        this.settings = {};
        this.currentTab = 'general';
    }

    init() {
        this.loadSettings();
        this.renderSettings();
    }

    async loadSettings() {
        // Демо данные настроек
        this.settings = {
            general: {
                companyName: 'BuildCRM Construction',
                companyEmail: 'info@buildcrm.com',
                companyPhone: '+7 (495) 123-45-67',
                companyAddress: 'г. Москва, ул. Строителей, д. 1',
                timezone: 'Europe/Moscow',
                currency: 'RUB',
                language: 'ru'
            },
            notifications: {
                emailNotifications: true,
                smsNotifications: false,
                projectUpdates: true,
                deadlineReminders: true,
                weeklyReports: true,
                dailyDigest: false
            },
            security: {
                twoFactorAuth: false,
                sessionTimeout: 30,
                passwordExpiry: 90,
                loginAttempts: 5,
                ipWhitelist: []
            },
            integrations: {
                emailIntegration: true,
                calendarIntegration: true,
                fileStorage: 'local',
                backupEnabled: true,
                backupFrequency: 'daily'
            },
            appearance: {
                theme: 'light',
                sidebarCollapsed: false,
                compactMode: false,
                showAnimations: true
            }
        };
    }

    renderSettings() {
        const container = document.getElementById('settings-section');
        if (!container) return;

        container.innerHTML = `
            <div class="settings-container">
                <!-- Заголовок -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Настройки</h2>
                        <p>Управление параметрами системы</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="SettingsModule.saveAllSettings()">
                            <i class="fas fa-save"></i> Сохранить все
                        </button>
                    </div>
                </div>

                <!-- Навигация по вкладкам -->
                <div class="settings-tabs">
                    <button class="tab-button ${this.currentTab === 'general' ? 'active' : ''}" 
                            onclick="SettingsModule.switchTab('general')">
                        <i class="fas fa-cog"></i> Общие
                    </button>
                    <button class="tab-button ${this.currentTab === 'notifications' ? 'active' : ''}" 
                            onclick="SettingsModule.switchTab('notifications')">
                        <i class="fas fa-bell"></i> Уведомления
                    </button>
                    <button class="tab-button ${this.currentTab === 'security' ? 'active' : ''}" 
                            onclick="SettingsModule.switchTab('security')">
                        <i class="fas fa-shield-alt"></i> Безопасность
                    </button>
                    <button class="tab-button ${this.currentTab === 'integrations' ? 'active' : ''}" 
                            onclick="SettingsModule.switchTab('integrations')">
                        <i class="fas fa-plug"></i> Интеграции
                    </button>
                    <button class="tab-button ${this.currentTab === 'appearance' ? 'active' : ''}" 
                            onclick="SettingsModule.switchTab('appearance')">
                        <i class="fas fa-palette"></i> Внешний вид
                    </button>
                </div>

                <!-- Контент вкладок -->
                <div class="settings-content">
                    ${this.renderCurrentTab()}
                </div>
            </div>
        `;
    }

    renderCurrentTab() {
        switch (this.currentTab) {
            case 'general':
                return this.renderGeneralSettings();
            case 'notifications':
                return this.renderNotificationSettings();
            case 'security':
                return this.renderSecuritySettings();
            case 'integrations':
                return this.renderIntegrationSettings();
            case 'appearance':
                return this.renderAppearanceSettings();
            default:
                return this.renderGeneralSettings();
        }
    }

    renderGeneralSettings() {
        const settings = this.settings.general;
        
        return `
            <div class="settings-section">
                <h3>Общие настройки</h3>
                
                <div class="settings-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Название компании</label>
                            <input type="text" value="${settings.companyName}" 
                                   onchange="SettingsModule.updateSetting('general', 'companyName', this.value)">
                        </div>
                        <div class="form-group">
                            <label>Email компании</label>
                            <input type="email" value="${settings.companyEmail}" 
                                   onchange="SettingsModule.updateSetting('general', 'companyEmail', this.value)">
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Телефон компании</label>
                            <input type="tel" value="${settings.companyPhone}" 
                                   onchange="SettingsModule.updateSetting('general', 'companyPhone', this.value)">
                        </div>
                        <div class="form-group">
                            <label>Адрес компании</label>
                            <input type="text" value="${settings.companyAddress}" 
                                   onchange="SettingsModule.updateSetting('general', 'companyAddress', this.value)">
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Часовой пояс</label>
                            <select onchange="SettingsModule.updateSetting('general', 'timezone', this.value)">
                                <option value="Europe/Moscow" ${settings.timezone === 'Europe/Moscow' ? 'selected' : ''}>Москва (UTC+3)</option>
                                <option value="Europe/London" ${settings.timezone === 'Europe/London' ? 'selected' : ''}>Лондон (UTC+0)</option>
                                <option value="America/New_York" ${settings.timezone === 'America/New_York' ? 'selected' : ''}>Нью-Йорк (UTC-5)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Валюта</label>
                            <select onchange="SettingsModule.updateSetting('general', 'currency', this.value)">
                                <option value="RUB" ${settings.currency === 'RUB' ? 'selected' : ''}>Рубль (₽)</option>
                                <option value="USD" ${settings.currency === 'USD' ? 'selected' : ''}>Доллар ($)</option>
                                <option value="EUR" ${settings.currency === 'EUR' ? 'selected' : ''}>Евро (€)</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Язык интерфейса</label>
                        <select onchange="SettingsModule.updateSetting('general', 'language', this.value)">
                            <option value="ru" ${settings.language === 'ru' ? 'selected' : ''}>Русский</option>
                            <option value="en" ${settings.language === 'en' ? 'selected' : ''}>English</option>
                        </select>
                    </div>
                </div>
            </div>
        `;
    }

    renderNotificationSettings() {
        const settings = this.settings.notifications;
        
        return `
            <div class="settings-section">
                <h3>Настройки уведомлений</h3>
                
                <div class="settings-form">
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.emailNotifications ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('notifications', 'emailNotifications', this.checked)">
                            <span class="checkmark"></span>
                            Email уведомления
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.smsNotifications ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('notifications', 'smsNotifications', this.checked)">
                            <span class="checkmark"></span>
                            SMS уведомления
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.projectUpdates ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('notifications', 'projectUpdates', this.checked)">
                            <span class="checkmark"></span>
                            Обновления проектов
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.deadlineReminders ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('notifications', 'deadlineReminders', this.checked)">
                            <span class="checkmark"></span>
                            Напоминания о дедлайнах
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.weeklyReports ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('notifications', 'weeklyReports', this.checked)">
                            <span class="checkmark"></span>
                            Еженедельные отчеты
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.dailyDigest ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('notifications', 'dailyDigest', this.checked)">
                            <span class="checkmark"></span>
                            Ежедневный дайджест
                        </label>
                    </div>
                </div>
            </div>
        `;
    }

    renderSecuritySettings() {
        const settings = this.settings.security;
        
        return `
            <div class="settings-section">
                <h3>Настройки безопасности</h3>
                
                <div class="settings-form">
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.twoFactorAuth ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('security', 'twoFactorAuth', this.checked)">
                            <span class="checkmark"></span>
                            Двухфакторная аутентификация
                        </label>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Таймаут сессии (минуты)</label>
                            <input type="number" value="${settings.sessionTimeout}" min="5" max="480" 
                                   onchange="SettingsModule.updateSetting('security', 'sessionTimeout', parseInt(this.value))">
                        </div>
                        <div class="form-group">
                            <label>Срок действия пароля (дни)</label>
                            <input type="number" value="${settings.passwordExpiry}" min="30" max="365" 
                                   onchange="SettingsModule.updateSetting('security', 'passwordExpiry', parseInt(this.value))">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Максимум попыток входа</label>
                        <input type="number" value="${settings.loginAttempts}" min="3" max="10" 
                               onchange="SettingsModule.updateSetting('security', 'loginAttempts', parseInt(this.value))">
                    </div>
                    
                    <div class="form-group">
                        <label>Белый список IP адресов</label>
                        <textarea rows="3" placeholder="Введите IP адреса, по одному на строку"
                                  onchange="SettingsModule.updateSetting('security', 'ipWhitelist', this.value.split('\\n').filter(ip => ip.trim()))">${settings.ipWhitelist.join('\\n')}</textarea>
                    </div>
                </div>
            </div>
        `;
    }

    renderIntegrationSettings() {
        const settings = this.settings.integrations;
        
        return `
            <div class="settings-section">
                <h3>Настройки интеграций</h3>
                
                <div class="settings-form">
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.emailIntegration ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('integrations', 'emailIntegration', this.checked)">
                            <span class="checkmark"></span>
                            Интеграция с email
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.calendarIntegration ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('integrations', 'calendarIntegration', this.checked)">
                            <span class="checkmark"></span>
                            Интеграция с календарем
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label>Хранилище файлов</label>
                        <select onchange="SettingsModule.updateSetting('integrations', 'fileStorage', this.value)">
                            <option value="local" ${settings.fileStorage === 'local' ? 'selected' : ''}>Локальное</option>
                            <option value="cloud" ${settings.fileStorage === 'cloud' ? 'selected' : ''}>Облачное</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.backupEnabled ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('integrations', 'backupEnabled', this.checked)">
                            <span class="checkmark"></span>
                            Автоматическое резервное копирование
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label>Частота резервного копирования</label>
                        <select onchange="SettingsModule.updateSetting('integrations', 'backupFrequency', this.value)">
                            <option value="daily" ${settings.backupFrequency === 'daily' ? 'selected' : ''}>Ежедневно</option>
                            <option value="weekly" ${settings.backupFrequency === 'weekly' ? 'selected' : ''}>Еженедельно</option>
                            <option value="monthly" ${settings.backupFrequency === 'monthly' ? 'selected' : ''}>Ежемесячно</option>
                        </select>
                    </div>
                </div>
            </div>
        `;
    }

    renderAppearanceSettings() {
        const settings = this.settings.appearance;
        
        return `
            <div class="settings-section">
                <h3>Настройки внешнего вида</h3>
                
                <div class="settings-form">
                    <div class="form-group">
                        <label>Тема оформления</label>
                        <select onchange="SettingsModule.updateSetting('appearance', 'theme', this.value)">
                            <option value="light" ${settings.theme === 'light' ? 'selected' : ''}>Светлая</option>
                            <option value="dark" ${settings.theme === 'dark' ? 'selected' : ''}>Темная</option>
                            <option value="auto" ${settings.theme === 'auto' ? 'selected' : ''}>Авто</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.sidebarCollapsed ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('appearance', 'sidebarCollapsed', this.checked)">
                            <span class="checkmark"></span>
                            Свернутая боковая панель
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.compactMode ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('appearance', 'compactMode', this.checked)">
                            <span class="checkmark"></span>
                            Компактный режим
                        </label>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" ${settings.showAnimations ? 'checked' : ''} 
                                   onchange="SettingsModule.updateSetting('appearance', 'showAnimations', this.checked)">
                            <span class="checkmark"></span>
                            Показывать анимации
                        </label>
                    </div>
                </div>
            </div>
        `;
    }

    static switchTab(tabName) {
        SettingsModule.currentTab = tabName;
        SettingsModule.renderSettings();
    }

    static updateSetting(category, key, value) {
        if (!SettingsModule.settings[category]) {
            SettingsModule.settings[category] = {};
        }
        SettingsModule.settings[category][key] = value;
        
        // Сохранить в localStorage
        Utils.storage.set('settings', SettingsModule.settings);
        
        app.showNotification('Настройка обновлена', 'success');
    }

    static saveAllSettings() {
        Utils.storage.set('settings', SettingsModule.settings);
        app.showNotification('Все настройки сохранены', 'success');
    }

    // Поиск
    search(query) {
        console.log('Поиск по настройкам:', query);
    }
}

// Инициализация модуля
window.SettingsModule = new SettingsModule();