package projects;

import java.sql.*;
import java.util.Scanner;

public class HotelBooking {
    static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    static final String USER = "root";      
    static final String PASS = "pass123"; 

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            while (true) {
                System.out.println("\n--- Hotel Booking System ---");
                System.out.println("1. Check Room Availability");
                System.out.println("2. Book Room");
                System.out.println("3. View Customer Records");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        checkAvailability(con);
                        break;
                    case 2:
                        System.out.print("Enter Customer ID: ");
                        int customerId = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter Contact: ");
                        String contact = sc.nextLine();
                        System.out.print("Enter Room Number: ");
                        int roomNo = sc.nextInt();
                        bookRoom(con, customerId, name, contact, roomNo);
                        break;
                    case 3:
                        viewCustomers(con);
                        break;
                    case 4:
                        System.out.println("Thank you!");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void checkAvailability(Connection con) throws SQLException {
        String query = "SELECT room_number, room_type, price FROM rooms WHERE is_booked = FALSE";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Available Rooms:");
            while (rs.next()) {
                System.out.println("Room No: " + rs.getInt("room_number") + ", Type: " + rs.getString("room_type") + ", Price: " + rs.getDouble("price"));
            }
        }
    }

    static void bookRoom(Connection con, int customerId, String name, String contact, int roomNo) throws SQLException {
        // Check if room exists and is not booked
        String checkRoom = "SELECT is_booked FROM rooms WHERE room_number = ?";
        try (PreparedStatement pstmt = con.prepareStatement(checkRoom)) {
            pstmt.setInt(1, roomNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boolean isBooked = rs.getBoolean("is_booked");
                    if (isBooked) {
                        System.out.println("Room already booked!");
                        return;
                    }
                } else {
                    System.out.println("Room not found!");
                    return;
                }
            }
        }

        // Insert customer booking
        String insertCustomer = "INSERT INTO customers (customer_id, name, contact, booked_room_number) VALUES (?, ?, ?, ?)";
        String updateRoom = "UPDATE rooms SET is_booked = TRUE WHERE room_number = ?";

        try (
            PreparedStatement insertStmt = con.prepareStatement(insertCustomer);
            PreparedStatement updateStmt = con.prepareStatement(updateRoom)
        ) {
            insertStmt.setInt(1, customerId);
            insertStmt.setString(2, name);
            insertStmt.setString(3, contact);
            insertStmt.setInt(4, roomNo);
            insertStmt.executeUpdate();

            updateStmt.setInt(1, roomNo);
            updateStmt.executeUpdate();

            System.out.println("Room " + roomNo + " booked successfully!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Customer ID already exists. Use a unique ID.");
        }
    }

    static void viewCustomers(Connection con) throws SQLException {
        String query = "SELECT * FROM customers";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Customer Records:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("customer_id") + ", Name: " + rs.getString("name") +
                        ", Contact: " + rs.getString("contact") + ", Room No: " + rs.getInt("booked_room_number"));
            }
        }
    }
}
