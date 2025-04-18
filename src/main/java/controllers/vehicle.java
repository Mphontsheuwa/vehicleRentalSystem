package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class vehicle {

    // Declare properties of the vehicle using StringProperty.
    // StringProperty allows JavaFX to observe changes in the properties for updating the UI automatically.
    private final StringProperty vehicleId;
    private final StringProperty brandModel;
    private final StringProperty category;
    private final StringProperty rentalPrice;
    private final StringProperty availability;

    // Constructor to initialize the vehicle object with values for each property.
    public vehicle(String vehicleId, String brandModel, String category, String rentalPrice, String availability) {
        this.vehicleId = new SimpleStringProperty(vehicleId);  // Initialize vehicleId property
        this.brandModel = new SimpleStringProperty(brandModel);  // Initialize brandModel property
        this.category = new SimpleStringProperty(category);  // Initialize category property
        this.rentalPrice = new SimpleStringProperty(rentalPrice);  // Initialize rentalPrice property
        this.availability = new SimpleStringProperty(availability);  // Initialize availability property
    }

    // Getters to retrieve the current value of each property.
    public String getVehicleId() { return vehicleId.get(); }  // Returns the vehicle ID
    public String getBrandModel() { return brandModel.get(); }  // Returns the brand and model of the vehicle
    public String getCategory() { return category.get(); }  // Returns the category of the vehicle (e.g., SUV, Sedan)
    public String getRentalPrice() { return rentalPrice.get(); }  // Returns the rental price of the vehicle
    public String getAvailability() { return availability.get(); }  // Returns the availability status of the vehicle

    // Setters to update the values of the properties.
    public void setVehicleId(String value) { vehicleId.set(value); }  // Sets a new value for vehicle ID
    public void setBrandModel(String value) { brandModel.set(value); }  // Sets a new value for brand and model
    public void setCategory(String value) { category.set(value); }  // Sets a new value for vehicle category
    public void setRentalPrice(String value) { rentalPrice.set(value); }  // Sets a new value for rental price
    public void setAvailability(String value) { availability.set(value); }  // Sets a new value for availability status

    // These methods return the properties as StringProperty, which are used for JavaFX binding.
    public StringProperty vehicleIdProperty() { return vehicleId; }  // Returns the vehicle ID property
    public StringProperty brandModelProperty() { return brandModel; }  // Returns the brand and model property
    public StringProperty categoryProperty() { return category; }  // Returns the category property
    public StringProperty rentalPriceProperty() { return rentalPrice; }  // Returns the rental price property
    public StringProperty availabilityProperty() { return availability; }  // Returns the availability property
}
