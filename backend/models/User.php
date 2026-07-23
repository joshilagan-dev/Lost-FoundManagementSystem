<?php

class User
{
    private PDO $conn;

    public function __construct(PDO $database)
    {
        $this->conn = $database;
    }

    /**
     * Register a new user
     */
    public function register(
        string $studNumber,
        string $firstName,
        string $lastName,
        string $password,
        string $userType
    ): bool {

        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

        $sql = "INSERT INTO users
                (stud_number, first_name, last_name, password, user_type)
                VALUES
                (:stud_number, :first_name, :last_name, :password, :user_type)";

        $stmt = $this->conn->prepare($sql);

        return $stmt->execute([
            ":stud_number" => $studNumber,
            ":first_name" => $firstName,
            ":last_name" => $lastName,
            ":password" => $hashedPassword,
            ":user_type" => $userType
        ]);
    }

    /**
     * Check if a student number already exists
     */
    public function studentExists(string $studNumber): bool
    {
        $stmt = $this->conn->prepare(
            "SELECT user_id
             FROM users
             WHERE stud_number = :stud_number"
        );

        $stmt->execute([
            ":stud_number" => $studNumber
        ]);

        return $stmt->rowCount() > 0;
    }

    /**
     * Login
     */
    public function login(
        string $studNumber,
        string $password
    )
    {
        $stmt = $this->conn->prepare(
            "SELECT *
             FROM users
             WHERE stud_number = :stud_number"
        );

        $stmt->execute([
            ":stud_number" => $studNumber
        ]);

        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$user) {
            return false;
        }

        if (!password_verify($password, $user["password"])) {
            return false;
        }

        unset($user["password"]);

        return $user;
    }
}