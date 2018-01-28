package comparer.controller;

import comparer.model.FileComparer;
import comparer.model.FileInfo;
import comparer.util.AppPreferences;
import comparer.util.FileFilter;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
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

    /*radio button for set absolute path in report*/
    @FXML
    private RadioButton absolutePathRadBtn;

    /*radio button for set relative path in report*/
    @FXML
    private RadioButton relativePathRadBtn;

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

    /*set comparer*/
    public void setComparer(FileComparer comparer) {
        this.comparer = comparer;
    }

    /*set values of class fields*/
    public void setFieldsValues(){
        FileFilter fileFilter = comparer.getFilter();
        if (fileFilter != null) {
            this.filterTextField.setText(Formatter.getArrayAsString(fileFilter.getExtensions()));
        }
        this.minLengthWordField.setText(String.valueOf(FileInfo.getMinLength()));
        this.absolutePathRadBtn.setSelected(AppPreferences.getShowAbsolutePath());
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
            String[] extensions = new String[0];
            if (!Formatter.stringIsEmpty(this.filterTextField.getText())){
                extensions = this.filterTextField.getText().split(" ");
            }
            FileFilter filter = new FileFilter(extensions);
            AppPreferences.setFilterExtensions(extensions);
            this.comparer.setFilter(filter);
            FileInfo.setMinLength(Integer.valueOf(this.minLengthWordField.getText()));
            FileInfo.setShowAbsolutePath(this.absolutePathRadBtn.isSelected());
            AppPreferences.setMinStringLength(this.minLengthWordField.getText());
            AppPreferences.setShowAbsolutePath(this.absolutePathRadBtn.isSelected());
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
        if ((!filterExtensions.matches("[a-zA-Z0-9\\s]+"))&&(!filterExtensions.isEmpty())){
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

    /*listener for observe change height of settings window */
    public ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
    {
        double height = this.dialogStage.getHeight();
        this.filterTextField.setStyle("-fx-font-size:"+ Formatter.getTextSize(height)+";");
        this.minLengthWordField.setStyle("-fx-font-size:"+Formatter.getTextSize(height)+";");
        this.saveBtn.setStyle("-fx-font-size:"+Formatter.getTextSize(height)+";");
        this.cancelBtn.setStyle("-fx-font-size:"+Formatter.getTextSize(height)+";");
        this.questionFilter.setStyle("-fx-font-size:"+Formatter.getTextSize(height)+";");
        this.questionMinLength.setStyle("-fx-font-size:"+Formatter.getTextSize(height)+";");
        this.filterLbl.setStyle("-fx-font-size:"+Formatter.getTextSize(height)+";");
        this.minLengthLbl.setStyle("-fx-font-size:"+Formatter.getTextSize(height)+";");
    };

}
