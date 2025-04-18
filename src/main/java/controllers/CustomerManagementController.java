package controllers;

import database.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerManagementController {

    // FXML elements from the UI
    @FXML private TextField customerIdField;
    @FXML private TextField nameField;
    @FXML private TextField contactField;          // This will correspond to contact_information in the database
    @FXML private TextField licenseNumberField;    // This will correspond to driving_license_number
    @FXML private TextField rentalHistoryField;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerIdColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> contactColumn;
    @FXML private TableColumn<Customer, String> licenseColumn;
    @FXML private TableColumn<Customer, String> rentalHistoryColumn;

    // ObservableList to store customer data for the TableView
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    // Called automatically when the FXML file is loaded
    @FXML
    public void initialize() {
        // Bind table columns to Customer class properties
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        licenseColumn.setCellValueFactory(cellData -> cellData.getValue().licenseNumberProperty());
        rentalHistoryColumn.setCellValueFactory(cellData -> cellData.getValue().rentalHistoryProperty());

        loadCustomers(); // Load customer records from the database
    }

    // Add a new customer to the database
    @FXML
    private void handleRegister() {
        // Get input from fields
        String name = nameField.getText();
        String contact = contactField.getText();
        String licenseNumber = licenseNumberField.getText();
        String rentalHistory = rentalHistoryField.getText();

        // SQL insert statement
        String sql = "INSERT INTO customers (name, contact_information, driving_license_number, rental_history) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters for the prepared statement
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, licenseNumber);
            pstmt.setString(4, rentalHistory);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Inserting customer failed, no rows affected.");
            }

            // Retrieve the auto-generated customer_id
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int customerId = generatedKeys.getInt(1);
                    System.out.println("Customer added with ID: " + customerId);

                    // Add new customer to the ObservableList for TableView display
                    Customer newCustomer = new Customer(String.valueOf(customerId), name, contact, licenseNumber, rentalHistory);
                    customerList.add(newCustomer);
                } else {
                    throw new SQLException("Inserting customer failed, no ID obtained.");
                }
            }
            refreshTable();  // Refresh table display
            clearFields();   // Clear input fields
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update the selected customer's data
    @FXML
    private void handleUpdate() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Get input values
            String name = nameField.getText();
            String contact = contactField.getText();
            String licenseNumber = licenseNumberField.getText();
            String rentalHistory = rentalHistoryField.getText();

            String sql = "UPDATE customers SET name = ?, contact_information = ?, driving_license_number = ?, rental_history = ? WHERE customer_id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // Set values in the SQL query
                pstmt.setString(1, name);
                pstmt.setString(2, contact);
                pstmt.setString(3, licenseNumber);
                pstmt.setString(4, rentalHistory);
                pstmt.setInt(5, Integer.parseInt(selected.getCustomerId()));

                pstmt.executeUpdate();

                // Update values in the local list (for TableView)
                selected.setName(name);
                selected.setContact(contact);
                selected.setLicenseNumber(licenseNumber);
                selected.setRentalHistory(rentalHistory);

                refreshTable();  // Refresh table
                clearFields();   // Clear form
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete selected customer from database and TableView
    @FXML
    private void handleDelete() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String sql = "DELETE FROM customers WHERE customer_id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, Integer.parseInt(selected.getCustomerId()));
                pstmt.executeUpdate();

                customerList.remove(selected);  // Remove from the list
                refreshTable();                 // Refresh the table
                clearFields();                  // Clear input fields
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Search for a customer using customer_id
    @FXML
    private void handleSearch() {
        String id = customerIdField.getText();
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = pstmt.executeQuery();

            customerList.clear();  // Clear current list
            while (rs.next()) {
                Customer customer = new Customer(
                        String.valueOf(rs.getInt("customer_id")),
                        rs.getString("name"),
                        rs.getString("contact_information"),
                        rs.getString("driving_license_number"),
                        rs.getString("rental_history")
                );
                customerList.add(customer);

                // Also populate input fields with found data
                nameField.setText(customer.getName());
                contactField.setText(customer.getContact());
                licenseNumberField.setText(customer.getLicenseNumber());
                rentalHistoryField.setText(customer.getRentalHistory());
            }
            customerTable.setItems(customerList);  // Set results to the table
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load all customers from the database into the TableView
    private void loadCustomers() {
        String sql = "SELECT * FROM customers";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            customerList.clear(); // Clear existing entries
            while (rs.next()) {
                Customer customer = new Customer(
                        String.valueOf(rs.getInt("customer_id")),
                        rs.getString("name"),
                        rs.getString("contact_information"),
                        rs.getString("driving_license_number"),
                        rs.getString("rental_history")
                );
                customerList.add(customer);
            }
            customerTable.setItems(customerList); // Show in table
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Refreshes the TableView with the latest ObservableList
    private void refreshTable() {
        customerTable.setItems(FXCollections.observableArrayList(customerList));
    }

    // Clears all text fields in the form
    private void clearFields() {
        customerIdField.clear();
        nameField.clear();
        contactField.clear();
        licenseNumberField.clear();
        rentalHistoryField.clear();
    }

    // --- Inner class representing a Customer (JavaFX properties) ---
    public static class Customer {
        private final StringProperty customerId;
        private final StringProperty name;
        private final StringProperty contact;
        private final StringProperty licenseNumber;
        private final StringProperty rentalHistory;

        // Constructor to initialize properties
        public Customer(String customerId, String name, String contact, String licenseNumber, String rentalHistory) {
            this.customerId = new SimpleStringProperty(customerId);
            this.name = new SimpleStringProperty(name);
            this.contact = new SimpleStringProperty(contact);
            this.licenseNumber = new SimpleStringProperty(licenseNumber);
            this.rentalHistory = new SimpleStringProperty(rentalHistory);
        }

        // JavaFX Property getters for binding to TableView
        public StringProperty customerIdProperty() { return customerId; }
        public StringProperty nameProperty() { return name; }
        public StringProperty contactProperty() { return contact; }
        public StringProperty licenseNumberProperty() { return licenseNumber; }
        public StringProperty rentalHistoryProperty() { return rentalHistory; }

        // Getters and setters for field values
        public String getCustomerId() { return customerId.get(); }
        public void setCustomerId(String id) { customerId.set(id); }

        public String getName() { return name.get(); }
        public void setName(String name) { this.name.set(name); }

        public String getContact() { return contact.get(); }
        public void setContact(String contact) { this.contact.set(contact); }

        public String getLicenseNumber() { return licenseNumber.get(); }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber.set(licenseNumber); }

        public String getRentalHistory() { return rentalHistory.get(); }
        public void setRentalHistory(String rentalHistory) { this.rentalHistory.set(rentalHistory); }
    }
}
