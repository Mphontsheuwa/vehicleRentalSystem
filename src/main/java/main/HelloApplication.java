package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    // This method is called to set up and display the primary stage of the application
    @Override
    public void start(Stage stage) {
        try {
            // Load the FXML file for the login view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));

            // Create a scene using the loaded FXML
            Scene scene = new Scene(fxmlLoader.load());

            // Set the title for the window (stage)
            stage.setTitle("Vehicle Rental System");

            // Set the scene to the stage
            stage.setScene(scene);

            // Show the stage (window)
            stage.show();
        } catch (Exception e) {
            // Print any errors to the console if FXML file loading fails
            e.printStackTrace();
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args); // Calls the launch method which starts the JavaFX application lifecycle
    }
}
