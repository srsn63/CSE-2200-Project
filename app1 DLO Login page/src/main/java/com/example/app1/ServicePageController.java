package com.example.app1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.List;


/**
 * Controller for managing profiles within a specific service.
 */
public class ServicePageController {

//
//
//    @FXML
//    private Label serviceNameLabel;
//
//    @FXML
//    private ListView<Profile> profilesListView;
//
//    private String serviceName;
//    private String currentUsername;
//
//    private MySQLConnector dbConnector;
//
//    @FXML
//    private void initialize() {
//        dbConnector = new MySQLConnector();
//    }
//
//    /**
//     * Sets the name of the service and updates the UI accordingly.
//     *
//     * @param serviceName The name of the service (e.g., Instagram, Gmail).
//     */
//    public void setServiceName(String serviceName) {
//        this.serviceName = serviceName;
//        serviceNameLabel.setText(serviceName);
//        if (this.currentUsername != null) {
//            loadProfiles();
//        }
//    }
//
//    /**
//     * Sets the current user's username and updates the UI accordingly.
//     *
//     * @param currentUsername The username of the currently logged-in user.
//     */
//    public void setCurrentUsername(String currentUsername) {
//        this.currentUsername = currentUsername;
//        if (this.serviceName != null) {
//            loadProfiles();
//        }
//    }
//
//    /**
//     * Loads all profiles associated with the current user and service.
//     */
//    private void loadProfiles() {
//        List<Profile> profiles = dbConnector.getProfiles(currentUsername, serviceName);
//        ObservableList<Profile> observableProfiles = FXCollections.observableArrayList(profiles);
//        profilesListView.setItems(observableProfiles);
//        profilesListView.setCellFactory(param -> new ProfileListCell());
//    }
//
//    /**
//     * Handles the action of adding a new profile.
//     *
//     * @param event The ActionEvent triggered by clicking the "Add Profile" button.
//     */
//    @FXML
//    private void addProfile(ActionEvent event) {
//        TextInputDialog addDialog = new TextInputDialog();
//        addDialog.setTitle("Add New " + serviceName + " Profile");
//        addDialog.setHeaderText(null);
//        addDialog.setContentText("Enter profile name:");
//
//        Optional<String> profileName = addDialog.showAndWait();
//        profileName.ifPresent(name -> {
//            if (!name.trim().isEmpty()) {
//                Dialog<Pair<String, String>> credentialsDialog = new Dialog<>();
//                credentialsDialog.setTitle("Profile Credentials");
//                credentialsDialog.setHeaderText("Enter Profile Username and Password:");
//
//                ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
//                credentialsDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
//
//                GridPane grid = new GridPane();
//                grid.setHgap(10);
//                grid.setVgap(10);
//                grid.setPadding(new Insets(20, 150, 10, 10));
//
//                TextField profileUsernameField = new TextField();
//                profileUsernameField.setPromptText("Profile Username");
//
//                PasswordField profilePasswordField = new PasswordField();  // Secure password input
//                profilePasswordField.setPromptText("Profile Password");
//
//                grid.add(new Label("Profile Username:"), 0, 0);
//                grid.add(profileUsernameField, 1, 0);
//                grid.add(new Label("Profile Password:"), 0, 1);
//                grid.add(profilePasswordField, 1, 1);
//
//                credentialsDialog.getDialogPane().setContent(grid);
//
//                // Enable/Disable add button depending on whether fields are filled
//                Node addButton = credentialsDialog.getDialogPane().lookupButton(addButtonType);
//                addButton.setDisable(true);
//
//                // Validate input
//                profileUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
//                    addButton.setDisable(newValue.trim().isEmpty() || profilePasswordField.getText().trim().isEmpty());
//                });
//
//                profilePasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
//                    addButton.setDisable(newValue.trim().isEmpty() || profileUsernameField.getText().trim().isEmpty());
//                });
//
//                credentialsDialog.setResultConverter(dialogButton -> {
//                    if (dialogButton == addButtonType) {
//                        return new Pair<>(profileUsernameField.getText(), profilePasswordField.getText());
//                    }
//                    return null;
//                });
//
//                Optional<Pair<String, String>> result = credentialsDialog.showAndWait();
//                result.ifPresent(credentials -> {
//                    String profileUsername = credentials.getKey();
//                    String profilePassword = credentials.getValue();
//                    boolean success = dbConnector.addProfile(currentUsername, serviceName, name.trim(), profileUsername, profilePassword);
//                    if (success) {
//                        loadProfiles();
//                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile added successfully.");
//                    } else {
//                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add profile.");
//                    }
//                });
//            } else {
//                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Profile name cannot be empty.");
//            }
//        });
//    }
//
//    /**
//     * Custom ListCell to display profile information with action buttons.
//     */
//    private class ProfileListCell extends ListCell<Profile> {
//        private HBox hBox = new HBox(10);
//        private Label nameLabel = new Label();
//        private Label usernameLabel = new Label();
//        private Label passwordLabel = new Label();
//        private Button viewEditButton = new Button("View/Edit");
//        private Button deleteButton = new Button("Delete");
//
//        public ProfileListCell() {
//            super();
//            hBox.setAlignment(Pos.CENTER_LEFT);
//            hBox.getChildren().addAll(nameLabel, usernameLabel, passwordLabel, viewEditButton, deleteButton);
//
//            viewEditButton.setOnAction(event -> {
//                Profile profile = getItem();
//                if (profile != null) {
//                    showProfileDetailsDialog(profile);
//                }
//            });
//
//            deleteButton.setOnAction(event -> {
//                Profile profile = getItem();
//                if (profile != null) {
//                    handleDeleteProfile(profile);
//                }
//            });
//        }
//
//        @Override
//        protected void updateItem(Profile profile, boolean empty) {
//            super.updateItem(profile, empty);
//            if (empty || profile == null) {
//                setText(null);
//                setGraphic(null);
//            } else {
//                nameLabel.setText("Name: " + profile.getProfileName());
//                usernameLabel.setText("Username: " + profile.getProfileUsername());
//                passwordLabel.setText("Password: " + profile.getProfilePassword()); // Consider hiding password
//                setGraphic(hBox);
//            }
//        }
//    }
//
//    /**
//     * Displays a dialog to view and edit profile details.
//     *
//     * @param profile The Profile object to view/edit.
//     */
//    private void showProfileDetailsDialog(Profile profile) {
//        Dialog<Void> dialog = new Dialog<>();
//        dialog.setTitle("Profile Details - " + profile.getProfileName());
//        dialog.setHeaderText(null);
//        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
//
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//
//        TextField profileNameField = new TextField();
//        profileNameField.setText(profile.getProfileName());
//
//        TextField profileUsernameField = new TextField();
//        profileUsernameField.setText(profile.getProfileUsername());
//
//        PasswordField profilePasswordField = new PasswordField(); // Secure password input
//        profilePasswordField.setText(profile.getProfilePassword());
//
//        grid.add(new Label("Profile Name:"), 0, 0);
//        grid.add(profileNameField, 1, 0);
//        grid.add(new Label("Profile Username:"), 0, 1);
//        grid.add(profileUsernameField, 1, 1);
//        grid.add(new Label("Profile Password:"), 0, 2);
//        grid.add(profilePasswordField, 1, 2);
//
//        dialog.getDialogPane().setContent(grid);
//
//        // Enable/Disable OK button based on input
//        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
//        okButton.setDisable(true);
//
//        // Validate input
//        profileNameField.textProperty().addListener((observable, oldValue, newValue) -> {
//            okButton.setDisable(newValue.trim().isEmpty() || profileUsernameField.getText().trim().isEmpty() || profilePasswordField.getText().trim().isEmpty());
//        });
//
//        profileUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
//            okButton.setDisable(newValue.trim().isEmpty() || profileNameField.getText().trim().isEmpty() || profilePasswordField.getText().trim().isEmpty());
//        });
//
//        profilePasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
//            okButton.setDisable(newValue.trim().isEmpty() || profileNameField.getText().trim().isEmpty() || profileUsernameField.getText().trim().isEmpty());
//        });
//
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == ButtonType.OK) {
//                String newProfileName = profileNameField.getText().trim();
//                String newProfileUsername = profileUsernameField.getText().trim();
//                String newProfilePassword = profilePasswordField.getText().trim();
//
//                // Update the profile object and database
//                boolean success = dbConnector.updateProfile(currentUsername, profile.getId(), newProfileName, newProfileUsername, newProfilePassword);
//                if (success) {
//                    profile.setProfileName(newProfileName);
//                    profile.setProfileUsername(newProfileUsername);
//                    profile.setProfilePassword(newProfilePassword);
//                    profilesListView.refresh();
//                    showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile.");
//                }
//            }
//            return null;
//        });
//
//        dialog.showAndWait();
//    }
//
//    /**
//     * Handles the deletion of a profile after user confirmation.
//     *
//     * @param profile The Profile object to delete.
//     */
//    private void handleDeleteProfile(Profile profile) {
//        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmationAlert.setTitle("Delete Confirmation");
//        confirmationAlert.setHeaderText(null);
//        confirmationAlert.setContentText("Are you sure you want to delete the profile '" + profile.getProfileName() + "'?");
//
//        Optional<ButtonType> result = confirmationAlert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            boolean success = dbConnector.deleteProfile(currentUsername, profile.getId());
//            if (success) {
//                loadProfiles();
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile deleted successfully.");
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete profile.");
//            }
//        }
//    }
//
//    /**
//     * Utility method to display alert dialogs.
//     *
//     * @param alertType The type of alert.
//     * @param title     The title of the alert window.
//     * @param message   The content message of the alert.
//     */
//    private void showAlert(Alert.AlertType alertType, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    /**
//     * Navigates back to the login page.
//     */
//    private void navigateToLoginPage() {
//        try {
//            URL loginPageUrl = getClass().getResource("/com/example/app1/login.fxml");
//            if (loginPageUrl == null) {
//                throw new IOException("FXML file for Login Page not found.");
//            }
//            FXMLLoader loader = new FXMLLoader(loginPageUrl);
//            Parent root = loader.load();
//            Stage stage = (Stage) serviceNameLabel.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Password Manager - Login");
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to navigate to login page.");
//        }
//    }
//
//    /**
//     * Handles the action when the Instagram button is clicked.
//     *
//     * @param event The ActionEvent triggered by clicking the Instagram button.
//     */
//    @FXML
//    private void InstagramButton_onAction(ActionEvent event) {
//        navigateToServicePage("Instagram");
//    }
//
//    /**
//     * Handles the action when the Codeforces button is clicked.
//     *
//     * @param event The ActionEvent triggered by clicking the Codeforces button.
//     */
//    @FXML
//    private void CodeforcesButton_onAction(ActionEvent event) {
//        navigateToServicePage("Codeforces");
//    }
//
//    /**
//     * Handles the action when the Gmail button is clicked.
//     *
//     * @param event The ActionEvent triggered by clicking the Gmail button.
//     */
//    @FXML
//    private void GmailButtton_onAction(ActionEvent event) {
//        navigateToServicePage("Gmail");
//    }
//
//    /**
//     * Handles the action when the LinkedIn button is clicked.
//     *
//     * @param event The ActionEvent triggered by clicking the LinkedIn button.
//     */
//    @FXML
//    private void LinkedInButton_onAction(ActionEvent event) {
//        navigateToServicePage("LinkedIn");
//    }
//
//    /**
//     * Handles the action when the Facebook button is clicked.
//     *
//     * @param event The ActionEvent triggered by clicking the Facebook button.
//     */
//    @FXML
//    private void fb_button_onAction(ActionEvent event) {
//        navigateToServicePage("Facebook");
//    }
//
//    /**
//     * Handles the action when the Add New Folder button is clicked.
//     *
//     * @param event The ActionEvent triggered by clicking the Add New Folder button.
//     */
//    @FXML
//    private void addNewFolder(ActionEvent event) {
//        // Implement the logic to add a new folder
//        // This could involve opening a dialog to enter folder details
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Add New Folder");
//        dialog.setHeaderText(null);
//        dialog.setContentText("Enter folder name:");
//
//        Optional<String> folderName = dialog.showAndWait();
//        folderName.ifPresent(name -> {
//            if (!name.trim().isEmpty()) {
//                // Implement folder creation logic
//                // For example, you might add it to the database or update the UI
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Folder '" + name.trim() + "' added successfully.");
//            } else {
//                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Folder name cannot be empty.");
//            }
//        });
//    }
//
//    /**
//     * Navigates to a specific service's page, displaying profiles related to that service.
//     *
//     * @param serviceName The name of the service to navigate to.
//     */
//    private void navigateToServicePage(String serviceName) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app1/ServicePage.fxml"));
//            Parent root = loader.load();
//            ServicePageController controller = loader.getController();
//            controller.setServiceName(serviceName);
//            controller.setCurrentUsername(currentUsername);
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Profiles - " + serviceName);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.showAndWait();
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load service page for " + serviceName + ".");
//        }
//    }
//
}
