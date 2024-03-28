package comparer;
/**
 * Class with main method
 */

import comparer.controller.MainController;
import comparer.controller.SettingsController;
import comparer.model.Settings;
import comparer.util.AppPreferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

//TODO 5. Сделать сохранение локали
//TODO 6. Вынести все настройки в Settings
//TODO 6. Привести в порядок resource bundle
//TODO 8. Сделать рефакторинг переменных и методов
//TODO 9. Сделать разделение на comparer и searcher (не забыть про разные пути сохранения параметров)




/*Main app JavaFX class */
public class MainApp extends Application {

    /*primary app stage*/
    private Stage primaryStage;

    /*root layout element*/
    private SplitPane rootLayout;

    /*link to main controller*/
    private MainController mainController;

    private Settings settings;

    /*main method*/
    public static void main(String[] args) {
        launch(args);
    }

    /*entry JavaFX method*/
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Directory compares");
        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream( "/comparer/resources/images/glass.png" )));
        initRootLayout(new Locale("ru","RU"));

        this.primaryStage.heightProperty().addListener(mainController.stageSizeListener);
        this.primaryStage.setWidth(AppPreferences.getMainWindowWidth());
        this.primaryStage.setHeight(AppPreferences.getMainWindowHeight());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        this.mainController.saveFormSettings();
        this.settings.saveFields();
        AppPreferences.setMainWindowHeight(this.getPrimaryStage().getHeight());
        AppPreferences.setMainWindowWidth(this.getPrimaryStage().getWidth());
    }

    /**
     * open main window
     */
    public void initRootLayout(Locale locale) {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("comparer.resources.bundles.Locale", locale));
            loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            this.settings = new Settings();
            this.settings.loadFields();

            // Give the mainController access to the main app.
            mainController = loader.getController();
            mainController.loadSettings();
            mainController.setupResultTable();
            mainController.setupPagination();
            mainController.setMainApp(this);
            mainController.setSettings(this.settings);
            mainController.changeLocale();

            this.updateSkin(this.settings.getSkin().getRepr());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*open settings window*/
    public void showSettingsEditDialog(ResourceBundle resourceBundle) {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resourceBundle);
            loader.setLocation(MainApp.class.getResource("view/SettingsView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create dialog window Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // create and adjust controller
            SettingsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setSettings(this.settings);
            controller.setResourceBundle(resourceBundle);
            controller.setFieldsValues();

            dialogStage.heightProperty().addListener(controller.stageSizeListener);
            dialogStage.setWidth(AppPreferences.getSettingsWindowWidth());
            dialogStage.setHeight(AppPreferences.getSettingsWindowHeight());


            // open dialog stage and wait till user close it
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*getter for primary stage*/
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void updateSkin(String skinValue) {
        String newStyle = String.format("comparer/style/%s.css", skinValue);
        this.primaryStage.getScene().getRoot().getStylesheets().clear();
        this.primaryStage.getScene().getRoot().getStylesheets().add(newStyle);

    }


}
