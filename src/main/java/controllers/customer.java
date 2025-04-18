package controllers; // This defines the package where the class is stored

// Importing JavaFX classes used to create observable string properties
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// This class represents a customer in the system
public class customer {

    // Defining properties for each customer detail
    // StringProperty allows JavaFX to observe changes in values
    private final StringProperty customerId;
    private final StringProperty name;
    private final StringProperty contact;
    private final StringProperty licenseNumber;
    private final StringProperty rentalHistory;

    // Constructor to initialize a new customer object with given details
    public customer(String customerId, String name, String contact, String licenseNumber, String rentalHistory) {
        this.customerId = new SimpleStringProperty(customerId); // Initializes the customerId property
        this.name = new SimpleStringProperty(name);             // Initializes the name property
        this.contact = new SimpleStringProperty(contact);       // Initializes the contact property
        this.licenseNumber = new SimpleStringProperty(licenseNumber); // Initializes the license number
        this.rentalHistory = new SimpleStringProperty(rentalHistory); // Initializes the rental history
    }

    // These are property getter methods used for JavaFX data binding
    public StringProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public StringProperty licenseNumberProperty() {
        return licenseNumber;
    }

    public StringProperty rentalHistoryProperty() {
        return rentalHistory;
    }

    // Regular getter method to retrieve the customer ID as a String
    public String getCustomerId() {
        return customerId.get();
    }

    // Regular setter method to update the customer ID
    public void setCustomerId(String id) {
        this.customerId.set(id);
    }

    // Getter method for the customer's name
    public String getName() {
        return name.get();
    }

    // Setter method to update the customer's name
    public void setName(String n) {
        this.name.set(n);
    }

    // Getter method for the customer's contact
    public String getContact() {
        return contact.get();
    }

    // Setter method to update the customer's contact
    public void setContact(String c) {
        this.contact.set(c);
    }

    // Getter method for the customer's license number
    public String getLicenseNumber() {
        return licenseNumber.get();
    }

    // Setter method to update the license number
    public void setLicenseNumber(String ln) {
        this.licenseNumber.set(ln);
    }

    // Getter method for rental history
    public String getRentalHistory() {
        return rentalHistory.get();
    }

    // Setter method to update rental history
    public void setRentalHistory(String rh) {
        this.rentalHistory.set(rh);
    }
}
