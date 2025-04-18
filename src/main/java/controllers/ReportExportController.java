package controllers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportExportController {

    // Method to export data to a CSV file
    public void exportToCSV(Stage stage) {
        // Define the header of the CSV file
        String[] header = {"ID", "Name", "Date", "Amount"};

        // Example data to be exported (this can be replaced with dynamic data)
        String[][] data = {
                {"1", "Product 1", "2024-04-18", "150.00"},
                {"2", "Product 2", "2024-04-19", "200.00"},
        };

        // Create a file chooser to allow the user to select where to save the CSV
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Show the save dialog and get the selected file
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write the header to the CSV file
                writer.write(String.join(",", header));
                writer.newLine();

                // Write each row of data to the CSV file
                for (String[] row : data) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }

                // Show confirmation message after successful export
                showConfirmation("CSV Export", "Data has been exported to CSV successfully.");
            } catch (IOException e) {
                // Show error message if there is an exception while exporting
                showError("Error", "Failed to export to CSV: " + e.getMessage());
            }
        }
    }

    // Method to show a confirmation alert when the export is successful
    private void showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to show an error alert if the export fails
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
