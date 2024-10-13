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

    static final int[] YELLOW_FACE = { 0, 8 };
    static final int[] BLUE_FACE = { 9, 17 };
    static final int[] RED_FACE = { 18, 26 };
    static final int[] GREEN_FACE = { 27, 35 };
    static final int[] ORANGE_FACE = { 36, 44 };
    static final int[] WHITE_FACE = { 45, 53 };

    static final int[] RIGHT_MAIN = { 2, 5, 8, 20, 23, 26, 47, 50, 53, 42, 39, 36 };
    static final int[] RIGHT_SUB = { 27, 30, 33, 34, 35, 32, 29, 28 };

    static final int[] LEFT_MAIN = { 6, 3, 0, 38, 41, 44, 51, 48, 45, 24, 21, 18 };
    static final int[] LEFT_SUB = { 17, 14, 11, 10, 9, 12, 15, 16 };

    static byte[] cube = new byte[54];

    public static void main(String[] args) {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        initCube(cube);
        L(cube);
        R(cube);
        displayCube(cube);
    }

    public static void initCube(byte[] cube) {
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

    public static byte[] getFace(byte[] cube, int[] faceI) {
        byte[] face = new byte[9];
        int sI = 0;
        for (int i = faceI[0]; i <= faceI[1]; i++, sI++) {
            face[sI] = cube[i];
        }
        return face;
    }

    public static void printMapToColour(byte[] array, int start, int end) {
        for (int i = start; i < end; i++) {
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

    public static void moveHelper(byte[] cube, int[] SIDE, int end) {
        byte[] tempSide = new byte[end];
        int tempSideI = 0;
        for (int i : SIDE) {
            tempSide[tempSideI] = cube[i];
            tempSideI++;
        }
        tempSideI = 0;
        for (int i = 3; i < end; i++, tempSideI++) {
            cube[SIDE[tempSideI]] = tempSide[i];
        }
        for (int i = 0; i < 3; i++, tempSideI++) {
            cube[SIDE[tempSideI]] = tempSide[i];
        }
    }

    public static void R(byte[] cube) {
        moveHelper(cube, RIGHT_MAIN, 12);
        moveHelper(cube, RIGHT_SUB, 8);
    }

    public static void R2(byte[] cube) {
        R(cube);
        R(cube);
    }

    public static void Ri(byte[] cube) {
        R2(cube);
        R(cube);
    }

    public static void L(byte[] cube) {
        moveHelper(cube, LEFT_MAIN, 12);
        moveHelper(cube, LEFT_SUB, 8);
    }
}
