import java.lang.*;
import javax.swing.SwingUtilities;
import java.io.PrintWriter;
import java.io.IOException;

public class Swapping {

	public Swapping() {
		
	}

	public void swap(int blockNR, String[] block) {
		PrintWriter writer = null;
		try {
        	// Open the file for writing.
        	writer = new PrintWriter(blockNR + ".txt", "UTF-8");
        	for(int i = 0; i < block.length; i++) {
				writer.println(block[i]);	
        	}
        	writer.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
   		}

	}
}