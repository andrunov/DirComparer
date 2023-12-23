package comparer.controller;


import comparer.MainApp;
import comparer.RowTableData;
import comparer.model.FileComparer;
import comparer.model.FileInfo;
import comparer.util.AppPreferences;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/*controller for MainView.fxml window*/
public class MainController implements Initializable {

    @FXML
    private TextField fileNameTextField;

    /*label of first directory*/
    @FXML
    private Label firstDirLbl;

    @FXML
    private Button goBtn;

    /*button for firs directory selection*/
    @FXML
    private Button firstDirSelectBtn;

    /*button for change language pocket*/
    @FXML
    private Button changeLocalButton;

    /*button for exit application*/
    @FXML
    private Button openResultBtn;

    /*button for clear resources to default*/
    @FXML
    private Button clearBtn;

    /*button for open settings window*/
    @FXML
    private Button settingsBtn;

    /*button for open application info window*/
    @FXML
    private Button aboutBtn;

    /*button for exit application*/
    @FXML
    private Button exitBtn;

    @FXML
    private TableView tableResult;

    /*language pocket*/
    private ResourceBundle resourceBundle;

    /*first choose directory for comparing*/
    private File firstDirectory;

    /*second choose directory for comparing*/
    private File secondDirectory;

    /*reference to compare engine class*/
    private FileComparer comparer;

    /* Reference to the main application*/
    private MainApp mainApp;

    /*desktop uses for open files just from JavaFX application*/
    private Desktop desktop;

    /*constructor*/
    public MainController() {
        this.comparer = new FileComparer();
        if (Desktop.isDesktopSupported()) {
            this.desktop = Desktop.getDesktop();
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /*choose first directory*/
    @FXML
    private void choseFirstDirectory(){
        File initialDirectory = this.firstDirectory == null ? null : this.firstDirectory.getParentFile();
        File directory = chooseDirectory(initialDirectory, "firstDirectory");
        if (directory != null) {
            this.firstDirectory = directory;
            AppPreferences.setDirectory(directory.getParentFile(), "firstDirectory");
            setTextDirLabel(this.firstDirLbl, "FirstDirectory", getDirInfo(directory));
            updateTextInfoLbl();
        }
    }

    @FXML
    private void onEnter() {
        this.executeSearch();
    }

    /*start comparing procedure*/
    @FXML
    private void executeSearch(){

        this.tableResult.getItems().clear();

        if (this.firstDirectory != null) {
            this.comparer.setStartDirectoryName(this.firstDirectory.getAbsolutePath());
        }


        String searchPhrase = this.fileNameTextField.getText().trim();
        if (searchPhrase.isEmpty()) {
            this.comparer.setFileToSearch(null);
        } else {
            this.comparer.setFileToSearch(new FileInfo(searchPhrase));
            //TODO remove later
            this.comparer.setEndDirectoryName(searchPhrase);
        }


        this.comparer.setResourceBundle(this.resourceBundle);
        try{
            if(this.comparer.search()) {
                //setTextDirLabel(this.resultLbl, "Result", getFileInfo(this.comparer.getReportName()));
                setVisibility(true);
                this.tableResult.getItems().addAll(this.comparer.getReport());
                this.comparer.clean();
            }
        }
        catch (Exception e){
            Message.errorAlert(this.resourceBundle,"Error: ", e);
            e.printStackTrace();
        }
    }


    private void addDataToTable() {
        this.tableResult.getItems().addAll(this.comparer.getReport());
    }

    /*open dialog to choose directory*/
    private File chooseDirectory(File initialDirectory, String directoryKey) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (initialDirectory == null) {
            initialDirectory = AppPreferences.getDirectory(directoryKey);
        }
        if ((initialDirectory != null)&&(initialDirectory.exists())) {
            directoryChooser.setInitialDirectory(initialDirectory);
        }
        return directoryChooser.showDialog(null);
    }

    /*initialize language pocket and set visibility to window elements*/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        setVisibility(false);
    }

    /*change pocket language*/
    @FXML
    private void changeLocale(){
        if (this.resourceBundle.getLocale().getLanguage().equalsIgnoreCase("ru")){
            this.resourceBundle = ResourceBundle.getBundle("comparer.resources.bundles.Locale",new Locale("en"));
        }else {
            this.resourceBundle = ResourceBundle.getBundle("comparer.resources.bundles.Locale",new Locale("ru"));
        }
        updateLocalText();
    }

    /*update text of window elements*/
    private void updateLocalText(){
        updateTextInfoLbl();
        this.firstDirSelectBtn.setText(this.resourceBundle.getString("Select"));
        this.changeLocalButton.setText(this.resourceBundle.getString("ChangeLocal"));
        this.clearBtn.setText(this.resourceBundle.getString("Clear"));
        this.openResultBtn.setText(this.resourceBundle.getString("Open"));
        this.settingsBtn.setText(this.resourceBundle.getString("Settings"));
        this.aboutBtn.setText(this.resourceBundle.getString("AppInfo"));
        this.exitBtn.setText(this.resourceBundle.getString("Exit"));
        this.updateResultTable();
    }

    /*updates text for infoLbl Label depending of
    * firstDirectory and secondDirectory directories*/
    private void updateTextInfoLbl(){
        setTextDirLabel(firstDirLbl,"FirstDirectory",getDirInfo(firstDirectory));
        String reportName = this.comparer.getReportName();
    }

    /*updates text for several Labels*/
    private void setTextDirLabel(Label label, String bundleKey, String infoPath){
         label.setText(resourceBundle.getString(bundleKey) + infoPath);
    }

    /**/
    private String getDirInfo(File directory){
        String result = "";
        if (directory != null) {
            result = ": " + directory.getPath();
        }
        return result;
    }

    /*return string-represent directory name with closest parent directory*/
    private String getFileInfo(String filePath){
        String result = "";
        File file = new File(filePath);
        if (file.exists()){
            result = ": " + file.getParentFile().getPath() + "\\" + file.getName();
        }
        return result;
    }

    /*open saved txt-result file*/
    @FXML
    private void openResult(){
        try {
            assert this.desktop != null;
            this.desktop.open(new File(this.comparer.getReportName()));
        } catch (Exception e) {
            Message.errorAlert(this.resourceBundle, "Error in MainController.openResult() ", e);
        }
    }

    /*set visibility to open result button and label*/
    private void setVisibility(boolean visibility){
        this.openResultBtn.setVisible(visibility);
    }

    /*clear fields to default*/
    @FXML
    private void clear(){
        this.comparer.clean();
        this.comparer.setReportName(null);
        this.firstDirectory = null;
        this.secondDirectory = null;
        this.tableResult.getItems().clear();
        updateTextInfoLbl();
        setVisibility(false);
    }

    /*open settings window*/
    @FXML
    private void openSettings(){
        mainApp.showSettingsEditDialog(this.resourceBundle, this.comparer);
    }

    /*show application info*/
    @FXML
    private void showAppInfo(){
        Message.info(this.resourceBundle,"AboutApp");
    }

    /*exit application*/
    @FXML
    private void doExitApp(){
        this.mainApp.getPrimaryStage().close();
    }

    /*listener for observe change height of main window */
    public ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
    {
        double newHeight = this.mainApp.getPrimaryStage().getHeight();
        double newWidth = Formatter.getTextSize(newHeight);
        String newSize = "-fx-font-size:" +  String.valueOf(newWidth) + ";";
        this.fileNameTextField.setStyle(newSize);
        this.goBtn.setStyle(newSize);
        this.firstDirLbl.setStyle(newSize);
        this.firstDirSelectBtn.setStyle(newSize);
        this.changeLocalButton.setStyle(newSize);
        this.openResultBtn.setStyle(newSize);
        this.clearBtn.setStyle(newSize);
        this.settingsBtn.setStyle(newSize);
        this.aboutBtn.setStyle(newSize);
        this.exitBtn.setStyle(newSize);
        this.tableResult.setStyle(newSize);
    };

    public void setupResultTable() {

        this.tableResult.setPlaceholder(new Label(this.resourceBundle.getString("TableViewPlaceholder")));

        ObservableList<TableColumn<RowTableData, String>> columns = this.tableResult.getColumns();
        for (TableColumn<RowTableData, String> column : columns) {
            if (column.getId().equals("rowSimilar")) {
                column.setCellValueFactory(new PropertyValueFactory<>("PercSimilarity"));
            }
            if (column.getId().equals("rowFolderName")) {
                column.setCellValueFactory(new PropertyValueFactory<>("BaseFolderPath"));
            }
            if (column.getId().equals("rowFileName")) {
                column.setCellValueFactory(new PropertyValueFactory<>("Name"));
            }
            if (column.getId().equals("rowFileSize")) {
                column.setCellValueFactory(new PropertyValueFactory<>("SizeFormatted"));
                column.setStyle("-fx-alignment: CENTER_RIGHT;");
            }
        }

        this.tableResult.setRowFactory( tv -> {
            TableRow<RowTableData> row = new TableRow<RowTableData>() {
                @Override
                protected void updateItem(RowTableData rowTableData, boolean empty) {
                    super.updateItem(rowTableData, empty);
                    if (rowTableData == null) {
                        setStyle("-fx-background-color: white;");
                    } else {
                        if (rowTableData.getSimilarity() == 100)
                            setStyle("-fx-background-color: rgba(0,255,0,0.10);");
                        else if (rowTableData.getSimilarity() >= 90)
                            setStyle("-fx-background-color: rgba(0,204,51,0.10);");
                        else if (rowTableData.getSimilarity() >= 80)
                            setStyle("-fx-background-color: rgba(0,153,102,0.10);");
                        else if (rowTableData.getSimilarity() >= 70)
                            setStyle("-fx-background-color: rgba(0,102,153,0.10);");
                        else if (rowTableData.getSimilarity() >= 60)
                            setStyle("-fx-background-color: rgba(0,51,204,0.10);");
                        else if (rowTableData.getSimilarity() >= 50)
                            setStyle("-fx-background-color: rgba(0,0,255,0.10);");
                        else if (rowTableData.getSimilarity() >= 40)
                            setStyle("-fx-background-color: rgba(51,204,0,0.10);");
                        else if (rowTableData.getSimilarity() >= 30)
                            setStyle("-fx-background-color: rgba(102,153,0,0.10);");
                        else if (rowTableData.getSimilarity() >= 20)
                            setStyle("-fx-background-color: rgba(153,102,0,0.10);");
                        else if (rowTableData.getSimilarity() >= 10)
                            setStyle("-fx-background-color: rgba(204,51,0,0.10);");
                        else if (rowTableData.getSimilarity() >= 0)
                            setStyle("-fx-background-color: rgba(255,0,0,0.10);");
                        }
                }
            };

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    RowTableData rowTableData = row.getItem();
                    String columnID = row.getTableView().getSelectionModel().getSelectedCells().get(0).getTableColumn().getId();
                    if (columnID.equals("rowFolderName")) {
                        try {
                            assert this.desktop != null;
                            this.desktop.open(new File(rowTableData.getBaseFolderPath()));
                        } catch (Exception e) {
                            Message.errorAlert(this.resourceBundle, "Error in MainController.openResult() ", e);
                        }
                    } else if (columnID.equals("rowFileName")) {
                        try {
                            assert this.desktop != null;
                            this.desktop.open(new File(rowTableData.getAbsolutePath()));
                        } catch (Exception e) {
                            Message.errorAlert(this.resourceBundle, "Error in MainController.openResult() ", e);
                        }
                    }
                }
            });

            return row ;
        });
    }

    public void updateResultTable() {
        this.tableResult.setPlaceholder(new Label(this.resourceBundle.getString("TableViewPlaceholder")));
        ObservableList<TableColumn<RowTableData, String>> columns = this.tableResult.getColumns();
        for (TableColumn<RowTableData, String> column : columns) {
            if (column.getId().equals("rowSimilar")) {
                column.setText(this.resourceBundle.getString("Similar"));
            }
            if (column.getId().equals("rowFolderName")) {
                column.setText(this.resourceBundle.getString("Folder"));
            }
            if (column.getId().equals("rowFileName")) {
                column.setText(this.resourceBundle.getString("FileName"));
            }
            if (column.getId().equals("rowFileSize")) {
                column.setText(this.resourceBundle.getString("FileSizeB"));
            }
        }
    }
}
