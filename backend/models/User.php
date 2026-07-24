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

        $studNumber = trim($studNumber);
        $firstName = trim($firstName);
        $lastName = trim($lastName);

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
        $studNumber = trim($studNumber);

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
     * Login.
     *
     * Returns the user row (without the password hash) on success,
     * or null if the student number doesn't exist or the password
     * doesn't match. Returning null (instead of die()-ing) lets
     * login.php decide how to respond, and keeps every response a
     * valid JSON body.
     */
    public function login(
        string $studNumber,
        string $password
    ): ?array {

        $studNumber = trim($studNumber);

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
            return null;
        }

        if (!password_verify($password, $user["password"])) {
            return null;
        }

        // Don't expose the password hash
        unset($user["password"]);

        return $user;
    }
}
