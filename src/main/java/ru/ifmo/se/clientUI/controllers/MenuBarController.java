package ru.ifmo.se.clientUI.controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuBarController implements Initializable {
    @FXML
    private Label currentUserLabel;
    @FXML
    private ToggleGroup languageOptions;
    private ResourceBundle bundle;
    private final MainController mainController;

    public MenuBarController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.languageOptions.selectedToggleProperty().addListener(this.switchLanguageListener());
        this.currentUserLabel.setText(bundle.getString("menubar.current.user.title") + ": " +
                this.mainController.getContext().getClient().getCurrentUser().getLogin());
    }

    private ChangeListener<Toggle> switchLanguageListener() {
        return (ov, old_toggle, new_toggle) -> {
            if (this.languageOptions.getSelectedToggle() != null) {
                String selectedID = ((RadioMenuItem)this.languageOptions.getSelectedToggle()).getId();
                this.mainController.switchLanguage(selectedID);
            }

        };
    }

    @FXML
    public void handleMenuLogOut(ActionEvent actionEvent) {
        try {
            this.mainController.closeWindow();
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/login_register.fxml"));
            loader.setController(new LoginRegisterController(mainController.getContext()));
            loader.setResources(ResourceBundle.getBundle("bundles.BundleLang", this.bundle.getLocale()));
            Parent root = (Parent)loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(bundle.getString("login.window.title"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException var5) {
            System.err.println(var5);
        }

    }

    @FXML
    public void handleMenuAddBand(ActionEvent actionEvent) {
        this.mainController.loadEditBandDialog(null, false, false);
    }

    @FXML
    public void handleAboutMenu(ActionEvent actionEvent) {
        Alert.showSimpleAlert(bundle.getString("menubar.help.alert.title"), bundle.getString("menubar.help.alert.content"));
    }
}
