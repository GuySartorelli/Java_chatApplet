package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static messages.Protocol.*;

public class ServerConnectionThread extends Thread {

    private Socket socket;
    private ChatServer server;
    private BufferedReader in;
    private int id;
    
    public ServerConnectionThread(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.id = socket.getPort();
        
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error opening stream for " + socket);
            socket.close();
        }
        System.out.println("Client accepted: " + socket);
    }
    
    @Override
    public void run() 
    {
        try {
            boolean done = false;
            while (server.getStatus() == 1 && !done) {
                String message = in.readLine();
                server.process(id, message);
                done = message.startsWith(USER_EXIT) || message.startsWith(ACKNOWLEDGE_FAIL);
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close() throws IOException {
        System.out.println("Client exited: " + socket);
        in.close();
        socket.close();
    }
}
