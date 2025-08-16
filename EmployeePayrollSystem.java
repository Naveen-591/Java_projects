package projects;

import java.sql.*;
import java.util.Scanner;

public class EmployeePayrollSystem {
    static final String URL = "jdbc:mysql://localhost:3306/payroll_db";
    static final String USER = "mysql user name";         
    static final String PASS = "mysl password";  

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            while (true) {
                System.out.println("\n--- Employee Payroll System ---");
                System.out.println("1. View Employees");
                System.out.println("2. View Employee Salary Details");
                System.out.println("3. Add Deduction");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        viewEmployees(con);
                        break;
                    case 2:
                        System.out.print("Enter Employee ID to view salary: ");
                        int empId = sc.nextInt();
                        viewSalaryDetails(con, empId);
                        break;
                    case 3:
                        addDeduction(con, sc);
                        break;
                    case 4:
                        System.out.println("Exiting system. Goodbye!");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice! Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error:");
            e.printStackTrace();
        }
    }

    static void viewEmployees(Connection con) throws SQLException {
        String query = "SELECT * FROM employees";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Employees:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("emp_id") +
                        ", Name: " + rs.getString("name") +
                        ", Department: " + rs.getString("department") +
                        ", Basic Salary: " + rs.getDouble("basic_salary"));
            }
        }
    }

    static void viewSalaryDetails(Connection con, int empId) throws SQLException {
        String empQuery = "SELECT name, basic_salary FROM employees WHERE emp_id = ?";
        String dedQuery = "SELECT SUM(amount) AS total_deductions FROM deductions WHERE emp_id = ?";

        try (PreparedStatement empStmt = con.prepareStatement(empQuery);
             PreparedStatement dedStmt = con.prepareStatement(dedQuery)) {

            empStmt.setInt(1, empId);
            try (ResultSet empRs = empStmt.executeQuery()) {
                if (empRs.next()) {
                    String name = empRs.getString("name");
                    double basicSalary = empRs.getDouble("basic_salary");

                    dedStmt.setInt(1, empId);
                    try (ResultSet dedRs = dedStmt.executeQuery()) {
                        double totalDeductions = 0;
                        if (dedRs.next()) {
                            totalDeductions = dedRs.getDouble("total_deductions");
                        }

                        double netSalary = basicSalary - totalDeductions;

                        System.out.println("Salary Details for " + name + ":");
                        System.out.println("Basic Salary: " + basicSalary);
                        System.out.println("Total Deductions: " + totalDeductions);
                        System.out.println("Net Salary: " + netSalary);
                    }
                } else {
                    System.out.println("Employee ID not found.");
                }
            }
        }
    }

    static void addDeduction(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter Employee ID: ");
        int empId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Deduction Type (e.g., Tax, PF): ");
        String type = sc.nextLine();
        System.out.print("Enter Deduction Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        String insert = "INSERT INTO deductions (emp_id, deduction_type, amount) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insert)) {
            pstmt.setInt(1, empId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Deduction added successfully!");
            } else {
                System.out.println("Failed to add deduction. Check Employee ID.");
            }
        }
    }
}
