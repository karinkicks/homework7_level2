package chat.server;

import chat.auth.AuthenticationService;
import chat.auth.AuthenticationServiceDB;
import chat.auth.BasicAuthenticationService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer implements Server {
    private Set<ClientHandler> clients;
    private AuthenticationService authenticationService;
    private DataInputStream in;
    private DataOutputStream out;
    private File file = new File("C://Users//Карина//IdeaProjects//homework7_level2//LogFile.txt");
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChatServer() {
        try {
            System.out.println("Server is starting up...");
            ServerSocket serverSocket = new ServerSocket(8888);
            clients = new HashSet<>();
            authenticationService = new AuthenticationServiceDB();
            System.out.println("Server is started up...");
            writer = new BufferedWriter(new FileWriter(file));
            try {
                boolean create = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при создании файла");
            }

            while (true) {
                System.out.println("Server is listening for clients...");
                Socket socket = serverSocket.accept();
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                System.out.println("Client accepted: " + socket);
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            throw new RuntimeException("public ChatServer", e);
        }
    }

    @Override
    public synchronized void broadcastMessage(String message) {

       if(message.startsWith("/w")) {
            String msg = "";
            String[] array = message.split("\\s");
            for(ClientHandler client : clients){
                if(client.getName().equals(array[1])){
                    for(int i=2;i<array.length;i++){
                        msg = msg + array[i];
                    }
                    client.sendMessage(msg);
                }
            }
        }else{
        clients.forEach(client -> client.sendMessage(message));}
    }

    @Override
    public synchronized boolean isLoggedIn(String nickname) {
        return clients.stream()
                .filter(clientHandler -> clientHandler.getName().equals(nickname))
                .findFirst()
                .isPresent();
    }

    @Override
    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }

    @Override
    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    @Override
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Override
    public synchronized void addLog(String message) {
        try{

            writer.write(message);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи в файл");
        }
    }

    @Override
    public void showLog() {

        if(!file.exists()) {
            System.out.println("Нет такого файла");
            return;
        }
        int amountOfLine=0;
        try {
            reader = new BufferedReader(new FileReader(file));
            out.writeUTF("--Начало истории чата--");
            while (amountOfLine<=100 && reader.ready()==true){
                out.writeUTF(reader.readLine());
                amountOfLine++;
            }
            out.writeUTF("--Конец истории чата--");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
