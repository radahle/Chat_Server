package Core;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

import java.awt.Toolkit;

/**
 * Class ServerService
 * This class extends Service and its' job is to run workers.
 * @author Kittimasak Bunrat <s300342>
 * @author Shohaib Muhammad <s3003263>
 * @author Pontus Sk�ld <s300377>
 * @author Rudi Andr� Dahle <s300373>
 */

public class ServerService extends Service {


    @FXML
    ListView listView_Log;
    @FXML
    ListView listView_Client;
    @FXML
    TextArea textArea_Server_Log;
    BufferedReader bufferedReader;
    PrintWriter printWriter;

    private DateFormat dateFormat;
    private Date date;
    private Socket socket;
    private InetAddress client_Address;
    private int serverPort;
    private int clientPort;
    private int client_ID;
    private String log;
    private List<Socket> clientList;
    private List<Socket> check;

    /**
     * Constructor for the ServerService class.
     * @param socket
     * @param listView_Log
     * @param listView_Client
     * @param textArea_Server_Log
     * @param clientList
     */
    public ServerService(Socket socket, ListView listView_Log, ListView listView_Client, TextArea textArea_Server_Log, List<Socket> clientList) {

        this.socket = socket;
        this.client_Address = socket.getInetAddress();
        this.serverPort = socket.getLocalPort();
        this.clientPort = socket.getPort();
        this.listView_Log = listView_Log;
        this.listView_Client = listView_Client;
        this.textArea_Server_Log = textArea_Server_Log;
        this.clientList = clientList;
        client_ID = (this.hashCode() / 200);
        dateFormat = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
        date = new Date();
    }


    /**
     * This method returns clientInfo
     * @return Information related to client.
     */
    public String getClientInfo() {

        return "Client: [" + this.client_Address + ":" + this.clientPort + "]";
    }

    /**
     * This method returns client info with timestamp and unique userID when a client connects.
     * @return Information related to when client Connected.
     */
    public String getConnectedLog() {

        return "Client: [" + this.client_Address + ":" + this.clientPort
                + "] Connected " + this.dateFormat.format(date) + " with userID: " + this.client_ID;
    }

    /**
     * This method returns client info with timestamp and unique userID when a client disconnects.
     * @return Information related to when client Disconnected
     */
    public String getDisconnectedLog() {

        return log = "Client: [" + this.client_Address + ":" + this.clientPort
                + "] Disconnected " + dateFormat.format(new Date()) + " with userID: " + this.client_ID;
    }

    /**
     * This method returns the client's port.
     * @return
     */
    public int getPort() {

        return this.clientPort;
    }

    /**
     * A method that will send active connections to every active sockets.
     * @throws IOException
     */
    public void clientUpdate() throws IOException {
        int length = clientList.size();
        for(int i = 0; i < length; i++){
            socket = clientList.get(i);
            for (int j = 0; j < length; j++) {
                String clientInfo = "#@$[" + clientList.get(j).getInetAddress() + ":" + clientList.get(j).getPort() + "]$@#";

                printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println(clientInfo);

            }
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println("£#|¤%|&&|%¤|#£");
        }
    }

    /**
     * Method will create a worker when thread is called.
     * @return 
     */
    @Override
    protected Task<Object> createTask() {

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    printWriter = new PrintWriter(socket.getOutputStream(), true);

                    String receivedText;

                    clientUpdate();

                    while ((receivedText = bufferedReader.readLine()) != null) {

                        String receivingClient;
                        String outText;
                        String registerText;

                        Pattern portPattern = Pattern.compile(".*@(.*)");
                        Matcher portMatcher = portPattern.matcher(receivedText);
                        receivingClient = portMatcher.find() ? portMatcher.group(1) : null;

                        Pattern textPattern = Pattern.compile("^(.*(?=@))");
                        Matcher textMatcher = textPattern.matcher(receivedText);
                        outText = textMatcher.find() ? textMatcher.group(1) : null;

                        textArea_Server_Log.appendText(getClientInfo() + " > " + receivedText + "\n");

                        for (int i = 0; i < clientList.size(); i++) {
                            if (clientList.get(i).getPort() == parseInt(receivingClient)) {
                                socket = clientList.get(i);
                                printWriter = new PrintWriter(socket.getOutputStream(), true);
                                printWriter.println(outText);
                            }
                        }

                    }

                    socket.close();
                    if (socket.isClosed()) {

                        Platform.runLater(() -> listView_Log.getItems().add(getDisconnectedLog()));

                        for (int i = 0; i < clientList.size(); i++) {
                            if (clientList.get(i).getPort() == getPort()) {
                                clientList.remove(i);
                            }
                        }


                        for (int i = 0; i < listView_Client.getItems().size(); i++) {
                            if (listView_Client.getItems().get(i).equals(getClientInfo())) {
                                int finalI = i;
                                Platform.runLater(() -> listView_Client.getItems().remove(finalI));
                            }
                        }
                        clientUpdate();
                    }
                } catch (IOException ioe) {

                    Platform.runLater(() -> listView_Log.getItems().add(getDisconnectedLog()));

                    for (int i = 0; i < clientList.size(); i++) {
                        if (clientList.get(i).getPort() == getPort()) {
                            clientList.remove(i);
                        }
                    }

                    for (int i = 0; i < listView_Client.getItems().size(); i++) {
                        if (listView_Client.getItems().get(i).equals(getClientInfo())) {
                            int finalI = i;
                            Platform.runLater(() -> listView_Client.getItems().remove(finalI));
                        }
                    }
                    clientUpdate();
                }

                return null;
            }

        };

        return task;
    }

}