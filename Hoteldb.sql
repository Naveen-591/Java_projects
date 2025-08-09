CREATE DATABASE hotel_db;
USE hotel_db;

CREATE TABLE rooms (
    room_number INT PRIMARY KEY,
    room_type VARCHAR(50),
    price DOUBLE,
    is_booked BOOLEAN DEFAULT FALSE
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100),
    contact VARCHAR(50),
    booked_room_number INT,
    FOREIGN KEY (booked_room_number) REFERENCES rooms(room_number)
);

INSERT INTO rooms VALUES 
(101, 'Single', 1000, FALSE),
(102, 'Double', 2000, FALSE),
(103, 'Deluxe', 3000, FALSE),
(104, 'Suite', 5000, FALSE),
(105, 'Single', 1000, FALSE);
