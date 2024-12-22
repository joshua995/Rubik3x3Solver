package rubik;

import java.util.HashMap;
import java.util.Map;

interface TextColours {
    static final String TRED = "\u001B[31m", TGREEN = "\u001B[32m", TYELLOW = "\u001B[33m";
    static final String TBLUE = "\u001B[34m", TWHITE = "\u001B[37m", TORANGE = "\033[38;2;255;125;0m";
    static final Map<String, String> TEXT_COLOUR = Map.of(
            "Y", TYELLOW, "G", TGREEN, "R", TRED, "B", TBLUE, "W", TWHITE, "O", TORANGE);
}

public class Cube2x2 implements TextColours {

    static final int[] YELLOW = { 0, 3 }, BLUE = { 4, 7 }, RED = { 8, 11 };
    static final int[] GREEN = { 12, 15 }, ORANGE = { 16, 19 }, WHITE = { 20, 23 };

    static HashMap<Integer, String> colourMap = new HashMap<Integer, String>();

    static int[] cube = new int[24];

    public static void main(String[] args) {
        cube = initCube(deepCopy(cube));
        display(cube);
        // int y = 0;
        // int b = 1;
        // int g = 2;
        // int o = 3;
        // int r = 4;
        // int w = 5;
        // System.out.println(Integer.parseInt(String.format("%3s", Integer.toBinaryString(w)).replace(" ", "0"), 2));
    }

    public static int[] deepCopy(int[] cube) {
        int[] copy = new int[24];
        for (int i = 0; i < cube.length; i++) {
            copy[i] = cube[i];
        }
        return copy;
    }

    public static void initColourMapHelper(int[] COLOURI, String colour) {
        for (int i = COLOURI[0]; i <= COLOURI[1]; i++) {
            colourMap.put(i, colour);
        }
    }

    public static void initColourMap() {
        initColourMapHelper(WHITE, "W");
        initColourMapHelper(YELLOW, "Y");
        initColourMapHelper(BLUE, "B");
        initColourMapHelper(RED, "R");
        initColourMapHelper(ORANGE, "O");
        initColourMapHelper(GREEN, "G");
    }

    public static int[] initCube(int[] cube) {
        for (int i = 0; i < 24; i++) {
            cube[i] = i;
        }
        initColourMap();
        return cube;
    }

    public static int[] getFace(int[] cube, int[] faceI) {
        int[] face = new int[4];
        int sI = 0;
        for (int i = faceI[0]; i <= faceI[1]; i++, sI++) {
            face[sI] = cube[i];
        }
        return face;
    }

    public static void printMapToColour(int[] array, int start, int end) {
        for (int i = start; i < end; i++) {
            System.out
                    .print("\033[4m" + TEXT_COLOUR.get(colourMap.get((int) array[i])) + colourMap.get((int) array[i])
                            + TWHITE + "|\033[0m");
        }
    }

    public static void display(int[] cube) {
        int[] yellow = getFace(cube, YELLOW), blue = getFace(cube, BLUE), red = getFace(cube, RED);
        int[] green = getFace(cube, GREEN), orange = getFace(cube, ORANGE), white = getFace(cube, WHITE);
        System.out.printf("%4s|", " ");
        printMapToColour(yellow, 0, 2);
        System.out.printf("\n" + "\033[4m%4s|\033[0m", " ");
        printMapToColour(yellow, 2, 4);
        System.out.printf("\033[4m%8s\033[0m\n|", "_");
        printMapToColour(blue, 0, 2);
        printMapToColour(red, 0, 2);
        printMapToColour(green, 0, 2);
        printMapToColour(orange, 0, 2);
        System.out.print("\n|");
        printMapToColour(blue, 2, 4);
        printMapToColour(red, 2, 4);
        printMapToColour(green, 2, 4);
        printMapToColour(orange, 2, 4);
        System.out.printf("\n" + "%4s|", " ");
        printMapToColour(white, 0, 2);
        System.out.printf("\n" + "%4s|", " ");
        printMapToColour(white, 2, 4);
        System.out.println();
    }
}
