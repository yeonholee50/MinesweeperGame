package cs1302.game;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Integer;

/**
 * This class represent a MinesweeperGame. It
 * contains the methods the {@link cs1302.game.MinesweeperDriver}
 * would use.
 */
public class MinesweeperGame {
    
    private final Scanner sTDIN; //assigned variable for StdIn
    String command;
    String seedFile; //assigned instance variable for seedPath
    int numRow; //number of row
    int numCol; //number of column
    int numMine; //number of mine
    int[] xMine; //x coordinate of mine
    int[] yMine; //y coordinate of mine
    int roundsCompleted; //number of rounds completed
    boolean nofog = false; 
    String cmd; //first command
    int cmd1 = 0; //first integer command
    int cmd2 = 0; //second integer command
    double score;

    //to track which mines are maked, guessed, or revealed
    int[] xMarked;
    int[] yMarked;
    int[] xGuessed;
    int[] yGuessed;
    int[] xRevealed;
    int[] yRevealed;
    int[] revealedAdjacentSum; //stores adjacent sum 
    int[] xNonMine; //stores coordinates that are not mine
    int[] yNonMine;

    //for adjacent values
    int adjacentNumberCount;
    int adjacentX;
    int adjacentY;


    //for counting commands    
    int numMark;
    int numGuess;
    int numReveal;
    int numAdjacent;

    //boolean values to test User Input
    boolean correctCMD; //tests command

    //extra arrays needed
    String[] stringArrOfSeed;
    int[] arrOfSeed;
    int indexOfXMine;
    int indexOfYMine;
    int indexOfXNonMine;
    int indexOfYNonMine;
    boolean isMatch;
    String[] mainCmd;
    int[] arrOfCmd;
    int countOccurenceOfSpace;
    
    /**
     * Constructs a {@code MinesweeperGame} object with a specified stdIn 
     * and seedPath. StdIn is the command input while seedPath
     * is the seed given when called.
     *@param stdIn the command given by user
     *@param seedPath the seed given to {@code MinesweeperGame}
     *@throws FileNotFoundException if seed file is not found
     */  
    public MinesweeperGame(Scanner stdIn, String seedPath) throws FileNotFoundException {
        this.sTDIN = stdIn;
        this.seedFile = seedPath;
        Scanner scannerSeed = new Scanner(new File(seedFile));
        String seed = "";
        while (scannerSeed.hasNextLine()) {
            String line = scannerSeed.nextLine();
            seed = seed + line + " ";
        } //while loop
        //initialize rounds completed
        this.roundsCompleted = 0;
        //clear all white space and assign values to array
        seed = seed.replaceAll("\\s{2,}", " ");
        seed = seed.trim();
        stringArrOfSeed = new String [(seed.length() / 2) + 1];
        stringArrOfSeed = seed.split(" ");            
        //initialize an integer array to store seed
        arrOfSeed = new int [stringArrOfSeed.length];
        //convert string array to int array
        for (int i = 0; i < stringArrOfSeed.length; i++) {
            arrOfSeed[i] = Integer.parseInt(stringArrOfSeed[i]);
        } //for loop
        //index 0 to 2 gives row, column, mine respectively 
        this.numRow = arrOfSeed[0];
        this.numCol = arrOfSeed[1];
        this.numMine = arrOfSeed[2];
        if (numMine > ((arrOfSeed.length) - 3) / 2) {
            System.err.println();
            System.err.println("Seed File Malformed Error: " +
                               "a token is expected but not found");
            System.exit(3);
        }
        checkMalformedError();
        assignArrayLengths();
        //initialize indexes so they start at 0
        indexOfXMine = 0;
        indexOfYMine = 0;
        //set number of marked (useful for mapping)
        this.numMark = 0;
        this.numGuess = 0;
        this.numReveal = 0;
        this.numAdjacent = 0;
        this.xNonMine = new int[(numRow * numCol) - numMine];
        this.yNonMine = new int[(numRow * numCol) - numMine];
        assignMineCoordinates();
        lastMalformedError();
        //calculate xNonMined and yNonMined coordinates
        indexOfXNonMine = 0;
        indexOfYNonMine = 0;
        isMatch = false;
        assignNonMineCoordinates();
                
    } //MinesweeperGameConstructor

    /**
     * This method assigns squares that are not mine.
     */
    public void assignNonMineCoordinates() {
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                isMatch = false;
                for (int k = 0; k < numMine; k++) {
                    if (i == xMine[k]) {
                        if (j == yMine[k]) {
                            isMatch = true;
                        } //if j == yMine[k]
                    } //if i == xMine[k]
                    if (isMatch == false && k == numMine - 1) {
                        xNonMine[indexOfXNonMine] = i;
                        indexOfXNonMine = indexOfXNonMine + 1;
                        yNonMine[indexOfYNonMine] = j;
                        indexOfYNonMine = indexOfYNonMine + 1;
                    } //is not matched
                } //for
            }
        }
        
    }

    /**
     * This method assigns length to arrays.
     */
    public void assignArrayLengths() {
        //maximize arrays to their maximum index numbers
        xMarked = new int[numCol * numRow];
        yMarked = new int[numCol * numRow];
        xGuessed = new int[numCol * numRow];
        yGuessed = new int[numCol * numRow];
        xRevealed = new int[numCol * numRow];
        yRevealed = new int[numCol * numRow];
        revealedAdjacentSum = new int[numCol * numRow];
        //set x mine and y mine coordinate arrays
        xMine = new int[arrOfSeed[2]];
        yMine = new int[arrOfSeed[2]];
    }
    
    /**
     * This method assigns mine coordinates.
     */
    public void assignMineCoordinates() {
        for (int i = 3; i < stringArrOfSeed.length; i++) {
            xMine[indexOfXMine] = arrOfSeed[i];
            indexOfXMine = indexOfXMine + 1;
            i = i + 1; //goes onto next index of arrOfSeed
            yMine[indexOfYMine] = arrOfSeed[i];
            indexOfYMine = indexOfYMine + 1;
        } //for
        
    }
    
    /**
     * This method checks for malformed errors.
     */
    public void checkMalformedError() {
        //checks malformed error 1
        if (stringArrOfSeed.length < 3) {
            System.err.println();
            System.err.println("Seed File Malformed Error: " +
                               " a token is expected but not found");
            System.exit(3);
        }
        //checks malformed error 2
        for (int i = 0; i < stringArrOfSeed.length; i++) {
            if (! stringArrOfSeed[i].equals("0") &&
                ! stringArrOfSeed[i].equals("1") &&
                ! stringArrOfSeed[i].equals("2") &&
                ! stringArrOfSeed[i].equals("3") &&
                ! stringArrOfSeed[i].equals("4") &&
                ! stringArrOfSeed[i].equals("5") &&
                ! stringArrOfSeed[i].equals("6") &&
                ! stringArrOfSeed[i].equals("7") &&
                ! stringArrOfSeed[i].equals("8") &&
                ! stringArrOfSeed[i].equals("9") &&
                ! stringArrOfSeed[i].equals("10")) {
                System.err.println();
                System.err.println("Seed File Malformed Error: " +
                                   "the token is not of the expected type" +
                                   " (e.g., it's expected to be an int but it's not)");
                System.exit(3);
            }
            //handles malformed errors 3 - 5
            if (numRow < 5 || numRow > 10) {
                System.err.println();
                System.err.println("Seed File Malformed Error:" +
                                   " the token for rows is less than 5 or greater than 10");
                System.exit(3);
            }
            if (numCol < 5 || numRow > 10) {
                System.err.println();
                System.err.println("Seed File Malformed Error:" +
                                   " the token for cols is less than 5 or greater than 10");
                System.exit(3);
            }
            if (numMine < 1 || numMine > ((numRow * numCol) - 1)) {
                System.err.println();
                System.err.println("Seed File Malformed Error:" +
                                   " the token for numMines is less than" +
                                   " 1 or greater than (rows * cols) - 1");
                System.exit(3);
            }
            
        } //for
        
    }

    /**
     * It checks for the last malformed error regarding mines.
     */
    public void lastMalformedError() {
        //handles malformed error 6
        for (int i = 0; i < numMine; i++) {
            if (xMine[i] >= numRow || xMine[i] < 0
                || yMine[i] >= numCol || yMine[i] < 0) {
                System.err.println();
                System.err.println("Seed file Malformed Error:" +
                                   " the location of a mine is not in bounds");
                System.exit(3);
            }
        }
    } //last Malformed Error
    
    /**
     * It imports a welcome message and converts it to a string.
     * It is called when the game begins.
     */
    public void printWelcome() {
        try {
            Scanner welcomeMessage = new Scanner(new File("resources/welcome.txt"));
            
            while (welcomeMessage.hasNextLine()) {
                System.out.println(welcomeMessage.nextLine());
            } //while
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println("Welcome Message File Not Found: " + fnfe);
            System.exit(0);
            
            
        }
    } //printWelcome
    
    /**
     * It prints a normal map without mine location based 
     * on user inputs (mark, reveal, guess). If the user 
     * puts in a nofog command, then the map will reveal
     * all mines.
     */
    public void printMineField() {
        System.out.println("Rounds Completed: " + roundsCompleted);
        System.out.println();
        String slash = ""; // straight slash |
        int rowCount = 0; //current count of row
        //if the user does not put in nofog
        if (nofog == false) {
            for (rowCount = 0; rowCount < numRow; rowCount++) {
                slash = ""; //reset 
                for (int j = 0; j < numCol; j++) {
                    boolean revealed = false; //reset
                    boolean guessed = false;
                    boolean marked = false;
                    for (int r = 0; r < numReveal; r++) { 
                        if (rowCount == xRevealed[r] && j == yRevealed[r]) {
                            slash = slash + "| " + revealedAdjacentSum[r]
                                + " "; 
                            revealed = true;       
                        } //if reveal
                    } //for reveal
                    for (int g = 0; g < numGuess; g++) {
                        if (rowCount == xGuessed[g] && j == yGuessed[g]) {
                            slash = slash + "| ? ";
                            guessed = true;
                        }
                    } //for guess
                    for (int m = 0; m < numMark; m++) {
                        if (rowCount == xMarked[m] && j == yMarked[m]) {
                            slash = slash + "| F ";
                            marked = true;
                        }
                    } //for mark
                    if (revealed == false && guessed == false
                        && marked == false) {
                        slash = slash + "|   ";
                    }
                } //for
                System.out.println(rowCount + " " + slash + "|");
            } //for
            //if at last row
            if (rowCount == numRow) {
                System.out.print(" "); //four spaces in the beginning
                for (int j = 0; j < numCol; j++) {
                    System.out.print("   " + j);
                } //prints the last row
            } //if at last row
        } //if fog = false
        //if user puts in nofog when prompted input
        if (nofog == true) {
            minefieldNoFog();
        } // if fog = true
    } //printMineField
    
    /**
     * When input is nofog, this map will be printed.
     */
    public void minefieldNoFog() {
        int rowCount = 0; //current count of row      
        for (rowCount = 0; rowCount < numRow; rowCount++) {
            String slash = ""; //reset
            for (int j = 0; j < numCol; j++) {
                boolean revealed = false; //reset
                boolean guessed = false;
                boolean marked = false;
                boolean mined = false;
                //check for mines first
                for (int m = 0; m < numMine; m++) {
                    if (rowCount == xMine[m] && j == yMine[m]) {
                        for (int g = 0; g < numGuess; g++) {
                            if (rowCount == xGuessed[g] && j == yGuessed[g]) {
                                slash = slash + "|<?>";
                                mined = true;
                            } //if
                        } //for
                        for (int n = 0; n < numMark; n++) {
                            if (rowCount == xMarked[n] && j == yMarked[n]) {
                                slash = slash + "|<F>";
                                mined = true;
                            } //if
                        } //for
                        if (mined == false) {
                            slash = slash + "|< >";
                            mined = true;
                        } //if
                    } //if
                } //for
                for (int r = 0; r < numReveal; r++) {
                    if (rowCount == xRevealed[r] && j == yRevealed[r] && mined == false) {
                        slash = slash + "| " + revealedAdjacentSum[r] + " ";
                        revealed = true;
                    } //if reveal
                } //for reveal
                for (int g = 0; g < numGuess; g++) {
                    if (rowCount == xGuessed[g] && j == yGuessed[g] && mined == false) {
                        slash = slash + "| ? ";
                        guessed = true;
                    } //if guess
                } //for guess
                for (int m = 0; m < numMark; m++) {
                    if (rowCount == xMarked[m] && j == yMarked[m] && mined == false) {
                        slash = slash + "| F ";
                        marked = true;
                    } //if mark
                } //for mark
                //if not revealed, marked, guessed etc. print normal
                if (revealed == false && guessed == false && marked == false && mined == false) {
                    slash = slash + "|   ";
                }
            } //for
            System.out.println(rowCount + " " + slash + "|");
        } //for
        System.out.print(" "); //four spaces in the beginning
        for (int j = 0; j < numCol; j++) {
            System.out.print("   " + j);
        } //prints the last row
    } //noFog minefield
    
    
    
    /**
     * It get user input and calls on specific methodos
     * based on the command. If mark, nofog, reveal, or guess
     * are used, then a rounds is completed
     *@throws ArrayIndexOutOfBoundsException if index that doesn't exist is called
     *@throws NumberFormatException if the format of a command is wrong
     *@throws NullPointerException if command doesn't exist
     */
    public void promptUser() throws ArrayIndexOutOfBoundsException,
                                    NumberFormatException, NullPointerException {
        // gets user input
        System.out.print("minesweeper-alpha: ");
        command = sTDIN.nextLine();
        command = command.trim();
        command = command.replaceAll("\\s{2,}", " ");
        //for parsing and putting into array
        countOccurenceOfSpace = 0;
        for (int i = 0; i < command.length(); i++) {
            if (command.charAt(i) == ' ') {
                countOccurenceOfSpace = countOccurenceOfSpace + 1;
            } //if
        } //for - counting all whitespaces
        mainCmd = new String[countOccurenceOfSpace + 1]; //set size of array
        mainCmd = command.split(" ");
        arrOfCmd = new int [countOccurenceOfSpace];
        correctCMD = false;
        if (mainCmd.length == 2 || mainCmd.length > 3) {
            throw new NumberFormatException(); 
        } //if string is not in form cmd int int or cmd, exception is thrown
        checkValidCommand(); //check if commands are valid (performs tests)
       
        if (cmd1 <= numRow - 1 && cmd2 <= numCol - 1) {           
            if (cmd.equals("mark") || cmd.equals("m")) {
                mark();     
            } //if mark
            if (cmd.equals("guess") || cmd.equals("g")) {
                guess();
            } //if guess
            if (cmd.equals("reveal") || cmd.equals("r")) {
                reveal();  
            } //if reveal 
            if (cmd.equals("nofog")) {
                nofog();
            } //if no fog
            if (cmd.equals("help") || cmd.equals("h")) {
                help();
            } //if help
            if (cmd.equals("quit") || cmd.equals("q")) {
                quit();
            } //if quit     
        } else { //if number is not within bound
            throw new ArrayIndexOutOfBoundsException();
        }
        
    } //promptUser

    /**
     * This method checks if first command (not integers) are valid.
     */
    public void checkValidCommand() {
        if (mainCmd[0].equalsIgnoreCase("reveal") || mainCmd[0].equalsIgnoreCase("r")
            || mainCmd[0].equalsIgnoreCase("mark") || mainCmd[0].equalsIgnoreCase("m")
            || mainCmd[0].equalsIgnoreCase("guess") || mainCmd[0].equalsIgnoreCase("g")) {
            cmd = mainCmd[0].toLowerCase(); //the main command itself
            for (int i = 0; i < countOccurenceOfSpace; i++) {
                arrOfCmd[i] = Integer.parseInt(mainCmd[i + 1]);
            } //for
            cmd1 = arrOfCmd[0]; //first integer value of command
            cmd2 = arrOfCmd[1]; //second integer value of command
            correctCMD = true;
        } //if
        if (mainCmd[0].equalsIgnoreCase("help") || mainCmd[0].equalsIgnoreCase("h")
            || mainCmd[0].equalsIgnoreCase("quit") || mainCmd[0].equalsIgnoreCase("q")
            || mainCmd[0].equalsIgnoreCase("nofog")) {
            cmd = mainCmd[0].toLowerCase(); //the main command itself
            correctCMD = true;
        } //if
        
    }
    
    /**
     * If this method is called, the boolean nofog will 
     * be true. This will give position of the mines
     * when printing the minefield.
     */
    public void nofog() {
        nofog = true;
        roundsCompleted = roundsCompleted + 1;
    }
    
    
    /**
     *If this method is called, a help menu will pop us.
     */
    public void help() {
        System.out.println();
        System.out.println("Commands Available...");
        System.out.println(" - Reveal: r/reveal row col");
        System.out.println(" -   Mark: m/mark   row col");
        System.out.println(" -  Guess: g/guess  row col");
        System.out.println(" -   Help: h/help          ");
        System.out.println(" -   Quit: q/quit          ");
    }
    
    /**
     * If this method is called, it will output a quit message
     * before exiting with status 0.
     */
    public void quit() {
        System.out.println();
        System.out.println("Quitting the game... ");
        System.out.println("Bye!");
        System.exit(0);
    } //quit
    
    /**
     * Checks if there was a previous input 
     * that had the same two integer values and
     * deletes them so they won't appear when 
     * called printing mindfield. Stores the marked 
     * numbers in an array that can be called when printing
     * mindfield.
     */
    public void mark() {
        for (int m = 0; m < numMark; m++) {
            if (cmd1 == xMarked[m] && cmd2 == yMarked[m]) {
                numMark = numMark - 1; //if previously marked, 
                for (int i = m; i < numMark; i++) { //removes index
                    xMarked[i] = xMarked[i + 1]; //if marked previously, 
                    yMarked[i] = yMarked[1 + 1]; //moves index up by 1
                }
            } //if
        } //for mark
        
        
        xMarked[numMark] = cmd1;
        yMarked[numMark] = cmd2;
        
        //if the coordinate was guessed,
        //we have to empty that array by moving it up by one
        for (int g = 0; g < numGuess; g++) {
            if (cmd1 == xGuessed[g] && cmd2 == yGuessed[g]) {
                numGuess = numGuess - 1;
                for (int i = g; i < numGuess; i++) {
                    xGuessed[i] = xGuessed[i + 1];
                    yGuessed[i] = yGuessed[i + 1];
                } //for
            } //if
        } //for
        for (int r = 0; r < numReveal; r++) {
            if (cmd1 == xRevealed[r] && cmd2 == yRevealed[r]) {
                numReveal = numReveal - 1;
                for (int i = r; i < numReveal; i++) {
                    xRevealed[i] = xRevealed[i + 1];
                    yRevealed[i] = yRevealed[i + 1];
                }
            }
        }
        numMark = numMark + 1;
        roundsCompleted = roundsCompleted + 1;
         
    } //mark
    
    /**
     * Checks if there was a previous input
     * that had the same two integer values and
     * deletes them so they won't appear when
     * called printing mindfield. Stores the guessed
     * numbers in an array that can be called when printing
     * mindfield.
     */
    public void guess() {    
        for (int g = 0; g < numGuess; g++) {
            if (cmd1 == xGuessed[g] && cmd2 == yGuessed[g]) {
                numGuess = numGuess - 1;
                for (int i = g; i < numGuess; i++) {
                    xGuessed[i] = xGuessed[i + 1];
                    yGuessed[i] = yGuessed[i + 1];
                } //for
            } //if
        } //for
        
        xGuessed[numGuess] = cmd1;
        yGuessed[numGuess] = cmd2;
        
        for (int m = 0; m < numMark; m++) {
            if (cmd1 == xMarked[m] && cmd2 == yMarked[m]) {
                numMark = numMark - 1;
                for (int i = m; i < numMark; i++) {
                    xMarked[i] = xMarked[i + 1];
                    yMarked[i] = yMarked[i + 1];
                } //for
            } //if
        } //for
        
        for ( int r = 0; r < numReveal; r++) {
            if (cmd1 == xRevealed[r] && cmd2 == yRevealed[r]) {
                numReveal = numReveal - 1;
                for (int i = r; i < numReveal; i++) {
                    xRevealed[i] = xRevealed[i + 1];
                    yRevealed[i] = yRevealed[i + 1];
                }
            }
        }
        
        numGuess = numGuess + 1;
        roundsCompleted = roundsCompleted + 1;
        
    } //guess
    
    /**
     * Checks if there was a previous input
     * that had the same two integer values and
     * deletes them so they won't appear when
     * called printing mindfield. Stores the revealed
     * numbers in an array that can be called when printing
     * mindfield. Along with the revealed number, this
     * method also stores the respective adjacent number
     * in a different array that can also be called
     * when printing the minefield.
     */
    public void reveal() {
        for (int r = 0; r < numReveal; r++) {
            if (cmd1 == xRevealed[r] && cmd2 == yRevealed[r]) {
                numReveal = numReveal - 1;
                numAdjacent = numAdjacent - 1;
                for (int i = r; i < numReveal; i++) {
                    xRevealed[i] = xRevealed[i + 1];
                    yRevealed[i] = yRevealed[i + 1];
                    revealedAdjacentSum[i] = revealedAdjacentSum[i + 1];
                }
            }
        }
        xRevealed[numReveal] = cmd1;
        yRevealed[numReveal] = cmd2;
        //gotta reset arrays for mark and guess if they were revelaed
        for (int g = 0; g < numGuess; g++) {
            if (cmd1 == xGuessed[g] && cmd2 == yGuessed[g]) {
                numGuess = numGuess - 1;
                for (int i = g; i < numGuess; i++) {
                    xGuessed[i] = xGuessed[i + 1];
                    yGuessed[i] = yGuessed[i + 1];
                } //for
            } //if
        } //for guess
        for (int m = 0; m < numMark; m++) {
            if (cmd1 == xMarked[m] && cmd2 == yMarked[m]) {
                numMark = numMark - 1;
                for (int i = m; i < numMark; i++) {
                    xMarked[i] = xMarked[i + 1];
                    yMarked[i] = yMarked[1 + 1];
                }
            } //if
        } //for mark        
        findAdjacentNumbers();
        // at the end, we assign arrays and add index count
        revealedAdjacentSum[numAdjacent] = adjacentNumberCount; //assigns to index
        numAdjacent = numAdjacent + 1; //number of index goes up
        numReveal = numReveal + 1; //number of index goes up
        roundsCompleted = roundsCompleted + 1;
    } //reveal method

    /**
     * For each input coordinate, this method will find the adjacent numbers and call
     * on another method to check if it is a mine.
     */
    public void findAdjacentNumbers() {
        adjacentNumberCount = 0;
        //for -1 0 case
        adjacentX = cmd1 - 1;
        adjacentY = cmd2 - 0;
        checkAdjacentNumber();
        //for +1, 0 case
        adjacentX = cmd1 + 1;
        adjacentY = cmd2 - 0;
        checkAdjacentNumber();
        //for 0, -1 case
        adjacentX = cmd1 - 0;
        adjacentY = cmd2 - 1;
        checkAdjacentNumber();
        //for 0, +1 case
        adjacentX = cmd1 - 0;
        adjacentY = cmd2 + 1;
        checkAdjacentNumber();
        //for -1,-1 case
        adjacentX = cmd1 - 1;
        adjacentY = cmd2 - 1;
        checkAdjacentNumber();
        //for -1, +1 case
        adjacentX = cmd1 - 1;
        adjacentY = cmd2 + 1;
        checkAdjacentNumber();
        //for +1, -1 case
        adjacentX = cmd1 + 1;
        adjacentY = cmd2 - 1;
        checkAdjacentNumber();
        //for +1, +1 case
        adjacentX = cmd1 + 1;
        adjacentY = cmd2 + 1;
        checkAdjacentNumber();

    }
    
    /**
     * For various inputs of x and y, it will calculate if the value is touching a mine.
     * If it is, then the adjacent number count will count up by 1.
     */
    public void checkAdjacentNumber() {
        //if not within bound, adjacent number stays the same
        if ((adjacentX > numRow - 1) || (adjacentY > numCol - 1)) {
            adjacentNumberCount = adjacentNumberCount  + 0;
        } else { //within bound
            for (int i = 0; i < numMine; i++) {
                if (adjacentX == xMine[i]) {
                    if (adjacentY == yMine[i]) {
                        adjacentNumberCount = adjacentNumberCount + 1;
                        //if within bound and values match, add 1 to the count
                    } // if y match
                } //if x match
            }
        }
        
    }
    
    /**
     * Specifies condition to win by checking all mines were marked
     * and that all nonmine squares were revealed. If both condition are
     * true, then this method return the user has won.
     * @return success if the win conditions are met, returns true
     */
    public boolean isWon() {
        //for losses (like revealing a mine, we will take care of that in play)
        boolean success = false;
        boolean allCheckedMark = false;
        boolean allCheckedReveal = false;
        int markedCells = 0;
        int revealedCells = 0;
        //use if statements to check is all mines are marked
        //and all squares not containing a mine are revealed
        for (int i = 0; i < numMark; i++) {
            for (int j = 0; j < numMine; j++) {
                if (i == (numMark - 1) && j == (numMine - 1)) {
                    allCheckedMark = true;
                } //if will check is all cells were checked
                if (xMarked[i] == xMine[j] && yMarked[i] == yMine[j]) {
                    markedCells = markedCells + 1;
                } //if will check marked cells
                if (allCheckedMark == true && markedCells == numMine) {
                    for (int f = 0; f < numReveal; f++) {
                        for (int k = 0; k < (numCol * numRow) - numMine; k++) {
                            if (f == (numReveal - 1) &&
                                k == (numRow * numCol) - numMine - 1) {
                                allCheckedReveal = true;
                            } //if all cells were checked
                            if (xRevealed[f] == xNonMine[k] &&
                                yRevealed[f] == yNonMine[k]) {
                                revealedCells = revealedCells + 1;
                            } //if will check revealed cells
                            if (allCheckedReveal == true &&
                                revealedCells == (numCol * numRow) - numMine) {
                                success = true;
                            } //if
                        } //for
                    } //for
                } //if all mines are marked
            }
        } //for
        
        return success;
    } //isWon
    
   /**
    *Prints the win message if the game is won. Also,
    * the method calculates the score and gives the 
    * score attached to the win message.
    */
    public void printWin()  {
        try {
            Scanner winMessage = new Scanner(new File("resources/gamewon.txt"));
            int countWinMessageLine = 0; //needed to print score on win message
            score = 100 * (numRow * numCol) / roundsCompleted;
            while (winMessage.hasNextLine()) {
                countWinMessageLine = countWinMessageLine + 1;
                if (countWinMessageLine == 19) {
                    System.out.print(winMessage.nextLine() + " ");
                    System.out.printf("%.2f", score);
                } else {
                    System.out.println(winMessage.nextLine());
                }
            } //while
            System.out.println();
            
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println("Win Message Not Found Error: " + fnfe);
            System.exit(0);
        }
    } //printWin
    
    /**
     * If the game is lost, this method will print the loss message.
     */
    public void printLoss() {
        try {
            Scanner lossMessage = new Scanner(new File("resources/gameover.txt"));
            
            while (lossMessage.hasNextLine()) {
                System.out.println(lossMessage.nextLine());
            } //while
            System.out.println();
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println("Loss Message Not Found Error: " + fnfe);
            System.exit(0);
        }
    } //printLoss
    
    /**
     * This method is where the main game loops. In the beginning
     * the game sets a prints the welcome and loops until either 
     * the game is won or lost. If the game is won, then the loop
     * will default to printing the win message. Vice versa for loss message.
     * The game specifies loss condition as revealing a mine. Also, method
     * wil attempt to catch multiple exceptions and delegate a proper 
     * response for each kind of exception.
     */
    public void play() {
        //edit later - this is to test all methods
        //dont forget to print map after command
        //dont forget to set nofog = 0 after promptuser and printminefield
        boolean lost = false;
        printWelcome();
        while (isWon() == false && lost == false) {
            try {
                printMineField();
                System.out.println();
                nofog = false;
                System.out.println();
                promptUser();
                    
                while (correctCMD == false) {
                    System.err.println();
                    System.err.println("Invalid Command: " + "Command not recognized!");
                    System.out.println();
                    promptUser();
                }
                System.out.println();
                for (int i = 0; i < numReveal; i++) {
                    for (int j = 0; j < numMine; j++) {
                        if (xRevealed[i] == xMine[j]) {
                            if (yRevealed[i] == yMine[j]) {
                                lost = true;
                            }
                        }
                    } //for
                } //for to calculate loss
            } catch (NullPointerException npe) {
                System.err.println();
                System.err.println("Invalid Command: " + npe);
                System.err.println();
            } catch (ArrayIndexOutOfBoundsException oobe) {
                System.err.println();
                System.err.println("Invalid Command: " + oobe);
                System.err.println();
            } catch (NumberFormatException nfe) {
                System.err.println();
                System.err.println("Invalid command: " + nfe);
                System.err.println();
            }
            
        }
        if (isWon() == true) {
            printWin();
            System.exit(0);
        }
        if (lost == true) {
            printLoss();
            System.exit(0);
        }
        
    } //play
    
} //MinesweeperGameClass
