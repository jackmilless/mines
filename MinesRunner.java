// Wrapper for Mines and MinesAI, asks user which version they would like to run
import java.util.Scanner;

public class MinesRunner {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter 0 for manual game or 1 for AI game:");	
		int gameType = scan.nextInt();
		if(gameType == 0) {
			Mines.playGame(scan);
		} else {
			MinesAI.playGameInput(scan);
		}
		scan.close();
	}
}
