package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import messages.Message;
import static messages.Protocol.*;

public class ServerConnectionThread extends Thread {

    private Socket socket;
    private ChatServer server;
    private ObjectInputStream in;
    private String name;
    private int id;
    
    public ServerConnectionThread(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.id = socket.getPort();
        
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
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
                if (name == null) { //user is not logged in
                    String[] tokens = in.readUTF().split("::");
                    int response;
                    /*
                     * Note: If I was worried about being secure I'd use something like the below:
                        SecureRandom random = new SecureRandom();
                        byte[] salt = new byte[16];
                        random.nextBytes(salt);
                        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
                        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                        byte[] hash = factory.generateSecret(spec).getEncoded();
                     * from https://www.baeldung.com/java-password-hashing
                     */
                    if (tokens.length == 2 && tokens[0].equals("chcknm")) {
                        String name = tokens[1];
                        response = ChatDB.checkUniqueName(name);
                        if (response != SUCCESS) {
                            done = true;
                        }
                    } else if (tokens.length == 3 && tokens[0].equals("signup")) {
                        String name = tokens[1];
                        String pwd = tokens[2];
                        response = ChatDB.signup(name, pwd);
                        if (response == SUCCESS) {
                            this.name = name;
                            System.out.println("Client authorised: " + socket + "as " + name);
                            server.process(id, new Message("server", name + " has entered"));
                        } else {
                            done = true;
                        }
                    } else if (tokens.length == 3 && tokens[0].equals("login")) {
                        String name = tokens[1];
                        String pwd = tokens[2];
                        response = ChatDB.login(name, pwd);
                        if (response == SUCCESS) {
                            this.name = name;
                            System.out.println("Client authorised: " + socket + "as " + name);
                            server.process(id, new Message("server", name + " has entered"));
                        } else {
                            done = true;
                        }
                    } else {
                        response = ERROR;
                    }
                    server.respond(id, response);
                    
                } else { //user is logged in and chatting
                    try {
                        Message message = (Message) in.readObject();
                        message.setSender(name);
                        server.process(id, message);
                        done = message.getMessage().equals("!exit");
                    } catch (EOFException | SocketException e) {
                        done = true;
                    } catch(IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
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
