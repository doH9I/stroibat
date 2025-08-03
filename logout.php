<?php
require_once 'src/Auth.php';

$auth = Auth::getInstance();
$auth->logout();

// Redirect to login page
header('Location: login.php');
exit;
?>