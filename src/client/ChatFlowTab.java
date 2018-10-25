package client;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatFlowTab extends Tab {
    
    private TextFlow outputArea;
    private final Paint BG_COLOR = Color.WHITE;
    private final Paint BORDER_COLOR = Color.GRAY;
    
    public ChatFlowTab(String name, boolean canClose) {
        this.setClosable(canClose);
        this.setText(name);
        outputArea = new TextFlow();
        ScrollPane outputScroll = new ScrollPane();
        this.setContent(outputScroll);
        outputScroll.setContent(outputArea);
        outputScroll.setFocusTraversable(false);
        outputScroll.setFitToHeight(true);
        outputScroll.setFitToWidth(true);
        outputArea.setFocusTraversable(false);
        outputArea.setPadding(new Insets(5));
        outputArea.setBackground(new Background(new BackgroundFill(BG_COLOR, null, null)));
        outputArea.setBorder(new Border(new BorderStroke(BORDER_COLOR, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
        outputArea.getChildren().addListener((ListChangeListener<Node>)(change)->{
            outputScroll.setVvalue(1);
        });
    }
    
    public void print(String message, Paint color) {
        Text text = new Text(message+"\n");
        text.setFill(color);
        outputArea.getChildren().add(text);
    }
}
