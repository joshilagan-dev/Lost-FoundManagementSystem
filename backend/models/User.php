<?php

class User
{
    private PDO $conn;

    public function __construct(PDO $database)
    {
        $this->conn = $database;
    }
}