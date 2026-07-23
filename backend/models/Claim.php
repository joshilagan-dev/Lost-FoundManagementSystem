<?php

class Claim
{
    private PDO $conn;

    public function __construct(PDO $database)
    {
        $this->conn = $database;
    }

    /**
     * Student submits a claim
     */
    public function create(
        int $foundItemId,
        int $claimantUserId,
        string $locationAnswer,
        string $dateAnswer
    ): bool {

        $sql = "INSERT INTO claims
                (
                    found_item_id,
                    claimant_user_id,
                    lost_location_answer,
                    lost_date_answer
                )
                VALUES
                (
                    :found_item_id,
                    :claimant_user_id,
                    :lost_location_answer,
                    :lost_date_answer
                )";

        $stmt = $this->conn->prepare($sql);

        return $stmt->execute([
            ":found_item_id" => $foundItemId,
            ":claimant_user_id" => $claimantUserId,
            ":lost_location_answer" => $locationAnswer,
            ":lost_date_answer" => $dateAnswer
        ]);
    }

    /**
     * Retrieve all submitted claims
     */
    public function getAll(): array
    {
        $sql = "
            SELECT
                c.claim_id,
                c.found_item_id,
                c.claimant_user_id,
                c.lost_location_answer,
                c.lost_date_answer,
                c.status,
                c.submitted_at,
                CONCAT(u.first_name, ' ', u.last_name) AS claimant_name,
                f.item_name
            FROM claims c
            INNER JOIN users u
                ON c.claimant_user_id = u.user_id
            INNER JOIN found_items f
                ON c.found_item_id = f.found_item_id
            ORDER BY c.submitted_at DESC
        ";

        $stmt = $this->conn->prepare($sql);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    /**
     * Staff approves or rejects a claim
     */
    public function updateStatus(
        int $claimId,
        string $status
    ): bool {

        $stmt = $this->conn->prepare(
            "UPDATE claims
             SET status = :status
             WHERE claim_id = :claim_id"
        );

        $success = $stmt->execute([
            ":status" => $status,
            ":claim_id" => $claimId
        ]);

        if (!$success) {
            return false;
        }

        // If approved, mark the corresponding found item as claimed
        if ($status === "Approved") {

            $stmt = $this->conn->prepare(
                "UPDATE found_items
                 SET status = 'Claimed'
                 WHERE found_item_id = (
                    SELECT found_item_id
                    FROM claims
                    WHERE claim_id = :claim_id
                 )"
            );

            $stmt->execute([
                ":claim_id" => $claimId
            ]);
        }

        return true;
    }
}