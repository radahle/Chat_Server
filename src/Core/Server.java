package Core;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

/**
 * Created by Shohaib on 30.01.2017.
 */
public class Server extends Task {

    public List<Socket> clientList;
    //private int clientList;
    @FXML
    ListView listView;
    @FXML
    ListView listView_Log;
    @FXML
    TextArea textArea_Server_Log;
    private int portNumber;


    /**
     * @param portNumber   Port number for server.
     * @param listView     List for clientList connected.
     * @param listView_Log List for showing entries.
     */
    public Server(int portNumber, ListView listView, ListView listView_Log, TextArea textArea_Server_Log) {

        this.portNumber = portNumber;
        this.listView = listView;
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
            //PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            clientList.add(socket);

            ServerService serverService = new ServerService(socket, listView_Log, listView, textArea_Server_Log, clientList);
            //serverService.getClientList().add(socket);

            Platform.runLater(() -> listView.getItems().add(serverService.getClientInfo()));

            Platform.runLater(() -> listView_Log.getItems().add(serverService.getConnectedLog()));

            System.out.println(serverService.getClientList());
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
