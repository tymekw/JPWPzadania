import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{

    private List<ConnectedClients> clientsList = new ArrayList<ConnectedClients>();

    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private Thread run;
    private Thread receive;

    private boolean running = false;
    private String messageReceived;

    public Server(int port) {
        this.port = port;
        try {
            serverSocket = ((SSLServerSocketFactory)SSLServerSocketFactory.getDefault()).createServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        run = new Thread(this, "Server");
        run.start();
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Server started on port: " + port);

        while(running){
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            receive(clientSocket);
        }
    }

    // ZADANIE 1 - UZUPEŁNIJ METODY SEND I SENDTOALL

    private void send(String message, Socket clientSocket){
        // użyj poprawnego strumienia oraz metod do pobrania danych od danego klienta (patrz prezentacja)
        try{
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendToAll(String message){
        for(int i = 0; i < clientsList.size(); i++){
            send(message,clientsList.get(i).getClientSocket());
            // za pomocą pętli for wyślij (metodą send) wiadomość message do każdego klienta z listy clientsList
            // do pobrania gniazda użyj metody getClientSocket() w stosunku do danego klienta z listy
        }
    }

    private void receive(Socket clientSocket){
        receive = new Thread("Receive"){
            @Override
            public void run() {
                while(running){
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        messageReceived = in.readLine();
                    } catch (IOException e) {
                        return;
                    }
                    process(messageReceived);
                }
            }
        };
        receive.start();
    }

    private void disconnect(String message, boolean status){
        int senderID = Integer.parseInt(message.substring(3));
        String nickname = "";
        for(int i = 0; i < clientsList.size(); i++){
            if (clientsList.get(i).getID() == senderID){
                nickname = clientsList.get(i).getNickname();
                System.out.println(nickname + " is getting disconnected;");
                try {
                    clientsList.get(i).getClientSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                clientsList.remove(i);
                break;
            }
        }

        String str = "";
        if(status){
            str = nickname + " has left the Chat.";
            sendToAll(str);
        } else {
            str = nickname + " timed out.";
            sendToAll(str);
        }
    }

    private void process(String message){
        if(message.startsWith("/c/")){
            int id = UniqueID.getIdentifier();
            clientsList.add(new ConnectedClients(message.substring(3), clientSocket, id));

            String idReturnMessage = "/c/" + id;
            send(idReturnMessage, clientSocket);

            System.out.println(message.substring(3) + " connected; ID : " + id + "; Socket: " + clientSocket);
            sendToAll(message.substring(3) + " just joined the Chat!");
        }
        else if(message.startsWith("/m/")){
            message = message.substring(3);
            sendToAll(message);
        }
        else if(message.startsWith("/d/")){
            disconnect(message, true);
        }
        else{
            System.out.println("Process method: " + message);
        }
    }



}
