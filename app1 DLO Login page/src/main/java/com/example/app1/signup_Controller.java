package com.example.app1;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class signup_Controller {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField rePasswordField;
    @FXML
    private Button submit_button;
    @FXML
    private Button back_to_login_button;
    @FXML
    private Label messageLabel; // Label for displaying messages (error/success)

    // MySQL connection instance
    private MySQLConnector mysqlConnector;

    public void initialize() {
        mysqlConnector = new MySQLConnector();
        addClickEffect(submit_button);
        addClickEffect(back_to_login_button);
    }

    @FXML
    private void submitButtonAction(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = rePasswordField.getText();

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Insert data into the database
        boolean success = mysqlConnector.saveUser(username, email, password);
        if (success) {
            messageLabel.setText("Account created successfully! Await admin approval.");
            messageLabel.setTextFill(Color.GREEN);
            // Optionally, navigate back to login
        } else {
            messageLabel.setText("Account creation failed. Try again.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void backButtonAction(ActionEvent e) throws IOException {
        // Transition back to login screen
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage) back_to_login_button.getScene().getWindow();
        stage.setScene(new Scene(root)); // Adjusted window size
    }

    private void addClickEffect(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.DARKGRAY);
        shadow.setRadius(15);
        shadow.setSpread(0.2);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);

        button.setOnMousePressed(event -> {
            scaleTransition.setToX(0.9);
            scaleTransition.setToY(0.9);
            scaleTransition.play();

            button.setEffect(shadow);
            button.setStyle("-fx-background-color: linear-gradient(to right, #ff7e5f, #feb47b); " +
                    "-fx-background-radius: 10; " +
                    "-fx-effect: innershadow(gaussian, rgba(255,255,255,0.3), 10, 0.5, 0, 0);");
        });

        button.setOnMouseReleased(event -> {
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();

            button.setEffect(null);
            button.setStyle("-fx-background-color: linear-gradient(to right, #ff7e5f, #feb47b); " +
                    "-fx-background-radius: 10;");
        });
    }
}
