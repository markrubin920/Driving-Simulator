/* Stoplight.java
 *
 *  This creates a stoplight object that changes colors
 *  after a specified amount of time.
 *
 *  Compile: javac-introcs Stoplight.java
 *  Run: java-introcs Stoplight
 */

import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

public class Stoplight {

    // Stores the color of the light
    private Color light;

    // Stores the x position of the light
    private int positionXSL;

    // Stores the y position of the light
    private int positionYSL;

    // Stores how long the light should be green
    private double timeRed;

    // Stores how long the light should be red
    private double timeGreen;

    // Stores the time the light is yellow
    private double timeYellow;

    // Constructor to build stoplight, takes the light's position and
    // time it is red and green
    public Stoplight(int px, int py, double timeRed, double timeGreen) {
        positionXSL = px;
        positionYSL = py;
        this.timeRed = timeRed;
        this.timeGreen = timeGreen;
        timeYellow = 1000;
        light = new Color(0, 255, 0);
    }

    // Draws the stoplight
    public void drawStoplight() {
        StdDraw.setPenColor(light);

        StdDraw.filledRectangle(positionXSL, positionYSL, .5, .5);
    }

    // Changes the light color to red
    public void changeLightRed() {
        light = new Color(255, 0, 0);
    }

    // Changes the light color to green
    public void changeLightGreen() {
        light = new Color(0, 255, 0);
    }

    // Changes the light color to yellow
    public void changeLightYellow() {
        light = new Color(255, 191, 0);
    }

    // Gets how long the light is red
    public double getTimeRed() {
        return timeRed;
    }

    // Gets how long the light is green
    public double getTimeGreen() {
        return timeGreen;
    }

    // Gets how long the light is yellow
    public double getTimeYellow() {
        return timeYellow;
    }

    // Gets horizontal position of light
    public int getHorizPosition() {
        return positionXSL;
    }

    // Gets vertical position of light
    public int getVertPosition() {
        return positionYSL;
    }

    // Tests the methods in the Stoplight class
    public static void main(String[] args) {
        // This should produce a stoplight that starts on green and switches
        // colors every 2 seconds

        StdDraw.setXscale(0, 10);
        StdDraw.setYscale(0, 10);
        Stoplight s = new Stoplight(3, 5, 5000, 2000);

        while (true) {
            s.drawStoplight();

            double time = System.currentTimeMillis();

            if ((time % (s.getTimeYellow() + s.getTimeRed() +
                    s.getTimeRed())) < s.getTimeGreen()) {
                s.changeLightGreen();
            }
            else if ((time % (s.getTimeYellow() + s.getTimeRed() +
                    s.getTimeRed())) < (s.getTimeGreen() + s.getTimeYellow())) {
                s.changeLightYellow();
            }
            else {
                s.changeLightRed();
            }
        }
    }
}
