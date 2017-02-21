package Controllers;

import Misc.AlertClass;
import Core.FileHandler;
import Core.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Shohaib on 26.01.2017.
 */
public class Controller implements Initializable {

    //Gui related objects
    @FXML ListView listView_Clients;
    @FXML ListView listView_Log;
    @FXML TextArea textArea_Server_Log;
    @FXML Label label_Clients;

    //objects
    FileHandler fileHandler;
    Server server;

    //private data fields
    private int portNumber = 5555;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileHandler = new FileHandler();
        server = new Server(portNumber, listView_Clients, listView_Log, textArea_Server_Log);
        textArea_Server_Log.setEditable(false);
        server.start();

    }

    /**
     *
     * @param actionEvent
     */
    public void onCloseClick(ActionEvent actionEvent) {

        System.exit(0);
    }

    /**
     *
     * @param actionEvent
     */
    public void onAboutClick(ActionEvent actionEvent) {
        System.out.println("Clientlisten:   " + server.getClientList());
        AlertClass.getInfo();
    }

    /**
     *
     * @param actionEvent
     */
    public void onSaveEntryLogClick(ActionEvent actionEvent) {

        FileHandler.saveEntryLog(listView_Log.getItems().toString());
    }

    /**
     *
     * @param actionEvent
     */
    public void onResetEntryLogClick(ActionEvent actionEvent) {

        listView_Log.getItems().clear();
    }

    /**
     *
     * @param actionEvent
     */
    public void onResetServerLogClick(ActionEvent actionEvent) {

        textArea_Server_Log.clear();
    }
}
