<?php

class Estimate {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    public function getAll($filters = []) {
        $sql = "SELECT e.*, p.name as project_name, c.company_name, u.first_name, u.last_name as created_by_name 
                FROM estimates e 
                JOIN projects p ON e.project_id = p.id 
                LEFT JOIN clients c ON p.client_id = c.id 
                JOIN users u ON e.created_by = u.id 
                WHERE 1=1";
        $params = [];
        
        if (!empty($filters['status'])) {
            $sql .= " AND e.status = ?";
            $params[] = $filters['status'];
        }
        
        if (!empty($filters['project_id'])) {
            $sql .= " AND e.project_id = ?";
            $params[] = $filters['project_id'];
        }
        
        $sql .= " ORDER BY e.created_at DESC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    public function getById($id) {
        return $this->db->fetch("SELECT e.*, p.name as project_name, c.company_name, c.contact_person, 
                                       c.email as client_email, c.phone as client_phone, 
                                       u.first_name, u.last_name as created_by_name 
                                FROM estimates e 
                                JOIN projects p ON e.project_id = p.id 
                                LEFT JOIN clients c ON p.client_id = c.id 
                                JOIN users u ON e.created_by = u.id 
                                WHERE e.id = ?", [$id]);
    }
    
    public function create($data) {
        $this->db->getConnection()->beginTransaction();
        
        try {
            // Generate estimate number
            $estimateNumber = $this->generateEstimateNumber();
            
            $sql = "INSERT INTO estimates (project_id, estimate_number, title, description, total_amount, 
                                         tax_rate, tax_amount, grand_total, status, valid_until, created_by) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            $taxRate = $data['tax_rate'] ?? 20;
            $totalAmount = $data['total_amount'] ?? 0;
            $taxAmount = ($totalAmount * $taxRate) / 100;
            $grandTotal = $totalAmount + $taxAmount;
            
            $params = [
                $data['project_id'],
                $estimateNumber,
                $data['title'],
                $data['description'] ?? null,
                $totalAmount,
                $taxRate,
                $taxAmount,
                $grandTotal,
                $data['status'] ?? 'draft',
                $data['valid_until'] ?? null,
                $data['created_by']
            ];
            
            $this->db->query($sql, $params);
            $estimateId = $this->db->lastInsertId();
            
            // Add estimate items
            if (!empty($data['items'])) {
                foreach ($data['items'] as $item) {
                    $this->addItem($estimateId, $item);
                }
            }
            
            $this->db->getConnection()->commit();
            return $estimateId;
            
        } catch (Exception $e) {
            $this->db->getConnection()->rollBack();
            throw $e;
        }
    }
    
    public function update($id, $data) {
        $this->db->getConnection()->beginTransaction();
        
        try {
            $sql = "UPDATE estimates SET title = ?, description = ?, total_amount = ?, tax_rate = ?, 
                                    tax_amount = ?, grand_total = ?, status = ?, valid_until = ?, 
                                    updated_at = CURRENT_TIMESTAMP 
                    WHERE id = ?";
            
            $taxRate = $data['tax_rate'] ?? 20;
            $totalAmount = $data['total_amount'] ?? 0;
            $taxAmount = ($totalAmount * $taxRate) / 100;
            $grandTotal = $totalAmount + $taxAmount;
            
            $params = [
                $data['title'],
                $data['description'] ?? null,
                $totalAmount,
                $taxRate,
                $taxAmount,
                $grandTotal,
                $data['status'] ?? 'draft',
                $data['valid_until'] ?? null,
                $id
            ];
            
            $this->db->query($sql, $params);
            
            // Update items if provided
            if (!empty($data['items'])) {
                // Delete existing items
                $this->db->query("DELETE FROM estimate_items WHERE estimate_id = ?", [$id]);
                
                // Add new items
                foreach ($data['items'] as $item) {
                    $this->addItem($id, $item);
                }
            }
            
            $this->db->getConnection()->commit();
            return true;
            
        } catch (Exception $e) {
            $this->db->getConnection()->rollBack();
            throw $e;
        }
    }
    
    public function delete($id) {
        return $this->db->query("DELETE FROM estimates WHERE id = ?", [$id]);
    }
    
    public function getItems($estimateId) {
        return $this->db->fetchAll("SELECT * FROM estimate_items WHERE estimate_id = ? ORDER BY id", [$estimateId]);
    }
    
    public function addItem($estimateId, $item) {
        $sql = "INSERT INTO estimate_items (estimate_id, description, quantity, unit, unit_price, total_price, category) 
                VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        $totalPrice = $item['quantity'] * $item['unit_price'];
        
        $params = [
            $estimateId,
            $item['description'],
            $item['quantity'],
            $item['unit'],
            $item['unit_price'],
            $totalPrice,
            $item['category'] ?? null
        ];
        
        return $this->db->query($sql, $params);
    }
    
    public function updateStatus($id, $status) {
        return $this->db->query("UPDATE estimates SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", [$status, $id]);
    }
    
    public function generateEstimateNumber() {
        $year = date('Y');
        $month = date('m');
        
        // Get count of estimates for this month
        $count = $this->db->fetch("SELECT COUNT(*) as count FROM estimates 
                                  WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?", [$year, $month])['count'];
        
        return sprintf('EST-%s-%s-%03d', $year, $month, $count + 1);
    }
    
    public function calculateTotals($estimateId) {
        $items = $this->getItems($estimateId);
        $totalAmount = 0;
        
        foreach ($items as $item) {
            $totalAmount += $item['total_price'];
        }
        
        $estimate = $this->getById($estimateId);
        $taxRate = $estimate['tax_rate'];
        $taxAmount = ($totalAmount * $taxRate) / 100;
        $grandTotal = $totalAmount + $taxAmount;
        
        $this->db->query("UPDATE estimates SET total_amount = ?, tax_amount = ?, grand_total = ? WHERE id = ?", 
                         [$totalAmount, $taxAmount, $grandTotal, $estimateId]);
        
        return [
            'total_amount' => $totalAmount,
            'tax_amount' => $taxAmount,
            'grand_total' => $grandTotal
        ];
    }
    
    public function duplicate($estimateId, $newProjectId = null) {
        $estimate = $this->getById($estimateId);
        $items = $this->getItems($estimateId);
        
        if (!$estimate) {
            throw new Exception('Estimate not found');
        }
        
        $newData = [
            'project_id' => $newProjectId ?? $estimate['project_id'],
            'title' => $estimate['title'] . ' (Копия)',
            'description' => $estimate['description'],
            'total_amount' => $estimate['total_amount'],
            'tax_rate' => $estimate['tax_rate'],
            'status' => 'draft',
            'created_by' => Auth::getInstance()->getUserId(),
            'items' => $items
        ];
        
        return $this->create($newData);
    }
    
    public function getStatistics() {
        $stats = [];
        
        // Total estimates
        $stats['total'] = $this->db->fetch("SELECT COUNT(*) as count FROM estimates")['count'];
        
        // Estimates by status
        $statusStats = $this->db->fetchAll("SELECT status, COUNT(*) as count FROM estimates GROUP BY status");
        foreach ($statusStats as $stat) {
            $stats['by_status'][$stat['status']] = $stat['count'];
        }
        
        // Total value
        $valueStats = $this->db->fetch("SELECT SUM(grand_total) as total_value FROM estimates WHERE status = 'approved'");
        $stats['total_value'] = $valueStats['total_value'] ?? 0;
        
        // This month
        $stats['this_month'] = $this->db->fetch("SELECT COUNT(*) as count FROM estimates 
                                                WHERE MONTH(created_at) = MONTH(CURRENT_DATE) 
                                                AND YEAR(created_at) = YEAR(CURRENT_DATE)")['count'];
        
        return $stats;
    }
}