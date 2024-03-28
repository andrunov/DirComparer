package comparer.controller;

import comparer.MainApp;
import comparer.model.Settings;
import comparer.style.Skin;
import comparer.util.AppPreferences;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ResourceBundle;

//*Controller class for SettingsWiew.fxml window*/
public class SettingsController {

    private Settings settings;

    /*window stage*/
    private Stage dialogStage;

    /*language pocket*/
    private ResourceBundle resourceBundle;

    private Skin skin;


    /*field for filter text*/
    @FXML
    private TextField filterTextField;

    /*button for save settings and exit*/
    @FXML
    private Button saveBtn;

    /*button for cancel changes and exit*/
    @FXML
    private Button cancelBtn;

    /*button for info for filter field*/
    @FXML
    private Button questionFilter;

    /*button for info for radiobuttons absolutePathRadBtn and relativePathRadBtn*/
    @FXML
    private Button exactWordMatchBtn;

    @FXML
    private Button saveHtmlBtn;

    /*label for for filter field*/
    @FXML
    private Label filterLbl;

    /*label for for radiobuttons absolutePathRadBtn and relativePathRadBtn*/
    @FXML
    private Label option1Lbl;

    @FXML
    private Label option2Lbl;

    /*checkbox for show analyze by letters*/
    @FXML
    private CheckBox exactWordMatchLbl;

    @FXML
    private CheckBox saveHtmlChBox;

    @FXML
    private Label skinLbl;

    @FXML
    private ComboBox<String> skinChoiceBox;


    /*set language pocket*/
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void setMainApp(MainApp mainApp) {
    }

    /*set values of class fields*/
    public void setFieldsValues(){
        this.filterTextField.setText(Formatter.getArrayAsString(this.settings.getAllowedExtensions()));
        this.exactWordMatchLbl.setSelected(this.settings.isExactWordMatch());
        this.saveHtmlChBox.setSelected(this.settings.isWriteHtmlReport());

        ObservableList<String> langs = FXCollections.observableArrayList(Skin.getLocaleValues(this.resourceBundle));
        this.skinChoiceBox.setItems(langs);
        this.skinChoiceBox.setValue(this.settings.getSkin().getLocale(this.resourceBundle));
        this.skinChoiceBox.setOnAction(event -> {
            this.skin = Skin.getByLocalValue(this.resourceBundle, this.skinChoiceBox.getValue());
            this.updateSkin(this.skin.getRepr());
        });
        this.skin = this.settings.getSkin();
        this.updateSkin(this.skin.getRepr());
    }

    private void updateSkin(String skinValue) {
        String newStyle = String.format("comparer/style/%s.css", skinValue);
        this.dialogStage.getScene().getRoot().getStylesheets().clear();
        this.dialogStage.getScene().getRoot().getStylesheets().add(newStyle);
    }


    /**
     * set dialog stage for this window
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public void setSettings(Settings settings) {
        this.settings = settings;
    }


    /**
     * Cancel button click handle
     */
    @FXML
    private void cancel() {
        AppPreferences.setSettingsWindowHeight(this.dialogStage.getHeight());
        AppPreferences.setSettingsWindowWidth(this.dialogStage.getWidth());
        dialogStage.close();
    }

    /**
     * Save button click handle
     */
    @FXML
    private void save() {
        if (isInputValid()) {
            String[] extensions = new String[0];
            if (!Formatter.stringIsEmpty(this.filterTextField.getText())){
                extensions = this.filterTextField.getText().split(" ");
            }
            this.settings.setAllowedExtensions(extensions);
            this.settings.setExactWordMatch(this.exactWordMatchLbl.isSelected());
            this.settings.setWriteHtmlReport(this.saveHtmlChBox.isSelected());
            this.settings.setSkin(this.skin);

            //update main stage skin
            Window primaryStage = this.dialogStage.getOwner();
            String newStyle = String.format("comparer/style/%s.css", this.skin.getRepr());
            primaryStage.getScene().getRoot().getStylesheets().clear();
            primaryStage.getScene().getRoot().getStylesheets().add(newStyle);

            //TODO remove to settings
            AppPreferences.setSettingsWindowHeight(this.dialogStage.getHeight());
            AppPreferences.setSettingsWindowWidth(this.dialogStage.getWidth());
            dialogStage.close();
        }
    }

    /*show info about filter*/
    @FXML
    private void showFilterInfo(){
        Message.info(this.resourceBundle,"FilterInfo");
    }

    /*show info about absolute and relative path*/
    @FXML
    private void showWordMatchInfo(){
        Message.info(this.resourceBundle,"WordMatchInfo");
    }

    @FXML
    public void showSaveHtmlInfo() {
        Message.info(this.resourceBundle,"SaveHtmlInfo");
    }


    /*check that user input correct data*/
    private boolean isInputValid() {
        String filterExtensions = this.filterTextField.getText();
        if ((!filterExtensions.matches("[a-zA-Z0-9\\s]+"))&&(!filterExtensions.isEmpty())){
            Message.errorAlert(this.resourceBundle,"FilterExtensionException");
            return false;
        }
        return true;
    }

    /*listener for observe change height of settings window */
    public ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
    {
        double newHeight = this.dialogStage.getHeight() * 3.5;
        double newWidth = Formatter.getTextSize(newHeight);
        String newSize = "-fx-font-size:" +  String.valueOf(newWidth) + ";";
        this.filterTextField.setStyle(newSize);
        this.exactWordMatchBtn.setStyle(newSize);
        this.saveBtn.setStyle(newSize);
        this.cancelBtn.setStyle(newSize);
        this.saveHtmlBtn.setStyle(newSize);
        this.questionFilter.setStyle(newSize);
        this.filterLbl.setStyle(newSize);
        this.option1Lbl.setStyle(newSize);
        this.option2Lbl.setStyle(newSize);
        this.exactWordMatchLbl.setStyle(newSize);
        this.skinLbl.setStyle(newSize);
        this.skinChoiceBox.setStyle(newSize);
        this.saveHtmlChBox.setStyle(newSize);
    };

}
