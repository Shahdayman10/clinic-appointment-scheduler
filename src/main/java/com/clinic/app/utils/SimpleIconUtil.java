package com.clinic.app.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Simple utility class for managing icons in the application.
 * This class provides methods to load and configure icons for use in the UI.
 */
public class SimpleIconUtil {

    // Default icon size
    private static final int DEFAULT_ICON_SIZE = 16;

    /**
     * Creates an ImageView with an icon from the resources
     * @param iconName The name of the icon file (without extension)
     * @return ImageView configured with the icon
     */
    public static ImageView createIcon(String iconName) {
        return createIcon(iconName, DEFAULT_ICON_SIZE);
    }

    /**
     * Creates an ImageView with an icon from the resources with specified size
     * @param iconName The name of the icon file (without extension)
     * @param size The size of the icon
     * @return ImageView configured with the icon
     */
    public static ImageView createIcon(String iconName, int size) {
        return createIcon(iconName, size, "icon");
    }

    /**
     * Creates an ImageView with an icon from the resources with specified size and style class
     * @param iconName The name of the icon file (without extension)
     * @param size The size of the icon
     * @param styleClass CSS style class to apply
     * @return ImageView configured with the icon
     */
    public static ImageView createIcon(String iconName, int size, String styleClass) {
        try {
            // Try to load SVG first
            String svgPath = "/icons/" + iconName + ".svg";
            java.io.InputStream svgStream = SimpleIconUtil.class.getResourceAsStream(svgPath);
            if (svgStream != null) {
                Image image = new Image(svgStream);
                if (!image.isError()) {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(size);
                    imageView.setFitHeight(size);
                    imageView.setPreserveRatio(true);
                    if (styleClass != null && !styleClass.isEmpty()) {
                        imageView.getStyleClass().add(styleClass);
                    }
                    // Apply special styling for doctor and patient icons
                    if (iconName.contains("doctor") || iconName.contains("stethoscope")) {
                        imageView.getStyleClass().add("doctor-icon");
                    } else if (iconName.contains("patient") || iconName.contains("person")) {
                        imageView.getStyleClass().add("patient-icon");
                    }
                    return imageView;
                }
            }

            // Fall back to PNG
            String pngPath = "/icons/" + iconName + ".png";
            Image image = new Image(SimpleIconUtil.class.getResourceAsStream(pngPath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);

            // Apply base style class
            if (styleClass != null && !styleClass.isEmpty()) {
                imageView.getStyleClass().add(styleClass);
            }

            // Apply special styling for doctor and patient icons
            if (iconName.contains("doctor") || iconName.contains("stethoscope")) {
                imageView.getStyleClass().add("doctor-icon");
            } else if (iconName.contains("patient") || iconName.contains("person")) {
                imageView.getStyleClass().add("patient-icon");
            }

            return imageView;
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconName + " - " + e.getMessage());
            ImageView imageView = new ImageView();
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            if (styleClass != null && !styleClass.isEmpty()) {
                imageView.getStyleClass().add(styleClass);
            }
            // Apply special styling for doctor and patient icons
            if (iconName.contains("doctor") || iconName.contains("stethoscope")) {
                imageView.getStyleClass().add("doctor-icon");
            } else if (iconName.contains("patient") || iconName.contains("person")) {
                imageView.getStyleClass().add("patient-icon");
            }
            return imageView; // Return empty ImageView
        }
    }

    /**
     * Creates a navigation icon with the nav-icon style class
     * @param iconName The name of the icon file (without extension)
     * @param size The size of the icon
     * @return ImageView configured with the icon
     */
    public static ImageView createNavigationIcon(String iconName, int size) {
        return createIcon(iconName, size, "nav-icon");
    }

    /**
     * Creates a card icon with the card-icon style class
     * @param iconName The name of the icon file (without extension)
     * @param size The size of the icon
     * @return ImageView configured with the icon
     */
    public static ImageView createCardIcon(String iconName, int size) {
        return createIcon(iconName, size, "card-icon");
    }

    /**
     * Gets the appropriate icon name for each navigation section
     * @param section The section name
     * @return String icon name for the section
     */
    public static String getNavigationIconName(String section) {
        switch (section.toLowerCase()) {
            case "dashboard":
                return "dashboard";
            case "patients":
                return "patients";
            case "doctors":
                return "doctors";
            case "appointments":
                return "appointments";
            case "schedule":
                return "schedule";
            default:
                return "home";
        }
    }
}