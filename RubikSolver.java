/*
Joshua Liu
Rubik's Solver
*/

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

    static byte[] cube = new byte[54];

    public static void main(String[] args) {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        initCube(cube);
        makeMove(cube, "U", (byte) 1);
        displayCube(cube);
    }

    public static void initCube(byte[] cube) {
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

    public static byte[] getFace(byte[] cube, byte[] faceI) {
        byte[] face = new byte[9];
        byte sI = 0;
        for (byte i = faceI[0]; i <= faceI[1]; i++, sI++) {
            face[sI] = cube[i];
        }
        return face;
    }

    public static void printMapToColour(byte[] array, byte start, byte end) {
        for (byte i = start; i < end; i++) {
            System.out.print(array[i]);
        }
    }

    public static void displayCube(byte[] cube) {
        byte[] yellow = getFace(cube, YELLOW_FACE);
        byte[] blue = getFace(cube, BLUE_FACE);
        byte[] red = getFace(cube, RED_FACE);
        byte[] green = getFace(cube, GREEN_FACE);
        byte[] orange = getFace(cube, ORANGE_FACE);
        byte[] white = getFace(cube, WHITE_FACE);
        System.out.printf("%3s", " ");
        printMapToColour(yellow, (byte) 0, (byte) 3);
        System.out.printf("\n%3s", " ");
        printMapToColour(yellow, (byte) 3, (byte) 6);
        System.out.printf("\n%3s", " ");
        printMapToColour(yellow, (byte) 6, (byte) 9);
        System.out.println();
        printMapToColour(blue, (byte) 0, (byte) 3);
        printMapToColour(red, (byte) 0, (byte) 3);
        printMapToColour(green, (byte) 0, (byte) 3);
        printMapToColour(orange, (byte) 0, (byte) 3);
        System.out.println();
        printMapToColour(blue, (byte) 3, (byte) 6);
        printMapToColour(red, (byte) 3, (byte) 6);
        printMapToColour(green, (byte) 3, (byte) 6);
        printMapToColour(orange, (byte) 3, (byte) 6);
        System.out.println();
        printMapToColour(blue, (byte) 6, (byte) 9);
        printMapToColour(red, (byte) 6, (byte) 9);
        printMapToColour(green, (byte) 6, (byte) 9);
        printMapToColour(orange, (byte) 6, (byte) 9);
        System.out.printf("\n%3s", " ");
        printMapToColour(white, (byte) 0, (byte) 3);
        System.out.printf("\n%3s", " ");
        printMapToColour(white, (byte) 3, (byte) 6);
        System.out.printf("\n%3s", " ");
        printMapToColour(white, (byte) 6, (byte) 9);
    }

    public static void moveHelper(byte[] cube, byte[] SIDE, byte end) {
        byte[] tempSide = new byte[end];
        byte tempSideI = 0;
        for (byte i : SIDE) {
            tempSide[tempSideI] = cube[i];
            tempSideI++;
        }
        tempSideI = 0;
        for (byte i = 3; i < end; i++, tempSideI++) {
            cube[SIDE[tempSideI]] = tempSide[i];
        }
        for (byte i = 0; i < 3; i++, tempSideI++) {
            cube[SIDE[tempSideI]] = tempSide[i];
        }
    }

    public static void makeMove(byte[] cube, String move, byte amt) {
        switch (move) {
            case "R":
                for (byte i = 0; i < amt; i++) {
                    moveHelper(cube, RIGHT_MAIN, (byte) 12);
                    moveHelper(cube, RIGHT_SUB, (byte) 8);
                }
                break;
            case "L":
                for (byte i = 0; i < amt; i++) {
                    moveHelper(cube, LEFT_MAIN, (byte) 12);
                    moveHelper(cube, LEFT_SUB, (byte) 8);
                }
                break;
            case "U":
                for (byte i = 0; i < amt; i++) {
                    moveHelper(cube, UP_MAIN, (byte) 12);
                    moveHelper(cube, UP_SUB, (byte) 8);
                }
                break;
            case "D":
                for (byte i = 0; i < amt; i++) {

                }
                break;
            case "F":
                for (byte i = 0; i < amt; i++) {

                }
                break;
            case "B":
                for (byte i = 0; i < amt; i++) {

                }
                break;
        }
    }
}
