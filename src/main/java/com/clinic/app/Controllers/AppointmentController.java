package com.clinic.app.Controllers;

import com.clinic.app.App;
import com.clinic.app.Models.Appointment;
import com.clinic.app.data.services.AppointmentService;
import com.clinic.app.data.services.PatientService;
import com.clinic.app.data.services.DoctorService;
import com.clinic.app.utils.SimpleIconUtil;
import com.clinic.app.utils.EntityModelConverter;
import com.clinic.app.utils.ApiClient;
import com.clinic.app.dto.AppointmentDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;

public class AppointmentController implements Initializable {
    // Top for Exit
    @FXML
    private HBox topBar;
    // Sidebar + Buttons
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
    private Button btnSchedule;  // Add this line
    // Containers
    @FXML
    private HBox mainContainer;
    @FXML
    private VBox contentArea;
    // Search and Sort
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbSortBy;
    @FXML
    private ComboBox<String> cbStatusFilter;
    @FXML
    private Label lblTotalAppointments;
    // Form
    @FXML
    private ComboBox<String> cbPatient;
    @FXML
    private ComboBox<String> cbDoctor;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField txtTime;
    @FXML
    private TextArea taNotes;
    // Table
    @FXML
    private TableView<Appointment> appointmentsTable;
    private final ObservableList<Appointment> appointmentsData = FXCollections.observableArrayList();
    // Filtered and Sorted Lists
    private FilteredList<Appointment> filteredAppointments;
    private SortedList<Appointment> sortedAppointments;
    // State
    private Appointment selectedAppointment = null;
    // Services
    private final AppointmentService appointmentService = AppointmentService.getInstance();
    private final PatientService patientService = PatientService.getInstance();
    private final DoctorService doctorService = DoctorService.getInstance();

    // New elements for background tasks and reminders
    @FXML
    private Button refreshButton;
    @FXML
    private Button generateRemindersButton;
    @FXML
    private Button sendExternalRemindersButton; // New button for external API
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea remindersTextArea;

    private Task<List<com.clinic.app.entities.Appointment>> refreshTask;

    // API Client for REST operations
    private final ApiClient apiClient = new ApiClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AppointmentController.initialize() called");
        if (contentArea != null) {
            HBox.setHgrow(contentArea, Priority.ALWAYS);
        }
        enableResponsiveCompactToggle();
        setActiveButton(btnAppointments);
        setupNavigationIcons();
        loadPatientsAndDoctors();
        setupTableColumns();
        loadAppointmentsFromDatabase(); // This will be changed to use API
        setupSearchAndSort();
    }

    private void setupNavigationIcons() {
        System.out.println("Setting up navigation icons for Appointment page...");
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
        if (btnSchedule != null) {  // Add this block
            var icon = SimpleIconUtil.createNavigationIcon("schedule", 24);
            icon.setOpacity(0.95);
            btnSchedule.setGraphic(icon);
            System.out.println("Schedule icon set: " + (icon.getImage() != null ? "Success" : "Failed"));
        }
    }

    private void setupSearchAndSort() {
        filteredAppointments = new FilteredList<>(appointmentsData, p -> true);
        sortedAppointments = new SortedList<>(filteredAppointments);
        sortedAppointments.comparatorProperty().bind(appointmentsTable.comparatorProperty());
        appointmentsTable.setItems(sortedAppointments);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAppointments(newValue);
        });
        cbSortBy.setItems(FXCollections.observableArrayList(
            "ID", "Patient", "Doctor", "Date", "Time", "Status"
        ));
        cbSortBy.setValue("Date"); // Default sort
        cbStatusFilter.setItems(FXCollections.observableArrayList(
            "All", "Scheduled", "Completed", "Cancelled"
        ));
        cbStatusFilter.setValue("All");
        // Handle sort selection changes - Uses Lambda expression for listener
        cbSortBy.valueProperty().addListener((observable, oldValue, newValue) -> {
            sortAppointments(newValue);
        });
        // Handle status filter changes - Uses Lambda expression for listener
        cbStatusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterAppointments(txtSearch.getText());
        });
        filteredAppointments.predicateProperty().addListener((observable, oldValue, newValue) -> {
            updateAppointmentCount();
        });
        appointmentsData.addListener((ListChangeListener<Appointment>) change -> {
            updateAppointmentCount();
        });
        updateAppointmentCount();
    }

    /**
     * Filters the appointment list based on the search text and status filter using a Predicate.
     * The filtered list is automatically updated in the TableView.
     * @param searchText The text to filter by (can be patient name, doctor name, date, etc.)
     */
    private void filterAppointments(String searchText) {
        String statusFilter = cbStatusFilter.getValue();
        filteredAppointments.setPredicate(appointment -> {
            // Apply status filter
            if (!"All".equals(statusFilter) && !statusFilter.equalsIgnoreCase(appointment.getStatus())) {
                return false;
            }
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return appointment.getPatientName().toLowerCase().contains(lowerCaseFilter) ||
                   appointment.getDoctorName().toLowerCase().contains(lowerCaseFilter) ||
                   appointment.getDate().toLowerCase().contains(lowerCaseFilter) ||
                   appointment.getTime().toLowerCase().contains(lowerCaseFilter) ||
                   appointment.getStatus().toLowerCase().contains(lowerCaseFilter) ||
                   (appointment.getNotes() != null && appointment.getNotes().toLowerCase().contains(lowerCaseFilter)) ||
                   String.valueOf(appointment.getId()).contains(lowerCaseFilter);
        });
    }

    /**
     * Sorts the appointment list based on the selected criteria.
     * Uses TableView's built-in sorting for all columns.
     * @param sortBy The criteria to sort by (ID, Patient, Doctor, Date, Time, Status)
     */
    private void sortAppointments(String sortBy) {
        appointmentsTable.getSortOrder().clear();
        switch (sortBy) {
            case "Patient":
                appointmentsTable.getSortOrder().add(appointmentsTable.getColumns().get(1)); // Patient column
                break;
            case "Doctor":
                appointmentsTable.getSortOrder().add(appointmentsTable.getColumns().get(2)); // Doctor column
                break;
            case "Date":
                appointmentsTable.getSortOrder().add(appointmentsTable.getColumns().get(3)); // Date column
                break;
            case "Time":
                appointmentsTable.getSortOrder().add(appointmentsTable.getColumns().get(4)); // Time column
                break;
            case "Status":
                appointmentsTable.getSortOrder().add(appointmentsTable.getColumns().get(5)); // Status column
                break;
            default:
                appointmentsTable.getSortOrder().add(appointmentsTable.getColumns().get(0)); // ID column
        }
    }

    private void updateAppointmentCount() {
        int total = appointmentsData.size();
        int filtered = filteredAppointments.size();
        if (filtered == total) {
            lblTotalAppointments.setText(String.valueOf(total));
        } else {
            lblTotalAppointments.setText(filtered + " of " + total);
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

    private void setActiveButton(Button activeBtn) {
        List<Button> nav = Arrays.asList(btnDashboard, btnPatients, btnDoctors, btnAppointments, btnSchedule);  // Add btnSchedule to the list
        nav.forEach(b -> {
            if (b != null) {
                b.getStyleClass().remove("active");
            }
        });
        if (activeBtn != null && !activeBtn.getStyleClass().contains("active")) {
            activeBtn.getStyleClass().add("active");
        }
        if (mainContainer != null) {
            // Remove all accent classes first
            mainContainer.getStyleClass().removeAll("dashboard-accent", "patients-accent", "doctors-accent", "appointments-accent", "schedule-accent");
            // Add the appointments accent class
            mainContainer.getStyleClass().add("appointments-accent");
        }
    }

    @FXML
    private void showDashboard() {
        setActiveButton(btnDashboard);
        App.showDashboard();
    }

    @FXML
    private void showPatients() {
        setActiveButton(btnPatients);
        App.showPatients();
    }

    @FXML
    private void showDoctors() {
        setActiveButton(btnDoctors);
        App.showDoctors();
    }

    @FXML
    private void showAppointments() {
        setActiveButton(btnAppointments);
        // Already on appointments page - no need to reload
    }

    @FXML
    private void showSchedule() {
        setActiveButton(btnSchedule);
        App.showSchedule();
    }

    private void enableResponsiveCompactToggle() {
        if (contentArea == null) {
            return;
        }
        contentArea.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                attachResponsiveBehavior(newScene);
            }
        });
        Scene scene = contentArea.getScene();
        if (scene != null) {
            attachResponsiveBehavior(scene);
        }
    }

    private void attachResponsiveBehavior(Scene scene) {
        toggleCompact(scene.getWidth() < 1024);
        scene.widthProperty().addListener((o, oldW, newW) -> toggleCompact(newW.doubleValue() < 1024));
    }

    private void toggleCompact(boolean compact) {
        if (contentArea == null || contentArea.getScene() == null) {
            return;
        }
        var root = contentArea.getScene().getRoot();
        var classes = root.getStyleClass();
        if (compact) {
            if (!classes.contains("compact")) {
                classes.add("compact");
            }
        } else {
            classes.remove("compact");
        }
    }

    private void loadPatientsAndDoctors() {
        try {
            // Load patients from database
            List<com.clinic.app.entities.Patient> patientEntities = patientService.getAllPatients();
            List<String> patientNames = patientEntities.stream()
                    .map(com.clinic.app.entities.Patient::getName)
                    .collect(Collectors.toList());
            cbPatient.setItems(FXCollections.observableArrayList(patientNames));

            // Load doctors from database
            List<com.clinic.app.entities.Doctor> doctorEntities = doctorService.getAllDoctors();
            List<String> doctorNames = doctorEntities.stream()
                    .map(doctor -> doctor.getName() + " - " + doctor.getSpecialty())
                    .collect(Collectors.toList());
            cbDoctor.setItems(FXCollections.observableArrayList(doctorNames));
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load patients or doctors: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAppointmentsFromDatabase() {
        try {
            appointmentsData.clear();
            List<com.clinic.app.entities.Appointment> appointmentEntities = appointmentService.getAllAppointments();

            // Convert entities to models
            for (com.clinic.app.entities.Appointment entity : appointmentEntities) {
                Appointment model = EntityModelConverter.toAppointmentModel(entity);
                appointmentsData.add(model);
            }

            System.out.println("Loaded " + appointmentsData.size() + " appointments from database");

            // Refresh the table view to ensure UI is updated
            if (appointmentsTable != null) {
                appointmentsTable.refresh();
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load appointments: " + e.getMessage() + ". Please try again.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // New method for background refresh
    @FXML
    private void refreshAppointments() {
        // Create and start the background task
        refreshTask = new Task<List<com.clinic.app.entities.Appointment>>() {
            @Override
            protected List<com.clinic.app.entities.Appointment> call() throws Exception {
                updateMessage("Loading appointments...");

                // Simulate a potentially long-running operation
                Thread.sleep(500); // Simulate network/database delay

                // Get appointments from service
                List<com.clinic.app.entities.Appointment> appointments = appointmentService.getAllAppointments();

                int total = appointments.size();
                updateMessage("Processing " + total + " appointments...");

                for (int i = 0; i < appointments.size(); i++) {
                    // Check if task was cancelled
                    if (isCancelled()) {
                        updateMessage("Refresh cancelled.");
                        break;
                    }

                    // Update progress
                    updateProgress(i + 1, total);
                    updateMessage("Processed " + (i + 1) + "/" + total + " appointments");

                    // Small delay to simulate processing
                    Thread.sleep(10);
                }

                return appointments;
            }
        };

        // Bind UI elements to task properties
        if (progressIndicator != null) {
            progressIndicator.visibleProperty().bind(refreshTask.runningProperty());
        }
        if (refreshButton != null) {
            refreshButton.disableProperty().bind(refreshTask.runningProperty());
        }
        if (statusLabel != null) {
            statusLabel.textProperty().bind(refreshTask.messageProperty());
        }

        // Handle task completion
        refreshTask.setOnSucceeded(e -> {
            List<com.clinic.app.entities.Appointment> result = refreshTask.getValue();
            appointmentsData.clear();

            // Convert entities to models
            for (com.clinic.app.entities.Appointment entity : result) {
                Appointment model = EntityModelConverter.toAppointmentModel(entity);
                appointmentsData.add(model);
            }

            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("Appointments refreshed successfully.");
            }

            if (appointmentsTable != null) {
                appointmentsTable.refresh();
            }
        });

        // Handle task failure
        refreshTask.setOnFailed(e -> {
            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("Failed to refresh appointments.");
            }
            Throwable exception = refreshTask.getException();
            showAlert("Error", "Failed to refresh appointments: " + exception.getMessage(), Alert.AlertType.ERROR);
            exception.printStackTrace();
        });

        // Handle task cancellation
        refreshTask.setOnCancelled(e -> {
            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("Refresh cancelled.");
            }
        });

        // Start the task in a background thread
        new Thread(refreshTask, "appointments-refresh-task").start();
    }

    // New method for generating reminders
    @FXML
    private void generateReminders() {
        Task<String> remindersTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Generating reminders...");

                // Get upcoming appointments
                List<com.clinic.app.entities.Appointment> upcomingAppointments = appointmentService.getUpcomingAppointments();

                int total = upcomingAppointments.size();
                updateMessage("Processing " + total + " upcoming appointments...");

                // Group appointments by doctor
                Map<String, List<com.clinic.app.entities.Appointment>> appointmentsByDoctor = new HashMap<>();

                for (int i = 0; i < upcomingAppointments.size(); i++) {
                    // Check if task was cancelled
                    if (isCancelled()) {
                        updateMessage("Reminders generation cancelled.");
                        break;
                    }

                    com.clinic.app.entities.Appointment appointment = upcomingAppointments.get(i);

                    // Update progress
                    updateProgress(i + 1, total);
                    updateMessage("Processed " + (i + 1) + "/" + total + " appointments");

                    String doctorName = appointment.getDoctor().getName();
                    appointmentsByDoctor.computeIfAbsent(doctorName, k -> new ArrayList<>()).add(appointment);
                }

                // Format reminders text
                StringBuilder reminders = new StringBuilder();
                reminders.append("UPCOMING APPOINTMENTS (Next 48 hours)\n");
                reminders.append("=====================================\n\n");

                if (appointmentsByDoctor.isEmpty()) {
                    reminders.append("No upcoming appointments found.\n");
                } else {
                    for (Map.Entry<String, List<com.clinic.app.entities.Appointment>> entry : appointmentsByDoctor.entrySet()) {
                        String doctorName = entry.getKey();
                        List<com.clinic.app.entities.Appointment> appointments = entry.getValue();

                        reminders.append("Dr. ").append(doctorName).append(":\n");

                        for (com.clinic.app.entities.Appointment appointment : appointments) {
                            reminders.append("  ")
                                    .append(appointment.getDate().toString())
                                    .append(" at ")
                                    .append(appointment.getTime().toString())
                                    .append(" - ")
                                    .append(appointment.getPatient().getName())
                                    .append("\n");
                        }

                        reminders.append("\n");
                    }
                }

                return reminders.toString();
            }
        };

        // Bind UI elements to task properties
        if (progressIndicator != null) {
            progressIndicator.visibleProperty().bind(remindersTask.runningProperty());
        }
        if (generateRemindersButton != null) {
            generateRemindersButton.disableProperty().bind(remindersTask.runningProperty());
        }
        if (statusLabel != null) {
            statusLabel.textProperty().bind(remindersTask.messageProperty());
        }

        // Handle task completion
        remindersTask.setOnSucceeded(e -> {
            String result = remindersTask.getValue();
            if (remindersTextArea != null) {
                remindersTextArea.setText(result);
            }

            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("Reminders generated successfully.");
            }
        });

        // Handle task failure
        remindersTask.setOnFailed(e -> {
            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("Failed to generate reminders.");
            }
            Throwable exception = remindersTask.getException();
            showAlert("Error", "Failed to generate reminders: " + exception.getMessage(), Alert.AlertType.ERROR);
            exception.printStackTrace();
        });

        // Handle task cancellation
        remindersTask.setOnCancelled(e -> {
            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("Reminders generation cancelled.");
            }
            if (remindersTextArea != null) {
                remindersTextArea.setText("Reminders generation was cancelled.");
            }
        });

        // Start the task in a background thread
        new Thread(remindersTask, "reminders-generation-task").start();
    }

    // New method for sending reminders via external API
    @FXML
    private void sendExternalReminders() {
        Task<String> externalRemindersTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Sending reminders via external API...");

                try {
                    // Send reminders via external API
                    boolean success = apiClient.sendAppointmentReminders();

                    if (success) {
                        return "Reminders sent successfully via external API.";
                    } else {
                        return "Failed to send reminders via external API.";
                    }
                } catch (Exception e) {
                    throw new Exception("Error sending reminders: " + e.getMessage());
                }
            }
        };

        // Bind UI elements to task properties
        if (progressIndicator != null) {
            progressIndicator.visibleProperty().bind(externalRemindersTask.runningProperty());
        }
        if (sendExternalRemindersButton != null) {
            sendExternalRemindersButton.disableProperty().bind(externalRemindersTask.runningProperty());
        }
        if (statusLabel != null) {
            statusLabel.textProperty().bind(externalRemindersTask.messageProperty());
        }

        // Handle task completion
        externalRemindersTask.setOnSucceeded(e -> {
            String result = externalRemindersTask.getValue();
            if (remindersTextArea != null) {
                remindersTextArea.setText(result);
            }

            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText(result);
            }
        });

        // Handle task failure
        externalRemindersTask.setOnFailed(e -> {
            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("Failed to send external reminders.");
            }
            Throwable exception = externalRemindersTask.getException();
            showAlert("Error", "Failed to send external reminders: " + exception.getMessage(), Alert.AlertType.ERROR);
            exception.printStackTrace();
        });

        // Handle task cancellation
        externalRemindersTask.setOnCancelled(e -> {
            if (statusLabel != null) {
                statusLabel.textProperty().unbind();
                statusLabel.setText("External reminders sending cancelled.");
            }
            if (remindersTextArea != null) {
                remindersTextArea.setText("External reminders sending was cancelled.");
            }
        });

        // Start the task in a background thread
        new Thread(externalRemindersTask, "external-reminders-task").start();
    }

    // New method for cancelling background tasks
    @FXML
    private void cancelTask() {
        if (refreshTask != null && refreshTask.isRunning()) {
            refreshTask.cancel();
        }
    }

    private void setupTableColumns() {
        appointmentsTable.getColumns().clear();
        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idCol.setPrefWidth(70);
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(cellData -> cellData.getValue().patientNameProperty());
        patientCol.setPrefWidth(180);
        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(cellData -> cellData.getValue().doctorNameProperty());
        doctorCol.setPrefWidth(180);
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        dateCol.setPrefWidth(120);
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        timeCol.setPrefWidth(100);
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusCol.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "scheduled":
                            setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                            break;
                        case "completed":
                            setStyle("-fx-text-fill: #6c757d; -fx-font-weight: bold;");
                            break;
                        case "cancelled":
                            setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        statusCol.setPrefWidth(100);
        TableColumn<Appointment, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button cancelBtn = new Button("Cancel");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.getStyleClass().add("edit-button");
                cancelBtn.getStyleClass().add("cancel-button");
                deleteBtn.getStyleClass().add("delete-button");
                editBtn.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    editAppointment(a);
                });
                cancelBtn.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    cancelAppointment(a);
                });
                deleteBtn.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    deleteAppointment(a);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(5);
                    box.getStyleClass().add("actions-container");
                    if ("scheduled".equals(appointment.getStatus())) {
                        box.getChildren().addAll(editBtn, cancelBtn);
                    } else {
                        box.getChildren().addAll(deleteBtn);
                    }
                    setGraphic(box);
                }
            }
        });
        actionsCol.setPrefWidth(160);
        appointmentsTable.getColumns().addAll(idCol, patientCol, doctorCol, dateCol, timeCol, statusCol, actionsCol);
        appointmentsTable.setItems(appointmentsData);
        // تأكد من تحديث الجدول
        appointmentsTable.refresh();
    }

    @FXML
    private void scheduleAppointment() {
        if (!validateForm()) {
            return;
        }
        try {
            if (selectedAppointment == null) {
                // إضافة موعد جديد
                Appointment appointmentModel = new Appointment(
                        0, // ID will be auto-generated
                        getPatientIdByName(cbPatient.getValue()),
                        getDoctorIdByName(cbDoctor.getValue()),
                        cbPatient.getValue(),
                        cbDoctor.getValue().split(" - ")[0], // Doctor name only
                        dpDate.getValue().toString(),
                        txtTime.getText().trim(),
                        "scheduled",
                        taNotes.getText().trim()
                );

                // Convert to DTO and send to API
                AppointmentDTO appointmentDTO = convertModelToDTO(appointmentModel);

                // Send to API
                AppointmentDTO createdDTO = apiClient.createAppointment(appointmentDTO);

                if (createdDTO != null) {
                    // Convert back to model for display
                    Appointment createdModel = convertDTOToModel(createdDTO);
                    appointmentsData.add(createdModel);
                    clearForm();
                    showAlert("Success", "Appointment scheduled successfully via API.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("API Error", "Failed to save appointment via API. Please try again.", Alert.AlertType.ERROR);
                }
            } else {
                // تعديل موعد موجود
                System.out.println("Updating existing appointment: " + selectedAppointment.getId());
                System.out.println("Patient ID before update: " + selectedAppointment.getPatientId());
                System.out.println("Doctor ID before update: " + selectedAppointment.getDoctorId());

                selectedAppointment.setPatientId(getPatientIdByName(cbPatient.getValue()));
                selectedAppointment.setDoctorId(getDoctorIdByName(cbDoctor.getValue()));
                selectedAppointment.setPatientName(cbPatient.getValue());
                selectedAppointment.setDoctorName(cbDoctor.getValue().split(" - ")[0]);
                selectedAppointment.setDate(dpDate.getValue().toString());
                selectedAppointment.setTime(txtTime.getText().trim());
                selectedAppointment.setNotes(taNotes.getText().trim());
                selectedAppointment.setStatus("scheduled");

                // Convert to DTO and send to API
                AppointmentDTO appointmentDTO = convertModelToDTO(selectedAppointment);

                // Send to API
                AppointmentDTO updatedDTO = apiClient.createAppointment(appointmentDTO);

                if (updatedDTO != null) {
                    // Update the model in the list
                    Appointment updatedModel = convertDTOToModel(updatedDTO);
                    int index = appointmentsData.indexOf(selectedAppointment);
                    if (index >= 0) {
                        appointmentsData.set(index, updatedModel);
                    }
                    clearForm();
                    showAlert("Success", "Appointment updated successfully via API.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("API Error", "Failed to update appointment via API. Please try again.", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            showAlert("API Error", "Error saving appointment via API: " + e.getMessage() + ". Please try again.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        selectedAppointment = null;
    }

    @FXML
    private void clearForm() {
        cbPatient.setValue(null);
        cbDoctor.setValue(null);
        dpDate.setValue(null);
        txtTime.clear();
        taNotes.clear();
        selectedAppointment = null;
    }

    private void editAppointment(Appointment a) {
        selectedAppointment = a;

        // Preserve the appointment ID, patient ID, and doctor ID
        int appointmentId = a.getId();
        int patientId = a.getPatientId();
        int doctorId = a.getDoctorId();

        cbPatient.setValue(a.getPatientName());

        // Set the patient ID in the selected appointment
        selectedAppointment.setPatientId(patientId);

        // Find the doctor with specialty format for ComboBox
        try {
            List<com.clinic.app.entities.Doctor> doctorEntities = doctorService.getAllDoctors();
            List<com.clinic.app.Models.Doctor> doctorModels = new ArrayList<>();
            for (com.clinic.app.entities.Doctor entity : doctorEntities) {
                doctorModels.add(EntityModelConverter.toDoctorModel(entity));
            }

            String doctorWithSpecialty = doctorModels.stream()
                    .filter(doctor -> doctor.getName().equals(a.getDoctorName()))
                    .map(doctor -> doctor.getName() + " - " + doctor.getSpecialty())
                    .findFirst()
                    .orElse(a.getDoctorName());
            cbDoctor.setValue(doctorWithSpecialty);

            // Set the doctor ID in the selected appointment
            selectedAppointment.setDoctorId(doctorId);
        } catch (Exception e) {
            cbDoctor.setValue(a.getDoctorName());
            // Set the doctor ID in the selected appointment even if there's an error
            selectedAppointment.setDoctorId(doctorId);
        }
        dpDate.setValue(LocalDate.parse(a.getDate()));
        txtTime.setText(a.getTime());
        taNotes.setText(a.getNotes());

        // Make sure the appointment ID is preserved
        selectedAppointment.setId(appointmentId);

        // Debug information
        System.out.println("Editing appointment - ID: " + appointmentId + ", Patient ID: " + patientId + ", Doctor ID: " + doctorId);
        System.out.println("Selected appointment - ID: " + selectedAppointment.getId() + ", Patient ID: " + selectedAppointment.getPatientId() + ", Doctor ID: " + selectedAppointment.getDoctorId());
    }

    private void cancelAppointment(Appointment a) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancellation");
        alert.setHeaderText("Cancel Appointment");
        alert.setContentText("Are you sure you want to cancel this appointment for " + a.getPatientName() + "?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    a.setStatus("cancelled");

                    // Convert to entity and update
                    com.clinic.app.entities.Appointment appointmentEntity = EntityModelConverter.toAppointmentEntity(a);
                    boolean success = appointmentService.updateAppointment(appointmentEntity);

                    if (success) {
                        loadAppointmentsFromDatabase();
                        clearForm();
                        showAlert("Success", "Appointment cancelled successfully.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Database Error", "Failed to cancel appointment.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "Error cancelling appointment: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void deleteAppointment(Appointment a) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Appointment");
        alert.setContentText("Are you sure you want to delete this appointment for " + a.getPatientName() + "?\n\nThis action cannot be undone.");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    // Convert model to entity for deletion
                    com.clinic.app.entities.Appointment appointmentEntity = EntityModelConverter.toAppointmentEntity(a);
                    boolean success = appointmentService.deleteAppointment(appointmentEntity.getId());

                    if (success) {
                        loadAppointmentsFromDatabase();
                        clearForm();
                        showAlert("Success", "Appointment deleted successfully.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Database Error", "Failed to delete appointment.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "Error deleting appointment: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private boolean validateForm() {
        // Check for empty fields
        if (cbPatient.getValue() == null || cbPatient.getValue().isEmpty()) {
            return error("Validation Error", "Please select a patient.");
        }
        if (cbDoctor.getValue() == null || cbDoctor.getValue().isEmpty()) {
            return error("Validation Error", "Please select a doctor.");
        }
        if (dpDate.getValue() == null) {
            return error("Validation Error", "Please select a date.");
        }
        if (txtTime.getText().trim().isEmpty()) {
            return error("Validation Error", "Please enter a time.");
        }

        // Validate time format (HH:MM)
        try {
            LocalTime.parse(txtTime.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return error("Validation Error", "Please enter time in HH:MM format (24-hour). Example: 14:30");
        }

        // Check if appointment date is not in the past
        if (dpDate.getValue().isBefore(LocalDate.now())) {
            return error("Validation Error", "Appointment date cannot be in the past. Please select a future date.");
        }

        // Validate field lengths
        if (txtTime.getText().trim().length() > 5) {
            return error("Validation Error", "Time format is too long. Please use HH:MM format.");
        }

        if (taNotes.getText().length() > 500) {
            return error("Validation Error", "Notes are too long (max 500 characters).");
        }

        // Check if patient and doctor exist in the database
        try {
            int patientId = getPatientIdByName(cbPatient.getValue());
            int doctorId = getDoctorIdByName(cbDoctor.getValue());

            if (patientId <= 0) {
                return error("Validation Error", "Selected patient not found in database.");
            }

            if (doctorId <= 0) {
                return error("Validation Error", "Selected doctor not found in database.");
            }
        } catch (Exception e) {
            return error("Database Error", "Error validating patient or doctor information: " + e.getMessage());
        }

        return true;
    }

    private boolean error(String title, String msg) {
        showAlert(title, msg, Alert.AlertType.ERROR);
        return false;
    }

    private int getPatientIdByName(String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }
        try {
            List<com.clinic.app.entities.Patient> patientEntities = patientService.getAllPatients();
            for (com.clinic.app.entities.Patient entity : patientEntities) {
                if (entity.getName() != null && entity.getName().equals(name)) {
                    return entity.getId();
                }
            }
            System.err.println("Patient not found with name: " + name);
        } catch (Exception e) {
            System.err.println("Error getting patient ID by name: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private int getDoctorIdByName(String doctorWithSpecialty) {
        if (doctorWithSpecialty == null || doctorWithSpecialty.isEmpty()) {
            return 0;
        }
        try {
            // Extract doctor name from "Doctor Name - Specialty" format
            String doctorName = doctorWithSpecialty;
            if (doctorWithSpecialty.contains(" - ")) {
                doctorName = doctorWithSpecialty.split(" - ")[0];
            }

            List<com.clinic.app.entities.Doctor> doctorEntities = doctorService.getAllDoctors();
            for (com.clinic.app.entities.Doctor entity : doctorEntities) {
                if (entity.getName() != null && entity.getName().equals(doctorName)) {
                    return entity.getId();
                }
            }
            System.err.println("Doctor not found with name: " + doctorName);
        } catch (Exception e) {
            System.err.println("Error getting doctor ID by name: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            showAlert("Search Error", "Please enter a search term.", Alert.AlertType.WARNING);
            txtSearch.requestFocus();
            return;
        }
        filterAppointments(searchText);
        if (filteredAppointments.isEmpty()) {
            showAlert("No Results", "No appointments found matching: '" + searchText + "'.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleClearSearch() {
        txtSearch.clear();
        filterAppointments("");
    }

    @FXML
    private void handleRowClick(MouseEvent e) {
        if (e.getClickCount() == 2) { // Double-click
            Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                editAppointment(selected);
            }
        }
    }

    // Helper method to convert model to DTO
    private AppointmentDTO convertModelToDTO(Appointment model) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(model.getId());
        dto.setPatientId(model.getPatientId());
        dto.setPatientName(model.getPatientName());
        dto.setDoctorId(model.getDoctorId());
        dto.setDoctorName(model.getDoctorName());
        dto.setDate(LocalDate.parse(model.getDate()));
        dto.setTime(LocalTime.parse(model.getTime()));
        dto.setStatus(model.getStatus());
        dto.setNotes(model.getNotes());
        // Note: createdAt and updatedAt would be set by the server
        return dto;
    }

    // Helper method to convert DTO to model
    private Appointment convertDTOToModel(AppointmentDTO dto) {
        return new Appointment(
                dto.getId(),
                dto.getPatientId(),
                dto.getDoctorId(),
                dto.getPatientName(),
                dto.getDoctorName(),
                dto.getDate().toString(),
                dto.getTime().toString(),
                dto.getStatus(),
                dto.getNotes()
        );
    }
}