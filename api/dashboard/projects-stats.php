<?php
require_once '../../config/database.php';
require_once '../../src/Auth.php';

header('Content-Type: application/json');

$auth = Auth::getInstance();
$auth->requireLogin();

$db = Database::getInstance();

try {
    // Get projects by status
    $stats = $db->fetchAll("SELECT status, COUNT(*) as count FROM projects GROUP BY status");
    
    $labels = [];
    $values = [];
    
    $statusLabels = [
        'planning' => 'Планирование',
        'in_progress' => 'В работе',
        'completed' => 'Завершен',
        'on_hold' => 'Приостановлен',
        'cancelled' => 'Отменен'
    ];
    
    foreach ($stats as $stat) {
        $labels[] = $statusLabels[$stat['status']] ?? $stat['status'];
        $values[] = (int)$stat['count'];
    }
    
    echo json_encode([
        'labels' => $labels,
        'values' => $values
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>