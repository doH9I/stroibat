<?php

class TimeTracking {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    public function getAll($filters = []) {
        $sql = "SELECT tt.*, u.first_name, u.last_name, p.name as project_name, pt.name as task_name 
                FROM time_tracking tt 
                JOIN users u ON tt.user_id = u.id 
                LEFT JOIN projects p ON tt.project_id = p.id 
                LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                WHERE 1=1";
        $params = [];
        
        if (!empty($filters['user_id'])) {
            $sql .= " AND tt.user_id = ?";
            $params[] = $filters['user_id'];
        }
        
        if (!empty($filters['project_id'])) {
            $sql .= " AND tt.project_id = ?";
            $params[] = $filters['project_id'];
        }
        
        if (!empty($filters['date_from'])) {
            $sql .= " AND tt.date >= ?";
            $params[] = $filters['date_from'];
        }
        
        if (!empty($filters['date_to'])) {
            $sql .= " AND tt.date <= ?";
            $params[] = $filters['date_to'];
        }
        
        if (!empty($filters['is_approved'])) {
            $sql .= " AND tt.is_approved = ?";
            $params[] = $filters['is_approved'];
        }
        
        $sql .= " ORDER BY tt.date DESC, tt.start_time DESC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    public function getById($id) {
        return $this->db->fetch("SELECT tt.*, u.first_name, u.last_name, p.name as project_name, pt.name as task_name 
                                FROM time_tracking tt 
                                JOIN users u ON tt.user_id = u.id 
                                LEFT JOIN projects p ON tt.project_id = p.id 
                                LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                                WHERE tt.id = ?", [$id]);
    }
    
    public function create($data) {
        $sql = "INSERT INTO time_tracking (user_id, project_id, task_id, date, start_time, end_time, 
                                         hours_worked, description) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        $hoursWorked = $this->calculateHours($data['start_time'], $data['end_time']);
        
        $params = [
            $data['user_id'],
            $data['project_id'] ?? null,
            $data['task_id'] ?? null,
            $data['date'],
            $data['start_time'],
            $data['end_time'],
            $hoursWorked,
            $data['description'] ?? null
        ];
        
        $this->db->query($sql, $params);
        return $this->db->lastInsertId();
    }
    
    public function update($id, $data) {
        $sql = "UPDATE time_tracking SET project_id = ?, task_id = ?, date = ?, start_time = ?, 
                                    end_time = ?, hours_worked = ?, description = ?, 
                                    updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?";
        
        $hoursWorked = $this->calculateHours($data['start_time'], $data['end_time']);
        
        $params = [
            $data['project_id'] ?? null,
            $data['task_id'] ?? null,
            $data['date'],
            $data['start_time'],
            $data['end_time'],
            $hoursWorked,
            $data['description'] ?? null,
            $id
        ];
        
        return $this->db->query($sql, $params);
    }
    
    public function delete($id) {
        return $this->db->query("DELETE FROM time_tracking WHERE id = ?", [$id]);
    }
    
    public function approve($id, $approvedBy) {
        return $this->db->query("UPDATE time_tracking SET is_approved = 1, approved_by = ? WHERE id = ?", 
                                [$approvedBy, $id]);
    }
    
    public function reject($id, $approvedBy) {
        return $this->db->query("UPDATE time_tracking SET is_approved = 0, approved_by = ? WHERE id = ?", 
                                [$approvedBy, $id]);
    }
    
    public function getByUser($userId, $filters = []) {
        $sql = "SELECT tt.*, p.name as project_name, pt.name as task_name 
                FROM time_tracking tt 
                LEFT JOIN projects p ON tt.project_id = p.id 
                LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                WHERE tt.user_id = ?";
        $params = [$userId];
        
        if (!empty($filters['date_from'])) {
            $sql .= " AND tt.date >= ?";
            $params[] = $filters['date_from'];
        }
        
        if (!empty($filters['date_to'])) {
            $sql .= " AND tt.date <= ?";
            $params[] = $filters['date_to'];
        }
        
        $sql .= " ORDER BY tt.date DESC, tt.start_time DESC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    public function getByProject($projectId, $filters = []) {
        $sql = "SELECT tt.*, u.first_name, u.last_name, pt.name as task_name 
                FROM time_tracking tt 
                JOIN users u ON tt.user_id = u.id 
                LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                WHERE tt.project_id = ?";
        $params = [$projectId];
        
        if (!empty($filters['date_from'])) {
            $sql .= " AND tt.date >= ?";
            $params[] = $filters['date_from'];
        }
        
        if (!empty($filters['date_to'])) {
            $sql .= " AND tt.date <= ?";
            $params[] = $filters['date_to'];
        }
        
        $sql .= " ORDER BY tt.date DESC, tt.start_time DESC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    public function getPendingApprovals() {
        return $this->db->fetchAll("SELECT tt.*, u.first_name, u.last_name, p.name as project_name 
                                   FROM time_tracking tt 
                                   JOIN users u ON tt.user_id = u.id 
                                   LEFT JOIN projects p ON tt.project_id = p.id 
                                   WHERE tt.is_approved = 0 
                                   ORDER BY tt.date DESC");
    }
    
    public function getWeeklyReport($userId, $weekStart) {
        $weekEnd = date('Y-m-d', strtotime($weekStart . ' +6 days'));
        
        return $this->db->fetchAll("SELECT tt.*, p.name as project_name, pt.name as task_name 
                                   FROM time_tracking tt 
                                   LEFT JOIN projects p ON tt.project_id = p.id 
                                   LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                                   WHERE tt.user_id = ? AND tt.date BETWEEN ? AND ? 
                                   ORDER BY tt.date ASC, tt.start_time ASC", 
                                   [$userId, $weekStart, $weekEnd]);
    }
    
    public function getMonthlyReport($userId, $year, $month) {
        return $this->db->fetchAll("SELECT tt.*, p.name as project_name, pt.name as task_name 
                                   FROM time_tracking tt 
                                   LEFT JOIN projects p ON tt.project_id = p.id 
                                   LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                                   WHERE tt.user_id = ? AND YEAR(tt.date) = ? AND MONTH(tt.date) = ? 
                                   ORDER BY tt.date ASC, tt.start_time ASC", 
                                   [$userId, $year, $month]);
    }
    
    public function getTotalHours($userId, $dateFrom = null, $dateTo = null) {
        $sql = "SELECT SUM(hours_worked) as total_hours FROM time_tracking WHERE user_id = ?";
        $params = [$userId];
        
        if ($dateFrom) {
            $sql .= " AND date >= ?";
            $params[] = $dateFrom;
        }
        
        if ($dateTo) {
            $sql .= " AND date <= ?";
            $params[] = $dateTo;
        }
        
        $result = $this->db->fetch($sql, $params);
        return $result['total_hours'] ?? 0;
    }
    
    public function getProjectHours($projectId, $dateFrom = null, $dateTo = null) {
        $sql = "SELECT SUM(hours_worked) as total_hours FROM time_tracking WHERE project_id = ?";
        $params = [$projectId];
        
        if ($dateFrom) {
            $sql .= " AND date >= ?";
            $params[] = $dateFrom;
        }
        
        if ($dateTo) {
            $sql .= " AND date <= ?";
            $params[] = $dateTo;
        }
        
        $result = $this->db->fetch($sql, $params);
        return $result['total_hours'] ?? 0;
    }
    
    public function getStatistics() {
        $stats = [];
        
        // Total hours this month
        $currentMonth = date('Y-m');
        $stats['total_hours_month'] = $this->db->fetch("SELECT SUM(hours_worked) as total 
                                                       FROM time_tracking 
                                                       WHERE DATE_FORMAT(date, '%Y-%m') = ?", 
                                                       [$currentMonth])['total'] ?? 0;
        
        // Pending approvals
        $stats['pending_approvals'] = $this->db->fetch("SELECT COUNT(*) as count 
                                                       FROM time_tracking 
                                                       WHERE is_approved = 0")['count'];
        
        // Hours by project this month
        $stats['hours_by_project'] = $this->db->fetchAll("SELECT p.name, SUM(tt.hours_worked) as hours 
                                                         FROM time_tracking tt 
                                                         JOIN projects p ON tt.project_id = p.id 
                                                         WHERE DATE_FORMAT(tt.date, '%Y-%m') = ? 
                                                         GROUP BY p.id, p.name 
                                                         ORDER BY hours DESC", 
                                                         [$currentMonth]);
        
        // Hours by user this month
        $stats['hours_by_user'] = $this->db->fetchAll("SELECT u.first_name, u.last_name, SUM(tt.hours_worked) as hours 
                                                       FROM time_tracking tt 
                                                       JOIN users u ON tt.user_id = u.id 
                                                       WHERE DATE_FORMAT(tt.date, '%Y-%m') = ? 
                                                       GROUP BY u.id, u.first_name, u.last_name 
                                                       ORDER BY hours DESC", 
                                                       [$currentMonth]);
        
        return $stats;
    }
    
    private function calculateHours($startTime, $endTime) {
        if (!$startTime || !$endTime) {
            return 0;
        }
        
        $start = strtotime($startTime);
        $end = strtotime($endTime);
        
        if ($start === false || $end === false) {
            return 0;
        }
        
        $diff = $end - $start;
        return round($diff / 3600, 2); // Convert seconds to hours
    }
    
    public function startTimer($userId, $projectId = null, $taskId = null) {
        $currentTime = date('H:i:s');
        $currentDate = date('Y-m-d');
        
        // Check if there's already an active timer
        $activeTimer = $this->db->fetch("SELECT * FROM time_tracking 
                                        WHERE user_id = ? AND date = ? AND end_time IS NULL", 
                                        [$userId, $currentDate]);
        
        if ($activeTimer) {
            throw new Exception('У вас уже есть активный таймер');
        }
        
        $sql = "INSERT INTO time_tracking (user_id, project_id, task_id, date, start_time, hours_worked) 
                VALUES (?, ?, ?, ?, ?, 0)";
        
        $params = [$userId, $projectId, $taskId, $currentDate, $currentTime];
        
        $this->db->query($sql, $params);
        return $this->db->lastInsertId();
    }
    
    public function stopTimer($userId) {
        $currentTime = date('H:i:s');
        $currentDate = date('Y-m-d');
        
        $activeTimer = $this->db->fetch("SELECT * FROM time_tracking 
                                        WHERE user_id = ? AND date = ? AND end_time IS NULL", 
                                        [$userId, $currentDate]);
        
        if (!$activeTimer) {
            throw new Exception('Нет активного таймера');
        }
        
        $hoursWorked = $this->calculateHours($activeTimer['start_time'], $currentTime);
        
        return $this->db->query("UPDATE time_tracking SET end_time = ?, hours_worked = ? WHERE id = ?", 
                                [$currentTime, $hoursWorked, $activeTimer['id']]);
    }
    
    public function getActiveTimer($userId) {
        $currentDate = date('Y-m-d');
        
        return $this->db->fetch("SELECT tt.*, p.name as project_name, pt.name as task_name 
                                FROM time_tracking tt 
                                LEFT JOIN projects p ON tt.project_id = p.id 
                                LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                                WHERE tt.user_id = ? AND tt.date = ? AND tt.end_time IS NULL", 
                                [$userId, $currentDate]);
    }
}