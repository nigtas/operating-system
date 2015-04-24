import java.lang.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class RealMachine {
	private static RealMachine instance = null;
	private VirtualMachine vm = null;
	private Memory ram = null;
   private Swapping swapping = null;
   //commands interpretator
   private CommandsInterpretator ci = null;

	// Registers
	private char[] esp = {'0', '0', '0', '0'};      // Steko rodykles registras
	private char[] ds = {'0', '0', '0', '0'};       // Data segmentas
	private char[] cs = {'0', '0', '0', '0'};       // Kodo segmentas
	private char[] ss = {'0', '0', '0', '0'};       // Steko segmentas
	private char[] ptr = {'0', '0', '0', '0'};      // Puslapiu lenteles registras
	private char[] ip = {'0', '0'}; 				      // VM programos skaitiklias
	private char[] flags = {'0', '0'}; 				   // Pozymiu registras
	private char[] c = {'1', '1'};					   // Kanalo registas
	private char[] ti = {'0', '0'};                 // Taimerio pertraukimo registras
	private char[] pi = {'0', '0'};                 // Programiniu pertraukimu registras  
	private char[] si = {'0', '0'};                 // Supervizoriniu pertraukimu registras 
	private char[] ioi = {'0', '0'};                // I/O registas 
	private char[] mode = {'0', '1'};               // MODE registas
	private char[] tm = {'0', '9'};                 // TM registras

	protected RealMachine() {
      try {
         SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
               GraphicalUserInterface.getInstance();
            }
         });
      } catch (Exception e) {

      }
		
		initRM();
	}

	public static RealMachine getInstance() {
		if(instance == null) {
			instance = new RealMachine();
		}
		return instance;
   	}	

   	private void initRM() {
   		ram = new Memory();
         swapping = new Swapping();

         // setting PTR register
         setPTR(ram.newPageTable());
         initStack();

   		char[][] memory = ram.getMemory();
   		String word = "";
   		for(int i = 0; i < ram.NUMBER_OF_BLOCKS * ram.NUMBER_OF_WORDS; i++) {
   			word = "";
   			for(int j = 0; j < ram.WORD_SIZE; j++) {
   				word += memory[i][j];
   			}
   			GraphicalUserInterface.getInstance().printDataToRAMCell(i, word);
   		}

   		GraphicalUserInterface.getInstance().getRAMJList().setModel(GraphicalUserInterface.getInstance().getRAMModel());
   		GraphicalUserInterface.getInstance().setRegisters(collectAllRegisters());
		   GraphicalUserInterface.getInstance().appendOutputText("...READY!\n");
   	}


   	/* ARRAY OF REGISTERS 
   		0 - ESP
   		1 - DS
   		2 - CS
   		3 - SS
   		4 - PTR
   		5 - MODE
   		6 - FLAGS
   		7 - IOI
   		8 - PI
   		9 - SI
   		10 - TI
   		11 - TM
   		12 - IP
   		13 - C
	   */

   	public String[] collectAllRegisters() {
   		String[] array = new String[14];
   		array[0] = new String(esp);
   		array[1] = new String(ds);
   		array[2] = new String(cs);
   		array[3] = new String(ss);
   		array[4] = new String(ptr);
   		array[5] = String.valueOf(mode);
   		array[6] = new String(flags);
   		array[7] = String.valueOf(ioi);
   		array[8] = String.valueOf(pi);
   		array[9] = String.valueOf(si);
   		array[10] = String.valueOf(ti);
   		array[11] = String.valueOf(tm);
   		array[12] = new String(ip);
   		array[13] = new String(c);
   		return array;
   	}

      public void initStack() {
         int ssAddress = Utilities.getInstance().hexToDec(new String(getPTR())) + (Memory.NUMBER_OF_WORDS - Memory.NUMBER_OF_STACK_BLOCK);         

         String ssValue = new String(ram.getWord(ssAddress - (Memory.NUMBER_OF_WORDS - Memory.NUMBER_OF_STACK_BLOCK), ssAddress));
         if(ssValue.equals("----")) {
            String activeVMBlock = new String( ram.getActiveVMblock(getPTR()) );
            int pageTablePlaceForActiveBlock = ram.getPageTablePlaceForActiveBlock(getPTR(), activeVMBlock);
            int activeBlockNr = Utilities.getInstance().hexToDec(activeVMBlock);
            
            swapping.swap(activeBlockNr / Memory.NUMBER_OF_WORDS, ram.getBlock(activeBlockNr / Memory.NUMBER_OF_WORDS));
            ram.setBlockInactive(Utilities.getInstance().hexToDec(new String(getPTR())), pageTablePlaceForActiveBlock);

            ram.setWord(Utilities.getInstance().hexToDec(new String(getPTR())), (Memory.NUMBER_OF_WORDS - Memory.NUMBER_OF_STACK_BLOCK), activeVMBlock.toCharArray());
            setSS(Utilities.getInstance().decToHex(ssAddress).toCharArray());

            // set esp
            // setESP()
            
         } 
      }

      public void execute() {
         ci.executeCommand();
      }

      public void loadCode() {
         List<String> code = new ArrayList<String>();
         BufferedReader br = null;
         try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader("code.txt"));
            while ((sCurrentLine = br.readLine()) != null) {
               code.add(sCurrentLine);
            } 
         }
         catch (IOException e) {
            e.printStackTrace();
         }
         finally {
            try {
               if (br != null)br.close();
            }  
            catch (IOException ex) {
               ex.printStackTrace();
            }
         }
         String[] convertedCode = new String[code.size()];
         convertedCode = code.toArray(convertedCode);
         System.out.println(convertedCode[1]);
         ci = new CommandsInterpretator(convertedCode);
         GraphicalUserInterface.getInstance().loadCodeToWritingArea(convertedCode);
      }

   	// =========== SETERS AND GETTERS ===========
   	public void setESP(char[] reg) {
   		this.esp = reg;
   	}
      public boolean incESP(){
         int decValue = Utilities.charToInt(getESP(), 16);
         int decValueSS = Utilities.charToInt(getSS(), 16);
         if(decValue - decValueSS == 255) {
            return false;
         }
         else {
            ++decValue;
            String hex = Integer.toHexString(decValue);
            setESP(hex.toCharArray());
            return true;
         }
      }
      public boolean decESP() {
         int decValue = Utilities.charToInt(getESP(), 16);
         int decValueSS = Utilities.charToInt(getSS(), 16);
         if(decValue - decValueSS == 0) {
            return false;
         }
         else {
            --decValue;
            String hex = Integer.toHexString(decValue);
            setESP(hex.toCharArray());
            return true;
         }
      }
   	public void setDS(char[] reg) {
   		this.ds = reg;
   	}
   	public void setCS(char[] reg) {
   		this.cs = reg;
   	}
   	public void setSS(char[] reg) {
   		this.ss = reg;
   	}
   	public void setPTR(char[] reg) {
   		this.ptr = reg;
   	}
   	public void setIP(char[] reg) {
   		this.ip = reg;
   	}
   	public void setFLAGS(char[] reg) {
   		this.flags = reg;
   	}
   	public void setC(char[] reg) {
   		this.c = reg;
   	}
   	public void setTI(char[] reg) {
   		this.ti = reg;
   	}
   	public void setPI(char[] reg) {
   		this.pi = reg;
   	}
   	public void setSI(char[] reg) {
   		this.si = reg;
   	}
   	public void setIOI(char[] reg) {
   		this.ioi = reg;
   	}
   	public void setMODE(char[] reg) {
   		this.mode = reg;
   	}
   	public void setTM(char[] reg) {
   		this.tm = reg;
   	}
   	public char[] getESP() {
   		return this.esp;
   	}
   	public char[] getDS() {
   		return this.ds;
   	}
   	public char[] getCS() {
   		return this.cs;
   	}
   	public char[] getSS() {
   		return this.ss;
   	}
   	public char[] getPTR() {
   		return this.ptr;
   	}
   	public char[] getIP() {
   		return this.ip;
   	}
   	public char[] getFLAGS() {
   		return this.flags;
   	}
   	public char[] getC() {
   		return this.c;
   	}
   	public char[] getTI() {
   		return this.ti;
   	}
   	public char[] getPI() {
   		return this.pi;
   	}
   	public char[] getSI() {
   		return this.si;
   	}
   	public char[] getIOI() {
   		return this.ioi;
   	}
   	public char[] getMODE() {
   		return this.mode;
   	}
   	public char[] getTM() {
   		return this.tm;
   	}
      public Memory getRAM() {
         return this.ram;
      }
   	// ============================================

}