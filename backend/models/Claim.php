<?php

class Claim
{
    private PDO $conn;

    public function __construct(PDO $database)
    {
        $this->conn = $database;
    }

    /**
     * Student submits a claim ("verifies ownership").
     *
     * Inserts the claim (status defaults to 'Pending' in the DB) and
     * moves the found item from 'Available' to 'ClaimPending' so staff
     * can see, at a glance, which items have a claim waiting on them.
     */
    public function create(
        int $foundItemId,
        int $claimantUserId,
        string $locationAnswer,
        string $dateAnswer
    ): bool {

        $this->conn->beginTransaction();

        try {

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

            $stmt->execute([
                ":found_item_id" => $foundItemId,
                ":claimant_user_id" => $claimantUserId,
                ":lost_location_answer" => $locationAnswer,
                ":lost_date_answer" => $dateAnswer
            ]);

            // Send it to staff: mark the item as awaiting a decision.
            $stmt = $this->conn->prepare(
                "UPDATE found_items
                 SET status = 'ClaimPending'
                 WHERE found_item_id = :found_item_id
                 AND status = 'Available'"
            );

            $stmt->execute([
                ":found_item_id" => $foundItemId
            ]);

            $this->conn->commit();

            return true;

        } catch (Exception $e) {

            $this->conn->rollBack();

            return false;
        }
    }

    /**
     * Retrieve all submitted claims (for the staff review screen)
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
     * Staff approves or rejects a claim.
     *
     * Approved -> the found item becomes 'Claimed'.
     * Rejected -> the found item goes back to 'Available' so it can
     *             be claimed by someone else (otherwise it would be
     *             stuck in 'ClaimPending' forever).
     */
    public function updateStatus(
        int $claimId,
        string $status
    ): bool {

        if (!in_array($status, ["Approved", "Rejected"], true)) {
            return false;
        }

        $this->conn->beginTransaction();

        try {

            $stmt = $this->conn->prepare(
                "UPDATE claims
                 SET status = :status
                 WHERE claim_id = :claim_id"
            );

            $stmt->execute([
                ":status" => $status,
                ":claim_id" => $claimId
            ]);

            $newItemStatus = $status === "Approved" ? "Claimed" : "Available";

            $stmt = $this->conn->prepare(
                "UPDATE found_items
                 SET status = :new_status
                 WHERE found_item_id = (
                    SELECT found_item_id
                    FROM claims
                    WHERE claim_id = :claim_id
                 )"
            );

            $stmt->execute([
                ":new_status" => $newItemStatus,
                ":claim_id" => $claimId
            ]);

            $this->conn->commit();

            return true;

        } catch (Exception $e) {

            $this->conn->rollBack();

            return false;
        }
    }
}
