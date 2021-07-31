package cs1302.game;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import cs1302.game.MinesweeperGame;

/**
 * A driver program that use the functionality of 
 *{@code cs1302.game.MinesweeperGame}.
 */
public class MinesweeperDriver {

    /**
     * Attempts to use the an object from
     * {@code cs1302.game.MinesweeperGame} 
     * after inputting a seed file and checking if 
     * seed file exists. It also attempts to catch multiple
     * exceptions.
     *@param args the argument is the seed file's path
     */
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println();
                System.err.println("Invalid Usage Error: " +
                                   "MinesweeperDriver SEED_FILE_PATH");
                System.exit(1);
            }            
            String seedPath = args[0];
            Scanner stdIn = new Scanner(System.in);         
            MinesweeperGame game = new MinesweeperGame(stdIn, seedPath);
            game.play();
        } catch (FileNotFoundException fnfe) {
            System.out.println();
            System.out.println("Seed File Not Found Error: " + fnfe);
            System.exit(2);
        }  
    } //main
    
} //MineSweeperDriver
