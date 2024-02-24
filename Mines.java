import java.util.Scanner;

// Runs manual game of mines
class Mines {
	private static Board board;
	private static boolean isRunning;

	public static void playGame(Scanner scan) {
		int difficulty;     	 
		int guessRow;               
		int guessCol;
		int revealOrFlag = -1;
		boolean isFirstTurn = true; // ensures first reveal never causes a bomb to be revealed
		isRunning = true;
		
		// game menu loop
		while(true) {
			difficulty = -1; 
			// request difficulty
			System.out.println("Enter 0 for Easy, 1 for Medium, or 2 for Hard:");
			while(difficulty > 2 || difficulty < 0) {
				difficulty = scan.nextInt();
				if(difficulty == 0) { 
					board = new Board(8, 8, 8);
				} else if(difficulty == 1) {
					board = new Board(12, 12, 20);
				} else if(difficulty == 2) {
					board = new Board(14, 18, 42);
				} else {
					System.out.println("That is not a valid number. Try again.");
				}
			}
			board.makeBoard();
			board.showBoard();

			// runs game until player wins (all safe tiles are revealed) or loses (a bomb is revealed)
			while(isRunning) {
				// request move type (reveal or flag) and board location
				System.out.println("Enter 0 for reveal, or 1 for flag:");
				revealOrFlag = scan.nextInt();
				while(revealOrFlag != 0 && revealOrFlag != 1) {
					System.out.println("That is not a valid number. Try again.");
					revealOrFlag = scan.nextInt();
				}
				System.out.println("Enter a row, then a column:");
				guessRow = scan.nextInt();
				while(guessRow >= board.getNumRows() || guessRow < 0) {
					System.out.println("That is an invalid number, try again.");
					guessRow = scan.nextInt();
				}
				guessCol = scan.nextInt();
				while(guessCol >= board.getNumCols() || guessCol < 0) {
					System.out.println("That is an invalid number, try again.");
					guessCol = scan.nextInt();
				}

				// carry out user request (flag or reveal)
				Tile tile1 = board.getTiles()[guessRow][guessCol];
				if(revealOrFlag == 0) { // reveal
					if(tile1.getNumSurroundingMines() == -1 && !tile1.getFlag()) { // mine found
						if(!isFirstTurn) { // can't lose on first turn
							for(int i = 0; i < board.getNumRows(); i++) { // reveal all mines
								for(int j = 0; j < board.getNumCols(); j++) {
									Tile tile2 = board.getTiles()[i][j];
									if(tile2.getNumSurroundingMines() == -1) {
										tile2.setReveal(true);
									}
								}
							}
							board.showBoard();
							endOfGame(false); // lose
						} else { // mine found on first turn; generate new board without a mine at this location
							while(board.getTiles()[guessRow][guessCol].getNumSurroundingMines() == -1) {
								board.makeBoard();
							}
							board.reveal(guessRow, guessCol); // reveal chosen tile
							board.showBoard();
							isFirstTurn = false; 
							if(board.getNumUnrevealedSafeTiles() == 0) { // (unlikely) immediate win
								endOfGame(true);
							}
						}
					} else if(tile1.getFlag()) { // can't reveal flagged tile
						System.out.println("There is a flag here, try again.");
					} else if(!tile1.getReveal()) { // standard reveal (user chose covered tile)
						board.reveal(guessRow, guessCol);
						board.showBoard();
						isFirstTurn = false; 
						if(board.getNumUnrevealedSafeTiles() == 0) { // win
							endOfGame(true);
						}
					} else { // user chose already revealed tile: reveal all unflagged tiles surrounding it
						boolean lose = false;
						// reveal surrounding tiles
						for(int m = guessRow - 1; m <= guessRow + 1; m++) {
							for(int n = guessCol - 1; n <= guessCol + 1; n++) {
								if(m >= 0 && n >= 0 && m < board.getNumRows() && n < board.getNumCols()) {
									Tile tile2 = board.getTiles()[m][n];
									if(!tile2.getFlag()) {
										board.reveal(m, n);
										if(tile2.getNumSurroundingMines() == -1) { // mine found
											lose = true;
										}
									}
								}
							}
						}
						board.showBoard();
						isFirstTurn =false;
						if(lose) { // lose
							endOfGame(false);
						}
						if(board.getNumUnrevealedSafeTiles() == 0) { // win
							endOfGame(true);
						}
					}
				} else { // flag
					if(board.getNumFlags() == 0) { // can't flag if all flags are used 
						System.out.println("You have no flags left.");
					} else if(tile1.getReveal()) { // can't flag a tile that's already revealed 
						System.out.println("You tried to flag a revealed tile. Try again."); 
					} else if(!tile1.getFlag()) { // currently unflagged: flag
						tile1.setFlag(true);
						board.setNumFlags(board.getNumFlags() - 1);
						board.showBoard();
					} else { // currently flagged: unflag 
						tile1.setFlag(false);
						board.setNumFlags(board.getNumFlags() + 1);
						board.showBoard();
					}
				}
			}
			System.out.println("Would you like to play again? Enter 0 for no or 1 for yes:");
			boolean playAgain = scan.nextInt() == 1; 
			if(!playAgain) {
				break;
			} else {
				isRunning = true;
				isFirstTurn = true;
			}
		}
	}

	// print end game message
	public static void endOfGame(boolean isWin) {
		if(isWin) {
			System.out.println("You win");
		} else {
			System.out.println("You lose");
		}
		isRunning = false;
	}
}