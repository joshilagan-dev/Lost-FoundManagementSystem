<?php

class Database
{
    // Database credentials
    private $host = "localhost";
    private $dbName = "LostFound";
    private $username = "root";
    private $password = "";

    // PDO connection
    private $connection = null;

    /**
     * Returns a PDO connection
     */
    public function connect()
    {
        if ($this->connection == null) {
            try {
                $this->connection = new PDO(
                    "mysql:host={$this->host};dbname={$this->dbName};charset=utf8mb4",
                    $this->username,
                    $this->password
                );

                // Throw exceptions when an error occurs
                $this->connection->setAttribute(
                    PDO::ATTR_ERRMODE,
                    PDO::ERRMODE_EXCEPTION
                );

                // Return associative arrays by default
                $this->connection->setAttribute(
                    PDO::ATTR_DEFAULT_FETCH_MODE,
                    PDO::FETCH_ASSOC
                );

            } catch (PDOException $e) {
                die("Database Connection Failed: " . $e->getMessage());
            }
        }

        return $this->connection;
    }
}