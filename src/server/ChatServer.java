package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static messages.Protocol.*;

public class ChatServer implements Runnable {
    
    private int port;
    ServerSocket listener;
    Map<Integer, PrintWriter> writers;
    Map<String, Integer> clients;
    private int status;
    
    private static String WELCOME_MESSAGE = "Welcome to the chat!";
    
    
    public ChatServer() {
        this(9090);
    }
    
    public ChatServer(int port) {
        if (port == 20 || port == 80 || port == 8080) throw new IllegalArgumentException("Reserved port " + port);
        this.port = port;
        writers = new HashMap<Integer, PrintWriter>();
        clients = new HashMap<String, Integer>();
    }

    public void start()  throws IOException {
        try {
            status = 1;
            listener = new ServerSocket(port);
            new Thread(this).start();
            System.out.println("Server listening on port " + port);
        } catch (IOException e) {
            status = -1;
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (status == 1) {
            try {
                Socket socket = listener.accept();
                ServerConnectionThread thread = new ServerConnectionThread(socket, this);
                thread.start();
                writers.put(socket.getPort(), new PrintWriter(socket.getOutputStream()));
                
            } catch (IOException e) {
                close();
                e.printStackTrace();
            }
        }
    }
    
    public void process(int client, String message) {
        /* Note: If I was worried about being secure I'd use something like the below:
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
         * from https://www.baeldung.com/java-password-hashing
         */
        if (message == null) return;
        String[] tokens = message.split(DELIM);
        
        switch (tokens[0]) {
        case CHECK_NAME:
            String name = tokens[1];
            String response = ChatDB.checkUniqueName(name);
            if (response == SUCCESS) sendTo(client, SUCCESS);
            else {
                sendTo(client, FAIL);
                System.out.println("Client not authorised: " + client);
            }
            break;
        case SIGNUP:
            name = tokens[1];
            String pwd = tokens[2];
            response = ChatDB.signup(name, pwd);
            if (response == SUCCESS) {
                authorise(name, client);
            } else {
                sendTo(client, FAIL);
                System.out.println("Client not authorised: " + client);
            }
            break;
        case LOGIN:
            name = tokens[1];
            pwd = tokens[2];
            response = ChatDB.login(name, pwd);
            if (response == SUCCESS) {
                authorise(name, client);
            } else {
                sendTo(client, FAIL);
                System.out.println("Client not authorised: " + client);
            }
            break;
        case ACKNOWLEDGE_FAIL:
            writers.remove(client);
            break;
        
        case USER_EXIT:
            name = tokens[1];
            writers.remove(client);
            clients.remove(name);
        case MESSAGE:
        case ACTION:
            if (tokens[1].equals(PRIVATE)) sendTo(tokens[3], message);
            else sendAll(client, message);
            break;
        default:
            System.err.println("Unexpected message from " + client + ": " + message);
            break;
        }
        
    }
    
    public void authorise(String name, int client) {
        clients.put(name, client);
        System.out.println("Client authorised: " + client + " as " + name);
        sendTo(client, SUCCESS);
        sendTo(client, WELCOME+DELIM + name+DELIM + WELCOME_MESSAGE+DELIM + getCurrentUsers());
        sendAll(client, USER_ENTER+DELIM + name);
    }
    
    public void sendTo(int client, String response) {
        writers.get(client).println(response);
        writers.get(client).flush();
    }
    
    public void sendTo(String user, String message) {
        sendTo(clients.get(user), message);
    }
    
    public void sendAll(String message) {
        for (Map.Entry<Integer, PrintWriter> entry : writers.entrySet()) {
            entry.getValue().println(message);
            entry.getValue().flush();
//            clients.remove(entry.getKey());
        }
    }
    
    public void sendAll(int sender, String message) {
//        List<Integer> toRemove = new ArrayList<Integer>();
        for (Map.Entry<Integer, PrintWriter> entry : writers.entrySet()) {
            if (entry.getKey() == sender) continue;
            entry.getValue().println(message);
            entry.getValue().flush();
//            toRemove.add(entry.getKey());
        }
//        for (int client : toRemove) clients.remove(client);
    }
    
    public String getCurrentUsers() {
        String users = "";
        String[] clientSet = clients.keySet().toArray(new String[0]);
        for (int i = 0; i < clientSet.length-1; i++) users += clientSet[i]+DELIM;
        users += clientSet[clientSet.length-1];
        return users;
    }
    
    public void close() {
        status = 0;
        try {
            writers.clear();
            listener.close();
            System.out.println("Server closed successfully");
        } catch (IOException e) {
            status = -1;
            e.printStackTrace();
        }
    }
    
    public int getStatus() {
        return status;
    }
    
    public static void main(String[] args)  throws IOException {
        ChatServer server = new ChatServer();
        server.start();
    }
}
