<?php

header("Content-Type: application/json");

require_once "../config/Database.php";
require_once "../models/Claim.php";

$data = json_decode(file_get_contents("php://input"), true);

if (
    empty($data["found_item_id"]) ||
    empty($data["claimant_user_id"]) ||
    empty($data["lost_location_answer"]) ||
    empty($data["lost_date_answer"])
) {

    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "All fields are required."
    ]);

    exit;
}

$db = new Database();
$conn = $db->connect();

$claim = new Claim($conn);

$success = $claim->create(
    $data["found_item_id"],
    $data["claimant_user_id"],
    $data["lost_location_answer"],
    $data["lost_date_answer"]
);

echo json_encode([
    "success" => $success,
    "message" => $success
        ? "Claim submitted."
        : "Unable to submit claim."
]);