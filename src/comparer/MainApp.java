package comparer;
/**
 * Class with main method
 */

import comparer.controller.MainController;
import comparer.controller.SettingsController;
import comparer.model.FileComparer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/*Main app JavaFX class */
public class MainApp extends Application {

    /*primary app stage*/
    private Stage primaryStage;

    /*root layout element*/
    private AnchorPane rootLayout;

    private MainController mainController;

    private ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
    {
//        System.out.println("Height: " + oldValue + " Width: " + newValue);
//        String text = mainController.firstDirSelectBtn.getText();
//        Double size = 0.3 * (double)newValue;
//        getTextSise(mainController.firstDirSelectBtn);
        mainController.firstDirSelectBtn.setStyle("-fx-font-size:"+getTextSise(mainController.firstDirSelectBtn)+";");

    };

    private double getTextSise(Labeled control){
        double sizeByHeight = 0.3 * control.getHeight();
        double sizeByWidth = control.getWidth()/control.getText().length();
        double limitByLength = 5 * control.getText().length();
//        System.out.println("sizeByHeight: " + sizeByHeight + "  sizeByWidth: " + sizeByWidth + "  limiyByLingth: " + limitByLength);

//        return sizeByWidth < sizeByHeight ? sizeByWidth : sizeByHeight;
        return sizeByWidth ;
    }




    public static void main(String[] args) {
        launch(args);
    }

    /*entry JavaFX method*/
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Directory compares");
        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream( "/comparer/resources/images/appImage.png" )));
        initRootLayout(new Locale("ru","RU"));
        primaryStage.widthProperty().addListener(this.stageSizeListener);
        primaryStage.heightProperty().addListener(this.stageSizeListener);
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
            rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Give the mainController access to the main app.
            mainController = loader.getController();
            mainController.setMainApp(this);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*open settings window*/
    public void showSettingsEditDialog(ResourceBundle resourceBundle, FileComparer comparer) {
        try {
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resourceBundle);
            loader.setLocation(MainApp.class.getResource("view/SettingsView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            SettingsController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setComparer(comparer);
            controller.setResourceBundle(resourceBundle);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*getter for primary stage*/
    public Stage getPrimaryStage() {
        return primaryStage;
    }


}
