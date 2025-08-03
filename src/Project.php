<?php

class Project {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    public function getAll($filters = []) {
        $sql = "SELECT p.*, c.company_name, c.contact_person, u.first_name, u.last_name as manager_name 
                FROM projects p 
                LEFT JOIN clients c ON p.client_id = c.id 
                LEFT JOIN users u ON p.manager_id = u.id 
                WHERE 1=1";
        $params = [];
        
        if (!empty($filters['status'])) {
            $sql .= " AND p.status = ?";
            $params[] = $filters['status'];
        }
        
        if (!empty($filters['manager_id'])) {
            $sql .= " AND p.manager_id = ?";
            $params[] = $filters['manager_id'];
        }
        
        if (!empty($filters['client_id'])) {
            $sql .= " AND p.client_id = ?";
            $params[] = $filters['client_id'];
        }
        
        $sql .= " ORDER BY p.created_at DESC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    public function getById($id) {
        return $this->db->fetch("SELECT p.*, c.company_name, c.contact_person, c.email as client_email, 
                                       c.phone as client_phone, u.first_name, u.last_name as manager_name 
                                FROM projects p 
                                LEFT JOIN clients c ON p.client_id = c.id 
                                LEFT JOIN users u ON p.manager_id = u.id 
                                WHERE p.id = ?", [$id]);
    }
    
    public function create($data) {
        $sql = "INSERT INTO projects (name, description, client_id, manager_id, start_date, end_date, 
                                    budget, status, priority, location, contract_number) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        $params = [
            $data['name'],
            $data['description'] ?? null,
            $data['client_id'] ?? null,
            $data['manager_id'] ?? null,
            $data['start_date'] ?? null,
            $data['end_date'] ?? null,
            $data['budget'] ?? null,
            $data['status'] ?? 'planning',
            $data['priority'] ?? 'medium',
            $data['location'] ?? null,
            $data['contract_number'] ?? null
        ];
        
        $this->db->query($sql, $params);
        return $this->db->lastInsertId();
    }
    
    public function update($id, $data) {
        $sql = "UPDATE projects SET name = ?, description = ?, client_id = ?, manager_id = ?, 
                                    start_date = ?, end_date = ?, budget = ?, status = ?, priority = ?, 
                                    location = ?, contract_number = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?";
        
        $params = [
            $data['name'],
            $data['description'] ?? null,
            $data['client_id'] ?? null,
            $data['manager_id'] ?? null,
            $data['start_date'] ?? null,
            $data['end_date'] ?? null,
            $data['budget'] ?? null,
            $data['status'] ?? 'planning',
            $data['priority'] ?? 'medium',
            $data['location'] ?? null,
            $data['contract_number'] ?? null,
            $id
        ];
        
        return $this->db->query($sql, $params);
    }
    
    public function delete($id) {
        return $this->db->query("DELETE FROM projects WHERE id = ?", [$id]);
    }
    
    public function getTasks($projectId) {
        return $this->db->fetchAll("SELECT pt.*, u.first_name, u.last_name as assigned_name 
                                   FROM project_tasks pt 
                                   LEFT JOIN users u ON pt.assigned_to = u.id 
                                   WHERE pt.project_id = ? 
                                   ORDER BY pt.due_date ASC", [$projectId]);
    }
    
    public function getMaterials($projectId) {
        return $this->db->fetchAll("SELECT pm.*, m.name as material_name, m.unit as material_unit 
                                   FROM project_materials pm 
                                   JOIN materials m ON pm.material_id = m.id 
                                   WHERE pm.project_id = ? 
                                   ORDER BY pm.date_used DESC", [$projectId]);
    }
    
    public function getEstimates($projectId) {
        return $this->db->fetchAll("SELECT e.*, u.first_name, u.last_name as created_by_name 
                                   FROM estimates e 
                                   JOIN users u ON e.created_by = u.id 
                                   WHERE e.project_id = ? 
                                   ORDER BY e.created_at DESC", [$projectId]);
    }
    
    public function getInvoices($projectId) {
        return $this->db->fetchAll("SELECT i.*, u.first_name, u.last_name as created_by_name 
                                   FROM invoices i 
                                   JOIN users u ON i.created_by = u.id 
                                   WHERE i.project_id = ? 
                                   ORDER BY i.issue_date DESC", [$projectId]);
    }
    
    public function getTimeTracking($projectId) {
        return $this->db->fetchAll("SELECT tt.*, u.first_name, u.last_name, pt.name as task_name 
                                   FROM time_tracking tt 
                                   JOIN users u ON tt.user_id = u.id 
                                   LEFT JOIN project_tasks pt ON tt.task_id = pt.id 
                                   WHERE tt.project_id = ? 
                                   ORDER BY tt.date DESC, tt.start_time DESC", [$projectId]);
    }
    
    public function getDocuments($projectId) {
        return $this->db->fetchAll("SELECT d.*, u.first_name, u.last_name as uploaded_by_name 
                                   FROM documents d 
                                   JOIN users u ON d.uploaded_by = u.id 
                                   WHERE d.project_id = ? 
                                   ORDER BY d.created_at DESC", [$projectId]);
    }
    
    public function getStatistics() {
        $stats = [];
        
        // Total projects
        $stats['total'] = $this->db->fetch("SELECT COUNT(*) as count FROM projects")['count'];
        
        // Projects by status
        $statusStats = $this->db->fetchAll("SELECT status, COUNT(*) as count FROM projects GROUP BY status");
        foreach ($statusStats as $stat) {
            $stats['by_status'][$stat['status']] = $stat['count'];
        }
        
        // Total budget vs actual cost
        $budgetStats = $this->db->fetch("SELECT SUM(budget) as total_budget, SUM(actual_cost) as total_actual 
                                       FROM projects WHERE budget IS NOT NULL");
        $stats['total_budget'] = $budgetStats['total_budget'] ?? 0;
        $stats['total_actual'] = $budgetStats['total_actual'] ?? 0;
        
        // Projects this month
        $stats['this_month'] = $this->db->fetch("SELECT COUNT(*) as count FROM projects 
                                                WHERE MONTH(created_at) = MONTH(CURRENT_DATE) 
                                                AND YEAR(created_at) = YEAR(CURRENT_DATE)")['count'];
        
        return $stats;
    }
    
    public function updateActualCost($projectId) {
        // Calculate actual cost from materials and equipment
        $materialsCost = $this->db->fetch("SELECT SUM(total_price) as cost FROM project_materials WHERE project_id = ?", [$projectId])['cost'] ?? 0;
        $equipmentCost = $this->db->fetch("SELECT SUM(total_cost) as cost FROM project_equipment WHERE project_id = ?", [$projectId])['cost'] ?? 0;
        
        $totalCost = $materialsCost + $equipmentCost;
        
        $this->db->query("UPDATE projects SET actual_cost = ? WHERE id = ?", [$totalCost, $projectId]);
        
        return $totalCost;
    }
}