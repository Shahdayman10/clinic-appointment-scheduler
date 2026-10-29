package com.clinic.app.Controllers;

import com.clinic.app.App;
import com.clinic.app.Models.Doctor;
import com.clinic.app.data.services.DoctorService;
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
import java.util.*;

public class DoctorController implements Initializable {
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
    private Label lblTotalDoctors;
    // Form
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSpecialty;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtEmail;
    @FXML
    private CheckBox chkAvailable;
    @FXML
    private TextArea taNotes;
    // Table
    @FXML
    private TableView<Doctor> doctorsTable;
    private final ObservableList<Doctor> doctorsData = FXCollections.observableArrayList();
    // Filtered and Sorted Lists
    private FilteredList<Doctor> filteredDoctors;
    private SortedList<Doctor> sortedDoctors;
    // State
    private Doctor selectedDoctor = null;
    // Service
    private final DoctorService doctorService = DoctorService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DoctorController.initialize() called");
        if (contentArea != null) {
            HBox.setHgrow(contentArea, Priority.ALWAYS);
        }
        enableResponsiveCompactToggle();
        setActiveButton(btnDoctors);
        setupNavigationIcons();
        setupTableColumns();
        loadDoctorsFromDatabase();
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
        filteredDoctors = new FilteredList<>(doctorsData, p -> true);
        sortedDoctors = new SortedList<>(filteredDoctors);
        sortedDoctors.comparatorProperty().bind(doctorsTable.comparatorProperty());
        doctorsTable.setItems(sortedDoctors);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDoctors(newValue);
        });
        filteredDoctors.predicateProperty().addListener((observable, oldValue, newValue) -> {
            updateDoctorCount();
        });
        doctorsData.addListener((javafx.collections.ListChangeListener.Change<? extends Doctor> change) -> {
            updateDoctorCount();
        });
        updateDoctorCount();
    }

    /**
     * Filters the doctor list based on the search text using a Predicate.
     * The filtered list is automatically updated in the TableView.
     * @param searchText The text to filter by (can be name, specialty, phone, email, etc.)
     */
    private void filterDoctors(String searchText) {
        filteredDoctors.setPredicate(doctor -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return doctor.getName().toLowerCase().contains(lowerCaseFilter) ||
                   doctor.getSpecialty().toLowerCase().contains(lowerCaseFilter) ||
                   doctor.getPhone().toLowerCase().contains(lowerCaseFilter) ||
                   doctor.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                   String.valueOf(doctor.getId()).contains(lowerCaseFilter);
        });
    }

    private void updateDoctorCount() {
        int total = doctorsData.size();
        int filtered = filteredDoctors.size();
        if (filtered == total) {
            lblTotalDoctors.setText(String.valueOf(total));
        } else {
            lblTotalDoctors.setText(filtered + " of " + total);
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
            mainContainer.getStyleClass().add("doctors-accent");
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
        // Already on doctors page - no need to reload
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

    private void loadDoctorsFromDatabase() {
        try {
            doctorsData.clear();
            List<com.clinic.app.entities.Doctor> doctorEntities = doctorService.getAllDoctors();

            // Convert entities to models
            for (com.clinic.app.entities.Doctor entity : doctorEntities) {
                Doctor model = new Doctor(
                    entity.getId(),
                    entity.getName(),
                    entity.getSpecialty(),
                    entity.getPhone(),
                    entity.getEmail(),
                    entity.getAvailable()
                );
                doctorsData.add(model);
            }

            System.out.println("Loaded " + doctorsData.size() + " doctors from database");

            // Refresh the table view to ensure UI is updated
            if (doctorsTable != null) {
                doctorsTable.refresh();
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load doctors: " + e.getMessage() + ". Please try again.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        doctorsTable.getColumns().clear();
        TableColumn<Doctor, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idCol.setPrefWidth(70);
        TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setPrefWidth(180);
        TableColumn<Doctor, String> specialtyCol = new TableColumn<>("Specialty");
        specialtyCol.setCellValueFactory(cellData -> cellData.getValue().specialtyProperty());
        specialtyCol.setPrefWidth(150);
        TableColumn<Doctor, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        phoneCol.setPrefWidth(120);
        TableColumn<Doctor, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailCol.setPrefWidth(180);
        TableColumn<Doctor, Boolean> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(cellData -> cellData.getValue().availableProperty());
        availableCol.setCellFactory(column -> new TableCell<Doctor, Boolean>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                } else {
                    setText(available ? "Yes" : "No");
                    if (available) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    }
                }
            }
        });
        availableCol.setPrefWidth(100);
        TableColumn<Doctor, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.getStyleClass().add("edit-button");
                deleteBtn.getStyleClass().add("delete-button");
                editBtn.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    editDoctor(d);
                });
                deleteBtn.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    deleteDoctor(d);
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
        doctorsTable.getColumns().addAll(idCol, nameCol, specialtyCol, phoneCol, emailCol, availableCol, actionsCol);
        doctorsTable.setItems(doctorsData);
    }

    @FXML
    private void saveDoctor() {
        if (!validateForm()) {
            return;
        }
        try {
            if (selectedDoctor == null) {
                // Add new doctor
                Doctor doctorModel = new Doctor(
                        0, // ID will be auto-generated
                        txtName.getText().trim(),
                        txtSpecialty.getText().trim(),
                        txtPhone.getText().trim(),
                        txtEmail.getText().trim(),
                        chkAvailable.isSelected()
                );

                // Convert to entity and save
                com.clinic.app.entities.Doctor doctorEntity = new com.clinic.app.entities.Doctor();
                doctorEntity.setName(doctorModel.getName());
                doctorEntity.setSpecialty(doctorModel.getSpecialty());
                doctorEntity.setPhone(doctorModel.getPhone());
                doctorEntity.setEmail(doctorModel.getEmail());
                doctorEntity.setAvailable(doctorModel.isAvailable());

                boolean success = doctorService.addDoctor(doctorEntity);

                if (success) {
                    loadDoctorsFromDatabase();
                    clearForm();
                    showAlert("Success", "Doctor added successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Database Error", "Failed to save doctor to database. Please try again.", Alert.AlertType.ERROR);
                }
            } else {
                // Update existing doctor
                selectedDoctor.setName(txtName.getText().trim());
                selectedDoctor.setSpecialty(txtSpecialty.getText().trim());
                selectedDoctor.setPhone(txtPhone.getText().trim());
                selectedDoctor.setEmail(txtEmail.getText().trim());
                selectedDoctor.setAvailable(chkAvailable.isSelected());

                // Convert to entity and update
                com.clinic.app.entities.Doctor doctorEntity = new com.clinic.app.entities.Doctor();
                doctorEntity.setId(selectedDoctor.getId());
                doctorEntity.setName(selectedDoctor.getName());
                doctorEntity.setSpecialty(selectedDoctor.getSpecialty());
                doctorEntity.setPhone(selectedDoctor.getPhone());
                doctorEntity.setEmail(selectedDoctor.getEmail());
                doctorEntity.setAvailable(selectedDoctor.isAvailable());

                boolean success = doctorService.updateDoctor(doctorEntity);

                if (success) {
                    loadDoctorsFromDatabase();
                    clearForm();
                    showAlert("Success", "Doctor updated successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Database Error", "Failed to update doctor in database. Please try again.", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            showAlert("Database Error", "Error saving doctor: " + e.getMessage() + ". Please try again.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        selectedDoctor = null;
    }

    @FXML
    private void clearForm() {
        txtName.clear();
        txtSpecialty.clear();
        txtPhone.clear();
        txtEmail.clear();
        chkAvailable.setSelected(true);
        taNotes.clear();
        selectedDoctor = null;
    }

    private void editDoctor(Doctor d) {
        selectedDoctor = d;
        txtName.setText(d.getName());
        txtSpecialty.setText(d.getSpecialty());
        txtPhone.setText(d.getPhone());
        txtEmail.setText(d.getEmail());
        chkAvailable.setSelected(d.isAvailable());
    }

    private void deleteDoctor(Doctor d) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Doctor");
        alert.setContentText("Are you sure you want to delete doctor " + d.getName() + "?\n\nThis action cannot be undone.");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    boolean success = doctorService.deleteDoctor(d.getId());
                    if (success) {
                        loadDoctorsFromDatabase();
                        clearForm();
                        showAlert("Success", "Doctor deleted successfully.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Database Error", "Failed to delete doctor.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "Error deleting doctor: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // Overloaded method for button action
    @FXML
    private void deleteDoctor() {
        if (selectedDoctor != null) {
            deleteDoctor(selectedDoctor);
        } else {
            showAlert("No Selection", "Please select a doctor to delete.", Alert.AlertType.WARNING);
        }
    }

    private boolean validateForm() {
        // Check for empty fields
        if (txtName.getText().trim().isEmpty()) {
            return error("Validation Error", "Please enter doctor name.");
        }

        if (txtSpecialty.getText().trim().isEmpty()) {
            return error("Validation Error", "Please enter doctor specialty.");
        }

        // Validate field lengths
        if (txtName.getText().trim().length() > 100) {
            return error("Validation Error", "Name is too long (max 100 characters).");
        }

        if (txtSpecialty.getText().trim().length() > 100) {
            return error("Validation Error", "Specialty is too long (max 100 characters).");
        }

        if (txtPhone.getText().length() > 20) {
            return error("Validation Error", "Phone number is too long (max 20 characters).");
        }

        if (txtEmail.getText().length() > 100) {
            return error("Validation Error", "Email is too long (max 100 characters).");
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
        filterDoctors(searchText);
        if (filteredDoctors.isEmpty()) {
            showAlert("No Results", "No doctors found matching: '" + searchText + "'.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleClearSearch() {
        txtSearch.clear();
        filterDoctors("");
    }

    @FXML
    private void handleRowClick(MouseEvent e) {
        if (e.getClickCount() == 2) { // Double-click
            Doctor selected = doctorsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                editDoctor(selected);
            }
        }
    }
}