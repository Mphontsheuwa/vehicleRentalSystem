package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.*;  // Importing chart components
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import database.DBConnection;  // Importing database connection utility

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsController {

    // These are the charts that will be displayed on the screen
    @FXML private PieChart availableVehiclesChart;  // Pie chart to show vehicle availability
    @FXML private BarChart<String, Number> customerRentalHistoryChart;  // Bar chart for customer rental history
    @FXML private LineChart<String, Number> revenueChart;  // Line chart to show monthly revenue
    @FXML private CategoryAxis categoryAxis;  // Axis for the customer rental chart (x-axis)
    @FXML private NumberAxis numberAxis;  // Axis for the customer rental chart (y-axis)
    @FXML private CategoryAxis revenueCategoryAxis;  // Axis for the revenue chart (x-axis)
    @FXML private NumberAxis revenueNumberAxis;  // Axis for the revenue chart (y-axis)

    // This method is called when the controller is first loaded to initialize everything
    @FXML
    public void initialize() {
        System.out.println("Initializing charts...");  // Print message to confirm initialization
        loadAvailableVehicles();  // Load data for the vehicle availability pie chart
        loadCustomerRentalHistory();  // Load data for the customer rental history bar chart
        loadRevenueReport();  // Load data for the monthly revenue line chart
        printPaymentData();  // A debug method to print payment data to the console for verification
    }

    // This method loads the data for the pie chart showing vehicle availability (e.g., Available, Rented)
    private void loadAvailableVehicles() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();  // List to hold pie chart data
        String query = "SELECT availability_status, COUNT(*) AS count FROM vehicles GROUP BY availability_status";  // SQL query to get vehicle availability data

        // Try to connect to the database and execute the query
        try (Connection conn = DBConnection.getConnection();  // Get connection from DBConnection class
             PreparedStatement ps = conn.prepareStatement(query);  // Prepare the query
             ResultSet rs = ps.executeQuery()) {  // Execute the query and get the result set

            // Loop through the result set to get vehicle status and count
            while (rs.next()) {
                String status = rs.getString("availability_status");  // Get the availability status (e.g., Available, Rented)
                int count = rs.getInt("count");  // Get the count of vehicles for that status
                System.out.println("Vehicle Status: " + status + ", Count: " + count);  // Print status and count for debugging
                data.add(new PieChart.Data(status + " (" + count + ")", count));  // Add data to pie chart
            }

            // Update the pie chart with the data on the JavaFX application thread
            Platform.runLater(() -> {
                availableVehiclesChart.setTitle("Vehicle Availability");  // Set chart title
                availableVehiclesChart.setData(data);  // Set the data for the chart
            });

        } catch (Exception e) {
            System.err.println("Error loading vehicle data:");  // Print error message if something goes wrong
            e.printStackTrace();  // Print the full error details
        }
    }

    // This method loads the data for the bar chart showing customer rental history
    private void loadCustomerRentalHistory() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();  // Create a series for the bar chart
        series.setName("Customer Rentals");  // Set the series name

        // SQL query to get the number of rentals for each customer
        String query = "SELECT c.name, COUNT(b.booking_id) AS total " +
                "FROM customers c LEFT JOIN bookings b ON c.customer_id = b.customer_id " +
                "GROUP BY c.customer_id, c.name";  // SQL query to join customers and bookings

        // Try to connect to the database and execute the query
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            // Loop through the result set to get customer names and total rentals
            while (rs.next()) {
                String customerName = rs.getString("name");  // Get customer name
                int total = rs.getInt("total");  // Get total rentals for the customer
                System.out.println("Customer: " + customerName + ", Rentals: " + total);  // Print customer name and rentals
                series.getData().add(new XYChart.Data<>(customerName, total));  // Add the data to the series
            }

            // Update the bar chart with the new data
            Platform.runLater(() -> {
                customerRentalHistoryChart.setTitle("Customer Rental History");  // Set the chart title
                customerRentalHistoryChart.getData().clear();  // Clear any existing data
                customerRentalHistoryChart.getData().add(series);  // Add the new data to the chart
                categoryAxis.setLabel("Customers");  // Set the label for the x-axis
                numberAxis.setLabel("Number of Rentals");  // Set the label for the y-axis
            });

        } catch (Exception e) {
            System.err.println("Error loading customer rental data:");  // Print error message if something goes wrong
            e.printStackTrace();  // Print the full error details
        }
    }

    // This method loads the data for the line chart showing monthly revenue
    private void loadRevenueReport() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();  // Create a series for the line chart
        series.setName("Monthly Revenue");  // Set the series name

        // SQL query to get the monthly revenue data
        String query = "SELECT DATE_FORMAT(payment_date, '%b %Y') AS month, " +
                "SUM(amount) AS revenue " +
                "FROM payments " +
                "GROUP BY YEAR(payment_date), MONTH(payment_date) " +
                "ORDER BY YEAR(payment_date), MONTH(payment_date)";  // Query to get the total revenue for each month

        // Try to connect to the database and execute the query
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Revenue Data:");  // Print message to indicate revenue data is being loaded
            // Loop through the result set to get revenue for each month
            while (rs.next()) {
                String month = rs.getString("month");  // Get the month
                double revenue = rs.getDouble("revenue");  // Get the total revenue for that month
                System.out.println("Month: " + month + ", Revenue: " + revenue);  // Print month and revenue
                series.getData().add(new XYChart.Data<>(month, revenue));  // Add data to the series
            }

            // Update the line chart with the new data
            Platform.runLater(() -> {
                revenueChart.setTitle("Monthly Revenue Report");  // Set the chart title
                revenueChart.getData().clear();  // Clear any existing data
                revenueChart.getData().add(series);  // Add the new data to the chart
                revenueCategoryAxis.setLabel("Month");  // Set the label for the x-axis
                revenueNumberAxis.setLabel("Amount (R)");  // Set the label for the y-axis
            });

        } catch (Exception e) {
            System.err.println("Error loading revenue data:");  // Print error message if something goes wrong
            e.printStackTrace();  // Print the full error details
        }
    }

    // Debugging method to print payment data to the console
    private void printPaymentData() {
        // SQL query to get payment details
        String sql = "SELECT p.payment_id, p.booking_id, p.amount, p.payment_method, p.payment_date, " +
                "c.name AS customer_name, v.brand_model AS vehicle " +
                "FROM payments p " +
                "JOIN bookings b ON p.booking_id = b.booking_id " +
                "JOIN customers c ON b.customer_id = c.customer_id " +
                "JOIN vehicles v ON b.vehicle_id = v.vehicle_id";  // Join payments, bookings, customers, and vehicles

        // Try to connect to the database and execute the query
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Print header for payment data
            System.out.println("\nPayment Data Details:");
            System.out.println("ID | Booking | Amount | Method | Date | Customer | Vehicle");
            // Loop through the result set to print payment details
            while (rs.next()) {
                System.out.printf("%d | %d | R%.2f | %s | %s | %s | %s%n",
                        rs.getInt("payment_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date"),
                        rs.getString("customer_name"),
                        rs.getString("vehicle"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment details:");  // Print error message if something goes wrong
            e.printStackTrace();  // Print the full error details
        }
    }
}
