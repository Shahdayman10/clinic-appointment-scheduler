package com.clinic.app.Controllers;

import com.clinic.app.App;
import com.clinic.app.Models.Patient;
import com.clinic.app.data.services.PatientService;
import com.clinic.app.utils.SimpleIconUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Window;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PatientController implements Initializable {
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
    private Button btnSchedule;
    // Containers
    @FXML
    private HBox mainContainer;
    @FXML
    private VBox contentArea;
    // Search and Sort
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTotalPatients;
    // Form
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker dpDateOfBirth;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextArea taMedicalHistory;
    // Table
    @FXML
    private TableView<Patient> patientsTable;
    private final ObservableList<Patient> patientsData = FXCollections.observableArrayList();
    // Filtered and Sorted Lists
    private FilteredList<Patient> filteredPatients;
    private SortedList<Patient> sortedPatients;
    // State
    private Patient selectedPatient = null;
    // Service
    private final PatientService patientService = PatientService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PatientController.initialize() called");
        if (contentArea != null) {
            HBox.setHgrow(contentArea, Priority.ALWAYS);
        }
        enableResponsiveCompactToggle();
        setActiveButton(btnPatients);
        setupNavigationIcons();
        setupTableColumns();
        loadPatientsFromDatabase();
        setupSearch();
    }

    private void setupNavigationIcons() {
        if (btnDashboard != null) {
            var icon = SimpleIconUtil.createNavigationIcon("dashboard", 24);
            icon.setOpacity(0.95);
            btnDashboard.setGraphic(icon);
        }
        if (btnPatients != null) {
            var icon = SimpleIconUtil.createNavigationIcon("patient", 24);
            icon.setOpacity(0.95);
            btnPatients.setGraphic(icon);
        }
        if (btnDoctors != null) {
            var icon = SimpleIconUtil.createNavigationIcon("doctor_stethoscope", 24);
            icon.setOpacity(0.95);
            btnDoctors.setGraphic(icon);
        }
        if (btnAppointments != null) {
            var icon = SimpleIconUtil.createNavigationIcon("appointments", 24);
            icon.setOpacity(0.95);
            btnAppointments.setGraphic(icon);
        }
        if (btnSchedule != null) {
            var icon = SimpleIconUtil.createNavigationIcon("schedule", 24);
            icon.setOpacity(0.95);
            btnSchedule.setGraphic(icon);
        }
    }

    private void setupSearch() {
        filteredPatients = new FilteredList<>(patientsData, p -> true);
        sortedPatients = new SortedList<>(filteredPatients);
        sortedPatients.comparatorProperty().bind(patientsTable.comparatorProperty());
        patientsTable.setItems(sortedPatients);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatients(newValue);
        });
        filteredPatients.predicateProperty().addListener((observable, oldValue, newValue) -> {
            updatePatientCount();
        });
        patientsData.addListener((javafx.collections.ListChangeListener.Change<? extends Patient> change) -> {
            updatePatientCount();
        });
        updatePatientCount();
    }

    /**
     * Filters the patient list based on the search text using a Predicate.
     * The filtered list is automatically updated in the TableView.
     * @param searchText The text to filter by (can be name, phone, email, etc.)
     */
    private void filterPatients(String searchText) {
        filteredPatients.setPredicate(patient -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return patient.getName().toLowerCase().contains(lowerCaseFilter) ||
                   patient.getPhone().toLowerCase().contains(lowerCaseFilter) ||
                   patient.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                   (patient.getAddress() != null && patient.getAddress().toLowerCase().contains(lowerCaseFilter)) ||
                   String.valueOf(patient.getId()).contains(lowerCaseFilter);
        });
    }

    private void updatePatientCount() {
        int total = patientsData.size();
        int filtered = filteredPatients.size();
        if (filtered == total) {
            lblTotalPatients.setText(String.valueOf(total));
        } else {
            lblTotalPatients.setText(filtered + " of " + total);
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
        List<Button> nav = Arrays.asList(btnDashboard, btnPatients, btnDoctors, btnAppointments, btnSchedule);
        nav.forEach(b -> {
            if (b != null) {
                b.getStyleClass().remove("active");
            }
        });
        if (activeBtn != null && !activeBtn.getStyleClass().contains("active")) {
            activeBtn.getStyleClass().add("active");
        }
        if (mainContainer != null) {
            mainContainer.getStyleClass().removeAll("dashboard-accent", "patients-accent", "doctors-accent", "appointments-accent", "schedule-accent");
            mainContainer.getStyleClass().add("patients-accent");
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
        // Already on patients page - no need to reload
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

    private void loadPatientsFromDatabase() {
        try {
            patientsData.clear();
            List<com.clinic.app.entities.Patient> patientEntities = patientService.getAllPatients();

            // Convert entities to models
            for (com.clinic.app.entities.Patient entity : patientEntities) {
                Patient model = new Patient(
                    entity.getId() != null ? entity.getId() : 0,
                    entity.getName(),
                    entity.getPhone(),
                    entity.getEmail(),
                    entity.getAddress(),
                    entity.getDateOfBirth(),
                    entity.getCreatedAt()
                );
                patientsData.add(model);
            }

            System.out.println("Loaded " + patientsData.size() + " patients from database");

            // Refresh the table view to ensure UI is updated
            if (patientsTable != null) {
                patientsTable.refresh();
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load patients: " + e.getMessage() + ". Please try again.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        patientsTable.getColumns().clear();
        TableColumn<Patient, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idCol.setPrefWidth(70);
        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setPrefWidth(180);
        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        phoneCol.setPrefWidth(120);
        TableColumn<Patient, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailCol.setPrefWidth(180);
        TableColumn<Patient, String> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(cellData -> {
            LocalDate dob = cellData.getValue().getDateOfBirth();
            return javafx.beans.binding.Bindings.createObjectBinding(() ->
                dob != null ? dob.toString() : "");
        });
        dobCol.setPrefWidth(120);
        TableColumn<Patient, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.getStyleClass().add("edit-button");
                deleteBtn.getStyleClass().add("delete-button");
                editBtn.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    editPatient(p);
                });
                deleteBtn.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    deletePatient(p);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5);
                    box.getStyleClass().add("actions-container");
                    box.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
        actionsCol.setPrefWidth(160);
        patientsTable.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, dobCol, actionsCol);
        patientsTable.setItems(patientsData);
    }

    @FXML
    private void savePatient() {
        if (!validateForm()) {
            return;
        }
        try {
            if (selectedPatient == null) {
                // Add new patient
                Patient patientModel = new Patient(
                        0, // ID will be auto-generated
                        txtName.getText().trim(),
                        txtPhone.getText().trim(),
                        txtEmail.getText().trim(),
                        txtAddress.getText().trim(),
                        dpDateOfBirth.getValue(),
                        LocalDateTime.now()
                );

                // Convert to entity and save
                com.clinic.app.entities.Patient patientEntity = new com.clinic.app.entities.Patient();
                patientEntity.setName(patientModel.getName());
                patientEntity.setPhone(patientModel.getPhone());
                patientEntity.setEmail(patientModel.getEmail());
                patientEntity.setAddress(patientModel.getAddress());
                patientEntity.setDateOfBirth(patientModel.getDateOfBirth());

                boolean success = patientService.addPatient(patientEntity);

                if (success) {
                    loadPatientsFromDatabase();
                    clearForm();
                    showAlert("Success", "Patient added successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Database Error", "Failed to save patient to database. Please try again.", Alert.AlertType.ERROR);
                }
            } else {
                // Update existing patient
                selectedPatient.setName(txtName.getText().trim());
                selectedPatient.setPhone(txtPhone.getText().trim());
                selectedPatient.setEmail(txtEmail.getText().trim());
                selectedPatient.setAddress(txtAddress.getText().trim());
                selectedPatient.setDateOfBirth(dpDateOfBirth.getValue());

                // Convert to entity and update
                com.clinic.app.entities.Patient patientEntity = new com.clinic.app.entities.Patient();
                patientEntity.setId(selectedPatient.getId());
                patientEntity.setName(selectedPatient.getName());
                patientEntity.setPhone(selectedPatient.getPhone());
                patientEntity.setEmail(selectedPatient.getEmail());
                patientEntity.setAddress(selectedPatient.getAddress());
                patientEntity.setDateOfBirth(selectedPatient.getDateOfBirth());
                patientEntity.setCreatedAt(selectedPatient.getCreatedAt());

                boolean success = patientService.updatePatient(patientEntity);

                if (success) {
                    loadPatientsFromDatabase();
                    clearForm();
                    showAlert("Success", "Patient updated successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Database Error", "Failed to update patient in database. Please try again.", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            showAlert("Database Error", "Error saving patient: " + e.getMessage() + ". Please try again.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        selectedPatient = null;
    }

    @FXML
    private void clearForm() {
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        dpDateOfBirth.setValue(null);
        txtAddress.clear();
        taMedicalHistory.clear();
        selectedPatient = null;
    }

    private void editPatient(Patient p) {
        selectedPatient = p;
        txtName.setText(p.getName());
        txtPhone.setText(p.getPhone());
        txtEmail.setText(p.getEmail());
        dpDateOfBirth.setValue(p.getDateOfBirth());
        txtAddress.setText(p.getAddress());
        // Note: Medical history is not part of the Patient entity in this implementation
    }

    private void deletePatient(Patient p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Patient");
        alert.setContentText("Are you sure you want to delete patient " + p.getName() + "?\n\nThis action cannot be undone.");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    boolean success = patientService.deletePatient(p.getId());
                    if (success) {
                        loadPatientsFromDatabase();
                        clearForm();
                        showAlert("Success", "Patient deleted successfully.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Database Error", "Failed to delete patient.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "Error deleting patient: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // Overloaded method for button action
    @FXML
    private void deletePatient() {
        if (selectedPatient != null) {
            deletePatient(selectedPatient);
        } else {
            showAlert("No Selection", "Please select a patient to delete.", Alert.AlertType.WARNING);
        }
    }

    private boolean validateForm() {
        // Check for empty fields
        if (txtName.getText().trim().isEmpty()) {
            return error("Validation Error", "Please enter patient name.");
        }

        // Validate field lengths
        if (txtName.getText().trim().length() > 100) {
            return error("Validation Error", "Name is too long (max 100 characters).");
        }

        if (txtPhone.getText().length() > 20) {
            return error("Validation Error", "Phone number is too long (max 20 characters).");
        }

        if (txtEmail.getText().length() > 100) {
            return error("Validation Error", "Email is too long (max 100 characters).");
        }

        if (txtAddress.getText().length() > 200) {
            return error("Validation Error", "Address is too long (max 200 characters).");
        }

        // Validate email format if provided
        if (!txtEmail.getText().trim().isEmpty() &&
            !txtEmail.getText().trim().matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            return error("Validation Error", "Please enter a valid email address.");
        }

        return true;
    }

    private boolean error(String title, String msg) {
        showAlert(title, msg, Alert.AlertType.ERROR);
        return false;
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
        filterPatients(searchText);
        if (filteredPatients.isEmpty()) {
            showAlert("No Results", "No patients found matching: '" + searchText + "'.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleClearSearch() {
        txtSearch.clear();
        filterPatients("");
    }

    @FXML
    private void handleRowClick(MouseEvent e) {
        if (e.getClickCount() == 2) { // Double-click
            Patient selected = patientsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                editPatient(selected);
            }
        }
    }
}