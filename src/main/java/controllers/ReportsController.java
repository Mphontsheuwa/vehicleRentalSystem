package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportsController {

    @FXML
    private PieChart availableVehiclesChart; // Pie chart to display vehicle availability (available and not available)

    @FXML
    private BarChart<String, Number> customerRentalHistoryChart; // Bar chart to display customer rental history

    @FXML
    private LineChart<String, Number> revenueChart; // Line chart to display revenue over time

    @FXML
    private CategoryAxis categoryAxis; // Category axis for the bar chart (used for customer rentals)

    @FXML
    private NumberAxis numberAxis; // Number axis for the bar chart (used for customer rentals)

    @FXML
    private CategoryAxis revenueCategoryAxis; // Category axis for the line chart (used for monthly revenue)

    @FXML
    private NumberAxis revenueNumberAxis; // Number axis for the line chart (used for monthly revenue)

    @FXML
    public void initialize() {
        System.out.println("Initializing charts..."); // Debugging line to indicate that charts are being initialized
        loadAvailableVehicles(); // Load available vehicle data into Pie chart
        loadCustomerRentalHistory(); // Load customer rental history data into Bar chart
        loadRevenueReport(); // Load revenue data into Line chart
    }

    // ✅ Now includes both Available and Not Available
    private void loadAvailableVehicles() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(); // List to hold Pie chart data
        String query = "SELECT availability_status, COUNT(*) AS count FROM vehicles GROUP BY availability_status"; // SQL query to get vehicle availability count

        try (Connection conn = DBConnection.getConnection(); // Get database connection
             PreparedStatement ps = conn.prepareStatement(query); // Prepare the SQL query
             ResultSet rs = ps.executeQuery()) { // Execute the query and get the result set

            while (rs.next()) { // Loop through result set
                String status = rs.getString("availability_status"); // Get availability status (Available or Not Available)
                int count = rs.getInt("count"); // Get the count of vehicles for the status
                System.out.println("Status: " + status + ", Count: " + count); // Debugging line
                data.add(new PieChart.Data(status + " (" + count + ")", count)); // Add the data to the Pie chart
            }

            // Update Pie chart with data on the JavaFX Application Thread
            Platform.runLater(() -> {
                availableVehiclesChart.setTitle("Vehicle Availability");
                availableVehiclesChart.setData(data); // Set the data for the Pie chart
            });

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any errors
        }
    }

    private void loadCustomerRentalHistory() {
        XYChart.Series<String, Number> series = new XYChart.Series<>(); // Create a new series for the Bar chart
        series.setName("Customer Rentals"); // Set the name of the series

        String query = "SELECT CONCAT(customer_id, ' - ', COUNT(*)) AS label, COUNT(*) AS total FROM bookings GROUP BY customer_id"; // SQL query to get customer rental history

        try (Connection conn = DBConnection.getConnection(); // Get database connection
             PreparedStatement ps = conn.prepareStatement(query); // Prepare the SQL query
             ResultSet rs = ps.executeQuery()) { // Execute the query and get the result set

            while (rs.next()) { // Loop through result set
                String label = rs.getString("label"); // Get the customer ID and rental count as label
                int total = rs.getInt("total"); // Get the total rental count for the customer
                System.out.println("Label: " + label + ", Total: " + total); // Debugging line
                series.getData().add(new XYChart.Data<>(label, total)); // Add the data to the Bar chart series
            }

            // Update Bar chart with data on the JavaFX Application Thread
            Platform.runLater(() -> {
                customerRentalHistoryChart.setTitle("Customer Rental History");
                customerRentalHistoryChart.getData().clear(); // Clear existing data
                customerRentalHistoryChart.getData().add(series); // Add the new data
            });

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any errors
        }
    }

    private void loadRevenueReport() {
        XYChart.Series<String, Number> series = new XYChart.Series<>(); // Create a new series for the Line chart
        series.setName("Monthly Revenue"); // Set the name of the series

        String query = "SELECT DATE_FORMAT(rental_start_date, '%b %Y') AS month, SUM(total_amount) AS revenue " +
                "FROM bookings GROUP BY YEAR(rental_start_date), MONTH(rental_start_date) ORDER BY YEAR(rental_start_date), MONTH(rental_start_date)"; // SQL query to get monthly revenue

        try (Connection conn = DBConnection.getConnection(); // Get database connection
             PreparedStatement ps = conn.prepareStatement(query); // Prepare the SQL query
             ResultSet rs = ps.executeQuery()) { // Execute the query and get the result set

            while (rs.next()) { // Loop through result set
                String month = rs.getString("month"); // Get the month
                double revenue = rs.getDouble("revenue"); // Get the total revenue for the month
                System.out.println("Month: " + month + ", Revenue: " + revenue); // Debugging line
                series.getData().add(new XYChart.Data<>(month, revenue)); // Add the data to the Line chart series
            }

            // Update Line chart with data on the JavaFX Application Thread
            Platform.runLater(() -> {
                revenueChart.setTitle("Revenue Report");
                revenueChart.getData().clear(); // Clear existing data
                revenueChart.getData().add(series); // Add the new data
            });

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any errors
        }
    }
}
