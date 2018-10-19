package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Main application/GUI for number guessing game
 * @author rallion
 */
public class ChatGUI extends Application {
    
  private int width = 600;
  private int height = 150;
  Stage primaryStage;
  private VBox layout;
  private ChatClient client;
  
  TextFlow outputArea;

  /* (non-Javadoc)
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
      this.primaryStage = primaryStage;
      primaryStage.setTitle("Chat Client");
      
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
      outputArea = new TextFlow();
      ScrollPane scroll = new ScrollPane(outputArea);
      scroll.setFocusTraversable(false);
      scroll.setFitToHeight(true);
      scroll.setFitToWidth(true);
//      outputArea.setEditable(false);
      outputArea.setFocusTraversable(false);
      outputArea.setPadding(new Insets(5));
      outputArea.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
      outputArea.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
      outputArea.getChildren().addListener((ListChangeListener<Node>)(change)->{
          scroll.setVvalue(1);
      });
      //set system out printstream to print to output area
//      System.setOut(new PrintStream(new TextStream(outputArea)));
      
      //set textfield and button event handlers
      //---------------------------------------
      msgField.setOnKeyReleased(msgFieldHandler(msgField, sendBtn));
      sendBtn.setOnAction(sendBtnHandler(msgField));
      quitBtn.setOnAction(quitBtnHandler());
      sendBtn.setFocusTraversable(false);
      quitBtn.setFocusTraversable(false);
      
      //construct layout and show window
      //----------------------------------------
      msgBox.getChildren().addAll(msgField, sendBtn, quitBtn);
      HBox.setHgrow(msgField, Priority.ALWAYS);
      layout.getChildren().addAll(scroll, msgBox);
      VBox.setVgrow(scroll, Priority.ALWAYS);
      primaryStage.setScene(new Scene(new UserGUI(this)));
      primaryStage.show();
  }
  
  public void startChat(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
      primaryStage.setScene(new Scene(layout));
      primaryStage.setMinHeight(height);
      primaryStage.setMinWidth(width);
      
      try {
        client = new ChatClient(this, socket, in, out);
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
  
  public synchronized void printToOutput(String msg, Paint color) {
      Text text = new Text(msg+"\n");
      text.setFill(color);
      outputArea.getChildren().add(text);
  }
  
  public static void main(String[] args) throws IOException {
      launch(args);
  }
}
