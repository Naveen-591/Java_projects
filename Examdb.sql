CREATE DATABASE examdb;
USE examdb;

-- Students table
CREATE TABLE students (
  student_id INT PRIMARY KEY AUTO_INCREMENT,
  roll_no VARCHAR(20) UNIQUE,
  name VARCHAR(100),
  phone VARCHAR(20)
);

-- Subjects table
CREATE TABLE subjects (
  subject_id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) UNIQUE,
  name VARCHAR(100),
  max_marks INT DEFAULT 100
);

-- Results table
CREATE TABLE results (
  result_id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT,
  subject_id INT,
  marks INT,
  FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
  UNIQUE(student_id, subject_id)
);
