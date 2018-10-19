package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import messages.Message;

/**
 * Main application/GUI for number guessing game
 * @author rallion
 */
public class ChatClient  implements Runnable {
	
    private int status;
	private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatGUI gui;
    
    private final Paint SERVER_MSG_COLOR = Color.DARKSLATEGRAY;
//    private final Paint OTHER_MSG_COLOR = Color.BLUE;
    private Map<String, Paint> userColors;
    
    public ChatClient(ChatGUI gui, Socket socket, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        this.gui = gui;
        this.socket = socket;
        this.out = out;
        this.in = in;
        gui.printToOutput("Client running", SERVER_MSG_COLOR);
        status = 1;
        userColors = new HashMap<String, Paint>();
        userColors.put("server", SERVER_MSG_COLOR);
        userColors.put("ME", Color.BLACK);
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        while (status == 1) {
            try {
                Message message = (Message) in.readObject();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        process(message);
                    }
                });
            } catch (EOFException e) {
                //Thrown when the server closes connection because it's stuck on in.readObject() when the connection terminates
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gui.printToOutput("Connection severed by server", SERVER_MSG_COLOR);
                    }
                });
                status = 0;
                break;
            } catch (SocketException e) {
                //Thrown when the client ends because it's stuck on in.readObject() when the socket closes
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void send(String msg) {
        if (status == 1) {
            try {
                out.writeObject(new Message(null, msg));
                out.flush();
                gui.printToOutput("ME: " + msg, userColors.get("ME"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void process(Message message) {
        if (message != null) {
            if (!userColors.containsKey(message.getSender())) {
                double R = Math.random()*0.8;
                double G = Math.random()*0.8;
                double B = Math.random()*0.8;
                userColors.put(message.getSender(), new Color(R,G,B, 1));
            }
            gui.printToOutput(message.toString(), userColors.get(message.getSender()));
        }
    }
    
    public void close() throws IOException {
        if (status == 1) {
            status = 0;
            send("!exit");
            out.close();
            socket.close();
            gui.printToOutput("Client closed", SERVER_MSG_COLOR);
        }
    }
}