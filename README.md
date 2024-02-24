# Mines game and solver

## Project description
Mines game and solver implemented. Solver searches for known safe tiles, randomly guessing when implemented logic is not sufficient. Can choose to play manually in terminal or run AI games. If running solver, can either run a single game and see the end board result, or run a number of games with particular inputs and see win/loss statistics.

Compile: `javac MinesRunner.java`

## How to run
call `java MinesRunner`

The program will ask for input, beginning with the type of game (manual or AI).
If manual, the program will first ask for a difficulty: <br />
**Easy:** 8x8 grid, 8 mines <br />
**Medium:** 12x12 grid, 20 mines <br />
**Hard:** 14x18 grid, 42 mines

Then it will generate and print a starting grid in the terminal. The program repeatedly requests the type of move (flag/unflag or guess) as well as row/column indices of that move, followed by printing an updated grid. If all empty spaces are found, the game will end with a "You win" message, but if a mine is found, the game will end with a "You lose" message. The program will then ask if the user would like to play again.

Spaces on the grid are represented with the following symbols:
**0-8:** the number of mines in surrounding spaces.
**#:** unknown, unflagged tile
**+:** flagged tile
**X:** mine

If AI, the program will ask whether you'd like to play a single game with the resulting board or multiple games with win/loss statistics. Then it will ask for the size of the grid in rows and columns as well as the number of mines.

## Examples
Manual game
```
Enter 0 for manual game or 1 for AI game:
0
Enter 0 for Easy, 1 for Medium, or 2 for Hard:
0
      0  1  2  3  4  5  6  7
     ________________________
 0  | #  #  #  #  #  #  #  #
 1  | #  #  #  #  #  #  #  #
 2  | #  #  #  #  #  #  #  #
 3  | #  #  #  #  #  #  #  #
 4  | #  #  #  #  #  #  #  #
 5  | #  #  #  #  #  #  #  #
 6  | #  #  #  #  #  #  #  #
 7  | #  #  #  #  #  #  #  #

Enter 0 for reveal, or 1 for flag:
0
Enter a row, then a column:
0 0
      0  1  2  3  4  5  6  7
     ________________________
 0  | 0  0  0  0  1  #  #  #
 1  | 1  2  1  2  2  #  #  #
 2  | #  #  #  #  #  #  #  #
 3  | #  #  #  #  #  #  #  #
 4  | #  #  #  #  #  #  #  #
 5  | #  #  #  #  #  #  #  #
 6  | #  #  #  #  #  #  #  #
 7  | #  #  #  #  #  #  #  #

Enter 0 for reveal, or 1 for flag:
1
Enter a row, then a column:
2 2
      0  1  2  3  4  5  6  7
     ________________________
 0  | 0  0  0  0  1  #  #  #
 1  | 1  2  1  2  2  #  #  #
 2  | #  #  +  #  #  #  #  #
 3  | #  #  #  #  #  #  #  #
 4  | #  #  #  #  #  #  #  #
 5  | #  #  #  #  #  #  #  #
 6  | #  #  #  #  #  #  #  #
 7  | #  #  #  #  #  #  #  #
```
AI game
```
Enter 0 for manual game or 1 for AI game:
1
Enter 0 for one game and final board state or 1 for multiple games and resulting statistics:
1
How many games would you like to run? Enter a positive integer:
1000
Enter number of rows:
8
Enter number of columns:
8
Enter number of mines:
8

Wins:    784
Losses:  216
Win %:   78.4
```
