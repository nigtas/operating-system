public class CommandsInterpretator {
	private String[] code;

	CommandsInterpretator() {
	}

	public void executeCommand() {
		int executionLine = Utilities.getInstance().charToInt(RealMachine.getInstance().getIP(), 16);
		GraphicalUserInterface.getInstance().highlightCurrentCodeLine(executionLine);
		String command = RealMachine.getInstance().getCodeFromMemory(executionLine);
		String executionCode = command.substring(0, 2);
		System.out.println("code" + executionCode);
		switch(executionCode) {
			case "LD" : ld(command.substring(2, 4));
						break;
			case "PT" : pt(command.substring(2, 4));
						break;
			case "AD" : add();
						break;
			case "SU" : sub();
						break;
			case "MU" : mul();
						break;
			case "DI" : div();
						break;
			case "CD" : cds(command.substring(2, 3));
						break;
			case "CC" : ccs(command.substring(2, 3));
						break;
			case "CS" : css(command.substring(2, 3));
						break;
			case "JP" : jp(command.substring(2, 4));
						break;
			case "JL" : jl(command.substring(2, 4));
						break;
			case "JG" : jg(command.substring(2, 4));
						break;
			case "GD" : gd(command.substring(2, 4));
						break;
			case "PD" : pd(command.substring(2, 4));
						break;
			case "HA" : halt();
						break;
		}
		RealMachine.getInstance().setIP(RealMachine.getInstance().
			incReg(Utilities.getInstance().decToHex(executionLine).toCharArray()));
	}

	/*commands functions*/
	public void ld(String elements) {
		// int ds = Utilities.getInstance().charToInt(RealMachine.getInstance().getDS(), 16);
		int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getPTR(), 16);
		// System.out.println("ds " + ds + " ptr " + ptr);
		// int block = Utilities.getInstance().charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ds), 16);
		int place = Integer.parseInt(elements, 16);
		// System.out.println("LOAD block: " + block + " place: " + place);
		char[] valueFromMemory = RealMachine.getInstance().getRAM().getWord(0, place);			// 0 only for fuction chech, here must be block
		System.out.println("LOAD value from memory: " + new String(valueFromMemory));
		int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
		int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
		int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		 System.out.println("place " + stackBlock + " top " + stackTop + " value " + new String(valueFromMemory));
		if(RealMachine.getInstance().decESP()){
			RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueFromMemory);
			GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(valueFromMemory));
		}
		else {
			System.out.println("Stack is full!");
		}
	}

	public void pt(String elements) {
		int stackPlace = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
		int block = Utilities.charToInt(RealMachine.getInstance().getDS());
		int place = Integer.parseInt(elements, 16);
		if(RealMachine.getInstance().incESP()){
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			char[] valueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, stackTop);
			// System.out.println("place " + stackPlace + " top " + (stackTop/stackPlace - 256) + " value " + new String(valueFromStack));
			RealMachine.getInstance().getRAM().nullWord(stackPlace, (stackTop/stackPlace - 256));
			RealMachine.getInstance().getRAM().setWord(block/256, place, valueFromStack);	
		}
		else {
			System.out.println("Stack is empty!");
		}
	}

	public void add() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if(espValue > 253){
			System.out.println("Not enough elements!");
		}
		else {
			int stackPlace = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				int sum = Utilities.charToInt(firstValueFromStack, 16) + Utilities.charToInt(secondValueFromStack, 16);
				char[] valueToAdd = (Integer.toHexString(sum)).toCharArray();
				// System.out.println("first value " + new String(firstValueFromStack) +
				// " second value " + new String(secondValueFromStack) + " value " + new String(valueToAdd));
				RealMachine.getInstance().getRAM().setWord(stackPlace, stackTop, valueToAdd);	
			}
			else {
				System.out.println("Stack is full!");
			}
		}
	}

	public void sub() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if(espValue > 253){
			System.out.println("Not enough elements!");
		}
		else {
			int stackPlace = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				int sub = Utilities.charToInt(firstValueFromStack, 16) - Utilities.charToInt(secondValueFromStack, 16);
				char[] valueToAdd = (Integer.toHexString(sub)).toCharArray();
				// System.out.println("first value " + new String(firstValueFromStack) +
				// " second value " + new String(secondValueFromStack) + " value " + new String(valueToAdd));
				RealMachine.getInstance().getRAM().setWord(stackPlace/256, stackTop, valueToAdd);	
			}
			else {
				System.out.println("Stack is full!");
			}
		}
	}

	public void mul() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if(espValue > 253){
			System.out.println("Not enough elements!");
		}
		else {
			int stackPlace = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				int mul = Utilities.charToInt(firstValueFromStack, 16) * Utilities.charToInt(secondValueFromStack, 16);
				char[] valueToAdd = (Integer.toHexString(mul)).toCharArray();
				// System.out.println("first value " + new String(firstValueFromStack) +
					// " second value " + new String(secondValueFromStack) + " value " + new String(valueToAdd));
				RealMachine.getInstance().getRAM().setWord(stackPlace/256, stackTop, valueToAdd);	
			}
			else {
				System.out.println("Stack is full!");
			}
		}
	}

	public void div() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if (espValue > 253) {
			System.out.println("Not enough elements!");
		} else {
			int stackPlace = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) - 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if (RealMachine.getInstance().decESP()) {
				int div = Utilities.charToInt(secondValueFromStack, 16) / Utilities.charToInt(firstValueFromStack, 16);
				int mod = Utilities.charToInt(secondValueFromStack, 16) % Utilities.charToInt(secondValueFromStack, 16);
				char[] valueToAddDiv = (Integer.toHexString(div)).toCharArray();
				char[] valueToAddMod = (Integer.toHexString(mod)).toCharArray();
				// System.out.println("first value " + new String(firstValueFromStack) + 
					// " second value " + new String(secondValueFromStack) + " Div value " + new String(valueToAddDiv) + 
					// " Mod value " + new String(valueToAddMod));
				RealMachine.getInstance().getRAM().setWord(stackPlace/256, stackTop, valueToAddDiv);
				/* liekana */
				if (RealMachine.getInstance().decESP()) {
					RealMachine.getInstance().getRAM().setWord(stackPlace/256, stackTop, valueToAddMod);
				} else {
					System.out.println("Stack is full! No place for mod.");
				}
			} else {
				System.out.println("Stack is full!");
			}
		}
	}

	public void cds(String elements) {

	}

	public void ccs(String elements) {

	}

	public void css(String elements) {

	}

	public void jp(String elements) {

	}

	public void jl(String elements) {

	}

	public void jg(String elements) {

	}

	public void gd(String elements) {

	}

	public void pd(String elements) {

	}

	public void halt() {
		// System.out.println(RealMachine.getInstance().getSI());
		char[] chars = {'0', '3'};
		RealMachine.getInstance().setSI(chars);
		// System.out.println(RealMachine.getInstance().getSI());
	}

	//setters, getters
	public void setCode(String[] code) {
		this.code = code;
	}

	public String[] getCode() {
		return code;
	}

}