package com.example.app1;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardController {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> statusColumn;
    @FXML
    private Button approveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button back_button;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private MySQLConnector mysqlConnector;

    @FXML
    public void initialize() {
        mysqlConnector = new MySQLConnector();

        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadUsers();
        addClickEffect(back_button);
        addClickEffect(approveButton);
        addClickEffect(deleteButton);
    }

    private void loadUsers() {
        userList.clear();
        String query = "SELECT * FROM users";
        try (Connection conn = MySQLConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }

            usersTable.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void backbuttonOnAction(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Adminlogin.fxml"));
            Stage stage = (Stage) back_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void approveUser(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if ("Approved".equals(selectedUser.getStatus())) {
                showAlert(Alert.AlertType.WARNING, "Already Approved", "This user is already approved.");
                return;
            }

            boolean success = mysqlConnector.approveUser(selectedUser.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User approved successfully!");
                loadUsers(); // Reload the users list
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve user.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to approve.");
        }
    }

    public void deleteUser(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Confirm deletion
            boolean confirmed = ConfirmDialog.show("Delete User", "Are you sure you want to delete this user?");
            if (confirmed) {
                boolean success = mysqlConnector.deleteUserById(selectedUser.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                    loadUsers(); // Reload the users list
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to delete.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
            button.setStyle("-fx-background-color:  linear-gradient(to right, #00008b, #87ceeb); " +
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
