/* UserCar.java
 *
 *  This class creates a user car that can be controlled
 *  with a keyboard object. The car will move left, right,
 *  up or down on the screen based on what it is told to do.
 *  It is a child class of the car since they share many
 *  similar characteristics.
 *
 *  Compile: javac-introcs UserCar.java
 *  Run: java-introcs UserCar
 */

import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

public class UserCar extends Car {

    /* User car constructor that calls parent class car constructor
     * takes input of starting x and y positions as well as x and y velocities
     *  and the car's color
     */
    public UserCar(double startingX, double startingY, double veloX,
                   double veloY, Color c) {
        super(startingX, startingY, veloX, veloY, c);
    }

    // Turns car in upward direction
    public void turnUp() {
        if (getXVelo() > 0)
            super.turnLeft();
        else if (getXVelo() < 0)
            super.turnRight();
    }

    // Turns car in downward direction
    public void turnDown() {
        if (getXVelo() > 0)
            super.turnRight();
        else if (getXVelo() < 0)
            super.turnLeft();
    }

    // Overrides parent turn left method to turn user-car left
    @Override
    public void turnLeft() {
        if (getYVelo() > 0) {
            setXVelo(-1 * getYVelo());
            setYVelo(0);
        }

        if (getYVelo() < 0) {
            setXVelo(getYVelo());
            setYVelo(0);
        }
    }

    // Overrides parent turn right method to turn user-car right
    @Override
    public void turnRight() {
        if (getYVelo() > 0) {
            setXVelo(getYVelo());
            setYVelo(0);
        }

        if (getYVelo() < 0) {
            setXVelo(-1 * getYVelo());
            setYVelo(0);
        }
    }

    // Checks the keyboard input to see how the user car should move
    // Takes a keyboard and user car objects as arguments
    public void checkInput(Keyboard k) {
        char c = k.nextKeyPressed();

        if (c == 'r') {
            turnRight();
        }
        else if (c == 'l') {
            turnLeft();
        }
        else if (c == 'u') {
            turnUp();
        }
        else if (c == 'd') {
            turnDown();
        }
        else if (c == 's') {
            brake();
        }
        else if (c == 'a') {
            accelerate();
        }
    }

    // Allows car to accelerate
    public void accelerate() {
        if (getYVelo() == 0 && getXVelo() == 0) {
            if (getDirection() == 'v')
                setYVelo(getSpeed());
            else if (getDirection() == 'h')
                setXVelo(getSpeed());
        }
    }

    // Stops the car
    public void brake() {

        if (getYVelo() != 0) {
            setSpeed(getYVelo());
            setDirection('v');
        }
        else if (getXVelo() != 0) {
            setSpeed(getXVelo());
            setDirection('h');
        }

        setYVelo(0);
        setXVelo(0);
    }

    // Tests the methods in the UserCar class
    public static void main(String[] args) {
        /* This should develop a very simple road system where
         * and the user should be able to control the car and
         * be able to move left, right, up, down and stop
         */
        int[][] roads = {
                { 25, 25, 1, 25 }, { 50, 49, 25, 1 },
                { 75, 58, 1, 10 }, { 85, 67, 10, 1 },
                { 95, 50, 1, 18 }, { 76, 32, 20, 1 }
        };

        StdDraw.enableDoubleBuffering();
        drawBackground(roads);

        UserCar a = new UserCar(25, 2, 0, 0.1,
                                new Color(0, 0, 255));
        Stoplight b = new Stoplight(50, 49, 6000, 2000);
        Keyboard k = new Keyboard();

        while (true) {
            if (k.wasNextKeyPressed()) {
                a.checkInput(k);
            }

            drawBackground(roads);
            b.drawStoplight();

            a.draw();
            a.updatePosition(2);

            StdDraw.show(20);
            StdDraw.clear();

            double time = System.currentTimeMillis();

            if ((time % (b.getTimeYellow() + b.getTimeRed() + b.getTimeRed()))
                    < b.getTimeGreen()) {
                b.changeLightGreen();
                a.accelerate(b);
            }
            else if ((time % (b.getTimeYellow() + b.getTimeRed() + b.getTimeRed()))
                    < (b.getTimeGreen() + b.getTimeYellow())) {
                b.changeLightYellow();
            }
            else {
                b.changeLightRed();
                hitRedlight(b);
            }
        }
    }
}
