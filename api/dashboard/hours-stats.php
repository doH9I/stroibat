<?php
require_once '../../config/database.php';
require_once '../../src/Auth.php';

header('Content-Type: application/json');

$auth = Auth::getInstance();
$auth->requireLogin();

$db = Database::getInstance();

try {
    // Get hours by project for current month
    $currentMonth = date('Y-m');
    $stats = $db->fetchAll("
        SELECT p.name, SUM(tt.hours_worked) as hours 
        FROM time_tracking tt 
        JOIN projects p ON tt.project_id = p.id 
        WHERE DATE_FORMAT(tt.date, '%Y-%m') = ? 
        GROUP BY p.id, p.name 
        ORDER BY hours DESC 
        LIMIT 10
    ", [$currentMonth]);
    
    $labels = [];
    $values = [];
    
    foreach ($stats as $stat) {
        $labels[] = $stat['name'];
        $values[] = round((float)$stat['hours'], 1);
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