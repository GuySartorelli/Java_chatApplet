package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import messages.Message;

public class ChatServer implements Runnable {
    
    private Thread thread;
    private int port;
    ServerSocket listener;
    List<ChatConnectionThread> clients;
    List<Message> messages;
    private int status;
    
    //get a basic thing working
//    private Socket socket;
//    private ObjectOutputStream out;
//    private ObjectInputStream in;
    
    
    public ChatServer() {
        this.port = 9090;
        clients = new ArrayList<ChatConnectionThread>();
        messages = new CopyOnWriteArrayList<Message>();
    }
    
    public ChatServer(int port) {
        if (port == 20 || port == 80 || port == 8080) throw new IllegalArgumentException("Reserved port " + port);
        this.port = port;
        clients = new ArrayList<ChatConnectionThread>();
        messages = new CopyOnWriteArrayList<Message>();
    }

    public void start()  throws IOException {
        try {
            status = 1;
            listener = new ServerSocket(port);
            thread = new Thread(this);
            thread.start();
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
                ChatConnectionThread t = new ChatConnectionThread(socket, this);
                t.start();
                clients.add(t);
                
            } catch (IOException e) {
                close();
                e.printStackTrace();
            }
        }
    }
    
    public void close() {
        status = 0;
        try {
            clients.clear();
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
