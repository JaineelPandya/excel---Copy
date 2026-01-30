package com.markytrics.excel;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * JavaFX GUI application for generating Excel access request forms.
 */
public class ExcelGeneratorGUI extends Application {

    private TextField docRefEntry;
    private TextField versionEntry;
    private TextField dateEntry;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Generate Access Form");

        // Create main container
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-size: 12px;");

        // DOC. REF. NO. field
        Label docRefLabel = new Label("DOC. REF. NO.");
        docRefEntry = new TextField();
        String currentYear = String.valueOf(LocalDate.now().getYear());
        docRefEntry.setText("IPACK-PAM-" + currentYear + "-001");
        docRefEntry.setPrefWidth(300);

        // VERSION NO. field
        Label versionLabel = new Label("VERSION NO.");
        versionEntry = new TextField();
        versionEntry.setText("v1.0");
        versionEntry.setPrefWidth(300);

        // DATE field
        Label dateLabel = new Label("DATE (DD-MMM-YYYY)");
        dateEntry = new TextField();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        dateEntry.setText(today.format(formatter));
        dateEntry.setPrefWidth(300);

        // Status label
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: blue;");

        // Submit button
        Button generateButton = new Button("Generate");
        generateButton.setPrefWidth(150);
        generateButton.setStyle("-fx-font-size: 12px; -fx-padding: 10px;");
        generateButton.setOnAction(e -> handleSubmit());

        // Add all components to root
        root.getChildren().addAll(
            docRefLabel, docRefEntry,
            versionLabel, versionEntry,
            dateLabel, dateEntry,
            generateButton,
            statusLabel
        );

        Scene scene = new Scene(root, 420, 320);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Handles the submit button action.
     */
    private void handleSubmit() {
        String docRef = docRefEntry.getText();
        String version = versionEntry.getText();
        String dateVal = dateEntry.getText();

        // Validate inputs
        if (docRef.isEmpty() || version.isEmpty() || dateVal.isEmpty()) {
            showError("Error", "All fields are required");
            return;
        }

        // Validate date format
        if (!isValidDateFormat(dateVal)) {
            showError("Error", "Date format must be DD-MMM-YYYY");
            return;
        }

        try {
            // Generate Excel file
            ExcelProcessor.generateExcel(docRef, version, dateVal);
            showSuccess("Success", "Excel form generated successfully!");
            statusLabel.setText("✓ File generated successfully");
            statusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            showError("Error", "Failed to generate Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates if the date string matches DD-MMM-YYYY format.
     *
     * @param dateStr the date string to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidDateFormat(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            formatter.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Shows an error alert dialog.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a success alert dialog.
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
