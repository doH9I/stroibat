<?php
require_once 'config/database.php';
require_once 'src/Auth.php';
require_once 'src/Project.php';

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

// Handle filters
$filters = [];
if (!empty($_GET['status'])) {
    $filters['status'] = $_GET['status'];
}
if (!empty($_GET['manager_id'])) {
    $filters['manager_id'] = $_GET['manager_id'];
}
if (!empty($_GET['client_id'])) {
    $filters['client_id'] = $_GET['client_id'];
}

$projects = $project->getAll($filters);

// Get managers for filter
$db = Database::getInstance();
$managers = $db->fetchAll("SELECT id, first_name, last_name FROM users WHERE role IN ('admin', 'manager') ORDER BY first_name, last_name");

// Get clients for filter
$clients = $db->fetchAll("SELECT id, company_name, contact_person FROM clients ORDER BY company_name");
?>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Проекты - Construction CRM</title>
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
                    <h1 class="h2">Проекты</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <div class="btn-group me-2">
                            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="exportProjects()">
                                <i class="fas fa-download"></i> Экспорт
                            </button>
                        </div>
                        <a href="project-form.php" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Новый проект
                        </a>
                    </div>
                </div>

                <!-- Filters -->
                <div class="card mb-4">
                    <div class="card-body">
                        <form method="GET" class="row g-3">
                            <div class="col-md-3">
                                <label for="status" class="form-label">Статус</label>
                                <select class="form-select" id="status" name="status">
                                    <option value="">Все статусы</option>
                                    <option value="planning" <?= ($_GET['status'] ?? '') === 'planning' ? 'selected' : '' ?>>Планирование</option>
                                    <option value="in_progress" <?= ($_GET['status'] ?? '') === 'in_progress' ? 'selected' : '' ?>>В работе</option>
                                    <option value="completed" <?= ($_GET['status'] ?? '') === 'completed' ? 'selected' : '' ?>>Завершен</option>
                                    <option value="on_hold" <?= ($_GET['status'] ?? '') === 'on_hold' ? 'selected' : '' ?>>Приостановлен</option>
                                    <option value="cancelled" <?= ($_GET['status'] ?? '') === 'cancelled' ? 'selected' : '' ?>>Отменен</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label for="manager_id" class="form-label">Менеджер</label>
                                <select class="form-select" id="manager_id" name="manager_id">
                                    <option value="">Все менеджеры</option>
                                    <?php foreach ($managers as $manager): ?>
                                        <option value="<?= $manager['id'] ?>" <?= ($_GET['manager_id'] ?? '') == $manager['id'] ? 'selected' : '' ?>>
                                            <?= htmlspecialchars($manager['first_name'] . ' ' . $manager['last_name']) ?>
                                        </option>
                                    <?php endforeach; ?>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label for="client_id" class="form-label">Клиент</label>
                                <select class="form-select" id="client_id" name="client_id">
                                    <option value="">Все клиенты</option>
                                    <?php foreach ($clients as $client): ?>
                                        <option value="<?= $client['id'] ?>" <?= ($_GET['client_id'] ?? '') == $client['id'] ? 'selected' : '' ?>>
                                            <?= htmlspecialchars($client['company_name'] ?: $client['contact_person']) ?>
                                        </option>
                                    <?php endforeach; ?>
                                </select>
                            </div>
                            <div class="col-md-3 d-flex align-items-end">
                                <button type="submit" class="btn btn-primary me-2">
                                    <i class="fas fa-search"></i> Фильтр
                                </button>
                                <a href="projects.php" class="btn btn-outline-secondary">
                                    <i class="fas fa-times"></i> Сброс
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Projects Table -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Список проектов (<?= count($projects) ?>)</h5>
                    </div>
                    <div class="card-body p-0">
                        <?php if (empty($projects)): ?>
                            <div class="text-center py-5">
                                <i class="fas fa-folder-open fa-3x text-muted mb-3"></i>
                                <h5 class="text-muted">Проекты не найдены</h5>
                                <p class="text-muted">Создайте первый проект или измените фильтры</p>
                                <a href="project-form.php" class="btn btn-primary">
                                    <i class="fas fa-plus"></i> Создать проект
                                </a>
                            </div>
                        <?php else: ?>
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Название</th>
                                            <th>Клиент</th>
                                            <th>Менеджер</th>
                                            <th>Статус</th>
                                            <th>Приоритет</th>
                                            <th>Бюджет</th>
                                            <th>Дата начала</th>
                                            <th>Действия</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <?php foreach ($projects as $project): ?>
                                            <tr>
                                                <td>
                                                    <div>
                                                        <strong><?= htmlspecialchars($project['name']) ?></strong>
                                                        <?php if ($project['description']): ?>
                                                            <br><small class="text-muted text-truncate-2"><?= htmlspecialchars($project['description']) ?></small>
                                                        <?php endif; ?>
                                                    </div>
                                                </td>
                                                <td>
                                                    <?php if ($project['company_name']): ?>
                                                        <span class="text-truncate-2"><?= htmlspecialchars($project['company_name']) ?></span>
                                                    <?php else: ?>
                                                        <span class="text-muted">Не указан</span>
                                                    <?php endif; ?>
                                                </td>
                                                <td>
                                                    <?php if ($project['manager_name']): ?>
                                                        <?= htmlspecialchars($project['manager_name']) ?>
                                                    <?php else: ?>
                                                        <span class="text-muted">Не назначен</span>
                                                    <?php endif; ?>
                                                </td>
                                                <td>
                                                    <span class="badge bg-<?= getStatusColor($project['status']) ?>">
                                                        <?= getStatusText($project['status']) ?>
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="priority-<?= $project['priority'] ?>">
                                                        <i class="fas fa-flag"></i>
                                                        <?= getPriorityText($project['priority']) ?>
                                                    </span>
                                                </td>
                                                <td>
                                                    <?php if ($project['budget']): ?>
                                                        <strong><?= number_format($project['budget'], 0, ',', ' ') ?> ₽</strong>
                                                        <?php if ($project['actual_cost']): ?>
                                                            <br><small class="text-muted">
                                                                Факт: <?= number_format($project['actual_cost'], 0, ',', ' ') ?> ₽
                                                            </small>
                                                        <?php endif; ?>
                                                    <?php else: ?>
                                                        <span class="text-muted">Не указан</span>
                                                    <?php endif; ?>
                                                </td>
                                                <td>
                                                    <?php if ($project['start_date']): ?>
                                                        <?= date('d.m.Y', strtotime($project['start_date'])) ?>
                                                    <?php else: ?>
                                                        <span class="text-muted">Не указана</span>
                                                    <?php endif; ?>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm" role="group">
                                                        <a href="project-details.php?id=<?= $project['id'] ?>" 
                                                           class="btn btn-outline-primary" 
                                                           data-bs-toggle="tooltip" 
                                                           title="Просмотр">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        <a href="project-form.php?id=<?= $project['id'] ?>" 
                                                           class="btn btn-outline-secondary" 
                                                           data-bs-toggle="tooltip" 
                                                           title="Редактировать">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                        <button type="button" 
                                                                class="btn btn-outline-danger" 
                                                                onclick="deleteProject(<?= $project['id'] ?>, '<?= htmlspecialchars($project['name']) ?>')"
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
                    <p>Вы действительно хотите удалить проект <strong id="projectName"></strong>?</p>
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

        function deleteProject(id, name) {
            document.getElementById('projectName').textContent = name;
            document.getElementById('confirmDelete').onclick = function() {
                window.location.href = `project-delete.php?id=${id}`;
            };
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }

        function exportProjects() {
            const format = prompt('Выберите формат экспорта (csv, xlsx, pdf):', 'csv');
            if (format) {
                const params = new URLSearchParams(window.location.search);
                params.append('format', format);
                window.open(`api/export/projects.php?${params.toString()}`, '_blank');
            }
        }

        // Auto-submit form on filter change
        document.getElementById('status').addEventListener('change', function() {
            this.form.submit();
        });

        document.getElementById('manager_id').addEventListener('change', function() {
            this.form.submit();
        });

        document.getElementById('client_id').addEventListener('change', function() {
            this.form.submit();
        });
    </script>
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

function getPriorityText($priority) {
    switch ($priority) {
        case 'low': return 'Низкий';
        case 'medium': return 'Средний';
        case 'high': return 'Высокий';
        case 'urgent': return 'Срочный';
        default: return $priority;
    }
}
?>