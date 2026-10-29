package com.clinic.app.Controllers;
import com.clinic.app.Models.User;
import com.clinic.app.data.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
public class SignUpController implements Initializable {
    @FXML private StackPane root;
    @FXML private ImageView backgroundImage;
    @FXML private TextField firstNameField;
    @FXML private TextField lastnameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signupButton;
    @FXML private Hyperlink loginLink;
    private final UserService userService = UserService.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ضبط حجم الخلفية
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());
        Rectangle overlay = (Rectangle) root.lookup("#overlay");
        if (overlay != null) {
            overlay.widthProperty().bind(root.widthProperty());
            overlay.heightProperty().bind(root.heightProperty());
        }

        // Remove icon from signup button (minimalist UI preference)
        // setupSignupButtonIcon(); // This line is commented out to remove icon from signup button

        // تهيئة الأزرار
        signupButton.setOnAction(e -> handleSignUp());
        loginLink.setOnAction(e -> openLoginPage());

        // If database is not available, show a message
        if (!userService.isDatabaseAvailable()) {
            showInfo("Database not available. Using mock data for demonstration.\n\nYou can register new users, but data will not be persisted.");
        }
    }

    // Comment out or remove the setupSignupButtonIcon method to maintain minimalist UI
    /*
    private void setupSignupButtonIcon() {
        if (signupButton != null) {
            System.out.println("Setting up signup button icon...");
            // Use patient icon for signup button
            var icon = com.clinic.app.utils.SimpleIconUtil.createNavigationIcon("patient", 24);
            System.out.println("Signup button icon created, image is null: " + (icon.getImage() == null));
            if (icon.getImage() != null) {
                System.out.println("Signup button icon URL: " + icon.getImage().getUrl());
            }
            icon.setOpacity(0.95);
            signupButton.setGraphic(icon);
            System.out.println("Signup button icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        } else {
            System.out.println("Signup button is null, cannot set icon");
        }
    }
    */
    private void handleSignUp() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastnameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        // تحقق من أن جميع الحقول مملوءة
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorAlert("Please fill all fields / يرجى ملء جميع الحقول");
            return;
        }
        // تحقق من صيغة البريد الإلكتروني
        if (!isValidEmail(email)) {
            showErrorAlert("Invalid email format / صيغة البريد الإلكتروني غير صحيحة");
            return;
        }
        // تحقق من تطابق كلمات المرور
        if (!password.equals(confirmPassword)) {
            showErrorAlert("Passwords do not match / كلمات المرور غير متطابقة");
            return;
        }
        // تحقق من قوة كلمة المرور
        if (password.length() < 6) {
            showErrorAlert("Password must be at least 6 characters / كلمة المرور يجب أن تكون 6 أحرف على الأقل");
            return;
        }
        // Show loading state
        signupButton.setDisable(true);
        signupButton.setText("Creating Account...");
        try {
            // تسجيل المستخدم الجديد باستخدام UserService
            com.clinic.app.entities.User newUserEntity = userService.registerUser(firstName, lastName, email, password, "USER");
            if (newUserEntity != null) {
                showSuccessAlert("Account created successfully! You can now log in / تم إنشاء الحساب بنجاح! يمكنك الآن تسجيل الدخول");
                clearForm();
                openLoginPage();
            } else {
                showErrorAlert("Registration failed. Email may already be in use / فشل في إنشاء الحساب. البريد الإلكتروني قد يكون مستخدماً مسبقاً");
            }
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            showErrorAlert("An error occurred during registration. Please try again / حدث خطأ أثناء التسجيل. يرجى المحاولة مرة أخرى");
        } finally {
            // Reset button state
            signupButton.setDisable(false);
            signupButton.setText("Sign Up");
        }
    }
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    /**
     * مسح جميع الحقول
     */
    private void clearForm() {
        firstNameField.clear();
        lastnameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
    @FXML
    public void openLoginPage() {
        com.clinic.app.App.showLogin();
    }
    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Sign Up Error");
        alert.setContentText(msg);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    private void showSuccessAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
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