<?php

class Auth {
    private static $instance = null;
    private $user = null;
    
    private function __construct() {
        session_start();
        $this->checkSession();
    }
    
    public static function getInstance() {
        if (self::$instance === null) {
            self::$instance = new self();
        }
        return self::$instance;
    }
    
    private function checkSession() {
        if (isset($_SESSION['user_id'])) {
            $db = Database::getInstance();
            $user = $db->fetch("SELECT * FROM users WHERE id = ? AND is_active = 1", [$_SESSION['user_id']]);
            if ($user) {
                $this->user = $user;
            } else {
                $this->logout();
            }
        }
    }
    
    public function login($username, $password) {
        $db = Database::getInstance();
        $user = $db->fetch("SELECT * FROM users WHERE (username = ? OR email = ?) AND is_active = 1", [$username, $username]);
        
        if ($user && password_verify($password, $user['password_hash'])) {
            $_SESSION['user_id'] = $user['id'];
            $_SESSION['username'] = $user['username'];
            $_SESSION['role'] = $user['role'];
            $this->user = $user;
            return true;
        }
        
        return false;
    }
    
    public function logout() {
        session_destroy();
        $this->user = null;
    }
    
    public function isLoggedIn() {
        return $this->user !== null;
    }
    
    public function getUser() {
        return $this->user;
    }
    
    public function getUserId() {
        return $this->user ? $this->user['id'] : null;
    }
    
    public function getUserRole() {
        return $this->user ? $this->user['role'] : null;
    }
    
    public function hasRole($role) {
        return $this->user && $this->user['role'] === $role;
    }
    
    public function hasAnyRole($roles) {
        return $this->user && in_array($this->user['role'], $roles);
    }
    
    public function requireLogin() {
        if (!$this->isLoggedIn()) {
            header('Location: /login.php');
            exit;
        }
    }
    
    public function requireRole($role) {
        $this->requireLogin();
        if (!$this->hasRole($role)) {
            header('HTTP/1.1 403 Forbidden');
            exit('Access denied');
        }
    }
    
    public function requireAnyRole($roles) {
        $this->requireLogin();
        if (!$this->hasAnyRole($roles)) {
            header('HTTP/1.1 403 Forbidden');
            exit('Access denied');
        }
    }
}