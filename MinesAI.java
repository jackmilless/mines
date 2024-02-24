import java.util.Scanner;
import java.util.ArrayList;

// Runs AI game of mines
class MinesAI {
	private static boolean isStatistics;
	private static int numRows;
	private static int numCols;
	private static int numMines;
	private static Board board;
	private static boolean isFirstTurn;
	private static boolean isStalled;
	private static boolean isRunning;
	private static int win; 

	public static void playGameInput(Scanner scan) {
		System.out.println("Enter 0 for one game and final board state or 1 for multiple games and resulting statistics:");
		int runType = scan.nextInt();
		isStatistics = (runType == 0) ? false : true;
		int numGames = 1;
		if(isStatistics) {
			System.out.println("How many games would you like to run? Enter a positive integer:");
			numGames = scan.nextInt(); 
		}
		System.out.println("Enter number of rows:");
		numRows = scan.nextInt(); 
		System.out.println("Enter number of columns:");
		numCols = scan.nextInt();
		System.out.println("Enter number of mines:");
		numMines = scan.nextInt();
		int numWins = 0;
		for(int i = 0; i < numGames; i++) {
			numWins += playGame();
		}
		if(isStatistics) {
			System.out.println();
			System.out.println("Wins:    " + numWins);
			System.out.println("Losses:  " + (numGames - numWins));
			System.out.println("Win %:   " + ((double) (numWins) / (numGames)) * 100);
		}
	}
	
	// for statistics, returns win value: 0 if lost, 1 if won
	public static int playGame() {
		isFirstTurn = true;
		isRunning = true;
		win = 0;
		board = new Board(numRows, numCols, numMines); 
		board.makeBoard();
		isStalled = false;
		// repeatedly call logical helper functions unless nothing else can be determined about the board
		// if nothing can with the current board state, reveal at random 
		while(isRunning) {
			isStalled = true;
			simpleReveal();
			if(!isRunning) {
				break;
			}
			simpleFlag();
			if(!isRunning) {
				break;
			}
			complexFlagAndReveal();
			if(!isRunning) {
				break;
			}
			if(isStalled) { // logic currently can't determine anything new about the board
				randomReveal();
			}		
		}
		return win;
	}

	// check board for known mines and flag them
	// if a revealed Tile's number of unknown surroundings equals its number of 
	// currently unflagged surrounding mines, flag the rest of its surroundings
	public static void simpleFlag() {
		int numSurroundingUnknown = 0;
		int numSurroundingFlags = 0;
		for(int i = 0; i < board.getNumRows(); i++) {
			for(int j = 0; j < board.getNumCols(); j++) {
				Tile tile1 = board.getTiles()[i][j];
				if(tile1.getReveal() && tile1.getNumSurroundingMines() != 0) {
					// determine surrounding flag + unknown numbers of tile1
					for(int k = i - 1; k <= i + 1; k++) {
						for(int l = j - 1; l <= j + 1; l++) {
							if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols()) {
								Tile tile2 = board.getTiles()[k][l];
								if(!tile2.getReveal()) {
									if(!tile2.getFlag()) {
										numSurroundingUnknown++; 
									} else {
										numSurroundingFlags++;
									}
								}
							}
						}
					}
					if(numSurroundingUnknown == tile1.getNumSurroundingMines() - numSurroundingFlags 
							&& numSurroundingUnknown != 0) { // set surrounding flags
						for(int k = i - 1; k <= i + 1; k++) {
							for(int l = j - 1; l <= j + 1; l++) {
								if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols()) {
									Tile tile2 = board.getTiles()[k][l];
									if(!tile2.getReveal() && !tile2.getFlag()) {
										tile2.setFlag(true);
									}
								}
							}
						}
						isStalled = false;
					}
					numSurroundingUnknown = 0;
					numSurroundingFlags = 0;
				}
			}
		}
	}

	// check board for known safe spaces and reveal them
	// if a revealed Tile's number of surrounding flags equals its number of 
	// surrounding mines, reveal its remaining surroundings
	public static void simpleReveal() {
		int numSurroundingFlags = 0;
		for(int i = 0; i < board.getNumRows(); i++) {
			for(int j = 0; j < board.getNumCols(); j++) {
				Tile tile1 = board.getTiles()[i][j];
				if(tile1.getReveal() && tile1.getNumSurroundingMines() != 0) {
					// determine number of surrounding flags of tile1
					for(int k = i - 1; k <= i + 1; k++) {
						for(int l = j - 1; l <= j + 1; l++) {
							if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols()) {
								Tile tile2 = board.getTiles()[k][l];
								if(!tile2.getReveal() && tile2.getFlag()) {
									numSurroundingFlags++;
								}
							}
						}
					}
					if(numSurroundingFlags == tile1.getNumSurroundingMines()) { // reveal surrounding tiles
						for(int k = i - 1; k <= i + 1; k++) {
							for(int l = j - 1; l <= j + 1; l++) {
								if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols()) {
									Tile tile2 = board.getTiles()[k][l];
									if(!tile2.getReveal() && !tile2.getFlag()) {
										isStalled = false;
										if(tile2.getNumSurroundingMines() == -1 && !tile2.getFlag()) {		
											for(int m = 0; m < board.getNumRows(); m++) {
												for(int n = 0; n < board.getNumCols(); n++) {
													if(tile1.getNumSurroundingMines() == -1) {
														tile1.setReveal(true);
													}
												}
											}
											endOfGame(false); // lose
										} else {
											board.reveal(k, l);
											if(board.getNumUnrevealedSafeTiles() == 0) {
												endOfGame(true); // win
											}
										}
									}
								}
							}
						}
					}
					numSurroundingFlags = 0;
				}
			}
		}
	}

	private static ArrayList<TilePair> tilePairs; 
	private static ArrayList<Tile> flagTiles; 
	private static ArrayList<Tile> revealTiles; 
	private static ArrayList<Integer[]> flagIndices;
	private static ArrayList<Integer[]> revealIndices;

	// compare information about pairs of tiles with at least one matching
	// surrounding tile in order to determine additional known mines and safe spaces
	public static void complexFlagAndReveal() {
		tilePairs = new ArrayList<TilePair>(0);
		flagTiles = new ArrayList<Tile>(0);
		revealTiles = new ArrayList<Tile>(0);
		flagIndices = new ArrayList<Integer[]>(0);
		revealIndices = new ArrayList<Integer[]>(0);
		addBoundaryPairs(); // fill tilePairs
		for(int i = 0; i < tilePairs.size(); i++) { 
			if(isRunning) {
				TilePair tilePair = tilePairs.get(i);
				ArrayList<Tile> unsharedTiles1 = new ArrayList<Tile>(0);
				ArrayList<Integer[]> unsharedIndices1 = new ArrayList<Integer[]>(0);
				ArrayList<Tile> unsharedTiles2 = new ArrayList<Tile>(0);
				ArrayList<Integer[]> unsharedIndices2 = new ArrayList<Integer[]>(0);
		    	ArrayList<Tile> sharedTiles = new ArrayList<Tile>(0);
				ArrayList<Integer[]> sharedIndices = new ArrayList<Integer[]>(0);
				int numRemainingSharedBoundary = 0;
				int numRemainingBoundary1 = 0;    
				int numRemainingBoundary2 = 0;
				int numUnsharedBoundary1 = 0;
				int numUnsharedBoundary2 = 0;
				int numRemainingMinesAround1 = 0;
				int numRemainingMinesAround2 = 0;
				boolean firstMNLoop = true;

				// collect shared boundary, tile numbers
				for(int k = tilePair.getRows()[0] - 1; k <= tilePair.getRows()[0] + 1; k++) {
					for(int l = tilePair.getCols()[0] - 1; l <= tilePair.getCols()[0] + 1; l++) {
						if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols() 
								&& (k != tilePair.getRows()[0] || l != tilePair.getCols()[0])) {
							Tile tile1 = board.getTiles()[k][l];
							if(!tile1.getReveal() && !tile1.getFlag()) {
								numRemainingBoundary1++;
							}
						}
						for(int m = tilePair.getRows()[1] - 1; m <= tilePair.getRows()[1] + 1; m++) {
							for(int n = tilePair.getCols()[1] - 1; n <= tilePair.getCols()[1] + 1; n++) {
								if(m >= 0 && n >= 0 && m < board.getNumRows() && n < board.getNumCols() 
										&& (m != tilePair.getRows()[1] || n != tilePair.getCols()[1])) {
									Tile tile2 = board.getTiles()[m][n];
									if(!tile2.getReveal() && !tile2.getFlag()) {
										if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols() 
												&& k == m && l == n
												&& (k != tilePair.getRows()[0] || l != tilePair.getCols()[0])) {
											Tile tile1 = board.getTiles()[k][l];
											if(!tile1.getReveal() && !tile1.getFlag()) {
												numRemainingSharedBoundary++;
												sharedTiles.add(board.getTiles()[k][n]);
												Integer[] indices = {m, l};
												sharedIndices.add(indices);
											}
										}
										if(firstMNLoop) {
											numRemainingBoundary2++;
										}
									}
								}
							}
						}
						firstMNLoop = false;
					}
				}

				// collect unshared tile numbers
				for(int k = tilePair.getRows()[0] - 1; k <= tilePair.getRows()[0] + 1; k++) {
					for(int l = tilePair.getCols()[0] - 1; l <= tilePair.getCols()[0] + 1; l++) {
						Integer[] indices = {k, l};
						if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols() 
								&& (k != tilePair.getRows()[0] || l != tilePair.getCols()[0]) 
								&& !isInIndices(sharedIndices, indices)) {
							Tile tile = board.getTiles()[k][l];
							if(!tile.getReveal() && !tile.getFlag()) {
								unsharedTiles1.add(tile);
								unsharedIndices1.add(indices);
							}
						}
					}
				}
				for(int k = tilePair.getRows()[1] - 1; k <= tilePair.getRows()[1] + 1; k++) {
					for(int l = tilePair.getCols()[1] - 1; l <= tilePair.getCols()[1] + 1; l++) {
						Integer[] indices = {k, l};
						if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols() 
								&& (k != tilePair.getRows()[1] || l != tilePair.getCols()[1]) 
								&& !isInIndices(sharedIndices, indices)) {
							Tile tile = board.getTiles()[k][l];
							if(!tile.getReveal() && !tile.getFlag()) {
								unsharedTiles2.add(tile);
								unsharedIndices2.add(indices);
							}
						}
					}
				}
				numUnsharedBoundary1 = numRemainingBoundary1 - numRemainingSharedBoundary;
				numUnsharedBoundary2 = numRemainingBoundary2 - numRemainingSharedBoundary;
				numRemainingMinesAround1 = tilePair.getTilePair()[0].getNumSurroundingMines();
				numRemainingMinesAround2 = tilePair.getTilePair()[1].getNumSurroundingMines();

				// adjust known number of surrounding mines for both tiles in pair
				for(int k = tilePair.getRows()[0] - 1; k <= tilePair.getRows()[0] + 1; k++) {
					for(int l = tilePair.getCols()[0] - 1; l <= tilePair.getCols()[0] + 1; l++) {
						if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols() 
								&& (k != tilePair.getRows()[0] || l != tilePair.getCols()[0])) {
							Tile tile = board.getTiles()[k][l];
							if(tile.getFlag()) {
								numRemainingMinesAround1--;
							}
						}
					}
				}
				for(int m = tilePair.getRows()[1] - 1; m <= tilePair.getRows()[1] + 1; m++) {
					for(int n = tilePair.getCols()[1] - 1; n <= tilePair.getCols()[1] + 1; n++) {
						if(m >= 0 && n >= 0 && m < board.getNumRows() && n < board.getNumCols() 
								&& (m != tilePair.getRows()[1] || n != tilePair.getCols()[1])) {
							Tile tile = board.getTiles()[m][n];
							if(tile.getFlag()) {
								numRemainingMinesAround2--;
							}
						}
					}
				}

				// compare numbers to determine min + max shared mines 
				int minSharedMines1 = numRemainingMinesAround1 - numUnsharedBoundary1;
				int minSharedMines2 = numRemainingMinesAround2 - numUnsharedBoundary2;
				int minSharedMines = max(minSharedMines1, minSharedMines2);
				int minRemainingMines = min(numRemainingMinesAround1, numRemainingMinesAround2);
				int maxSharedMines = minRemainingMines;
				ArrayList<Tile> minMinesTiles;
				ArrayList<Integer[]> minMinesIndices;
				if(minRemainingMines == numRemainingMinesAround1) {
					minMinesTiles = unsharedTiles1;
					minMinesIndices = unsharedIndices1;
				} else {
					minMinesTiles = unsharedTiles2;
					minMinesIndices = unsharedIndices2;
				}

				// if shared number of mines is known, can potentially reveal or flag tiles
				// save known safe spots and mines to reveal and flag after all tile pairs are analyzed
				if(minSharedMines == maxSharedMines) {
					setReveal(minMinesTiles, minMinesIndices); 
					int numSharedMines = minSharedMines;
					if(numRemainingMinesAround1 == numSharedMines) {
						setReveal(unsharedTiles1, unsharedIndices1);
					}
					if(numRemainingMinesAround2 == numSharedMines) {
						setReveal(unsharedTiles2, unsharedIndices2);
					}
					int numUnsharedMines1 = numRemainingMinesAround1 - numSharedMines;
					int numUnsharedMines2 = numRemainingMinesAround2 - numSharedMines;
					if(numUnsharedMines1 == numUnsharedBoundary1) {
						setFlag(unsharedTiles1, unsharedIndices1);
					}
					if(numUnsharedMines2 == numUnsharedBoundary2) {
						setFlag(unsharedTiles2, unsharedIndices2);
					}
				}
			}
		}

		// flag and reveal all set tiles
		for(int i = 0; i < flagTiles.size(); i++) {
			isStalled = false;
			flagTiles.get(i).setFlag(true);
		}
		for(int i = 0; i < revealTiles.size(); i++) {	
			isStalled = false;
			Tile tile = revealTiles.get(i);
			if(tile.getNumSurroundingMines() == -1 && !tile.getFlag()) {		
				for(int m = 0; m < board.getNumRows(); m++) {
					for(int n = 0; n < board.getNumCols(); n++) {
						if(tile.getNumSurroundingMines() == -1) {
							tile.setReveal(true);
						}
					}
				}
				endOfGame(false); // lose
				break;
			} else {
				Integer[] indices = revealIndices.get(i);
				board.reveal(indices[0], indices[1]);
				if(board.getNumUnrevealedSafeTiles() == 0) {
					endOfGame(true); // win
					break;
				}
			}
		}
	}

	// randomly reveal a tile
	public static void randomReveal() {
		int rand1 = (int)(Math.random() * board.getNumRows());
		int rand2 = (int)(Math.random() * board.getNumCols());
		Tile tile1 = board.getTiles()[rand1][rand2];
		while(tile1.getReveal() || tile1.getFlag()) {
			rand1 = (int)(Math.random() * board.getNumRows());
			rand2 = (int)(Math.random() * board.getNumCols());
			tile1 = board.getTiles()[rand1][rand2];
		}
		if(tile1.getNumSurroundingMines() == -1) { // mine found
			if(!isFirstTurn) { // can't lose on first turn
				for(int i = 0; i < board.getNumRows(); i++) { // reveal all mines
					for(int j = 0; j < board.getNumCols(); j++) {
						Tile tile2 = board.getTiles()[i][j];
						if(tile2.getNumSurroundingMines() == -1) {
							tile2.setReveal(true);
						}
					}
				}
				endOfGame(false); // lose
			} else { // mine found on first turn; generate new board without a mine at this location
				while(board.getTiles()[rand1][rand2].getNumSurroundingMines() == -1) {
					board.makeBoard();
				}
				board.reveal(rand1, rand2); // reveal randomly chosen tile
				isFirstTurn = false;
				isStalled = false;
				if(board.getNumUnrevealedSafeTiles() == 0) {
					endOfGame(true); // win
				}
			}
		} else {
			board.reveal(rand1, rand2); // reveal randomly chosen tile
			isFirstTurn = false;
			isStalled = false;
			if(board.getNumUnrevealedSafeTiles() == 0) {
				endOfGame(true); // win
			}
		}
	}

	// helper function for complexFlagAndReveal: find and save all pairs of tiles that share at least one surrounding tile
	public static void addBoundaryPairs() {
		for(int i = 0; i < board.getNumRows(); i++) {
			for(int j = 0; j < board.getNumCols(); j++) {
				Tile tile1 = board.getTiles()[i][j];
				if(tile1.getReveal() && tile1.getNumSurroundingMines() != 0) {
					for(int k = i - 2; k <= i + 2; k++) {
						for(int l = j - 2; l <= j + 2; l++) {
							if(k >= 0 && l >= 0 && k < board.getNumRows() && l < board.getNumCols() 
									&& (k != i || l != j)) {
								Tile tile2 = board.getTiles()[k][l];
								if(tile2.getReveal()) {
									// create consistently ordered TilePair, check pair doesn't exist before adding to tilePairs
									TilePair tilePair = orderTilePair(tile1, tile2, new int[] {i, k}, new int[] {j, l});
									if(!isInPairList(tilePair.getTilePair())) {
										tilePairs.add(tilePair);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// helper function for addBoundaryPairs: order tile pair in a consistent manner
	public static TilePair orderTilePair(Tile firstTile, Tile secondTile, int[] rows, int[] cols) {
		Tile[] orderedTilePair = new Tile[2];
		TilePair tilePair;
		if(rows[0] + cols[0] < rows[1] + cols[1] || (rows[0] + cols[0] == rows[1] + cols[1] && rows[0] > rows[1])) {
			orderedTilePair[0] = firstTile;
			orderedTilePair[1] = secondTile;
			tilePair = new TilePair(orderedTilePair, rows, cols);
		} else {
			orderedTilePair[0] = secondTile;
			orderedTilePair[1] = firstTile;
			int tmp = rows[0];
			rows[0] = rows[1];
			rows[1] = tmp;
			tmp = cols[0];
			cols[0] = cols[1];
			cols[1] = tmp;
			tilePair = new TilePair(orderedTilePair, rows, cols);
		}
		return tilePair;
	}

	// helper function for addBoundaryList: check if tilePair is in tilePairs
	public static boolean isInPairList(Tile[] tilePair) {
		for(int i = 0; i < tilePairs.size(); i++) {
			if(isEqual(tilePair, tilePairs.get(i).getTilePair())) {
				return true;
			}
		}
		return false;
	}

	// helper function for complexFlagAndReveal: check if indices are in sharedIndices
	public static boolean isInIndices(ArrayList<Integer[]> sharedIndices, Integer[] indices) {
		for(int i = 0; i < sharedIndices.size(); i++) {
			if(indices[0].equals(sharedIndices.get(i)[0]) && indices[1].equals(sharedIndices.get(i)[1])) {
				return true;
			} 
		}
		return false;
	}

	public static boolean isEqual(Tile[] tilePair1, Tile[] tilePair2) {
		return tilePair1[0] == tilePair2[0] && tilePair1[1] == tilePair2[1];
	}

	public static int max(int numOne, int numTwo) {
		if(numOne > numTwo) {
			return numOne;
		} else {
			return numTwo;
		}
	}

	public static int min(int inNumOne, int inNumTwo) {
		if(inNumOne < inNumTwo) {
			return inNumOne;
		} else {
			return inNumTwo;
		}
	}

	// helper function for complexFlagAndReveal: save tiles yet to be flagged while testing boundary pairs
	public static void setFlag(ArrayList<Tile> tiles, ArrayList<Integer[]> indices) {
		for(int i = 0; i < tiles.size(); i++) {
			if(!isInTileList(flagTiles, tiles.get(i))) {
				flagTiles.add(tiles.get(i));	
				flagIndices.add(indices.get(i));
			}
		}
	}

	// helper function for complexFlagAndReveal: save tiles yet to be revealed while testing boundary pairs
	public static void setReveal(ArrayList<Tile> tiles, ArrayList<Integer[]> indices) {	
		for(int i = 0; i < tiles.size(); i++) {
			if(!isInTileList(revealTiles, tiles.get(i))) {
				revealTiles.add(tiles.get(i));
				revealIndices.add(indices.get(i));
			}
		}
	}

	// helper function for setFlag and setReveal: check if inTile is in inTiles
	public static boolean isInTileList(ArrayList<Tile> tiles, Tile tile) {
		for(int i = 0; i < tiles.size(); i++) {
			if(tiles.get(i) == tile) {
				return true;
			}
		}
		return false;
	}
	
	// print end game board and message if not isStatistics, track win
	public static void endOfGame(boolean isWin) {
		String endGameMessage;
		if(isWin) {
			endGameMessage = "Win";
			win = 1;
		} else {
			endGameMessage = "Loss";
		}
		if(!isStatistics) {
			board.showBoard();
			System.out.println(endGameMessage);
		}
		isRunning = false;
	}
}

// organizes two tiles together with row and col locations in grid
// aids use of arrayList for storing pairs of tiles in complexFlagAndReveal
class TilePair {
	private Tile[] tilePair;
	private int[] rows;
	private int[] cols;

	public TilePair(Tile[] tilePair, int[] rows, int[] cols) {
		this.tilePair = tilePair;
		this.rows = rows;
		this.cols = cols;
	}

	public Tile[] getTilePair() {
		return this.tilePair;
	}

	public int[] getRows() {
		return this.rows;
	}

	public int[] getCols() {
		return this.cols;
	}
}
