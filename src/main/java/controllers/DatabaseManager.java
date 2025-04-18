package controllers; // Defines the package where this class is located

import java.sql.*; // Imports SQL classes for database connection and operations
import java.time.LocalDate; // Imports Java's LocalDate class to work with dates
import java.util.ArrayList; // Imports ArrayList to store multiple items
import java.util.List; // Imports List interface for returning lists of bookings

public class DatabaseManager {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/vehicle_rental"; // URL of the database
    private static final String USER = "vehicle_rental"; // Username for database connection
    private static final String PASSWORD = "12345"; // Password for database connection

    private Connection connection; // This will hold the connection to the database

    // Constructor to establish a database connection when an object of DatabaseManager is created
    public DatabaseManager() {
        try {
            // Attempting to connect to the MySQL database using the provided credentials
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // If there is an error in connection, print the error
            e.printStackTrace();
        }
    }

    // Method to add a booking into the database
    public boolean addBooking(String vehicle, LocalDate startDate, LocalDate endDate) {
        // SQL query to check if a booking already exists with the same vehicle and dates
        String query = "SELECT * FROM bookings WHERE vehicle_id = ? AND start_date = ? AND end_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set values for the query parameters
            stmt.setString(1, vehicle);
            stmt.setDate(2, Date.valueOf(startDate)); // Convert LocalDate to SQL Date
            stmt.setDate(3, Date.valueOf(endDate)); // Convert LocalDate to SQL Date
            // Execute the query
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // If a record is found, it means the booking already exists
                return false; // Return false to indicate failure to add booking
            }
        } catch (SQLException e) {
            // If an error occurs, print the error
            e.printStackTrace();
            return false; // Return false in case of error
        }

        // SQL query to insert a new booking if it doesn't already exist
        query = "INSERT INTO bookings (vehicle_id, start_date, end_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set values for the new booking
            stmt.setString(1, vehicle);
            stmt.setDate(2, Date.valueOf(startDate)); // Convert LocalDate to SQL Date
            stmt.setDate(3, Date.valueOf(endDate)); // Convert LocalDate to SQL Date
            // Execute the update to insert the new booking
            stmt.executeUpdate();
            return true; // Return true to indicate the booking was successfully added
        } catch (SQLException e) {
            // If an error occurs, print the error
            e.printStackTrace();
            return false; // Return false in case of error
        }
    }

    // Method to remove a booking from the database
    public void removeBooking(String vehicle, LocalDate startDate, LocalDate endDate) {
        // SQL query to delete a booking from the database
        String query = "DELETE FROM bookings WHERE vehicle_id = ? AND start_date = ? AND end_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set values for the query parameters
            stmt.setString(1, vehicle);
            stmt.setDate(2, Date.valueOf(startDate)); // Convert LocalDate to SQL Date
            stmt.setDate(3, Date.valueOf(endDate)); // Convert LocalDate to SQL Date
            // Execute the update to delete the booking
            stmt.executeUpdate();
        } catch (SQLException e) {
            // If an error occurs, print the error
            e.printStackTrace();
        }
    }

    // Method to get all bookings from the database
    public List<String> getAllBookings() {
        List<String> bookings = new ArrayList<>(); // Create an empty list to store bookings
        // SQL query to select all bookings from the database
        String query = "SELECT * FROM bookings";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            // Loop through all results in the ResultSet
            while (rs.next()) {
                // Get values for each column in the booking record
                String vehicle = rs.getString("vehicle_id");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                // Add formatted booking string to the list
                bookings.add(vehicle + " - " + startDate.toLocalDate() + " to " + endDate.toLocalDate());
            }
        } catch (SQLException e) {
            // If an error occurs, print the error
            e.printStackTrace();
        }
        return bookings; // Return the list of all bookings
    }
}
