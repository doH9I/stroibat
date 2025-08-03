<nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse">
    <div class="position-sticky pt-3">
        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'index.php' ? 'active' : '' ?>" href="index.php">
                    <i class="fas fa-tachometer-alt me-2"></i>
                    Дашборд
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'projects.php' ? 'active' : '' ?>" href="projects.php">
                    <i class="fas fa-project-diagram me-2"></i>
                    Проекты
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'estimates.php' ? 'active' : '' ?>" href="estimates.php">
                    <i class="fas fa-clipboard-list me-2"></i>
                    Сметы
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'clients.php' ? 'active' : '' ?>" href="clients.php">
                    <i class="fas fa-users me-2"></i>
                    Клиенты
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'time-tracking.php' ? 'active' : '' ?>" href="time-tracking.php">
                    <i class="fas fa-clock me-2"></i>
                    Учет времени
                </a>
            </li>
        </ul>

        <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
            <span>Управление ресурсами</span>
        </h6>
        <ul class="nav flex-column mb-2">
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'materials.php' ? 'active' : '' ?>" href="materials.php">
                    <i class="fas fa-boxes me-2"></i>
                    Материалы
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'equipment.php' ? 'active' : '' ?>" href="equipment.php">
                    <i class="fas fa-tools me-2"></i>
                    Оборудование
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'invoices.php' ? 'active' : '' ?>" href="invoices.php">
                    <i class="fas fa-file-invoice me-2"></i>
                    Счета
                </a>
            </li>
        </ul>

        <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
            <span>Аналитика</span>
        </h6>
        <ul class="nav flex-column mb-2">
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'reports.php' ? 'active' : '' ?>" href="reports.php">
                    <i class="fas fa-chart-bar me-2"></i>
                    Отчеты
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'analytics.php' ? 'active' : '' ?>" href="analytics.php">
                    <i class="fas fa-chart-line me-2"></i>
                    Аналитика
                </a>
            </li>
        </ul>

        <?php if ($auth->hasRole('admin')): ?>
        <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
            <span>Администрирование</span>
        </h6>
        <ul class="nav flex-column mb-2">
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'users.php' ? 'active' : '' ?>" href="users.php">
                    <i class="fas fa-users-cog me-2"></i>
                    Пользователи
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link <?= basename($_SERVER['PHP_SELF']) == 'settings.php' ? 'active' : '' ?>" href="settings.php">
                    <i class="fas fa-cog me-2"></i>
                    Настройки
                </a>
            </li>
        </ul>
        <?php endif; ?>
    </div>
</nav>