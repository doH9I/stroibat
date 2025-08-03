<nav class="navbar navbar-expand-lg navbar-dark bg-primary fixed-top">
    <div class="container-fluid">
        <a class="navbar-brand" href="index.php">
            <i class="fas fa-building me-2"></i>
            Construction CRM
        </a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="index.php">
                        <i class="fas fa-tachometer-alt me-1"></i> Дашборд
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="projects.php">
                        <i class="fas fa-project-diagram me-1"></i> Проекты
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="estimates.php">
                        <i class="fas fa-clipboard-list me-1"></i> Сметы
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="clients.php">
                        <i class="fas fa-users me-1"></i> Клиенты
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="time-tracking.php">
                        <i class="fas fa-clock me-1"></i> Учет времени
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="materials.php">
                        <i class="fas fa-boxes me-1"></i> Материалы
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="equipment.php">
                        <i class="fas fa-tools me-1"></i> Оборудование
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="invoices.php">
                        <i class="fas fa-file-invoice me-1"></i> Счета
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="reports.php">
                        <i class="fas fa-chart-bar me-1"></i> Отчеты
                    </a>
                </li>
            </ul>
            
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                        <i class="fas fa-user-circle me-1"></i>
                        <?= htmlspecialchars($auth->getUser()['first_name'] . ' ' . $auth->getUser()['last_name']) ?>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="profile.php">
                            <i class="fas fa-user me-2"></i> Профиль
                        </a></li>
                        <li><a class="dropdown-item" href="settings.php">
                            <i class="fas fa-cog me-2"></i> Настройки
                        </a></li>
                        <?php if ($auth->hasRole('admin')): ?>
                        <li><a class="dropdown-item" href="users.php">
                            <i class="fas fa-users-cog me-2"></i> Пользователи
                        </a></li>
                        <?php endif; ?>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="logout.php">
                            <i class="fas fa-sign-out-alt me-2"></i> Выйти
                        </a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div style="margin-top: 76px;"></div>