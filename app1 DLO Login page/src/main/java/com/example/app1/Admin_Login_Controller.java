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
public class Admin_Login_Controller {

    @FXML
    private TextField admin_username_textfield;
    @FXML
    private PasswordField admin_password_field;
    @FXML
    private Label login_message_label;
@FXML
private Button back_button;
@FXML
private Button admin_login_button;

    public void initialize() {

        addClickEffect(admin_login_button);
        addClickEffect(back_button);

    }
    public void adminLoginButtonOnAction(ActionEvent e) {
        String username = admin_username_textfield.getText();
        String password = admin_password_field.getText();

        if (username.equals("admin") && password.equals("admin123")) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("adminDashboard.fxml"));
                Stage stage = (Stage) admin_login_button.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            login_message_label.setText("Invalid credentials. Please try again.");
            login_message_label.setTextFill(Color.RED);
        }
    }

    // admin theke login page e jawar jonno back button
    public void backbuttonOnaction(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) back_button.getScene().getWindow();
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
            button.setStyle("-fx-background-color: linear-gradient(to right, #ff7e5f, #feb47b); " +
                    "-fx-background-radius: 10;");
        });
    }


}
