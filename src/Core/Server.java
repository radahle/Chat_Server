package Core;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Created by Shohaib on 30.01.2017.
 */
public class Server extends Task {

    public List<Socket> clientList;
    //private int clientList;
    @FXML
    ListView listView_clients;
    @FXML
    ListView listView_Log;
    @FXML
    TextArea textArea_Server_Log;
    private int portNumber;


    /**
     * @param portNumber   Port number for server.
     * @param listView_clients     List for clientList connected.
     * @param listView_Log List for showing entries.
     */
    public Server(int portNumber, ListView listView_clients, ListView listView_Log, TextArea textArea_Server_Log) {

        this.portNumber = portNumber;
        this.listView_clients = listView_clients;
        this.listView_Log = listView_Log;
        this.textArea_Server_Log = textArea_Server_Log;
        //clientList = 0;
    }

    /**
     * This method will accept clientList and create connection between clientList.
     * This method is a part of task thread, and will add information related to clientList to ListView's
     *
     * @return
     * @throws Exception
     */
    @Override
    protected Object call() throws Exception {

        System.out.println("Server running...");
        ServerSocket serverSocket = new ServerSocket(portNumber);
            clientList = new ArrayList<Socket>();

        while (true) {
            Socket socket = serverSocket.accept();       
            clientList.add(socket);
            ServerService serverService = new ServerService(socket, listView_Log, listView_clients, textArea_Server_Log, clientList);
            Platform.runLater(() -> {

                listView_clients.getItems().add(serverService.getClientInfo());
                //kopierer til en liste
                //for loop kopierer til ENDA en liste men denne er fra SS


                /*
                så etter det går du i serverservice inne i DC metodene og sammenlikner NÅværende liste og fjerner fra den
                 */
            });
            Platform.runLater(() -> listView_Log.getItems().add(serverService.getConnectedLog()));       
            serverService.start();
        }
    }

    public List<Socket> getClientList() {
        return clientList;
    }

    /**
     * This method will start the thread.
     */
    public void start() {

        Thread thread = new Thread(this);
        thread.start();
    }
}
