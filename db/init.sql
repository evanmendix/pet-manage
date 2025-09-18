-- This script will be executed automatically when the container is first created.

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- You can add more tables here in the future.
-- Create a table for pets
CREATE TABLE IF NOT EXISTS pets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert some sample data into the pets table
INSERT INTO pets (name, type) VALUES ('Buddy', 'Dog');
INSERT INTO pets (name, type) VALUES ('Lucy', 'Cat');
INSERT INTO pets (name, type) VALUES ('Rocky', 'Parrot');
