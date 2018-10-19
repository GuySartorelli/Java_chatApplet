package client;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class UserGUI extends VBox{
    private ChatGUI mainUI;
    private TextField nameField;
    private PasswordField pwdField;
    private Text feedback = new Text();
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String serverAddress = "10.140.136.80";
    private int port = 9090;

    public UserGUI(ChatGUI main) {
        mainUI = main;
        HBox nameBox = new HBox();
        HBox pwdBox = new HBox();
        getChildren().addAll(nameBox, pwdBox);
        
        Label nameLabel = new Label("User Name");
        nameField = new TextField();
        nameBox.getChildren().addAll(nameLabel, nameField);

        Label pwdLabel = new Label("Password");
        pwdField = new PasswordField();
        pwdBox.getChildren().addAll(pwdLabel, pwdField);
        
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(this::onLogin);
        Button signupBtn = new Button("Signup");
        signupBtn.setOnAction(this::onSignup);
        getChildren().addAll(loginBtn, signupBtn, feedback);
    }
    
    public void onLogin(ActionEvent event) {
        clearFeedback();
        
        if (nameField.getText().length() == 0 || pwdField.getText().length() == 0) {
            badFeedback("Name and password must not be empty");
            return;
        }
        
        boolean isConnected = connectToServer();
        if (!isConnected) {
            badFeedback("Unable to connect to server");
            return;
        }
        
        int loginIsValid = login();
        if (loginIsValid < 1) {
            String msg = "unknown error";
            switch (loginIsValid) {
            case 0:
                msg = "Name or password was incorrect";
                break;
            case -1:
                msg = "Unable to ask server if credentials are valid";
                break;
            }
            badFeedback(msg);
            closeConnection();
            return;
        }
        
        mainUI.startChat(socket, in, out);
    }
    
    public void onSignup(ActionEvent event) {
        clearFeedback();
        
        if (nameField.getText().length() == 0 || pwdField.getText().length() == 0) {
            badFeedback("Name and password must not be empty");
            return;
        }
        
        boolean isConnected = connectToServer();
        if (!isConnected) {
            badFeedback("Unable to connect to server");
            return;
        }
        
        int nameIsUnique = checkUniqueName();
        if (nameIsUnique < 1) {
            String msg = "unkown error";
            switch (nameIsUnique) {
            case 0:
                msg = "That name is already in use";
                break;
            case -1:
                msg = "Unable to ask server if name is in use";
                break;
            }
            badFeedback(msg);
            closeConnection();
            return;
        }
        
        int isSignedUp = signup();
        if (isSignedUp < 1) {
            String msg = "unknown error";
            switch (isSignedUp) {
            case 0:
                msg = "Could not sign you up. Please try again";
                break;
            case -1:
                msg = "Unable to ask server to sign you up";
                break;
            }
            badFeedback(msg);
            closeConnection();
            return;
        }
        
        mainUI.startChat(socket, in, out);
    }
    
    public void badFeedback(String msg) {
        feedback.setFill(Color.RED);
        feedback.setText(msg);
    }
    
    public void clearFeedback() {
        feedback.setText("");
    }
    
    public int checkUniqueName() {
        if (socket == null) return -1;
        
        try {
            out.writeUTF("chcknm::"+nameField.getText());
            out.flush();
            return in.readInt();
        } catch (IOException e) {
            return -1;
        }
    }
    
    public int signup() {
        if (socket == null) return -1;
        
        try {
            //note: If I was worried about being secure I'd hash the pwd before sending it to protect against sniffing
            out.writeUTF("signup::"+nameField.getText()+"::"+pwdField.getText());
            out.flush();
            return in.readInt();
        } catch (IOException e) {
            return -1;
        }
    }
    
    public int login() {
        if (socket == null) return -1;
        
        try {
            //note: If I was worried about being secure I'd hash the pwd before sending it to protect against sniffing
            out.writeUTF("login::"+nameField.getText()+"::"+pwdField.getText());
            out.flush();
            return in.readInt();
        } catch (IOException e) {
            return -1;
        }
    }
    
    public boolean connectToServer() {
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            socket = null;
            out = null;
            in = null;
            return false;
        }
        return true;
    }
    
    public void closeConnection() {
        if (socket == null) return;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }
}
