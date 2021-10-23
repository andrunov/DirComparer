package comparer.controller;

import comparer.model.FileComparer;
import comparer.model.FileInfo;
import comparer.util.AppPreferences;
import comparer.util.FileFilter;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    /*button for info for radiobuttons absolutePathRadBtn and relativePathRadBtn*/
    @FXML
    private Button questionPath;

    /*label for for filter field*/
    @FXML
    private Label filterLbl;

    /*label for for min length field*/
    @FXML
    private Label minLengthLbl;

    /*label for for radiobuttons absolutePathRadBtn and relativePathRadBtn*/
    @FXML
    private Label pathLbl;

    /*label for for checkbox showSimilarityMiddle*/
    @FXML
    private Label showMiddleLbl;

    /*label for for checkbox showSimilarityLow*/
    @FXML
    private Label showLowLbl;

    /*checkbox for show middle similarity boolean*/
    @FXML
    private CheckBox showSimilarityMiddle;

    /*checkbox for show low similarity boolean*/
    @FXML
    private CheckBox showSimilarityLow;

    /*checkbox for show analyze by letters*/
    @FXML
    private CheckBox analyzeByLetters;

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
        this.showSimilarityMiddle.setSelected(this.comparer.isShowSimilarityMiddle());
        this.showSimilarityLow.setSelected(this.comparer.isShowSimilarityLow());
        this.analyzeByLetters.setSelected(this.comparer.isAnalyzeByLetters());
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
            FileFilter filter = new FileFilter(extensions);
            AppPreferences.setFilterExtensions(extensions);
            this.comparer.setFilter(filter);
            this.comparer.setShowSimilarityMiddle(this.showSimilarityMiddle.isSelected());
            this.comparer.setShowSimilarityLow(this.showSimilarityLow.isSelected());
            this.comparer.setAnalyzeByLetters(this.analyzeByLetters.isSelected());
            FileInfo.setMinLength(Integer.valueOf(this.minLengthWordField.getText()));
            AppPreferences.setMinStringLength(this.minLengthWordField.getText());
            AppPreferences.setSettingsWindowHeight(this.dialogStage.getHeight());
            AppPreferences.setSettingsWindowWidth(this.dialogStage.getWidth());
            AppPreferences.setShowSimilarityMiddle(this.showSimilarityMiddle.isSelected());
            AppPreferences.setShowSimilarityLow(this.showSimilarityLow.isSelected());
            AppPreferences.setAnalyseByLetters(this.analyzeByLetters.isSelected());
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

    /*show info about absolute and relative path*/
    @FXML
    private void showPathInfo(){
        Message.info(this.resourceBundle,"AnalyzeByLettersInfo");
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
        double newHeight = this.dialogStage.getHeight() * 0.9;
        double newWidth = Formatter.getTextSize(newHeight);
        String newSize = "-fx-font-size:" +  String.valueOf(newWidth) + ";";
        this.filterTextField.setStyle(newSize);
        this.minLengthWordField.setStyle(newSize);
        this.saveBtn.setStyle(newSize);
        this.cancelBtn.setStyle(newSize);
        this.questionFilter.setStyle(newSize);
        this.questionMinLength.setStyle(newSize);
        this.filterLbl.setStyle(newSize);
        this.minLengthLbl.setStyle(newSize);
        this.pathLbl.setStyle(newSize);
        this.questionPath.setStyle(newSize);
        this.showSimilarityMiddle.setStyle(newSize);
        this.showSimilarityLow.setStyle(newSize);
        this.analyzeByLetters.setStyle(newSize);
        this.showMiddleLbl.setStyle(newSize);
        this.showLowLbl.setStyle(newSize);
    };

}
