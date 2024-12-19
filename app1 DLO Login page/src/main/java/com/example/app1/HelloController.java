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
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    @FXML
    private Button cancel_button;
    @FXML
    private Button login_Button;
    @FXML
    private Label login_message_label;
    @FXML
    private TextField username_textfield;
    @FXML
    private PasswordField password_field;
    @FXML
    private Button signup_button;
    @FXML
    private Button admin_login_button;

    public void initialize() {
        // Adding click effect for buttons
        addClickEffect(cancel_button);
        addClickEffect(login_Button);
        addClickEffect(signup_button);
        addClickEffect(admin_login_button);
    }

    @FXML
    private void loginButtonOnAction(ActionEvent e) {
        String inputUsername = username_textfield.getText().trim();
        UserSession.getInstance().setUsername(inputUsername);
        String inputPassword = password_field.getText();

        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            login_message_label.setText("Please fill in both fields.");
            login_message_label.setTextFill(Color.RED);
            return;
        }

        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = MySQLConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, inputUsername);  // Bind parameter to username
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");
                String userStatus = resultSet.getString("status");

                // Check if the password matches
                if (PasswordUtils.checkPassword(inputPassword, storedPasswordHash)) {
                    if ("Approved".equals(userStatus)) {
                        navigateToHomePage(inputUsername); // Pass the username
                    } else {
                        login_message_label.setText("Access denied. Your account is not approved.");
                        login_message_label.setTextFill(Color.RED);
                    }
                } else {
                    login_message_label.setText("Invalid username or password.");
                    login_message_label.setTextFill(Color.RED);
                }
            } else {
                login_message_label.setText("User not found.");
                login_message_label.setTextFill(Color.RED);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            login_message_label.setText("Database error. Please try again later.");
            login_message_label.setTextFill(Color.RED);
        }
    }

    private void navigateToHomePage(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage.fxml"));
            Parent root = loader.load();

            // Get the controller and set the username
            HomePageController homeController = loader.getController();
            homeController.setCurrentUsername(username);

            Stage stage = (Stage) login_Button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Password Manager - Home");
        } catch (IOException ex) {
            ex.printStackTrace();
            login_message_label.setText("Failed to load homepage.");
            login_message_label.setTextFill(Color.RED);
        }
    }


    public void cancel_buttonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }

    public void adminLoginButtonOnAction(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/app1/Adminlogin.fxml")); // Update with correct path
            Stage s1 = (Stage) admin_login_button.getScene().getWindow();
            s1.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
            login_message_label.setText("Error loading admin login.");
            login_message_label.setTextFill(Color.RED);
        }
    }

    public void signupButtonOnAction(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/app1/signup.fxml")); // Update with correct path
            Stage stage = (Stage) signup_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
            login_message_label.setText("Failed to load signup page.");
            login_message_label.setTextFill(Color.RED);
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
            button.setStyle("-fx-background-color: linear-gradient(to right, #ff7e5f, #feb47b); " +
                    "-fx-background-radius: 10;");
        });
    }
}
