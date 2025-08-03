<?php
require_once '../../config/database.php';
require_once '../../src/Auth.php';

header('Content-Type: application/json');

$auth = Auth::getInstance();
$auth->requireLogin();

$db = Database::getInstance();

try {
    $stats = [];
    
    // Projects statistics
    $projectStats = $db->fetch("SELECT COUNT(*) as total FROM projects");
    $budgetStats = $db->fetch("SELECT SUM(budget) as total_budget, SUM(actual_cost) as total_actual FROM projects WHERE budget IS NOT NULL");
    
    $stats['projects'] = [
        'total' => (int)$projectStats['total'],
        'total_budget' => (float)($budgetStats['total_budget'] ?? 0),
        'total_actual' => (float)($budgetStats['total_actual'] ?? 0)
    ];
    
    // Estimates statistics
    $estimateStats = $db->fetch("SELECT COUNT(*) as total FROM estimates");
    $estimateValueStats = $db->fetch("SELECT SUM(grand_total) as total_value FROM estimates WHERE status = 'approved'");
    
    $stats['estimates'] = [
        'total' => (int)$estimateStats['total'],
        'total_value' => (float)($estimateValueStats['total_value'] ?? 0)
    ];
    
    // Time tracking statistics
    $currentMonth = date('Y-m');
    $timeStats = $db->fetch("SELECT SUM(hours_worked) as total FROM time_tracking WHERE DATE_FORMAT(date, '%Y-%m') = ?", [$currentMonth]);
    $pendingApprovals = $db->fetch("SELECT COUNT(*) as count FROM time_tracking WHERE is_approved = 0");
    
    $stats['time'] = [
        'total_hours_month' => (float)($timeStats['total'] ?? 0),
        'pending_approvals' => (int)$pendingApprovals['count']
    ];
    
    echo json_encode($stats);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>