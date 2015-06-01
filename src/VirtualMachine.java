import java.lang.*;
import javax.swing.SwingUtilities;

public class VirtualMachine {
	private Memory vram = null;

	public VirtualMachine() {
		initVM();
	}

	private void initVM() {
		// setting PTR register
		RealMachine.getInstance().setDS(new char[] {'0', '0', '0', '0'});
		RealMachine.getInstance().setSS(new char[] {'0', '0', '0', '0'});
		RealMachine.getInstance().setCS(new char[] {'0', '0', '0', '0'});
        RealMachine.getInstance().setPTR(RealMachine.getInstance().getRAM().newPageTable());
        if(!new String(RealMachine.getInstance().getPTR()).equals("EROR")){
        	RealMachine.getInstance().countMaxPages();
	        RealMachine.getInstance().initStack();
	        RealMachine.getInstance().setESP(new char[]{'0', '0', 'F', 'F'});
	        RealMachine.getInstance().initDataSegment();
	        RealMachine.getInstance().initCodeSegment();


	   		char[][] memory = RealMachine.getInstance().getRAM().getMemory();
	   		String word = "";
	   		for(int i = 0; i < RealMachine.getInstance().getRAM().NUMBER_OF_BLOCKS * RealMachine.getInstance().getRAM().NUMBER_OF_WORDS; i++) {
	   			word = "";
	   			for(int j = 0; j < RealMachine.getInstance().getRAM().WORD_SIZE; j++) {
	   				word += memory[i][j];
	   			}
	   			GraphicalUserInterface.getInstance().updateRAMCell(i, word);
	   		}

	   		System.out.println("DS: " + new String(RealMachine.getInstance().getDS()) + " Cs:" + new String(RealMachine.getInstance().getCS()));
	   		GraphicalUserInterface.getInstance().getRAMJList().setModel(GraphicalUserInterface.getInstance().getRAMModel());
   			GraphicalUserInterface.getInstance().setRegisters(RealMachine.getInstance().collectAllRegisters());
			GraphicalUserInterface.getInstance().appendOutputText("...READY!\n");
        }
        else {
        	GraphicalUserInterface.getInstance().getRAMJList().setModel(GraphicalUserInterface.getInstance().getRAMModel());
   			GraphicalUserInterface.getInstance().setRegisters(RealMachine.getInstance().collectAllRegisters());
        }

        


   		
	}


}