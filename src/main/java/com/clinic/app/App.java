package com.clinic.app;

import com.clinic.app.Models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;

public class App extends Application {
    private static Stage primaryStage;
    private static boolean isDashboardShown = false;
    private static User currentUser;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        showWelcome();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Get the primary stage of the application
     * @return the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Set the current logged-in user
     * @param user the current user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Get the current logged-in user
     * @return the current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    // صفحة الترحيب
    public static void showWelcome() {
        try {
            System.out.println("Attempting to load Welcome.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/Welcome.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find Welcome.fxml");
                showError("Could not find Welcome.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded Welcome.fxml");
            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Welcome");
            applyWindowSettings(scene, true); // Welcome page should be maximized per user preference
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load welcome page: " + e.getMessage());
        }
    }

    // صفحة تسجيل الدخول
    public static void showLogin() {
        try {
            System.out.println("Attempting to load Login.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/Login.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find Login.fxml");
                showError("Could not find Login.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded Login.fxml");
            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Login");
            applyWindowSettings(scene, true); // Login page should be maximized per user preference
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load login page: " + e.getMessage());
        }
    }

    // صفحة إنشاء حساب
    public static void showSignup() {
        try {
            System.out.println("Attempting to load SignUp.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/SignUp.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find SignUp.fxml");
                showError("Could not find SignUp.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded SignUp.fxml");
            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Sign Up");
            applyWindowSettings(scene, true); // Signup page should be maximized per user preference
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load signup page: " + e.getMessage());
        }
    }

    // Dashboard الرئيسي
    public static void showDashboard() {
        try {
            System.out.println("Attempting to load Dashboard.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/Dashboard.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find Dashboard.fxml");
                showError("Could not find Dashboard.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded Dashboard.fxml");

            // Get the controller and set the current user if available
            Object controller = loader.getController();
            if (controller instanceof com.clinic.app.Controllers.DashboardController dashboardController) {
                if (currentUser != null) {
                    dashboardController.setCurrentUser(currentUser);
                    System.out.println("Set current user in dashboard controller: " + currentUser.getFullName());
                } else {
                    System.out.println("No current user to set in dashboard controller");
                }
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Dashboard");
            applyWindowSettings(scene, true); // Dashboard should be maximized
            isDashboardShown = true;
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard: " + e.getMessage());
        }
    }

    // صفحة المرضى
    public static void showPatients() {
        try {
            System.out.println("Attempting to load Patient.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/Patient.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find Patient.fxml");
                showError("Could not find Patient.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded Patient.fxml");
            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Patients");
            applyWindowSettings(scene, true); // Set to true to maximize patients page
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load patients page: " + e.getMessage());
        }
    }

    // صفحة الأطباء
    public static void showDoctors() {
        try {
            System.out.println("Attempting to load Doctor.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/Doctor.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find Doctor.fxml");
                showError("Could not find Doctor.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded Doctor.fxml");
            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Doctors");
            applyWindowSettings(scene, true); // Set to true to maximize doctors page
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load doctors page: " + e.getMessage());
        }
    }

    // صفحة المواعيد
    public static void showAppointments() {
        try {
            System.out.println("App.showAppointments() called");
            System.out.println("Attempting to load Appointment.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/Appointment.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find Appointment.fxml");
                showError("Could not find Appointment.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded Appointment.fxml");
            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Appointments");
            applyWindowSettings(scene, true); // Set to true to maximize appointments page
            System.out.println("Appointments page displayed successfully");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load appointments page: " + e.getMessage());
        }
    }

    // صفحة الجدول
    public static void showSchedule() {
        try {
            System.out.println("App.showSchedule() called");
            System.out.println("Attempting to load Schedule.fxml");
            URL fxmlUrl = App.class.getResource("/fxml/Schedule.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("Could not find Schedule.fxml");
                showError("Could not find Schedule.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("Successfully loaded Schedule.fxml");
            Scene scene = new Scene(root);
            primaryStage.setTitle("Clinic Management System - Doctor Schedule");
            applyWindowSettings(scene, true); // Set to true to maximize schedule page
            System.out.println("Schedule page displayed successfully");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load schedule page: " + e.getMessage());
        }
    }

    private static void showError(String message) {
        // Run on JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Application Error");
            alert.setContentText(message);
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        });
    }

    // تطبيق إعدادات النافذة الموحدة لجميع الصفحات
    private static void applyWindowSettings(Scene scene, boolean maximize) {
        primaryStage.setScene(scene);

        // Maximize window for all pages
        if (maximize) {
            // Force the window to maximize properly
            primaryStage.setMaximized(false); // First set to false to ensure state change
            primaryStage.centerOnScreen();
            primaryStage.setMaximized(true);  // Then set to true

            // Additional settings for Windows compatibility
            primaryStage.setFullScreen(false); // Ensure fullscreen is off
        } else {
            // This branch should not be reached with our current implementation
            primaryStage.setMaximized(false);
            primaryStage.setFullScreen(false);
            primaryStage.centerOnScreen();
        }

        primaryStage.show();
    }
}