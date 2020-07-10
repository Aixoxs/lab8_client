package ru.ifmo.se.clientUI.controllers.tabs;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import ru.ifmo.se.client.Reader;
import ru.ifmo.se.clientUI.controllers.Alert;
import ru.ifmo.se.clientUI.controllers.MainController;
import ru.ifmo.se.commands.*;
import ru.ifmo.se.musicians.*;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MainTabController implements Initializable {
    private final ObservableList<MusicBand> musicBands = FXCollections.observableArrayList();
    @FXML
    public GridPane commandsBtnGrid;
    @FXML
    private JFXTextField inputIdBand;
    @FXML
    public JFXTextField inputBandSearch;
    @FXML
    public TableView<MusicBand> musicBandsTableView;
    @FXML
    public TableColumn<MusicBand, Integer> idCol;
    @FXML
    public TableColumn<MusicBand, String> nameCol;
    @FXML
    public TableColumn<MusicBand, Coordinates> coordinatesCol;
    @FXML
    public TableColumn<MusicBand, LocalDateTime> creationDateCol;
    @FXML
    public TableColumn<MusicBand, Long> numberOfParticipantsCol;
    @FXML
    public TableColumn<MusicBand, Color> establishmentDateCol;
    @FXML
    public TableColumn<MusicBand, Integer> genreCol;
    @FXML
    public TableColumn<MusicBand, Person> frontManCol;
    @FXML TableColumn<MusicBand, Integer> userIdCol;
    private ResourceBundle bundle;
    MainController mainController;

    public MainTabController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.initCol();
        this.refreshData();
        this.loadFilteringOption();
        this.inputIdBand.getValidators().addAll(new ValidatorBase[]{new NumberValidator(), new RequiredFieldValidator()});
    }

    public void refreshData() {
        this.musicBands.clear();
        this.musicBands.addAll(mainController.getContext().getClient().getCollectionManager().getLocalList());
        this.musicBandsTableView.setItems(this.musicBands);
        this.loadFilteringOption();
    }

    private void initCol() {
        this.idCol.setCellValueFactory(new PropertyValueFactory("id"));
        this.nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        this.coordinatesCol.setCellValueFactory(new PropertyValueFactory("coordinates"));
        this.creationDateCol.setCellValueFactory(new PropertyValueFactory("creationDate"));
        this.numberOfParticipantsCol.setCellValueFactory(new PropertyValueFactory("numberOfParticipants"));
        this.establishmentDateCol.setCellValueFactory(new PropertyValueFactory("establishmentDate"));
        this.genreCol.setCellValueFactory(new PropertyValueFactory("genre"));
        this.frontManCol.setCellValueFactory(new PropertyValueFactory("frontMan"));
        this.userIdCol.setCellValueFactory(new PropertyValueFactory("userId"));
    }

    public void loadFilteringOption() {
        FilteredList<MusicBand> filteredData = new FilteredList<>(this.musicBands, (s) -> true);
        this.inputBandSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate((musicBand) -> {
                if (newVal != null && newVal.length() != 0) {
                    String writtenText = newVal.toLowerCase();
                    if (String.valueOf(musicBand.getNumberOfParticipants()).contains(writtenText)) {
                        return true;
                    } else if (String.valueOf(musicBand.getId()).contains(writtenText)) {
                        return true;
                    } else {
                        return musicBand.getName().toLowerCase().contains(writtenText);
                    }
                } else {
                    return true;
                }
            });
        });
        SortedList<MusicBand> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(this.musicBandsTableView.comparatorProperty());
        this.musicBandsTableView.setItems(sortedData);
    }

    @FXML public void handleCommandByIdButtonAction(ActionEvent actionEvent){
        String commandName = ((Control)actionEvent.getSource()).getId().trim().toUpperCase();
        ClassCommand classCommand = mainController.getContext().getReader().commandFactory(CommandName.valueOf(commandName));
        classCommand.setUser(mainController.getContext().getClient().getCurrentUser());
        String id = this.inputIdBand.getText();
        if (!inputIdBand.validate() || mainController.getContext().getClient().getCollectionManager().getByID(Integer.parseInt(id)) == null){
            Alert.showSimpleAlert(bundle.getString("tab.main.alert.validation.error.title"),
                    bundle.getString("tab.main.alert.validation.error.content"));
        }else {
            if (commandName.equals("UPDATE")){
                MusicBand musicBand = mainController.getContext().getClient().getCollectionManager().getByID(Integer.parseInt(id));
                mainController.loadEditBandDialog(musicBand, true, false);
            }else {
                MusicBand musicBand = mainController.getContext().getClient().getCollectionManager().getByID(Integer.parseInt(id));
                classCommand.addBandInput(musicBand);
                classCommand.setArgument(musicBand.getId());
                sendRequest(classCommand);
            }
        }
    }

    void sendRequest(Object object){
        Object response = mainController.getContext().getClient().sendResponse(object);
        if (response instanceof String){
            Alert.showSimpleAlert(bundle.getString("tab.main.alert.server.response"), (String) response);
        }else if (response instanceof List){
            StringBuilder result = new StringBuilder();
            ((List)response).forEach((o) -> result.append(o.toString()).append("\n"));
            Alert.showSimpleAlert(bundle.getString("tab.main.alert.server.response"), result.toString());
        }
    }


    @FXML protected void handleInfoButtonAction(){
        ClassCommand classCommand = new InfoCommand();
        classCommand.setUser(mainController.getContext().getClient().getCurrentUser());
        sendRequest(classCommand);
    }

    @FXML protected void handleExecuteScriptButtonAction(){
        TextInputDialog dialog = new TextInputDialog("example.txt");
        dialog.setTitle("execute_script");
        String content = MessageFormat.format(this.bundle.getString("dashboard.alert.commandwithfile.content"), "execute_script");
        dialog.setHeaderText(content);
        dialog.setContentText(this.bundle.getString("dashboard.alert.commandwithfile.input.label"));
        Optional<String> answer = dialog.showAndWait();
        if (answer.isPresent()) {
            ClassCommand classCommand = new ExecuteScriptCommand();
            classCommand.setUser(mainController.getContext().getClient().getCurrentUser());
            Object file = Reader.readFile((String)answer.get());
            if (file instanceof File){
                Scanner scanner;
                ArrayList<ClassCommand> classCommands = new ArrayList<>();
                try {
                    scanner = new Scanner((File) file);
                    while (scanner.hasNextLine()) {
                        classCommands.add(mainController.getContext().getReader().readCommand(scanner));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                classCommand.setArgument(classCommands);
                sendRequest(classCommand);
            }else {
                Alert.showSimpleAlert(this.bundle.getString("dashboard.alert.error.remove.cancelled.title"), (String) file);
            }
        } else {
            Alert.showSimpleAlert(this.bundle.getString("dashboard.alert.error.remove.cancelled.title"), this.bundle.getString("dashboard.alert.error.remove.cancelled.content"));
        }
    }

    @FXML protected void handleClearButtonAction(){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(this.bundle.getString("dashboard.alert.command.clear.title"));
        alert.setContentText(this.bundle.getString("dashboard.alert.command.clear.content"));
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            ClassCommand classCommand = new ClearCommand();
            classCommand.setUser(mainController.getContext().getClient().getCurrentUser());
            sendRequest(classCommand);
        }
    }

    @FXML protected void handleExitButtonAction(){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(this.bundle.getString("dashboard.alert.command.exit.title"));
        alert.setContentText(this.bundle.getString("dashboard.alert.command.exit.content"));
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            ClassCommand classCommand = new ExitCommand();
            classCommand.setUser(mainController.getContext().getClient().getCurrentUser());
            mainController.getContext().getClient().getMessageWriter().writeRequest(classCommand);
            System.exit(-1);
        }
    }

    @FXML protected void handleRefresh(){
        ClassCommand classCommand = new ShowCommand();
        classCommand.setUser(mainController.getContext().getClient().getCurrentUser());
        mainController.getContext().getClient().sendResponse(classCommand);
        refreshData();
    }

    @FXML protected void handleBandEdit(){
        MusicBand selectedForEdit = (MusicBand) this.musicBandsTableView.getSelectionModel().getSelectedItem();
        this.mainController.loadEditBandDialog(selectedForEdit, true, false);
    }

    @FXML protected void handleCommandFilterBandButtonAction(){
        String nop = this.inputIdBand.getText();
        if (!inputIdBand.validate()){
            Alert.showSimpleAlert(bundle.getString("tab.main.alert.validation.error.title"),
                    bundle.getString("tab.main.alert.validation.error.content"));
        } else {
            this.musicBands.clear();
            this.musicBands.addAll(mainController.getContext().getClient().getCollectionManager().getLocalList().stream().
                    filter((o) -> o.getNumberOfParticipants() < Integer.parseInt(nop)).collect(Collectors.toList()));
            this.musicBandsTableView.setItems(this.musicBands);
            this.loadFilteringOption();
        }
    }

    @FXML protected void handleBandRemove(){
        MusicBand musicBand = (MusicBand) this.musicBandsTableView.getSelectionModel().getSelectedItem();
        mainController.removeBand(musicBand);
    }

    @FXML protected void handleAddButtonAction(){
        mainController.loadEditBandDialog(null, false, false);
    }
    @FXML protected void handleHistoryButtonAction(){
        ClassCommand classCommand = new HistoryCommand();
        classCommand.setUser(mainController.getContext().getClient().getCurrentUser());
        sendRequest(classCommand);
    }
}
