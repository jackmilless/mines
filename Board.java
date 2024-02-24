// 2D array of Tile objects; gameboard 
class Board {
	private int numRows;
	private int numCols;
	private int numMines;
	private int numFlags;
	private int numUnrevealedSafeTiles;
	private Tile[][] tiles;

	/* 
	 * Board constructor 
	 * numRows: number of rows in board
	 * numCols: number of cols in board
	 * numMines: number of mines on board
	 */
	public Board(int numRows, int numCols, int numMines) {
		this.tiles = new Tile[numRows][numCols];
		this.numRows = numRows;
		this.numCols = numCols;
		this.numMines = numMines;
		this.numFlags = numMines;
		this.numUnrevealedSafeTiles = numRows * numCols - numMines;
	}

	// initialize Board/Tiles with mines + surrounding mine information
	public void makeBoard() {	
		int copyNumMines = numMines;
		// Initialize each Tile to 0 surrounding mines, unflagged, unrevealed
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				tiles[i][j] = new Tile(0, false, false);
			}
		}
		int randRow;
		int randCol;
		// randomize the locations of the mines on the board
		// Tiles with mines are represented by numSurroundingMines = -1
		while(numMines > 0) {
			randRow = (int)(Math.random()*numRows);
			randCol = (int)(Math.random()*numCols);
			if(tiles[randRow][randCol].getNumSurroundingMines() != -1) {
				tiles[randRow][randCol].setNumSurroundingMines(-1);
				numMines--;
			}
		}
		// Initialize numSurroundingMines for each non-mine Tile
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				Tile tile1 = tiles[i][j];
				for(int k = i-1; k <= i+1; k++) {
					for(int m = j-1; m <= j+1; m++) {
						if(0 <= k && k < numRows && 0 <= m && m < numCols) {
							Tile tile2 = tiles[k][m];
							if(tile2.getNumSurroundingMines() == -1 && tile1.getNumSurroundingMines() != -1) {
								tile1.setNumSurroundingMines(tile1.getNumSurroundingMines() + 1);
							}
						}
					}
				}
			}
		}
		numMines = copyNumMines;
	}

	// print out the board in its current state. # represents a covered tile, + a flag and X a revealed mine
	// otherwise the int numSurroundingMines is printed for a revealed tile
	public void showBoard() {
		System.out.print("     ");
		for(int j = 0; j < numCols; j++) {
			if(j < 10) {
				System.out.print(" " + j + " ");
			} else {
				System.out.print(j + " ");
			}
		}
		System.out.println();
		System.out.print("     ");
		for(int j = 0; j < numCols; j++) {
			System.out.print("___");
		}
		System.out.println();
		for(int i = 0; i < numRows; i++) {
			for (int j = -2; j < numCols; j++) {
				if(j == -2) {
					if(i < 10) {
						System.out.print(" " + i + " ");
					} else {
						System.out.print(" " + i);
					}
				} else if(j == -1) {
					System.out.print(" |");
				} else {
					Tile tile = tiles[i][j];
					if(tile.getFlag()) {
						System.out.print(" + ");
					} else if(!tile.getReveal()) {
						System.out.print(" # ");
					} else if(tile.getNumSurroundingMines() == -1) {
						System.out.print(" X ");
					} else {
						System.out.print(" " + tile.getNumSurroundingMines() + " ");
					}
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	// reveal current tile. If numSurroundingMines of current tile == 0,
	// recursively call on all unflagged tiles surrounding this tile
	public void reveal(int row, int col) {
		Tile tile = tiles[row][col];
		if(!tile.getReveal() && !tile.getFlag()) { 
			tile.setReveal(true);
			numUnrevealedSafeTiles--;
			if(tile.getNumSurroundingMines() == 0) { // recursive case
				for(int i = row - 1; i <= row + 1; i++) {
					for (int j = col - 1; j <= col + 1; j++) {
						if(0 <= i && i < numRows && 0 <= j && j < numCols) {
							reveal(i, j); // recursion
						}
					}
				}
			}
		}
	}
	
	// return true if entire board is not revealed, false if at least one tile is revealed
	public boolean isCovered() {	
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				if(tiles[i][j].getReveal()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Tile[][] getTiles() {
		return this.tiles;
	}
	
	public int getNumRows() {
		return this.numRows;
	}
	
	public int getNumCols() {
		return this.numCols;
	}
	
	public int getNumMines() {
		return this.numMines;
	}
	
	public int getNumFlags() {
		return this.numFlags;
	}
	
	public int getNumUnrevealedSafeTiles() {
		return this.numUnrevealedSafeTiles;
	}
	
	public void setNumFlags(int numFlags) {
		this.numFlags = numFlags;
	}
}