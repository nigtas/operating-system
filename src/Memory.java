import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Memory {

	// word size
	public static int WORD_SIZE = 4;
	// stack size
	public static int NUMBER_OF_STACK_BLOCK = 64;
	// number of words in a block
	public static int NUMBER_OF_WORDS = 256;
	// number of blocks
	public static int NUMBER_OF_BLOCKS = 20;
	//VM number of blocks
	public static int VM_NUMBER_OF_BLOCKS = 4;
	// ram memory
	public char memory[][] = new char[NUMBER_OF_BLOCKS * NUMBER_OF_WORDS][WORD_SIZE];
	// used blocks
	public boolean usedBlock[] = new boolean[NUMBER_OF_BLOCKS];
	public boolean usedWords[][] = new boolean[NUMBER_OF_BLOCKS][NUMBER_OF_WORDS];
	public boolean usedAndActiveVMblock[] = new boolean[NUMBER_OF_WORDS]; 
	
	public Memory() {
		for (int i = 0; i < memory.length; i++) {
		    for(int j = 0; j < WORD_SIZE; j++) {
			    memory[i][j] = '-';
			}   
		}
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			usedBlock[i] = false;
			for(int j = 0; j < NUMBER_OF_WORDS; j++) {
				usedWords[i][j] = false;	
			}
		} 

		for (int i = 0; i < NUMBER_OF_WORDS; i++) {
			usedAndActiveVMblock[i] = false;
		}
	}
	
	public char[][] getMemory() {
		return memory;
	}

	// sets word at index
	// place - 0..NUMBER_OF_WORDS - 1
	public void setWord(int block, int place, char[] data) {
		// System.out.println(block + "place " + place);
		int memoryPlace = 0;

		if(usedWords[block][place]) { 
			GraphicalUserInterface.getInstance().appendOutputText("#WORD IN: "+ block +" BLOCK\n");
			GraphicalUserInterface.getInstance().appendOutputText("#AND: "+ place +" PLACE OVERWRITTEN!\n");
		}
		if(block > 0 && place > 0) {
			memoryPlace = block * NUMBER_OF_WORDS + place;
		} else if(block == 0) {
			memoryPlace = place;
		} else if(place == 0) {
			memoryPlace = block * NUMBER_OF_WORDS;
		}
		
		if(data.length < WORD_SIZE) {
			for (int i = 0; i < (WORD_SIZE - data.length); i++) {
				memory[memoryPlace][i] = '0';
			}
			for (int i = (WORD_SIZE - data.length); i < WORD_SIZE; i++) {
				memory[memoryPlace][i] = data[i-(WORD_SIZE - data.length)];
			}
		} else {
			for (int i = 0; i < WORD_SIZE; i++) {
				memory[memoryPlace][i] = data[i];
			}
		}

		if(!usedWords[block][place]) {
			usedWords[block][place] = true;
		}
		if(!usedBlock[block]) {
			usedBlock[block] = true;
		}
	}

	// returns word from memory at index
	// place - 0..NUMBER_OF_WORDS - 1
	public char[] getWord(int block, int place) {
		// System.out.println(block + " " + place);
		char data[] = new char[4];
		int memoryPlace = 0;
		if(block > 0 && place > 0) {
			memoryPlace = block * NUMBER_OF_WORDS + place;
		} else if (block == 0) {
			memoryPlace = place;
		} else if (place == 0) {
			memoryPlace = block * NUMBER_OF_WORDS;
		}
		for (int i = 0; i < WORD_SIZE; i++) {
			data[i] = memory[memoryPlace][i];
		}
		return data;
	}

	// null the word at index
	// place - 0..NUMBER_OF_WORDS - 1
	public void nullWord(int block, int place) {
		int memoryPlace = 0;
		if(block > 0 && place > 0) {
			memoryPlace = block * NUMBER_OF_WORDS + place;
		} else if (block == 0) {
			memoryPlace = place;
		} else if (place == 0) {
			memoryPlace = block * NUMBER_OF_WORDS;
		}
		for (int i = 0; i < WORD_SIZE; i++) {
			memory[memoryPlace][i] = '0';
		}
		usedWords[block][place] = false;
	}

	public void nullBlock(int block) {
		int place = 0;
		if(block == 0) {
			place = 0;
		} else if(block > 0) {
			place = block * NUMBER_OF_WORDS;
		}
		for(int i = 0; i<NUMBER_OF_WORDS; i++) {
			for(int j = 0; j < WORD_SIZE; j++) {
				memory[place+i][j] = '0';
			}
			GraphicalUserInterface.getInstance().updateRAMCell( place+i, new String(memory[block*NUMBER_OF_WORDS + i]) );
		} 
	}

	public void setBlock(int block, String[] data) {
		for(int i = 0; i < NUMBER_OF_WORDS; i++) {
			memory[block*NUMBER_OF_WORDS + i] = data[i].toCharArray();
			GraphicalUserInterface.getInstance().updateRAMCell( block*NUMBER_OF_WORDS + i, new String(memory[block*NUMBER_OF_WORDS + i]) );
		} 
	}

	public String[] getBlock(int block) {
		int place = 0;
		String blockArray[] = new String[NUMBER_OF_WORDS];
		if(block > 0) {
			place = block * NUMBER_OF_WORDS;
		}
		for(int i = 0; i < NUMBER_OF_WORDS; i++) {
			blockArray[i] = new String( memory[place + i] );
		}
		return blockArray;
	}

	// returns index of unused block
	public int getFreeBlock() {
		Random rand = new Random();
		for (int i = 0; i < 100; i++) {
			int randomNum = rand.nextInt(NUMBER_OF_BLOCKS);

			if (!usedBlock[randomNum] && randomNum <= NUMBER_OF_BLOCKS) {
				return randomNum;
			}
		}
		return -1;
	}	

	// returns index of unused word in a block
	public int getFreeWord(int block) {
		for (int i = 0; i < NUMBER_OF_WORDS; i++) {
			if (!usedWords[block][i]) {
				return i;
			}
		}
		return -1;
	}	

	// creates new page table with virtual machine blocks real addresses
	// returns PTR
	public char[] newPageTable() {
		char PTR[] = new char[WORD_SIZE];

		int pageTableAddress = 0;
		int blockNumber = 0;
		int tempBlock = 0;
		int freeBlocks = 0;

		PTR[0] = PTR[1] = PTR[2] = PTR[3] = '0';

		// how much free block we have?
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (!usedBlock[i]) {
				freeBlocks++;
			}
		}


		int ptrBlock = getFreeBlock();
		System.out.println("FREE BLOCKS: " + ptrBlock);

		if(ptrBlock > -1){
			PTR[2] = Character.forDigit((ptrBlock / 16), 16);
			PTR[3] = Character.forDigit((ptrBlock % 16), 16);	
			pageTableAddress = getRealAddress(blockNumber);
			pageTableAddress = ((16 * Character.getNumericValue(PTR[2]) + Character.getNumericValue(PTR[3])) * NUMBER_OF_WORDS);
			usedBlock[ptrBlock] = true;
			freeBlocks--;
		}
		else {
			System.out.println("Cannot create new page table!!");
			GraphicalUserInterface.getInstance().appendOutputText("Cannot create new page table!!");
			return new char[] {'E', 'R', 'O', 'R'};
		}

		System.out.println("FREE BLOCKS coming: " + freeBlocks);
		// less than 256 blocks ~ SPECIFIC CASE NEEDS SWAPING
		if (freeBlocks >= VM_NUMBER_OF_BLOCKS) {
			// blockNumber = getFreeBlock();
			// if(blockNumber < 0) {
			// 	GraphicalUserInterface.getInstance().appendOutputText("#~PAGE TABLE NOT SET!\n#~NO FREE BLOCKS IN MEMORY!\n");
			// 	char[][] nullWord = new char[1][4];
			// 	nullWord[0][0] = nullWord[0][1] = nullWord[0][2] = nullWord[0][3] = '0';
			// 	return nullWord[0];
			// }
			// usedBlock[blockNumber] = true;
			// pageTableAddress = getRealAddress(blockNumber);
			// PTR[2] = Character.forDigit((blockNumber / 16), 16);
			// PTR[3] = Character.forDigit((blockNumber % 16), 16);
			// pageTableAddress = ((16 * Character.getNumericValue(PTR[2]) + Character.getNumericValue(PTR[3])) * NUMBER_OF_WORDS);
			// System.out.println("memory " + pageTableAddress);
			
			// write real address for every virtual block
			int i = 0;
			tempBlock = getFreeBlock();
			System.out.println("temp block: " + tempBlock);
			int pageTablePlace = ((16 * Character.getNumericValue(PTR[2]) + Character.getNumericValue(PTR[3])) * NUMBER_OF_WORDS);
			while (i < VM_NUMBER_OF_BLOCKS) {
				// while there are free blocks count real address for it
				if(tempBlock > 0) {
					memory[pageTableAddress] = Utilities.getInstance().decToHex(getRealAddress(tempBlock) / NUMBER_OF_WORDS).toCharArray();
					System.out.println("PAGE TABLE CREATING address: " + pageTableAddress + " value: " + new String(memory[pageTableAddress]));
					usedBlock[tempBlock] = true;
					tempBlock = getFreeBlock();
					System.out.println("page tbable place: " + pageTablePlace + " i " + i);
					usedWords[pageTablePlace/NUMBER_OF_WORDS][i] = true;
				}
				else { // no more blocks left, address is with negative sign
					memory[pageTableAddress] = ("----").toCharArray();
				}
				pageTableAddress++;
				i++;
			}
		} // more than 256 block ~ NO SWAPING NEEDED
		System.out.println("?PTR: " + new String(PTR));
		return PTR;
	}

	// return real address for free block
	public int getRealAddress(int freeBlock) {
		char[] address = new char[WORD_SIZE];
		address[0] = address[1] = address[2] = address[3] = '0';
		address[2] = Character.forDigit((freeBlock / 16), 16);
		address[3] = Character.forDigit((freeBlock % 16), 16);

		int realAddress = ((16 * Character.getNumericValue(address[2]) + Character.getNumericValue(address[3])) * NUMBER_OF_WORDS);
		return realAddress;		
	}

	public char[] getActiveVMblockForSwapping(char[] ptr, char[] ds, char[] ss, char[] cs) {
		int ptrAddress = Utilities.getInstance().hexToDec(new String(ptr));
		int i = 0;
		System.out.println("get active vm block: " + ptrAddress + " ds " + new String(ds) + " ss " + new String(ss) + " cs " + new String(cs));
		for(i = 0;  i < NUMBER_OF_WORDS; i++) {
			if(!new String(memory[i+ptrAddress*NUMBER_OF_WORDS]).equals("----"))
				System.out.println("hahahaha" + new String(memory[i+ptrAddress*NUMBER_OF_WORDS]) + " i " + i);
			if(new String(memory[i+ptrAddress*NUMBER_OF_WORDS]).equals("----") ) {
				continue;
			} else if( (i == Utilities.getInstance().charToInt(ds, 16) ) || (i == Utilities.getInstance().charToInt(ss, 16) ) || (i == Utilities.getInstance().charToInt(cs, 16) ) ) {
				continue;
			} else {
				break;
			}
		}
		System.out.println("VM BLOCK: " + i);
		if(i > 255) {
			return new char[]{'-', '-', '-', '-'};
		}
		else {
			return memory[i+ptrAddress*NUMBER_OF_WORDS];
		}
	}

	public int getPageTablePlaceForActiveBlock(char[] ptr, String active) {
		int ptrAddress = Utilities.getInstance().hexToDec(new String(ptr));
		for(int i = 0; i < NUMBER_OF_WORDS; i++) {
			if(active.equals(new String(memory[ptrAddress*NUMBER_OF_WORDS + i]))) {
				return i;
			}
		}
		return -1;
	}

	public void setBlockInactive(int block, int place) {
		int memoryPlace = 0;
		if(block > 0 && place > 0) {
			memoryPlace = block * place;
		} else if (block == 0) {
			memoryPlace = place;
		} else if (place == 0) {
			memoryPlace = block * NUMBER_OF_WORDS;
		}
		for (int i = 0; i < WORD_SIZE; i++) {
			memory[memoryPlace][i] = '-';
		}
		System.out.println("inactive: " + block + place);
		usedWords[block][place] = false;
	}

}