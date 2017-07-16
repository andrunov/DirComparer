package comparer.util;

import javafx.scene.control.Alert;

import java.util.ResourceBundle;

/**
 * Class for show messages
 */
public class Message {

    /*show alert message*/
    public static void warningAlert(ResourceBundle resourceBundle, String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(resourceBundle.getString("AlertTitle"));
        alert.setHeaderText(resourceBundle.getString("SelectDirAlertHeaderTex"));
        alert.setContentText(resourceBundle.getString(message));
        alert.showAndWait();
    }

    /*show error message*/
    public static void errorAlert(ResourceBundle resourceBundle, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resourceBundle.getString("ErrorTitle"));
        alert.setHeaderText(resourceBundle.getString("ErrorAlertHeaderTex"));
        alert.setContentText(resourceBundle.getString(message));
        alert.showAndWait();
    }

}
