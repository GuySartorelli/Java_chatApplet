package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import messages.Message;

public class ChatServer implements Runnable {
    
    private int port;
    ServerSocket listener;
    Map<Integer, ObjectOutputStream> clients;
    List<Message> messages;
    private int status;
    
    
    public ChatServer() {
        this.port = 9090;
        clients = new HashMap<Integer, ObjectOutputStream>();
        messages = new CopyOnWriteArrayList<Message>();
    }
    
    public ChatServer(int port) {
        if (port == 20 || port == 80 || port == 8080) throw new IllegalArgumentException("Reserved port " + port);
        this.port = port;
        clients = new HashMap<Integer, ObjectOutputStream>();
        messages = new CopyOnWriteArrayList<Message>();
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
                clients.put(socket.getPort(), new ObjectOutputStream(socket.getOutputStream()));
                
            } catch (IOException e) {
                close();
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void process(int client, Message message) {
        if (message == null) return;
        if (message.getMessage().equals("!exit")) {
            clients.remove(client);
            try {
                sendAll(new Message("server", message.getSender() + " has left"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                sendAll(client, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void respond(int client, int response) {
        try {
            clients.get(client).writeInt(response);
            clients.get(client).flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public synchronized void sendAll(Message message) throws IOException {
        for (Map.Entry<Integer, ObjectOutputStream> entry : clients.entrySet()) {
            try {
                entry.getValue().writeObject(message);
            } catch (SocketException e) {
                clients.remove(entry.getKey());
            }
        }
    }
    
    public synchronized void sendAll(int sender, Message message) throws IOException {
        for (Map.Entry<Integer, ObjectOutputStream> entry : clients.entrySet()) {
            if (entry.getKey() == sender) continue;
            try {
                entry.getValue().writeObject(message);
            } catch (SocketException e) {
                clients.remove(entry.getKey());
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
