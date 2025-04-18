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
import java.sql.SQLException;

public class ReportsController {

    @FXML private PieChart availableVehiclesChart;
    @FXML private BarChart<String, Number> customerRentalHistoryChart;
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis categoryAxis;
    @FXML private NumberAxis numberAxis;
    @FXML private CategoryAxis revenueCategoryAxis;
    @FXML private NumberAxis revenueNumberAxis;

    @FXML
    public void initialize() {
        System.out.println("Initializing charts...");
        loadAvailableVehicles();
        loadCustomerRentalHistory();
        loadRevenueReport();
        printPaymentData(); // Debugging method
    }

    private void loadAvailableVehicles() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        String query = "SELECT availability_status, COUNT(*) AS count FROM vehicles GROUP BY availability_status";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("availability_status");
                int count = rs.getInt("count");
                System.out.println("Vehicle Status: " + status + ", Count: " + count);
                data.add(new PieChart.Data(status + " (" + count + ")", count));
            }

            Platform.runLater(() -> {
                availableVehiclesChart.setTitle("Vehicle Availability");
                availableVehiclesChart.setData(data);
            });

        } catch (Exception e) {
            System.err.println("Error loading vehicle data:");
            e.printStackTrace();
        }
    }

    private void loadCustomerRentalHistory() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Customer Rentals");

        String query = "SELECT c.name, COUNT(b.booking_id) AS total " +
                "FROM customers c LEFT JOIN bookings b ON c.customer_id = b.customer_id " +
                "GROUP BY c.customer_id, c.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String customerName = rs.getString("name");
                int total = rs.getInt("total");
                System.out.println("Customer: " + customerName + ", Rentals: " + total);
                series.getData().add(new XYChart.Data<>(customerName, total));
            }

            Platform.runLater(() -> {
                customerRentalHistoryChart.setTitle("Customer Rental History");
                customerRentalHistoryChart.getData().clear();
                customerRentalHistoryChart.getData().add(series);
                categoryAxis.setLabel("Customers");
                numberAxis.setLabel("Number of Rentals");
            });

        } catch (Exception e) {
            System.err.println("Error loading customer rental data:");
            e.printStackTrace();
        }
    }

    private void loadRevenueReport() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        String query = "SELECT DATE_FORMAT(payment_date, '%b %Y') AS month, " +
                "SUM(amount) AS revenue " +
                "FROM payments " +
                "GROUP BY YEAR(payment_date), MONTH(payment_date) " +
                "ORDER BY YEAR(payment_date), MONTH(payment_date)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Revenue Data:");
            while (rs.next()) {
                String month = rs.getString("month");
                double revenue = rs.getDouble("revenue");
                System.out.println("Month: " + month + ", Revenue: " + revenue);
                series.getData().add(new XYChart.Data<>(month, revenue));
            }

            Platform.runLater(() -> {
                revenueChart.setTitle("Monthly Revenue Report");
                revenueChart.getData().clear();
                revenueChart.getData().add(series);
                revenueCategoryAxis.setLabel("Month");
                revenueNumberAxis.setLabel("Amount (R)");
            });

        } catch (Exception e) {
            System.err.println("Error loading revenue data:");
            e.printStackTrace();
        }
    }

    // Debugging method to verify payment data
    private void printPaymentData() {
        String sql = "SELECT p.payment_id, p.booking_id, p.amount, p.payment_method, p.payment_date, " +
                "c.name AS customer_name, v.brand_model AS vehicle " +
                "FROM payments p " +
                "JOIN bookings b ON p.booking_id = b.booking_id " +
                "JOIN customers c ON b.customer_id = c.customer_id " +
                "JOIN vehicles v ON b.vehicle_id = v.vehicle_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nPayment Data Details:");
            System.out.println("ID | Booking | Amount | Method | Date | Customer | Vehicle");
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
            System.err.println("Error retrieving payment details:");
            e.printStackTrace();
        }
    }
}