<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/FoundItem.php";

$data = json_decode(file_get_contents("php://input"), true);

if (
    empty($data["found_item_id"]) ||
    empty($data["status"])
) {
    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "found_item_id and status are required."
    ]);

    exit;
}

$database = new Database();
$conn = $database->connect();

$item = new FoundItem($conn);

$success = $item->updateStatus(
    (int) $data["found_item_id"],
    $data["status"]
);

echo json_encode([
    "success" => $success,
    "message" => $success
        ? "Item status updated successfully."
        : "Unable to update item status."
]);
