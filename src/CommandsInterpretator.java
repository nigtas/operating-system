public class CommandsInterpretator {

	CommandsInterpretator() {
	}

	public void executeCommand() {
		int executionLine = Utilities.getInstance().charToInt(RealMachine.getInstance().getIP(), 16);
		GraphicalUserInterface.getInstance().highlightCurrentCodeLine(executionLine);
		String command = RealMachine.getInstance().getCodeFromMemory(executionLine);
		String executionCode = command.substring(0, 2);
		System.out.println("ip " + executionLine + " command " + command + " code" + executionCode);
		RealMachine.getInstance().setIP(RealMachine.getInstance().incReg(RealMachine.getInstance().getIP()));
		GraphicalUserInterface.getInstance().setRegisters(RealMachine.getInstance().collectAllRegisters());
		switch(executionCode) {
			case "LD" : ld(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "PT" : pt(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "0A" : add();
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "0S" : sub();
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "0M" : mul();
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "0D" : div();
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "CD" : cds(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "CC" : ccs(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "CS" : css(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "JP" : jp(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "JL" : jl(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "JG" : jg(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "JE" : je(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "GD" : gd(command.substring(2, 4));
						break;
			case "PD" : pd(command.substring(2, 4));
						break;
			case "HA" : //TODO stop IP after halt
					    halt();
						break;
						//TODO check, all code start with PROG
			case "PR" : break;
			default : RealMachine.getInstance().setPI(new char[] {'0', '7'});
					  return;
		}
		
		if(Utilities.getInstance().charToInt(RealMachine.getInstance().getTM(), 16) == 0) {
			RealMachine.getInstance().setTI(new char[] {'0', '1'});
		}
		GraphicalUserInterface.getInstance().setRegisters(RealMachine.getInstance().collectAllRegisters());
		RealMachine.getInstance().test();
	}

	/*commands functions*/
	public void ld(String elements) {
		//TODO check address
		int ds = Utilities.getInstance().charToInt(RealMachine.getInstance().getDS(), 16);
		int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
		int block = Utilities.getInstance().charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ds), 16);
		int place = Integer.parseInt(elements, 16);
		char[] valueFromMemory = RealMachine.getInstance().getRAM().getWord(block, place);			
		int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
		int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
		int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if(RealMachine.getInstance().decESP()){
			RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueFromMemory);
			GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '4'});
		}
	}

	public void pt(String elements) {
		//TODO check address
		int ds = Utilities.getInstance().charToInt(RealMachine.getInstance().getDS(), 16);
		int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
		int block = Utilities.getInstance().charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ds), 16);
		int place = Integer.parseInt(elements, 16);
		int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
		int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
		if(RealMachine.getInstance().incESP()){
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			char[] valueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop);
			RealMachine.getInstance().getRAM().nullWord(stackBlock, stackTop);
			RealMachine.getInstance().getRAM().setWord(block, place, valueFromStack);
			GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));		
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '5'});
		}
	}

	public void add() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if(espValue > 253){
			System.out.println("Not enough elements!");
		}
		else {
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				//TODO check if not above 255
				int sum = Utilities.charToInt(firstValueFromStack, 16) + Utilities.charToInt(secondValueFromStack, 16);
				char[] valueToAdd = (Integer.toHexString(sum)).toCharArray();
				RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueToAdd);
				GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));	
			}
			else {
				RealMachine.getInstance().setPI(new char[] {'0', '4'});
			}
		}
	}

	public void sub() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if(espValue > 253){
			System.out.println("Not enough elements!");
		}
		else {
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			if(Utilities.charToInt(firstValueFromStack) < Utilities.charToInt(secondValueFromStack)) {
				RealMachine.getInstance().setPI(new char[] {'0', '2'});
				return;
			}
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				//TODO check if not negative
				int sub = Utilities.charToInt(firstValueFromStack, 16) - Utilities.charToInt(secondValueFromStack, 16);
				char[] valueToAdd = (Integer.toHexString(sub)).toCharArray();
				RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueToAdd);
				GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));	
			}
			else {
				RealMachine.getInstance().setPI(new char[] {'0', '4'});
			}
		}
	}

	public void mul() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if(espValue > 253){
			System.out.println("Not enough elements!");
		}
		else {
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				int mul = Utilities.charToInt(firstValueFromStack, 16) * Utilities.charToInt(secondValueFromStack, 16);
				if(mul > 255) {
					RealMachine.getInstance().setPI(new char[] {'0', '6'});
					return;
				}
				else {
					char[] valueToAdd = (Integer.toHexString(mul)).toCharArray();
					RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueToAdd);
					GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));	
				}
			}
			else {
				RealMachine.getInstance().setPI(new char[] {'0', '4'});
			}
		}
	}

	public void div() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if (espValue > 253) {
			System.out.println("Not enough elements!");
		} else {
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			if(Utilities.charToInt(firstValueFromStack) == 0) {
				RealMachine.getInstance().setPI(new char[] {'0', '1'});
				return;
			}
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if (RealMachine.getInstance().decESP()) {
				int div = Utilities.charToInt(secondValueFromStack, 16) / Utilities.charToInt(firstValueFromStack, 16);
				int mod = Utilities.charToInt(secondValueFromStack, 16) % Utilities.charToInt(firstValueFromStack, 16);
				char[] valueToAddDiv = (Integer.toHexString(div)).toCharArray();
				char[] valueToAddMod = (Integer.toHexString(mod)).toCharArray();
				RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueToAddDiv);
				GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));	
				stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
				/* liekana */
				if (RealMachine.getInstance().decESP()) {
					RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueToAddMod);
					GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));	
				} else {
					System.out.println("Stack is full! No place for mod.");
					RealMachine.getInstance().setPI(new char[] {'0', '4'});
				}
			} else {
				RealMachine.getInstance().setPI(new char[] {'0', '4'});
			}
		}
	}

	public void cds(String elements) {
		int place = Integer.parseInt(elements, 16);
		if(place < 256) {
			RealMachine.getInstance().setDS(elements.toCharArray());
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
			char[] block = RealMachine.getInstance().getRAM().getWord(ptr, place);
			if(block == new char[] {'-', '-', '-', '-'}) {

			}
			else {

			}
			RealMachine.getInstance().setPI(new char[] {'0', '8'});
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '3'});
		}
	}

	public void ccs(String elements) {
		int place = Integer.parseInt(elements, 16);
		if(place < 256) {
			RealMachine.getInstance().setDS(elements.toCharArray());
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
			char[] block = RealMachine.getInstance().getRAM().getWord(ptr, place);
			if(block == new char[] {'-', '-', '-', '-'}) {

			}
			else {
				
			}
			RealMachine.getInstance().setPI(new char[] {'0', '9'});
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '3'});
		}
	}

	public void css(String elements) {
		int place = Integer.parseInt(elements, 16);
		if(place < 256) {
			RealMachine.getInstance().setDS(elements.toCharArray());
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
			char[] block = RealMachine.getInstance().getRAM().getWord(ptr, place);
			if(block == new char[] {'-', '-', '-', '-'}) {

			}
			else {
				
			}
			RealMachine.getInstance().setPI(new char[] {'0', 'A'});
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '3'});
		}
	}

	public void jp(String elements) {
		int place = Integer.parseInt(elements, 16);
		System.out.println("jump " + place);
		if(place < 256) {
			RealMachine.getInstance().setIP(elements.toCharArray());
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '3'});
		}
	}

	public void jl(String elements) {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if (espValue > 254) {
			RealMachine.getInstance().setPI(new char[] {'0', '5'});
		}
		else {
			int place = Integer.parseInt(elements, 16);
			System.out.println("jumpl " + place);
			if(place < 256) {
				int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
				int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
				int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
				int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
				char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
				if(Utilities.getInstance().charToInt(firstValueFromStack, 16) == 0) {
					RealMachine.getInstance().setIP(elements.toCharArray());
				}
				else System.out.println("Top element not 0!");
			}
			else {
				RealMachine.getInstance().setPI(new char[] {'0', '3'});
			}
		}
	}

	public void jg(String elements) {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		int place = Integer.parseInt(elements, 16);
		System.out.println("jumpg " + place);
		if (espValue > 254) {
			RealMachine.getInstance().setPI(new char[] {'0', '5'});
		}
		else {
			if(place < 256) {
				int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
				int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
				int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
				int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
				char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
				if(Utilities.getInstance().charToInt(firstValueFromStack, 16) == 2) {
					RealMachine.getInstance().setIP(elements.toCharArray());
				}
				else System.out.println("Top element not 2!");
			}
			else {
				RealMachine.getInstance().setPI(new char[] {'0', '3'});
			}
		}
	}

	public void je(String elements) {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		int place = Integer.parseInt(elements, 16);
		System.out.println("jumpe " + place);
		if (espValue > 254) {
			RealMachine.getInstance().setPI(new char[] {'0', '5'});
		}
		else {
			if(place < 256) {
				int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
				int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
				int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
				int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
				char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
				System.out.println("value " + new String(firstValueFromStack) + "block " + stackBlock + "");
				if(Utilities.getInstance().charToInt(firstValueFromStack, 16) == 1) {
					RealMachine.getInstance().setIP(elements.toCharArray());
				}
				else System.out.println("Top element not 0!");
			}
			else {
				RealMachine.getInstance().setPI(new char[] {'0', '1'});
			}
		}
	}

	public void gd(String elements) {
	}

	public void pd(String elements) {

	}

	public void halt() {
		char[] chars = {'0', '3'};
		RealMachine.getInstance().setSI(chars);
	}
}