package projects;
import java.sql.*;
import java.util.Scanner;

public class ExamResultSystem 
{
	static final String URL = "jdbc:mysql://localhost:3306/examdb";
    static final String USER = "root";
    static final String PASS = "pass123";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Exam Result Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. Add Subject");
            System.out.println("3. Enter / Update Result");
            System.out.println("4. View Student Result");
            System.out.println("5. Subject Report (Average, Min, Max, Pass/Fail)");
            System.out.println("6. Class Average");
            System.out.println("7. Top N Students (by total marks)");
            System.out.println("8. List Students");
            System.out.println("9. List Subjects");
            System.out.println("10. Exit");
            System.out.print("Enter choice: ");

            int choice = -1;
            try { choice = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { choice = -1; }

            if (choice == 1) addStudent();
            else if (choice == 2) addSubject();
            else if (choice == 3) enterOrUpdateResult();
            else if (choice == 4) viewStudentResult();
            else if (choice == 5) subjectReport();
            else if (choice == 6) classAverage();
            else if (choice == 7) topNStudents();
            else if (choice == 8) listStudents();
            else if (choice == 9) listSubjects();
            else if (choice == 10) { System.out.println("Exiting..."); break; }
            else System.out.println("Invalid choice.");
        }
        sc.close();
    }

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    static void addStudent() {
        System.out.print("Enter roll no: ");
        String roll = sc.nextLine().trim();
        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter phone: ");
        String phone = sc.nextLine().trim();

        String sql = "INSERT INTO students (roll_no, name, phone) VALUES (?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, roll);
            pst.setString(2, name);
            pst.setString(3, phone);
            pst.executeUpdate();
            System.out.println("Student added.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    static void addSubject() {
        System.out.print("Enter subject code: ");
        String code = sc.nextLine().trim();
        System.out.print("Enter subject name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter max marks (default 100): ");
        String mmStr = sc.nextLine().trim();
        int maxMarks = 100;
        try { if (!mmStr.isEmpty()) maxMarks = Integer.parseInt(mmStr); } catch (Exception e) { maxMarks = 100; }

        String sql = "INSERT INTO subjects (code, name, max_marks) VALUES (?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, code);
            pst.setString(2, name);
            pst.setInt(3, maxMarks);
            pst.executeUpdate();
            System.out.println("Subject added.");
        } catch (SQLException e) {
            System.out.println("Error adding subject: " + e.getMessage());
        }
    }

    static void enterOrUpdateResult() {
        listStudents();
        System.out.print("Enter student_id: ");
        int sid = Integer.parseInt(sc.nextLine().trim());

        listSubjects();
        System.out.print("Enter subject_id: ");
        int subid = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Enter marks obtained: ");
        int marks = Integer.parseInt(sc.nextLine().trim());

        String checkSql = "SELECT result_id FROM results WHERE student_id = ? AND subject_id = ?";
        String insertSql = "INSERT INTO results (student_id, subject_id, marks) VALUES (?, ?, ?)";
        String updateSql = "UPDATE results SET marks = ? WHERE result_id = ?";

        try (Connection con = getConnection();
             PreparedStatement pc = con.prepareStatement(checkSql)) {
            pc.setInt(1, sid);
            pc.setInt(2, subid);
            ResultSet rs = pc.executeQuery();
            if (rs.next()) {
                int rid = rs.getInt("result_id");
                try (PreparedStatement pu = con.prepareStatement(updateSql)) {
                    pu.setInt(1, marks);
                    pu.setInt(2, rid);
                    pu.executeUpdate();
                    System.out.println("Result updated for student_id " + sid + ", subject_id " + subid);
                }
            } else {
                try (PreparedStatement pi = con.prepareStatement(insertSql)) {
                    pi.setInt(1, sid);
                    pi.setInt(2, subid);
                    pi.setInt(3, marks);
                    pi.executeUpdate();
                    System.out.println("Result inserted for student_id " + sid + ", subject_id " + subid);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error inserting/updating result: " + e.getMessage());
        }
    }

    static void viewStudentResult() {
        listStudents();
        System.out.print("Enter student_id to view result: ");
        int sid = Integer.parseInt(sc.nextLine().trim());

        String sql = "SELECT s.student_id, s.roll_no, s.name, subj.name AS subject_name, r.marks, subj.max_marks " +
                     "FROM students s " +
                     "LEFT JOIN results r ON s.student_id = r.student_id " +
                     "LEFT JOIN subjects subj ON r.subject_id = subj.subject_id " +
                     "WHERE s.student_id = ?";

        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, sid);
            ResultSet rs = pst.executeQuery();
            boolean any = false;
            int totalMarks = 0, totalMax = 0, count = 0;
            System.out.println("\nResults for Student ID: " + sid);
            while (rs.next()) {
                if (!any) {
                    System.out.println("Roll: " + rs.getString("roll_no") + " | Name: " + rs.getString("name"));
                }
                String subj = rs.getString("subject_name");
                int marks = rs.getInt("marks");
                int mx = rs.getInt("max_marks");
                if (subj == null) {
                    // student exists but no results yet for any subject
                    System.out.println("No results recorded yet for this student.");
                    any = true; // to prevent duplicate "no results" messages
                    break;
                }
                System.out.println("Subject: " + subj + " | Marks: " + marks + " / " + mx);
                totalMarks += marks;
                totalMax += mx;
                count++;
                any = true;
            }
            if (any && count > 0) {
                double percent = (totalMarks * 100.0) / totalMax;
                System.out.printf("Total: %d / %d | Percentage: %.2f%%\n", totalMarks, totalMax, percent);
            } else if (!any) {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student result: " + e.getMessage());
        }
    }

    static void subjectReport() {
        listSubjects();
        System.out.print("Enter subject_id for report: ");
        int subid = Integer.parseInt(sc.nextLine().trim());

        // pass threshold default 40% of max marks
        int maxMarks = 100;
        try (Connection con = getConnection()) {
            try (PreparedStatement pmax = con.prepareStatement("SELECT max_marks, name FROM subjects WHERE subject_id = ?")) {
                pmax.setInt(1, subid);
                ResultSet rmax = pmax.executeQuery();
                if (rmax.next()) {
                    maxMarks = rmax.getInt("max_marks");
                    System.out.println("Subject: " + rmax.getString("name") + " | Max Marks: " + maxMarks);
                } else {
                    System.out.println("Subject not found.");
                    return;
                }
            }

            String sql = "SELECT s.student_id, s.roll_no, s.name, r.marks " +
                         "FROM results r JOIN students s ON r.student_id = s.student_id " +
                         "WHERE r.subject_id = ? ORDER BY r.marks DESC";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, subid);
                ResultSet rs = pst.executeQuery();
                int total = 0, count = 0, pass = 0, fail = 0, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
                System.out.println("\nSubject Results:");
                while (rs.next()) {
                    int marks = rs.getInt("marks");
                    String name = rs.getString("name");
                    String roll = rs.getString("roll_no");
                    System.out.println("Roll: " + roll + " | Name: " + name + " | Marks: " + marks);
                    total += marks;
                    count++;
                    if (marks > max) max = marks;
                    if (marks < min) min = marks;
                    double perc = marks * 100.0 / maxMarks;
                    if (perc >= 40.0) pass++; else fail++;
                }
                if (count == 0) {
                    System.out.println("No results found for this subject.");
                    return;
                }
                double avg = total * 1.0 / count;
                System.out.println("\nCount: " + count + " | Average: " + String.format("%.2f", avg) +
                                   " | Max: " + max + " | Min: " + min +
                                   " | Pass: " + pass + " | Fail: " + fail);
            }
        } catch (SQLException e) {
            System.out.println("Error generating subject report: " + e.getMessage());
        }
    }

    static void classAverage() {
        String sql = "SELECT s.student_id, s.roll_no, s.name, SUM(r.marks) AS total_obtained, SUM(subj.max_marks) AS total_max " +
                     "FROM students s " +
                     "LEFT JOIN results r ON s.student_id = r.student_id " +
                     "LEFT JOIN subjects subj ON r.subject_id = subj.subject_id " +
                     "GROUP BY s.student_id HAVING total_obtained IS NOT NULL";

        try (Connection con = getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int count = 0;
            double sumPercent = 0.0;
            System.out.println("\nClass Totals:");
            while (rs.next()) {
                int tot = rs.getInt("total_obtained");
                int tmx = rs.getInt("total_max");
                double perc = (tot * 100.0) / tmx;
                System.out.println("Roll: " + rs.getString("roll_no") + " | Name: " + rs.getString("name") +
                                   " | Total: " + tot + " / " + tmx + " | %: " + String.format("%.2f", perc));
                sumPercent += perc;
                count++;
            }
            if (count == 0) {
                System.out.println("No results recorded yet for any student.");
                return;
            }
            System.out.println("Class Average Percentage: " + String.format("%.2f", (sumPercent / count)) + "%");
        } catch (SQLException e) {
            System.out.println("Error computing class average: " + e.getMessage());
        }
    }

    static void topNStudents() {
        System.out.print("Enter N (top N students): ");
        int n = Integer.parseInt(sc.nextLine().trim());

        String sql = "SELECT s.roll_no, s.name, SUM(r.marks) AS total_marks " +
                     "FROM students s JOIN results r ON s.student_id = r.student_id " +
                     "GROUP BY s.student_id ORDER BY total_marks DESC LIMIT ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, n);
            ResultSet rs = pst.executeQuery();
            int rank = 1;
            System.out.println("\nTop " + n + " Students:");
            while (rs.next()) {
                System.out.println("#" + rank + " | Roll: " + rs.getString("roll_no") +
                                   " | Name: " + rs.getString("name") +
                                   " | Total: " + rs.getInt("total_marks"));
                rank++;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching top students: " + e.getMessage());
        }
    }

    static void listStudents() {
        String sql = "SELECT student_id, roll_no, name FROM students";
        try (Connection con = getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nStudents:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") + " | Roll: " + rs.getString("roll_no") + " | Name: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing students: " + e.getMessage());
        }
    }

    static void listSubjects() {
        String sql = "SELECT subject_id, code, name FROM subjects";
        try (Connection con = getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nSubjects:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("subject_id") + " | Code: " + rs.getString("code") + " | Name: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing subjects: " + e.getMessage());
        }
    }
}
