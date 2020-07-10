package ru.ifmo.se.clientUI.controllers;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.ifmo.se.clientUI.Context;
import ru.ifmo.se.clientUI.controllers.tabs.CoordTabController;
import ru.ifmo.se.clientUI.controllers.tabs.MainTabController;
import ru.ifmo.se.commands.ClassCommand;
import ru.ifmo.se.commands.RemoveByIdCommand;
import ru.ifmo.se.commands.ShowCommand;
import ru.ifmo.se.model.User;
import ru.ifmo.se.musicians.*;

import java.io.IOException;
import java.net.URL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private StackPane rootLayoutPane;
    @FXML
    private StackPane menubarPane;
    @FXML
    private JFXTabPane mainTabPane;
    @FXML
    private Tab mainTab;
    @FXML
    private Tab mapTab;
    private ResourceBundle bundle;
    Context context;
    private MainTabController mainTabController;
    private MenuBarController menuBarController;
    private CoordTabController coordTabController;

    public MainController(Context context) {
        this.context = context;
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        loadComponents();
    }

    public void loadComponents() {
        try {
            this.menuBarController = new MenuBarController(this);
            this.mainTabController = new MainTabController(this);
            coordTabController = new CoordTabController(this);
            FXMLLoader menuLoader = new FXMLLoader(this.getClass().getResource("/views/menubar.fxml"));
            menuLoader.setController(this.menuBarController);
            menuLoader.setResources(ResourceBundle.getBundle("bundles.BundleLang", this.bundle.getLocale()));
            Parent menuRoot = (Parent) menuLoader.load();
            this.menubarPane.getChildren().addAll(new Node[]{menuRoot});
            FXMLLoader mainLoader = new FXMLLoader(this.getClass().getResource("/views/tabs/table.fxml"));
            mainLoader.setController(this.mainTabController);
            mainLoader.setResources(ResourceBundle.getBundle("bundles.BundleLang", this.bundle.getLocale()));
            Parent mainRoot = (Parent) mainLoader.load();
            this.mainTab.setContent(mainRoot);
            FXMLLoader mapLoader = new FXMLLoader(this.getClass().getResource("/views/tabs/coord.fxml"));
            mapLoader.setController(this.coordTabController);
            mapLoader.setResources(ResourceBundle.getBundle("bundles.BundleLang", this.bundle.getLocale()));
            Parent mapRoot = (Parent) mapLoader.load();
            this.mapTab.setContent(mapRoot);
            this.mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
                if (newTab == this.mainTab) {
                    this.mainTabController.refreshData();
                } else if (newTab == this.mapTab) {
                    this.coordTabController.refreshData();
                }

            });
        } catch (IOException var9) {
            System.err.println(var9);
            System.exit(0);
        }

    }

    public void refreshData(List<MusicBand> musicBands){
        context.getClient().getCollectionManager().getLocalList().clear();
        context.getClient().getCollectionManager().getLocalList().addAll(musicBands);
        if (mainTabController != null && coordTabController != null) {
            mainTabController.refreshData();
            coordTabController.refreshData();
        }
    }


    public Context getContext() {
        return context;
    }

    public void loadEditBandDialog(MusicBand selectedForEdit, boolean editMode, boolean passingKey) {
        if (selectedForEdit == null && editMode) {
            Alert.showSimpleAlert(bundle.getString("dashboard.alert.error.noband.selected.title"),
                    bundle.getString("dashboard.alert.error.noband.selected.content"));
        } else if (selectedForEdit != null && selectedForEdit.getUserId() != getContext().getClient().getCurrentUser().getId()) {
            Alert.showSimpleAlert(this.bundle.getString("dashboard.alert.error.noaccess.title"),
                    this.bundle.getString("dashboard.alert.error.noaccess.content"));
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/add_band.fxml"));
                loader.setResources(ResourceBundle.getBundle("bundles.BundleLang", this.bundle.getLocale()));
                AddController controller = new AddController(context, editMode);
                loader.setResources(this.bundle);
                loader.setController(controller);
                Parent parent = (Parent) loader.load();
                if (editMode || passingKey) {
                    controller.inflateUI(selectedForEdit);
                }

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setTitle(editMode ? bundle.getString("dashboard.alert.edit.title") : bundle.getString("dashboard.alert.insert.title"));
                stage.setScene(new Scene(parent));
                stage.show();
                stage.setOnHiding((e) -> {
                    this.mainTabController.refreshData();
                    this.coordTabController.refreshData();
                });
            } catch (IOException var8) {
                System.err.println("edit/update error" + var8);
            }

        }
    }

    public void removeBand(MusicBand musicBand) {
        if (musicBand == null) {
            Alert.showSimpleAlert(this.bundle.getString("dashboard.alert.error.noband.selected.title"), this.bundle.getString("dashboard.alert.error.noband.selected.content"));
        } else if (musicBand.getUserId() != getContext().getClient().getCurrentUser().getId()) {
            Alert.showSimpleAlert(this.bundle.getString("dashboard.alert.error.noaccess.title"),
                    this.bundle.getString("dashboard.alert.error.noaccess.content"));
        } else {
            ClassCommand classCommand = new RemoveByIdCommand();
            classCommand.setUser(getContext().getClient().getCurrentUser());
            classCommand.setArgument(musicBand.getId());
            Object response = getContext().getClient().sendResponse(classCommand);
            this.mainTabController.refreshData();
            this.coordTabController.refreshData();
            if (response instanceof String) {
                Alert.showSimpleAlert(bundle.getString("tab.main.alert.server.response"), (String) response);
            }
        }
    }

    public void switchLanguage(String languageCode) {
        Locale locale = Locale.forLanguageTag(languageCode);
        this.bundle = ResourceBundle.getBundle("bundles.BundleLang", locale);
        this.loadComponents();
    }

    public void closeWindow() {
        this.context.getClient().setCurrentUser(new User());
        Stage stage = (Stage) this.mainTab.getTabPane().getScene().getWindow();
        stage.close();
    }
}
