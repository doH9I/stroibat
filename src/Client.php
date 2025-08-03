<?php

class Client {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    public function getAll($filters = []) {
        $sql = "SELECT * FROM clients WHERE 1=1";
        $params = [];
        
        if (!empty($filters['status'])) {
            $sql .= " AND status = ?";
            $params[] = $filters['status'];
        }
        
        if (!empty($filters['search'])) {
            $sql .= " AND (company_name LIKE ? OR contact_person LIKE ? OR email LIKE ?)";
            $search = '%' . $filters['search'] . '%';
            $params[] = $search;
            $params[] = $search;
            $params[] = $search;
        }
        
        $sql .= " ORDER BY company_name, contact_person";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    public function getById($id) {
        return $this->db->fetch("SELECT * FROM clients WHERE id = ?", [$id]);
    }
    
    public function create($data) {
        $sql = "INSERT INTO clients (company_name, contact_person, email, phone, address, 
                                   tax_number, bank_details, notes, status) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        $params = [
            $data['company_name'] ?? null,
            $data['contact_person'],
            $data['email'],
            $data['phone'] ?? null,
            $data['address'] ?? null,
            $data['tax_number'] ?? null,
            $data['bank_details'] ?? null,
            $data['notes'] ?? null,
            $data['status'] ?? 'active'
        ];
        
        $this->db->query($sql, $params);
        return $this->db->lastInsertId();
    }
    
    public function update($id, $data) {
        $sql = "UPDATE clients SET company_name = ?, contact_person = ?, email = ?, phone = ?, 
                                    address = ?, tax_number = ?, bank_details = ?, notes = ?, 
                                    status = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?";
        
        $params = [
            $data['company_name'] ?? null,
            $data['contact_person'],
            $data['email'],
            $data['phone'] ?? null,
            $data['address'] ?? null,
            $data['tax_number'] ?? null,
            $data['bank_details'] ?? null,
            $data['notes'] ?? null,
            $data['status'] ?? 'active',
            $id
        ];
        
        return $this->db->query($sql, $params);
    }
    
    public function delete($id) {
        return $this->db->query("DELETE FROM clients WHERE id = ?", [$id]);
    }
    
    public function getProjects($clientId) {
        return $this->db->fetchAll("SELECT * FROM projects WHERE client_id = ? ORDER BY created_at DESC", [$clientId]);
    }
    
    public function getEstimates($clientId) {
        return $this->db->fetchAll("
            SELECT e.*, p.name as project_name 
            FROM estimates e 
            JOIN projects p ON e.project_id = p.id 
            WHERE p.client_id = ? 
            ORDER BY e.created_at DESC
        ", [$clientId]);
    }
    
    public function getInvoices($clientId) {
        return $this->db->fetchAll("
            SELECT i.*, p.name as project_name 
            FROM invoices i 
            JOIN projects p ON i.project_id = p.id 
            WHERE p.client_id = ? 
            ORDER BY i.issue_date DESC
        ", [$clientId]);
    }
    
    public function getStatistics() {
        $stats = [];
        
        // Total clients
        $stats['total'] = $this->db->fetch("SELECT COUNT(*) as count FROM clients")['count'];
        
        // Clients by status
        $statusStats = $this->db->fetchAll("SELECT status, COUNT(*) as count FROM clients GROUP BY status");
        foreach ($statusStats as $stat) {
            $stats['by_status'][$stat['status']] = $stat['count'];
        }
        
        // Active clients with projects
        $stats['active_with_projects'] = $this->db->fetch("
            SELECT COUNT(DISTINCT c.id) as count 
            FROM clients c 
            JOIN projects p ON c.id = p.client_id 
            WHERE c.status = 'active'
        ")['count'];
        
        // Total revenue from clients
        $stats['total_revenue'] = $this->db->fetch("
            SELECT SUM(i.total_amount) as total 
            FROM invoices i 
            JOIN projects p ON i.project_id = p.id 
            JOIN clients c ON p.client_id = c.id 
            WHERE i.status = 'paid'
        ")['total'] ?? 0;
        
        return $stats;
    }
    
    public function search($query) {
        return $this->db->fetchAll("
            SELECT * FROM clients 
            WHERE company_name LIKE ? OR contact_person LIKE ? OR email LIKE ? 
            ORDER BY company_name
            LIMIT 10
        ", ["%$query%", "%$query%", "%$query%"]);
    }
}