package ru.ifmo.se.clientUI.controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.ifmo.se.clientUI.Context;
import ru.ifmo.se.clientUI.DashBoardLoader;
import ru.ifmo.se.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    @FXML
    private TextField login;
    @FXML
    private PasswordField passwordField;
    @FXML
    public ToggleGroup languageOptions;
    private Context context;
    private ResourceBundle bundle;
    private LoginRegisterController loginRegisterController;

    public LoginController(Context context, LoginRegisterController loginRegisterController) {
        this.context = context;
        this.loginRegisterController = loginRegisterController;
    }

    @FXML protected void handleSubmitButtonAction(ActionEvent actionEvent) throws IOException, ClassNotFoundException, InterruptedException {
        String buttonClicked = ((Control)actionEvent.getSource()).getId();
        this.makeUserRequest(getStatus(buttonClicked));
    }

    private ChangeListener<Toggle> switchLanguageListener() {
        return (ov, old_toggle, new_toggle) -> {
            if (this.languageOptions.getSelectedToggle() != null) {
                String selectedID = ((RadioMenuItem)this.languageOptions.getSelectedToggle()).getId();
                this.loginRegisterController.switchLanguage(selectedID);
            }

        };
    }

    private void makeUserRequest(User.Status status) throws IOException, ClassNotFoundException, InterruptedException {
        String loginText = login.getText();
        String passwordText = passwordField.getText();
        if (loginText.equals("") || passwordText.equals("")){
            Alert.showSimpleAlert(bundle.getString("login.alert.error.msg"),bundle.getString("login.alert.error.data"));
        } else {
            User user = new User();
            user.setLogin(loginText);
            user.setPassword(passwordText);
            user.setStatus(status);

            Object answer = context.getClient().sendResponse(user);
            if (answer instanceof String) {
                Alert.showSimpleAlert(bundle.getString("login.alert.error.msg"),(String) answer);
            }else if (answer instanceof User) {
                closeStage();
                context.getClient().setCurrentUser((User) answer);
                loadMain();
            }
        }
    }

    private void loadMain(){
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/main.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.BundleLang", this.bundle.getLocale()));
            MainController mainController = new MainController(context);
            context.getClient().setMainController(mainController);
            loader.setController(new DashBoardLoader(mainController));
            Parent parent = loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Main");
            stage.setScene(new Scene(parent, 1000, 600));
            stage.show();
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    private void closeStage() {
        ((Stage)this.login.getScene().getWindow()).close();
    }

    private User.Status getStatus(String id){
        return id.equals("register") ? User.Status.UNREGISTER : User.Status.UNLOGIN;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        this.languageOptions.selectedToggleProperty().addListener(this.switchLanguageListener());
    }
}
