<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/LostItem.php";

$database = new Database();
$conn = $database->connect();

$item = new LostItem($conn);

echo json_encode(
    $item->getAll()
);