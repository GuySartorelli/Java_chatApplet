package client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static messages.Protocol.*;

/**
 * Main application/GUI for number guessing game
 * @author rallion
 */
public class ChatClient  implements Runnable {
	
    private int status;
	private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private ChatGUI gui;
    private ChatFlow msgDisplay;
    
    private final Paint SERVER_MSG_COLOR = Color.DARKSLATEGRAY;
    private Map<String, Paint> userColors;
    
    public ChatClient(ChatGUI gui, Socket socket, BufferedReader in, PrintWriter out, ChatFlow msgDisplay) throws IOException {
        this.gui = gui;
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.msgDisplay = msgDisplay;
        status = 1;
        userColors = new HashMap<String, Paint>();
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        while (status == 1) {
            try {
                String message = in.readLine();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        processFromServer(message);
                    }
                });
            } catch (EOFException e) {
                //Thrown when the server closes connection because it's stuck on in.readObject() when the connection terminates
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        msgDisplay.print("Connection severed by server", SERVER_MSG_COLOR);
                    }
                });
                status = 0;
                break;
            } catch (SocketException e) {
                //Thrown when the client ends because it's stuck on in.readObject() when the socket closes
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void processToServer(String msg) {
        if (status == 1) {
            if (msg.contains(DELIM)) {
                msgDisplay.print("message cannot contain the string \""+DELIM+"\"", SERVER_MSG_COLOR);
                return;
            }
            
            String toServer = "";
            if (msg.startsWith("/me ")) {
                toServer = ACTION+DELIM;
                msg = msg.replace("/me ", "");
                msgDisplay.print(name+" "+msg, SERVER_MSG_COLOR);
            }
            else {
                toServer = MESSAGE+DELIM;
                msgDisplay.print("ME: " + msg, userColors.get(name));
            }
            toServer += PUBLIC+DELIM + name+DELIM + msg;
            
            out.println(toServer);
            out.flush();
        }
    }
    
    public void processFromServer(String message) {
        if (message != null) {
            String[] tokens = message.split(DELIM);
            switch (tokens[0]) {
            case MESSAGE:
                String from = tokens[2];
                if (tokens[1].equals(PRIVATE)) break; //not yet handled
                else msgDisplay.print(from+": " + tokens[3], userColors.get(from));
                break;
            case ACTION:
                from = tokens[2];
                if (tokens[1].equals(PRIVATE)) break; //not yet handled
                else msgDisplay.print(from+" " + tokens[3], SERVER_MSG_COLOR);
                break;
            case USER_ENTER:
                String user = tokens[1];
                addUser(user);
                msgDisplay.print(user+" has entered", SERVER_MSG_COLOR);
                break;
            case USER_EXIT:
                user = tokens[1];
                userColors.remove(user);
                gui.removeUser(user);
                msgDisplay.print(user+" has exited", SERVER_MSG_COLOR);
                break;
            case WELCOME:
                name = tokens[1];
                userColors.put(name, Color.BLACK);
                msgDisplay.print(tokens[2], SERVER_MSG_COLOR);
                for (int i = 3; i < tokens.length; i++) addUser(tokens[i]);
                break;
            default:
                System.err.println("Unexpected message from server: " + message);
                break;
            }
        } else System.err.println("Message from server was null");
    }
    
    public void addUser(String user) {
        Paint color;
        if (!userColors.containsKey(user)) {
            double R = Math.random()*0.8;
            double G = Math.random()*0.8;
            double B = Math.random()*0.8;
            color = new Color(R,G,B, 1);
            userColors.put(user, color);
        } else color = userColors.get(user);
        
        gui.addUser(user, color);
    }
    
    public void close() throws IOException {
        if (status == 1) {
            status = 0;
            out.println(USER_EXIT+DELIM + name);
            out.flush();
            out.close();
            socket.close();
            msgDisplay.print("Client closed", SERVER_MSG_COLOR);
        }
    }
}