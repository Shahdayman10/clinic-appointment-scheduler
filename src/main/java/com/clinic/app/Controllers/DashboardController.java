package com.clinic.app.Controllers;
import com.clinic.app.App;
import com.clinic.app.Models.User;
import com.clinic.app.data.services.PatientService;
import com.clinic.app.data.services.DoctorService;
import com.clinic.app.data.services.AppointmentService;
import com.clinic.app.utils.SimpleIconUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.Node;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private HBox topBar;
    @FXML
    private HBox cardsRow;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label patientsCount;
    @FXML
    private Label doctorsCount;
    @FXML
    private Label appointmentsCount;
    @FXML
    private Label scheduleCount;  // Add this line
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnPatients;
    @FXML
    private Button btnDoctors;
    @FXML
    private Button btnAppointments;
    @FXML
    private Button btnSchedule;
    @FXML
    private ImageView patientsCardIcon;
    @FXML
    private ImageView doctorsCardIcon;
    @FXML
    private ImageView appointmentsCardIcon;
    @FXML
    private ImageView scheduleCardIcon;

    private final List<Button> navButtons = new ArrayList<>();
    private String currentUserName;
    private User currentUser;
    // Service instances for database operations
    private final PatientService patientService = PatientService.getInstance();
    private final DoctorService doctorService = DoctorService.getInstance();
    private final AppointmentService appointmentService = AppointmentService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DashboardController.initialize() called");
        addIfNotNull(navButtons, btnDashboard, btnPatients, btnDoctors, btnAppointments, btnSchedule);

        // Debug output to check if buttons are properly injected
        System.out.println("btnDashboard: " + (btnDashboard != null));
        System.out.println("btnPatients: " + (btnPatients != null));
        System.out.println("btnDoctors: " + (btnDoctors != null));
        System.out.println("btnAppointments: " + (btnAppointments != null));
        System.out.println("btnSchedule: " + (btnSchedule != null));
        System.out.println("patientsCardIcon: " + (patientsCardIcon != null));
        System.out.println("doctorsCardIcon: " + (doctorsCardIcon != null));
        System.out.println("appointmentsCardIcon: " + (appointmentsCardIcon != null));
        System.out.println("scheduleCardIcon: " + (scheduleCardIcon != null));

        setupCardsRow();
        setupButtonHoverEffects();
        setupNavigationIcons();
        setupCardIcons();
        if (!navButtons.isEmpty()) {
            setActiveButton(navButtons.get(0));
        }
        initializeDashboardData();
    }
    /**
     * Set the current logged-in user
     * @param user Current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.currentUserName = user.getFullName();
        updateWelcomeLabel();
        refreshDashboardData();
    }
    public void setUserEmail(String name) {
        this.currentUserName = name;
        updateWelcomeLabel();
    }
    private void updateWelcomeLabel() {
        if (welcomeLabel != null) {
            if (currentUserName != null && !currentUserName.isEmpty()) {
                welcomeLabel.setText("Welcome back, " + currentUserName + "!");
            } else {
                welcomeLabel.setText("Welcome back!");
            }
        }
    }
    private static void addIfNotNull(List<Button> list, Button... buttons) {
        for (Button b : buttons) {
            if (b != null) {
                list.add(b);
            }
        }
    }
    private void initializeDashboardData() {
        refreshDashboardData();
        updateWelcomeLabel();
    }
    private void setupCardsRow() {
        if (cardsRow != null) {
            for (Node n : cardsRow.getChildren()) {
                HBox.setHgrow(n, Priority.ALWAYS);
                if (n instanceof Region r) {
                    r.setMaxWidth(Double.MAX_VALUE);
                    r.setPrefWidth(260);
                }
            }
        }
    }
    private void setupButtonHoverEffects() {
        // Removed inline styling to allow CSS to handle hover effects
    }
    private void setActiveButton(Button activeBtn) {
        System.out.println("DashboardController.setActiveButton called with: " + (activeBtn != null ? activeBtn.getText() : "null"));
        for (Button b : navButtons) {
            b.getStyleClass().remove("active");
        }
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("active");
            System.out.println("Set '" + activeBtn.getText() + "' as active in DashboardController");
        }

        // Update main container accent class
        if (cardsRow != null && cardsRow.getScene() != null) {
            Node root = cardsRow.getScene().getRoot();
            if (root instanceof HBox) {
                HBox mainContainer = (HBox) root;
                // Remove all accent classes first
                mainContainer.getStyleClass().removeAll("dashboard-accent", "patients-accent", "doctors-accent", "appointments-accent", "schedule-accent");

                // Add the appropriate accent class based on the active button
                if (activeBtn == btnDashboard) {
                    mainContainer.getStyleClass().add("dashboard-accent");
                } else if (activeBtn == btnPatients) {
                    mainContainer.getStyleClass().add("patients-accent");
                } else if (activeBtn == btnDoctors) {
                    mainContainer.getStyleClass().add("doctors-accent");
                } else if (activeBtn == btnAppointments) {
                    mainContainer.getStyleClass().add("appointments-accent");
                } else if (activeBtn == btnSchedule) {
                    mainContainer.getStyleClass().add("schedule-accent");
                } else {
                    // Default to dashboard accent
                    mainContainer.getStyleClass().add("dashboard-accent");
                }
            }
        }
    }
    @FXML
    private void showDashboard() {
        setActiveButton(btnDashboard);
        App.showDashboard();
    }

    @FXML
    private void showPatients() {
        try {
            setActiveButton(btnPatients);
            System.out.println("Attempting to navigate to Patients page...");
            App.showPatients();
            System.out.println("Successfully navigated to Patients page.");
        } catch (Exception e) {
            System.err.println("Error navigating to Patients page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showDoctors() {
        setActiveButton(btnDoctors);
        App.showDoctors();
    }

    @FXML
    private void showAppointments() {
        setActiveButton(btnAppointments);
        App.showAppointments();
    }


    @FXML
    private void showSchedule() {
        setActiveButton(btnSchedule);
        App.showSchedule();
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        Object src = event.getSource();
        if (!(src instanceof VBox card)) {
            return;
        }
        Object key = card.getUserData();
        if (!(key instanceof String k)) {
            return;
        }
        switch (k) {
            case "PATIENTS" -> showPatients();
            case "DOCTORS" -> showDoctors();
            case "APPOINTMENTS" -> showAppointments();
            case "SCHEDULE" -> showSchedule();
        }
    }
    @FXML
    private void exitApp() {
        if (topBar != null && topBar.getScene() != null) {
            topBar.getScene().getWindow().hide();
        }
        Platform.exit();
        new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
            System.exit(0);
        }).start();
    }
    public void refreshDashboardData() {
        try {
            int patients = patientService.getAllPatients().size();
            int doctors = doctorService.getAllDoctors().size();
            int appointments = appointmentService.getAllAppointments().size();
            // For schedule count, we'll show the number of doctors as it represents scheduling opportunities
            int schedules = doctors;
            Platform.runLater(() -> {
                if (patientsCount != null) patientsCount.setText(String.valueOf(patients));
                if (doctorsCount != null) doctorsCount.setText(String.valueOf(doctors));
                if (appointmentsCount != null) appointmentsCount.setText(String.valueOf(appointments));
                if (scheduleCount != null) scheduleCount.setText(String.valueOf(schedules));  // Add this line
            });
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard data: " + e.getMessage());
            e.printStackTrace();
            // Fallback to default values
            Platform.runLater(() -> {
                if (patientsCount != null) patientsCount.setText("0");
                if (doctorsCount != null) doctorsCount.setText("0");
                if (appointmentsCount != null) appointmentsCount.setText("0");
                if (scheduleCount != null) scheduleCount.setText("0");  // Add this line
            });
        }
        updateWelcomeLabel();
    }

    private void setupNavigationIcons() {
        System.out.println("Setting up navigation icons...");
        if (btnDashboard != null) {
            var icon = SimpleIconUtil.createNavigationIcon("dashboard", 24);
            icon.setOpacity(0.95);
            btnDashboard.setGraphic(icon);
            System.out.println("Dashboard icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
        if (btnPatients != null) {
            var icon = SimpleIconUtil.createNavigationIcon("patient", 24);
            icon.setOpacity(0.95);
            btnPatients.setGraphic(icon);
            System.out.println("Patients icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
        if (btnDoctors != null) {
            var icon = SimpleIconUtil.createNavigationIcon("doctor_stethoscope", 24);
            icon.setOpacity(0.95);
            btnDoctors.setGraphic(icon);
            System.out.println("Doctors icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
        if (btnAppointments != null) {
            var icon = SimpleIconUtil.createNavigationIcon("appointments", 24);
            icon.setOpacity(0.95);
            btnAppointments.setGraphic(icon);
            System.out.println("Appointments icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
        if (btnSchedule != null) {
            var icon = SimpleIconUtil.createNavigationIcon("schedule", 24); // Changed from "calendar" to "schedule"
            icon.setOpacity(0.95);
            btnSchedule.setGraphic(icon);
            System.out.println("Schedule icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
    }

    private void setupCardIcons() {
        System.out.println("Setting up card icons...");
        if (patientsCardIcon != null) {
            var icon = SimpleIconUtil.createCardIcon("patient", 48);
            icon.setOpacity(0.95);
            patientsCardIcon.setImage(icon.getImage());
            System.out.println("Patients card icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
        if (doctorsCardIcon != null) {
            var icon = SimpleIconUtil.createCardIcon("doctor_stethoscope", 48);
            icon.setOpacity(0.95);
            doctorsCardIcon.setImage(icon.getImage());
            System.out.println("Doctors card icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
        if (appointmentsCardIcon != null) {
            var icon = SimpleIconUtil.createCardIcon("appointments", 48);
            icon.setOpacity(0.95);
            appointmentsCardIcon.setImage(icon.getImage());
            System.out.println("Appointments card icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
        if (scheduleCardIcon != null) {
            var icon = SimpleIconUtil.createCardIcon("schedule", 48); // Changed from "calendar" to "schedule"
            icon.setOpacity(0.95);
            scheduleCardIcon.setImage(icon.getImage());
            System.out.println("Schedule card icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
    }
}