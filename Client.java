/*  Client.java
 *   Mark Rubin and Jaisnav Rajesh, 2022
 *
 *   This is the client class that takes a command line argument
 *   of the environment and runs the driving simulator. It only has
 *   a main method.
 *
 *   Compile: javac-introcs Client.java
 *   Run: java-introcs Client < Environment1 or
 *   java-introcs Client < Environment2
 */

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

import java.awt.Color;

public class Client {

    // This creates the car simulation
    public static void main(String[] args) {

        // Reads number of autonomous cars from standard input
        int numAC = StdIn.readInt();

        // Reads number of roads from standard input
        int numRoads = StdIn.readInt();

        // Reads number of stoplights from standard input
        int numSL = StdIn.readInt();

        // Declares 2D array of roads
        int[][] roads = new int[numRoads][4];

        // Declares array of autonomous cars
        Car[] cars = new Car[numAC];

        // Declares array of stoplights
        Stoplight[] stoplights = new Stoplight[numSL];

        // Initializes user powered car's initial position
        UserCar uc = new UserCar(StdIn.readDouble(), StdIn.readDouble(),
                                 StdIn.readDouble(), StdIn.readDouble(),
                                 new Color(StdIn.readInt(), StdIn.readInt(), StdIn.readInt()));

        // Initializes autonomous cars array
        for (int i = 0; i < numAC; i++) {
            cars[i] = new Car(StdIn.readDouble(), StdIn.readDouble(),
                              StdIn.readDouble(), StdIn.readDouble(),
                              new Color(StdIn.readInt(), StdIn.readInt(), StdIn.readInt()));
        }

        // Initializes 2D roads array
        for (int i = 0; i < numRoads; i++) {
            for (int j = 0; j < 4; j++)
                roads[i][j] = StdIn.readInt();
        }

        // Initializes stoplights array
        for (int i = 0; i < numSL; i++) {
            stoplights[i] = new Stoplight(StdIn.readInt(), StdIn.readInt(),
                                          StdIn.readInt(), StdIn.readInt());
        }

        // Initializes standard draw
        StdDraw.enableDoubleBuffering();
        Car.drawBackground(roads);

        // Creates keyboard object
        Keyboard k = new Keyboard();

        // Simulation loop
        while (true) {

            // Checks if there was input for the user car and adjusts the car as such
            if (k.wasNextKeyPressed()) {
                uc.checkInput(k);
            }

            // Draws the road system
            Car.drawBackground(roads);

            // Draws the stoplights
            for (int i = 0; i < numSL; i++) {
                stoplights[i].drawStoplight();
            }

            // Draws the user car and updates its position
            uc.draw();
            uc.updatePosition(2);

            // Draws the autonomous cars and updates their positions
            for (int i = 0; i < numAC; i++) {
                cars[i].draw();
                cars[i].makeTurn();
                cars[i].updatePosition(2);
            }

            // Shows the cars in the standard draw window
            StdDraw.show(20);
            StdDraw.clear();

            // Checks the time to see which stoplights should be red, green or yellow
            double time = System.currentTimeMillis();

            for (int i = 0; i < numSL; i++) {
                if ((time % (stoplights[i].getTimeYellow() + stoplights[i].getTimeRed() +
                        stoplights[i].getTimeRed())) < stoplights[i].getTimeGreen()) {
                    stoplights[i].changeLightGreen();
                    for (int j = 0; j < numAC; j++) {
                        cars[j].accelerate(stoplights[i]);
                    }
                }
                else if ((time % (stoplights[i].getTimeYellow() +
                        stoplights[i].getTimeRed() + stoplights[i].getTimeRed()))
                        < (stoplights[i].getTimeGreen() + stoplights[i].getTimeYellow())) {
                    stoplights[i].changeLightYellow();
                }
                else {
                    stoplights[i].changeLightRed();
                    Car.hitRedlight(stoplights[i]);
                }

            }

            // Stops the autonomous cars if they go off the track
            for (int i = 0; i < numAC; i++)
                cars[i].stop();
        }
    }
}
