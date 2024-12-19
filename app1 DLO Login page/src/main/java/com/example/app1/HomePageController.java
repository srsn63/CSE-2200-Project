package com.example.app1;


import com.google.gson.*;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Callback;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class HomePageController {

    @FXML
    private Button LogOutButton;
    @FXML
    private Button fb_button;
    @FXML
    private Button LinkedInButton;
    @FXML
    private Button GmailButtton;
    @FXML
    private Button CodeforcesButton;
    @FXML
    private Button InstagramButton;
    @FXML
    private Button add_new_folder_button1;
    @FXML
    private Button ProfileButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox servicesVBox; // Container for all service buttons

    @FXML
    private ScrollPane serviceScrollPane; // ScrollPane containing servicesVBox

    private String currentUsername; // Set via setCurrentUsername method
    private MySQLConnector dbConnector;

    // Define styles: normal and bold
    private static final String NORMAL_STYLE = "-fx-background-color: #607d8b; -fx-text-fill: white; -fx-font-weight: normal;";
    private static final String BOLD_STYLE = "-fx-background-color: #607d8b; -fx-text-fill: white; -fx-font-weight: bold;";

    @FXML
    private void initialize() {
        dbConnector = new MySQLConnector();

        try (Connection conn = MySQLConnector.getConnection()) {
            MySQLConnector.initializeDatabase(conn);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to initialize database.");
        }

    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadUserFolders();
    }

    private void loadUserFolders() {
        if (currentUsername != null && !currentUsername.isEmpty()) {
            List<String> userFolders = dbConnector.getUserFolders(currentUsername);
            for (String folderName : userFolders) {
                Button folderButton = createServiceButton(folderName);
                servicesVBox.getChildren().add(servicesVBox.getChildren().size() - 1, folderButton);
            }
        }
    }

    @FXML
    private void addNewFolder(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Folder");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter folder name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(originalFolderName -> {
            String folderName = originalFolderName.trim();
            if (!folderName.isEmpty()) {
                if (isFolderNameExists(folderName)) {
                    showAlert(Alert.AlertType.WARNING, "Duplicate Folder", "A folder with this name already exists.");
                } else {
                    boolean isAdded = dbConnector.addUserFolder(currentUsername, folderName);
                    if (isAdded) {
                        Button newFolderButton = createServiceButton(folderName);
                        servicesVBox.getChildren().add(servicesVBox.getChildren().size() - 1, newFolderButton);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "New folder added successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add new folder.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Folder name cannot be empty.");
            }
        });
    }

    private boolean isFolderNameExists(String folderName) {
        for (Node node : servicesVBox.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (btn.getText().equalsIgnoreCase(folderName)) {
                    return true;
                }
            }
        }
        return dbConnector.isFolderNameExists(currentUsername, folderName);
    }

    private Button createServiceButton(String serviceName) {
        final String finalServiceName = serviceName;
        Button serviceButton = new Button(finalServiceName);
        // Set initial style to normal or bold as you prefer. We assume normal for differentiation.
        serviceButton.setStyle(NORMAL_STYLE);
        serviceButton.setPrefHeight(62.0);
        serviceButton.setPrefWidth(200.0);

        serviceButton.setOnAction(e -> handleServiceButtonAction(finalServiceName));
        return serviceButton;
    }

    @FXML
    private void fb_button_onAction(ActionEvent event) {
        handleServiceButtonAction("Facebook");
    }

    @FXML
    private void InstagramButton_onAction(ActionEvent event) {
        handleServiceButtonAction("Instagram");
    }

    @FXML
    private void CodeforcesButton_onAction(ActionEvent event) {
        handleServiceButtonAction("Codeforces");
    }

    @FXML
    private void LinkedInButton_onAction(ActionEvent event) {
        handleServiceButtonAction("LinkedIn");
    }

    @FXML
    private void GmailButtton_onAction(ActionEvent event) {
        handleServiceButtonAction("Gmail");
    }

    @FXML
    private void searchFieldonAction() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Search", "Please enter a search term.");
            return;
        }

        boolean matchFound = false;
        Button firstMatchedButton = null;

        // Reset all buttons to normal style
        for (Node node : servicesVBox.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setStyle(NORMAL_STYLE);
            }
        }

        // Search for matches (case-insensitive)
        for (Node node : servicesVBox.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                String buttonText = btn.getText().trim().toLowerCase();

                if (buttonText.contains(query)) {
                    // Highlight the matched button by making it bold
                    btn.setStyle(BOLD_STYLE);

                    if (!matchFound) {
                        firstMatchedButton = btn;
                        matchFound = true;
                    }
                }
            }
        }

        if (matchFound && firstMatchedButton != null) {
            // Scroll to bring the matched button into view if needed
            double buttonIndex = servicesVBox.getChildren().indexOf(firstMatchedButton);
            double buttonHeight = firstMatchedButton.getHeight() + servicesVBox.getSpacing();
            double totalHeight = servicesVBox.getHeight();
            double viewportHeight = serviceScrollPane.getViewportBounds().getHeight();

            double targetY = buttonIndex * buttonHeight;
            double vValue = targetY / (totalHeight - viewportHeight);
            vValue = Math.max(0, Math.min(vValue, 1));

            serviceScrollPane.setVvalue(vValue);
            firstMatchedButton.requestFocus();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Match Found", "No service matches your search query.");
        }
    }

    private void handleServiceButtonAction(String serviceName) {
        final String finalServiceName = serviceName;
        List<Profile> profiles = dbConnector.getProfiles(currentUsername, finalServiceName);

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(finalServiceName + " Profiles");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));

        ListView<Profile> listView = new ListView<>();
        ObservableList<Profile> observableProfiles = FXCollections.observableArrayList(profiles);
        listView.setItems(observableProfiles);

        listView.setCellFactory(new Callback<ListView<Profile>, ListCell<Profile>>() {
            @Override
            public ListCell<Profile> call(ListView<Profile> param) {
                return new ListCell<Profile>() {
                    @Override
                    protected void updateItem(Profile profile, boolean empty) {
                        super.updateItem(profile, empty);
                        if (empty || profile == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(10);
                            hBox.setAlignment(Pos.CENTER_LEFT);

                            Label nameLabel = new Label(profile.getProfileName());
                            nameLabel.setPrefWidth(150);

                            TextField passwordField = new TextField(profile.getProfilePassword());
                            passwordField.setPrefWidth(200);
                            passwordField.setEditable(false);

                            final Profile currentProfile = profile;
                            final ListView<Profile> currentListView = listView;

                            Button renameButton = new Button("Rename");
                            renameButton.setOnAction(e -> {
                                TextInputDialog renameDialog = new TextInputDialog(currentProfile.getProfileName());
                                renameDialog.setTitle("Rename Profile");
                                renameDialog.setHeaderText(null);
                                renameDialog.setContentText("Enter new profile name:");

                                Optional<String> newName = renameDialog.showAndWait();
                                newName.ifPresent(originalName -> {
                                    String trimmedName = originalName.trim();
                                    if (!trimmedName.isEmpty()) {
                                        currentProfile.setProfileName(trimmedName);
                                        boolean success = dbConnector.updateProfile(
                                                currentProfile.getId(),
                                                currentUsername,
                                                currentProfile.getProfileName(),
                                                currentProfile.getProfileUsername(),
                                                currentProfile.getProfilePassword()
                                        );
                                        if (success) {
                                            listView.refresh();
                                            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile renamed successfully.");
                                        } else {
                                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to rename profile.");
                                        }
                                    } else {
                                        showAlert(Alert.AlertType.WARNING, "Invalid Input", "Profile name cannot be empty.");
                                    }
                                });
                            });

                            Button viewEditButton = new Button("View/Edit");
                            viewEditButton.setOnAction(e -> {
                                showProfileDetailsDialog(currentProfile, currentListView);
                            });

                            Button deleteButton = new Button("Delete");
                            deleteButton.setOnAction(e -> {
                                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                                confirmationAlert.setTitle("Delete Confirmation");
                                confirmationAlert.setHeaderText(null);
                                confirmationAlert.setContentText("Are you sure you want to delete this profile?");

                                Optional<ButtonType> result = confirmationAlert.showAndWait();
                                if (result.isPresent() && result.get() == ButtonType.OK) {
                                    boolean success = dbConnector.deleteProfile(currentProfile.getId(), currentUsername);
                                    if (success) {
                                        observableProfiles.remove(currentProfile);
                                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile deleted successfully.");
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete profile.");
                                    }
                                }
                            });

                            hBox.getChildren().addAll(nameLabel, passwordField, renameButton, viewEditButton, deleteButton);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });

        dialogVBox.getChildren().add(listView);

        Button addProfileButton = new Button("Add New Profile");
        addProfileButton.setOnAction(e -> {
            TextInputDialog addDialog = new TextInputDialog();
            addDialog.setTitle("Add New " + finalServiceName + " Profile");
            addDialog.setHeaderText(null);
            addDialog.setContentText("Enter profile name:");

            Optional<String> profileName = addDialog.showAndWait();
            profileName.ifPresent(originalName -> {
                String trimmedName = originalName.trim();
                if (!trimmedName.isEmpty()) {
                    Dialog<Pair<String, String>> credentialsDialog = new Dialog<>();
                    credentialsDialog.setTitle("Profile Credentials");
                    credentialsDialog.setHeaderText("Enter Profile Username and Password:");

                    ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
                    credentialsDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    TextField profileUsernameField = new TextField();
                    profileUsernameField.setPromptText("Profile Username");
                    PasswordField profilePasswordField = new PasswordField();
                    profilePasswordField.setPromptText("Profile Password");

                    grid.add(new Label("Profile Username:"), 0, 0);
                    grid.add(profileUsernameField, 1, 0);
                    grid.add(new Label("Profile Password:"), 0, 1);
                    grid.add(profilePasswordField, 1, 1);

                    credentialsDialog.getDialogPane().setContent(grid);

                    Node addButton = credentialsDialog.getDialogPane().lookupButton(addButtonType);
                    addButton.setDisable(true);

                    profileUsernameField.textProperty().addListener((obs, oldVal, newVal) -> {
                        addButton.setDisable(newVal.trim().isEmpty() || profilePasswordField.getText().trim().isEmpty());
                    });

                    profilePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                        addButton.setDisable(newVal.trim().isEmpty() || profileUsernameField.getText().trim().isEmpty());
                    });

                    credentialsDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == addButtonType) {
                            return new Pair<>(profileUsernameField.getText(), profilePasswordField.getText());
                        }
                        return null;
                    });

                    Optional<Pair<String, String>> result = credentialsDialog.showAndWait();
                    result.ifPresent(credentials -> {
                        String profileUsername = credentials.getKey().trim();
                        String profilePassword = credentials.getValue().trim();
                        if (!profileUsername.isEmpty() && !profilePassword.isEmpty()) {
                            boolean success = dbConnector.addProfile(
                                    currentUsername,
                                    finalServiceName,
                                    trimmedName,
                                    profileUsername,
                                    profilePassword
                            );
                            if (success) {
                                List<Profile> updatedProfiles = dbConnector.getProfiles(currentUsername, finalServiceName);
                                observableProfiles.setAll(updatedProfiles);
                                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile added successfully.");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add profile.");
                            }
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Username and password cannot be empty.");
                        }
                    });
                } else {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input", "Profile name cannot be empty.");
                }
            });
        });

        addProfileButton.setPrefWidth(150.0);
        addProfileButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        dialogVBox.getChildren().add(addProfileButton);

        Scene dialogScene = new Scene(dialogVBox, 700, 500);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void showProfileDetailsDialog(Profile profile, ListView<Profile> listView) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Profile Details - " + profile.getProfileName());
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField profileNameField = new TextField(profile.getProfileName());
        TextField profileUsernameField = new TextField(profile.getProfileUsername());
        TextField profilePasswordField = new TextField(profile.getProfilePassword());

        grid.add(new Label("Profile Name:"), 0, 0);
        grid.add(profileNameField, 1, 0);
        grid.add(new Label("Profile Username:"), 0, 1);
        grid.add(profileUsernameField, 1, 1);
        grid.add(new Label("Profile Password:"), 0, 2);
        grid.add(profilePasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        profileNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty() || profileUsernameField.getText().trim().isEmpty() || profilePasswordField.getText().trim().isEmpty());
        });

        profileUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty() || profileNameField.getText().trim().isEmpty() || profilePasswordField.getText().trim().isEmpty());
        });

        profilePasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty() || profileNameField.getText().trim().isEmpty() || profileUsernameField.getText().trim().isEmpty());
        });

        final Profile finalProfile = profile;
        final ListView<Profile> finalListView = listView;

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String newProfileName = profileNameField.getText().trim();
                String newProfileUsername = profileUsernameField.getText().trim();
                String newProfilePassword = profilePasswordField.getText().trim();

                finalProfile.setProfileName(newProfileName);
                finalProfile.setProfileUsername(newProfileUsername);
                finalProfile.setProfilePassword(newProfilePassword);

                boolean success = dbConnector.updateProfile(
                        finalProfile.getId(),
                        currentUsername,
                        newProfileName,
                        newProfileUsername,
                        newProfilePassword
                );
                if (success) {
                    finalListView.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void openProfile(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/app1/profilepage.fxml"));
            Stage s1 = (Stage) ProfileButton.getScene().getWindow();
            s1.setScene((new Scene(root)));
        }
        catch (IOException ex)
        {
           ex.printStackTrace();
            System.out.println("profile page failed to load ");
        }
    }



    @FXML
    private void logOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage) LogOutButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Password Manager - Login");
    }

    @FXML
    private void handlePrivacyPolicy(ActionEvent event) {
        String url = "https://api.myjson.online/v1/records/e972f30d-e049-4386-b19d-67df646dbbdf";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonParse3(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleTermsOfService(ActionEvent event) {
        String url = "https://api.myjson.online/v1/records/08c56f85-481b-43ac-8ab4-27d713e659eb";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonParse2(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    private void handleContactUs(ActionEvent event) {

            String url = "https://api.myjson.online/v1/records/9834bae1-ef43-4ade-8fc4-c240664dcafa";
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                jsonParse(response.body());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
    }
    public  void jsonParse3(String response )
    {
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        JsonArray policyArray = dataObject.get("policies").getAsJsonArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < policyArray.size(); i++)
        {
            JsonObject policy = policyArray.get(i).getAsJsonObject();
            String st = policy.get("policy").getAsString();
            int k = i+1;
            builder.append("Policy "+k+" :- ").append(st).append("\n\n");
        }
        showParsedInfoInNewWindow(builder.toString());

    }

        public  void jsonParse2(String response ) {

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
JsonArray pointsArray = dataObject.get("points").getAsJsonArray();
StringBuilder builder = new StringBuilder();
for(int i =0;i<pointsArray.size();i++)
{
    JsonObject point = pointsArray.get(i).getAsJsonObject();
    String st = point.get("statement").getAsString();
    int k = i+1;
    builder.append("Term "+k+" :- ").append(st).append("\n\n");
}
        showParsedInfoInNewWindow(builder.toString());
         }





    public void jsonParse(String response) { // to print contact us
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

        // Access the "data" object
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();

        // Access the "users" array within "data"
        JsonArray usersArray = dataObject.get("users").getAsJsonArray();

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < usersArray.size(); i++) {
            JsonObject user = usersArray.get(i).getAsJsonObject();

            // Extract basic user info
            int id = user.get("id").getAsInt();
            String name = user.get("name").getAsString();

            // Extract address info
            JsonObject address = user.get("address").getAsJsonObject();
            String city = address.get("city").getAsString();
            String street = address.get("street").getAsString();
            String zipCode = address.get("zipCode").getAsString();
            String landmark = address.has("landmark") ? address.get("landmark").getAsString() : "N/A";

            // Extract coordinates
            JsonObject coordinates = address.get("coordinates").getAsJsonObject();
            double latitude = coordinates.get("latitude").getAsDouble();
            double longitude = coordinates.get("longitude").getAsDouble();

            // Extract contact info
            JsonObject contact = user.get("contact").getAsJsonObject();
            String email = contact.get("email").getAsString();
            String phone = contact.get("phone").getAsString();

            // Build the output string for each user
            stringBuilder.append("ID: ").append(id).append("\n")
                    .append("Name: ").append(name).append("\n")
                    .append("Address: ").append(street).append(", ")
                    .append(city).append(" (ZIP: ").append(zipCode).append(")\n")
                    .append("Landmark: ").append(landmark).append("\n")
                    .append("Coordinates: ").append(latitude).append(", ").append(longitude).append("\n")
                    .append("Contact Email: ").append(email).append("\n")
                    .append("Contact Phone: ").append(phone).append("\n\n");
        }

        // Display the parsed information
        showParsedInfoInNewWindow(stringBuilder.toString());
    }
    private void showParsedInfoInNewWindow(String info) {
        // Create a TextArea for displaying parsed info
        TextArea textArea = new TextArea(info);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Apply some styling to the TextArea
        textArea.setStyle("-fx-control-inner-background: #f0f8ff; " +
                "-fx-font-family: 'Arial'; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #333333;");

        // Create a VBox as the root layout with background color and padding
        VBox root = new VBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2); " +
                "-fx-padding: 20px;");
        root.getChildren().add(textArea);

        // Optionally, set some window dimensions
        textArea.setPrefSize(400, 300);

        // Create and set the scene
        Scene scene = new Scene(root);
        Stage newWindow = new Stage();
        newWindow.setTitle("Parsed Information");
        newWindow.setScene(scene);

        // Show the new styled window
        newWindow.show();
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        } else {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}
