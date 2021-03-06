import java.util.Arrays;
import java.io.*;




public class CommandsInterpretator {

	CommandsInterpretator() {
	}

	public void executeCommand() {
		if(Utilities.getInstance().charToInt(RealMachine.getInstance().getSI(), 16) == 3 || Utilities.getInstance().charToInt(RealMachine.getInstance().getPI(), 16) == 7) {
			return;
		}

		int executionLine = Utilities.getInstance().charToInt(RealMachine.getInstance().getIP(), 16);
		GraphicalUserInterface.getInstance().highlightCurrentCodeLine(executionLine);
		String command = RealMachine.getInstance().getCodeFromMemory(executionLine);
		String executionCode = command.substring(0, 2);

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
						if(!RealMachine.getInstance().decThreeTM()) {
							int sk = Utilities.getInstance().charToInt(RealMachine.getInstance().getTM(), 16);
							switch(sk) {
								case 1 :
									RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
									break;
								case 2 :
									RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
									RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
									break;
								default :
							}
						} else {

						}
						break;
			case "PD" : pd(command.substring(2, 4));
						if(!RealMachine.getInstance().decThreeTM()) {
							int sk = Utilities.getInstance().charToInt(RealMachine.getInstance().getTM(), 16);
							switch(sk) {
								case 1 :
									RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
									break;
								case 2 :
									RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
									RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
									break;
								default :
							}
						} else {

						}
						break;
			case "HA" : halt();
						break;
			case "LO" : loop(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "CX" : ccx(command.substring(2, 4));
						RealMachine.getInstance().setTM(RealMachine.getInstance().decReg(RealMachine.getInstance().getTM()));
						break;
			case "TN" : switch(command.substring(2, 4)) {
							case "ON": turnOn();	
									   break;
							case "OF": turnOff();
									   break;
						}
						break;
			case "CH" : checkStatus();
					    break;
			case "0X" : xor();
						break;
			// case "PR" : break;
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
		int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
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
		int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
		int block = Utilities.getInstance().charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ds), 16);
		int place = Integer.parseInt(elements, 16);
		int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
		int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
		if(RealMachine.getInstance().incESP()){
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			char[] valueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop);
			GraphicalUserInterface.getInstance().updateRAMCell(block * 256 + place, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));
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
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				int sum;
				//TODO check if not above 65535
				if(RealMachine.getInstance().getSF()){
					System.out.println("pirmas sumos su zenklu skaicius = " + Utilities.charToSignedInt(firstValueFromStack, 16));
					System.out.println("pirmas sumos su zenklu skaicius = " + Utilities.charToSignedInt(firstValueFromStack, 16));
					sum = Utilities.charToSignedInt(firstValueFromStack, 16) + Utilities.charToInt(secondValueFromStack, 16);
					System.out.println("suma, stack top su zenklu = " + sum);
				}
				else {
					sum = Utilities.charToInt(firstValueFromStack, 16) + Utilities.charToInt(secondValueFromStack, 16);
				}
				// RealMachine.getInstance().setFLAGS(new char[]{'0', '0'});
				if (sum > 65535) {
					RealMachine.getInstance().setPI(new char[] {'0', '6'});
				}
				RealMachine.getInstance().setFLAGS(new char[]{'0', '0', '0', '0'});
				char[] flags = RealMachine.getInstance().getFLAGS();
				if(sum < 65535 && sum > 0) {
					flags[1] = '1';
				}
				if (sum == 0) {
					flags[2] = '1';
				}
				if (sum < 0) {
					flags[3] = '1';
				}
				RealMachine.getInstance().setFLAGS(flags);		
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
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			// if(Utilities.charToInt(firstValueFromStack) < Utilities.charToInt(secondValueFromStack)) {
				// RealMachine.getInstance().setPI(new char[] {'0', '2'});
				// return;
			// }
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				int sub;
				//TODO check if not negative
				if(RealMachine.getInstance().getSF()){
					sub = Utilities.charToSignedInt(firstValueFromStack, 16) - Utilities.charToInt(secondValueFromStack, 16);
				}
				else {
					sub = Utilities.charToInt(firstValueFromStack, 16) - Utilities.charToInt(secondValueFromStack, 16);
				}
				RealMachine.getInstance().setFLAGS(new char[]{'0', '0', '0', '0'});
				char[] flags = RealMachine.getInstance().getFLAGS();
				if(sub > 0) {
					flags[1] = '1';
				}
				if (sub == 0) {
					flags[2] = '1';
				}
				if (sub < 0) {
					flags[3] = '1';
				}
				RealMachine.getInstance().setFLAGS(flags);
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
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if(RealMachine.getInstance().decESP()){
				int mul;
				if(RealMachine.getInstance().getSF()){
					mul = Utilities.charToSignedInt(firstValueFromStack, 16) * Utilities.charToInt(secondValueFromStack, 16);
				}
				else {
					mul = Utilities.charToInt(firstValueFromStack, 16) * Utilities.charToInt(secondValueFromStack, 16);
				}
				RealMachine.getInstance().setFLAGS(new char[]{'0', '0', '0', '0'});
				char[] flags = RealMachine.getInstance().getFLAGS();
				if(mul < 65535 && mul > 0) {
					flags[1] = '1';
				}
				if (mul == 0) {
					flags[2] = '1';
				}
				if (mul < 0) {
					flags[3] = '1';
				} 
				RealMachine.getInstance().setFLAGS(flags);	
				if(mul > 65535) {
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
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			if(Utilities.charToInt(firstValueFromStack, 16) == 0) {
				RealMachine.getInstance().setPI(new char[] {'0', '1'});
				return;
			}
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if (RealMachine.getInstance().decESP()) {
				int div, mod;
				if(RealMachine.getInstance().getSF()){
					div = Utilities.charToInt(secondValueFromStack, 16) / Utilities.charToSignedInt(firstValueFromStack, 16);
					mod = Utilities.charToInt(secondValueFromStack, 16) % Utilities.charToSignedInt(firstValueFromStack, 16);
				}
				else {
					div = Utilities.charToInt(secondValueFromStack, 16) / Utilities.charToInt(firstValueFromStack, 16);
					mod = Utilities.charToInt(secondValueFromStack, 16) % Utilities.charToInt(firstValueFromStack, 16);
				}
				RealMachine.getInstance().setFLAGS(new char[]{'0', '0', '0', '0'});
				char[] flags = RealMachine.getInstance().getFLAGS();
				if(div > 0) {
					flags[1] = '1';
				}
				if(div == 0) {
					flags[2] = '1';
				}
				if(div < 0) {
					flags[3] = '1';
				}
				RealMachine.getInstance().setFLAGS(flags);
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
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			char[] block = RealMachine.getInstance().getRAM().getWord(ptr, place);
			if(new String(block).equals("----")) {
				// =========   SWAPPING   ==========
				changeSegment(place);
			} // else there is an active block so data segment points to it
			RealMachine.getInstance().setPI(new char[] {'0', '8'});
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '3'});
		}
	}

	public void ccs(String elements) {
		int place = Integer.parseInt(elements, 16);
		if(place < 256) {
			RealMachine.getInstance().setCS(elements.toCharArray());
			RealMachine.getInstance().changedCS = true;
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			char[] block = RealMachine.getInstance().getRAM().getWord(ptr, place);
			if(new String(block).equals("----")) {
				changeSegment(place);
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
			RealMachine.getInstance().setSS(elements.toCharArray());
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			char[] block = RealMachine.getInstance().getRAM().getWord(ptr, place);
			if(new String(block).equals("----")) {
				changeSegment(place);
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
				// int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
				// int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
				// int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
				// int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
				// char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
				if(RealMachine.getInstance().getFLAGS()[3] == '1') {
					RealMachine.getInstance().setIP(elements.toCharArray());
				}
				else System.out.println("FLAG neg value not 1");
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
				// int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
				// int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
				// int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
				// int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
				// char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
				if(RealMachine.getInstance().getFLAGS()[1] == '1') {
					RealMachine.getInstance().setIP(elements.toCharArray());
				}
				else System.out.println("FLAG positive value not 1");
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
				// int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
				// int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
				// int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
				// int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
				// char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
				// System.out.println("value " + new String(firstValueFromStack) + "block " + stackBlock + "");
				if(RealMachine.getInstance().getFLAGS()[2] == '1') {
					RealMachine.getInstance().setIP(elements.toCharArray());
				}
				else System.out.println("FLAG zero value not 1");
			}
			else {
				RealMachine.getInstance().setPI(new char[] {'0', '1'});
			}
		}
	}
	
	public void gd(String elements) {
		PrintWriter writer = null;
		int place = Integer.parseInt(elements, 16);
		int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
		try {
			char[] realBlock = RealMachine.getInstance().getRAM().getWord(ptr, place);
			if(Arrays.equals(realBlock, new char[] {'-', '-', '-', '-'})) {
				RealMachine.getInstance().setPI(new char[] {'0', '3'});
			}
			else {
				String[] data = RealMachine.getInstance().getRAM().getBlock(Utilities.getInstance().charToInt(realBlock, 16));
        		// Open the file for writing.
        		writer = new PrintWriter("data.txt", "UTF-8");
        		for(int i=0; i < data.length; ++i) {
        			writer.println(data[i]);
				}
				writer.close();
			}
        }
        catch(IOException ex) {
            ex.printStackTrace();
   		}
	}

	public void pd(String elements) {
		int place = Integer.parseInt(elements, 16);
		int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			char[] realBlock = RealMachine.getInstance().getRAM().getWord(ptr, place);
			
			if(Arrays.equals(realBlock, new char[] {'-', '-', '-', '-'})) {
				RealMachine.getInstance().setPI(new char[] {'0', '3'});
			}
			else {
				String[] data = readData();
        		RealMachine.getInstance().getRAM().setBlock(Utilities.getInstance().charToInt(realBlock,16), data);
			}
	}

	public void halt() {
		char[] chars = {'0', '3'};
		RealMachine.getInstance().setSI(chars);
	}

	public void loop(String elements) {
		int place = Integer.parseInt(elements, 16);
		if(place < 256) {
			int cx = Utilities.getInstance().charToInt(RealMachine.getInstance().getCX(), 16);
			if(cx > 0) {
				RealMachine.getInstance().setIP(elements.toCharArray());
				RealMachine.getInstance().setCX(RealMachine.getInstance().decReg(RealMachine.getInstance().getCX()));
			}
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '3'});
		}
	}

	public void ccx(String elements) {
		int value = Integer.parseInt(elements, 16);
		if(value < 256) {
			RealMachine.getInstance().setCX(elements.toCharArray());
		}
		else {
			RealMachine.getInstance().setPI(new char[] {'0', '3'});
		}
	}

	public void turnOn() {
		Light.getInstance().turnOn();
	}

	public void turnOff() {
		Light.getInstance().turnOff();
	} 

	public void checkStatus() {
		try {
			if(OsUtils.isUnix()) {
				Process p = Runtime.getRuntime().exec("cat /sys/class/power_supply/BAT1/capacity");
				System.out.println(p);
				getBatteryStatus(p, Light.getInstance().isTurnedOn());
			}
			if(OsUtils.isMac()) {
				Process p = Runtime.getRuntime().exec("pmset -g batt | egrep \"([0-9]+\\%).*\" -o --colour=auto | cut -f1 -d';'");
				getBatteryStatus(p, Light.getInstance().isTurnedOn());
			}
		}
		catch(Exception ex) {
			System.out.println("Command execution for battery");
		}
	}

	public void xor() {
		int espValue = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
		if (espValue > 253) {
			System.out.println("Not enough elements!");
		} else {
			int ptr = Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16);
			int ss = Utilities.charToInt(RealMachine.getInstance().getSS(), 16);
			int stackBlock = Utilities.charToInt(RealMachine.getInstance().getRAM().getWord(ptr, ss), 16);
			int firstElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 1;
			char[] firstValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, firstElStackTop);
			int secondElStackTop = (Utilities.charToInt(RealMachine.getInstance().getESP(), 16)) + 2;
			char[] secondValueFromStack = RealMachine.getInstance().getRAM().getWord(stackBlock, secondElStackTop);
			int stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			if (RealMachine.getInstance().decESP()) {
				int xor = Utilities.charToInt(secondValueFromStack, 16)^Utilities.charToInt(firstValueFromStack, 16);
				RealMachine.getInstance().setFLAGS(new char[]{'0', '0', '0', '0'});
				char[] flags = RealMachine.getInstance().getFLAGS();
				if(xor > 0) {
					flags[1] = '1';
				}
				if (xor == 0) {
					flags[2] = '1';
				}
				if (xor < 0) {
					flags[3] = '1';
				}
				RealMachine.getInstance().setFLAGS(flags);
				char[] valueToAdd = (Integer.toHexString(xor)).toCharArray();
				RealMachine.getInstance().getRAM().setWord(stackBlock, stackTop, valueToAdd);
				GraphicalUserInterface.getInstance().updateRAMCell(stackBlock * 256 + stackTop, new String(RealMachine.getInstance().getRAM().getWord(stackBlock, stackTop)));	
				stackTop = Utilities.charToInt(RealMachine.getInstance().getESP(), 16);
			} else {
				RealMachine.getInstance().setPI(new char[] {'0', '4'});
			}
		}
	}

	private void getBatteryStatus(Process p, boolean lightStatus) {
		try {
			// p.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(
			p.getInputStream()));
			String line = "";
			String output = "";

			while ((line = buf.readLine()) != null) {
				output += line;
			}
			GraphicalUserInterface.getInstance().setOutputText("Battery: " + output + "%\n" + (lightStatus ? "Light is turned on" : "Light is turned off"));
			System.out.println(output);
		}
		catch(Exception e) {
			System.out.println("Battery read exception");
		}
		
	}
	public String[] readData() {
		String filePathString = new String( "data.txt" );
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


	public void changeSegment(int place) {
		String activeVMBlockValue = new String( RealMachine.getInstance().getRAM().getActiveVMblockForSwapping(RealMachine.getInstance().getHalfPTR(), RealMachine.getInstance().getDS(), RealMachine.getInstance().getSS(), RealMachine.getInstance().getCS() ) );
		int activeVMBlockDecValue = Utilities.getInstance().hexToDec(activeVMBlockValue);
        int pageTablePlaceForActiveBlock = RealMachine.getInstance().getRAM().getPageTablePlaceForActiveBlock(RealMachine.getInstance().getHalfPTR(), activeVMBlockValue);

        RealMachine.getInstance().getSwapping().swap(pageTablePlaceForActiveBlock, RealMachine.getInstance().getRAM().getBlock(activeVMBlockDecValue));
        // clear block
        RealMachine.getInstance().getRAM().nullBlock(activeVMBlockDecValue);
        // set block inactive
        RealMachine.getInstance().getRAM().setBlockInactive(Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16), pageTablePlaceForActiveBlock);

        GraphicalUserInterface.getInstance().updateRAMCell(Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16) + pageTablePlaceForActiveBlock, "----");
        RealMachine.getInstance().getRAM().setWord(Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16), place, activeVMBlockValue.toCharArray());
        GraphicalUserInterface.getInstance().updateRAMCell(Utilities.getInstance().charToInt(RealMachine.getInstance().getHalfPTR(), 16) + place, activeVMBlockValue);

        // check if file place.txt exists if so, then we need to load data from it to activeVMblockDecValue
        String[] blockFromSwap = RealMachine.getInstance().getSwapping().getBlockFromFile(place);
        if(blockFromSwap != null) {
        	RealMachine.getInstance().getRAM().setBlock(activeVMBlockDecValue, blockFromSwap);	
        }
	}
}