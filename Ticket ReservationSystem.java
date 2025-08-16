package projects;
import java.sql.*;
import java.util.Scanner;

public class ReservationSystem 
{
    static final String URL = "jdbc:mysql://localhost:3306/reservationdb";
    static final String USER = "mysql user name";
    static final String PASS = "mysl password"; 
    static final int TOTAL_SEATS = 20;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Ticket Reservation System ---");
            System.out.println("1. Book Seat");
            System.out.println("2. Cancel Booking");
            System.out.println("3. View Seats");
            System.out.println("4. View Passenger Records");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int ch = Integer.parseInt(sc.nextLine());

            if (ch == 1) {
                bookSeat(sc);
            } else if (ch == 2) {
                cancelBooking(sc);
            } else if (ch == 3) {
                viewSeats();
            } else if (ch == 4) {
                viewPassengers();
            } else if (ch == 5) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }

    static void bookSeat(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            if (isFull(con)) {
                System.out.println("All seats are booked.");
                return;
            }

            // Show available seats
            boolean[] seatStatus = new boolean[TOTAL_SEATS + 1];
            String sql1 = "SELECT seat_no FROM passengers";
            Statement st = con.createStatement();
            ResultSet rs1 = st.executeQuery(sql1);
            while (rs1.next()) {
                seatStatus[rs1.getInt("seat_no")] = true;
            }
            System.out.println("\nAvailable Seats:");
            for (int i = 1; i <= TOTAL_SEATS; i++) {
                if (!seatStatus[i]) System.out.print(i + " ");
            }
            System.out.println();

            // Passenger details
            System.out.print("Enter passenger name: ");
            String name = sc.nextLine();
            System.out.print("Enter phone number: ");
            String phone = sc.nextLine();

            // Seat selection
            int seatNo;
            while (true) {
                System.out.print("Enter seat number to book: ");
                seatNo = Integer.parseInt(sc.nextLine());

                if (seatNo < 1 || seatNo > TOTAL_SEATS) {
                    System.out.println("Invalid seat number. Please choose between 1 and " + TOTAL_SEATS);
                    continue;
                }
                if (seatStatus[seatNo]) {
                    System.out.println("Seat " + seatNo + " is already booked. Choose another seat.");
                    continue;
                }
                break;
            }

            // Insert booking
            String sql = "INSERT INTO passengers (name, phone, seat_no) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, name);
            pst.setString(2, phone);
            pst.setInt(3, seatNo);
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int pnr = rs.getInt(1);
                System.out.println("Booking successful! PNR: " + pnr + ", Seat: " + seatNo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void cancelBooking(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter PNR to cancel: ");
            int pnr = Integer.parseInt(sc.nextLine());

            String sql = "DELETE FROM passengers WHERE pnr = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, pnr);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("Booking cancelled successfully.");
            } else {
                System.out.println("PNR not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void viewSeats() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            boolean[] seatStatus = new boolean[TOTAL_SEATS + 1];
            String sql = "SELECT seat_no FROM passengers";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                seatStatus[rs.getInt("seat_no")] = true;
            }

            System.out.println("\n--- Seat Layout ---");
            for (int i = 1; i <= TOTAL_SEATS; i++) {
                String status = seatStatus[i] ? "Booked" : "Free";
                System.out.printf("Seat %2d : %s%n", i, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void viewPassengers() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT * FROM passengers";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (!rs.isBeforeFirst()) {
                System.out.println("No passenger records found.");
                return;
            }

            while (rs.next()) {
                System.out.println("PNR: " + rs.getInt("pnr") +
                        " | Name: " + rs.getString("name") +
                        " | Phone: " + rs.getString("phone") +
                        " | Seat: " + rs.getInt("seat_no") +
                        " | Date: " + rs.getTimestamp("booking_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static boolean isFull(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM passengers";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        rs.next();
        return rs.getInt("cnt") >= TOTAL_SEATS;
    }
}
