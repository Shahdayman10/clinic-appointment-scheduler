package com.clinic.app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class AlertUtil {

    /**
     * Show an information alert
     * @param title The alert title
     * @param header The header text (can be null)
     * @param content The content text
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show a warning alert
     * @param title The alert title
     * @param header The header text (can be null)
     * @param content The content text
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show an error alert
     * @param title The alert title
     * @param header The header text (can be null)
     * @param content The content text
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show a confirmation dialog
     * @param title The alert title
     * @param header The header text (can be null)
     * @param content The content text
     * @return true if user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show an input dialog
     * @param title The dialog title
     * @param header The header text (can be null)
     * @param content The content text
     * @param defaultValue The default value for the input field
     * @return The user input, or empty Optional if cancelled
     */
    public static Optional<String> showInput(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        return dialog.showAndWait();
    }

    /**
     * Show a validation error alert for invalid input
     * @param field The field name that has invalid input
     * @param reason The reason why the input is invalid
     */
    public static void showValidationError(String field, String reason) {
        showError("Validation Error", "Invalid " + field, reason);
    }

    /**
     * Show a database error alert
     * @param operation The operation that failed
     * @param error The error message
     */
    public static void showDatabaseError(String operation, String error) {
        showError("Database Error", "Failed to " + operation, error);
    }

    /**
     * Show a success alert
     * @param operation The operation that succeeded
     */
    public static void showSuccess(String operation) {
        showInfo("Success", null, operation + " completed successfully.");
    }
}