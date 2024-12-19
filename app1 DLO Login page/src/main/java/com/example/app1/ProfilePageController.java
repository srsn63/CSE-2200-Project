package com.example.app1;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class ProfilePageController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    public  String loggedInUsername;

    private MySQLConnector mySQLConnector = new MySQLConnector();

    public void initialize() {
        addClickEffect(backButton);
        addClickEffect(saveButton);
        loggedInUsername = UserSession.getInstance().getUsername();
        if (loggedInUsername == null) {
            System.out.println("Logged in username is null");
            return; // Exit early if no logged-in username
        }
        try {
            // Fetch user details from the database
            String[] userDetails = mySQLConnector.getUserDetails(loggedInUsername);
            if (userDetails != null) {
                System.out.println("Fetched user details: " + userDetails[0] + ", " + userDetails[1]);
                usernameField.setText(userDetails[0]); // Username
                emailField.setText(userDetails[1]);   // Email
            } else {
                System.out.println("User details not found");
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch user details: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML

    public void handleSave(ActionEvent event) {
        String newUsername = usernameField.getText();
        String newEmail = emailField.getText();

        // Check if there's any change in the username
        if (!newUsername.equals(loggedInUsername)) {
            try {
                // Attempt to update the user details in the database
                boolean updated = mySQLConnector.updateUserDetails(loggedInUsername, newUsername, newEmail);

                // Show success or failure alert based on the result
                if (updated) {
                    System.out.println("Profile updated successfully.");
                    loggedInUsername = newUsername; // Update in memory

                    // Show success alert
                    Alert successAlert = new Alert(AlertType.INFORMATION);
                    successAlert.setTitle("Profile Update");
                    successAlert.setHeaderText(null); // No header
                    successAlert.setContentText("Profile updated successfully.");
                    successAlert.showAndWait(); // Display the alert and wait for user response
                } else {
                    System.out.println("Failed to update profile.");

                    // Show failure alert
                    Alert failureAlert = new Alert(AlertType.ERROR);
                    failureAlert.setTitle("Profile Update");
                    failureAlert.setHeaderText(null); // No header
                    failureAlert.setContentText("Failed to update profile.");
                    failureAlert.showAndWait(); // Display the alert and wait for user response
                }
            } catch (Exception e) {
                System.out.println("Error updating profile: " + e.getMessage());
                e.printStackTrace();

                // Show error alert in case of an exception
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Profile Update");
                errorAlert.setHeaderText(null); // No header
                errorAlert.setContentText("Error updating profile: " + e.getMessage());
                errorAlert.showAndWait(); // Display the alert and wait for user response
            }
        } else {
            System.out.println("No changes detected.");

            // Show alert for no changes detected
            Alert noChangesAlert = new Alert(Alert.AlertType.INFORMATION);
            noChangesAlert.setTitle("Profile Update");
            noChangesAlert.setHeaderText(null); // No header
            noChangesAlert.setContentText("No changes detected.");
            noChangesAlert.showAndWait(); // Display the alert and wait for user response
        }
    }
@FXML
    public void backButtonOnAction(ActionEvent actionEvent)
    {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("homepage.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root)); // Adjusted window size
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    private void addClickEffect(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.DARKGRAY);
        shadow.setRadius(15);
        shadow.setSpread(0.2);

        // Transition for scaling
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);

        // On mouse press, apply a ripple effect, shrink the button, and add shadow
        button.setOnMousePressed(event -> {
            scaleTransition.setToX(0.9);
            scaleTransition.setToY(0.9);
            scaleTransition.play();

            // Add the shadow
            button.setEffect(shadow);

            // Add a subtle glow
            button.setStyle("-fx-background-color: linear-gradient(to right, #ff7e5f, #feb47b); " +
                    "-fx-background-radius: 10; " +
                    "-fx-effect: innershadow(gaussian, rgba(255,255,255,0.3), 10, 0.5, 0, 0);");
        });

        // On mouse release, restore the button's size and remove the shadow with animation
        button.setOnMouseReleased(event -> {
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();

            // Remove the shadow and reset the style
            button.setEffect(null);
            button.setStyle("-fx-background-color:  linear-gradient(to right, #004d00, #66cc66); " +
                    "-fx-background-radius: 10;");
        });
    }
}
