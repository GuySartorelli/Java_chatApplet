package client;

import java.io.IOException;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application/GUI for number guessing game
 * @author rallion
 */
public class ChatGUI extends Application {
    
  private int width = 600;
  private int height = 150;
  private Stage primaryStage;
  private VBox layout;
  private ChatClient client;

  /* (non-Javadoc)
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
      this.primaryStage = primaryStage;
      primaryStage.setTitle("Chat Client");
      primaryStage.setMinHeight(height);
      primaryStage.setMinWidth(width);
      
      //Input pane with user interaction
      //--------------------------------
      layout = new VBox();
      layout.setPadding(new Insets(0, 10, 0, 10));
      layout.setAlignment(Pos.CENTER_LEFT);
      HBox msgBox = new HBox();
      msgBox.setPadding(new Insets(10, 0, 10, 0));
      msgBox.setAlignment(Pos.CENTER);
      msgBox.setSpacing(5);
      TextField msgField = new TextField();
      msgField.setPromptText("Type your message");
      Button sendBtn = new Button("Send");
      Button quitBtn = new Button("Quit");
      
      //Output area with chat messages
      TextArea outputArea = new TextArea();
      outputArea.setEditable(false);
      outputArea.setFocusTraversable(false);
      outputArea.setPadding(new Insets(5));
      //set system out printstream to print to output area
      System.setOut(new PrintStream(new TextStream(outputArea)));
      
      //set textfield and button event handlers
      //---------------------------------------
      msgField.setOnKeyReleased(msgFieldHandler(msgField, sendBtn));
      sendBtn.setOnAction(sendBtnHandler(msgField));
      quitBtn.setOnAction(quitBtnHandler());
      
      //construct layout and show window
      //----------------------------------------
      msgBox.getChildren().addAll(msgField, sendBtn, quitBtn);
      HBox.setHgrow(msgField, Priority.ALWAYS);
      layout.getChildren().addAll(outputArea, msgBox);
      VBox.setVgrow(outputArea, Priority.ALWAYS);
      primaryStage.setScene(new Scene(new UserGUI(this)));
      primaryStage.show();
  }
  
  public void startChat(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
      primaryStage.setScene(new Scene(layout));
      
      try {
        client = new ChatClient(socket, in, out);
    } catch (IOException e) {
        client = null;
        System.out.println("Unable to connect to server");
    }
  }
  
  /**
   * Sets the event handler for guessField to send user input to game on key "enter"
   * @param TextField guessField
   * @return new EventHandler
   */
  private EventHandler<KeyEvent> msgFieldHandler(TextField msgField, Button sendBtn) {
      return new EventHandler<KeyEvent>() {
          @Override
          public void handle(KeyEvent event) {
              if (event.getCode().equals(KeyCode.ENTER)) {
                  sendBtn.fire();
              }
          }
      };
  }
  
  /**
   * Sets the event handler for guessBtn to send user input to game on button press
   * @param msgField
   * @return new EventHandler
   */
  private EventHandler<ActionEvent> sendBtnHandler(TextField msgField){
      return new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
              client.send(msgField.getText());
              msgField.clear();
          }
      };
  }
  
  /**
   * Sets the event handler for quitBtn to quit the application on button press
   * @return new EventHandler
   */
  private EventHandler<ActionEvent> quitBtnHandler(){
      return new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
              try {
                client.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
              Platform.exit();
          }
      };
  }
  
  public static void main(String[] args) throws IOException {
      launch(args);
  }
}
