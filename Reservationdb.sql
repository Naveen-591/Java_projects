CREATE DATABASE reservationdb;
USE reservationdb;

CREATE TABLE passengers (
    pnr INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    phone VARCHAR(15),
    seat_no INT UNIQUE,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
