/* Keyboard.java
 *
 *  Adapted from given keyboard class in COS126 Guitar Hero assignment,
 *  this class allows for user input to adjust the user car
 *
 *  Compile: javac-introcs Keyboard.java
 *  Run: java-introcs Keyboard
 */

import edu.princeton.cs.algs4.StdOut;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public class Keyboard {

    // initial font size
    private static final int DEFAULT_FONT_SIZE = 18;

    // initial width and height
    private final int initialWidth;
    private final int initialHeight;

    // keys linked list
    private LinkedList<Key> keys = new LinkedList<Key>();

    // for synchronization
    private final Object mouseLock = new Object();
    private final Object keyLock = new Object();

    // queue of typed keys (yet to be processed by client)
    private LinkedList<Character> keysTyped = new LinkedList<Character>();

    // set of key characters currently pressed down
    private TreeSet<Character> keysDown = new TreeSet<Character>();

    // Key that is being clicked (null if no such key)
    private Key mouseKey = null;

    // Stores the alphabet in the keyboard
    private char[] alphabet = { 'r', 'l', 'u', 'd', 's', 'a' };

    // default 37-key keyboard
    public Keyboard() {
        this("rludsa");
    }

    // custom keyboard of arbitrary size, takes alphabet string as input
    private Keyboard(String keyboardString) {

        // determine offset
        String[] keyNames = { "Right", "Left", "Up", "Down", "Stop", "Accelerate" };

        // create the keys
        for (int i = 0; i < keyNames.length; i++) {

            Key k = new Key(keys.size() + .1, keyNames[i], keyboardString.charAt(i));
            keys.add(k);
        }

        // reasonable values for initial dimensions
        initialWidth = 100 * keys.size();
        initialHeight = 200;

        // create and show the GUI (in the event-dispatching thread)
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new KeyboardPanel();
            panel.setPreferredSize(new Dimension(initialWidth, initialHeight));

            JFrame frame = new JFrame("User Controlled Car Panel");
            frame.setMinimumSize(new Dimension(initialWidth / 4, initialHeight / 4));
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);   // center on screen
            frame.setVisible(true);
        });
    }

    // Returns the last key that was pressed
    public char nextKeyPressed() {
        synchronized (keyLock) {
            if (keysTyped.isEmpty()) {
                throw new NoSuchElementException(
                        "your program has already processed all typed keys");
            }
            return keysTyped.removeLast();
        }
    }

    // Returns whether a key was pressed
    public boolean wasNextKeyPressed() {
        synchronized (keyLock) {
            return !keysTyped.isEmpty();
        }
    }

    // Returns if the letter is in the alphabet taking a character as input
    public boolean inAlphabet(char c) {
        for (int i = 0; i < alphabet.length; i++) {
            if (alphabet[i] == c)
                return true;
        }

        return false;
    }


    // the JPanel for drawing the keyboard
    private class KeyboardPanel extends JPanel implements MouseListener,
                                                          KeyListener {
        public static final long serialVersionUID = 12558137269921L;

        public KeyboardPanel() {
            setBackground(Color.WHITE);
            addMouseListener(this);
            addKeyListener(this);
            setFocusable(true);
        }

        // draw the keyboard
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);

            Dimension size = getSize();
            double width = size.getWidth();
            double height = size.getHeight();

            // draws the keys
            for (Key k : keys) {

                // mouse click or key typed
                if ((k == mouseKey) || keysDown.contains(k.getKeyStroke())) {
                    k.draw(g, width, height, Color.BLUE, Color.WHITE);
                }
                else {
                    k.draw(g, width, height, Color.WHITE, Color.BLACK);
                }
            }
        }

        /***************************************************************************
         *  Mouse events.
         ***************************************************************************/

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            synchronized (mouseLock) {
                Dimension size = getSize();
                double width = size.getWidth();
                double height = size.getHeight();
                double mouseX = e.getX() / width * keys.size();
                double mouseY = e.getY() / height;

                // Checks the keys
                for (Key k : keys) {
                    if (k.contains(mouseX, mouseY)) {
                        mouseKey = k;
                        char c = k.getKeyStroke();
                        keysTyped.addFirst(c);
                        repaint();
                        return;
                    }
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            synchronized (mouseLock) {
                mouseKey = null;
                repaint();
            }
        }


        /***************************************************************************
         *  Keyboard events.
         ***************************************************************************/

        public void keyTyped(KeyEvent e) {
            synchronized (keyLock) {
                char c = Character.toLowerCase(e.getKeyChar());
                keysTyped.addFirst(c);
            }
        }

        public void keyPressed(KeyEvent e) {
            synchronized (keyLock) {
                char c = Character.toLowerCase(e.getKeyChar());
                keysDown.add(c);
                repaint();
            }
        }

        public void keyReleased(KeyEvent e) {
            synchronized (keyLock) {
                char c = Character.toLowerCase(e.getKeyChar());
                keysDown.remove(c);
                repaint();
            }
        }
    }


    /***************************************************************************
     *  Helper data type to represent individual keys.
     ***************************************************************************/
    private Font getFont(int defaultFontSize, double width, double height) {
        int size = (int) (width * defaultFontSize / initialWidth);
        if (height < initialHeight / 2.0) size = 0;
        if (width < initialWidth / 2.0) size = 0;
        return new Font("SansSerif", Font.PLAIN, size);
    }

    private class Key {

        // key name (e.g., C)
        private final String name;

        // keyboard keystroke that correspond to piano key
        private final char keyStroke;

        // rectangle for key
        // (coordinate system is scaled so that white keys have width and height 1.0)
        private final double xmin, xmax, ymin, ymax;


        public Key(double x, String name, char keyStroke) {
            this.name = name;
            this.keyStroke = keyStroke;

            xmin = x;
            xmax = x + 0.6;
            ymin = 0.0;
            ymax = 0.6;
        }


        // draw the key using the given background and foreground colors
        private void draw(Graphics2D g, double width, double height,
                          Color backgroundColor, Color foregroundColor) {
            double SCALE_X = width / keys.size();
            double SCALE_Y = height;
            Rectangle2D.Double rectangle = new Rectangle2D.Double(xmin * SCALE_X,
                                                                  ymin * SCALE_Y,
                                                                  (xmax - xmin) * SCALE_X,
                                                                  (ymax - ymin) * SCALE_Y);

            // white key
            g.setColor(backgroundColor);
            g.fill(rectangle);

            // include outline (since fill color might be white)
            g.setColor(Color.BLACK);
            g.draw(rectangle);

            g.setFont(getFont(DEFAULT_FONT_SIZE, width, height));
            FontMetrics metrics = g.getFontMetrics();
            int hs = metrics.getHeight();
            int ws = metrics.stringWidth(name);
            g.setColor(foregroundColor);
            g.drawString(keyStroke + "", (float) ((xmin + xmax) /
                                 2.0 * SCALE_X - ws / 2.0),
                         (float) (0.95 * SCALE_Y - hs / 2.0));
        }

        // the computer keyboard keystroke corresponding to this piano key
        private char getKeyStroke() {
            return keyStroke;
        }

        // does the rectangle contain the given (x, y)
        private boolean contains(double x, double y) {
            return x >= xmin && x < xmax && y >= ymin && y < ymax;
        }

    }

    // Tests the methods in the Keyboard class
    public static void main(String[] args) {
        Keyboard keyboard = new Keyboard();

        // Prints all the keys typed in or pressed that are in the alphabet

        while (true) {
            if (keyboard.wasNextKeyPressed()) {
                char c = keyboard.nextKeyPressed();
                if (keyboard.inAlphabet(c))
                    StdOut.println(c);
            }
        }
    }
}
