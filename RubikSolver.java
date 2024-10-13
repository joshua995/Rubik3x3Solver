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

    static final int[] YELLOW_SIDE = { 0, 8 };
    static final int[] BLUE_SIDE = { 9, 17 };
    static final int[] RED_SIDE = { 18, 26 };
    static final int[] GREEN_SIDE = { 27, 35 };
    static final int[] ORANGE_SIDE = { 36, 44 };
    static final int[] WHITE_SIDE = { 45, 53 };

    static byte[] cube = new byte[54];

    public static void main(String[] args) {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        initCube();
        R();
        displayCube();
    }

    public static void initCube() {
        for (int i = 0; i < 9; i++) {
            cube[i] = YELLOW;
        }
        for (int i = 9; i < 18; i++) {
            cube[i] = BLUE;
        }
        for (int i = 18; i < 27; i++) {
            cube[i] = RED;
        }
        for (int i = 27; i < 36; i++) {
            cube[i] = GREEN;
        }
        for (int i = 36; i < 45; i++) {
            cube[i] = ORANGE;
        }
        for (int i = 45; i < 54; i++) {
            cube[i] = WHITE;
        }
    }

    public static byte[] getSide(byte[] cube, int[] sideI) {
        byte[] side = new byte[9];
        int sI = 0;
        for (int i = sideI[0]; i <= sideI[1]; i++, sI++) {
            side[sI] = cube[i];
        }
        return side;
    }

    public static void printMapToColour(byte[] array, int start, int end) {
        for (int i = start; i < end; i++) {
            System.out.print(array[i]);
        }
    }

    public static void displayCube() {
        byte[] yellow = getSide(cube, YELLOW_SIDE);
        byte[] blue = getSide(cube, BLUE_SIDE);
        byte[] red = getSide(cube, RED_SIDE);
        byte[] green = getSide(cube, GREEN_SIDE);
        byte[] orange = getSide(cube, ORANGE_SIDE);
        byte[] white = getSide(cube, WHITE_SIDE);
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

    public static void R() {

    }
}
