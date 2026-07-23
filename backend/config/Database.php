<?php

// =========================================================
// Global safety net
//
// Without this, any unexpected PHP error/warning/exception
// (a bad date, a duplicate key, a missing field, etc.) makes
// PHP print nothing at all. The Android app then tries to
// parse an empty response body as JSON and crashes with
// "End of input at line 1 column 1 path $".
//
// This guarantees every request - success or failure - always
// gets back a valid JSON body.
// =========================================================

if (!defined("LOSTFOUND_ERROR_HANDLERS_REGISTERED")) {
    define("LOSTFOUND_ERROR_HANDLERS_REGISTERED", true);

    // Don't let PHP print raw HTML error output into the response body
    ini_set("display_errors", "0");
    error_reporting(E_ALL);

    function lostfound_send_json_error(string $message): void
    {
        if (!headers_sent()) {
            http_response_code(500);
            header("Content-Type: application/json");
        }

        echo json_encode([
            "success" => false,
            "message" => "Server error: " . $message
        ]);
    }

    // Turn warnings/notices into catchable exceptions instead of
    // letting them silently continue or blank out the response
    set_error_handler(function ($severity, $message, $file, $line) {
        throw new ErrorException($message, 0, $severity, $file, $line);
    });

    // Catch anything that isn't handled by the individual endpoints
    set_exception_handler(function ($e) {
        lostfound_send_json_error($e->getMessage());
    });

    // Catch fatal errors (e.g. TypeErrors) that bypass the handlers above
    register_shutdown_function(function () {
        $error = error_get_last();

        if ($error !== null && in_array($error["type"], [
            E_ERROR, E_PARSE, E_CORE_ERROR, E_COMPILE_ERROR, E_USER_ERROR
        ], true)) {
            lostfound_send_json_error($error["message"]);
        }
    });
}

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
                lostfound_send_json_error("Database connection failed - " . $e->getMessage());
                exit;
            }
        }

        return $this->connection;
    }
}