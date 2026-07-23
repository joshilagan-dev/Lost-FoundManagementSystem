<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/Claim.php";

$database = new Database();
$conn = $database->connect();

$claim = new Claim($conn);

echo json_encode(
    $claim->getAll()
);