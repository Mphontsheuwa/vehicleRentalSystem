package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class vehicle {
    private final StringProperty vehicleId;
    private final StringProperty brandModel;
    private final StringProperty category;
    private final StringProperty rentalPrice;
    private final StringProperty availability;

    public vehicle(String vehicleId, String brandModel, String category, String rentalPrice, String availability) {
        this.vehicleId = new SimpleStringProperty(vehicleId);
        this.brandModel = new SimpleStringProperty(brandModel);
        this.category = new SimpleStringProperty(category);
        this.rentalPrice = new SimpleStringProperty(rentalPrice);
        this.availability = new SimpleStringProperty(availability);
    }

    public String getVehicleId() { return vehicleId.get(); }
    public String getBrandModel() { return brandModel.get(); }
    public String getCategory() { return category.get(); }
    public String getRentalPrice() { return rentalPrice.get(); }
    public String getAvailability() { return availability.get(); }

    public void setVehicleId(String value) { vehicleId.set(value); }
    public void setBrandModel(String value) { brandModel.set(value); }
    public void setCategory(String value) { category.set(value); }
    public void setRentalPrice(String value) { rentalPrice.set(value); }
    public void setAvailability(String value) { availability.set(value); }

    public StringProperty vehicleIdProperty() { return vehicleId; }
    public StringProperty brandModelProperty() { return brandModel; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty rentalPriceProperty() { return rentalPrice; }
    public StringProperty availabilityProperty() { return availability; }
}
