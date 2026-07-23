<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/User.php";

// Read JSON sent by Android
$data = json_decode(file_get_contents("php://input"), true);

// Validate required fields
if (
    empty($data["studNumber"]) ||
    empty($data["firstName"]) ||
    empty($data["lastName"]) ||
    empty($data["password"]) ||
    empty($data["userType"])
) {
    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "All fields are required."
    ]);

    exit;
}

$database = new Database();
$conn = $database->connect();

$user = new User($conn);

// Check if student number already exists
if ($user->studentExists($data["studNumber"])) {

    echo json_encode([
        "success" => false,
        "message" => "Student number already exists."
    ]);

    exit;
}

// Register user
$registered = $user->register(
    $data["studNumber"],
    $data["firstName"],
    $data["lastName"],
    $data["password"],
    $data["userType"]
);

if ($registered) {

    echo json_encode([
        "success" => true,
        "message" => "Registration successful."
    ]);

} else {

    http_response_code(500);

    echo json_encode([
        "success" => false,
        "message" => "Registration failed."
    ]);
}