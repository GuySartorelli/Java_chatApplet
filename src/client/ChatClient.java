package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messages.Message;

/**
 * Main application/GUI for number guessing game
 * @author rallion
 */
public class ChatClient  implements Runnable { //extends Application
	
//	private int width = 600;
//	private int height = 150;
//
//	/* (non-Javadoc)
//	 * @see javafx.application.Application#start(javafx.stage.Stage)
//	 */
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		primaryStage.setTitle("Number Guessing Game");
//		primaryStage.setMinHeight(height);
//		primaryStage.setMinWidth(width);
//		primaryStage.setMaxWidth(width);
//		HBox layout = new HBox(); //main scene layout
//		
//		//Input pane with user interaction
//		//--------------------------------
//		VBox inputPane = new VBox();
//		inputPane.setPadding(new Insets(0, 10, 0, 10));
//		inputPane.setAlignment(Pos.CENTER_LEFT);
//		Button newGameBtn = new Button("New Game");
//		HBox guessBox = new HBox();
//		guessBox.setPadding(new Insets(10, 0, 10, 0));
//		TextField guessField = new TextField();
//		guessField.setPromptText("Guess here");
//		Button guessBtn = new Button("Guess");
//		Button quitBtn = new Button("Quit");
//		
//		//Output area with computer logic/reaction
//		TextArea outputArea = new TextArea();
//		outputArea.setEditable(false);
//		outputArea.setFocusTraversable(false);
//		//set system out printstream to print to output area
//		System.setOut(new PrintStream(new TextStream(outputArea)));
//		
//		//set textfield and button event handlers
//		//---------------------------------------
//		guessField.setOnKeyReleased(setGuessFieldHandler(guessField));
//		newGameBtn.setOnAction(setNewGameBtnHandler(outputArea));
//		guessBtn.setOnAction(setGuessBtnHandler(guessField));
//		quitBtn.setOnAction(setQuitBtnHandler());
//		
//		//construct layout and show window
//		//----------------------------------------
//		guessBox.getChildren().addAll(guessField, guessBtn);
//		inputPane.getChildren().addAll(newGameBtn, guessBox, quitBtn);
//		layout.getChildren().addAll(inputPane, outputArea);
//		primaryStage.setScene(new Scene(layout));
//		primaryStage.show();
//	}
//	
//	/**
//	 * Sets the event handler for guessField to send user input to game on key "enter"
//	 * @param TextField guessField
//	 * @return new EventHandler
//	 */
//	private EventHandler<KeyEvent> setGuessFieldHandler(TextField guessField) {
//		return new EventHandler<KeyEvent>() {
//			@Override
//			public void handle(KeyEvent event) {
//				if (event.getCode().equals(KeyCode.ENTER)) {
////					game.guess(guessField.getText());
//					guessField.clear();
//				}
//			}
//		};
//	}
//	
//	/**
//	 * Sets the event handler for newGameBtn to start a new game on button press
//	 * @param TextArea outputArea
//	 * @return new EventHandler
//	 */
//	private EventHandler<ActionEvent> setNewGameBtnHandler(TextArea outputArea){
//		return new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				outputArea.clear();
////				game.newGame();
//			}
//		};
//	}
//	
//	/**
//	 * Sets the event handler for guessBtn to send user input to game on button press
//	 * @param guessField
//	 * @return new EventHandler
//	 */
//	private EventHandler<ActionEvent> setGuessBtnHandler(TextField guessField){
//		return new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
////				game.guess(guessField.getText());
//			}
//		};
//	}
//	
//	/**
//	 * Sets the event handler for quitBtn to quit the application on button press
//	 * @return new EventHandler
//	 */
//	private EventHandler<ActionEvent> setQuitBtnHandler(){
//		return new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				//primaryStage.close();
//				Platform.exit(); //docs say this is the preferred method for exiting an application
//			}
//		};
//	}
	
    private int port = 9090;
    String serverAddress = "10.140.136.80";
    private ClientConnectionThread reciever;
	private Socket socket;
    private ObjectOutputStream out;
    private BufferedReader in;
    private String userName = "Guy";
    
    public ChatClient() throws IOException {
        socket = new Socket(serverAddress, port);
        System.out.println("Client running");
        
    
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            socket.close();
        }

        new Thread(this).start();
        reciever = new ClientConnectionThread(this, socket);
    }

    @Override
    public void run() {
        String line = "";
        while (!line.equals("!exit"))
        {  try
           {  line = in.readLine();
              out.writeObject(new Message(userName, line));
              out.flush();
              System.out.println("Me: " + line);
           }
           catch(IOException e)
           {  
               System.out.println("Sending error: ");
               e.printStackTrace();
           }
        }
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void process(Message message) {
        System.out.println(message);
    }
    
    public void close() throws IOException {
        out.close();
        reciever.close();
        socket.close();
        System.out.println("Client closed");
    }
    

    public static void main(String[] args) throws IOException {
//      launch(args);
//        System.exit(0);
        new ChatClient();
    }
}