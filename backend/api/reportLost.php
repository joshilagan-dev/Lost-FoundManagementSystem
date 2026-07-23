<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/LostItem.php";

$data = json_decode(file_get_contents("php://input"), true);

if (
    empty($data["user_id"]) ||
    empty($data["item_name"]) ||
    empty($data["item_description"]) ||
    empty($data["lost_location"]) ||
    empty($data["lost_date"])
) {

    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "Please complete all fields."
    ]);

    exit;
}

$database = new Database();
$conn = $database->connect();

$item = new LostItem($conn);

$result = $item->create(
    $data["item_name"],
    $data["item_description"],
    $data["lost_location"],
    $data["lost_date"],
    (int) $data["user_id"]
);

if ($result) {

    echo json_encode([
        "success" => true,
        "message" => "Lost item reported successfully."
    ]);

} else {

    http_response_code(500);

    echo json_encode([
        "success" => false,
        "message" => "Unable to report lost item."
    ]);
}