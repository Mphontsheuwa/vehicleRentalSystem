package controllers; // Or models, depending on your structure

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class customer {

    private final StringProperty customerId;
    private final StringProperty name;
    private final StringProperty contact;
    private final StringProperty licenseNumber;
    private final StringProperty rentalHistory;

    public customer(String customerId, String name, String contact, String licenseNumber, String rentalHistory) {
        this.customerId = new SimpleStringProperty(customerId);
        this.name = new SimpleStringProperty(name);
        this.contact = new SimpleStringProperty(contact);
        this.licenseNumber = new SimpleStringProperty(licenseNumber);
        this.rentalHistory = new SimpleStringProperty(rentalHistory);
    }

    // Getters for property bindings
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

    // Optional: Regular getters and setters if needed
    public String getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(String id) {
        this.customerId.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String n) {
        this.name.set(n);
    }

    public String getContact() {
        return contact.get();
    }

    public void setContact(String c) {
        this.contact.set(c);
    }

    public String getLicenseNumber() {
        return licenseNumber.get();
    }

    public void setLicenseNumber(String ln) {
        this.licenseNumber.set(ln);
    }

    public String getRentalHistory() {
        return rentalHistory.get();
    }

    public void setRentalHistory(String rh) {
        this.rentalHistory.set(rh);
    }
}
