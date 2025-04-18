package controllers;

// These imports are for JavaFX UI components and database connection
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

// This is the controller class for the Vehicle Management GUI
public class VehicleManagementController {

    // Linking UI elements from FXML to Java code using @FXML
    @FXML private TableView<vehicle> vehicleTable;
    @FXML private TableColumn<vehicle, String> vehicleIdColumn;
    @FXML private TableColumn<vehicle, String> brandModelColumn;
    @FXML private TableColumn<vehicle, String> categoryColumn;
    @FXML private TableColumn<vehicle, String> rentalPriceColumn;
    @FXML private TableColumn<vehicle, String> availabilityColumn;

    @FXML private TextField vehicleIdField;
    @FXML private TextField brandModelField;
    @FXML private ComboBox<String> categoryField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> availabilityField;

    // List to hold the vehicle data for the TableView
    private final ObservableList<vehicle> vehicleList = FXCollections.observableArrayList();

    // Database connection details
    private final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_rental";
    private final String DB_USER = "vehicle_rental";
    private final String DB_PASSWORD = "12345";

    @FXML
    private void initialize() {
        // Link table columns with vehicle class properties
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        brandModelColumn.setCellValueFactory(new PropertyValueFactory<>("brandModel"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        rentalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("rentalPrice"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Set options in the ComboBoxes
        categoryField.setItems(FXCollections.observableArrayList("Car", "Bike", "Truck", "Van"));
        availabilityField.setItems(FXCollections.observableArrayList("Available", "Unavailable"));

        // Load data from the database into the TableView
        loadVehicles();

        // When a row in the table is selected, show its details in the input fields
        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                vehicleIdField.setText(newSelection.getVehicleId());
                brandModelField.setText(newSelection.getBrandModel());
                categoryField.setValue(newSelection.getCategory());
                priceField.setText(newSelection.getRentalPrice());
                availabilityField.setValue(newSelection.getAvailability());
            }
        });
    }

    // Method to load all vehicles from the database into the list and table
    private void loadVehicles() {
        vehicleList.clear(); // Clear old data
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // SQL query to get all vehicles
            ResultSet rs = stmt.executeQuery("SELECT * FROM vehicles");

            // Add each row from the result set into the list
            while (rs.next()) {
                vehicleList.add(new vehicle(
                        String.valueOf(rs.getInt("vehicle_id")),
                        rs.getString("brand_model"),
                        rs.getString("category"),
                        String.valueOf(rs.getDouble("rental_price_per_day")),
                        rs.getString("availability_status")
                ));
            }

            // Show the list in the table
            vehicleTable.setItems(vehicleList);

        } catch (Exception e) {
            e.printStackTrace(); // Print any error to the console
        }
    }

    // Method for adding a new vehicle to the database
    @FXML
    private void handleAdd() {
        // Get input values
        String brandModel = brandModelField.getText();
        String category = categoryField.getValue();
        String priceText = priceField.getText();
        String availability = availabilityField.getValue();

        // Check if any input is missing
        if (brandModel.isEmpty() || category == null || priceText.isEmpty() || availability == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all fields.");
            return;
        }

        try {
            double rentalPrice = Double.parseDouble(priceText); // Convert price to number

            // Connect to database and insert the vehicle
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO vehicles (brand_model, category, rental_price_per_day, availability_status) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, brandModel);
                stmt.setString(2, category);
                stmt.setDouble(3, rentalPrice);
                stmt.setString(4, availability);
                stmt.executeUpdate();

                // Get the ID of the new vehicle and add it to the list
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    vehicleList.add(new vehicle(
                            String.valueOf(newId), brandModel, category,
                            String.valueOf(rentalPrice), availability));
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle added successfully!");
                    clearFields(); // Clear the form fields
                }

                stmt.close();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid price.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // Method to update selected vehicle details
    @FXML
    private void handleUpdate() {
        vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a vehicle to update.");
            return;
        }

        // Get updated values from form fields
        String id = selectedVehicle.getVehicleId();
        String brandModel = brandModelField.getText();
        String category = categoryField.getValue();
        String priceText = priceField.getText();
        String availability = availabilityField.getValue();

        // Check for missing inputs
        if (brandModel.isEmpty() || category == null || priceText.isEmpty() || availability == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all fields.");
            return;
        }

        try {
            double rentalPrice = Double.parseDouble(priceText);

            // Connect to database and update the record
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE vehicles SET brand_model=?, category=?, rental_price_per_day=?, availability_status=? WHERE vehicle_id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, brandModel);
                stmt.setString(2, category);
                stmt.setDouble(3, rentalPrice);
                stmt.setString(4, availability);
                stmt.setInt(5, Integer.parseInt(id));

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // Update the local list and refresh the table
                    selectedVehicle.setBrandModel(brandModel);
                    selectedVehicle.setCategory(category);
                    selectedVehicle.setRentalPrice(String.valueOf(rentalPrice));
                    selectedVehicle.setAvailability(availability);
                    vehicleTable.refresh();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle updated successfully!");
                    clearFields();
                }

                stmt.close();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid price.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // Method to delete selected vehicle
    @FXML
    private void handleDelete() {
        vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a vehicle to delete.");
            return;
        }

        try {
            String id = selectedVehicle.getVehicleId();
            // Connect to DB and delete the record
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "DELETE FROM vehicles WHERE vehicle_id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(id));

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    vehicleList.remove(selectedVehicle); // Remove from local list
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Vehicle deleted successfully!");
                    clearFields();
                }

                stmt.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // Clear all input fields
    private void clearFields() {
        vehicleIdField.clear();
        brandModelField.clear();
        categoryField.setValue(null);
        priceField.clear();
        availabilityField.setValue(null);
    }

    // Show pop-up messages to the user
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No subtitle
        alert.setContentText(message);
        alert.showAndWait(); // Show message and wait for user to close
    }

    // Placeholder for search logic
    @FXML
    private void handleSearch() {
        // You can implement search functionality here
        System.out.println("Search button clicked");
    }

    // Placeholder for clear button logic
    @FXML
    private void handleClear() {
        // You can implement extra clear logic here
        System.out.println("Clear button clicked");
    }
}
