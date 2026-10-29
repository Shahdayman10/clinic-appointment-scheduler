package com.clinic.app.Controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController {
    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;
    @FXML
    private ImageView welcomeIcon;

    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("WelcomeController.initialize() called");
        System.out.println("Login button: " + (loginButton != null));
        System.out.println("Signup button: " + (signupButton != null));
        System.out.println("Welcome icon: " + (welcomeIcon != null));
        setupButtonHoverEffects();
        setupWelcomeIcon();
    }

    private void setupWelcomeIcon() {
        if (welcomeIcon != null) {
            System.out.println("Setting up welcome icon...");
            welcomeIcon.setOpacity(0.95);
            System.out.println("Welcome icon setup complete");
        } else {
            System.out.println("Welcome icon is null");
        }
    }

    private void setupButtonHoverEffects() {
        if (loginButton != null) {
            loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #64b5f6, #42a5f5, #2196f3);"));
            loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #42a5f5, #2196f3, #1e88e5);"));
        }
        if (signupButton != null) {
            signupButton.setOnMouseEntered(e -> signupButton.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #64b5f6, #42a5f5, #2196f3);"));
            signupButton.setOnMouseExited(e -> signupButton.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #42a5f5, #2196f3, #1e88e5);"));
        }
    }

    @FXML
    public void handleLogin() {
        System.out.println("handleLogin() called");
        try {
            com.clinic.app.App.showLogin();
            System.out.println("Successfully called App.showLogin()");
        } catch (Exception e) {
            System.err.println("Failed to open login page: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to open login page: " + e.getMessage());
        }
    }

    @FXML
    public void handleSignUp() {
        System.out.println("handleSignUp() called");
        try {
            com.clinic.app.App.showSignup();
            System.out.println("Successfully called App.showSignup()");
        } catch (Exception e) {
            System.err.println("Failed to open signup page: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to open signup page: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Navigation Error");
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
}