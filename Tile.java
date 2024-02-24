// a particular grid location in a Board
class Tile {
	private int numSurroundingMines;
	private boolean isFlagged = false;
	private boolean isRevealed = false;

	/*
	 * Tile constructor
	 * numSurroundingMines: number of surrounding tiles containing a mine, -1 if mine
	 * isFlagged: whether tile is currently flagged
	 * isRevealed: whether tile is currently revealed
	 */
	public Tile(int numSurroundingMines, boolean isFlagged, boolean isRevealed) {
		this.numSurroundingMines = numSurroundingMines;
		this.isFlagged = isFlagged;
		this.isRevealed = isRevealed;
	}

	public void setNumSurroundingMines(int numSurroundingMines) {
		this.numSurroundingMines = numSurroundingMines;
	}
	
	public int getNumSurroundingMines() {
		return this.numSurroundingMines;
	}
	
	public void setFlag(boolean isFlagged) {
		this.isFlagged = isFlagged;
	}
	
	public boolean getFlag() {
		return this.isFlagged;
	}
	
	public void setReveal(boolean isRevealed) {
		this.isRevealed = isRevealed;
	}
	
	public boolean getReveal() {
		return this.isRevealed;
	}
}