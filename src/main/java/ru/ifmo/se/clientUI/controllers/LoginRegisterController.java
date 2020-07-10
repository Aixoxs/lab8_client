package ru.ifmo.se.clientUI.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import ru.ifmo.se.clientUI.Context;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginRegisterController implements Initializable {
    @FXML
    public AnchorPane rootAnchorPane;
    @FXML
    public JFXTextField username;
    @FXML
    public JFXPasswordField password;
    private ResourceBundle bundle;
    private final Context context;

    public LoginRegisterController(Context context) {
        this.context = context;
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.loadComponents();
    }

    public void loadComponents() {
        try {
            LoginController controller = new LoginController(context, this);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/login.fxml"));
            loader.setController(controller);
            loader.setResources(ResourceBundle.getBundle("bundles.BundleLang", this.bundle.getLocale()));
            Parent menuRoot = (Parent)loader.load();
            this.rootAnchorPane.getChildren().setAll(new Node[]{menuRoot});
        } catch (IOException var4) {
            System.err.println("reloading pane while changing language" + var4);
        }
    }

    public void switchLanguage(String languageCode) {
        Locale locale = Locale.forLanguageTag(languageCode);
        this.bundle = ResourceBundle.getBundle("bundles.BundleLang", locale);
        this.loadComponents();
    }
}
