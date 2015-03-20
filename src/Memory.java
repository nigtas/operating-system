import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Memory {
	// þodþio dydis
	public static int WORD_SIZE = 4;
	// þodþiø skaièius atmintyje
	public static int NUMBER_OF_WORDS = 256;
	// blokø skaièius atmintyje
	public static int NUMBER_OF_BLOCKS = 256;
	// vartotojo atmintis
	public char memory[][] = new char[NUMBER_OF_WORDS][WORD_SIZE];
	// uþimti blokai
	public boolean usedBlock[] = new boolean[NUMBER_OF_BLOCKS];
	// inicializuoja atmintá
	public Memory() {
		for (int i = 0; i < NUMBER_OF_WORDS; i++) {
		    for(int j = 0; j < WORD_SIZE; j++) {
			    memory[i][j] = '0';
			}   
		}
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			usedBlock[i] = false;
		}
	}
	// iðspausdina atmintá
	public void printMemory() {
		for (int i = 0; i < NUMBER_OF_WORDS; i++) {
			System.out.print(i);
			System.out.print(" ");
			System.out.println(memory[i]);
		}
	}
	// nustato reikðmæ atmintyje
	public void setWord(int place, char[] data) {
		for (int i = 0; i < WORD_SIZE; i++) {
			memory[place][i] = data[i];
		}
	}
	// paima reikðmæ ið atminties
	public char[] getWord(int place) {
		char data[] = new char[4];
		for (int i = 0; i < WORD_SIZE; i++) {
			data[i] = memory[place][i];
		}
		return data;
	}
	// nunulina þodá nurodytu adresu
	public void nullWord(int place) {
		for (int i = 0; i < WORD_SIZE; i++) {
			memory[place][i] = '0';
		}
	}
	// gauna laisvo bloko indeksà
	public int getFreeBlock() {
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (!usedBlock[i]) {
				return i;
			}
		}
		return NUMBER_OF_BLOCKS;
	}
	// gràþina realø adresà
	public int getRealAddress(char[] PTR, char x, char y) {
		char tempData[] = new char[WORD_SIZE];
		int tempAddress;
		tempAddress = 256*(256*Character.getNumericValue(PTR[2]) + Character.getNumericValue(PTR[3])) + Character.getNumericValue(x);
		tempData = getWord(tempAddress);
		return 256*(256*Character.getNumericValue(tempData[2]) + Character.getNumericValue(tempData[3])) + Character.getNumericValue(y);	
	}
	// sukuria naujà puslapiø lentelæ
	public char[] newPageTable() {
		Random generator = new Random();
		char PTR[] = new char[WORD_SIZE];
		int pageTableAddress;
		int blockNumber;
		int tempBlock;
		int freeBlocks = 0;
		int blocks = 0;
		PTR[0] = PTR[1] = PTR[2] = PTR[3] = '0';
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (!usedBlock[i]) {
				freeBlocks++;
			}
		}
		if (freeBlocks < 17) {
			Machine.showMessage("Not enough memory for virtual machine!");
		}
		else {
			blockNumber = getFreeBlock();
			usedBlock[blockNumber] = true;
			PTR[2] = Character.toUpperCase(Character.forDigit((blockNumber / 256) % 256, 256));
			PTR[3] = Character.toUpperCase(Character.forDigit((blockNumber % 256) % 256, 256));
			pageTableAddress = 256*(256*Character.getNumericValue(PTR[2]) + Character.getNumericValue(PTR[3]));
			while (blocks != 256) {
				tempBlock = generator.nextInt(64);
				if (!usedBlock[tempBlock]) {
					blocks++;
					usedBlock[tempBlock] = true;
					memory[pageTableAddress][2] = Character.toUpperCase(Character.forDigit((tempBlock / 256) % 256, 256));
					memory[pageTableAddress][3] = Character.toUpperCase(Character.forDigit((tempBlock % 256) % 256, 256));
					pageTableAddress++;
				}
			}		
		}
		return PTR;
	}
	// uþkrauna programà á atmintá
	public void loadProgram(char[] PTR) throws IOException {
		String programFile = "program.txt";
		char buffer[] = new char[4];
		String line;
		char dataBlock = '0';
		char codeBlock = '0';
		char codeWord = '0';
		char dataWord = '0';
		boolean toData = false;
		boolean nextToData = false;
		boolean toCode = false;
		boolean nextToCode = false;
		boolean interrupt = false;
		boolean prog = false;
		boolean name = false;
		boolean datx = false; 
		boolean data = false;
		boolean code = false;
		String programName = null;

		try {	
			BufferedReader br = new BufferedReader(new FileReader(programFile));
			while (!interrupt) {
				if ((line = br.readLine()).equals("&END")) {
					interrupt = true;
					break;
				}
				buffer = line.toCharArray();
				if (nextToData) {
					nextToData = false;
					toData = true;
				}
				if (nextToCode) {
					nextToCode = false;
					toCode = true;
				}
				if (line.equals("PROG") && !prog) {
					prog = true;
				}
				if (buffer[0] == 'N' && buffer[1] == 'A' && buffer[2] == 'M' && buffer[3] == 'E' && !name) {
					if (prog) {
						programName = line.substring(5);
						name = true;
					}
					else {
						interrupt = true;
						Machine.showMessage("Wrong program file format!");
						break;
					}
				}
				if (buffer[0] == 'D' && buffer[1] == 'A' && buffer[2] == 'T' && Character.getNumericValue(buffer[3]) > 0 && Character.getNumericValue(buffer[3]) < 12 && !datx) {
					if (name) {
						datx = true;
						dataBlock = Character.forDigit((12 - Character.getNumericValue(buffer[3])) % 256, 256);
					}
					else {
						interrupt = true;
						Machine.showMessage("Wrong program file format!");
						break;
					}
				}
				if (line.equals("DATA") && !data) {
					if (datx) {
						nextToData = true;
						data = true;
					}
					else {
						interrupt = true;
						Machine.showMessage("Wrong program file format!");
						break;
					}
				}
				if (line.equals("CODE") && prog && !code) {
					if (data == datx && name ) {
						toData = false;
						nextToCode = true;
						code = true;
					}
					else {
						interrupt = true;
						Machine.showMessage("Wrong program file format!");
						break;
					}
				}
				if (toData) {
					setWord(getRealAddress(PTR, dataBlock, dataWord), buffer);
					if ((Character.getNumericValue(dataWord) + 1) % 256 == 0) {
						dataBlock = Character.forDigit((Character.getNumericValue(dataBlock) + 1) % 256, 256);
						dataWord = '0';
					}
					else {
						dataWord = Character.forDigit((Character.getNumericValue(dataWord) + 1) % 256, 256);
					}			
				}
				if (toCode) {
					setWord(getRealAddress(PTR, codeBlock, codeWord), buffer);
					if ((Character.getNumericValue(codeWord) + 1) % 256 == 0) {
						codeBlock = Character.forDigit((Character.getNumericValue(codeBlock) + 1) % 256, 256);
						codeWord = '0';
					}
					else {
						codeWord = Character.forDigit((Character.getNumericValue(codeWord) + 1) % 256, 256);
					}	
				}
			
			}
			Machine.showMessage("The program "+programName+" has been successfully uploaded to memory!");
			br.close();
		} catch (FileNotFoundException e) {
			Machine.showMessage("Program file is not found!");
		}
	}
}
