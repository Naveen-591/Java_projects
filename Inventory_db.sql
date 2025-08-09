CREATE DATABASE inventory_db;
USE inventory_db;

CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY,
    name VARCHAR(100),
    contact VARCHAR(50)
);

CREATE TABLE products (
    product_id INT PRIMARY KEY,
    name VARCHAR(100),
    supplier_id INT,
    price DOUBLE,
    stock INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- Insert some sample suppliers
INSERT INTO suppliers VALUES 
(1, 'Supplier One', '1234567890'),
(2, 'Supplier Two', '0987654321');

-- Insert some sample products
INSERT INTO products VALUES
(101, 'Product A', 1, 50.0, 100),
(102, 'Product B', 2, 30.0, 200);
