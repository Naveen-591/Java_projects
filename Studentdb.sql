CREATE DATABASE studentdb;
USE studentdb;

-- Student Table
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    class VARCHAR(20) NOT NULL
);

-- Marks Table
CREATE TABLE marks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    subject VARCHAR(50),
    marks_obtained DOUBLE,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- Attendance Table
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    total_classes INT,
    classes_attended INT,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);
