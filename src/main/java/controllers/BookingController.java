package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    @FXML private ComboBox<String> vehicleComboBox; // ComboBox to select vehicle
    @FXML private DatePicker startDatePicker; // DatePicker to select the start date
    @FXML private DatePicker endDatePicker; // DatePicker to select the end date
    @FXML private Button submitBookingButton; // Button to submit a new booking
    @FXML private Button modifyBookingButton; // Button to modify an existing booking
    @FXML private Button cancelBookingButton; // Button to cancel an existing booking
    @FXML private ListView<String> bookingListView; // ListView to display all bookings

    private List<String> bookings = new ArrayList<>(); // List to store booking details

    @FXML
    private void initialize() {
        // Preload vehicle options for ComboBox
        vehicleComboBox.getItems().addAll("Vehicle 1", "Vehicle 2", "Vehicle 3");
        vehicleComboBox.getSelectionModel().selectFirst(); // Default vehicle selection
        updateBookingListView(); // Update booking list initially
    }

    // Handle Booking Submission
    @FXML
    private void handleBookingSubmission() {
        String vehicle = vehicleComboBox.getValue(); // Get selected vehicle
        LocalDate startDate = startDatePicker.getValue(); // Get selected start date
        LocalDate endDate = endDatePicker.getValue(); // Get selected end date

        // Check if any of the fields are empty
        if (vehicle == null || startDate == null || endDate == null) {
            showAlert("Error", "Please fill in all fields!");
            return;
        }

        // Check if start date is after end date
        if (startDate.isAfter(endDate)) {
            showAlert("Error", "Start date cannot be after end date.");
            return;
        }

        // Create booking string and check if the same booking already exists
        String booking = vehicle + " - " + startDate + " to " + endDate;
        if (bookings.contains(booking)) {
            showAlert("Error", "This vehicle is already booked for the selected dates.");
            return;
        }

        // Add new booking to the list
        bookings.add(booking);
        showAlert("Success", "Your booking has been successfully created!");
        updateBookingListView(); // Update the ListView with the new booking
    }

    // Handle Modify Booking
    @FXML
    private void handleModifyBooking() {
        String selectedBooking = bookingListView.getSelectionModel().getSelectedItem(); // Get selected booking from ListView
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to modify.");
            return;
        }

        // Extract vehicle and dates from the selected booking string
        String[] parts = selectedBooking.split(" - ");
        String vehicle = parts[0];
        String[] dates = parts[1].split(" to ");
        LocalDate startDate = LocalDate.parse(dates[0]);
        LocalDate endDate = LocalDate.parse(dates[1]);

        // Set the values back to the fields for modification
        vehicleComboBox.setValue(vehicle);
        startDatePicker.setValue(startDate);
        endDatePicker.setValue(endDate);

        // Remove the old booking from the list
        bookings.remove(selectedBooking);
    }

    // Handle Cancel Booking
    @FXML
    private void handleCancelBooking() {
        String selectedBooking = bookingListView.getSelectionModel().getSelectedItem(); // Get selected booking from ListView
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to cancel.");
            return;
        }

        // Remove the selected booking from the list
        bookings.remove(selectedBooking);
        showAlert("Success", "Your booking has been successfully canceled!");
        updateBookingListView(); // Update the ListView to reflect the cancellation
    }

    // Update the ListView to show current bookings
    private void updateBookingListView() {
        bookingListView.getItems().clear(); // Clear the ListView
        bookingListView.getItems().addAll(bookings); // Add updated bookings to the ListView
    }

    // Show an Alert Message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Create an information alert
        alert.setTitle(title); // Set the title of the alert
        alert.setHeaderText(null); // Set the header text of the alert (null means no header)
        alert.setContentText(message); // Set the content of the alert
        alert.showAndWait(); // Show the alert and wait for user response
    }
}
