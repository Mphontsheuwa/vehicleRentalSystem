package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class PaymentController {

    // Text field to input number of rental days
    @FXML
    private TextField rentalDaysField;

    // Text field to input daily vehicle fee
    @FXML
    private TextField vehicleFeeField;

    // Text field to input additional services cost
    @FXML
    private TextField additionalServicesField;

    // Text field to input late fee
    @FXML
    private TextField lateFeeField;

    // Combo box for selecting payment method
    @FXML
    private ComboBox<String> paymentMethodCombo;

    // Button to trigger invoice generation (declared for completeness)
    @FXML
    private Button generateInvoiceButton;

    // Text area to display the generated invoice
    @FXML
    private Text invoiceOutput;

    // This method is automatically called after the FXML is loaded
    @FXML
    public void initialize() {
        // Populate the combo box with payment method options
        paymentMethodCombo.getItems().addAll("Cash", "Credit Card", "Online");
    }

    // This method is triggered when the "Generate Invoice" button is clicked
    @FXML
    private void generateInvoice() {
        try {
            // Parse and collect all input values
            int rentalDays = Integer.parseInt(rentalDaysField.getText());
            double vehicleFee = Double.parseDouble(vehicleFeeField.getText());
            double additional = Double.parseDouble(additionalServicesField.getText());
            double lateFee = Double.parseDouble(lateFeeField.getText());
            String method = paymentMethodCombo.getValue();

            // Calculate total payment
            double total = (vehicleFee * rentalDays) + additional + lateFee;

            // Format the invoice text
            String invoice = String.format(
                    "====== INVOICE ======\n" +
                            "Rental Days: %d\n" +
                            "Vehicle Fee/day: %.2f\n" +
                            "Additional Services: %.2f\n" +
                            "Late Fee: %.2f\n" +
                            "Payment Method: %s\n" +
                            "---------------------------\n" +
                            "Total Amount: %.2f",
                    rentalDays, vehicleFee, additional, lateFee, method, total
            );

            // Display the invoice
            invoiceOutput.setText(invoice);

        } catch (NumberFormatException e) {
            // Handle invalid number input
            invoiceOutput.setText("Please enter valid numbers in all fields.");
        }
    }
}
