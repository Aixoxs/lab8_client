package ru.ifmo.se.clientUI.controllers.tabs;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ru.ifmo.se.clientUI.canvas.ResizeBandCanvas;
import ru.ifmo.se.clientUI.canvas.ResizeCanvas;
import ru.ifmo.se.clientUI.canvas.ResizeCoordCanvas;
import ru.ifmo.se.clientUI.controllers.Alert;
import ru.ifmo.se.clientUI.controllers.MainController;
import ru.ifmo.se.musicians.MusicBand;
import ru.ifmo.se.musicians.MusicGenre;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CoordTabController implements Initializable {
    @FXML
    public Pane wrapperCoordPane;
    @FXML
    public Pane bandPicturePane;
    @FXML
    public JFXButton editBandButton;
    @FXML
    public JFXButton removeBandButton;
    @FXML
    public GridPane bandDetailsGrid;
    private ResizeCanvas bandsMapCanvas;
    private ResizeCanvas bandPictureCanvas;
    private ResourceBundle bundle;
    private final MainController mainController;
    private final ArrayList<MusicBand> musicBands = new ArrayList<>();
    private MusicBand selectedBand = null;

    public CoordTabController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        int actualUserID = this.mainController.getContext().getClient().getCurrentUser().getId();
        this.bandsMapCanvas = new ResizeCoordCanvas(this.musicBands, actualUserID);
        this.wrapperCoordPane.getChildren().add(this.bandsMapCanvas);
        this.bandsMapCanvas.widthProperty().bind(this.wrapperCoordPane.widthProperty());
        this.bandsMapCanvas.heightProperty().bind(this.wrapperCoordPane.heightProperty());
        this.bandsMapCanvas.setOnMouseClicked((event) -> {
            this.selectedBand = (MusicBand) this.bandsMapCanvas.findObj(event.getSceneX(), event.getSceneY());
            this.handleDetailBand();
        });
        this.bandPictureCanvas = new ResizeBandCanvas();
        this.bandPicturePane.getChildren().add(this.bandPictureCanvas);
        this.bandPictureCanvas.widthProperty().bind(this.bandPicturePane.widthProperty());
        this.bandPictureCanvas.heightProperty().bind(this.bandPicturePane.heightProperty());
        this.refreshData();
    }

    public void refreshData() {
        this.musicBands.clear();
        this.musicBands.addAll(this.mainController.getContext().getClient().getCollectionManager().getLocalList());
        this.bandsMapCanvas.setObj(this.mainController.getContext().getClient().getCollectionManager().getLocalList());
        this.bandsMapCanvas.draw();
        if (this.selectedBand != null) {
            this.updateBandDetails();
        }

    }

    public void updateBandDetails() {
        int oldselectedBandID = this.selectedBand.getId();
        this.selectedBand = this.mainController.getContext().getClient().getCollectionManager().getByID(oldselectedBandID);
        if (this.selectedBand != null) {
            this.handleDetailBand();
        } else {
            Platform.runLater(() -> {
                this.bandDetailsGrid.getChildren().clear();
                this.bandPictureCanvas.setObj((Object) null);
                this.bandPictureCanvas.draw();
            });
        }

    }

    public void handleDetailBand() {
        if (this.selectedBand != null) {
            this.changeStatusActionButtons(this.selectedBand.getUserId());

            this.loadingBandPicture();
            this.loadingBandFields();

        }
    }

    private void changeStatusActionButtons(int userID) {
        int currentUserID = this.mainController.getContext().getClient().getCurrentUser().getId();
        if (currentUserID != 1 && currentUserID != userID) {
            this.editBandButton.setDisable(true);
            this.removeBandButton.setDisable(true);
        } else {
            this.editBandButton.setDisable(false);
            this.removeBandButton.setDisable(false);
        }
    }

    private void loadingBandPicture() {
        this.bandPictureCanvas.setObj(this.selectedBand);
        this.bandPictureCanvas.draw();
        MusicGenre type = selectedBand.getGenre() == null ? MusicGenre.BLUES : selectedBand.getGenre();
        if (type.equals(MusicGenre.BLUES)) {
            this.bandPicturePane.setStyle("-fx-background-color: #CD853F");
        } else if (type.equals(MusicGenre.JAZZ)) {
            this.bandPicturePane.setStyle("-fx-background-color: #00FFFF");
        } else if (type.equals(MusicGenre.MATH_ROCK)) {
            this.bandPicturePane.setStyle("-fx-background-color: #D3D3D3");
        } else if (type.equals(MusicGenre.K_POP)) {
            this.bandPicturePane.setStyle("-fx-background-color: #800000");
        }

//        RotateTransition rotateTransition = new RotateTransition();
//        rotateTransition.setDuration(Duration.millis(200.0D));
//        rotateTransition.setNode(this.bandPictureCanvas);
//        rotateTransition.setCycleCount(1);
//        rotateTransition.setAutoReverse(false);
//        rotateTransition.play();

        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(1500), bandPicturePane);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(1.0);
        fadeInTransition.setCycleCount(1);
        fadeInTransition.setAutoReverse(false);
        fadeInTransition.play();

    }

    private void loadingBandFields() {
        Platform.runLater(() -> {
            this.bandDetailsGrid.getChildren().clear();
            int x = 0;
            int y = 0;
            Field[] var3 = this.selectedBand.getClass().getDeclaredFields();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Field bandField = var3[var5];
                if (!Modifier.isStatic(bandField.getModifiers())) {
                    bandField.setAccessible(true);
                    String str = bandField.getName();//this.bundle.getString("tab.main.table.col." + bandField.getName());
                    Label attrTemp = null;
                    try {
                        attrTemp = new Label(str + ": " + bandField.get(this.selectedBand));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    attrTemp.getStyleClass().add("simple-text");
                    if (x == 2) {
                        ++y;
                        x = 0;
                    }

                    this.bandDetailsGrid.add(attrTemp, x, y);
                    ++x;
                }
            }
        });
    }

    @FXML
    public void handleEditBandButtonAction(ActionEvent actionEvent) {
        this.mainController.loadEditBandDialog(this.selectedBand, true, false);
    }

    @FXML
    public void handleRemoveBandButtonAction(ActionEvent actionEvent) {
        this.mainController.removeBand(this.selectedBand);
    }
}
