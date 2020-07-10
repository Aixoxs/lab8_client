package ru.ifmo.se.clientUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ru.ifmo.se.client.Client;
import ru.ifmo.se.clientUI.controllers.MainController;
import ru.ifmo.se.commands.ClassCommand;
import ru.ifmo.se.commands.ShowCommand;

public class DashBoardLoader implements Initializable {
    @FXML
    private StackPane rootLayoutPane;
    private ResourceBundle bundle;
    private final MainController mainController;

    public DashBoardLoader(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        Client var10000 = this.mainController.getContext().getClient();
        ClassCommand classCommand = new ShowCommand();
        classCommand.setUser(var10000.getCurrentUser());
        var10000.getMessageWriter().writeRequest(classCommand);
        this.loadSplashScreen();
    }

    public void loadSplashScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/splash.fxml"));
            loader.setResources(this.bundle);
            StackPane splashScreenPane = (StackPane)loader.load();
            this.rootLayoutPane.getChildren().setAll(new Node[]{splashScreenPane});
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(3.0D), splashScreenPane);
            fadeIn.setFromValue(0.0D);
            fadeIn.setToValue(1.0D);
            fadeIn.setCycleCount(1);
            fadeIn.play();
            fadeIn.setOnFinished((e) -> {
                this.loadMainScreen();
            });
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/dashboard.fxml"));
            loader.setController(this.mainController);
            loader.setResources(this.bundle);
            Parent dashboardPane = (Parent)loader.load();
            this.rootLayoutPane.getChildren().setAll(new Node[]{dashboardPane});
        } catch (IOException var3) {
        }

    }
}

