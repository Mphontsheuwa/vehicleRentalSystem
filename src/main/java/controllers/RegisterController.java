package controllers; // Defines the package where this class is located

import javafx.event.ActionEvent; // Imports ActionEvent class for event handling
import javafx.fxml.FXML; // Imports FXML annotation to mark fields for dependency injection
import javafx.scene.Scene; // Imports Scene class to manage the window scene
import javafx.scene.control.Alert; // Imports Alert class to show pop-up alerts
import javafx.scene.control.ComboBox; // Imports ComboBox class to display a drop-down list
import javafx.scene.control.PasswordField; // Imports PasswordField class for password input
import javafx.scene.control.TextField; // Imports TextField class for user input
import javafx.scene.layout.StackPane; // Imports StackPane layout
import javafx.fxml.FXMLLoader; // Imports FXMLLoader class to load FXML files
import javafx.scene.Parent; // Imports Parent class for FXML loading
import javafx.stage.Stage; // Imports Stage class for window handling

import java.sql.Connection; // Imports SQL Connection class
import java.sql.DriverManager; // Imports DriverManager to manage the database connection
import java.sql.PreparedStatement; // Imports PreparedStatement for executing SQL queries
import java.sql.SQLException; // Imports SQLException to handle SQL errors
import java.io.IOException; // Imports IOException to handle input/output errors

public class RegisterController {

    @FXML
    private TextField usernameField; // FXML field for entering the username

    @FXML
    private PasswordField passwordField; // FXML field for entering the password

    @FXML
    private PasswordField confirmPasswordField; // FXML field for confirming the password

    @FXML
    private ComboBox<String> roleComboBox; // FXML field for selecting a role (Admin/Employee)

    // Database connection details
    private final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_rental"; // URL of the MySQL database
    private final String DB_USER = "vehicle_rental"; // Username for the database
    private final String DB_PASSWORD = "12345"; // Password for the database

    // This method will populate the ComboBox with available roles (Admin or Employee)
    @FXML
    public void initialize() {
        // Adding "Admin" and "Employee" as possible roles in the ComboBox
        roleComboBox.setItems(javafx.collections.FXCollections.observableArrayList("Admin", "Employee"));
    }

    // Method to register the user when the register button is clicked
    public void registerUser(ActionEvent event) {
        String username = usernameField.getText(); // Get the username entered by the user
        String password = passwordField.getText(); // Get the password entered by the user
        String confirmPassword = confirmPasswordField.getText(); // Get the confirm password entered by the user
        String role = roleComboBox.getValue(); // Get the selected role (Admin/Employee)

        // Validate the form inputs to ensure no fields are left empty
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            showAlert("Error", "Please fill in all fields."); // Show an alert if any field is empty
            return;
        }

        // Check if the entered passwords match
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match."); // Show an alert if passwords don't match
            return;
        }

        // SQL query to insert a new user into the database
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // Establish database connection
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepare the SQL statement

            stmt.setString(1, username); // Set the username in the SQL query
            stmt.setString(2, password); // Set the password in the SQL query
            stmt.setString(3, role);  // Set the selected role in the SQL query

            // Execute the SQL query and get the number of affected rows
            int rowsAffected = stmt.executeUpdate();

            // If rowsAffected is greater than 0, registration was successful
            if (rowsAffected > 0) {
                showAlert("Success", "Registration successful!"); // Show success alert
                redirectToLoginPage(); // Redirect to the login page after successful registration
            } else {
                showAlert("Failed", "Registration failed. Please try again."); // Show failure alert
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print SQL error stack trace
            showAlert("Database Error", e.getMessage()); // Show an alert with the error message
        } catch (Exception e) {
            e.printStackTrace(); // Print other errors
            showAlert("Error", "Something went wrong."); // Show a general error alert
        }
    }

    // Method to show an alert with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Create an informational alert
        alert.setTitle(title); // Set the alert title
        alert.setHeaderText(null); // Remove header text
        alert.setContentText(message); // Set the content message of the alert
        alert.showAndWait(); // Show the alert and wait for the user to close it
    }

    // Method to redirect the user to the login page after successful registration
    private void redirectToLoginPage() {
        try {
            // Load the login screen (login.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent loginRoot = loader.load(); // Load the login screen FXML

            // Set the scene with the login page
            Stage stage = (Stage) usernameField.getScene().getWindow(); // Get the current window
            stage.setScene(new Scene(loginRoot)); // Set the scene to the login screen
            stage.show(); // Show the login screen
        } catch (IOException e) {
            e.printStackTrace(); // Print error if unable to load login screen
            showAlert("Error", "Failed to load login screen."); // Show error alert
        }
    }
}
