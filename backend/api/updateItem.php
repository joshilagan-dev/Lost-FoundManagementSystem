<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/Claim.php";

$data = json_decode(file_get_contents("php://input"), true);

if (
    empty($data["claim_id"]) ||
    empty($data["status"])
) {
    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "Missing parameters."
    ]);

    exit;
}

$database = new Database();
$conn = $database->connect();

$claim = new Claim($conn);

$success = $claim->updateStatus(
    $data["claim_id"],
    $data["status"]
);

echo json_encode([
    "success" => $success,
    "message" => $success
        ? "Claim updated successfully."
        : "Unable to update claim."
]);