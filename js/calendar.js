// Модуль календаря BuildCRM

class CalendarModule {
    constructor() {
        this.currentDate = new Date();
        this.events = [];
        this.selectedDate = null;
    }

    init() {
        this.loadEvents();
        this.renderCalendar();
    }

    async loadEvents() {
        // Демо данные для событий
        this.events = [
            {
                id: 1,
                title: 'Встреча с клиентом',
                description: 'Обсуждение проекта жилого комплекса',
                date: '2024-01-15',
                time: '10:00',
                duration: 60,
                type: 'meeting',
                project: 'Жилой комплекс "Солнечный"',
                attendees: ['Иванов И.И.', 'Петрова А.С.']
            },
            {
                id: 2,
                title: 'Сдача проекта',
                description: 'Финальная сдача торгового центра',
                date: '2024-01-20',
                time: '14:00',
                duration: 120,
                type: 'delivery',
                project: 'Торговый центр "МегаМолл"',
                attendees: ['Сидоров А.П.', 'Козлов В.М.']
            },
            {
                id: 3,
                title: 'Планирование',
                description: 'Планирование работ на неделю',
                date: '2024-01-22',
                time: '09:00',
                duration: 90,
                type: 'planning',
                project: 'Общее',
                attendees: ['Все менеджеры']
            }
        ];
    }

    renderCalendar() {
        const container = document.getElementById('calendar-section');
        if (!container) return;

        container.innerHTML = `
            <div class="calendar-container">
                <!-- Заголовок -->
                <div class="page-header">
                    <div class="header-content">
                        <h2>Календарь</h2>
                        <p>Управление событиями и задачами</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn btn-secondary" onclick="CalendarModule.showMonthView()">
                            <i class="fas fa-calendar-alt"></i> Месяц
                        </button>
                        <button class="btn btn-secondary" onclick="CalendarModule.showWeekView()">
                            <i class="fas fa-calendar-week"></i> Неделя
                        </button>
                        <button class="btn btn-primary" onclick="CalendarModule.showAddEventModal()">
                            <i class="fas fa-plus"></i> Добавить событие
                        </button>
                    </div>
                </div>

                <!-- Навигация календаря -->
                <div class="calendar-navigation">
                    <button class="btn btn-icon" onclick="CalendarModule.previousMonth()">
                        <i class="fas fa-chevron-left"></i>
                    </button>
                    <h3 class="current-month">${this.getMonthYearString()}</h3>
                    <button class="btn btn-icon" onclick="CalendarModule.nextMonth()">
                        <i class="fas fa-chevron-right"></i>
                    </button>
                </div>

                <!-- Календарь -->
                <div class="calendar-grid">
                    ${this.renderCalendarGrid()}
                </div>

                <!-- События на выбранный день -->
                <div class="events-section">
                    <h3>События на ${this.selectedDate ? this.formatDate(this.selectedDate) : 'сегодня'}</h3>
                    <div class="events-list">
                        ${this.renderEventsForSelectedDate()}
                    </div>
                </div>
            </div>
        `;
    }

    renderCalendarGrid() {
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const startDate = new Date(firstDay);
        startDate.setDate(startDate.getDate() - firstDay.getDay());
        
        const endDate = new Date(lastDay);
        endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()));
        
        let html = `
            <div class="calendar-header">
                <div class="calendar-day-header">Пн</div>
                <div class="calendar-day-header">Вт</div>
                <div class="calendar-day-header">Ср</div>
                <div class="calendar-day-header">Чт</div>
                <div class="calendar-day-header">Пт</div>
                <div class="calendar-day-header">Сб</div>
                <div class="calendar-day-header">Вс</div>
            </div>
        `;
        
        const currentDate = new Date(startDate);
        
        while (currentDate <= endDate) {
            const isCurrentMonth = currentDate.getMonth() === month;
            const isToday = this.isToday(currentDate);
            const isSelected = this.selectedDate && this.isSameDate(currentDate, this.selectedDate);
            const dayEvents = this.getEventsForDate(currentDate);
            
            const dayClass = `calendar-day ${isCurrentMonth ? 'current-month' : 'other-month'} ${isToday ? 'today' : ''} ${isSelected ? 'selected' : ''}`;
            
            html += `
                <div class="${dayClass}" onclick="CalendarModule.selectDate('${this.formatDate(currentDate)}')">
                    <div class="day-number">${currentDate.getDate()}</div>
                    ${dayEvents.length > 0 ? `
                        <div class="day-events">
                            ${dayEvents.slice(0, 2).map(event => `
                                <div class="event-dot ${event.type}"></div>
                            `).join('')}
                            ${dayEvents.length > 2 ? `<div class="more-events">+${dayEvents.length - 2}</div>` : ''}
                        </div>
                    ` : ''}
                </div>
            `;
            
            currentDate.setDate(currentDate.getDate() + 1);
        }
        
        return html;
    }

    renderEventsForSelectedDate() {
        const date = this.selectedDate || new Date();
        const events = this.getEventsForDate(date);
        
        if (events.length === 0) {
            return '<div class="no-events">Нет событий на этот день</div>';
        }
        
        return events.map(event => `
            <div class="event-card ${event.type}">
                <div class="event-time">${event.time}</div>
                <div class="event-content">
                    <div class="event-title">${event.title}</div>
                    <div class="event-description">${event.description}</div>
                    <div class="event-project">${event.project}</div>
                    <div class="event-attendees">
                        <i class="fas fa-users"></i> ${event.attendees.join(', ')}
                    </div>
                </div>
                <div class="event-actions">
                    <button class="btn btn-icon btn-sm" onclick="CalendarModule.editEvent(${event.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-icon btn-sm" onclick="CalendarModule.deleteEvent(${event.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `).join('');
    }

    getEventsForDate(date) {
        const dateStr = this.formatDate(date);
        return this.events.filter(event => event.date === dateStr);
    }

    isToday(date) {
        const today = new Date();
        return this.isSameDate(date, today);
    }

    isSameDate(date1, date2) {
        return date1.getDate() === date2.getDate() &&
               date1.getMonth() === date2.getMonth() &&
               date1.getFullYear() === date2.getFullYear();
    }

    formatDate(date) {
        return date.toISOString().split('T')[0];
    }

    getMonthYearString() {
        const months = [
            'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
            'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
        ];
        return `${months[this.currentDate.getMonth()]} ${this.currentDate.getFullYear()}`;
    }

    selectDate(dateStr) {
        this.selectedDate = new Date(dateStr);
        this.renderCalendar();
    }

    previousMonth() {
        this.currentDate.setMonth(this.currentDate.getMonth() - 1);
        this.renderCalendar();
    }

    nextMonth() {
        this.currentDate.setMonth(this.currentDate.getMonth() + 1);
        this.renderCalendar();
    }

    static showMonthView() {
        app.showNotification('Переключение на месячный вид', 'info');
    }

    static showWeekView() {
        app.showNotification('Переключение на недельный вид', 'info');
    }

    static showAddEventModal() {
        const modalContent = `
            <div class="modal-header">
                <h3>Добавить событие</h3>
                <button class="btn btn-icon" onclick="app.closeModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">
                <form id="add-event-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Название события</label>
                            <input type="text" name="title" required>
                        </div>
                        <div class="form-group">
                            <label>Тип события</label>
                            <select name="type" required>
                                <option value="meeting">Встреча</option>
                                <option value="delivery">Сдача проекта</option>
                                <option value="planning">Планирование</option>
                                <option value="task">Задача</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Дата</label>
                            <input type="date" name="date" required>
                        </div>
                        <div class="form-group">
                            <label>Время</label>
                            <input type="time" name="time" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Описание</label>
                        <textarea name="description" rows="3"></textarea>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Проект</label>
                            <select name="project">
                                <option value="">Общее</option>
                                <option value="Жилой комплекс \"Солнечный\"">Жилой комплекс "Солнечный"</option>
                                <option value="Торговый центр \"МегаМолл\"">Торговый центр "МегаМолл"</option>
                                <option value="Завод по производству бетона">Завод по производству бетона</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Длительность (мин)</label>
                            <input type="number" name="duration" value="60" min="15" step="15">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Участники</label>
                        <input type="text" name="attendees" placeholder="Иванов И.И., Петрова А.С.">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="app.closeModal()">Отмена</button>
                <button class="btn btn-primary" onclick="CalendarModule.saveEvent()">Сохранить</button>
            </div>
        `;
        
        app.showModal(modalContent);
        
        // Установить текущую дату
        const dateInput = document.querySelector('input[name="date"]');
        if (dateInput) {
            dateInput.value = CalendarModule.selectedDate ? 
                CalendarModule.formatDate(CalendarModule.selectedDate) : 
                CalendarModule.formatDate(new Date());
        }
    }

    static saveEvent() {
        const form = document.getElementById('add-event-form');
        const formData = new FormData(form);
        
        const event = {
            id: Date.now(),
            title: formData.get('title'),
            description: formData.get('description'),
            date: formData.get('date'),
            time: formData.get('time'),
            duration: parseInt(formData.get('duration')),
            type: formData.get('type'),
            project: formData.get('project'),
            attendees: formData.get('attendees').split(',').map(s => s.trim()).filter(s => s)
        };
        
        CalendarModule.events.push(event);
        CalendarModule.renderCalendar();
        app.closeModal();
        app.showNotification('Событие добавлено', 'success');
    }

    static editEvent(eventId) {
        const event = CalendarModule.events.find(e => e.id === eventId);
        if (!event) return;
        
        app.showNotification('Редактирование события', 'info');
    }

    static deleteEvent(eventId) {
        if (confirm('Удалить это событие?')) {
            CalendarModule.events = CalendarModule.events.filter(e => e.id !== eventId);
            CalendarModule.renderCalendar();
            app.showNotification('Событие удалено', 'success');
        }
    }

    // Поиск
    search(query) {
        console.log('Поиск по календарю:', query);
    }
}

// Инициализация модуля
window.CalendarModule = new CalendarModule();