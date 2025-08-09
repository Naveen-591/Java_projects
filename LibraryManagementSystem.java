package projects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class LibraryManagementSystem {
    static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    static final String USER = "root";      
    static final String PASS = "pass123";   
    
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== BOOK OPERATIONS =====");
            System.out.println("1. Insert Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> insertBook();
                case 2 -> updateBook();
                case 3 -> deleteBook();
                case 4 -> {
                    System.out.println("Goodbye!");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    // INSERT
    public static void insertBook() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Book ID: ");
            int id = sc.nextInt();
            sc.nextLine(); 

            System.out.print("Enter Book Title: ");
            String title = sc.nextLine();
            System.out.print("Enter Author Name: ");
            String author = sc.nextLine();
            System.out.print("Enter Price: ");
            double price = sc.nextDouble();

            String sql = "INSERT INTO books (id, title, author, price) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setDouble(4, price);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Book Added Successfully!" : "Insert Failed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE
    public static void updateBook() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Book ID to Update: ");
            int id = sc.nextInt();
            sc.nextLine(); // consume newline

            System.out.print("Enter New Title: ");
            String title = sc.nextLine();
            System.out.print("Enter New Author: ");
            String author = sc.nextLine();
            System.out.print("Enter New Price: ");
            double price = sc.nextDouble();

            String sql = "UPDATE books SET title = ?, author = ?, price = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setDouble(3, price);
            ps.setInt(4, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Book Updated Successfully!" : "Update Failed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public static void deleteBook() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Book ID to Delete: ");
            int id = sc.nextInt();

            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Book Deleted Successfully!" : " Delete Failed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

