package comparer.controller;


import comparer.MainApp;
import comparer.RowTableData;
import comparer.model.FileComparer;
import comparer.model.Settings;
import comparer.util.AppPreferences;
import comparer.util.ColorController;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/*controller for MainView.fxml window*/
public class MainController implements Initializable {

    private static final int ROWS_RER_PAGE = 15;

    @FXML
    private SplitPane splitPane;

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

    @FXML
    private ProgressBar progressBar;

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

    @FXML
    private Pagination pagination;

    /*language pocket*/
    private ResourceBundle resourceBundle;

    /*first choose directory for comparing*/
    private File firstDirectory;

    /*second choose directory for comparing*/
    private File secondDirectory;


    /* Reference to the main application*/
    private MainApp mainApp;

    /*desktop uses for open files just from JavaFX application*/
    private Desktop desktop;

    private  List<RowTableData> rowTableDataList;

    private Settings settings;


    private String reportName;

    private int searchAttemptNumber;

    /*constructor*/
    public MainController() {
        this.rowTableDataList = new ArrayList<>();
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
            //setTextDirLabel(this.firstDirLbl, "FirstDirectory", getDirInfo(directory));
            this.firstDirLbl.setText(getDirInfo(directory));
           // updateTextInfoLbl();
        }
    }

    @FXML
    private void onEnter() {
        this.executeSearch();
    }

    /*start comparing procedure*/
    @FXML
    private void executeSearch(){
        if (this.checkFields()) {
            this.searchAttemptNumber = 0;
            this.startSearchTask(false);
        }
    }

    public void startSearchTask(boolean deepCompare) {


        this.tableResult.getItems().clear();
        this.firstDirLbl.setVisible(false);
        this.progressBar.setVisible(true);
        FileComparer comparer = FileComparer.createForSearch(this);
        if (deepCompare) {
            comparer.setExactWordMatch(false);
        }

        try{
            Thread thread = new Thread(comparer);
            thread.setDaemon(true);
            thread.start();
            this.progressBar.progressProperty().bind(comparer.progressProperty());
            this.searchAttemptNumber++;

        }
        catch (Exception e){
            Message.errorAlert(this.resourceBundle,"Error: ", e);
            e.printStackTrace();
        }
    }

    private boolean checkFields() {
        boolean result = false;
        if ((this.fileNameTextField.getText().trim().isEmpty())
            && (this.firstDirectory == null)) {
                Message.warningAlert(this.resourceBundle,"SelectDirAndWordAlert");
        } else  if (this.firstDirectory == null){
            Message.warningAlert(this.resourceBundle,"SelectDirAlert");
        } else if (this.fileNameTextField.getText().trim().isEmpty()){
            Message.warningAlert(this.resourceBundle,"SelectWordAlert");
        } else result = true;
        return result;
    }

    public void showResult(List<RowTableData> report) {
        if (report.size() == 0
            && this.searchAttemptNumber < 2
            && this.getSettings().isExactWordMatch()) {
            if(Message.confirmationAlert(this.resourceBundle, "DeepSearchMessage")) {
                this.startSearchTask(true);
            } else {
                this.updateTable(report);
            }
        } else {
            this.updateTable(report);
        }
    }

    private void updateTable(List<RowTableData> report) {
        if (this.getSettings().isSaveHtmlReport()) {
            this.openResultBtn.setVisible(true);
        }
        this.progressBar.progressProperty().unbind();
        this.progressBar.setProgress(0);
        this.pagination.setPageCount(report.size()/ROWS_RER_PAGE + 1);
        this.pagination.setMaxPageIndicatorCount(15);
        this.pagination.setCurrentPageIndex(0);
        int toIndex = Math.min(ROWS_RER_PAGE, report.size());
        this.tableResult.setItems(FXCollections.observableArrayList(report.subList(0, toIndex)));
        this.rowTableDataList = report;
        this.progressBar.setVisible(false);
        this.firstDirLbl.setVisible(true);
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
        this.openResultBtn.setVisible(false);
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
        this.fileNameTextField.setPromptText(this.resourceBundle.getString("FileName"));
        this.updateResultTable();
    }

    /*updates text for infoLbl Label depending of
    * firstDirectory and secondDirectory directories*/
    private void updateTextInfoLbl(){
        setTextDirLabel(firstDirLbl,"FirstDirectory",getDirInfo(firstDirectory));
    }

    /*updates text for several Labels*/
    private void setTextDirLabel(Label label, String bundleKey, String infoPath){
         label.setText(resourceBundle.getString(bundleKey) + infoPath);
    }

    /**/
    private String getDirInfo(File directory){
        String result = "";
        if (directory != null) {
            result = directory.getPath();
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


    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public File getFirstDirectory() {
        return firstDirectory;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public String getSearchPhrase() {
        return this.fileNameTextField.getText().trim();
    }

    /*open saved txt-result file*/
    @FXML
    private void openResult(){
        try {
            assert this.desktop != null;
            this.desktop.open(new File(this.reportName));
        } catch (Exception e) {
            Message.errorAlert(this.resourceBundle, "Error in MainController.openResult() ", e);
        }
    }

    /*clear fields to default*/
    @FXML
    private void clear(){
        this.firstDirectory = null;
        this.secondDirectory = null;
        this.rowTableDataList.clear();
        this.tableResult.getItems().clear();
        this.pagination.setPageCount(1);
        this.fileNameTextField.clear();
        updateTextInfoLbl();
        this.openResultBtn.setVisible(false);
    }

    /*open settings window*/
    @FXML
    private void openSettings(){
        mainApp.showSettingsEditDialog(this.resourceBundle);
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
        this. pagination.setStyle(newSize);
    };

    public void setupResultTable() {

        this.tableResult.setPlaceholder(new Label(this.resourceBundle.getString("TableViewPlaceholder")));

        ObservableList<TableColumn<RowTableData, String>> columns = this.tableResult.getColumns();
        for (TableColumn<RowTableData, String> column : columns) {
            if (column.getId().equals("rowSimilar")) {
                column.setCellValueFactory(new PropertyValueFactory<>("PercSimilarity"));
            }
            if (column.getId().equals("rowFolderName")) {
                column.setCellValueFactory(new PropertyValueFactory<>("BaseFolderPathFormatted"));
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
                        int similarity = rowTableData.getSimilarity();
                        String bgRGB = ColorController.getBgRGB(similarity);
                        String cssFormatString = String.format("-fx-background-color: rgba(%s,0.05);", bgRGB);
                        if (rowTableData.isDirectory()) {
                            cssFormatString = cssFormatString + "-fx-font-weight: bold;";
                        }
                        setStyle(cssFormatString);
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
                column.setText(this.resourceBundle.getString("FileSize"));
            }
        }
    }

    public void setupPagination() {
        this.pagination.setPageCount(1);
        this.pagination.setPageFactory(this::createPage);
    }

    public void loadSettings() {
        this.splitPane.setDividerPosition(0, AppPreferences.getSplitPaneDividerPosition());
        ObservableList<TableColumn<RowTableData, String>> columns = this.tableResult.getColumns();
        for (TableColumn<RowTableData, String> column : columns) {
            if (column.isVisible()) {
                double prefWidth = AppPreferences.getTableColumnWidth(column.getId());
                column.setPrefWidth(prefWidth);
            }
        }
    }

    public void saveFormSettings() {
        AppPreferences.setSplitPaneDividerPosition(this.splitPane.getDividerPositions()[0]);
        ObservableList<TableColumn<RowTableData, String>> columns = this.tableResult.getColumns();
        for (TableColumn<RowTableData, String> column : columns) {
            if (column.isVisible()) {
                AppPreferences.setTableColumnWidth(column.getId(), column.getWidth());
            }
        }
    }


    private Node createPage(int pageIndex) {

        int fromIndex = pageIndex * ROWS_RER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_RER_PAGE, this.rowTableDataList.size());
        if (this.rowTableDataList.size() > 0) {
            tableResult.setItems(FXCollections.observableArrayList(this.rowTableDataList.subList(fromIndex, toIndex)));
        } else {
            tableResult.setItems(FXCollections.observableArrayList(this.rowTableDataList));
        }

        return new BorderPane(tableResult);
    }

}
