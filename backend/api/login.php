<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/User.php";

// Read JSON
$data = json_decode(file_get_contents("php://input"), true);

// Validate input
if (
    empty($data["studNumber"]) ||
    empty($data["password"])
) {
    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "Student number and password are required."
    ]);

    exit;
}

$database = new Database();
$conn = $database->connect();

$user = new User($conn);

$result = $user->login(
    $data["studNumber"],
    $data["password"]
);

if ($result) {
    echo json_encode([
        "success" => true,
        "message" => "Login successful.",
        "user" => $result
    ]);

} else {

    http_response_code(401);

    echo json_encode([
        "success" => false,
        "message" => "Invalid student number or password."
    ]);
}