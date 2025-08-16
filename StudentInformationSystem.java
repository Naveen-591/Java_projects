package projects;

import java.sql.*;
import java.util.Scanner;

public class StudentInformationSystem {

    static final String URL = "jdbc:mysql://localhost:3306/studentdb";
    static final String USER = "mysql user name";
    static final String PASS = "mysl password"; 

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== STUDENT INFORMATION SYSTEM =====");
            System.out.println("1. Insert Student");
            System.out.println("2. Update Student");
            System.out.println("3. Delete Student");
            System.out.println("4. Add Marks");
            System.out.println("5. View Marks");
            System.out.println("6. Add Attendance");
            System.out.println("7. View Attendance");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    insertStudent(sc);
                    break;
                case 2:
                    updateStudent(sc);
                    break;
                case 3:
                    deleteStudent(sc);
                    break;
                case 4:
                    addMarks(sc);
                    break;
                case 5:
                    viewMarks(sc);
                    break;
                case 6:
                    addAttendance(sc);
                    break;
                case 7:
                    viewAttendance(sc);
                    break;
                case 8:
                    System.out.println("Goodbye!");
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    // 1. Insert Student
    public static void insertStudent(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Class: ");
            String sclass = sc.nextLine();

            String sql = "INSERT INTO students (student_id, name, class) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, sclass);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Student Added Successfully!" : " Insert Failed.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Update Student
    public static void updateStudent(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Student ID to Update: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter New Name: ");
            String name = sc.nextLine();
            System.out.print("Enter New Class: ");
            String sclass = sc.nextLine();

            String sql = "UPDATE students SET name = ?, class = ? WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, sclass);
            ps.setInt(3, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Student Updated Successfully!" : "Update Failed.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Delete Student
    public static void deleteStudent(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Student ID to Delete: ");
            int id = sc.nextInt();

            String sql = "DELETE FROM students WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Student Deleted Successfully!" : " Delete Failed.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Add Marks
    public static void addMarks(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Subject: ");
            String subject = sc.nextLine();
            System.out.print("Enter Marks Obtained: ");
            int marks = sc.nextInt();

            String sql = "INSERT INTO marks (student_id, subject, marks_obtained) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, subject);
            ps.setInt(3, marks);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? " Marks Added Successfully!" : " Failed to Add Marks.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. View Marks
    public static void viewMarks(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();

            String sql = "SELECT * FROM marks WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            System.out.println("\nMarks for Student ID: " + id);
            while (rs.next()) {
                System.out.println("Subject: " + rs.getString("subject") +
                        ", Marks: " + rs.getInt("marks_obtained"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 6. Add Attendance
    public static void addAttendance(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();
            System.out.print("Enter Total Classes: ");
            int total = sc.nextInt();
            System.out.print("Enter Classes Attended: ");
            int attended = sc.nextInt();

            String sql = "INSERT INTO attendance (student_id, total_classes, classes_attended) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, total);
            ps.setInt(3, attended);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Attendance Added Successfully!" : " Failed to Add Attendance.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 7. View Attendance
    public static void viewAttendance(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();

            String sql = "SELECT * FROM attendance WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            System.out.println("\nAttendance for Student ID: " + id);
            while (rs.next()) {
                System.out.println("Total Classes: " + rs.getInt("total_classes") +
                        ", Classes Attended: " + rs.getInt("classes_attended"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
