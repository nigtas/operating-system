import java.lang.*;
import javax.swing.*;

public class RealMachine {
	private static RealMachine instance = null;
	private VirtualMachine vm = null;
	private Memory ram = null;
   //commands interpretator
   private CommandsInterpretator ci = new CommandsInterpretator();

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
   		vm = new VirtualMachine();

   		char[][] commands = new char[15][4];
   		commands[0][0] = 'L';
   		commands[0][1] = 'D';
   		commands[0][2] = 'x';
   		commands[0][3] = 'y';

   		commands[1][0] = 'P';
   		commands[1][1] = 'T';
   		commands[1][2] = 'x';
   		commands[1][3] = 'y';

   		commands[2][0] = '0';
   		commands[2][1] = 'A';
   		commands[2][2] = 'D';
   		commands[2][3] = 'D';

   		commands[3][0] = '0';
   		commands[3][1] = 'S';
   		commands[3][2] = 'U';
   		commands[3][3] = 'B';

   		commands[4][0] = '0';
   		commands[4][1] = 'M';
   		commands[4][2] = 'U';
   		commands[4][3] = 'L';

   		commands[5][0] = '0';
   		commands[5][1] = 'D';
   		commands[5][2] = 'I';
   		commands[5][3] = 'V';

   		commands[6][0] = 'C';
   		commands[6][1] = 'D';
   		commands[6][2] = 'S';
   		commands[6][3] = 'x';

   		commands[7][0] = 'C';
   		commands[7][1] = 'C';
   		commands[7][2] = 'S';
   		commands[7][3] = 'x';

   		commands[8][0] = 'C';
   		commands[8][1] = 'S';
   		commands[8][2] = 'S';
   		commands[8][3] = 'x';

   		commands[9][0] = 'J';
   		commands[9][1] = 'P';
   		commands[9][2] = 'x';
   		commands[9][3] = 'y';

   		commands[10][0] = 'J';
   		commands[10][1] = 'E';
   		commands[10][2] = 'x';
   		commands[10][3] = 'y';

   		commands[11][0] = 'J';
   		commands[11][1] = 'L';
   		commands[11][2] = 'x';
   		commands[11][3] = 'y';

   		commands[12][0] = 'J';
   		commands[12][1] = 'G';
   		commands[12][2] = 'x';
   		commands[12][3] = 'y';

   		commands[13][0] = 'G';
   		commands[13][1] = 'D';
   		commands[13][2] = 'x';
   		commands[13][3] = 'y';

   		commands[14][0] = 'P';
   		commands[14][1] = 'D';
   		commands[14][2] = 'x';
   		commands[14][3] = 'y';



         // initializing COMMAND array
         int block = ram.getFreeBlock();
   		for(int i = 0; i < commands.length; i++) {
               ram.setWord(block, i, commands[i]);
   		}

         // setting PTR register
         setPTR(ram.newPageTable());

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

      public void execute(String[] code) {
         ci.executeCommand(code);
      }

   	// =========== SETERS AND GETTERS ===========
   	public void setESP(char[] reg) {
   		this.esp = reg;
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
   	// ============================================

}