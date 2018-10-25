package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
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
import javafx.stage.Stage;

import static messages.Protocol.SERVER;

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
  
  private ChatFlowTab serverTab;
  private TabPane outputTabPane;
  private VBox userPanel;
  private Map<String, Button> userButtons = new HashMap<String, Button>();
  private Map<String, ChatFlowTab> userTabs = new HashMap<String, ChatFlowTab>();

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
      HBox msgInBox = new HBox();
      msgInBox.setPadding(new Insets(10, 0, 10, 0));
      msgInBox.setAlignment(Pos.CENTER);
      msgInBox.setSpacing(5);
      TextField msgField = new TextField();
      msgField.setPromptText("Type your message");
      Button sendBtn = new Button("Send");
      Button quitBtn = new Button("Quit");
      
      //Output area with chat messages
      //------------------------------
      outputTabPane = new TabPane();
      serverTab = new ChatFlowTab(SERVER, false);
      outputTabPane.getTabs().add(serverTab);
      userTabs.put(SERVER, serverTab);
      
      //Current users panel
      //-------------------
      userPanel = new VBox(5);
      userPanel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
      userPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));

      ScrollPane userScroll = new ScrollPane(userPanel);
      userScroll.setFocusTraversable(false);
      userScroll.setFitToHeight(true);
      userScroll.setFitToWidth(true);
      
      HBox outputAndUsers = new HBox(outputTabPane, userScroll);
      HBox.setHgrow(outputTabPane, Priority.ALWAYS);
      
      //set textfield and button event handlers
      //---------------------------------------
      msgField.setOnKeyReleased(msgFieldHandler(msgField, sendBtn));
      sendBtn.setOnAction(sendBtnHandler(msgField));
      quitBtn.setOnAction(quitBtnHandler());
      sendBtn.setFocusTraversable(false);
      quitBtn.setFocusTraversable(false);
      
      //construct layout and show window
      //----------------------------------------
      msgInBox.getChildren().addAll(msgField, sendBtn, quitBtn);
      HBox.setHgrow(msgField, Priority.ALWAYS);
      layout.getChildren().addAll(outputAndUsers, msgInBox);
      VBox.setVgrow(outputAndUsers, Priority.ALWAYS);
      primaryStage.setScene(new Scene(new UserGUI(this)));
      primaryStage.show();
  }
  
  public void startChat(Socket socket, BufferedReader in, PrintWriter out) {
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
              String tab = outputTabPane.getSelectionModel().getSelectedItem().getText();
              client.processToServer(tab, msgField.getText());
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
  
  public void print(String tab, String message, Paint color) {
      if (!userTabs.containsKey(tab)) addTab(tab);
      userTabs.get(tab).print(message, color);
  }
  
  public void addUser(String user, Paint color) {
      Button userButton = new Button(user);
      userButton.setTextFill(color);
      userButton.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
      
      userButton.setOnMouseClicked(event -> {
          if (event.getButton().toString().equals("PRIMARY")) {
              addTab(user);
          } else {
              System.out.println(user + " clicked with " + event.getButton());
          }
      });
      userButton.setOnMouseEntered(event -> {
          userButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
      });
      userButton.setOnMouseExited(event -> {
          userButton.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
      });
      userPanel.getChildren().add(userButton);
      userButtons.put(user, userButton);
  }
  
  public void removeUser(String user) {
      userPanel.getChildren().remove(userButtons.get(user));
      userButtons.remove(user);
  }
  
  public void addTab(String user) {
      ChatFlowTab userTab = new ChatFlowTab(user, true);
      userTabs.put(user, userTab);
      outputTabPane.getTabs().add(userTab);
      outputTabPane.getSelectionModel().select(userTab);
  }
  
  public static void main(String[] args) throws IOException {
      launch(args);
  }
}
