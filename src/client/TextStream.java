package client;

import java.io.IOException;
import java.io.OutputStream;

import javafx.scene.control.TextArea;

/**
 * @author rallion
 * OutputStream to allow NumberGame to directly print into output TextArea
 * see https://stackoverflow.com/questions/13841884/redirecting-system-out-to-a-textarea-in-javafx
 */
public class TextStream extends OutputStream {
	
	private TextArea output;
	
	/**
	 * Constructor to define the output TextArea
	 * @param TextArea area
	 */
	public TextStream(TextArea area) {
		output = area;
	}

	/**
	 * Appends stream to output TextArea
	 */
	@Override
	//apparently chars are stored internally as int
	public void write(int out) throws IOException {
		output.appendText(String.valueOf((char) out));
	}

}
