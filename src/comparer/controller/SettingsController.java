package comparer.controller;

import comparer.util.AppPreferences;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

//*Controller class for SettingsWiew.fxml window*/
public class SettingsController {

    /*window stage*/
    private Stage dialogStage;

    /*language pocket*/
    private ResourceBundle resourceBundle;

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
    private Button questionPath;

    /*label for for filter field*/
    @FXML
    private Label filterLbl;

    /*label for for radiobuttons absolutePathRadBtn and relativePathRadBtn*/
    @FXML
    private Label pathLbl;

    /*checkbox for show analyze by letters*/
    @FXML
    private CheckBox analyzeByLetters;

    /*set language pocket*/
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /*set values of class fields*/
    public void setFieldsValues(){
        this.filterTextField.setText(Formatter.getArrayAsString(AppPreferences.getFilterExtensions()));
        this.analyzeByLetters.setSelected(AppPreferences.getAnalyseByLetters());
    }


    /**
     * set dialog stage for this window
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
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
            AppPreferences.setFilterExtensions(extensions);
            AppPreferences.setSettingsWindowHeight(this.dialogStage.getHeight());
            AppPreferences.setSettingsWindowWidth(this.dialogStage.getWidth());
            AppPreferences.setAnalyseByLetters(this.analyzeByLetters.isSelected());
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
    private void showPathInfo(){
        Message.info(this.resourceBundle,"AnalyzeByLettersInfo");
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
        double newHeight = this.dialogStage.getHeight() * 4;
        double newWidth = Formatter.getTextSize(newHeight);
        String newSize = "-fx-font-size:" +  String.valueOf(newWidth) + ";";
        this.filterTextField.setStyle(newSize);
        this.saveBtn.setStyle(newSize);
        this.cancelBtn.setStyle(newSize);
        this.questionFilter.setStyle(newSize);
        this.filterLbl.setStyle(newSize);
        this.pathLbl.setStyle(newSize);
        this.questionPath.setStyle(newSize);
        this.analyzeByLetters.setStyle(newSize);
    };

}
