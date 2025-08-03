// Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize charts
    initializeCharts();
    
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Auto-refresh dashboard data every 5 minutes
    setInterval(refreshDashboardData, 300000);
});

function initializeCharts() {
    // Projects by Status Chart
    const projectsCtx = document.getElementById('projectsChart');
    if (projectsCtx) {
        fetch('api/dashboard/projects-stats.php')
            .then(response => response.json())
            .then(data => {
                new Chart(projectsCtx, {
                    type: 'doughnut',
                    data: {
                        labels: data.labels,
                        datasets: [{
                            data: data.values,
                            backgroundColor: [
                                '#6c757d', // Planning
                                '#007bff', // In Progress
                                '#28a745', // Completed
                                '#ffc107', // On Hold
                                '#dc3545'  // Cancelled
                            ],
                            borderWidth: 2,
                            borderColor: '#fff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    padding: 20,
                                    usePointStyle: true
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.parsed;
                                        const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                        const percentage = ((value / total) * 100).toFixed(1);
                                        return `${label}: ${value} (${percentage}%)`;
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error loading projects chart:', error));
    }
    
    // Hours by Project Chart
    const hoursCtx = document.getElementById('hoursChart');
    if (hoursCtx) {
        fetch('api/dashboard/hours-stats.php')
            .then(response => response.json())
            .then(data => {
                new Chart(hoursCtx, {
                    type: 'bar',
                    data: {
                        labels: data.labels,
                        datasets: [{
                            label: 'Часы',
                            data: data.values,
                            backgroundColor: 'rgba(0, 123, 255, 0.8)',
                            borderColor: 'rgba(0, 123, 255, 1)',
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
                                        return value + ' ч';
                                    }
                                }
                            }
                        },
                        plugins: {
                            legend: {
                                display: false
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return context.parsed.y + ' часов';
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error loading hours chart:', error));
    }
}

function refreshDashboardData() {
    // Refresh statistics cards
    fetch('api/dashboard/stats.php')
        .then(response => response.json())
        .then(data => {
            updateStatisticsCards(data);
        })
        .catch(error => console.error('Error refreshing dashboard data:', error));
}

function updateStatisticsCards(data) {
    // Update project count
    const projectCount = document.querySelector('.card-body .h5');
    if (projectCount) {
        projectCount.textContent = data.projects.total;
    }
    
    // Update budget
    const budgetElement = document.querySelector('.border-left-success .h5');
    if (budgetElement) {
        budgetElement.textContent = new Intl.NumberFormat('ru-RU').format(data.projects.total_budget);
    }
    
    // Update estimates count
    const estimatesElement = document.querySelector('.border-left-info .h5');
    if (estimatesElement) {
        estimatesElement.textContent = data.estimates.total;
    }
    
    // Update hours
    const hoursElement = document.querySelector('.border-left-warning .h5');
    if (hoursElement) {
        hoursElement.textContent = data.time.total_hours_month.toFixed(1);
    }
}

function exportData() {
    const format = prompt('Выберите формат экспорта (csv, xlsx, pdf):', 'csv');
    if (format) {
        window.open(`api/export/dashboard.php?format=${format}`, '_blank');
    }
}

// Real-time notifications
function checkNotifications() {
    fetch('api/notifications/unread.php')
        .then(response => response.json())
        .then(data => {
            if (data.count > 0) {
                showNotificationBadge(data.count);
            }
        })
        .catch(error => console.error('Error checking notifications:', error));
}

function showNotificationBadge(count) {
    let badge = document.getElementById('notification-badge');
    if (!badge) {
        const navbar = document.querySelector('.navbar-nav');
        const badgeElement = document.createElement('span');
        badgeElement.id = 'notification-badge';
        badgeElement.className = 'badge bg-danger position-absolute top-0 start-100 translate-middle';
        badgeElement.style.fontSize = '0.6rem';
        navbar.appendChild(badgeElement);
        badge = badgeElement;
    }
    badge.textContent = count;
    badge.style.display = count > 0 ? 'block' : 'none';
}

// Check for notifications every 30 seconds
setInterval(checkNotifications, 30000);

// Search functionality
function searchProjects(query) {
    if (query.length < 2) return;
    
    fetch(`api/search/projects.php?q=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
            displaySearchResults(data);
        })
        .catch(error => console.error('Error searching projects:', error));
}

function displaySearchResults(results) {
    const resultsContainer = document.getElementById('search-results');
    if (!resultsContainer) return;
    
    resultsContainer.innerHTML = '';
    
    if (results.length === 0) {
        resultsContainer.innerHTML = '<div class="p-3 text-muted">Ничего не найдено</div>';
        return;
    }
    
    results.forEach(project => {
        const resultItem = document.createElement('div');
        resultItem.className = 'dropdown-item d-flex justify-content-between align-items-center';
        resultItem.innerHTML = `
            <div>
                <strong>${project.name}</strong><br>
                <small class="text-muted">${project.company_name || 'Без клиента'}</small>
            </div>
            <span class="badge bg-${getStatusColor(project.status)}">${getStatusText(project.status)}</span>
        `;
        resultItem.addEventListener('click', () => {
            window.location.href = `project-details.php?id=${project.id}`;
        });
        resultsContainer.appendChild(resultItem);
    });
}

function getStatusColor(status) {
    const colors = {
        'planning': 'secondary',
        'in_progress': 'primary',
        'completed': 'success',
        'on_hold': 'warning',
        'cancelled': 'danger'
    };
    return colors[status] || 'secondary';
}

function getStatusText(status) {
    const texts = {
        'planning': 'Планирование',
        'in_progress': 'В работе',
        'completed': 'Завершен',
        'on_hold': 'Приостановлен',
        'cancelled': 'Отменен'
    };
    return texts[status] || status;
}

// Quick actions
function quickAddProject() {
    window.location.href = 'project-form.php';
}

function quickAddEstimate() {
    window.location.href = 'estimate-form.php';
}

function quickTimeEntry() {
    window.location.href = 'time-entry.php';
}

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + N for new project
    if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
        e.preventDefault();
        quickAddProject();
    }
    
    // Ctrl/Cmd + E for new estimate
    if ((e.ctrlKey || e.metaKey) && e.key === 'e') {
        e.preventDefault();
        quickAddEstimate();
    }
    
    // Ctrl/Cmd + T for time entry
    if ((e.ctrlKey || e.metaKey) && e.key === 't') {
        e.preventDefault();
        quickTimeEntry();
    }
    
    // Ctrl/Cmd + K for search
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        document.getElementById('search-input').focus();
    }
});

// Print dashboard
function printDashboard() {
    window.print();
}

// Export dashboard as PDF
function exportAsPDF() {
    window.open('api/export/dashboard-pdf.php', '_blank');
}

// Share dashboard
function shareDashboard() {
    if (navigator.share) {
        navigator.share({
            title: 'Construction CRM Dashboard',
            url: window.location.href
        });
    } else {
        // Fallback: copy URL to clipboard
        navigator.clipboard.writeText(window.location.href).then(() => {
            alert('Ссылка скопирована в буфер обмена');
        });
    }
}