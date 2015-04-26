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
   	private char[] ds = {'9', '9', '9', '9'};       // Data segmentas
   	private char[] cs = {'9', '9', '9', '9'};       // Kodo segmentas
   	private char[] ss = {'9', '9', '9', '9'};       // Steko segmentas
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

      /*
         FLAGS :
         0 - Sign flag (value : 01)
         1 - Zero flag (value : 02)
      */

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
         ci = new CommandsInterpretator();

         // setting PTR register
         setPTR(ram.newPageTable());
         initStack();
         setESP(new char[]{'0', '0', 'F', 'F'});
         initDataSegment();
         initCodeSegment();


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
            String activeVMBlock = new String( ram.getActiveVMblockForSwapping(getPTR(), getDS(), getSS(), getCS() ) );
            int pageTablePlaceForActiveBlock = ram.getPageTablePlaceForActiveBlock(getPTR(), activeVMBlock);
            int activeBlockNr = Utilities.getInstance().hexToDec(activeVMBlock);
            
            swapping.swap(pageTablePlaceForActiveBlock, ram.getBlock(activeBlockNr / Memory.NUMBER_OF_WORDS));
            ram.setBlockInactive(Utilities.getInstance().hexToDec(new String(getPTR())), pageTablePlaceForActiveBlock);

            ram.setWord(Utilities.getInstance().hexToDec(new String(getPTR())), (Memory.NUMBER_OF_WORDS - Memory.NUMBER_OF_STACK_BLOCK), activeVMBlock.toCharArray());
            setSS(Utilities.getInstance().decToHex(ssAddress).toCharArray());            
         } 
      }

      public void initDataSegment() {
         String findActiveVmBlock = new String( ram.getActiveVMblockForSwapping( getPTR(), getDS(), getSS(), getCS() ) ); 
         System.out.println("active data segment = " + findActiveVmBlock);
         int block = Utilities.getInstance().hexToDec(findActiveVmBlock);
         for(int i = 0; i < ram.NUMBER_OF_WORDS - 1; i++) {
            ram.setWord(block, i, Utilities.getInstance().decToHex(i).toCharArray());
         }
         ram.setWord(block, 255, new char[] {'F', 'F', 'F', 'F'});
         int pageTablePlaceForActiveBlock = ram.getPageTablePlaceForActiveBlock(getPTR(), findActiveVmBlock);
         setDS(Utilities.getInstance().decToHex(pageTablePlaceForActiveBlock).toCharArray());
      }

      public void initCodeSegment() {
         String findActiveVmBlock = new String( ram.getActiveVMblockForSwapping( getPTR(), getDS(), getSS(), getCS() ) ); 
         int pageTablePlaceForActiveBlock = ram.getPageTablePlaceForActiveBlock(getPTR(), findActiveVmBlock);
         System.out.println(pageTablePlaceForActiveBlock);
         setCS(Utilities.getInstance().decToHex(pageTablePlaceForActiveBlock).toCharArray());
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
         int memoryBlockForCode = Utilities.getInstance().charToInt(ram.getWord(0, Utilities.getInstance().charToInt(getCS())));
         convertedCode = code.toArray(convertedCode);
         loadCodeToMemory(memoryBlockForCode, convertedCode);
         ci = new CommandsInterpretator();
         GraphicalUserInterface.getInstance().loadCodeToWritingArea(convertedCode);
      }

      public void loadCodeToMemory(int blockNumber, String[] code) {
         if(code.length > 255) {
            System.out.println("Too big code!!"); //is it enough??
         }
         else {
            for(int i = 0; i < code.length; ++i) {
            ram.setWord(blockNumber, i, code[i].toCharArray());
            GraphicalUserInterface.getInstance().updateRAMCell(blockNumber * 256 + i, new String(ram.getWord(blockNumber, i)));
            }
         }
      }

      public String getCodeFromMemory(int ip) {
         int blockNumber = Utilities.getInstance().charToInt(ram.getWord(0, Utilities.getInstance().charToInt(getCS())));
         return new String(ram.getWord(blockNumber, ip));
      }

      public void test() {
         int pi = Utilities.getInstance().charToInt(getPI(), 16);
         int ti = Utilities.getInstance().charToInt(getTI(), 16);
         int si = Utilities.getInstance().charToInt(getSI(), 16);
         int ioi = Utilities.getInstance().charToInt(getIOI(), 16);
         if(pi > 0) {
            switch(pi) {
               case 1: GraphicalUserInterface.getInstance().setOutputText("Division by 0");
                       System.out.println("Division by 0"); //done
                       break;
               case 2: GraphicalUserInterface.getInstance().setOutputText("Negative value");
                       System.out.println("Negative value"); //done
                       break;
               case 3: GraphicalUserInterface.getInstance().setOutputText("Wrong address");
                       System.out.println("Wrong address");
                       break;
               case 4: GraphicalUserInterface.getInstance().setOutputText("Stack is full");
                       System.out.println("Stack is full"); //done
                       break;
               case 5: GraphicalUserInterface.getInstance().setOutputText("Stack is empty");
                       System.out.println("Stack is empty"); //done
                       break;
               case 6: GraphicalUserInterface.getInstance().setOutputText("Overflow");
                       System.out.println("Overflow");
                       break;
               case 7: GraphicalUserInterface.getInstance().setOutputText("Wrong operation code");
                       System.out.println("Wrong operation code"); //done
                       break;
               case 8: GraphicalUserInterface.getInstance().setOutputText("DS changed");
                       System.out.println("DS changed");  //done (not sure all)
                       break;
               case 9: GraphicalUserInterface.getInstance().setOutputText("CS changed");
                       System.out.println("CS changed");  //done (not sure all)
                       break;
               case 10: GraphicalUserInterface.getInstance().setOutputText("SS changed");
                        System.out.println("SS changed"); //done (not sure all)
                        break;
            }
         }
         if(ti == 1) {
            System.out.println("Time is out");
         }
         if(si > 0) {
            switch(si) {
               case 1: GraphicalUserInterface.getInstance().setOutputText("Input command");
                       System.out.println("Input command");
                       break;
               case 2: GraphicalUserInterface.getInstance().setOutputText("Output command");
                       System.out.println("Output command");
                       break;
               case 3: GraphicalUserInterface.getInstance().setOutputText("HALT");
                       System.out.println("HALT");  //done
                       break;
            }
         }
         if(ioi > 0) {
             switch(ioi) {
               case 1: GraphicalUserInterface.getInstance().setOutputText("Interrupt in 2nd channel");
                       System.out.println("Interrupt in 1st channel");
                       break;
               case 2: GraphicalUserInterface.getInstance().setOutputText("Interrupt in 2nd channel");
                       System.out.println("Interrupt in 2nd channel");
                       break;
               case 3: GraphicalUserInterface.getInstance().setOutputText("Interrupt in 2nd channel");
                       System.out.println("Interrupt in 3rd channel");
                       break;
            }
         }
      }

   	// =========== SETERS AND GETTERS ===========
      public boolean getSF() {
         int value = Utilities.getInstance().charToInt(getFLAGS(), 16);
         return (value == 1 || value == 3);
      }

      public boolean getZF() {
         int value = Utilities.getInstance().charToInt(getFLAGS(), 16);
         return (value == 2 || value == 3);
      }

   	public void setESP(char[] reg) {
         if(Utilities.getInstance().charToInt(reg, 16) < 256) {
            this.esp = reg;   
         }
   		else {
            System.out.println("Too big ESP set");
         }
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
      public char[] incReg(char[] value) {
         int decValue = Utilities.getInstance().charToInt(value, 16);
         if(decValue + 1 == 255) {
            System.out.println("Cannot increase!");
            return value;
         }
         else {
            ++decValue;
            char[] hex = Integer.toHexString(decValue).toCharArray();
            return hex;
         }
      }
      public char[] decReg(char[] value) {
         int decValue = Utilities.getInstance().charToInt(value, 16);
         if(decValue - 1 < 0) {
            System.out.println("Cannot decrease!");
            return value;
         }
         else {
            --decValue;
            char[] hex = Integer.toHexString(decValue).toCharArray();
            return hex;
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
         if(Utilities.getInstance().charToInt(reg, 16) < 256) {
            this.ip = reg;
         }
         else {
            System.out.println("Too big IP set");
         }
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

      public Swapping getSwapping() {
         return this.swapping;
      }
   	// ============================================

}