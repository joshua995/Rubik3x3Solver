/*
Joshua Liu
Rubik"s Solver
*/

import java.util.HashMap;
import java.util.Random;

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

    static final byte[] M_MAIN = { 52, 49, 46, 25, 22, 19, 7, 4, 1, 37, 40, 43 };
    static final byte[] E_MAIN = { 41, 40, 39, 32, 31, 30, 23, 22, 21, 14, 13, 12 };
    static final byte[] S_MAIN = { 5, 4, 3, 10, 13, 16, 48, 49, 50, 34, 31, 28 };

    static final String[] ALL_MOVES = { "R1", "R2", "R3", "L1", "L2", "L3", "U1", "U2", "U3", "D1", "D2", "D3",
            "F1", "F2", "F3", "B1", "B2", "B3", "E1", "E2", "E3", "M1", "M2", "M3" };

    static HashMap<Integer, String> colourMap = new HashMap<Integer, String>();
    static HashMap<String, String> moveMap = new HashMap<String, String>();

    static byte[] cube = new byte[54];

    static String movesMade = "";

    public static void main(String[] args) throws Exception {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        initCube(cube);
        cube = scrambleCube(deepCopyCube(cube), 5);
        displayCube(cube);
        System.out.println(movesMade);
        movesMade = "";
        cube = solveCube(deepCopyCube(cube));
        displayCube(cube);
        System.out.println(movesMade);
    }

    public static void initCube(byte[] cube) {
        colourMap.put(1, "W");
        colourMap.put(2, "Y");
        colourMap.put(3, "R");
        colourMap.put(4, "O");
        colourMap.put(5, "B");
        colourMap.put(6, "G");
        moveMap.put("R1", "R");
        moveMap.put("R2", "R2");
        moveMap.put("R3", "R'");
        moveMap.put("L1", "L");
        moveMap.put("L2", "L2");
        moveMap.put("L3", "L'");
        moveMap.put("U1", "U");
        moveMap.put("U2", "U2");
        moveMap.put("U3", "U'");
        moveMap.put("D1", "D");
        moveMap.put("D2", "D2");
        moveMap.put("D3", "D'");
        moveMap.put("F1", "F");
        moveMap.put("F2", "F2");
        moveMap.put("F3", "F'");
        moveMap.put("B1", "B");
        moveMap.put("B2", "B2");
        moveMap.put("B3", "B'");
        moveMap.put("E1", "E");
        moveMap.put("E2", "E2");
        moveMap.put("E3", "E'");
        moveMap.put("M1", "M");
        moveMap.put("M2", "M2");
        moveMap.put("M3", "M'");
        moveMap.put("S1", "S");
        moveMap.put("S2", "S2");
        moveMap.put("S3", "S'");
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

    public static byte[] deepCopyCube(byte[] cube) {
        byte[] deepCopyCube = new byte[54];
        for (int i = 0; i < cube.length; i++) {
            deepCopyCube[i] = cube[i];
        }
        return deepCopyCube;
    }

    public static byte[] getFace(byte[] cube, byte[] faceI) {
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
        System.out.println();
    }

    public static byte[] moveHelper(byte[] cube, byte[] SIDE, int start, int end) {
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
        return cube;
    }

    public static byte[] makeMove(byte[] cube, String move) {
        String[] splitMove = { Character.toString(move.charAt(0)), Character.toString(move.charAt(1)) };
        int amt = Integer.parseInt(splitMove[1]);
        movesMade += moveMap.get(move) + " ";
        switch (splitMove[0]) {
            case "R":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, RIGHT_MAIN, 3, 12);
                    cube = moveHelper(cube, RIGHT_SUB, 2, 8);
                }
                break;
            case "L":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, LEFT_MAIN, 3, 12);
                    cube = moveHelper(cube, LEFT_SUB, 2, 8);
                }
                break;
            case "U":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, UP_MAIN, 3, 12);
                    cube = moveHelper(cube, UP_SUB, 2, 8);
                }
                break;
            case "D":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, DOWN_MAIN, 3, 12);
                    cube = moveHelper(cube, DOWN_SUB, 2, 8);
                }
                break;
            case "F":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, FRONT_MAIN, 3, 12);
                    cube = moveHelper(cube, FRONT_SUB, 2, 8);
                }
                break;
            case "B":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, BACK_MAIN, 3, 12);
                    cube = moveHelper(cube, BACK_SUB, 2, 8);
                }
                break;
            case "E":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, E_MAIN, 3, 12);
                }
                break;
            case "M":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, M_MAIN, 3, 12);
                }
                break;
            case "S":
                for (int i = 0; i < amt; i++) {
                    cube = moveHelper(cube, S_MAIN, 3, 12);
                }
                break;
        }
        return cube;
    }

    public static byte[] makeMoves(byte[] cube, String moves) {
        for (String move : moves.split(" "))
            cube = makeMove(cube, move);
        return cube;
    }

    public static byte[] scrambleCube(byte[] cube, int amt) {
        for (int i = 0; i < amt; i++) {
            cube = makeMove(cube, ALL_MOVES[new Random().nextInt(ALL_MOVES.length)]);
        }
        return cube;
    }

    public static byte[] solveCube(byte[] cube) {
        cube = solveBlueCenter(cube);
        cube = solveBlueWhiteEdge(cube);
        return cube;
    }

    public static byte[] solveBlueCenter(byte[] cube) {
        if (cube[13] == BLUE)
            return cube;
        else if (cube[22] == BLUE)
            return makeMove(cube, "E3");
        else if (cube[4] == BLUE)
            return makeMove(cube, "S3");
        else if (cube[31] == BLUE)
            return makeMove(cube, "E2");
        else if (cube[40] == BLUE)
            return makeMove(cube, "E1");
        else // (cube[49] == BLUE)
            return makeMove(cube, "S1");
    }

    public static byte[] solveBlueWhiteEdge(byte[] cube) {
        if (cube[16] == BLUE && cube[48] == WHITE)
            return cube;
        else if (cube[14] == BLUE && cube[21] == WHITE)
            return makeMove(cube, "L1");
        else if (cube[14] == WHITE && cube[21] == BLUE)
            return makeMoves(cube, "F3 D3");
            
        else if (cube[14] == BLUE && cube[21] == WHITE)
            return makeMove(cube, "L1");
        else if (cube[14] == WHITE && cube[21] == BLUE)
            return makeMoves(cube, "F3 D3");
        return cube;
    }
}
