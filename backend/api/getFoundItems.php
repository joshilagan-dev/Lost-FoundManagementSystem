<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/FoundItem.php";

$database = new Database();
$conn = $database->connect();

$item = new FoundItem($conn);

echo json_encode(
    $item->getAll()
);