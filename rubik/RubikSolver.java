package rubik;
/*
Joshua Liu
Rubik"s Solver (ROUX)
*/

import java.util.HashMap;
import java.util.Random;

interface EdgePairings {// First letter of the colour followed by indices from map
    public int[] yo1x37 = { 1, 37 }, yg5x28 = { 5, 28 }, yr7x19 = { 7, 19 }, yb3x10 = { 3, 10 };
    public int[] bo12x41 = { 12, 41 }, br14x21 = { 14, 21 }, bw16x48 = { 16, 48 }, rg23x30 = { 23, 30 };
    public int[] rw25x46 = { 25, 46 }, go32x39 = { 32, 39 }, gw34x50 = { 34, 50 }, ow43x52 = { 43, 52 };
}

interface CornerTrios {// First letter of the colour followed by indices from map
    public int[] ybo0x9x38 = { 0, 9, 38 }, ygo2x29x36 = { 2, 29, 36 }, ybr6x11x18 = { 6, 11, 18 };
    public int[] yrg8x20x27 = { 8, 20, 27 }, bow15x44x51 = { 15, 44, 51 }, brw17x24x45 = { 17, 24, 45 };
    public int[] rgw26x33x47 = { 26, 33, 47 }, gow35x42x53 = { 35, 42, 53 };
}

interface MoveHelpers {
    static final int[] RIGHT_MAIN = { 2, 5, 8, 20, 23, 26, 47, 50, 53, 42, 39, 36 };
    static final int[] RIGHT_SUB = { 27, 30, 33, 34, 35, 32, 29, 28 };

    static final int[] LEFT_MAIN = { 6, 3, 0, 38, 41, 44, 51, 48, 45, 24, 21, 18 };
    static final int[] LEFT_SUB = { 17, 14, 11, 10, 9, 12, 15, 16 };

    static final int[] UP_MAIN = { 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38 };
    static final int[] UP_SUB = { 0, 3, 6, 7, 8, 5, 2, 1 };

    static final int[] DOWN_MAIN = { 44, 43, 42, 35, 34, 33, 26, 25, 24, 17, 16, 15 };
    static final int[] DOWN_SUB = { 47, 46, 45, 48, 51, 52, 53, 50 };

    static final int[] FRONT_MAIN = { 8, 7, 6, 11, 14, 17, 45, 46, 47, 33, 30, 27 };
    static final int[] FRONT_SUB = { 18, 21, 24, 25, 26, 23, 20, 19 };

    static final int[] BACK_MAIN = { 0, 1, 2, 29, 32, 35, 53, 52, 51, 15, 12, 9 };
    static final int[] BACK_SUB = { 38, 37, 36, 39, 42, 43, 44, 41 };

    static final int[] M_MAIN = { 52, 49, 46, 25, 22, 19, 7, 4, 1, 37, 40, 43 };
    static final int[] E_MAIN = { 41, 40, 39, 32, 31, 30, 23, 22, 21, 14, 13, 12 };
    static final int[] S_MAIN = { 5, 4, 3, 10, 13, 16, 48, 49, 50, 34, 31, 28 };
}

public class RubikSolver implements EdgePairings, CornerTrios, MoveHelpers {
    static final int[] YELLOW = { 0, 8 }, BLUE = { 9, 17 }, RED = { 18, 26 };
    static final int[] GREEN = { 27, 35 }, ORANGE = { 36, 44 }, WHITE = { 45, 53 };

    static final String[] ALL_MOVES = { "R", "R2", "R'", "L", "L2", "L'", "U", "U2", "U'", "D", "D2", "D'", "F", "F2",
            "F'", "B", "B2", "B'", "E", "E2", "E'", "M", "M2", "M'" };

    static HashMap<Integer, String> colourMap = new HashMap<Integer, String>();
    static HashMap<String, String> moveMap = new HashMap<String, String>();

    static int[] cube = new int[54];

    static String movesMade = "";

    public static void main(String[] args) throws Exception {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        cube = initCube(deepCopy(cube));
        cube = scrambleCube(deepCopy(cube), 5);
        // cube = makeMoves(cube, "R' R2 F E' U2 ");
        displayCube(cube);
        System.out.println(movesMade);
        movesMade = "";
        cube = solveCube(deepCopy(cube));
        displayCube(cube);
        System.out.println(movesMade);
    }

    public static int[] initCubeHelper(int[] cube, int COLOUR, int start, int end) {
        for (int i = start; i < end; i++) {
            cube[i] = COLOUR;
        }
        return cube;
    }

    public static int[] initCube(int[] cube) {
        for (int i = WHITE[0]; i <= WHITE[1]; i++) {
            colourMap.put(i, "W");
        }
        for (int i = YELLOW[0]; i <= YELLOW[1]; i++) {
            colourMap.put(i, "Y");
        }
        for (int i = RED[0]; i <= RED[1]; i++) {
            colourMap.put(i, "R");
        }
        for (int i = ORANGE[0]; i <= ORANGE[1]; i++) {
            colourMap.put(i, "O");
        }
        for (int i = BLUE[0]; i <= BLUE[1]; i++) {
            colourMap.put(i, "B");
        }
        for (int i = GREEN[0]; i <= GREEN[1]; i++) {
            colourMap.put(i, "G");
        }
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
        String[] keys = new String[moveMap.size()];
        int sI = 0;
        for (String s : moveMap.keySet()) {
            keys[sI] = s;
            sI++;
        }
        for (String s : keys) {
            moveMap.put(moveMap.get(s), s);
        }
        for (int i = 0; i < 54; i++) {
            cube[i] = i;
        }
        return cube;
    }

    public static int[] deepCopy(int[] cube) {
        int[] deepCopy = new int[54];
        for (int i = 0; i < cube.length; i++) {
            deepCopy[i] = cube[i];
        }
        return deepCopy;
    }

    public static int[] getFace(int[] cube, int[] faceI) {
        int[] face = new int[9];
        int sI = 0;
        for (int i = faceI[0]; i <= faceI[1]; i++, sI++) {
            face[sI] = cube[i];
        }
        return face;
    }

    public static void printMapToColour(int[] array, int start, int end) {
        for (int i = start; i < end; i++) {
            System.out.print(colourMap.get((int) array[i]));
        }
    }

    public static void displayCube(int[] cube) {
        int[] yellow = getFace(cube, YELLOW);
        int[] blue = getFace(cube, BLUE);
        int[] red = getFace(cube, RED);
        int[] green = getFace(cube, GREEN);
        int[] orange = getFace(cube, ORANGE);
        int[] white = getFace(cube, WHITE);
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

    public static int[] moveHelper(int[] cube, int[] SIDE, int start, int end) {
        int[] tempSide = new int[end];
        int tempSideI = 0;
        for (int i : SIDE) {
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

    public static int[] makeMove(int[] cube, String move) {
        move = moveMap.get(move);
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

    public static int[] makeMoves(int[] cube, String moves) {
        for (String move : moves.split(" "))
            cube = makeMove(cube, move);
        return cube;
    }

    public static int[] scrambleCube(int[] cube, int amt) {
        for (int i = 0; i < amt; i++) {
            cube = makeMove(cube, ALL_MOVES[new Random().nextInt(ALL_MOVES.length)]);
        }
        return cube;
    }

    public static int[] solveCube(int[] cube) {
        cube = solveBlueCenter(cube);
        cube = solveBlueWhiteEdge(cube);
        cube = loadBlueRedEdge(cube);
        cube = solveBlueRedEdge(cube);
        cube = loadBlueOrangeEdge(cube);
        cube = solveBlueOrangeEdge(cube);
        cube = solveGreenWhiteEdge(cube);
        cube = loadGreenRedEdge(cube);
        cube = solveGreenRedEdge(cube);
        cube = loadGreenOrangeEdge(cube);
        cube = solveGreenOrangeEdge(cube);
        // cube = solveYellowCorners(cube);
        // cube = orientColouredCorners(cube);
        // cube = solveBadEdges(cube);
        // cube = solveBlueGreenSide(cube);
        // cube = solveRestOfCube(cube);
        return cube;
    }

    public static int[] solveBlueCenter(int[] cube) {
        if (cube[13] == 13)
            return cube;
        else if (cube[22] == 13)
            return makeMoves(cube, "E'");
        else if (cube[4] == 13)
            return makeMoves(cube, "S'");
        else if (cube[31] == 13)
            return makeMoves(cube, "E2");
        else if (cube[40] == 13)
            return makeMoves(cube, "E");
        else // (cube[49] == 13)
            return makeMoves(cube, "S");
    }

    public static int[] solveBlueWhiteEdge(int[] cube) {
        if (cube[bw16x48[0]] == bw16x48[0])
            return cube;
        else if (cube[br14x21[0]] == bw16x48[0])
            return makeMoves(cube, "L");
        else if (cube[br14x21[0]] == bw16x48[1])
            return makeMoves(cube, "F' D'");
        else if (cube[bo12x41[0]] == bw16x48[0])
            return makeMoves(cube, "L'");
        else if (cube[bo12x41[0]] == bw16x48[1])
            return makeMoves(cube, "B D");
        else if (cube[yb3x10[0]] == bw16x48[0])
            return makeMoves(cube, "L F' D'");
        else if (cube[yb3x10[0]] == bw16x48[1])
            return makeMoves(cube, "L2");
        else if (cube[yo1x37[0]] == bw16x48[0])
            return makeMoves(cube, "B L'");
        else if (cube[yo1x37[0]] == bw16x48[1])
            return makeMoves(cube, "U' L2");
        else if (cube[yg5x28[0]] == bw16x48[0])
            return makeMoves(cube, "U F' L");
        else if (cube[yg5x28[0]] == bw16x48[1])
            return makeMoves(cube, "U2 L2");
        else if (cube[yr7x19[0]] == bw16x48[0])
            return makeMoves(cube, "F' L");
        else if (cube[yr7x19[0]] == bw16x48[1])
            return makeMoves(cube, "U L2");
        else if (cube[rg23x30[0]] == bw16x48[0])
            return makeMoves(cube, "F D'");
        else if (cube[rg23x30[0]] == bw16x48[1])
            return makeMoves(cube, "F2 L");
        else if (cube[rw25x46[0]] == bw16x48[0])
            return makeMoves(cube, "D'");
        else if (cube[rw25x46[0]] == bw16x48[1])
            return makeMoves(cube, "F L");
        else if (cube[go32x39[0]] == bw16x48[0])
            return makeMoves(cube, "B2 L'");
        else if (cube[go32x39[0]] == bw16x48[1])
            return makeMoves(cube, "B' D");
        else if (cube[gw34x50[0]] == bw16x48[0])
            return makeMoves(cube, "D2");
        else if (cube[gw34x50[0]] == bw16x48[1])
            return makeMoves(cube, "R F D'");
        else if (cube[ow43x52[0]] == bw16x48[0])
            return makeMoves(cube, "D");
        else if (cube[ow43x52[0]] == bw16x48[1])
            return makeMoves(cube, "B' L'");
        else // cube[bw16x48[0]] == bw16x48[1]
            return makeMoves(cube, "D F L");
    }

    public static int[] loadBlueRedEdge(int[] cube) {
        if ((cube[br14x21[0]] == br14x21[0] && cube[brw17x24x45[0]] == brw17x24x45[0])
                || (cube[rw25x46[0]] == br14x21[0]) || (cube[rw25x46[0]] == br14x21[1]))
            return cube;
        else if (cube[yo1x37[0]] == br14x21[0] || cube[yo1x37[0]] == br14x21[1])
            return makeMoves(cube, "M2");
        else if (cube[yg5x28[0]] == br14x21[0] || cube[yg5x28[0]] == br14x21[1])
            return makeMoves(cube, "U M");
        else if (cube[yr7x19[0]] == br14x21[0] || cube[yr7x19[0]] == br14x21[1])
            return makeMoves(cube, "M");
        else if (cube[yb3x10[0]] == br14x21[0] || cube[yb3x10[0]] == br14x21[1])
            return makeMoves(cube, "U' M");
        else if (cube[bo12x41[0]] == br14x21[0] || cube[bo12x41[0]] == br14x21[1])
            return makeMoves(cube, "B M'");
        else if (cube[br14x21[0]] == br14x21[0] || cube[br14x21[0]] == br14x21[1])
            return makeMoves(cube, "F'");
        else if (cube[rg23x30[0]] == br14x21[0] || cube[rg23x30[0]] == br14x21[1])
            return makeMoves(cube, "F");
        else if (cube[go32x39[0]] == br14x21[0] || cube[go32x39[0]] == br14x21[1])
            return makeMoves(cube, "R2 F");
        else if (cube[gw34x50[0]] == br14x21[0] || cube[gw34x50[0]] == br14x21[1])
            return makeMoves(cube, "R F");
        else // (cube[ow43x52[0]] == br14x21[0] || cube[ow43x52[0]] == br14x21[1])
            return makeMoves(cube, "M'");
    }

    public static int[] solveBlueRedEdge(int[] cube) {
        if (cube[br14x21[0]] == br14x21[0] && cube[brw17x24x45[0]] == brw17x24x45[0])
            return cube;
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ybo0x9x38[0]] == brw17x24x45[0])
            return makeMoves(cube, "U' M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ybo0x9x38[1]] == brw17x24x45[0])
            return makeMoves(cube, "U R' U M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ybo0x9x38[2]] == brw17x24x45[0])
            return makeMoves(cube, "M2 U2 R' M F");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ygo2x29x36[0]] == brw17x24x45[0])
            return makeMoves(cube, "U2 M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ygo2x29x36[1]] == brw17x24x45[0])
            return makeMoves(cube, "U' M2 U2 R' M F");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ygo2x29x36[2]] == brw17x24x45[0])
            return makeMoves(cube, "R' U M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[yrg8x20x27[0]] == brw17x24x45[0])
            return makeMoves(cube, "U M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[yrg8x20x27[1]] == brw17x24x45[0])
            return makeMoves(cube, "R U2 M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[yrg8x20x27[2]] == brw17x24x45[0])
            return makeMoves(cube, "U' R' U M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ybr6x11x18[0]] == brw17x24x45[0])
            return makeMoves(cube, "M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ybr6x11x18[1]] == brw17x24x45[0])
            return makeMoves(cube, "U M2 U2 R' M F");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[ybr6x11x18[2]] == brw17x24x45[0])
            return makeMoves(cube, "U B' U2 M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[bow15x44x51[0]] == brw17x24x45[0])
            return makeMoves(cube, "B' U' M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[bow15x44x51[1]] == brw17x24x45[0])
            return makeMoves(cube, "B' M2 U2 R' M F");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[bow15x44x51[2]] == brw17x24x45[0])
            return makeMoves(cube, "M B R2 F2");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[brw17x24x45[0]] == brw17x24x45[0])
            return makeMoves(cube, "M F M2 F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[brw17x24x45[1]] == brw17x24x45[0])
            return makeMoves(cube, "F' R U F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[brw17x24x45[2]] == brw17x24x45[0])
            return makeMoves(cube, "M F2 U M2 F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[rgw26x33x47[0]] == brw17x24x45[0])
            return makeMoves(cube, "R U M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[rgw26x33x47[1]] == brw17x24x45[0])
            return makeMoves(cube, "R2 U' M2 U2 R' M F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[rgw26x33x47[2]] == brw17x24x45[0])
            return makeMoves(cube, "R2 U2 M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[gow35x42x53[0]] == brw17x24x45[0])
            return makeMoves(cube, "B U2 M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[gow35x42x53[1]] == brw17x24x45[0])
            return makeMoves(cube, "R' U2 M' F'");
        else if (cube[rw25x46[0]] == br14x21[0] && cube[gow35x42x53[2]] == brw17x24x45[0])
            return makeMoves(cube, "R2 U M' F'");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ybo0x9x38[0]] == brw17x24x45[0])
            return makeMoves(cube, "U M2 U2 F'");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ybo0x9x38[1]] == brw17x24x45[0])
            return makeMoves(cube, "B' M2 U2 F'");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ybo0x9x38[2]] == brw17x24x45[0])
            return makeMoves(cube, "U2 R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ygo2x29x36[0]] == brw17x24x45[0])
            return makeMoves(cube, "R2 F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ygo2x29x36[1]] == brw17x24x45[0])
            return makeMoves(cube, "U R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ygo2x29x36[2]] == brw17x24x45[0])
            return makeMoves(cube, "R' U' R2 F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[yrg8x20x27[0]] == brw17x24x45[0])
            return makeMoves(cube, "U' R2 F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[yrg8x20x27[1]] == brw17x24x45[0])
            return makeMoves(cube, "R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[yrg8x20x27[2]] == brw17x24x45[0])
            return makeMoves(cube, "R U R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ybr6x11x18[0]] == brw17x24x45[0])
            return makeMoves(cube, "U2 R2 F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ybr6x11x18[1]] == brw17x24x45[0])
            return makeMoves(cube, "U' R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[ybr6x11x18[2]] == brw17x24x45[0])
            return makeMoves(cube, "U' R U R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[bow15x44x51[0]] == brw17x24x45[0])
            return makeMoves(cube, "B' U R2 F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[bow15x44x51[1]] == brw17x24x45[0])
            return makeMoves(cube, "B' U2 R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[bow15x44x51[2]] == brw17x24x45[0])
            return makeMoves(cube, "B2 R2 F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[brw17x24x45[0]] == brw17x24x45[0])
            return makeMoves(cube, "L' U' M' U L");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[brw17x24x45[1]] == brw17x24x45[0])
            return makeMoves(cube, "L' U2 R' M' U L");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[brw17x24x45[2]] == brw17x24x45[0])
            return makeMoves(cube, "L' U L U' R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[rgw26x33x47[0]] == brw17x24x45[0])
            return makeMoves(cube, "R U' R2 F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[rgw26x33x47[1]] == brw17x24x45[0])
            return makeMoves(cube, "R2 U R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[rgw26x33x47[2]] == brw17x24x45[0])
            return makeMoves(cube, "F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[gow35x42x53[0]] == brw17x24x45[0])
            return makeMoves(cube, "R' U R' F");
        else if (cube[rw25x46[0]] == br14x21[1] && cube[gow35x42x53[1]] == brw17x24x45[0])
            return makeMoves(cube, "R F");
        else // (cube[rw25x46[0]] == br14x21[1] && cube[gow35x42x53[2]] == brw17x24x45[0])
            return makeMoves(cube, "B U R' F");
    }

    public static int[] loadBlueOrangeEdge(int[] cube) {
        if ((cube[bo12x41[0]] == bo12x41[0] && cube[bow15x44x51[0]] == bow15x44x51[0])
                || (cube[rw25x46[0]] == bo12x41[0]) || (cube[rw25x46[0]] == bo12x41[1]))
            return cube;
        else if (cube[yo1x37[0]] == bo12x41[0] || cube[yo1x37[0]] == bo12x41[1])
            return makeMoves(cube, "M2");
        else if (cube[yg5x28[0]] == bo12x41[0] || cube[yg5x28[0]] == bo12x41[1])
            return makeMoves(cube, "U M");
        else if (cube[yr7x19[0]] == bo12x41[0] || cube[yr7x19[0]] == bo12x41[1])
            return makeMoves(cube, "M");
        else if (cube[yb3x10[0]] == bo12x41[0] || cube[yb3x10[0]] == bo12x41[1])
            return makeMoves(cube, "U' M");
        else if (cube[bo12x41[0]] == bo12x41[0] || cube[bo12x41[0]] == bo12x41[1])
            return makeMoves(cube, "B M'");
        else if (cube[rg23x30[0]] == bo12x41[0] || cube[rg23x30[0]] == bo12x41[1])
            return makeMoves(cube, "L F L'");
        else if (cube[go32x39[0]] == bo12x41[0] || cube[go32x39[0]] == bo12x41[1])
            return makeMoves(cube, "R' U M");
        else if (cube[gw34x50[0]] == bo12x41[0] || cube[gw34x50[0]] == bo12x41[1])
            return makeMoves(cube, "L' D' L");
        else // (cube[ow43x52[0]] == bo12x41[0] || cube[ow43x52[0]] == bo12x41[1])
            return makeMoves(cube, "M'");
    }

    public static int[] solveBlueOrangeEdge(int[] cube) {
        if (cube[bo12x41[0]] == bo12x41[0] && cube[bow15x44x51[0]] == bow15x44x51[0])
            return cube;
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ybo0x9x38[0]] == bow15x44x51[0])
            return makeMoves(cube, "U2 M' U2 B");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ybo0x9x38[1]] == bow15x44x51[0])
            return makeMoves(cube, "U R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ybo0x9x38[2]] == bow15x44x51[0])
            return makeMoves(cube, "B' R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ygo2x29x36[0]] == bow15x44x51[0])
            return makeMoves(cube, "U R2 M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ygo2x29x36[1]] == bow15x44x51[0])
            return makeMoves(cube, "R' U' R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ygo2x29x36[2]] == bow15x44x51[0])
            return makeMoves(cube, "R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[yrg8x20x27[0]] == bow15x44x51[0])
            return makeMoves(cube, "R2 M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[yrg8x20x27[1]] == bow15x44x51[0])
            return makeMoves(cube, "R U R2 M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[yrg8x20x27[2]] == bow15x44x51[0])
            return makeMoves(cube, "U' R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ybr6x11x18[0]] == bow15x44x51[0])
            return makeMoves(cube, "U' R2 M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ybr6x11x18[1]] == bow15x44x51[0])
            return makeMoves(cube, "U B' R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[ybr6x11x18[2]] == bow15x44x51[0])
            return makeMoves(cube, "U2 R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[bow15x44x51[0]] == bow15x44x51[0])
            return makeMoves(cube, "B M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[bow15x44x51[1]] == bow15x44x51[0])
            return makeMoves(cube, "B2 R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[bow15x44x51[2]] == bow15x44x51[0])
            return makeMoves(cube, "B' U R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[rgw26x33x47[0]] == bow15x44x51[0])
            return makeMoves(cube, "R' M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[rgw26x33x47[1]] == bow15x44x51[0])
            return makeMoves(cube, "R U R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[rgw26x33x47[2]] == bow15x44x51[0])
            return makeMoves(cube, "R2 U R2 M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[gow35x42x53[0]] == bow15x44x51[0])
            return makeMoves(cube, "R2 U' R M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[gow35x42x53[1]] == bow15x44x51[0])
            return makeMoves(cube, "R' U R2 M B'");
        else if (cube[rw25x46[0]] == bo12x41[0] && cube[gow35x42x53[2]] == bow15x44x51[0])
            return makeMoves(cube, "M B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ybo0x9x38[0]] == bow15x44x51[0])
            return makeMoves(cube, "M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ybo0x9x38[1]] == bow15x44x51[0])
            return makeMoves(cube, "U' M' U2 R M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ybo0x9x38[2]] == bow15x44x51[0])
            return makeMoves(cube, "B' R M' U2 M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ygo2x29x36[0]] == bow15x44x51[0])
            return makeMoves(cube, "U' M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ygo2x29x36[1]] == bow15x44x51[0])
            return makeMoves(cube, "B M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ygo2x29x36[2]] == bow15x44x51[0])
            return makeMoves(cube, "R M' U2 M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[yrg8x20x27[0]] == bow15x44x51[0])
            return makeMoves(cube, "R2 M' U2 M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[yrg8x20x27[1]] == bow15x44x51[0])
            return makeMoves(cube, "R U' M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[yrg8x20x27[2]] == bow15x44x51[0])
            return makeMoves(cube, "U' R M' U2 M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ybr6x11x18[0]] == bow15x44x51[0])
            return makeMoves(cube, "U M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ybr6x11x18[1]] == bow15x44x51[0])
            return makeMoves(cube, "U' R U' M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[ybr6x11x18[2]] == bow15x44x51[0])
            return makeMoves(cube, "M' U2 R M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[bow15x44x51[0]] == bow15x44x51[0])
            return makeMoves(cube, "B M' U2 M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[bow15x44x51[1]] == bow15x44x51[0])
            return makeMoves(cube, "M B2 R M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[bow15x44x51[2]] == bow15x44x51[0])
            return makeMoves(cube, "B' U R M' U2 M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[rgw26x33x47[0]] == bow15x44x51[0])
            return makeMoves(cube, "R' M' U2 M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[rgw26x33x47[1]] == bow15x44x51[0])
            return makeMoves(cube, "R U M' U2 R M' B'");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[rgw26x33x47[2]] == bow15x44x51[0])
            return makeMoves(cube, "R2 U' M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[gow35x42x53[0]] == bow15x44x51[0])
            return makeMoves(cube, "B U' M2 B");
        else if (cube[rw25x46[0]] == bo12x41[1] && cube[gow35x42x53[1]] == bow15x44x51[0])
            return makeMoves(cube, "R' U' M2 B");
        else // (cube[rw25x46[0]] == bo12x41[1] && cube[gow35x42x53[2]] == bow15x44x51[0])
            return makeMoves(cube, "M' U2 M' B'");
    }

    public static int[] solveGreenWhiteEdge(int[] cube) {
        if (cube[gw34x50[0]] == gw34x50[0])
            return cube;
        else if (cube[yo1x37[0]] == gw34x50[0])
            return makeMoves(cube, "M U' R2");
        else if (cube[yo1x37[0]] == gw34x50[1])
            return makeMoves(cube, "U R2");
        else if (cube[yg5x28[0]] == gw34x50[0])
            return makeMoves(cube, "U M' U R2");
        else if (cube[yg5x28[0]] == gw34x50[1])
            return makeMoves(cube, "R2");
        else if (cube[yr7x19[0]] == gw34x50[0])
            return makeMoves(cube, "M' U R2");
        else if (cube[yr7x19[0]] == gw34x50[1])
            return makeMoves(cube, "U' R2");
        else if (cube[yb3x10[0]] == gw34x50[0])
            return makeMoves(cube, "U M U' R2");
        else if (cube[yb3x10[0]] == gw34x50[1])
            return makeMoves(cube, "U2 R2");
        else if (cube[rg23x30[0]] == gw34x50[0])
            return makeMoves(cube, "R U M' U R2");
        else if (cube[rg23x30[0]] == gw34x50[1])
            return makeMoves(cube, "R'");
        else if (cube[rw25x46[0]] == gw34x50[0])
            return makeMoves(cube, "M2 U R2");
        else if (cube[rw25x46[0]] == gw34x50[1])
            return makeMoves(cube, "M' U' R2");
        else if (cube[go32x39[0]] == gw34x50[0])
            return makeMoves(cube, "R");
        else if (cube[go32x39[0]] == gw34x50[1])
            return makeMoves(cube, "R' U M' U R2");
        else if (cube[ow43x52[0]] == gw34x50[0])
            return makeMoves(cube, "M2 U' R2");
        else if (cube[ow43x52[0]] == gw34x50[1])
            return makeMoves(cube, "M U R2");
        else // (cube[gw34x50[0]] == gw34x50[1])
            return makeMoves(cube, "R2 U M' U R2");
    }

    public static int[] loadGreenRedEdge(int[] cube) {
        if ((cube[rg23x30[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[0])
                || (cube[rw25x46[0]] == rg23x30[0]) || (cube[rw25x46[0]] == rg23x30[1]))
            return cube;
        else if (cube[yo1x37[0]] == rg23x30[0] || cube[yo1x37[0]] == rg23x30[1])
            return makeMoves(cube, "M2");
        else if (cube[yg5x28[0]] == rg23x30[0] || cube[yg5x28[0]] == rg23x30[1])
            return makeMoves(cube, "U M");
        else if (cube[yr7x19[0]] == rg23x30[0] || cube[yr7x19[0]] == rg23x30[1])
            return makeMoves(cube, "M");
        else if (cube[yb3x10[0]] == rg23x30[0] || cube[yb3x10[0]] == rg23x30[1])
            return makeMoves(cube, "U' M");
        else if (cube[rg23x30[0]] == rg23x30[0] || cube[rg23x30[0]] == rg23x30[1])
            return makeMoves(cube, "R U R' M");
        else if (cube[go32x39[0]] == rg23x30[0] || cube[go32x39[0]] == rg23x30[1])
            return makeMoves(cube, "R' U R M");
        else // (cube[ow43x52[0]] == rg23x30[0] || cube[ow43x52[0]] == rg23x30[1])
            return makeMoves(cube, "M'");
    }

    public static int[] solveGreenRedEdge(int[] cube) {
        if (cube[rg23x30[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[0])
            return cube;
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ybo0x9x38[0]] == rgw26x33x47[0])
            return makeMoves(cube, "U' R M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ybo0x9x38[0]] == rgw26x33x47[1])
            return makeMoves(cube, "M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ybo0x9x38[0]] == rgw26x33x47[2])
            return makeMoves(cube, "U R' U R2 M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ygo2x29x36[0]] == rgw26x33x47[0])
            return makeMoves(cube, "U2 R M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ygo2x29x36[0]] == rgw26x33x47[1])
            return makeMoves(cube, "U' M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ygo2x29x36[0]] == rgw26x33x47[2])
            return makeMoves(cube, "R' U R2 M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[yrg8x20x27[0]] == rgw26x33x47[0])
            return makeMoves(cube, "U R M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[yrg8x20x27[0]] == rgw26x33x47[1])
            return makeMoves(cube, "U2 M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[yrg8x20x27[0]] == rgw26x33x47[2])
            return makeMoves(cube, "R U' R' M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ybr6x11x18[0]] == rgw26x33x47[0])
            return makeMoves(cube, "R M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ybr6x11x18[0]] == rgw26x33x47[1])
            return makeMoves(cube, "U M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[ybr6x11x18[0]] == rgw26x33x47[2])
            return makeMoves(cube, "U' R U' R' M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[0])
            return makeMoves(cube, "R U M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[1])
            return makeMoves(cube, "R U2 R' M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[2])
            return makeMoves(cube, "R U' R' U R M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[gow35x42x53[0]] == rgw26x33x47[0])
            return makeMoves(cube, "R' U' R M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[gow35x42x53[0]] == rgw26x33x47[1])
            return makeMoves(cube, "R' U R U' M2 U2 R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[0] && cube[gow35x42x53[0]] == rgw26x33x47[2])
            return makeMoves(cube, "R' U2 R2 M' U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ybo0x9x38[0]] == rgw26x33x47[0])
            return makeMoves(cube, "U M2 U2 R U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ybo0x9x38[0]] == rgw26x33x47[1])
            return makeMoves(cube, "U2 R M2 U R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ybo0x9x38[0]] == rgw26x33x47[2])
            return makeMoves(cube, "U R' U M2 U2 R2 U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ygo2x29x36[0]] == rgw26x33x47[0])
            return makeMoves(cube, "M2 U2 R U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ygo2x29x36[0]] == rgw26x33x47[1])
            return makeMoves(cube, "U R M2 U R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ygo2x29x36[0]] == rgw26x33x47[2])
            return makeMoves(cube, "R' U' M2 U2 R2 U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[yrg8x20x27[0]] == rgw26x33x47[0])
            return makeMoves(cube, "U' M2 U2 R U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[yrg8x20x27[0]] == rgw26x33x47[1])
            return makeMoves(cube, "R M2 U R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[yrg8x20x27[0]] == rgw26x33x47[2])
            return makeMoves(cube, "U' R' U' M2 U2 R2 U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ybr6x11x18[0]] == rgw26x33x47[0])
            return makeMoves(cube, "U2 M2 U2 R U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ybr6x11x18[0]] == rgw26x33x47[1])
            return makeMoves(cube, "U' R M2 U R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[ybr6x11x18[0]] == rgw26x33x47[2])
            return makeMoves(cube, "U2 R' U' M2 U2 R2 U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[rgw26x33x47[0]] == rgw26x33x47[0])
            return makeMoves(cube, "R U' M2 U R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[rgw26x33x47[0]] == rgw26x33x47[1])
            return makeMoves(cube, "R M' U R' U' R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[rgw26x33x47[0]] == rgw26x33x47[2])
            return makeMoves(cube, "R U' R' U' M2 U2 R U' R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[gow35x42x53[0]] == rgw26x33x47[0])
            return makeMoves(cube, "R' U M' U R U' R M' U R'");
        else if (cube[rw25x46[0]] == rg23x30[1] && cube[gow35x42x53[0]] == rgw26x33x47[1])
            return makeMoves(cube, "R' U R U R M2 U R'");
        else // (cube[rw25x46[0]] == rg23x30[1] && cube[gow35x42x53[0]] == rgw26x33x47[2])
            return makeMoves(cube, "R' M2 U2 R2 U' R'");
    }

    public static int[] loadGreenOrangeEdge(int[] cube) {
        if ((cube[go32x39[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[0])
                || (cube[rw25x46[0]] == go32x39[0]) || (cube[rw25x46[0]] == go32x39[1]))
            return cube;
        else if (cube[yo1x37[0]] == go32x39[0] || cube[yo1x37[0]] == go32x39[1])
            return makeMoves(cube, "M2");
        else if (cube[yg5x28[0]] == go32x39[0] || cube[yg5x28[0]] == go32x39[1])
            return makeMoves(cube, "U M");
        else if (cube[yr7x19[0]] == go32x39[0] || cube[yr7x19[0]] == go32x39[1])
            return makeMoves(cube, "M");
        else if (cube[yb3x10[0]] == go32x39[0] || cube[yb3x10[0]] == go32x39[1])
            return makeMoves(cube, "U' M");
        else if (cube[go32x39[0]] == go32x39[0] || cube[go32x39[0]] == go32x39[1])
            return makeMoves(cube, "R' U R M");
        else // (cube[ow43x52[0]] == go32x39[0] || cube[ow43x52[0]] == go32x39[1])
            return makeMoves(cube, "M'");
    }

    public static int[] solveGreenOrangeEdge(int[] cube) {
        if (cube[go32x39[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[0])
            return cube;
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ybo0x9x38[0]] == gow35x42x53[0])
            return makeMoves(cube, "U' M' U2 R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ybo0x9x38[0]] == gow35x42x53[1])
            return makeMoves(cube, "M2 R' U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ybo0x9x38[0]] == gow35x42x53[2])
            return makeMoves(cube, "R' U2 R U' M2 R' U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ygo2x29x36[0]] == gow35x42x53[0])
            return makeMoves(cube, "U2 M' U2 R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ygo2x29x36[0]] == gow35x42x53[1])
            return makeMoves(cube, "U' M2 R' U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ygo2x29x36[0]] == gow35x42x53[2])
            return makeMoves(cube, "R' U R M' U2 R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[yrg8x20x27[0]] == gow35x42x53[0])
            return makeMoves(cube, "U M' U2 R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[yrg8x20x27[0]] == gow35x42x53[1])
            return makeMoves(cube, "U2 M2 R' U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[yrg8x20x27[0]] == gow35x42x53[2])
            return makeMoves(cube, "U' R' U R M' U2 R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ybr6x11x18[0]] == gow35x42x53[0])
            return makeMoves(cube, "M' U2 R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ybr6x11x18[0]] == gow35x42x53[1])
            return makeMoves(cube, "U M2 R' U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[ybr6x11x18[0]] == gow35x42x53[2])
            return makeMoves(cube, "R' U' R U M2 R' U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[0])
            return makeMoves(cube, "R' U' M2 U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[1])
            return makeMoves(cube, "R' U R U' M2 R' U R");
        else if (cube[rw25x46[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[2])
            return makeMoves(cube, "R' U2 R M' U2 R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ybo0x9x38[0]] == gow35x42x53[0])
            return makeMoves(cube, "U R' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ybo0x9x38[0]] == gow35x42x53[1])
            return makeMoves(cube, "R' U2 M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ybo0x9x38[0]] == gow35x42x53[2])
            return makeMoves(cube, "U R' U R U2 R' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ygo2x29x36[0]] == gow35x42x53[0])
            return makeMoves(cube, "R' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ygo2x29x36[0]] == gow35x42x53[1])
            return makeMoves(cube, "U M' U R' U2 R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ygo2x29x36[0]] == gow35x42x53[2])
            return makeMoves(cube, "R' U' M2 U' R U R' M U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[yrg8x20x27[0]] == gow35x42x53[0])
            return makeMoves(cube, "U' R' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[yrg8x20x27[0]] == gow35x42x53[1])
            return makeMoves(cube, "U R' U' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[yrg8x20x27[0]] == gow35x42x53[2])
            return makeMoves(cube, "U' R' U R U2 R' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ybr6x11x18[0]] == gow35x42x53[0])
            return makeMoves(cube, "U2 R' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ybr6x11x18[0]] == gow35x42x53[1])
            return makeMoves(cube, "R' U' M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[ybr6x11x18[0]] == gow35x42x53[2])
            return makeMoves(cube, "R' U' R U M' U R' U2 R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[gow35x42x53[0]] == gow35x42x53[0])
            return makeMoves(cube, "R' U M' U' R");
        else if (cube[rw25x46[0]] == go32x39[1] && cube[gow35x42x53[0]] == gow35x42x53[1])
            return makeMoves(cube, "R' U R U M' U R' U2 R");
        else // (cube[rw25x46[0]] == go32x39[1] && cube[gow35x42x53[0]] == gow35x42x53[2])
            return makeMoves(cube, "R' M2 U2 R U2 R' M U' R");
    }
}
