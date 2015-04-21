public class CommandsInterpretator {
	private String[] code;
	private int executionLine = 0;

	CommandsInterpretator(String[] code) {
		setCode(code);
	}

	public void executeCommand() {
		GraphicalUserInterface.getInstance().highlightCurrendCodeLine(executionLine);
		if(code.length >= (executionLine + 1)) {
			String command = code[executionLine];
			System.out.println(command.substring(0, 2));
			String executionCode = command.substring(0, 2);
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
			++executionLine;
		}
			
	}

	/*commands functions*/
	public void ld(String elements) {
		int block = Utilities.charToInt(RealMachine.getInstance().getDS());
		int place = Integer.parseInt(elements, 16);
		char[] valueFromMemory = RealMachine.getInstance().getRAM().getWord(block, place);
		int stackPlace = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) / 256;
		int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		System.out.println("place " + stackPlace + " top " + (stackTop/stackPlace - 256) + " value " + new String(valueFromMemory));
		if(RealMachine.getInstance().decESP()){
			RealMachine.getInstance().getRAM().setWord(stackPlace, (stackTop/stackPlace - 256), valueFromMemory);
			GraphicalUserInterface.getInstance().updateRAMCell(stackPlace * stackTop, new String(valueFromMemory));
		}
		else {
			System.out.println("Stack is full!");
		}
	}

	public void pt(String elements) {
		int stackPlace = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
		int block = Utilities.charToInt(RealMachine.getInstance().getDS());\
		int place = Integer.parseInt(elements, 16);
		if(RealMachine.getInstance().incESP()){
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			char[] valueFromStack = RealMachine.getInstance().getRAM().getWord(stackPlace, stackTop);
			System.out.println("place " + stackPlace + " top " + (stackTop/stackPlace - 256) + " value " + new String(valueFromStack));
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
				System.out.println("first value " + new String(firstValueFromStack) +
				" second value " + new String(secondValueFromStack) + " value " + new String(valueToAdd));
				RealMachine.getInstance().getRAM().setWord(stackPlace/256, stackTop, valueToAdd);	
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
				System.out.println("first value " + new String(firstValueFromStack) +
				" second value " + new String(secondValueFromStack) + " value " + new String(valueToAdd));
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
				System.out.println("first value " + new String(firstValueFromStack) +
				" second value " + new String(secondValueFromStack) + " value " + new String(valueToAdd));
				RealMachine.getInstance().getRAM().setWord(stackPlace/256, stackTop, valueToAdd);	
			}
			else {
				System.out.println("Stack is full!");
			}
		}
	}

	public void div() {

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
		System.out.println(RealMachine.getInstance().getSI());
		char[] chars = {'0', '3'};
		RealMachine.getInstance().setSI(chars);
		System.out.println(RealMachine.getInstance().getSI());
	}

	//setters, getters
	public void setCode(String[] code) {
		this.code = code;
	}

	public String[] getCode() {
		return code;
	}

}