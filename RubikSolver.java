/*
Joshua Liu
Rubik's Solver
*/

import java.util.HashMap;

public class RubikSolver {
    static final byte WHITE = 1;
    static final byte YELLOW = 2;
    static final byte RED = 3;
    static final byte ORANGE = 4;
    static final byte BLUE = 5;
    static final byte GREEN = 6;

    static final byte[] YELLOW_FACE = { 0, 8 };
    static final byte[] BLUE_FACE = { 9, 17 };
    static final byte[] RED_FACE = { 18, 26 };
    static final byte[] GREEN_FACE = { 27, 35 };
    static final byte[] ORANGE_FACE = { 36, 44 };
    static final byte[] WHITE_FACE = { 45, 53 };

    static final byte[] RIGHT_MAIN = { 2, 5, 8, 20, 23, 26, 47, 50, 53, 42, 39, 36 };
    static final byte[] RIGHT_SUB = { 27, 30, 33, 34, 35, 32, 29, 28 };

    static final byte[] LEFT_MAIN = { 6, 3, 0, 38, 41, 44, 51, 48, 45, 24, 21, 18 };
    static final byte[] LEFT_SUB = { 17, 14, 11, 10, 9, 12, 15, 16 };

    static final byte[] UP_MAIN = { 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38 };
    static final byte[] UP_SUB = { 0, 3, 6, 7, 8, 5, 2, 1 };

    static final byte[] DOWN_MAIN = { 44, 43, 42, 35, 34, 33, 26, 25, 24, 17, 16, 15 };
    static final byte[] DOWN_SUB = { 47, 46, 45, 48, 51, 52, 53, 50 };

    static final byte[] FRONT_MAIN = { 8, 7, 6, 11, 14, 17, 45, 46, 47, 33, 30, 27 };
    static final byte[] FRONT_SUB = { 18, 21, 24, 25, 26, 23, 20, 19 };

    static final byte[] BACK_MAIN = { 0, 1, 2, 29, 32, 35, 53, 52, 51, 15, 12, 9 };
    static final byte[] BACK_SUB = { 38, 37, 36, 39, 42, 43, 44, 41 };

    static byte[] cube = new byte[54];

    static HashMap<Integer, String> colourMap = new HashMap<Integer, String>();

    public static void main(String[] args) {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        initCube();
        makeMove("R", 1);
        makeMove("U", 1);
        displayCube();
    }

    public static void initCube() {
        colourMap.put(1, "W");
        colourMap.put(2, "Y");
        colourMap.put(3, "R");
        colourMap.put(4, "O");
        colourMap.put(5, "B");
        colourMap.put(6, "G");
        for (byte i = 0; i < 9; i++) {
            cube[i] = YELLOW;
        }
        for (byte i = 9; i < 18; i++) {
            cube[i] = BLUE;
        }
        for (byte i = 18; i < 27; i++) {
            cube[i] = RED;
        }
        for (byte i = 27; i < 36; i++) {
            cube[i] = GREEN;
        }
        for (byte i = 36; i < 45; i++) {
            cube[i] = ORANGE;
        }
        for (byte i = 45; i < 54; i++) {
            cube[i] = WHITE;
        }
    }

    public static byte[] getFace(byte[] faceI) {
        byte[] face = new byte[9];
        byte sI = 0;
        for (int i = faceI[0]; i <= faceI[1]; i++, sI++) {
            face[sI] = cube[i];
        }
        return face;
    }

    public static void printMapToColour(byte[] array, int start, int end) {
        for (int i = start; i < end; i++) {
            System.out.print(colourMap.get((int) array[i]));
        }
    }

    public static void displayCube() {
        byte[] yellow = getFace(YELLOW_FACE);
        byte[] blue = getFace(BLUE_FACE);
        byte[] red = getFace(RED_FACE);
        byte[] green = getFace(GREEN_FACE);
        byte[] orange = getFace(ORANGE_FACE);
        byte[] white = getFace(WHITE_FACE);
        System.out.printf("%3s", " ");
        printMapToColour(yellow, 0, 3);
        System.out.printf("\n%3s", " ");
        printMapToColour(yellow, 3, 6);
        System.out.printf("\n%3s", " ");
        printMapToColour(yellow, 6, 9);
        System.out.println();
        printMapToColour(blue, 0, 3);
        printMapToColour(red, 0, 3);
        printMapToColour(green, 0, 3);
        printMapToColour(orange, 0, 3);
        System.out.println();
        printMapToColour(blue, 3, 6);
        printMapToColour(red, 3, 6);
        printMapToColour(green, 3, 6);
        printMapToColour(orange, 3, 6);
        System.out.println();
        printMapToColour(blue, 6, 9);
        printMapToColour(red, 6, 9);
        printMapToColour(green, 6, 9);
        printMapToColour(orange, 6, 9);
        System.out.printf("\n%3s", " ");
        printMapToColour(white, 0, 3);
        System.out.printf("\n%3s", " ");
        printMapToColour(white, 3, 6);
        System.out.printf("\n%3s", " ");
        printMapToColour(white, 6, 9);
    }

    public static void moveHelper(byte[] SIDE, int start, int end) {
        byte[] tempSide = new byte[end];
        byte tempSideI = 0;
        for (byte i : SIDE) {
            tempSide[tempSideI] = cube[i];
            tempSideI++;
        }
        tempSideI = 0;
        for (int i = start; i < end; i++, tempSideI++) {
            cube[SIDE[tempSideI]] = tempSide[i];
        }
        for (int i = 0; i < start; i++, tempSideI++) {
            cube[SIDE[tempSideI]] = tempSide[i];
        }
    }
    
    public static void makeMove(String move, int amt) {
        switch (move) {
            case "R":
                for (int i = 0; i < amt; i++) {
                    moveHelper(RIGHT_MAIN, 3, 12);
                    moveHelper(RIGHT_SUB, 2, 8);
                }
                break;
            case "L":
                for (int i = 0; i < amt; i++) {
                    moveHelper(LEFT_MAIN, 3, 12);
                    moveHelper(LEFT_SUB, 2, 8);
                }
                break;
            case "U":
                for (int i = 0; i < amt; i++) {
                    moveHelper(UP_MAIN, 3, 12);
                    moveHelper(UP_SUB, 2, 8);
                }
                break;
            case "D":
                for (int i = 0; i < amt; i++) {
                    moveHelper(DOWN_MAIN, 3, 12);
                    moveHelper(DOWN_SUB, 2, 8);
                }
                break;
            case "F":
                for (int i = 0; i < amt; i++) {
                    moveHelper(FRONT_MAIN, 3, 12);
                    moveHelper(FRONT_SUB, 2, 8);
                }
                break;
            case "B":
                for (int i = 0; i < amt; i++) {
                    moveHelper(BACK_MAIN, 3, 12);
                    moveHelper(BACK_SUB, 2, 8);
                }
                break;
        }
    }
}
