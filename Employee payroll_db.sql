CREATE DATABASE payroll_db;
USE payroll_db;

CREATE TABLE employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(50),
    basic_salary DOUBLE
);

CREATE TABLE deductions (
    deduction_id INT PRIMARY KEY AUTO_INCREMENT,
    emp_id INT,
    deduction_type VARCHAR(50),
    amount DOUBLE,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
);

-- Sample employee data
INSERT INTO employees VALUES 
(1, 'Alice', 'HR', 50000),
(2, 'Bob', 'IT', 60000),
(3, 'Charlie', 'Finance', 55000);

-- Sample deductions
INSERT INTO deductions (emp_id, deduction_type, amount) VALUES
(1, 'Tax', 5000),
(1, 'PF', 2000),
(2, 'Tax', 6000),
(3, 'Tax', 5500),
(3, 'PF', 2500);
