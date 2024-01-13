package comparer.controller;


import comparer.MainApp;
import comparer.RowTableData;
import comparer.model.FileComparer;
import comparer.model.FileInfo;
import comparer.util.AppPreferences;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
        this.startTask(null);

    }

    public void startTask(ActionEvent event) {

        FileComparer comparer1 = new FileComparer(this);

        this.tableResult.getItems().clear();
        //comparer.clean();

        if (this.firstDirectory != null) {
            comparer1.setStartDirectoryName(this.firstDirectory.getAbsolutePath());
        }


        String searchPhrase = this.fileNameTextField.getText().trim();
        if (searchPhrase.isEmpty()) {
            comparer1.setFileToSearch(null);
        } else {
            comparer1.setFileToSearch(new FileInfo(searchPhrase));
            //TODO remove later
            comparer1.setEndDirectoryName(searchPhrase);
        }


        comparer1.setResourceBundle(this.resourceBundle);
        try{
            Thread thread = new Thread(comparer1);
            thread.setDaemon(true);
            thread.start();
            this.progressBar.progressProperty().bind(comparer1.progressProperty());

        }
        catch (Exception e){
            Message.errorAlert(this.resourceBundle,"Error: ", e);
            e.printStackTrace();
        }
    }

    public void updateTable(List<RowTableData> report) {
        //setVisibility(true);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        pagination.setPageCount(report.size()/ROWS_RER_PAGE + 1);
        pagination.setMaxPageIndicatorCount(15);
        pagination.setCurrentPageIndex(0);
        int toIndex = Math.min(ROWS_RER_PAGE, report.size());
        tableResult.setItems(FXCollections.observableArrayList(report.subList(0, toIndex)));
        this.rowTableDataList = report;
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

    /*open saved txt-result file*/
    @FXML
    private void openResult(){
        try {
            assert this.desktop != null;
            //TODO remove or rework
           // this.desktop.open(new File(this.comparer.getReportName()));
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
        this.firstDirectory = null;
        this.secondDirectory = null;
        this.tableResult.getItems().clear();
        this.pagination.setMaxPageIndicatorCount(3);
        this.pagination.setPageCount(0);
        this.fileNameTextField.clear();
        this.rowTableDataList.clear();
        updateTextInfoLbl();
        setVisibility(false);
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
                    int R = 0;
                    int G = 0;
                    int B = 0;
                    if (rowTableData == null) {
                        setStyle("-fx-background-color: white;");
                    } else {
                        int similarity = rowTableData.getSimilarity();

                        if (similarity > 75 && similarity <=100) {
                            R = 0;
                            G = 255;
                            B = (255/25) * (100 - similarity);
                        } else if (similarity > 50 && similarity <= 75) {
                            R = 0;
                            G = (255/25) * (similarity - 50);
                            B = 255;
                        } else if (similarity > 25 && similarity <= 50) {
                            R = (255/25) * (50 - similarity);
                            G = 0;
                            B = 255;
                        } else if (similarity > 0 && similarity <= 25) {
                            R = 255;
                            G = 0;
                            B = (255/25) * similarity;
                        }
                        String cssFormatString = String.format("-fx-background-color: rgba(%s,%s,%s,0.05);", R, G, B);
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
                column.setText(this.resourceBundle.getString("FileSizeB"));
            }
        }
    }

    public void setupPagination() {
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

    public void saveSettings() {
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
        tableResult.setItems(FXCollections.observableArrayList(this.rowTableDataList.subList(fromIndex, toIndex)));

        return new BorderPane(tableResult);
    }

}
