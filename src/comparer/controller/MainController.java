package comparer.controller;

/*Controller class for main window*/

import comparer.MainApp;
import comparer.model.FileComparer;
import comparer.util.AppPreferences;
import comparer.util.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label firstDirLbl;

    @FXML
    private Label secondDirLbl;

    @FXML
    private Label infoLbl;

    @FXML
    private Label resultLbl;

    @FXML
    private Button firstDirSelectBtn;

    @FXML
    private Button secondDirSelectBtn;

    @FXML
    private Button changeLocalButton;

    @FXML
    private Button executeButton;

    @FXML
    private Button openResultBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Button settingsBtn;

    private ResourceBundle resourceBundle;

    private File firstDirectory;

    private File secondDirectory;

    /*reference to compare engine class*/
    private FileComparer comparer;

    /* Reference to the main application*/
    private MainApp mainApp;

    /*desctop uses for open files just from JavaFX application*/
    private Desktop desktop;

    public MainController() {
        this.comparer = new FileComparer();
        if (Desktop.isDesktopSupported()) {
            this.desktop = Desktop.getDesktop();
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void choseFirstDirectory(){
        File directory = chooseDirectory();
        if (directory != null) {
            this.firstDirectory = directory;
            AppPreferences.setDirectory(directory.getParentFile());
            setTextDirLabel(firstDirLbl, "FirstDirectory", getDirInfo(directory));
            updateTextInfoLbl();
        }
    }


    @FXML
    private void choseSecondDirectory(){
        File directory = chooseDirectory();
        if (directory != null) {
            this.secondDirectory = directory;
            AppPreferences.setDirectory(directory.getParentFile());
            setTextDirLabel(secondDirLbl, "SecondDirectory", "" + getDirInfo(directory));
            updateTextInfoLbl();
        }
    }

    @FXML
    private void execute(){
        if (firstDirectory != null) {
            this.comparer.setStartDirectoryName(firstDirectory.getAbsolutePath());
        }
        if (secondDirectory != null) {
            this.comparer.setEndDirectoryName(secondDirectory.getAbsolutePath());
        }
        this.comparer.setResourceBundle(this.resourceBundle);
        if (this.comparer.execute()) {
            setTextDirLabel(resultLbl, "Result", " " + this.comparer.getReportName());
            setVisibility(true);
        }
    }

    private File chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(AppPreferences.getDirectory());
        return directoryChooser.showDialog(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        setVisibility(false);
    }

    @FXML
    private void changeLocale(){
        if (this.resourceBundle.getLocale().getLanguage().equalsIgnoreCase("ru")){
            this.resourceBundle = ResourceBundle.getBundle("comparer.resources.bundles.Locale",new Locale("en"));
        }else {
            this.resourceBundle = ResourceBundle.getBundle("comparer.resources.bundles.Locale",new Locale("ru"));
        }
        updateLocalText();
    }

    private void updateLocalText(){
        updateTextInfoLbl();
        this.firstDirSelectBtn.setText(resourceBundle.getString("Select"));
        this.secondDirSelectBtn.setText(resourceBundle.getString("Select"));
        this.changeLocalButton.setText(resourceBundle.getString("ChangeLocal"));
        this.executeButton.setText(resourceBundle.getString("Compare"));
        this.clearBtn.setText(resourceBundle.getString("Clear"));
        this.openResultBtn.setText(resourceBundle.getString("Open"));
        this.settingsBtn.setText(resourceBundle.getString("Settings"));
    }

    /*updates text for infoLbl Label depending of
    * firstDirectory and secondDirectory directories*/
    private void updateTextInfoLbl(){
        setTextDirLabel(firstDirLbl,"FirstDirectory",getDirInfo(firstDirectory));
        setTextDirLabel(secondDirLbl,"SecondDirectory",getDirInfo(secondDirectory));
        setTextDirLabel(resultLbl,"Result"," " + this.comparer.getReportName());
        if ((firstDirectory ==null)&&(secondDirectory ==null)){
            infoLbl.setText(resourceBundle.getString("InfoDefault"));
        }
        else if ((firstDirectory ==null)||(secondDirectory ==null)){
            infoLbl.setText(resourceBundle.getString("CompareSingleDirectory"));
        }else if(firstDirectory.equals(secondDirectory)){
            infoLbl.setText(resourceBundle.getString("CompareSingleDirectory"));
        }
        else {
            infoLbl.setText(resourceBundle.getString("CompareTwoDirectories"));
        }
    }

    /*updates text for several Labels*/
    private void setTextDirLabel(Label label, String bundleKey, String infoPath){
         label.setText(resourceBundle.getString(bundleKey) + infoPath);
    }

    private String getDirInfo(File directory){
        String result = "";
        if (directory != null) {
            if(directory.getParent() == null){
                result = ": " + directory.getAbsolutePath();
            }else {
                result = ": " + directory.getName();
            }
        }
        return result;
    }

    @FXML
    private void openResult(){
        try {
            assert this.desktop != null;
            this.desktop.open(new File(this.comparer.getReportName()));
        } catch (Exception e) {
            Message.errorAlert(this.resourceBundle,e.getMessage());
        }
    }

    private void setVisibility(boolean visibility){
        resultLbl.setVisible(visibility);
        openResultBtn.setVisible(visibility);
    }

    @FXML
    private void clear(){
        this.comparer.cleanFields();
        this.firstDirectory = null;
        this.secondDirectory = null;
        updateTextInfoLbl();
        setVisibility(false);
    }

    @FXML
    private void openSettings(){
        mainApp.showSettingsEditDialog(this.resourceBundle, this.comparer);
    }


}
