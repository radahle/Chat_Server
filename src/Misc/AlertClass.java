package Misc;

import javafx.scene.control.Alert;

/**
 * Created by shohaib on 03/02/2017.
 */
public class AlertClass {

    /**
     * This method will open a new Stage (Alert Box) and give information related to the server.
     */
    public static void getInfo(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText("This is information");
        alert.showAndWait();
    }

}
