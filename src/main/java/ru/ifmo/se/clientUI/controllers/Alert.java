package ru.ifmo.se.clientUI.controllers;

import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

public class Alert {
        public Alert() {
        }

        public static void showSimpleAlert(String title, String content) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText((String)null);
            alert.setContentText(content);
            styleAlert(alert);
            alert.showAndWait();
        }

        public static void showErrorMessage(String title, String content) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(title);
            alert.setContentText(content);
            styleAlert(alert);
            alert.showAndWait();
        }

        private static void styleAlert(javafx.scene.control.Alert alert) {
            Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Alert.class.getClassLoader().getResource("Login.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");
        }
    }

