package comparer.controller;

//*Controller class for settings window*/

import comparer.model.FileComparer;
import comparer.model.FileInfo;
import comparer.model.Filter;
import comparer.util.AppPreferences;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class SettingsController {

    private Stage dialogStage;

    private FileComparer comparer;

    private ResourceBundle resourceBundle;

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField minLengthTextField;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;


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
        this.minLengthTextField.setText(String.valueOf(FileInfo.getMinLength()));
    }


    /**
     * set dialog stage for this window
     *
     * @param dialogStage
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
            FileInfo.setMinLength(Integer.valueOf(this.minLengthTextField.getText()));
            AppPreferences.setMinStringLength(this.minLengthTextField.getText());
            dialogStage.close();
        }
    }

    @FXML
    private void showFilterInfo(){
        Message.info(this.resourceBundle,"FilterInfo");
    }

    @FXML
    private void showMinLengthInfo(){
        Message.info(this.resourceBundle,"MinLengthInfo");
    }

    /*check that user input correct data*/
    private boolean isInputValid() {
        String filterExtensions = this.filterTextField.getText();
        String minLength = this.minLengthTextField.getText();
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


}
