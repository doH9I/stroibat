<?php

class Material {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    public function getAll($filters = []) {
        $sql = "SELECT * FROM materials WHERE 1=1";
        $params = [];
        
        if (!empty($filters['category'])) {
            $sql .= " AND category = ?";
            $params[] = $filters['category'];
        }
        
        if (!empty($filters['supplier'])) {
            $sql .= " AND supplier LIKE ?";
            $params[] = '%' . $filters['supplier'] . '%';
        }
        
        if (!empty($filters['low_stock'])) {
            $sql .= " AND current_stock <= min_stock";
        }
        
        if (!empty($filters['search'])) {
            $sql .= " AND (name LIKE ? OR description LIKE ?)";
            $search = '%' . $filters['search'] . '%';
            $params[] = $search;
            $params[] = $search;
        }
        
        $sql .= " ORDER BY name";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    public function getById($id) {
        return $this->db->fetch("SELECT * FROM materials WHERE id = ?", [$id]);
    }
    
    public function create($data) {
        $sql = "INSERT INTO materials (name, description, unit, price_per_unit, supplier, 
                                     supplier_contact, min_stock, current_stock, category) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        $params = [
            $data['name'],
            $data['description'] ?? null,
            $data['unit'],
            $data['price_per_unit'],
            $data['supplier'] ?? null,
            $data['supplier_contact'] ?? null,
            $data['min_stock'] ?? 0,
            $data['current_stock'] ?? 0,
            $data['category'] ?? null
        ];
        
        $this->db->query($sql, $params);
        return $this->db->lastInsertId();
    }
    
    public function update($id, $data) {
        $sql = "UPDATE materials SET name = ?, description = ?, unit = ?, price_per_unit = ?, 
                                    supplier = ?, supplier_contact = ?, min_stock = ?, 
                                    current_stock = ?, category = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?";
        
        $params = [
            $data['name'],
            $data['description'] ?? null,
            $data['unit'],
            $data['price_per_unit'],
            $data['supplier'] ?? null,
            $data['supplier_contact'] ?? null,
            $data['min_stock'] ?? 0,
            $data['current_stock'] ?? 0,
            $data['category'] ?? null,
            $id
        ];
        
        return $this->db->query($sql, $params);
    }
    
    public function delete($id) {
        return $this->db->query("DELETE FROM materials WHERE id = ?", [$id]);
    }
    
    public function updateStock($id, $quantity, $operation = 'add') {
        $material = $this->getById($id);
        if (!$material) {
            throw new Exception('Material not found');
        }
        
        $newStock = $operation === 'add' 
            ? $material['current_stock'] + $quantity
            : $material['current_stock'] - $quantity;
        
        if ($newStock < 0) {
            throw new Exception('Insufficient stock');
        }
        
        return $this->db->query("UPDATE materials SET current_stock = ? WHERE id = ?", [$newStock, $id]);
    }
    
    public function getLowStock() {
        return $this->db->fetchAll("SELECT * FROM materials WHERE current_stock <= min_stock ORDER BY current_stock ASC");
    }
    
    public function getCategories() {
        return $this->db->fetchAll("SELECT DISTINCT category FROM materials WHERE category IS NOT NULL ORDER BY category");
    }
    
    public function getSuppliers() {
        return $this->db->fetchAll("SELECT DISTINCT supplier FROM materials WHERE supplier IS NOT NULL ORDER BY supplier");
    }
    
    public function getProjectUsage($materialId) {
        return $this->db->fetchAll("
            SELECT pm.*, p.name as project_name, p.start_date, p.end_date 
            FROM project_materials pm 
            JOIN projects p ON pm.project_id = p.id 
            WHERE pm.material_id = ? 
            ORDER BY pm.date_used DESC
        ", [$materialId]);
    }
    
    public function getStatistics() {
        $stats = [];
        
        // Total materials
        $stats['total'] = $this->db->fetch("SELECT COUNT(*) as count FROM materials")['count'];
        
        // Total value
        $stats['total_value'] = $this->db->fetch("
            SELECT SUM(current_stock * price_per_unit) as total 
            FROM materials
        ")['total'] ?? 0;
        
        // Low stock items
        $stats['low_stock'] = $this->db->fetch("
            SELECT COUNT(*) as count 
            FROM materials 
            WHERE current_stock <= min_stock
        ")['count'];
        
        // Materials by category
        $stats['by_category'] = $this->db->fetchAll("
            SELECT category, COUNT(*) as count, SUM(current_stock * price_per_unit) as value 
            FROM materials 
            WHERE category IS NOT NULL 
            GROUP BY category 
            ORDER BY value DESC
        ");
        
        // Top suppliers
        $stats['top_suppliers'] = $this->db->fetchAll("
            SELECT supplier, COUNT(*) as materials_count, SUM(current_stock * price_per_unit) as total_value 
            FROM materials 
            WHERE supplier IS NOT NULL 
            GROUP BY supplier 
            ORDER BY total_value DESC 
            LIMIT 10
        ");
        
        return $stats;
    }
    
    public function search($query) {
        return $this->db->fetchAll("
            SELECT * FROM materials 
            WHERE name LIKE ? OR description LIKE ? OR supplier LIKE ? 
            ORDER BY name
            LIMIT 10
        ", ["%$query%", "%$query%", "%$query%"]);
    }
    
    public function importFromCSV($file) {
        if (!file_exists($file)) {
            throw new Exception('File not found');
        }
        
        $handle = fopen($file, 'r');
        if (!$handle) {
            throw new Exception('Cannot open file');
        }
        
        $this->db->getConnection()->beginTransaction();
        
        try {
            $row = 1;
            while (($data = fgetcsv($handle)) !== false) {
                if ($row === 1) {
                    $row++;
                    continue; // Skip header
                }
                
                if (count($data) < 4) {
                    continue; // Skip invalid rows
                }
                
                $materialData = [
                    'name' => $data[0],
                    'description' => $data[1] ?? null,
                    'unit' => $data[2],
                    'price_per_unit' => (float)($data[3] ?? 0),
                    'supplier' => $data[4] ?? null,
                    'supplier_contact' => $data[5] ?? null,
                    'min_stock' => (int)($data[6] ?? 0),
                    'current_stock' => (int)($data[7] ?? 0),
                    'category' => $data[8] ?? null
                ];
                
                $this->create($materialData);
                $row++;
            }
            
            fclose($handle);
            $this->db->getConnection()->commit();
            
            return $row - 2; // Return number of imported items
            
        } catch (Exception $e) {
            $this->db->getConnection()->rollBack();
            fclose($handle);
            throw $e;
        }
    }
    
    public function exportToCSV() {
        $materials = $this->getAll();
        
        $filename = 'materials_export_' . date('Y-m-d_H-i-s') . '.csv';
        $filepath = 'uploads/exports/' . $filename;
        
        if (!is_dir('uploads/exports/')) {
            mkdir('uploads/exports/', 0755, true);
        }
        
        $handle = fopen($filepath, 'w');
        
        // Write header
        fputcsv($handle, [
            'Name', 'Description', 'Unit', 'Price per Unit', 'Supplier', 
            'Supplier Contact', 'Min Stock', 'Current Stock', 'Category'
        ]);
        
        // Write data
        foreach ($materials as $material) {
            fputcsv($handle, [
                $material['name'],
                $material['description'],
                $material['unit'],
                $material['price_per_unit'],
                $material['supplier'],
                $material['supplier_contact'],
                $material['min_stock'],
                $material['current_stock'],
                $material['category']
            ]);
        }
        
        fclose($handle);
        return $filepath;
    }
}