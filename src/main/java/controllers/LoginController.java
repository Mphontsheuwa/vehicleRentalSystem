package controllers;

// Import necessary JavaFX and SQL classes
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    // Link to username input field in FXML
    @FXML
    private TextField usernameField;

    // Link to password input field in FXML
    @FXML
    private PasswordField passwordField;

    // Database connection parameters
    private final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_rental";
    private final String DB_USER = "vehicle_rental";
    private final String DB_PASSWORD = "12345";

    // Method triggered when login button is pressed
    public void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Show error if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password.");
            return;
        }

        // SQL query to check if credentials exist
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (
                // Open database connection and prepare statement
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            // Set input parameters in SQL query
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Execute query and get result
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // If login is successful, get user role
                String role = rs.getString("role"); // Either "admin" or "employee"

                // Load dashboard view and pass the role
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardController = loader.getController();
                dashboardController.setUserRole(role);

                // Switch to dashboard scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } else {
                // Show error if credentials are invalid
                showAlert("Failed", "Invalid username or password.");
            }

        } catch (SQLException e) {
            // Show database-related errors
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        } catch (Exception e) {
            // Show general errors
            e.printStackTrace();
            showAlert("Error", "Something went wrong.");
        }
    }

    // Method to switch to the registration screen
    public void goToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open registration screen.");
        }
    }

    // Reusable method to display alert popups
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
