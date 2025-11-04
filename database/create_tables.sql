-- Create database
CREATE DATABASE IF NOT EXISTS topsis_db;
USE topsis_db;

-- Table for storing problems/projects
CREATE TABLE IF NOT EXISTS problems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for storing criteria
CREATE TABLE IF NOT EXISTS criteria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT,
    criterion_name VARCHAR(100) NOT NULL,
    weight DOUBLE NOT NULL,
    criterion_type VARCHAR(20) NOT NULL, -- 'BENEFIT' or 'COST'
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

-- Table for storing alternatives
CREATE TABLE IF NOT EXISTS alternatives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT,
    alternative_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

-- Table for storing performance scores
CREATE TABLE IF NOT EXISTS performance_scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT,
    alternative_id INT,
    criterion_id INT,
    score DOUBLE NOT NULL,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    FOREIGN KEY (alternative_id) REFERENCES alternatives(id) ON DELETE CASCADE,
    FOREIGN KEY (criterion_id) REFERENCES criteria(id) ON DELETE CASCADE
);

-- Table for storing TOPSIS results
CREATE TABLE IF NOT EXISTS topsis_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT,
    alternative_id INT,
    separation_positive DOUBLE,
    separation_negative DOUBLE,
    closeness_coefficient DOUBLE,
    ranking INT,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    FOREIGN KEY (alternative_id) REFERENCES alternatives(id) ON DELETE CASCADE
);
