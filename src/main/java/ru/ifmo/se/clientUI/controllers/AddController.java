package ru.ifmo.se.clientUI.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.ifmo.se.clientUI.Context;
import ru.ifmo.se.commands.AddCommand;
import ru.ifmo.se.commands.ClassCommand;
import ru.ifmo.se.commands.UpdateIdCommand;
import ru.ifmo.se.musicians.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddController implements Initializable {
    @FXML
    private JFXDatePicker creationDatePicker;
    @FXML
    private JFXTextField nameTextField;
    @FXML
    private StackPane rootPane;
    @FXML
    private JFXTextField coordinateX;
    @FXML
    private JFXTextField coordinateY;
    @FXML
    private JFXTextField nopTextField;
    @FXML
    private JFXComboBox<Label> genreBox;
    @FXML
    private JFXTextField personNameTextField;
    @FXML
    private JFXTextField heightTextField;
    @FXML
    private JFXComboBox<Label> eyeColorBox;
    @FXML
    private JFXComboBox<Label> hairColorBox;
    @FXML
    private JFXComboBox<Label> nationBox;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private Label idLabel;
    @FXML
    private AnchorPane mainContainer;
    private ResourceBundle bundle;
    private boolean editMode = false;
    private int editingID = -1;
    private Context context;

    public AddController(Context context, boolean editMode) {
        this.context = context;
        this.editMode = editMode;
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

        Color[] var3 = Color.values();
        int var4 = var3.length;

        int var5;
        for(var5 = 0; var5 < var4; ++var5) {
            Color color = var3[var5];
            this.eyeColorBox.getItems().add(new Label(color.name()));
            this.hairColorBox.getItems().add(new Label(color.name()));
        }

        Country[] var6 = Country.values();
        int var7 = var6.length;

        int var8;
        for(var8 = 0; var8 < var7; ++var8) {
            Country country = var6[var8];
            this.nationBox.getItems().add(new Label(country.name()));
        }

        MusicGenre[] var9 = MusicGenre.values();
        int var10 = var9.length;

        int var11;
        for(var11 = 0; var11 < var10; ++var11) {
            MusicGenre musicGenre = var9[var11];
            this.genreBox.getItems().add(new Label(musicGenre.name()));
        }

        initValidators();
    }

    public void initValidators() {
        NumberValidator numValidator = new NumberValidator();
        RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
        this.personNameTextField.getValidators().addAll(requiredValidator);
        this.nameTextField.getValidators().addAll(requiredValidator);
        this.coordinateX.getValidators().addAll(numValidator, requiredValidator);
        this.coordinateY.getValidators().addAll(numValidator, requiredValidator);
        this.heightTextField.getValidators().addAll(numValidator, requiredValidator);
        this.nopTextField.getValidators().addAll(numValidator, requiredValidator);
    }

    @FXML
    private void cancelOperation(ActionEvent event) {
        Stage stage = (Stage)this.rootPane.getScene().getWindow();
        stage.close();
    }

    private boolean validationGetsError(){
        return nameTextField.validate() && validateX() && validateY() && nopTextField.validate() && validateNop() &&
                personNameTextField.validate() && validateHeight();
    }

    private boolean validateHeight(){
        if (!this.heightTextField.validate()) {
            return false;
        } else {
            return Double.parseDouble(this.heightTextField.getText()) > 0;
        }
    }

    private boolean validateNop(){
        if (!this.nopTextField.validate()) {
            return false;
        } else {
            try {
                return Integer.parseInt(this.nopTextField.getText()) > 0;
            }catch (NumberFormatException var11){
                return false;
            }
        }
    }

    private boolean validateX(){
        if (!this.coordinateX.validate()) {
            return false;
        } else {
            try {
                return Long.parseLong(this.coordinateX.getText()) <= 913;
            }catch (NumberFormatException var10){
                return false;
            }
        }
    }

    private boolean validateY(){
        if (!this.coordinateY.validate()) {
            return false;
        } else {
            return Double.parseDouble(this.coordinateY.getText()) >= -224;
        }
    }

    @FXML
    public void addBand(ActionEvent actionEvent) throws InterruptedException {
        if (!this.validationGetsError()) {
            Alert.showSimpleAlert(bundle.getString("tab.main.alert.validation.error.title"), bundle.getString("form.add.validation.msg"));
        } else {
            Color eyeColor = null;
            if (eyeColorBox.getSelectionModel().getSelectedItem()!=null){
                eyeColor = Color.valueOf(eyeColorBox.getSelectionModel().getSelectedItem().getText());
            }
            Color hairColor = null;
            if (hairColorBox.getSelectionModel().getSelectedItem()!=null){
                hairColor = Color.valueOf(hairColorBox.getSelectionModel().getSelectedItem().getText());
            }
            Country nation = null;
            if (nationBox.getSelectionModel().getSelectedItem()!=null){
                nation = Country.valueOf(nationBox.getSelectionModel().getSelectedItem().getText());
            }
            MusicGenre musicGenre = null;
            if (genreBox.getSelectionModel().getSelectedItem()!=null){
                musicGenre = MusicGenre.valueOf(genreBox.getSelectionModel().getSelectedItem().getText());
            }
//            LocalDate localDate = null;
//            if (genreBox.getSelectionModel().getSelectedItem()!=null){
//                localDate = MusicGenre.valueOf(genreBox.getSelectionModel().getSelectedItem().getText());
//            }
            MusicBand musicBand = new MusicBand(nameTextField.getText(), new Coordinates(Long.parseLong(coordinateX.getText()),
                    Double.parseDouble(coordinateY.getText())), Integer.parseInt(nopTextField.getText()),
                    creationDatePicker.getValue(), musicGenre,
                    new Person(personNameTextField.getText(), Double.valueOf(heightTextField.getText()), eyeColor, hairColor, nation));
            if (this.editMode) {
                ClassCommand classCommand = new UpdateIdCommand();
                classCommand.setArgument(editingID);
                classCommand.addBandInput(musicBand);
                classCommand.setUser(context.getClient().getCurrentUser());
                this.sendRequest(classCommand);
                this.editMode = false;
            } else {
                ClassCommand classCommand = new AddCommand();
                classCommand.addBandInput(musicBand);
                classCommand.setUser(context.getClient().getCurrentUser());
                this.sendRequest(classCommand);
            }

        }
    }

    public void cleanEntries() {
        this.idLabel.setText("ID = ?");
        this.personNameTextField.setText("");
        this.nameTextField.setText("");
        this.coordinateX.setText("");
        this.coordinateY.setText("");
        this.nopTextField.setText("");
        this.creationDatePicker.setValue(LocalDate.now());
        this.hairColorBox.getSelectionModel().clearSelection();
        this.eyeColorBox.getSelectionModel().clearSelection();
        this.genreBox.getSelectionModel().clearSelection();
        this.nationBox.getSelectionModel().clearSelection();
        this.heightTextField.setText("");
    }

    public void sendRequest(ClassCommand classCommand) throws InterruptedException {
        Object response = this.context.getClient().sendResponse(classCommand);
        if (response instanceof String) {
//            String result = "";

//            try {
//                result = this.bundle.getString((String)response);
//            } catch (MissingResourceException | NullPointerException var8) {
//                result = (String)response;
//            }

            Alert.showSimpleAlert(bundle.getString("dashboard.alert.request.result"), (String) response);
            this.cleanEntries();
            this.cancelOperation(new ActionEvent());
        }
    }

    public void inflateUI(MusicBand musicBand) {
        if (musicBand == null) {
        } else {
            this.idLabel.setText("ID= " + musicBand.getId().toString());
            this.editingID = musicBand.getId();
            this.nameTextField.setText(musicBand.getName());
            this.coordinateX.setText(Long.valueOf(musicBand.getCoordinates().getX()).toString());
            this.coordinateY.setText(Double.valueOf(musicBand.getCoordinates().getY()).toString());
            this.nopTextField.setText(Integer.valueOf(musicBand.getNumberOfParticipants()).toString());
            this.personNameTextField.setText(musicBand.getFrontMan().getName());
            this.heightTextField.setText(musicBand.getFrontMan().getHeight().toString());
            this.creationDatePicker.setValue(musicBand.getEstablishmentDate());
            if (musicBand.getFrontMan().getEyeColor() != null) {
                this.autoSelectComboBoxValue(this.eyeColorBox, musicBand.getFrontMan().getEyeColor(), (color, colorBoxVal) -> {
                    return color.equals(Enum.valueOf(Color.class, colorBoxVal));
                });
            }
            if (musicBand.getFrontMan().getHairColor() != null) {
                this.autoSelectComboBoxValue(this.hairColorBox, musicBand.getFrontMan().getHairColor(), (color, colorBoxVal) -> {
                    return color.equals(Enum.valueOf(Color.class, colorBoxVal));
                });
            }
            if (musicBand.getFrontMan().getNationality() != null) {
                this.autoSelectComboBoxValue(this.nationBox, musicBand.getFrontMan().getNationality(), (nation, nationBoxVal) -> {
                    return nation.equals(Enum.valueOf(Country.class, nationBoxVal));
                });
            }
            if (musicBand.getGenre() != null) {
                this.autoSelectComboBoxValue(this.genreBox, musicBand.getGenre(), (genre, genreBoxVal) -> {
                    return genre.equals(Enum.valueOf(MusicGenre.class, genreBoxVal));
                });
            }
        }
    }

    private <T> void autoSelectComboBoxValue(JFXComboBox<Label> comboBox, T value, AddController.Func<T, String> f) {

        for (Label t : comboBox.getItems()) {
            if (f.compare(value, t.getText())) {
                comboBox.setValue(t);
            }
        }
    }

    public interface Func<T, V> {
        boolean compare(T var1, V var2);
    }
}
