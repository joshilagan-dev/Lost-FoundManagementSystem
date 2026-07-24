<?php

class FoundItem
{
    private PDO $conn;

    public function __construct(PDO $database)
    {
        $this->conn = $database;
    }

    public function create(
        string $itemName,
        string $description,
        string $location,
        string $date,
        int $userId
    ): bool {

        $sql = "INSERT INTO found_items
                (
                    item_name,
                    item_description,
                    found_location,
                    found_date,
                    found_by_user
                )
                VALUES
                (
                    :item_name,
                    :item_description,
                    :found_location,
                    :found_date,
                    :found_by_user
                )";

        $stmt = $this->conn->prepare($sql);

        return $stmt->execute([
            ":item_name" => $itemName,
            ":item_description" => $description,
            ":found_location" => $location,
            ":found_date" => $date,
            ":found_by_user" => $userId
        ]);
    }

    public function getAll(): array
    {
        $sql = "
            SELECT
                f.found_item_id,
                f.item_name,
                f.item_description,
                f.found_location,
                f.found_date,
                f.reported_date,
                f.status,
                COALESCE(CONCAT(u.first_name, ' ', u.last_name), 'Unknown') AS found_by
            FROM found_items f
            LEFT JOIN users u
                ON f.found_by_user = u.user_id
            ORDER BY f.reported_date DESC
        ";

        $stmt = $this->conn->prepare($sql);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    /**
     * Let staff set a found item's status directly
     * (e.g. mark it 'Claimed' even without going through
     * the claim-approval flow).
     */
    public function updateStatus(int $foundItemId, string $status): bool
    {
        if (!in_array($status, ["Available", "ClaimPending", "Claimed"], true)) {
            return false;
        }

        $stmt = $this->conn->prepare(
            "UPDATE found_items
             SET status = :status
             WHERE found_item_id = :found_item_id"
        );

        return $stmt->execute([
            ":status" => $status,
            ":found_item_id" => $foundItemId
        ]);
    }

    public function delete(int $foundItemId): bool
    {
        $stmt = $this->conn->prepare(
            "DELETE FROM found_items WHERE found_item_id = :found_item_id"
        );

        return $stmt->execute([
            ":found_item_id" => $foundItemId
        ]);
    }
}
