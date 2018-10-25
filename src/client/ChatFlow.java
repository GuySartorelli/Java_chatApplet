package client;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatFlow extends ScrollPane{
    
    private TextFlow outputArea;
    private final Paint BG_COLOR = Color.WHITE;
    private final Paint BORDER_COLOR = Color.GRAY;
    
    public ChatFlow() {
        outputArea = new TextFlow();
        this.setContent(outputArea);
        this.setFocusTraversable(false);
        this.setFitToHeight(true);
        this.setFitToWidth(true);
        outputArea.setFocusTraversable(false);
        outputArea.setPadding(new Insets(5));
        outputArea.setBackground(new Background(new BackgroundFill(BG_COLOR, null, null)));
        outputArea.setBorder(new Border(new BorderStroke(BORDER_COLOR, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
        outputArea.getChildren().addListener((ListChangeListener<Node>)(change)->{
            this.setVvalue(1);
        });
    }
    
    public void print(String message, Paint color) {
        Text text = new Text(message+"\n");
        text.setFill(color);
        outputArea.getChildren().add(text);
    }
}
