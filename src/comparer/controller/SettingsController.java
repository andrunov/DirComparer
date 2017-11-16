package comparer.controller;

import comparer.model.FileComparer;
import comparer.model.FileInfo;
import comparer.model.Filter;
import comparer.util.AppPreferences;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

//*Controller class for SettingsWiew.fxml window*/
public class SettingsController {

    /*window stage*/
    private Stage dialogStage;

    /*file comparer*/
    private FileComparer comparer;

    /*language pocket*/
    private ResourceBundle resourceBundle;

    /*field for filter text*/
    @FXML
    private TextField filterTextField;

    /*field for min length of word*/
    @FXML
    private TextField minLengthWordField;

    /*button for save settings and exit*/
    @FXML
    private Button saveBtn;

    /*button for cancel changes and exit*/
    @FXML
    private Button cancelBtn;

    /*button for info for filter field*/
    @FXML
    private Button questionFilter;

    /*button for info for min length field*/
    @FXML
    private Button questionMinLength;

    /*label for for filter field*/
    @FXML
    private Label filterLbl;

    /*label for for min length field*/
    @FXML
    private Label minLengthLbl;

    /*set language pocket*/
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /*set comparer and associate text fields with its data*/
    public void setComparer(FileComparer comparer) {
        this.comparer = comparer;
        Filter filter = comparer.getFilter();
        if (filter != null) {
            this.filterTextField.setText(Formatter.getArrayAsString(filter.getExtensions()));
        }
        this.minLengthWordField.setText(String.valueOf(FileInfo.getMinLength()));
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
        dialogStage.close();
    }

    /**
     * Save button click handle
     */
    @FXML
    private void save() {
        if (isInputValid()) {
            String[] extensions = this.filterTextField.getText().split(" ");
            Filter filter = new Filter(extensions);
            AppPreferences.setFilterExtensions(extensions);
            this.comparer.setFilter(filter);
            FileInfo.setMinLength(Integer.valueOf(this.minLengthWordField.getText()));
            AppPreferences.setMinStringLength(this.minLengthWordField.getText());
            dialogStage.close();
        }
    }

    /*show info about filter*/
    @FXML
    private void showFilterInfo(){
        Message.info(this.resourceBundle,"FilterInfo");
    }

    /*show info about min length of word*/
    @FXML
    private void showMinLengthInfo(){
        Message.info(this.resourceBundle,"MinLengthInfo");
    }

    /*check that user input correct data*/
    private boolean isInputValid() {
        String filterExtensions = this.filterTextField.getText();
        String minLength = this.minLengthWordField.getText();
        if (!filterExtensions.matches("[a-zA-Z0-9\\s]+")){
            Message.errorAlert(this.resourceBundle,"FilterExtensionException");
            return false;
        }
        try {
            if(Integer.parseInt(minLength)<1) {
                Message.errorAlert(this.resourceBundle, "MinLengthLimitException");
                return false;
            }
        }catch (NumberFormatException e){
            Message.errorAlert(this.resourceBundle,"MinLengthFormatException");
            return false;
        }
        return true;
    }

    /*listener for observe change width and height of main window
    and change font size of buttons and labels */
    public ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
    {
//        this.filterTextField.setStyle("-fx-font-size:"+ Formatter.getTextSize(this.filterTextField)+";");
//        this.minLengthWordField.setStyle("-fx-font-size:"+Formatter.getTextSize(this.minLengthWordField)+";");
        this.saveBtn.setStyle("-fx-font-size:"+Formatter.getTextSize(this.saveBtn)+";");
        this.cancelBtn.setStyle("-fx-font-size:"+Formatter.getTextSize(this.cancelBtn)+";");
        this.questionFilter.setStyle("-fx-font-size:"+Formatter.getTextSize(this.questionFilter)+";");
        this.questionMinLength.setStyle("-fx-font-size:"+Formatter.getTextSize(this.questionMinLength)+";");
        this.filterLbl.setStyle("-fx-font-size:"+Formatter.getTextSize(this.filterLbl)+";");
        this.minLengthLbl.setStyle("-fx-font-size:"+Formatter.getTextSize(this.minLengthLbl)+";");
    };


}
