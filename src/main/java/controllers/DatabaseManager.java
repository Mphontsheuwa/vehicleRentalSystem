package controllers;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/vehicle_rental";
    private static final String USER = "vehicle_rental";
    private static final String PASSWORD = "12345";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add Booking to Database
    public boolean addBooking(String vehicle, LocalDate startDate, LocalDate endDate) {
        String query = "SELECT * FROM bookings WHERE vehicle_id = ? AND start_date = ? AND end_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, vehicle);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return false; // Booking already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Insert new booking
        query = "INSERT INTO bookings (vehicle_id, start_date, end_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, vehicle);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Remove Booking from Database
    public void removeBooking(String vehicle, LocalDate startDate, LocalDate endDate) {
        String query = "DELETE FROM bookings WHERE vehicle_id = ? AND start_date = ? AND end_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, vehicle);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all bookings from the database
    public List<String> getAllBookings() {
        List<String> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String vehicle = rs.getString("vehicle_id");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                bookings.add(vehicle + " - " + startDate.toLocalDate() + " to " + endDate.toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
}

