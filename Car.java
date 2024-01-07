/* Car.java
 *
 *  This class builds the autonomous car and background, allowing for
 *  vehicle to infrastructure communication so the car knows where to turn and
 *  stop.
 *
 *  Compilation: javac-algs4 Car.java
 *  Run: java-algs4 Car
 */

import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

public class Car {

    // Display will be DISPLAY_SIZE x DISPLAY_SIZE
    private static int DISPLAY_SIZE = 100;

    // Sets the road width (matched in all input files)
    private final int ROAD_WIDTH = 2;

    // Sets all points that are road as true and all points that are not road as false
    static boolean[][] isRoad = new boolean[DISPLAY_SIZE][DISPLAY_SIZE];

    // Stores the horizontal-position of the car
    private double posX;

    // Stores the vertical-position of the car
    private double posY;

    // Stores the horizontal-velocity of the car
    private double vx;

    // Stores the vertical-velocity of the car
    private double vy;

    // Stores the car's general speed
    private double speed;

    // Stores the radius of the car for when it is drawn
    private double rad;

    // Stores what direction the car will accelerate in
    private char direction;

    // Stores the car's color
    private Color color;

    // Constructor that sets the initial position of the car and declares
    // the radius to be .5. Takes horizontal and vertical positions and velocities
    //  as well as color as arguments
    public Car(double startingX, double startingY, double veloX, double veloY, Color c) {
        posX = startingX;
        posY = startingY;
        vx = veloX;
        vy = veloY;
        rad = .5;
        color = c;
    }

    // Draws the background (the road system) with the 2D roads array as the argument
    public static void drawBackground(int[][] roads) {
        StdDraw.setXscale(0, DISPLAY_SIZE);
        StdDraw.setYscale(0, DISPLAY_SIZE);
        StdDraw.setPenColor(new Color(0, 71, 49));
        StdDraw.filledSquare(DISPLAY_SIZE / 2, DISPLAY_SIZE / 2, DISPLAY_SIZE / 2);
        StdDraw.setPenColor(Color.gray);

        for (int i = 0; i < roads.length; i++) {
            buildRoad(roads[i][0], roads[i][1], roads[i][2], roads[i][3]);
        }
    }


    // Draws the road with standard draw and sets the area of the road in isRoad to true
    // Takes xCenter, yCenter, half width and half height as arguments
    public static void buildRoad(int xCenter, int yCenter, int halfWidth, int halfHeight) {
        StdDraw.filledRectangle(xCenter, yCenter, halfWidth, halfHeight);

        for (int i = yCenter - halfHeight; i <= yCenter + halfHeight; i++) {
            for (int j = xCenter - halfWidth; j <= xCenter + halfWidth; j++) {
                if (i >= 0 && i < isRoad.length && j >= 0 && j < isRoad[i].length)
                    isRoad[i][j] = true;
            }
        }
    }

    // Updates the position given a time step (deltaT)
    public void updatePosition(double deltaT) {
        posX += vx * deltaT;
        posY += vy * deltaT;
    }


    // Turns the car right by changing the car's velocity depending on the
    // current trajectory
    public void turnRight() {
        if (vy != 0) {
            vx = vy;
            vy = 0;
        }

        else if (vx != 0) {
            vy = -1 * vx;
            vx = 0;
        }
    }

    // Turns the car left by changing the car's velocity depending on
    // the current trajectory
    public void turnLeft() {
        if (vy != 0) {
            vx = -1 * vy;
            vy = 0;
        }

        else if (vx != 0) {
            vy = vx;
            vx = 0;
        }
    }

    // Draws the car object
    public void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledSquare(posX, posY, rad);
    }

    // Checks if the car is on the road through the isRoad 2D array given
    // x and y positions as arguments
    public boolean onRoad(int py, int px) {
        if (px >= 0 && px <= DISPLAY_SIZE && py >= 0 && py <= DISPLAY_SIZE)
            return isRoad[py][px];
        return false;
    }

    // If the car hits a dead end, it will make a turn in the direction
    // where there is road or stop
    public void makeTurn() {
        if (!onRoad((int) (posY + ROAD_WIDTH), (int) (posX)) && vy > 0) {
            if (isRoad[(int) posY][(int) (posX - ROAD_WIDTH)]) {
                turnLeft();
            }
            else if (isRoad[(int) posY][(int) (posX + ROAD_WIDTH)]) {
                turnRight();
            }
            else {
                stop((int) posY + ROAD_WIDTH, (int) posX);
            }
        }

        else if (!onRoad((int) (posY), (int) (posX + ROAD_WIDTH)) && vx > 0) {
            if (isRoad[(int) (posY + ROAD_WIDTH)][(int) posX]) {
                turnLeft();
            }
            else if (isRoad[(int) (posY - ROAD_WIDTH)][(int) posX]) {
                turnRight();
            }
            else {
                stop((int) posY, (int) posX + ROAD_WIDTH);
            }
        }

        else if (!onRoad((int) (posY), (int) (posX - ROAD_WIDTH)) && vx < 0) {
            if (isRoad[(int) (posY + ROAD_WIDTH)][(int) posX]) {
                turnRight();
            }
            else if (isRoad[(int) (posY - ROAD_WIDTH)][(int) posX]) {
                turnLeft();
            }
            else {
                stop((int) posY, (int) posX - ROAD_WIDTH);
            }
        }

        else if (!onRoad((int) (posY - ROAD_WIDTH), (int) (posX)) && vy < 0) {
            if (isRoad[(int) posY][(int) (posX + ROAD_WIDTH)]) {
                turnLeft();
            }
            else if (isRoad[(int) posY][(int) (posX - ROAD_WIDTH)]) {
                turnRight();
            }
            else {
                stop((int) posY - ROAD_WIDTH, (int) posX);
            }
        }
    }

    // Allows car to accelerate after stoplight turns green
    public void accelerate(Stoplight sl) {
        if (vy == 0 && vx == 0 &&
                distanceTo(posX, posY, sl.getHorizPosition(), sl.getVertPosition()) < 2) {
            if (direction == 'v')
                vy = speed;
            else if (direction == 'h')
                vx = speed;
        }
    }

    // Calculates distance between two points (given two x and y positions)
    public static double distanceTo(double px1, double py1, double px2, double py2) {
        return Math.sqrt(Math.pow(px1 - px2, 2) + Math.pow(py1 - py2, 2));
    }

    // If the car is off or will go off the road it will stop
    public void stop(int py, int px) {
        if (!onRoad(py, px)) {
            vx = 0;
            vy = 0;
        }
        else
            stop();
    }

    // Stops the car if it goes off the road
    public void stop() {
        if (!onRoad((int) posY, (int) posX)) {
            if (vy != 0) {
                speed = vy;
                direction = 'v';
            }
            else if (vx != 0) {
                speed = vx;
                direction = 'h';
            }

            vy = 0;
            vx = 0;
        }
    }

    // If the light is red, it will treat it as there is no road there to stop the car
    // Given stoplight as an argument
    public static void hitRedlight(Stoplight a) {
        isRoad[a.getVertPosition()][a.getHorizPosition()] = false;
    }

    // Returns the horizontal velocity of the car
    public double getXVelo() {
        return vx;
    }

    // Returns the vertical velocity of the car
    public double getYVelo() {
        return vy;
    }

    // Returns the speed of the car
    public double getSpeed() {
        return speed;
    }

    // Returns the direction of the car

    public char getDirection() {
        return direction;
    }

    // Changes the horizontal velocity of the car given a double value
    public void setXVelo(double velX) {
        vx = velX;
    }

    // Changes the vertical velocity of the car given a double value
    public void setYVelo(double velY) {
        vy = velY;
    }

    // Changes the direction of the car given a character as input
    public void setDirection(char direction) {
        this.direction = direction;
    }

    // Changes the speed of the car given a double as input
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // Tests the other methods in the Car class
    public static void main(String[] args) {
        /* This should develop a very simple road system where
         * the car should initially move up, turn right, stop if it is a red light,
         * accelerate when the light turns green, turn left/up and stop at the end
         * of the track
         */
        int[][] roads = { { 25, 25, 1, 25 }, { 50, 49, 25, 1 }, { 75, 58, 1, 10 } };

        StdDraw.enableDoubleBuffering();
        drawBackground(roads);

        Car a = new Car(25, 2, 0, 0.2,
                        new Color(0, 0, 255));
        Stoplight b = new Stoplight(50, 49, 8000, 1500);

        while (true) {
            drawBackground(roads);
            b.drawStoplight();

            a.draw();
            a.updatePosition(2);
            a.makeTurn();

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

            a.stop();
        }
    }
}
