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
 * Created by Shohaib on 30.01.2017.
 */
public class ServerService extends Service {


    @FXML
    ListView listView_Log;
    @FXML
    ListView listView_Client;
    @FXML
    TextArea textArea_Server_Log;

    //datafields
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
    BufferedReader bufferedReader;
    PrintWriter printWriter;


    /**
     * @param socket
     * @param listView_Log
     * @param listView_Client
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
     * @return Information related to client.
     */
    public String getClientInfo() {

        return "Client: [" + this.client_Address + ":" + this.clientPort + "]";
    }

    /**
     * @return Information related to when client Connected.
     */
    public String getConnectedLog() {

        return "Client: [" + this.client_Address + ":" + this.clientPort
                + "] Connected " + this.dateFormat.format(date) + " with userID: " + this.client_ID;
    }

    /**
     * @return Information related to when client Disconnected
     */
    public String getDisconnectedLog() {

        return log = "Client: [" + this.client_Address + ":" + this.clientPort
                + "] Disconnected " + dateFormat.format(new Date()) + " with userID: " + this.client_ID;
    }
    
    public int getPort(){
    	
    	return this.clientPort;
    }
    
    
    /**
     * @return
     */
    @Deprecated
    public List<Socket> getClientList() {

        return clientList;
    }

    /**
     * @param clientList
     */
    @Deprecated
    public void setClientList(List<Socket> clientList) {

        this.clientList = clientList;
    }
    
    
    public void clientUpdate() throws IOException {
         	for(int i = 0; i < clientList.size(); i++){
         		socket = clientList.get(i);
         		for (int j = 0; j < clientList.size(); j++) {
         			String clientInfo = "#@$[" + clientList.get(j).getInetAddress() + ":" + clientList.get(j).getPort() + "]$@#";

         			printWriter.println(clientInfo);
             		printWriter = new PrintWriter(socket.getOutputStream(), true);
             		
					}
         	}
    }
   
    /**
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
                    
                    System.out.println("Liste oppdatert etter når klient logger på: " + clientList);
                    clientUpdate();
                    
                    /*
                    int temp = 0; 
                    while(clientList.size() != temp) {
                    	for(int i = 0; i < clientList.size(); i++){
                    		socket = clientList.get(i);
                    		for (int j = 0; j < clientList.size(); j++) {
                    			String clientInfo = "IP: " + clientList.get(j).getInetAddress() + " Port: " + clientList.get(j).getPort();
                        		printWriter.println(clientInfo);
                        		printWriter = new PrintWriter(socket.getOutputStream(), true);
							}
                    	}
                    	temp = clientList.size();
                    }
					*/
                   /* 
                    int temp = 0;
                        
                    if(clientList.size() != temp) {
                    	for (int i = 0; i < clientList.size(); i++) {
                    		temp = 0;
                    		socket = clientList.get(i);
                    		System.out.println(clientList.get(i));
                    		printWriter.println(clientList.get(i));
                    		printWriter = new PrintWriter(socket.getOutputStream(), true);
                    	}
                    	temp = clientList.size();
                    }
                    *herherherherherherherherherherherherherherherherherherherh/

                    
                    //String info = listView_Client.getItems().toString();


                    /*if (info.length() < 1) {
                        printWriter.println("List does not contain enough members");
                    } else if (info.length() >= 1) {
                        printWriter.println(info);
                    } else if (info.equals(getClientInfo())) {
                        System.out.println("meg selv");
                        return null; //?? nope
                    }*/

                    while ((receivedText = bufferedReader.readLine()) != null) {
                        //              System.out.println("ClientList: " + clientList);

                        String receivingClient;
                        String outText;

                        System.out.println();
                        //             System.out.println("Mottatt tekst: " + receivedText);

                        // rgex for å hente ut portnummer til mottaker
                        Pattern portPattern = Pattern.compile(".*@(.*)");
                        Matcher portMatcher = portPattern.matcher(receivedText);
                        receivingClient = portMatcher.find() ? portMatcher.group(1) : null;  // Portnummer til mottaker
                        //       System.out.println("Splittet ut mottakerport: " + receivingClient);

                        // regex for å fjerne portnummer til mottaker fra meldingen
                        Pattern textPattern = Pattern.compile("^(.*(?=@))");
                        Matcher textMatcher = textPattern.matcher(receivedText);
                        outText = textMatcher.find() ? textMatcher.group(1) : null;  // Melding til mottaker
                        //      System.out.println("Sendes videre til mottaker: " + outText);


                        textArea_Server_Log.appendText(getClientInfo() + " > " + receivedText/*.toUpperCase()*/ + "\n");

                        for (int i = 0; i < clientList.size(); i++) {
                            if (clientList.get(i).getPort() == parseInt(receivingClient)) {
                                socket = clientList.get(i);
                                printWriter = new PrintWriter(socket.getOutputStream(), true);
                                printWriter.println(outText);
                                //break;
                            }
                        }

                        if (receivedText.equals("/w " + clientPort)) {

                            System.out.println(clientPort);
                            System.out.println(socket.getOutputStream().equals(getClientList().contains(clientPort)));
                        }
                    }


                    socket.close();

                    if (socket.isClosed()) {

                        Platform.runLater(() -> listView_Log.getItems().add(getDisconnectedLog()));

                        for(int i = 0; i < clientList.size(); i++){
                        	
                        	if(clientList.get(i).getPort() == getPort()){
                        		
                        		clientList.remove(i);
                        		
                        		System.out.println("Oppdatert liste etter at en klient logget ut: " + 
                                		clientList);
                        		
                        		clientUpdate();
                        		//printWriter = new PrintWriter(socket.getOutputStream(), true);
                        		//printWriter.println(clientList);
                        		//printWriter.flush();
                        		
    	

                        	}
                        }
                        
                        
                        for (int i = 0; i < listView_Client.getItems().size(); i++) {
                            if (listView_Client.getItems().get(i).equals(getClientInfo())) {

                                int finalI = i;
                                Platform.runLater(() -> listView_Client.getItems().remove(finalI));
                            }
                        }
                       
                    }

                } catch (IOException ioe) {
                    Platform.runLater(() -> listView_Log.getItems().add(getDisconnectedLog()));

                    for(int i = 0; i < clientList.size(); i++){
                    	
                    	if(clientList.get(i).getPort() == getPort()){
                    		
                    		clientList.remove(i);
                    		
                    		System.out.println("Oppdatert liste etter at en klient logget ut: " + 
                    		clientList);
                    		
                    		clientUpdate();
                    		
                    		
                            //printWriter = new PrintWriter(socket.getOutputStream(), true);
                     		//printWriter.println(clientList);
                     		//printWriter.flush();
	
                    	}
                    }
                    
                    for (int i = 0; i < listView_Client.getItems().size(); i++) {
                        if (listView_Client.getItems().get(i).equals(getClientInfo())) {

                            int finalI = i;
                            Platform.runLater(() -> listView_Client.getItems().remove(finalI));

                        }
                    }
                    clientUpdate();
                    System.out.println("Exception while reading or receiving " +
                            "from in/out port");
                   
                }

                return null;
            }

        };

        return task;
    }

}