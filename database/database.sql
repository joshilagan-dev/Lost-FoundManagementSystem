SQL script
-- =========================================================
-- Lost & Found Management System Database
-- Author: Team
-- Database: LostFound
-- =========================================================

CREATE DATABASE IF NOT EXISTS LostFound
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE LostFound;

-- =========================================================
-- USERS TABLE
-- =========================================================

DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS found_items;
DROP TABLE IF EXISTS lost_items;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,

    stud_number VARCHAR(20) NOT NULL UNIQUE,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100) NOT NULL,

    password VARCHAR(255) NOT NULL,

    user_type ENUM('Student','Staff') NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- LOST ITEMS
-- =========================================================

CREATE TABLE lost_items (

    lost_item_id INT AUTO_INCREMENT PRIMARY KEY,

    item_name VARCHAR(150) NOT NULL,

    item_description TEXT,

    lost_location VARCHAR(255) NOT NULL,

    lost_date DATE NOT NULL,

    reported_by_user INT NOT NULL,

    reported_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    status ENUM(
        'Open',
        'Matched',
        'ClaimPending',
        'Closed'
    ) DEFAULT 'Open',

    FOREIGN KEY (reported_by_user)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- =========================================================
-- FOUND ITEMS
-- =========================================================

CREATE TABLE found_items (

    found_item_id INT AUTO_INCREMENT PRIMARY KEY,

    item_name VARCHAR(150) NOT NULL,

    item_description TEXT,

    found_location VARCHAR(255) NOT NULL,

    found_date DATE NOT NULL,

    found_by_user INT NOT NULL,

    reported_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    status ENUM(
        'Available',
        'ClaimPending',
        'Claimed'
    ) DEFAULT 'Available',

    FOREIGN KEY (found_by_user)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- =========================================================
-- CLAIMS
-- =========================================================

CREATE TABLE claims (

    claim_id INT AUTO_INCREMENT PRIMARY KEY,

    found_item_id INT NOT NULL,

    claimant_user_id INT NOT NULL,

    lost_location_answer TEXT,

    lost_date_answer DATE,

    status ENUM(
        'Pending',
        'Approved',
        'Rejected'
    ) DEFAULT 'Pending',

    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (found_item_id)
        REFERENCES found_items(found_item_id)
        ON DELETE CASCADE,

    FOREIGN KEY (claimant_user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- =========================================================
-- SAMPLE USERS
-- =========================================================

INSERT INTO users
(stud_number, first_name, last_name, password, user_type)
VALUES

('2023-00001','Joshua','Ilagan','password123','Student'),

('2023-00002','Juan','Dela Cruz','password123','Student'),

('STAFF-001','Maria','Santos','password123','Staff');

-- =========================================================
-- SAMPLE LOST ITEM
-- =========================================================

INSERT INTO lost_items
(item_name,item_description,lost_location,lost_date,reported_by_user)
VALUES

(
'Wallet',
'Black leather wallet',
'Library',
'2026-07-20',
1
);

-- =========================================================
-- SAMPLE FOUND ITEM
-- =========================================================

INSERT INTO found_items
(item_name,item_description,found_location,found_date,found_by_user)
VALUES

(
'Calculator',
'Casio FX-991ES',
'Engineering Building',
'2026-07-21',
3
);

-- =========================================================
-- SAMPLE CLAIM
-- =========================================================

INSERT INTO claims
(found_item_id,claimant_user_id,lost_location_answer,lost_date_answer)
VALUES

(
1,
2,
'Engineering Building',
'2026-07-21'
);
