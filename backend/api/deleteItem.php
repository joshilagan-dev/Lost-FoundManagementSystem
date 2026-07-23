<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/LostItem.php";
require_once "../models/FoundItem.php";

$data = json_decode(file_get_contents("php://input"), true);

if (
    empty($data["item_type"]) ||
    empty($data["item_id"]) ||
    !in_array($data["item_type"], ["lost", "found"], true)
) {

    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "item_type ('lost' or 'found') and item_id are required."
    ]);

    exit;
}

$database = new Database();
$conn = $database->connect();

$itemId = (int) $data["item_id"];

if ($data["item_type"] === "lost") {
    $item = new LostItem($conn);
} else {
    $item = new FoundItem($conn);
}

$result = $item->delete($itemId);

if ($result) {

    echo json_encode([
        "success" => true,
        "message" => "Item deleted successfully."
    ]);

} else {

    http_response_code(500);

    echo json_encode([
        "success" => false,
        "message" => "Unable to delete item."
    ]);
}