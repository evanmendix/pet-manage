-- This script initializes the database schema for the Pet Feeder application.
-- It is designed to be consistent with the code-first schema defined using Exposed in the Kotlin backend.

-- Users are managed by Firebase Authentication, so this table stores a reference to the Firebase UID.
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    profile_picture_url VARCHAR(255)
);

-- Pets are created and managed by users.
CREATE TABLE IF NOT EXISTS pets (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    photo_url VARCHAR(255)
);

-- This table creates a many-to-many relationship between users and pets,
-- allowing multiple users to manage a single pet.
CREATE TABLE IF NOT EXISTS pet_managers (
    pet_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (pet_id, user_id),
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- This table stores feeding events for each pet.
CREATE TABLE IF NOT EXISTS feedings (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    pet_id VARCHAR(255) NOT NULL,
    timestamp BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    photo_url VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);
