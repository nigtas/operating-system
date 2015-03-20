
public class SupervisorMemory {
	// þodþio dydis
	public static int WORD_SIZE = 4;
	// þodþiø skaièius supervizorinëje atmintyje
	public static int NUMBER_OF_WORDS = 512;
	// supervizorinë atmintis
	public char supervisorMemory[][] = new char[NUMBER_OF_WORDS][WORD_SIZE];
	// inicializuoja atmintá
	public SupervisorMemory() {
		for (int i = 0; i < NUMBER_OF_WORDS; i++) {
		    for(int j = 0; j < WORD_SIZE; j++) {
			    supervisorMemory[i][j] = '0';
			}   
		}
	}
	// iðspausdina atmintá
	public void printSupervisorMemory() {
		for (int i = 0; i < NUMBER_OF_WORDS; i++) {
			System.out.print(i);
			System.out.print(" ");
			System.out.println(supervisorMemory[i]);
		}
	}
	// nustato reikðmæ atmintyje
	public void setWord(int place, char[] data) {
		for (int i = 0; i < WORD_SIZE; i++) {
			supervisorMemory[place][i] = data[i];
		}
	}
	// paima reikðmæ ið atminties
	public char[] getWord(int place) {
		char data[] = new char[4];
		for (int i = 0; i < WORD_SIZE; i++) {
			data[i] = supervisorMemory[place][i];
		}
		return data;
	}
	// nunulina þodá nurodytu adresu
	public void nullWord(int place) {
		for (int i = 0; i < WORD_SIZE; i++) {
			supervisorMemory[place][i] = '0';
		}
	}
}
