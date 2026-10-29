package com.clinic.app.Controllers;

import com.clinic.app.App;
import com.clinic.app.data.services.DoctorService;
import com.clinic.app.data.services.AppointmentService;
import com.clinic.app.entities.Doctor;
import com.clinic.app.entities.Appointment;
import com.clinic.app.utils.SimpleIconUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ArrayList;

public class ScheduleController implements Initializable {
    @FXML
    private HBox topBar;

    @FXML
    private VBox sidebar;

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
    private ComboBox<String> doctorComboBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Button generateButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea scheduleTextArea;

    @FXML
    private HBox mainContainer;

    private DoctorService doctorService;
    private AppointmentService appointmentService;
    private Task<String> scheduleTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doctorService = DoctorService.getInstance();
        appointmentService = AppointmentService.getInstance();

        setupDatePickers();
        loadDoctors();
        setupNavigationIcons();
        setActiveButton(btnSchedule);

        // Clear the schedule text area to ensure it's empty by default
        if (scheduleTextArea != null) {
            scheduleTextArea.clear();
        }
    }

    private void setupDatePickers() {
        // Leave date pickers empty by default as requested by the user
        // Do not set any default dates
        if (fromDatePicker != null) {
            fromDatePicker.setValue(null);
        }
        if (toDatePicker != null) {
            toDatePicker.setValue(null);
        }
    }

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            doctorComboBox.getItems().clear();
            for (Doctor doctor : doctors) {
                doctorComboBox.getItems().add(doctor.getName() + " - " + doctor.getSpecialty());
            }
            if (!doctorComboBox.getItems().isEmpty()) {
                doctorComboBox.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load doctors: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupNavigationIcons() {
        System.out.println("Setting up navigation icons for Schedule page...");
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
            var icon = SimpleIconUtil.createNavigationIcon("schedule", 24);
            icon.setOpacity(0.95);
            btnSchedule.setGraphic(icon);
            System.out.println("Schedule icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
    }

    private void setActiveButton(Button activeBtn) {
        System.out.println("setActiveButton called with: " + (activeBtn != null ? activeBtn.getText() : "null"));
        List<Button> navButtons = new ArrayList<>();
        navButtons.add(btnDashboard);
        navButtons.add(btnPatients);
        navButtons.add(btnDoctors);
        navButtons.add(btnAppointments);
        navButtons.add(btnSchedule);

        // Print debug info
        System.out.println("Nav buttons status:");
        for (Button b : navButtons) {
            if (b != null) {
                System.out.println("  " + b.getText() + " - has 'active' class: " + b.getStyleClass().contains("active"));
            }
        }

        // Remove active class from all buttons first
        navButtons.forEach(b -> {
            if (b != null) {
                b.getStyleClass().remove("active");
            }
        });

        // Add active class to the specified button
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("active");
            System.out.println("Set '" + activeBtn.getText() + "' as active");
        }

        // Update main container accent class
        if (mainContainer != null) {
            // Remove all accent classes first
            mainContainer.getStyleClass().removeAll("dashboard-accent", "patients-accent", "doctors-accent", "appointments-accent", "schedule-accent");
            // Add the appropriate accent class
            mainContainer.getStyleClass().add("schedule-accent");
        }
    }

    @FXML
    private void showDashboard() {
        System.out.println("showDashboard() called");
        setActiveButton(btnDashboard);
        System.out.println("Calling App.showDashboard()");
        App.showDashboard();
    }

    @FXML
    private void showPatients() {
        System.out.println("showPatients() called");
        setActiveButton(btnPatients);
        System.out.println("Calling App.showPatients()");
        App.showPatients();
    }

    @FXML
    private void showDoctors() {
        System.out.println("showDoctors() called");
        setActiveButton(btnDoctors);
        System.out.println("Calling App.showDoctors()");
        App.showDoctors();
    }

    @FXML
    private void showAppointments() {
        System.out.println("showAppointments() called");
        setActiveButton(btnAppointments);
        System.out.println("Calling App.showAppointments()");
        App.showAppointments();
    }

    @FXML
    private void showSchedule() {
        System.out.println("showSchedule() called");
        setActiveButton(btnSchedule);
        System.out.println("Calling App.showSchedule()");
        App.showSchedule();
    }

    @FXML
    private void generateSchedule() {
        // Validate inputs
        if (doctorComboBox.getValue() == null || doctorComboBox.getValue().isEmpty()) {
            showAlert("Validation Error", "Please select a doctor.", Alert.AlertType.WARNING);
            return;
        }

        if (fromDatePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a start date.", Alert.AlertType.WARNING);
            return;
        }

        if (toDatePicker.getValue() == null) {
            showAlert("Validation Error", "Please select an end date.", Alert.AlertType.WARNING);
            return;
        }

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate.isAfter(toDate)) {
            showAlert("Validation Error", "Start date must be before or equal to end date.", Alert.AlertType.WARNING);
            return;
        }

        // Check date range (should not exceed 31 days)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(fromDate, toDate);
        if (daysBetween > 31) {
            showAlert("Validation Error", "Date range should not exceed 31 days.", Alert.AlertType.WARNING);
            return;
        }

        // Get selected doctor
        String selectedDoctorText = doctorComboBox.getValue();
        String doctorName = selectedDoctorText.split(" - ")[0];
        Doctor selectedDoctor = null;

        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            for (Doctor doctor : doctors) {
                if (doctor.getName().equals(doctorName)) {
                    selectedDoctor = doctor;
                    break;
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to find selected doctor: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        if (selectedDoctor == null) {
            showAlert("Error", "Selected doctor not found.", Alert.AlertType.ERROR);
            return;
        }

        // Create and start the background task
        // Make copies of the variables to be effectively final
        final Doctor finalSelectedDoctor = selectedDoctor;
        final LocalDate finalFromDate = fromDate;
        final LocalDate finalToDate = toDate;

        scheduleTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Loading appointments...");

                // Get appointments for the selected doctor and date range
                List<Appointment> appointments = appointmentService.getDoctorSchedule(finalSelectedDoctor, finalFromDate, finalToDate);

                int total = appointments.size();
                updateMessage("Processing " + total + " appointments...");

                // Format the schedule text
                StringBuilder schedule = new StringBuilder();
                schedule.append("Schedule for Dr. ").append(finalSelectedDoctor.getName())
                        .append(" (").append(finalSelectedDoctor.getSpecialty()).append(")\n");
                schedule.append("Period: ").append(finalFromDate.format(DateTimeFormatter.ISO_DATE))
                        .append(" to ").append(finalToDate.format(DateTimeFormatter.ISO_DATE)).append("\n\n");

                if (appointments.isEmpty()) {
                    schedule.append("No appointments found for the selected period.");
                } else {
                    // Group appointments by date
                    LocalDate currentDate = null;
                    for (int i = 0; i < appointments.size(); i++) {
                        // Check if task was cancelled
                        if (isCancelled()) {
                            updateMessage("Schedule generation cancelled.");
                            break;
                        }

                        Appointment appointment = appointments.get(i);

                        // Update progress
                        updateProgress(i + 1, total);
                        updateMessage("Processed " + (i + 1) + "/" + total + " appointments");

                        // Add date header if new date
                        if (currentDate == null || !currentDate.equals(appointment.getDate())) {
                            currentDate = appointment.getDate();
                            schedule.append("\n").append(currentDate.format(DateTimeFormatter.ISO_DATE)).append(":\n");
                        }

                        // Add appointment details
                        schedule.append("  ")
                                .append(appointment.getTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                                .append(" - ")
                                .append(appointment.getPatient().getName())
                                .append("\n");
                    }
                }

                return schedule.toString();
            }
        };

        // Bind UI elements to task properties
        progressIndicator.visibleProperty().bind(scheduleTask.runningProperty());
        generateButton.disableProperty().bind(scheduleTask.runningProperty());
        cancelButton.disableProperty().bind(scheduleTask.runningProperty().not());
        statusLabel.textProperty().bind(scheduleTask.messageProperty());

        // Handle task completion
        scheduleTask.setOnSucceeded(e -> {
            String result = scheduleTask.getValue();
            scheduleTextArea.setText(result);
            statusLabel.textProperty().unbind();
            statusLabel.setText("Schedule generated successfully.");
        });

        // Handle task failure
        scheduleTask.setOnFailed(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Failed to generate schedule.");
            Throwable exception = scheduleTask.getException();
            showAlert("Error", "Failed to generate schedule: " + exception.getMessage(), Alert.AlertType.ERROR);
            exception.printStackTrace();
        });

        // Handle task cancellation
        scheduleTask.setOnCancelled(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Schedule generation cancelled.");
            scheduleTextArea.setText("Schedule generation was cancelled.");
        });

        // Start the task in a background thread
        new Thread(scheduleTask, "schedule-generation-task").start();
    }

    @FXML
    private void cancelGeneration() {
        if (scheduleTask != null) {
            scheduleTask.cancel();
        }
    }

    @FXML
    private void exitApp() {
        try {
            Window w = (topBar != null && topBar.getScene() != null) ? topBar.getScene().getWindow() : null;
            if (w != null) {
                w.hide();
            }
        } catch (Exception ignored) {
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}