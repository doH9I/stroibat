<?php
require_once 'config/database.php';
require_once 'src/Auth.php';
require_once 'src/Client.php';

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

$client = new Client();

// Handle filters
$filters = [];
if (!empty($_GET['status'])) {
    $filters['status'] = $_GET['status'];
}
if (!empty($_GET['search'])) {
    $filters['search'] = $_GET['search'];
}

$clients = $client->getAll($filters);
$stats = $client->getStatistics();
?>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Клиенты - Construction CRM</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="assets/css/style.css" rel="stylesheet">
</head>
<body>
    <?php include 'includes/header.php'; ?>
    
    <div class="container-fluid">
        <div class="row">
            <?php include 'includes/sidebar.php'; ?>
            
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">Клиенты</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <div class="btn-group me-2">
                            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="exportClients()">
                                <i class="fas fa-download"></i> Экспорт
                            </button>
                        </div>
                        <a href="client-form.php" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Новый клиент
                        </a>
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
                                            Всего клиентов</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800"><?= $stats['total'] ?></div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-users fa-2x text-gray-300"></i>
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
                                            Активные с проектами</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800"><?= $stats['active_with_projects'] ?></div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-check-circle fa-2x text-gray-300"></i>
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
                                            Общий доход (₽)</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            <?= number_format($stats['total_revenue'], 0, ',', ' ') ?>
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
                        <div class="card border-left-warning shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                            Потенциальные</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            <?= $stats['by_status']['potential'] ?? 0 ?>
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-lightbulb fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Filters -->
                <div class="card mb-4">
                    <div class="card-body">
                        <form method="GET" class="row g-3">
                            <div class="col-md-4">
                                <label for="search" class="form-label">Поиск</label>
                                <input type="text" class="form-control" id="search" name="search" 
                                       value="<?= htmlspecialchars($_GET['search'] ?? '') ?>" 
                                       placeholder="Название компании, контактное лицо, email">
                            </div>
                            <div class="col-md-3">
                                <label for="status" class="form-label">Статус</label>
                                <select class="form-select" id="status" name="status">
                                    <option value="">Все статусы</option>
                                    <option value="active" <?= ($_GET['status'] ?? '') === 'active' ? 'selected' : '' ?>>Активные</option>
                                    <option value="inactive" <?= ($_GET['status'] ?? '') === 'inactive' ? 'selected' : '' ?>>Неактивные</option>
                                    <option value="potential" <?= ($_GET['status'] ?? '') === 'potential' ? 'selected' : '' ?>>Потенциальные</option>
                                </select>
                            </div>
                            <div class="col-md-3 d-flex align-items-end">
                                <button type="submit" class="btn btn-primary me-2">
                                    <i class="fas fa-search"></i> Поиск
                                </button>
                                <a href="clients.php" class="btn btn-outline-secondary">
                                    <i class="fas fa-times"></i> Сброс
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Clients Table -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Список клиентов (<?= count($clients) ?>)</h5>
                    </div>
                    <div class="card-body p-0">
                        <?php if (empty($clients)): ?>
                            <div class="text-center py-5">
                                <i class="fas fa-users fa-3x text-muted mb-3"></i>
                                <h5 class="text-muted">Клиенты не найдены</h5>
                                <p class="text-muted">Добавьте первого клиента или измените фильтры</p>
                                <a href="client-form.php" class="btn btn-primary">
                                    <i class="fas fa-plus"></i> Добавить клиента
                                </a>
                            </div>
                        <?php else: ?>
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Клиент</th>
                                            <th>Контактное лицо</th>
                                            <th>Email</th>
                                            <th>Телефон</th>
                                            <th>Статус</th>
                                            <th>Проекты</th>
                                            <th>Действия</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <?php foreach ($clients as $client): ?>
                                            <tr>
                                                <td>
                                                    <div>
                                                        <strong><?= htmlspecialchars($client['company_name'] ?: $client['contact_person']) ?></strong>
                                                        <?php if ($client['company_name'] && $client['contact_person']): ?>
                                                            <br><small class="text-muted"><?= htmlspecialchars($client['contact_person']) ?></small>
                                                        <?php endif; ?>
                                                    </div>
                                                </td>
                                                <td>
                                                    <?= htmlspecialchars($client['contact_person']) ?>
                                                </td>
                                                <td>
                                                    <a href="mailto:<?= htmlspecialchars($client['email']) ?>">
                                                        <?= htmlspecialchars($client['email']) ?>
                                                    </a>
                                                </td>
                                                <td>
                                                    <?php if ($client['phone']): ?>
                                                        <a href="tel:<?= htmlspecialchars($client['phone']) ?>">
                                                            <?= htmlspecialchars($client['phone']) ?>
                                                        </a>
                                                    <?php else: ?>
                                                        <span class="text-muted">Не указан</span>
                                                    <?php endif; ?>
                                                </td>
                                                <td>
                                                    <span class="badge bg-<?= getStatusColor($client['status']) ?>">
                                                        <?= getStatusText($client['status']) ?>
                                                    </span>
                                                </td>
                                                <td>
                                                    <?php 
                                                    $clientProjects = $client->getProjects($client['id']);
                                                    $projectCount = count($clientProjects);
                                                    ?>
                                                    <span class="badge bg-info"><?= $projectCount ?></span>
                                                    <?php if ($projectCount > 0): ?>
                                                        <small class="text-muted">проектов</small>
                                                    <?php endif; ?>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm" role="group">
                                                        <a href="client-details.php?id=<?= $client['id'] ?>" 
                                                           class="btn btn-outline-primary" 
                                                           data-bs-toggle="tooltip" 
                                                           title="Просмотр">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        <a href="client-form.php?id=<?= $client['id'] ?>" 
                                                           class="btn btn-outline-secondary" 
                                                           data-bs-toggle="tooltip" 
                                                           title="Редактировать">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                        <button type="button" 
                                                                class="btn btn-outline-danger" 
                                                                onclick="deleteClient(<?= $client['id'] ?>, '<?= htmlspecialchars($client['company_name'] ?: $client['contact_person']) ?>')"
                                                                data-bs-toggle="tooltip" 
                                                                title="Удалить">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        <?php endforeach; ?>
                                    </tbody>
                                </table>
                            </div>
                        <?php endif; ?>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Подтверждение удаления</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Вы действительно хотите удалить клиента <strong id="clientName"></strong>?</p>
                    <p class="text-danger"><small>Это действие нельзя отменить!</small></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                    <button type="button" class="btn btn-danger" id="confirmDelete">Удалить</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Initialize tooltips
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });

        function deleteClient(id, name) {
            document.getElementById('clientName').textContent = name;
            document.getElementById('confirmDelete').onclick = function() {
                window.location.href = `client-delete.php?id=${id}`;
            };
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }

        function exportClients() {
            const format = prompt('Выберите формат экспорта (csv, xlsx, pdf):', 'csv');
            if (format) {
                const params = new URLSearchParams(window.location.search);
                params.append('format', format);
                window.open(`api/export/clients.php?${params.toString()}`, '_blank');
            }
        }
    </script>
</body>
</html>

<?php
function getStatusColor($status) {
    switch ($status) {
        case 'active': return 'success';
        case 'inactive': return 'secondary';
        case 'potential': return 'warning';
        default: return 'secondary';
    }
}

function getStatusText($status) {
    switch ($status) {
        case 'active': return 'Активный';
        case 'inactive': return 'Неактивный';
        case 'potential': return 'Потенциальный';
        default: return $status;
    }
}
?>