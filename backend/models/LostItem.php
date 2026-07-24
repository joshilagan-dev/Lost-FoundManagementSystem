<?php

class LostItem
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

        $sql = "INSERT INTO lost_items
                (
                    item_name,
                    item_description,
                    lost_location,
                    lost_date,
                    reported_by_user
                )
                VALUES
                (
                    :item_name,
                    :item_description,
                    :lost_location,
                    :lost_date,
                    :reported_by_user
                )";

        $stmt = $this->conn->prepare($sql);

        return $stmt->execute([
            ":item_name" => $itemName,
            ":item_description" => $description,
            ":lost_location" => $location,
            ":lost_date" => $date,
            ":reported_by_user" => $userId
        ]);
    }

    public function getAll(): array
    {
        $sql = "
            SELECT
                l.lost_item_id,
                l.item_name,
                l.item_description,
                l.lost_location,
                l.lost_date,
                l.reported_date,
                l.status,
                COALESCE(CONCAT(u.first_name, ' ', u.last_name), 'Unknown') AS reported_by
            FROM lost_items l
            LEFT JOIN users u
                ON l.reported_by_user = u.user_id
            ORDER BY l.reported_date DESC
        ";

        $stmt = $this->conn->prepare($sql);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function delete(int $lostItemId): bool
    {
        $stmt = $this->conn->prepare(
            "DELETE FROM lost_items WHERE lost_item_id = :lost_item_id"
        );

        return $stmt->execute([
            ":lost_item_id" => $lostItemId
        ]);
    }
}
