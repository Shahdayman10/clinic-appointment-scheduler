package com.clinic.app.utils;

import com.clinic.app.Models.Appointment;
import com.clinic.app.data.services.AppointmentService;
import com.clinic.app.utils.EntityModelConverter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.concurrent.Task;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Service for managing appointment notifications and reminders
 */
public class AppointmentNotificationService {
    private static final Logger LOGGER = Logger.getLogger(AppointmentNotificationService.class.getName());
    private static AppointmentNotificationService instance;
    private final AppointmentService appointmentService;
    private final ScheduledExecutorService scheduler;
    private boolean notificationsEnabled = true;
    // Notification settings
    private int reminderHours = 24; // Default reminder 24 hours before appointment
    private int checkIntervalMinutes = 30; // Check for reminders every 30 minutes
    private AppointmentNotificationService() {
        this.appointmentService = AppointmentService.getInstance();
        this.scheduler = Executors.newScheduledThreadPool(2);
        startNotificationScheduler();
    }
    public static synchronized AppointmentNotificationService getInstance() {
        if (instance == null) {
            instance = new AppointmentNotificationService();
        }
        return instance;
    }
    /**
     * Start the notification scheduler
     */
    private void startNotificationScheduler() {
        scheduler.scheduleAtFixedRate(
            this::checkForUpcomingAppointments,
            0, // Initial delay
            checkIntervalMinutes,
            TimeUnit.MINUTES
        );
        LOGGER.info("Appointment notification scheduler started");
    }
    /**
     * Check for upcoming appointments and send notifications
     */
    private void checkForUpcomingAppointments() {
        if (!notificationsEnabled) {
            return;
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<Appointment> upcomingAppointments = getUpcomingAppointments();
                    for (Appointment appointment : upcomingAppointments) {
                        if (shouldSendReminder(appointment)) {
                            Platform.runLater(() -> sendAppointmentReminder(appointment));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error checking for upcoming appointments", e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    /**
     * Get upcoming appointments from database
     */
    private List<Appointment> getUpcomingAppointments() {
        try {
            List<com.clinic.app.entities.Appointment> entityAppointments = appointmentService.getAllAppointments();
            return entityAppointments.stream()
                .filter(app -> "scheduled".equalsIgnoreCase(app.getStatus()))
                .filter(app -> !app.getDate().isBefore(LocalDate.now()) &&
                               !app.getDate().isAfter(LocalDate.now().plusDays(2)))
                .map(EntityModelConverter::toAppointmentModel)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get upcoming appointments", e);
            return java.util.Collections.emptyList();
        }
    }
    /**
     * Check if a reminder should be sent for an appointment
     */
    private boolean shouldSendReminder(Appointment appointment) {
        try {
            LocalDate appointmentDate = LocalDate.parse(appointment.getDate());
            LocalTime appointmentTime = LocalTime.parse(appointment.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
            // Calculate hours until appointment
            long hoursUntil = ChronoUnit.HOURS.between(
                LocalDate.now().atTime(LocalTime.now()),
                appointmentDate.atTime(appointmentTime)
            );
            // Send reminder if appointment is within reminder window
            return hoursUntil <= reminderHours && hoursUntil > 0;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error calculating reminder time for appointment: " + appointment.getId(), e);
            return false;
        }
    }
    /**
     * Send appointment reminder notification
     */
    private void sendAppointmentReminder(Appointment appointment) {
        try {
            String title = "Appointment Reminder";
            String message = buildReminderMessage(appointment);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText("Upcoming Appointment");
            alert.setContentText(message);
            alert.show();
            LOGGER.info("Sent reminder for appointment: " + appointment.getId());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send appointment reminder", e);
        }
    }
    /**
     * Build reminder message text
     */
    private String buildReminderMessage(Appointment appointment) {
        StringBuilder message = new StringBuilder();
        try {
            LocalDate appointmentDate = LocalDate.parse(appointment.getDate());
            LocalTime appointmentTime = LocalTime.parse(appointment.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
            long hoursUntil = ChronoUnit.HOURS.between(
                LocalDate.now().atTime(LocalTime.now()),
                appointmentDate.atTime(appointmentTime)
            );
            message.append("Patient: ").append(appointment.getPatientName()).append("\n");
            message.append("Doctor: ").append(appointment.getDoctorName()).append("\n");
            message.append("Date: ").append(formatDate(appointmentDate)).append("\n");
            message.append("Time: ").append(appointment.getTime()).append("\n");
            if (hoursUntil <= 1) {
                message.append("\n⚠️ This appointment is in less than 1 hour!");
            } else if (hoursUntil <= 4) {
                message.append("\n⏰ This appointment is in ").append(hoursUntil).append(" hours");
            } else {
                message.append("\n📅 This appointment is tomorrow");
            }
            if (appointment.getNotes() != null && !appointment.getNotes().trim().isEmpty()) {
                message.append("\n\nNotes: ").append(appointment.getNotes());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error building reminder message", e);
            message.append("Upcoming appointment for ").append(appointment.getPatientName());
        }
        return message.toString();
    }
    /**
     * Format date for display
     */
    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        return date.format(formatter);
    }
    /**
     * Send immediate notification for new appointment
     */
    public void sendAppointmentConfirmation(Appointment appointment) {
        if (!notificationsEnabled) return;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Scheduled");
            alert.setHeaderText("New Appointment Confirmed");
            alert.setContentText(buildConfirmationMessage(appointment));
            alert.show();
        });
    }
    /**
     * Build confirmation message for new appointments
     */
    private String buildConfirmationMessage(Appointment appointment) {
        StringBuilder message = new StringBuilder();
        message.append("✅ Appointment successfully scheduled!\n\n");
        message.append("Patient: ").append(appointment.getPatientName()).append("\n");
        message.append("Doctor: ").append(appointment.getDoctorName()).append("\n");
        message.append("Date: ").append(appointment.getDate()).append("\n");
        message.append("Time: ").append(appointment.getTime()).append("\n");
        if (appointment.getNotes() != null && !appointment.getNotes().trim().isEmpty()) {
            message.append("Notes: ").append(appointment.getNotes()).append("\n");
        }
        message.append("\nReminder will be sent ").append(reminderHours).append(" hours before the appointment.");
        return message.toString();
    }
    /**
     * Check for appointments today and show daily summary
     */
    public void showDailySummary() {
        if (!notificationsEnabled) return;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<Appointment> todayAppointments = getTodayAppointments();
                    if (!todayAppointments.isEmpty()) {
                        Platform.runLater(() -> sendDailySummary(todayAppointments));
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error generating daily summary", e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    /**
     * Get today's appointments
     */
    private List<Appointment> getTodayAppointments() {
        try {
            List<com.clinic.app.entities.Appointment> entityAppointments = appointmentService.getAppointmentsByDate(LocalDate.now());
            return entityAppointments.stream()
                .filter(app -> "scheduled".equalsIgnoreCase(app.getStatus()))
                .map(EntityModelConverter::toAppointmentModel)
                .sorted((a1, a2) -> a1.getTime().compareTo(a2.getTime()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get today's appointments", e);
            return java.util.Collections.emptyList();
        }
    }
    /**
     * Send daily summary notification
     */
    private void sendDailySummary(List<Appointment> appointments) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Daily Appointment Summary");
        alert.setHeaderText("Today's Appointments (" + appointments.size() + ")");
        alert.setContentText(buildDailySummaryMessage(appointments));
        alert.show();
    }
    /**
     * Build daily summary message
     */
    private String buildDailySummaryMessage(List<Appointment> appointments) {
        StringBuilder message = new StringBuilder();
        message.append("📅 ").append(formatDate(LocalDate.now())).append("\n\n");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment app = appointments.get(i);
            message.append(i + 1).append(". ");
            message.append(app.getTime()).append(" - ");
            message.append(app.getPatientName()).append(" with Dr. ");
            message.append(app.getDoctorName()).append("\n");
        }
        if (appointments.size() > 5) {
            message.append("\n... and ").append(appointments.size() - 5).append(" more appointments");
        }
        return message.toString();
    }
    /**
     * Enable or disable notifications
     */
    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
        LOGGER.info("Notifications " + (enabled ? "enabled" : "disabled"));
    }
    /**
     * Set reminder time in hours before appointment
     */
    public void setReminderHours(int hours) {
        if (hours > 0 && hours <= 168) { // Max 1 week
            this.reminderHours = hours;
            LOGGER.info("Reminder time set to " + hours + " hours");
        }
    }
    /**
     * Set check interval in minutes
     */
    public void setCheckIntervalMinutes(int minutes) {
        if (minutes >= 5 && minutes <= 60) { // Between 5 minutes and 1 hour
            this.checkIntervalMinutes = minutes;
            // Restart scheduler with new interval
            restartScheduler();
            LOGGER.info("Check interval set to " + minutes + " minutes");
        }
    }
    /**
     * Restart the notification scheduler
     */
    private void restartScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        startNotificationScheduler();
    }
    /**
     * Get notification settings
     */
    public int getReminderHours() {
        return reminderHours;
    }
    public int getCheckIntervalMinutes() {
        return checkIntervalMinutes;
    }
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }
    /**
     * Shutdown the notification service
     */
    public void shutdown() {
        notificationsEnabled = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Appointment notification service shutdown");
    }
}