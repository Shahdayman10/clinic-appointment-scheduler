package com.clinic.app.Controllers;

import com.clinic.app.Models.User;
import com.clinic.app.data.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class LoginController implements Initializable {

    @FXML
    private StackPane root;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private ImageView profileIcon;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink signupLink;
    @FXML
    private Hyperlink forgotLink;
    private UserService userService = UserService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Debug: Check if profileIcon is loaded
        if (profileIcon != null) {
            System.out.println("Profile icon loaded successfully");
            Image image = profileIcon.getImage();
            if (image != null) {
                System.out.println("Profile icon image loaded: " + image.getUrl());
            } else {
                System.out.println("Profile icon image is null, trying to load user icon dynamically");
                // Try to load the user icon dynamically using SimpleIconUtil
                try {
                    ImageView dynamicIcon = com.clinic.app.utils.SimpleIconUtil.createIcon("user", 100);
                    if (dynamicIcon.getImage() != null && !dynamicIcon.getImage().isError()) {
                        profileIcon.setImage(dynamicIcon.getImage());
                        System.out.println("Successfully loaded user icon dynamically");
                    } else {
                        System.out.println("Failed to load user icon dynamically");
                        // Fallback to patient icon if user icon fails
                        ImageView fallbackIcon = com.clinic.app.utils.SimpleIconUtil.createIcon("patient", 100);
                        if (fallbackIcon.getImage() != null && !fallbackIcon.getImage().isError()) {
                            profileIcon.setImage(fallbackIcon.getImage());
                            System.out.println("Successfully loaded fallback patient icon");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Exception while loading user icon dynamically: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Profile icon is null");
        }

        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());
        Rectangle overlay = (Rectangle) root.lookup("#overlay");
        if (overlay != null) {
            overlay.widthProperty().bind(root.widthProperty());
            overlay.heightProperty().bind(root.heightProperty());
        }

        // Remove the icon from login button
        // setupLoginButtonIcon(); // This line is commented out to remove icon from login button

        loginButton.setOnAction(e -> handleLogin());
        signupLink.setOnAction(e -> openSignUpPage());
        forgotLink.setOnAction(e -> showInfo("Password reset instructions sent to your email."));

        // If database is not available, show a message
        if (!userService.isDatabaseAvailable()) {
            showInfo("Database not available. Using mock data for demonstration.\n\nDefault accounts:\nadmin@clinic.com / admin123\ntest@clinic.com / test123");
        }
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        // Show loading indicator
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");
        try {
            Optional<com.clinic.app.entities.User> userOpt = userService.authenticateUser(email, password);
            if (userOpt.isPresent()) {
                com.clinic.app.entities.User userEntity = userOpt.get();
                // Convert entity to model
                com.clinic.app.Models.User userModel = new com.clinic.app.Models.User(
                        userEntity.getId(),
                        userEntity.getFirstName(),
                        userEntity.getLastName(),
                        userEntity.getEmail(),
                        userEntity.getPasswordHash(),
                        userEntity.getRole(),
                        userEntity.isActive(),
                        userEntity.getCreatedAt(),
                        userEntity.getUpdatedAt()
                );

                // Set the current user in the App class
                com.clinic.app.App.setCurrentUser(userModel);

                // Navigate to dashboard
                navigateToDashboard();
            } else {
                showError("Invalid email or password.");
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            showError("An error occurred during login. Please try again.");
        } finally {
            // Reset login button
            loginButton.setDisable(false);
            loginButton.setText("Login");
        }
    }

    private void navigateToDashboard() {
        try {
            // استخدام طريقة موحدة لإدارة النوافذ من خلال App class
            com.clinic.app.App.showDashboard();
            System.out.println("Successfully navigated to dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Cannot open dashboard. Please contact support.");
        }
    }

    private String extractFirstName(String email) {
        if (email == null || email.isEmpty()) {
            return "User";
        }
        String namePart = email.split("@")[0];
        return capitalize(namePart);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @FXML
    public void openSignUpPage() {
        com.clinic.app.App.showSignup();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Login Error");
        alert.setContentText(msg);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
}