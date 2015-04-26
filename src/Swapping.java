import java.lang.*;
import javax.swing.SwingUtilities;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class Swapping {

	public Swapping() {}

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

    public String[] getBlockFromFile(int blockNR) {
        String filePathString = new String( blockNR + ".txt" );
        // System.out.println(filePathString);
        String[] arrayToReturn = new String[256];
        File f = new File(filePathString);   
        if(f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                int i = 0;
                while ((line = br.readLine()) != null) {
                   arrayToReturn[i] = line;
                   i++;
                }
                return arrayToReturn;
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        return null; 
    }
}