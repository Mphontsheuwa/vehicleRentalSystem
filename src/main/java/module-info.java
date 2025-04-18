module firstcom.example.vehiclerentalsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports main;  // You export your main package if needed
    exports controllers;  // You also export controllers
    opens controllers to javafx.fxml;  // This allows FXML to access controllers

    opens firstcom.example.vehiclerentalsystem to javafx.fxml;
    opens main to javafx.fxml;  // Make sure this is correctly placed (usually not needed)
}
