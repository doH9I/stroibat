<?php
require_once 'config/database.php';
require_once 'src/Auth.php';
require_once 'src/Project.php';
require_once 'src/Estimate.php';
require_once 'src/TimeTracking.php';

// Load environment variables
$envFile = '.env';
if (file_exists($envFile)) {
    $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        if (strpos($line, '=') !== false && strpos($line, '#') !== 0) {
            list($key, $value) = explode('=', $line, 2);
            $_ENV[trim($key)] = trim($value);
        }
    }
}

$auth = Auth::getInstance();
$auth->requireLogin();

$project = new Project();
$estimate = new Estimate();
$timeTracking = new TimeTracking();

$projectStats = $project->getStatistics();
$estimateStats = $estimate->getStatistics();
$timeStats = $timeTracking->getStatistics();

$recentProjects = $project->getAll(['limit' => 5]);
$pendingTimeApprovals = $timeTracking->getPendingApprovals();
$recentEstimates = $estimate->getAll(['limit' => 5]);
?>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Construction CRM - Дашборд</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="assets/css/style.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <?php include 'includes/header.php'; ?>
    
    <div class="container-fluid">
        <div class="row">
            <?php include 'includes/sidebar.php'; ?>
            
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">Дашборд</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <div class="btn-group me-2">
                            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="exportData()">
                                <i class="fas fa-download"></i> Экспорт
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Statistics Cards -->
                <div class="row mb-4">
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-primary shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                            Всего проектов</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800"><?= $projectStats['total'] ?></div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-calendar fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-success shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                            Бюджет (₽)</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            <?= number_format($projectStats['total_budget'], 0, ',', ' ') ?>
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-info shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                            Сметы</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800"><?= $estimateStats['total'] ?></div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-clipboard-list fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-warning shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                            Часов в месяц</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800"><?= round($timeStats['total_hours_month'], 1) ?></div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-clock fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Charts Row -->
                <div class="row mb-4">
                    <div class="col-xl-8 col-lg-7">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">Проекты по статусам</h6>
                            </div>
                            <div class="card-body">
                                <canvas id="projectsChart" width="400" height="200"></canvas>
                            </div>
                        </div>
                    </div>

                    <div class="col-xl-4 col-lg-5">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">Часы по проектам</h6>
                            </div>
                            <div class="card-body">
                                <canvas id="hoursChart" width="400" height="200"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Content Row -->
                <div class="row">
                    <!-- Recent Projects -->
                    <div class="col-lg-6 mb-4">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Последние проекты</h6>
                            </div>
                            <div class="card-body">
                                <?php if (!empty($recentProjects)): ?>
                                    <?php foreach (array_slice($recentProjects, 0, 5) as $project): ?>
                                        <div class="d-flex justify-content-between align-items-center mb-3">
                                            <div>
                                                <h6 class="mb-0"><?= htmlspecialchars($project['name']) ?></h6>
                                                <small class="text-muted">
                                                    <?= htmlspecialchars($project['company_name'] ?? 'Без клиента') ?>
                                                </small>
                                            </div>
                                            <span class="badge bg-<?= getStatusColor($project['status']) ?>">
                                                <?= getStatusText($project['status']) ?>
                                            </span>
                                        </div>
                                    <?php endforeach; ?>
                                <?php else: ?>
                                    <p class="text-muted">Нет проектов</p>
                                <?php endif; ?>
                                <a href="projects.php" class="btn btn-primary btn-sm">Все проекты</a>
                            </div>
                        </div>
                    </div>

                    <!-- Pending Approvals -->
                    <div class="col-lg-6 mb-4">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Ожидают одобрения</h6>
                            </div>
                            <div class="card-body">
                                <?php if (!empty($pendingTimeApprovals)): ?>
                                    <?php foreach (array_slice($pendingTimeApprovals, 0, 5) as $approval): ?>
                                        <div class="d-flex justify-content-between align-items-center mb-3">
                                            <div>
                                                <h6 class="mb-0"><?= htmlspecialchars($approval['first_name'] . ' ' . $approval['last_name']) ?></h6>
                                                <small class="text-muted">
                                                    <?= htmlspecialchars($approval['project_name'] ?? 'Без проекта') ?> - 
                                                    <?= $approval['hours_worked'] ?> ч
                                                </small>
                                            </div>
                                            <small class="text-muted"><?= date('d.m.Y', strtotime($approval['date'])) ?></small>
                                        </div>
                                    <?php endforeach; ?>
                                <?php else: ?>
                                    <p class="text-muted">Нет записей для одобрения</p>
                                <?php endif; ?>
                                <a href="time-tracking.php" class="btn btn-primary btn-sm">Учет времени</a>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="assets/js/dashboard.js"></script>
</body>
</html>

<?php
function getStatusColor($status) {
    switch ($status) {
        case 'planning': return 'secondary';
        case 'in_progress': return 'primary';
        case 'completed': return 'success';
        case 'on_hold': return 'warning';
        case 'cancelled': return 'danger';
        default: return 'secondary';
    }
}

function getStatusText($status) {
    switch ($status) {
        case 'planning': return 'Планирование';
        case 'in_progress': return 'В работе';
        case 'completed': return 'Завершен';
        case 'on_hold': return 'Приостановлен';
        case 'cancelled': return 'Отменен';
        default: return $status;
    }
}
?>