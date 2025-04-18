package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class DashboardController {

    @FXML private Label welcomeLabel;            // Label to display the welcome message
    @FXML private StackPane mainContent;         // StackPane to hold main content for the dashboard

    // Buttons for various actions in the dashboard
    @FXML private Button manageVehiclesButton;
    @FXML private Button manageCustomersButton;
    @FXML private Button bookingsButton;
    @FXML private Button paymentsButton;
    @FXML private Button viewReportsButton;
    @FXML private Button dashboardButton;
    @FXML private Button logoutButton;
    @FXML private Button exportReportsButton;

    private String userRole; // Stores the role of the user (e.g., employee or admin)

    @FXML
    public void initialize() {
        // Set the default welcome text on initialization
        welcomeLabel.setText("Welcome");
    }

    // Set the user role and modify UI components accordingly
    public void setUserRole(String role) {
        this.userRole = role.toLowerCase();   // Store the role in lowercase
        welcomeLabel.setText("Welcome, " + capitalize(role));   // Set the welcome message with the role

        // Disable buttons based on the user role (for employees)
        if ("employee".equals(userRole)) {
            manageVehiclesButton.setDisable(true);
            manageCustomersButton.setDisable(true);
            viewReportsButton.setDisable(true);
        }
    }

    // Handler for the dashboard button, displays a welcome message in the main content area
    @FXML
    public void handleDashboard() {
        mainContent.getChildren().setAll(new Label("WELCOME TO OUR VehicleRentalSystem..."));
    }

    // Handler for the vehicle management button, loads the vehicle management UI
    @FXML
    public void handleVehicleManagement() {
        loadUI("/views/vehicleManagement.fxml");
    }

    // Handler for the customer management button, loads the customer management UI
    @FXML
    public void handleCustomerManagement() {
        loadUI("/views/customerManagement.fxml");
    }

    // Handler for the bookings button, loads the booking UI
    @FXML
    public void handleBookings() {
        loadUI("/views/booking.fxml");
    }

    // Handler for the payments button, loads the payment UI
    @FXML
    public void handlePayments() {
        loadUI("/views/payment.fxml");
    }

    // Handler for the reports button, loads the reports UI
    @FXML
    public void handleReports() {
        loadUI("/views/reports.fxml");
    }

    // Handler for the export reports button, exports reports to CSV
    @FXML
    public void handleExportReports() {
        ReportExportController reportExport = new ReportExportController();
        reportExport.exportToCSV(new Stage());   // Calls the report export function
    }

    // Handler for the logout button, logs the user out and redirects to the login screen
    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();  // Load the login screen

            // Change the scene to the login screen
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to logout: " + e.getMessage()); // Show alert if logout fails
        }
    }

    // Utility method to load UI views from an FXML file
    private void loadUI(String fxmlPath) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                throw new IllegalStateException("FXML file not found at path: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Pane pane = loader.load();
            mainContent.getChildren().setAll(pane);  // Set the loaded pane as the main content
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load UI: " + e.getMessage()); // Show alert if UI loading fails
        }
    }

    // Utility method to show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();   // Show the alert and wait for user interaction
    }

    // Utility method to capitalize the first letter of a string (role name)
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;  // Return text if it's null or empty
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();  // Capitalize first letter
    }
}
