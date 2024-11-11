package rubik;
/*
Joshua Liu
Rubik"s Solver (ROUX)
*/

import java.util.*;

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

interface MoveHelpers {// Indices of for making moves
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

interface TextColours {
    static final String TRED = "\u001B[31m", TGREEN = "\u001B[32m", TYELLOW = "\u001B[33m";
    static final String TBLUE = "\u001B[34m", TWHITE = "\u001B[37m", TORANGE = "\033[38;2;255;125;0m";
    static final Map<String, String> TEXT_COLOUR = Map.of(
            "Y", TYELLOW, "G", TGREEN, "R", TRED, "B", TBLUE, "W", TWHITE, "O", TORANGE);
}

public class RubikSolverRemake implements EdgePairings, CornerTrios, MoveHelpers, TextColours {
    static final int[] YELLOW = { 0, 8 }, BLUE = { 9, 17 }, RED = { 18, 26 };
    static final int[] GREEN = { 27, 35 }, ORANGE = { 36, 44 }, WHITE = { 45, 53 };

    static final String[] ALL_MOVES = { "R", "R2", "R'", "L", "L2", "L'", "U", "U2", "U'", "D", "D2", "D'", "F", "F2",
            "F'", "B", "B2", "B'", "E", "E2", "E'", "M", "M2", "M'" };

    static HashMap<Integer, String> colourMap = new HashMap<Integer, String>();
    static HashMap<String, String> moveMap = new HashMap<String, String>();
    static HashMap<String, int[]> parseMap = new HashMap<String, int[]>();

    static int[] cube = new int[54];

    static String movesMade = "";

    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        movesMade = "";
        initMoveMap();
        initColourMap();
        System.out.println(
                "Enter:\033[1m 1\033[0m for auto scramble and solve |\033[1m 2\033[0m to manually input colors");
        char input = scan.next().charAt(0);
        if (input == '1') {
            cube = scrambleCube(initCube(deepCopy(cube)), 10);
        } else if (input == '2')
            cube = getUserInput();
        displayCube(deepCopy(cube), false);
        System.out.println(movesMade);
        movesMade = "";
        cube = solveCube(deepCopy(cube));
        displayCube(deepCopy(cube), false);
        reduceMovesUsed();
        System.out.println(movesMade);
    }

    public static int[] getUserInput() {
        int[] inputArray = new int[54];
        for (int i = 0; i < inputArray.length; i++) {
            inputArray[i] = ' ';
        }
        inputArray[0] = '*';
        displayCube(inputArray, true);
        for (int i = 0; i < inputArray.length; i++) {
            System.out.println("Enter a colour (w, y, b, g, r, o) or u to undo");
            char input = scan.next().toUpperCase().charAt(0);
            if (input == 'U') {
                if (i > 0) {
                    inputArray[i] = ' ';
                    inputArray[i - 1] = '*';
                    i -= 2;
                }
            } else {
                inputArray[i] = input;
                if ((i + 1 < inputArray.length))
                    inputArray[i + 1] = '*';
            }
            displayCube(inputArray, true);
        }
        scan.close();
        return parseUserInput(inputArray);
    }

    public static boolean parseUserInputHelper(int[] inputC, int[] searchI, String colours) {
        return (searchI.length == 3)
                ? colours.contains(Character.toString(inputC[searchI[0]]))
                        && colours.contains(Character.toString(inputC[searchI[1]]))
                        && colours.contains(Character.toString(inputC[searchI[2]]))
                : colours.contains(Character.toString(inputC[searchI[0]]))
                        && colours.contains(Character.toString(inputC[searchI[1]]));
    }

    public static int[] parseUserInput(int[] inputCube) {
        parseMap.put("YBO", deepCopy(ybo0x9x38));
        parseMap.put("YGO", deepCopy(ygo2x29x36));
        parseMap.put("YBR", deepCopy(ybr6x11x18));
        parseMap.put("YRG", deepCopy(yrg8x20x27));
        parseMap.put("BOW", deepCopy(bow15x44x51));
        parseMap.put("BRW", deepCopy(brw17x24x45));
        parseMap.put("RGW", deepCopy(rgw26x33x47));
        parseMap.put("GOW", deepCopy(gow35x42x53));
        parseMap.put("YO", deepCopy(yo1x37));
        parseMap.put("YG", deepCopy(yg5x28));
        parseMap.put("YR", deepCopy(yr7x19));
        parseMap.put("YB", deepCopy(yb3x10));
        parseMap.put("BR", deepCopy(br14x21));
        parseMap.put("BO", deepCopy(bo12x41));
        parseMap.put("RG", deepCopy(rg23x30));
        parseMap.put("GO", deepCopy(go32x39));
        parseMap.put("RW", deepCopy(rw25x46));
        parseMap.put("GW", deepCopy(gw34x50));
        parseMap.put("OW", deepCopy(ow43x52));
        parseMap.put("BW", deepCopy(bw16x48));
        parseMap.put("Y", YELLOW);
        parseMap.put("B", BLUE);
        parseMap.put("R", RED);
        parseMap.put("G", GREEN);
        parseMap.put("O", ORANGE);
        parseMap.put("W", WHITE);
        int[] cube = new int[54];
        int[][] allCornerI = { deepCopy(ybo0x9x38), deepCopy(ybr6x11x18), deepCopy(ygo2x29x36), deepCopy(yrg8x20x27),
                deepCopy(brw17x24x45), deepCopy(bow15x44x51), deepCopy(rgw26x33x47), deepCopy(gow35x42x53) };
        int[][] allEdgeI = { deepCopy(yo1x37), deepCopy(yg5x28), deepCopy(yr7x19), deepCopy(yb3x10), deepCopy(br14x21),
                deepCopy(bo12x41), deepCopy(rg23x30), deepCopy(go32x39), deepCopy(rw25x46), deepCopy(gw34x50),
                deepCopy(ow43x52), deepCopy(bw16x48) };
        for (int[] i : allCornerI) {
            String key = (parseUserInputHelper(inputCube, i, "YBO")) ? "YBO"
                    : (parseUserInputHelper(inputCube, i, "YBR")) ? "YBR"
                            : (parseUserInputHelper(inputCube, i, "YRG")) ? "YRG"
                                    : (parseUserInputHelper(inputCube, i, "YGO")) ? "YGO"
                                            : (parseUserInputHelper(inputCube, i, "BOW")) ? "BOW"
                                                    : (parseUserInputHelper(inputCube, i, "BRW")) ? "BRW"
                                                            : (parseUserInputHelper(inputCube, i, "RGW")) ? "RGW"
                                                                    : "GOW";
            int[] cornerToUse = parseMap.get(key);
            cube[i[0]] = cornerToUse[key.indexOf(inputCube[i[0]])];
            cube[i[1]] = cornerToUse[key.indexOf(inputCube[i[1]])];
            cube[i[2]] = cornerToUse[key.indexOf(inputCube[i[2]])];
        }
        for (int[] i : allEdgeI) {
            String key = (parseUserInputHelper(inputCube, i, "YB")) ? "YB"
                    : (parseUserInputHelper(inputCube, i, "YO")) ? "YO"
                            : (parseUserInputHelper(inputCube, i, "YG")) ? "YG"
                                    : (parseUserInputHelper(inputCube, i, "YR")) ? "YR"
                                            : (parseUserInputHelper(inputCube, i, "BR")) ? "BR"
                                                    : (parseUserInputHelper(inputCube, i, "BO")) ? "BO"
                                                            : (parseUserInputHelper(inputCube, i, "RG")) ? "RG"
                                                                    : (parseUserInputHelper(inputCube, i, "GO")) ? "GO"
                                                                            : (parseUserInputHelper(inputCube, i, "GW"))
                                                                                    ? "GW"
                                                                                    : (parseUserInputHelper(inputCube,
                                                                                            i, "BW"))
                                                                                                    ? "BW"
                                                                                                    : (parseUserInputHelper(
                                                                                                            inputCube,
                                                                                                            i, "RW"))
                                                                                                                    ? "RW"
                                                                                                                    : "OW";
            int[] valsToUse = parseMap.get(key);
            cube[i[0]] = valsToUse[key.indexOf(inputCube[i[0]])];
            cube[i[1]] = valsToUse[key.indexOf(inputCube[i[1]])];
        }
        cube[4] = Arrays.stream(parseMap.get(Character.toString(inputCube[4]))).sum() / 2;
        cube[13] = Arrays.stream(parseMap.get(Character.toString(inputCube[13]))).sum() / 2;
        cube[22] = Arrays.stream(parseMap.get(Character.toString(inputCube[22]))).sum() / 2;
        cube[31] = Arrays.stream(parseMap.get(Character.toString(inputCube[31]))).sum() / 2;
        cube[40] = Arrays.stream(parseMap.get(Character.toString(inputCube[40]))).sum() / 2;
        cube[49] = Arrays.stream(parseMap.get(Character.toString(inputCube[49]))).sum() / 2;
        return cube;
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

    public static void initMoveMap() {
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
    }

    public static int[] initCube(int[] cube) {
        for (int i = 0; i < 54; i++) {
            cube[i] = i;
        }
        return cube;
    }

    public static int[] deepCopy(int[] array) {
        int[] deepCopy = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            deepCopy[i] = array[i];
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

    public static void printMapToColour(int[] array, int start, int end, boolean isInput) {
        for (int i = start; i < end; i++) {
            System.out.print((!isInput
                    ? "\033[4m" + TEXT_COLOUR.get(colourMap.get((int) array[i])) + colourMap.get((int) array[i])
                    : (TEXT_COLOUR.keySet().contains(Character.toString((char) array[i])))
                            ? TEXT_COLOUR.get(Character.toString((char) array[i])) + (char) array[i]
                            : (char) array[i])
                    + TWHITE + "|\033[0m");
        }
    }

    public static void displayCube(int[] cube, boolean isInput) {
        int[] yellow = getFace(cube, YELLOW), blue = getFace(cube, BLUE), red = getFace(cube, RED);
        int[] green = getFace(cube, GREEN), orange = getFace(cube, ORANGE), white = getFace(cube, WHITE);
        System.out.printf("%6s|", " ");
        printMapToColour(yellow, 0, 3, isInput);
        System.out.printf("\n" + "%6s|", " ");
        printMapToColour(yellow, 3, 6, isInput);
        System.out.printf("\n" + "\033[4m%6s|\033[0m", " ");
        printMapToColour(yellow, 6, 9, isInput);
        System.out.printf("\033[4m%12s\033[0m\n|", "_");
        printMapToColour(blue, 0, 3, isInput);
        printMapToColour(red, 0, 3, isInput);
        printMapToColour(green, 0, 3, isInput);
        printMapToColour(orange, 0, 3, isInput);
        System.out.print("\n|");
        printMapToColour(blue, 3, 6, isInput);
        printMapToColour(red, 3, 6, isInput);
        printMapToColour(green, 3, 6, isInput);
        printMapToColour(orange, 3, 6, isInput);
        System.out.print("\n|");
        printMapToColour(blue, 6, 9, isInput);
        printMapToColour(red, 6, 9, isInput);
        printMapToColour(green, 6, 9, isInput);
        printMapToColour(orange, 6, 9, isInput);
        System.out.printf("\n" + "%6s|", " ");
        printMapToColour(white, 0, 3, isInput);
        System.out.printf("\n" + "%6s|", " ");
        printMapToColour(white, 3, 6, isInput);
        System.out.printf("\n" + "%6s|", " ");
        printMapToColour(white, 6, 9, isInput);
        System.out.println(TWHITE);
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

    public static int[] makeMoves(int[] cube, String moves) {
        for (String move : moves.split(" ")) {
            move = moveMap.get(move);
            String[] splitMove = { Character.toString(move.charAt(0)), Character.toString(move.charAt(1)) };
            int amt = Integer.parseInt(splitMove[1]);
            movesMade += moveMap.get(move) + " ";
            for (int i = 0; i < amt; i++) {
                switch (splitMove[0]) {
                    case "R":
                        cube = moveHelper(moveHelper(deepCopy(cube), RIGHT_MAIN, 3, 12), RIGHT_SUB, 2, 8);
                        break;
                    case "L":
                        cube = moveHelper(moveHelper(deepCopy(cube), LEFT_MAIN, 3, 12), LEFT_SUB, 2, 8);
                        break;
                    case "U":
                        cube = moveHelper(moveHelper(deepCopy(cube), UP_MAIN, 3, 12), UP_SUB, 2, 8);
                        break;
                    case "D":
                        cube = moveHelper(moveHelper(deepCopy(cube), DOWN_MAIN, 3, 12), DOWN_SUB, 2, 8);
                        break;
                    case "F":
                        cube = moveHelper(moveHelper(deepCopy(cube), FRONT_MAIN, 3, 12), FRONT_SUB, 2, 8);
                        break;
                    case "B":
                        cube = moveHelper(moveHelper(deepCopy(cube), BACK_MAIN, 3, 12), BACK_SUB, 2, 8);
                        break;
                    case "E":
                        cube = moveHelper(deepCopy(cube), E_MAIN, 3, 12);
                        break;
                    case "M":
                        cube = moveHelper(deepCopy(cube), M_MAIN, 3, 12);
                        break;
                    case "S":
                        cube = moveHelper(deepCopy(cube), S_MAIN, 3, 12);
                        break;
                }
            }
        }
        return cube;
    }

    public static int[] scrambleCube(int[] cube, int amt) {
        for (int i = 0; i < amt; i++) {
            cube = makeMoves(cube, ALL_MOVES[new Random().nextInt(ALL_MOVES.length)]);
        }
        return cube;
    }

    public static int[] solveCube(int[] cube) {
        cube = solveBlueCenter(deepCopy(cube));
        cube = solveBlueWhiteEdge(deepCopy(cube));
        cube = loadBlueRedEdge(deepCopy(cube));
        cube = solveBlueRedEdge(deepCopy(cube));
        cube = loadBlueOrangeEdge(deepCopy(cube));
        cube = solveBlueOrangeEdge(deepCopy(cube));
        cube = solveGreenWhiteEdge(deepCopy(cube));
        cube = loadGreenRedEdge(deepCopy(cube));
        cube = solveGreenRedEdge(deepCopy(cube));
        cube = loadGreenOrangeEdge(deepCopy(cube));
        cube = solveGreenOrangeEdge(deepCopy(cube));
        cube = orientYellowCorners(deepCopy(cube));
        cube = orientColouredCorners(deepCopy(cube));
        cube = solveBadEdges(deepCopy(cube));
        cube = solveBlueGreenSide(deepCopy(cube));
        cube = solveRestOfCube(deepCopy(cube));
        return cube;
    }

    public static int[] solveBlueCenter(int[] cube) {
        return cube[13] == 13 ? cube : cube[22] == 13 ? makeMoves(deepCopy(cube), "E'")
        : cube[4] == 13 ? makeMoves(deepCopy(cube), "S'") : cube[31] == 13 ? makeMoves(deepCopy(cube), "E2")
        : cube[40] == 13 ? makeMoves(deepCopy(cube), "E") : makeMoves(deepCopy(cube), "S");
    }

    public static int[] solveBlueWhiteEdge(int[] cube) {
        return cube[bw16x48[0]] == bw16x48[0] ? cube
        : cube[br14x21[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "L")
        : cube[br14x21[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "F' D'")
        : cube[bo12x41[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "L'")
        : cube[bo12x41[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "B D")
        : cube[yb3x10[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "L F' D'")
        : cube[yb3x10[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "L2")
        : cube[yo1x37[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "B L'")
        : cube[yo1x37[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "U' L2")
        : cube[yg5x28[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "U F' L")
        : cube[yg5x28[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "U2 L2")
        : cube[yr7x19[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "F' L")
        : cube[yr7x19[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "U L2")
        : cube[rg23x30[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "F D'")
        : cube[rg23x30[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "F2 L")
        : cube[rw25x46[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "D'")
        : cube[rw25x46[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "F L")
        : cube[go32x39[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "B2 L'")
        : cube[go32x39[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "B' D")
        : cube[gw34x50[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "D2")
        : cube[gw34x50[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "R F D'")
        : cube[ow43x52[0]] == bw16x48[0] ? makeMoves(deepCopy(cube), "D")
        : cube[ow43x52[0]] == bw16x48[1] ? makeMoves(deepCopy(cube), "B' L'")
        : makeMoves(deepCopy(cube),"D F L");
    }

    public static int[] loadBlueRedEdge(int[] cube) {
        return (cube[br14x21[0]] == br14x21[0] && cube[brw17x24x45[0]] == brw17x24x45[0])
                || (cube[rw25x46[0]] == br14x21[0]) || (cube[rw25x46[0]] == br14x21[1]) ? cube
        : cube[yo1x37[0]] == br14x21[0] || cube[yo1x37[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "M2")
        : cube[yg5x28[0]] == br14x21[0] || cube[yg5x28[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "U M")
        : cube[yr7x19[0]] == br14x21[0] || cube[yr7x19[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "M")
        : cube[yb3x10[0]] == br14x21[0] || cube[yb3x10[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "U' M")
        : cube[bo12x41[0]] == br14x21[0] || cube[bo12x41[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "B M'")
        : cube[br14x21[0]] == br14x21[0] || cube[br14x21[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "F'")
        : cube[rg23x30[0]] == br14x21[0] || cube[rg23x30[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "F")
        : cube[go32x39[0]] == br14x21[0] || cube[go32x39[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "R2 F")
        : cube[gw34x50[0]] == br14x21[0] || cube[gw34x50[0]] == br14x21[1] ? makeMoves(deepCopy(cube), "R F")
        : makeMoves(deepCopy(cube), "M'");

    }

    public static int[] solveBlueRedEdge(int[] cube) {
        return cube[br14x21[0]] == br14x21[0] && cube[brw17x24x45[0]] == brw17x24x45[0]?cube
        :cube[rw25x46[0]] == br14x21[0] && cube[ybo0x9x38[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U' M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[ybo0x9x38[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U R' U M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[ybo0x9x38[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "M2 U2 R' M F")
        :cube[rw25x46[0]] == br14x21[0] && cube[ygo2x29x36[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U2 M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[ygo2x29x36[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U' M2 U2 R' M F")
        :cube[rw25x46[0]] == br14x21[0] && cube[ygo2x29x36[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R' U M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[yrg8x20x27[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[yrg8x20x27[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R U2 M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[yrg8x20x27[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U' R' U M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[ybr6x11x18[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[ybr6x11x18[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U M2 U2 R' M F")
        :cube[rw25x46[0]] == br14x21[0] && cube[ybr6x11x18[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U B' U2 M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[bow15x44x51[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "B' U' M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[bow15x44x51[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "B' M2 U2 R' M F")
        :cube[rw25x46[0]] == br14x21[0] && cube[bow15x44x51[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "M B R2 F2")
        :cube[rw25x46[0]] == br14x21[0] && cube[brw17x24x45[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "M F M2 F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[brw17x24x45[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "F' R U F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[brw17x24x45[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "M F2 U M2 F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[rgw26x33x47[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R U M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[rgw26x33x47[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R2 U' M2 U2 R' M F")
        :cube[rw25x46[0]] == br14x21[0] && cube[rgw26x33x47[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R2 U2 M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[gow35x42x53[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "B U2 M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[gow35x42x53[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R' U2 M' F'")
        :cube[rw25x46[0]] == br14x21[0] && cube[gow35x42x53[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R2 U M' F'")
        :cube[rw25x46[0]] == br14x21[1] && cube[ybo0x9x38[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U M2 U2 F'")
        :cube[rw25x46[0]] == br14x21[1] && cube[ybo0x9x38[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "B' M2 U2 F'")
        :cube[rw25x46[0]] == br14x21[1] && cube[ybo0x9x38[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U2 R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[ygo2x29x36[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R2 F")
        :cube[rw25x46[0]] == br14x21[1] && cube[ygo2x29x36[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[ygo2x29x36[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R' U' R2 F")
        :cube[rw25x46[0]] == br14x21[1] && cube[yrg8x20x27[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U' R2 F")
        :cube[rw25x46[0]] == br14x21[1] && cube[yrg8x20x27[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[yrg8x20x27[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R U R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[ybr6x11x18[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U2 R2 F")
        :cube[rw25x46[0]] == br14x21[1] && cube[ybr6x11x18[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U' R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[ybr6x11x18[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "U' R U R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[bow15x44x51[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "B' U R2 F")
        :cube[rw25x46[0]] == br14x21[1] && cube[bow15x44x51[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "B' U2 R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[bow15x44x51[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "B2 R2 F")
        :cube[rw25x46[0]] == br14x21[1] && cube[brw17x24x45[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "L' U' M' U L")
        :cube[rw25x46[0]] == br14x21[1] && cube[brw17x24x45[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "L' U2 R' M' U L")
        :cube[rw25x46[0]] == br14x21[1] && cube[brw17x24x45[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "L' U L U' R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[rgw26x33x47[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R U' R2 F")
        :cube[rw25x46[0]] == br14x21[1] && cube[rgw26x33x47[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R2 U R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[rgw26x33x47[2]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "F")
        :cube[rw25x46[0]] == br14x21[1] && cube[gow35x42x53[0]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R' U R' F")
        :cube[rw25x46[0]] == br14x21[1] && cube[gow35x42x53[1]] == brw17x24x45[0]?makeMoves(deepCopy(cube), "R F")
        :makeMoves(deepCopy(cube), "B U R' F");
    }

    public static int[] loadBlueOrangeEdge(int[] cube) {
        return (cube[bo12x41[0]] == bo12x41[0] && cube[bow15x44x51[0]] == bow15x44x51[0])
        || (cube[rw25x46[0]] == bo12x41[0]) || (cube[rw25x46[0]] == bo12x41[1])?cube
        :cube[yo1x37[0]] == bo12x41[0] || cube[yo1x37[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "M2")
        :cube[yg5x28[0]] == bo12x41[0] || cube[yg5x28[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "U M")
        :cube[yr7x19[0]] == bo12x41[0] || cube[yr7x19[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "M")
        :cube[yb3x10[0]] == bo12x41[0] || cube[yb3x10[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "U' M")
        :cube[bo12x41[0]] == bo12x41[0] || cube[bo12x41[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "B M'")
        :cube[rg23x30[0]] == bo12x41[0] || cube[rg23x30[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "L F L'")
        :cube[go32x39[0]] == bo12x41[0] || cube[go32x39[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "R' U M")
        :cube[gw34x50[0]] == bo12x41[0] || cube[gw34x50[0]] == bo12x41[1]?makeMoves(deepCopy(cube), "L' D' L")
        :makeMoves(deepCopy(cube), "M'");
    }

    public static int[] solveBlueOrangeEdge(int[] cube) {
        return cube[bo12x41[0]] == bo12x41[0] && cube[bow15x44x51[0]] == bow15x44x51[0]?cube
        :cube[rw25x46[0]] == bo12x41[0] && cube[ybo0x9x38[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U2 M' U2 B")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ybo0x9x38[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ybo0x9x38[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B' R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ygo2x29x36[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U R2 M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ygo2x29x36[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R' U' R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ygo2x29x36[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[yrg8x20x27[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R2 M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[yrg8x20x27[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R U R2 M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[yrg8x20x27[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U' R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ybr6x11x18[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U' R2 M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ybr6x11x18[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U B' R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[ybr6x11x18[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U2 R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[bow15x44x51[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[bow15x44x51[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B2 R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[bow15x44x51[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B' U R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[rgw26x33x47[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R' M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[rgw26x33x47[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R U' R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[rgw26x33x47[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R2 U R2 M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[gow35x42x53[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R2 U' R M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[gow35x42x53[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R' U R2 M B'")
        :cube[rw25x46[0]] == bo12x41[0] && cube[gow35x42x53[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "M B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ybo0x9x38[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ybo0x9x38[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U' M' U2 R M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ybo0x9x38[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B' R M' U2 M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ygo2x29x36[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U' M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ygo2x29x36[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ygo2x29x36[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R M' U2 M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[yrg8x20x27[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R2 M' U2 M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[yrg8x20x27[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R U' M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[yrg8x20x27[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U' R M' U2 M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ybr6x11x18[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ybr6x11x18[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "U' R U' M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[ybr6x11x18[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "M' U2 R M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[bow15x44x51[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B M' U2 M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[bow15x44x51[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "M B2 R M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[bow15x44x51[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B' U R M' U2 M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[rgw26x33x47[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R' M' U2 M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[rgw26x33x47[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R U M' U2 R M' B'")
        :cube[rw25x46[0]] == bo12x41[1] && cube[rgw26x33x47[2]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R2 U' M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[gow35x42x53[0]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "B U' M2 B")
        :cube[rw25x46[0]] == bo12x41[1] && cube[gow35x42x53[1]] == bow15x44x51[0]?makeMoves(deepCopy(cube), "R' U' M2 B")
        :makeMoves(deepCopy(cube), "M' U2 M' B'");
    }

    public static int[] solveGreenWhiteEdge(int[] cube) {
        return cube[gw34x50[0]] == gw34x50[0]?cube 
        :cube[yo1x37[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "M U' R2")
        :cube[yo1x37[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "U R2")
        :cube[yg5x28[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "U M' U R2")
        :cube[yg5x28[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "R2")
        :cube[yr7x19[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "M' U R2")
        :cube[yr7x19[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "U' R2")
        :cube[yb3x10[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "U M U' R2")
        :cube[yb3x10[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "U2 R2")
        :cube[rg23x30[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "R U M' U R2")
        :cube[rg23x30[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "R'")
        :cube[rw25x46[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "M2 U R2")
        :cube[rw25x46[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "M' U' R2")
        :cube[go32x39[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "R")
        :cube[go32x39[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "R' U M' U R2")
        :cube[ow43x52[0]] == gw34x50[0]?makeMoves(deepCopy(cube), "M2 U' R2")
        :cube[ow43x52[0]] == gw34x50[1]?makeMoves(deepCopy(cube), "M U R2")
        :makeMoves(deepCopy(cube), "R2 U M' U R2");
    }

    public static int[] loadGreenRedEdge(int[] cube) {
        return (cube[rg23x30[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[0])
        || (cube[rw25x46[0]] == rg23x30[0]) || (cube[rw25x46[0]] == rg23x30[1])?cube
        :cube[yo1x37[0]] == rg23x30[0] || cube[yo1x37[0]] == rg23x30[1]?makeMoves(deepCopy(cube), "M2")
        :cube[yg5x28[0]] == rg23x30[0] || cube[yg5x28[0]] == rg23x30[1]?makeMoves(deepCopy(cube), "U M")
        :cube[yr7x19[0]] == rg23x30[0] || cube[yr7x19[0]] == rg23x30[1]?makeMoves(deepCopy(cube), "M")
        :cube[yb3x10[0]] == rg23x30[0] || cube[yb3x10[0]] == rg23x30[1]?makeMoves(deepCopy(cube), "U' M")
        :cube[rg23x30[0]] == rg23x30[0] || cube[rg23x30[0]] == rg23x30[1]?makeMoves(deepCopy(cube), "R U R' M")
        :cube[go32x39[0]] == rg23x30[0] || cube[go32x39[0]] == rg23x30[1]?makeMoves(deepCopy(cube), "R' U R M")
        :makeMoves(deepCopy(cube), "M'");
    }

    public static int[] solveGreenRedEdge(int[] cube) {
        return cube[rg23x30[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[0]?cube
        :cube[rw25x46[0]] == rg23x30[0] && cube[ybo0x9x38[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "U' R M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ybo0x9x38[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ybo0x9x38[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "U R' U R2 M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ygo2x29x36[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "U2 R M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ygo2x29x36[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "U' M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ygo2x29x36[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "R' U R2 M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[yrg8x20x27[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "U R M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[yrg8x20x27[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "U2 M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[yrg8x20x27[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "R U' R' M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ybr6x11x18[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "R M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ybr6x11x18[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "U M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[ybr6x11x18[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "U' R U' R' M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "R U M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "R U2 R' M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[rgw26x33x47[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "R U' R' U R M' U' R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[gow35x42x53[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "R' U' R M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[gow35x42x53[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "R' U R U' M2 U2 R M' U R'")
        :cube[rw25x46[0]] == rg23x30[0] && cube[gow35x42x53[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "R' U2 R2 M' U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ybo0x9x38[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "U M2 U2 R U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ybo0x9x38[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "U2 R M2 U R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ybo0x9x38[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "U R' U' M2 U2 R2 U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ygo2x29x36[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "M2 U2 R U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ygo2x29x36[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "U R M2 U R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ygo2x29x36[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "R' U' M2 U2 R2 U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[yrg8x20x27[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "U' M2 U2 R U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[yrg8x20x27[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "R M2 U R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[yrg8x20x27[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "U' R' U' M2 U2 R2 U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ybr6x11x18[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "U2 M2 U2 R U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ybr6x11x18[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "U' R M2 U R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[ybr6x11x18[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "U2 R' U' M2 U2 R2 U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[rgw26x33x47[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "R U' M2 U R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[rgw26x33x47[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "R M' U R' U' R M' U R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[rgw26x33x47[0]] == rgw26x33x47[2]?makeMoves(deepCopy(cube), "R U' R' U' M2 U2 R U' R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[gow35x42x53[0]] == rgw26x33x47[0]?makeMoves(deepCopy(cube), "R' U M' U R U' R M' U R'")
        :cube[rw25x46[0]] == rg23x30[1] && cube[gow35x42x53[0]] == rgw26x33x47[1]?makeMoves(deepCopy(cube), "R' U R U R M2 U R'")
        :makeMoves(deepCopy(cube), "R' M2 U2 R2 U' R'");
    }

    public static int[] loadGreenOrangeEdge(int[] cube) {
        return (cube[go32x39[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[0])
                || (cube[rw25x46[0]] == go32x39[0]) || (cube[rw25x46[0]] == go32x39[1])?cube
        :cube[yo1x37[0]] == go32x39[0] || cube[yo1x37[0]] == go32x39[1]?makeMoves(deepCopy(cube), "M2")
        :cube[yg5x28[0]] == go32x39[0] || cube[yg5x28[0]] == go32x39[1]?makeMoves(deepCopy(cube), "U M")
        :cube[yr7x19[0]] == go32x39[0] || cube[yr7x19[0]] == go32x39[1]?makeMoves(deepCopy(cube), "M")
        :cube[yb3x10[0]] == go32x39[0] || cube[yb3x10[0]] == go32x39[1]?makeMoves(deepCopy(cube), "U' M")
        :cube[go32x39[0]] == go32x39[0] || cube[go32x39[0]] == go32x39[1]?makeMoves(deepCopy(cube), "R' U R M")
        :makeMoves(deepCopy(cube), "M'");
    }

    public static int[] solveGreenOrangeEdge(int[] cube) {
        return cube[go32x39[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[0]?cube
        :cube[rw25x46[0]] == go32x39[0] && cube[ybo0x9x38[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "U' M' U2 R' M U' R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ybo0x9x38[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "M2 R' U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ybo0x9x38[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "R' U2 R U' M2 R' U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ygo2x29x36[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "U2 M' U2 R' M U' R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ygo2x29x36[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "U' M2 R' U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ygo2x29x36[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "R' U R M' U2 R' M U' R")
        :cube[rw25x46[0]] == go32x39[0] && cube[yrg8x20x27[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "U M' U2 R' M U' R")
        :cube[rw25x46[0]] == go32x39[0] && cube[yrg8x20x27[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "U2 M2 R' U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[yrg8x20x27[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "U' R' U R M' U2 R' M U' R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ybr6x11x18[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "M' U2 R' M U' R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ybr6x11x18[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "U M2 R' U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[ybr6x11x18[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "R' U' R U' M2 R' U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "R' U' M2 U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "R' U R U' M2 R' U R")
        :cube[rw25x46[0]] == go32x39[0] && cube[gow35x42x53[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "R' U2 R M' U2 R' M U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ybo0x9x38[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "U R' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ybo0x9x38[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "R' U2 M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ybo0x9x38[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "U R' U R U2 R' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ygo2x29x36[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "R' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ygo2x29x36[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "U M' U R' U2 R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ygo2x29x36[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "R' U' M2 U' R U R' M U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[yrg8x20x27[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "U' R' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[yrg8x20x27[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "U R' U' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[yrg8x20x27[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "U' R' U R U2 R' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ybr6x11x18[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "U2 R' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ybr6x11x18[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "R' U' M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[ybr6x11x18[0]] == gow35x42x53[2]?makeMoves(deepCopy(cube), "R' U' R U M' U R' U2 R")
        :cube[rw25x46[0]] == go32x39[1] && cube[gow35x42x53[0]] == gow35x42x53[0]?makeMoves(deepCopy(cube), "R' U M' U' R")
        :cube[rw25x46[0]] == go32x39[1] && cube[gow35x42x53[0]] == gow35x42x53[1]?makeMoves(deepCopy(cube), "R' U R U M' U R' U2 R")
        :makeMoves(deepCopy(cube), "R' M2 U2 R U2 R' M U' R");
    }

    public static boolean OYCHelper(int[] cube, int i, int i1, int i2, int i3) {
        return colourMap.get(cube[i]) == colourMap.get(cube[i1]) && colourMap.get(cube[i1]) == colourMap.get(cube[i2])
                && colourMap.get(cube[i2]) == colourMap.get(cube[i3]) && colourMap.get(cube[i]) == "Y";
    }

    public static int[] orientYellowCorners(int[] cube) {
        return OYCHelper(deepCopy(cube), ybo0x9x38[0], ygo2x29x36[0], yrg8x20x27[0], ybr6x11x18[0])?cube
        :OYCHelper(deepCopy(cube), ybr6x11x18[2], yrg8x20x27[1], ygo2x29x36[2], ybo0x9x38[2])?makeMoves(deepCopy(cube), "R U2 R' U' R U R' U' R U' R'")
        :OYCHelper(deepCopy(cube), ybr6x11x18[1], yrg8x20x27[2], ygo2x29x36[1], ybo0x9x38[1])?makeMoves(deepCopy(cube), "U R U2 R' U' R U R' U' R U' R'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[1], ybr6x11x18[1], ygo2x29x36[2], yrg8x20x27[1])?makeMoves(deepCopy(cube), "F R U R' U' R U R' U' F'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[2], ygo2x29x36[2], ybr6x11x18[1], yrg8x20x27[2])?makeMoves(deepCopy(cube), "U' F R U R' U' R U R' U' F'")
        :OYCHelper(deepCopy(cube), ybr6x11x18[2], ybo0x9x38[2], ygo2x29x36[1], yrg8x20x27[2])?makeMoves(deepCopy(cube), "U2 F R U R' U' R U R' U' F'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[1], ybr6x11x18[2], yrg8x20x27[1], ygo2x29x36[1])?makeMoves(deepCopy(cube), "U F R U R' U' R U R' U' F'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], ybr6x11x18[0], yrg8x20x27[2], ygo2x29x36[1])?makeMoves(deepCopy(cube), "U2 F R U R' U' F'")
        :OYCHelper(deepCopy(cube), ybr6x11x18[0], yrg8x20x27[0], ybo0x9x38[2], ygo2x29x36[2])?makeMoves(deepCopy(cube), "U' F R U R' U' F'")
        :OYCHelper(deepCopy(cube), ygo2x29x36[0], yrg8x20x27[0], ybo0x9x38[1], ybr6x11x18[1])?makeMoves(deepCopy(cube), "F R U R' U' F'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], ygo2x29x36[0], ybr6x11x18[2], yrg8x20x27[1])?makeMoves(deepCopy(cube), "U F R U R' U' F'")
        :OYCHelper(deepCopy(cube), ygo2x29x36[0], yrg8x20x27[0], ybo0x9x38[2], ybr6x11x18[2])?makeMoves(deepCopy(cube), "R U R' U' R' F R F'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], ygo2x29x36[0], ybr6x11x18[1], yrg8x20x27[2])?makeMoves(deepCopy(cube), "U R U R' U' R' F R F'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], ybr6x11x18[0], ygo2x29x36[2], yrg8x20x27[1])?makeMoves(deepCopy(cube), "U2 R U R' U' R' F R F'")
        :OYCHelper(deepCopy(cube), ybr6x11x18[0], yrg8x20x27[0], ybo0x9x38[1], ygo2x29x36[1])?makeMoves(deepCopy(cube), "U' R U R' U' R' F R F'")
        :OYCHelper(deepCopy(cube), ybr6x11x18[0], yrg8x20x27[1], ygo2x29x36[1], ybo0x9x38[2])?makeMoves(deepCopy(cube), "R U R' U R U2 R'")
        :OYCHelper(deepCopy(cube), yrg8x20x27[0], ygo2x29x36[1], ybo0x9x38[2], ybr6x11x18[1])?makeMoves(deepCopy(cube), "U R U R' U R U2 R'")
        :OYCHelper(deepCopy(cube), ygo2x29x36[0], ybo0x9x38[2], ybr6x11x18[1], yrg8x20x27[1])?makeMoves(deepCopy(cube), "U2 R U R' U R U2 R'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], ybr6x11x18[1], yrg8x20x27[1], ygo2x29x36[1])?makeMoves(deepCopy(cube), "U' R U R' U R U2 R'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], ybr6x11x18[2], yrg8x20x27[2], ygo2x29x36[2])?makeMoves(deepCopy(cube), "R' U' R U' R' U2 R")
        :OYCHelper(deepCopy(cube), ybr6x11x18[0], ybo0x9x38[1], yrg8x20x27[2], ygo2x29x36[2])?makeMoves(deepCopy(cube), "U R' U' R U' R' U2 R")
        :OYCHelper(deepCopy(cube), yrg8x20x27[0], ybr6x11x18[2], ybo0x9x38[1], ygo2x29x36[2])?makeMoves(deepCopy(cube), "U2 R' U' R U' R' U2 R")
        :OYCHelper(deepCopy(cube), ygo2x29x36[0], ybo0x9x38[1], ybr6x11x18[2], yrg8x20x27[2])?makeMoves(deepCopy(cube), "U' R' U' R U' R' U2 R")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], yrg8x20x27[0], ybr6x11x18[2], ygo2x29x36[1])?makeMoves(deepCopy(cube), "F R' F' R U R U' R'")
        :OYCHelper(deepCopy(cube), ygo2x29x36[0], ybr6x11x18[0], yrg8x20x27[2], ybo0x9x38[2])?makeMoves(deepCopy(cube), "U F R' F' R U R U' R'")
        :OYCHelper(deepCopy(cube), ybo0x9x38[0], yrg8x20x27[0], ybr6x11x18[1], ygo2x29x36[2])?makeMoves(deepCopy(cube), "U2 F R' F' R U R U' R'")
        :makeMoves(deepCopy(cube), "U' F R' F' R U R U' R'");
    }

    public static int[] orientColouredCorners(int[] cube) {
        return colourMap.get(cube[ybo0x9x38[1]]) == colourMap.get(cube[ybr6x11x18[1]])
                && colourMap.get(cube[ybr6x11x18[2]]) == colourMap.get(cube[yrg8x20x27[1]])?cube
        :colourMap.get(cube[ybo0x9x38[1]]) == colourMap.get(cube[ybr6x11x18[1]])?makeMoves(deepCopy(cube), "R U R' U' R' F R2 U' R' U' R U R' F'")
        :colourMap.get(cube[ybr6x11x18[2]]) == colourMap.get(cube[yrg8x20x27[1]])?makeMoves(deepCopy(cube), "U R U R' U' R' F R2 U' R' U' R U R' F'")
        :colourMap.get(cube[yrg8x20x27[2]]) == colourMap.get(cube[ygo2x29x36[1]])?makeMoves(deepCopy(cube), "U2 R U R' U' R' F R2 U' R' U' R U R' F'")
        :colourMap.get(cube[ybo0x9x38[2]]) == colourMap.get(cube[ygo2x29x36[2]])?makeMoves(deepCopy(cube), "U' R U R' U' R' F R2 U' R' U' R U R' F'")
        :makeMoves(deepCopy(cube), "R U R' U' R' F R2 U' R' U' R U R' F' U2 R U R' U' R' F R2 U' R' U' R U R' F'");
    }

    public static boolean SBEHelper(int[] cube, int i, int i1, int i2, int i3, int i4, int i5) {
        return (colourMap.get(cube[i]) == "Y" || colourMap.get(cube[i]) == "W")
                && (colourMap.get(cube[i1]) == "Y" || colourMap.get(cube[i1]) == "W")
                && (colourMap.get(cube[i2]) == "Y" || colourMap.get(cube[i2]) == "W")
                && (colourMap.get(cube[i3]) == "Y" || colourMap.get(cube[i3]) == "W")
                && (colourMap.get(cube[i4]) == "Y" || colourMap.get(cube[i4]) == "W")
                && (colourMap.get(cube[i5]) == "Y" || colourMap.get(cube[i5]) == "W");
    }

    public static int[] solveBadEdges(int[] cube) {
        cube = colourMap.get(cube[4]) != "Y" && colourMap.get(cube[4]) != "W" ? makeMoves(deepCopy(cube), "M") : cube;
        return SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[0], yg5x28[0], rw25x46[1], ow43x52[1]) ? cube
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[1], yg5x28[1], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[1], yr7x19[1], yg5x28[0], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "U' M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[1], yr7x19[0], yg5x28[1], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "U2 M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[1], yg5x28[1], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "U M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[1], yr7x19[0], yg5x28[1], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[1], yr7x19[1], yg5x28[0], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "U M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[1], yg5x28[1], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[1], yg5x28[1], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "U' M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[1], yg5x28[0], rw25x46[0],ow43x52[0])?makeMoves(deepCopy(cube), "U M' U2 M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[0], yg5x28[1], rw25x46[0],ow43x52[0])?makeMoves(deepCopy(cube), "M' U2 M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[1], yg5x28[1], rw25x46[0],ow43x52[0])?makeMoves(deepCopy(cube), "M U2 M' U' M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[1], yr7x19[0], yg5x28[0], rw25x46[0],ow43x52[0])?makeMoves(deepCopy(cube), "M' U2 M U' M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[0], yg5x28[1], rw25x46[0],ow43x52[0])?makeMoves(deepCopy(cube), "M' U2 M U M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[0], yg5x28[0], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "M' U M' U' M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[1], yg5x28[0], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "M U M' U' M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[0], yg5x28[1], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "U M U M' U' M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[0], yg5x28[1], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "M' U' M' U2 M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[1], yr7x19[0], yg5x28[0], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "M' U M' U2 M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[1], yg5x28[0], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "M U' M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[1], yg5x28[1], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "M U M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[0], yg5x28[0], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "U M' U M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[0], yg5x28[0], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "M' U' M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[1], yg5x28[0], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "U M' U' M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[0], yg5x28[1], rw25x46[1],ow43x52[0])?makeMoves(deepCopy(cube), "M' U M U2 M U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[0], yr7x19[0], yg5x28[0], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "U M U' M U2 M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[0], yg5x28[0], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "M U M U2 M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[1], yg5x28[0], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "U M U M U2 M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[0], yg5x28[1], rw25x46[0],ow43x52[1])?makeMoves(deepCopy(cube), "M U' M U2 M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[0], yr7x19[0], yg5x28[0], rw25x46[0],ow43x52[0])?makeMoves(deepCopy(cube), "M U M U' M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[0], yb3x10[1], yr7x19[1], yg5x28[0], rw25x46[0],ow43x52[0])?makeMoves(deepCopy(cube), "M U2 M' U M' U M")
        :SBEHelper(deepCopy(cube), yo1x37[1], yb3x10[1], yr7x19[1], yg5x28[1], rw25x46[1],ow43x52[1])?makeMoves(deepCopy(cube), "M' U2 M U2 M' U M")
        :makeMoves(deepCopy(cube), "M' U M' U2 M U M U M' U M'");
    }

    public static int[] solveBlueGreenSide(int[] cube) {
        cube = cube[yrg8x20x27[1]] == ybr6x11x18[1] ? makeMoves(deepCopy(cube), "U")
        : cube[ygo2x29x36[1]] == ybr6x11x18[1] ? makeMoves(deepCopy(cube), "U2")
        : cube[ybo0x9x38[2]] == ybr6x11x18[1] ? makeMoves(deepCopy(cube), "U'") : cube;
        return cube[yb3x10[1]] == yb3x10[1] && cube[yg5x28[1]] == yg5x28[1]?cube
        :cube[yb3x10[1]] == yg5x28[1] && cube[yg5x28[1]] == yb3x10[1]?makeMoves(deepCopy(cube), "U M2 U2 M2 U")
        :cube[yb3x10[1]] == yb3x10[1] && cube[rw25x46[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U M' U2 M' U2 M2 U'")
        :cube[yb3x10[1]] == yb3x10[1] && cube[yo1x37[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U M' U2 M' U2 M2 U'")
        :cube[yb3x10[1]] == yb3x10[1] && cube[ow43x52[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U' M U2 M U2 M2 U")
        :cube[yb3x10[1]] == yb3x10[1] && cube[yr7x19[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U' M U2 M U2 M2 U")
        :cube[rw25x46[0]] == yb3x10[1] && cube[yg5x28[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "U' M' U2 M' U2 M2 U")
        :cube[ow43x52[0]] == yb3x10[1] && cube[yg5x28[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "U M U2 M U2 M2 U'")
        :cube[yr7x19[1]] == yb3x10[1] && cube[yg5x28[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U M U2 M U2 M2 U'")
        :cube[yo1x37[1]] == yb3x10[1] && cube[yg5x28[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U' M' U2 M' U2 M2 U")
        :cube[yr7x19[1]] == yb3x10[1] && cube[yb3x10[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U' M U2 M' U'")
        :cube[yr7x19[1]] == yb3x10[1] && cube[yo1x37[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U' M2 U")
        :cube[yr7x19[1]] == yb3x10[1] && cube[rw25x46[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U2 M' U2 M' U M2 U'")
        :cube[yr7x19[1]] == yb3x10[1] && cube[ow43x52[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "M U2 M U M2 U")
        :cube[yo1x37[1]] == yb3x10[1] && cube[yb3x10[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U M' U2 M U")
        :cube[yo1x37[1]] == yb3x10[1] && cube[ow43x52[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U2 M U2 M U' M2 U")
        :cube[yo1x37[1]] == yb3x10[1] && cube[yr7x19[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U M2 U'")
        :cube[yo1x37[1]] == yb3x10[1] && cube[rw25x46[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "M' U2 M' U' M2 U'")
        :cube[yg5x28[1]] == yb3x10[1] && cube[rw25x46[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U' M' U2 M U'")
        :cube[yg5x28[1]] == yb3x10[1] && cube[ow43x52[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U M U2 M' U")
        :cube[yg5x28[1]] == yb3x10[1] && cube[yr7x19[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U M U2 M' U")
        :cube[yg5x28[1]] == yb3x10[1] && cube[yo1x37[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M2 U' M' U2 M U'")
        :cube[rw25x46[0]] == yb3x10[1] && cube[yb3x10[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "U M' U2 M U")
        :cube[rw25x46[0]] == yb3x10[1] && cube[yo1x37[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M' U2 M' U M2 U")
        :cube[rw25x46[0]] == yb3x10[1] && cube[ow43x52[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U M2 U'")
        :cube[rw25x46[0]] == yb3x10[1] && cube[yr7x19[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "U2 M' U2 M' U' M2 U")
        :cube[ow43x52[0]] == yb3x10[1] && cube[rw25x46[0]] == yg5x28[1]?makeMoves(deepCopy(cube), "U' M2 U")
        :cube[ow43x52[0]] == yb3x10[1] && cube[yo1x37[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "U2 M U2 M U M2 U'")
        :cube[ow43x52[0]] == yb3x10[1] && cube[yr7x19[1]] == yg5x28[1]?makeMoves(deepCopy(cube), "M U2 M U' M2 U'")
        :makeMoves(deepCopy(cube), "U' M U2 M' U'");
    }

    public static int[] solveRestOfCube(int[] cube) {
        if (isSolved(cube))
            return cube;
        while (!isSolved(cube)) {
            if (cube[yr7x19[1]] == yo1x37[0] && cube[22] == 4 && cube[rw25x46[0]] == yr7x19[0])
                cube = makeMoves(deepCopy(cube), "M'");
            else if (cube[rw25x46[1]] == yo1x37[0] && cube[49] == 4 && cube[ow43x52[1]] == yr7x19[0])
                cube = makeMoves(deepCopy(cube), "M2");
            else if (cube[ow43x52[0]] == yo1x37[0] && cube[40] == 4 && cube[yo1x37[1]] == yr7x19[0])
                cube = makeMoves(deepCopy(cube), "M");
            else if (colourMap.get(cube[yr7x19[0]]) == colourMap.get(cube[yo1x37[0]])
                    && colourMap.get(cube[yr7x19[0]]) == colourMap.get(cube[4])
                    && colourMap.get(cube[yr7x19[1]]) == colourMap.get(cube[rw25x46[0]]))
                cube = makeMoves(deepCopy(cube), "M2 U2 M2 U2");
            else if (colourMap.get(cube[yr7x19[1]]) == colourMap.get(cube[22])
                    && colourMap.get(cube[yr7x19[1]]) == colourMap.get(cube[rw25x46[0]])
                    && colourMap.get(cube[yr7x19[0]]) == colourMap.get(cube[yo1x37[0]]))
                cube = makeMoves(deepCopy(cube), "M U2 M2 U2");
            else if (colourMap.get(cube[yr7x19[1]]) != colourMap.get(cube[22])
                    && colourMap.get(cube[yr7x19[1]]) == colourMap.get(cube[rw25x46[0]])
                    && colourMap.get(cube[yr7x19[0]]) == colourMap.get(cube[yo1x37[0]])
                    && colourMap.get(cube[yr7x19[0]]) != colourMap.get(cube[4]))
                cube = makeMoves(deepCopy(cube), "E2 M E2");
            else
                cube = makeMoves(deepCopy(cube), "U2 M' U2 M");
        }
        return cube;
    }

    public static boolean isSolved(int[] cube) {
        for (int i = 0; i < cube.length; i++) {
            if (cube[i] != i)
                return false;
        }
        return true;
    }

    public static void reduceMovesUsed() {
        // Cancellations
        movesMade = movesMade.replace("R2 R2", "");
        movesMade = movesMade.replace("L2 L2", "");
        movesMade = movesMade.replace("U2 U2", "");
        movesMade = movesMade.replace("D2 D2", "");
        movesMade = movesMade.replace("F2 F2", "");
        movesMade = movesMade.replace("B2 B2", "");
        movesMade = movesMade.replace("E2 E2", "");
        movesMade = movesMade.replace("M2 M2", "");
        movesMade = movesMade.replace("S2 S2", "");
        movesMade = movesMade.replace("R R'", "");
        movesMade = movesMade.replace("R' R ", " ");
        movesMade = movesMade.replace("L L'", "");
        movesMade = movesMade.replace("L' L ", " ");
        movesMade = movesMade.replace("U U'", "");
        movesMade = movesMade.replace("U' U ", " ");
        movesMade = movesMade.replace("D D'", "");
        movesMade = movesMade.replace("D' D ", " ");
        movesMade = movesMade.replace("F F'", "");
        movesMade = movesMade.replace("F' F ", " ");
        movesMade = movesMade.replace("B B'", "");
        movesMade = movesMade.replace("B' B ", " ");
        movesMade = movesMade.replace("E E'", "");
        movesMade = movesMade.replace("E' E ", " ");
        movesMade = movesMade.replace("M M'", "");
        movesMade = movesMade.replace("M' M ", " ");
        movesMade = movesMade.replace("S S'", "");
        movesMade = movesMade.replace("S' S ", " ");
        // Merges
        movesMade = movesMade.replace("R R ", "R2 ");
        movesMade = movesMade.replace("R' R'", "R2");
        movesMade = movesMade.replace("L L ", "L2 ");
        movesMade = movesMade.replace("L' L'", "L2");
        movesMade = movesMade.replace("U U ", "U2 ");
        movesMade = movesMade.replace("U' U'", "U2");
        movesMade = movesMade.replace("D D ", "D2 ");
        movesMade = movesMade.replace("D' D'", "D2");
        movesMade = movesMade.replace("F F ", "F2 ");
        movesMade = movesMade.replace("F' F'", "F2");
        movesMade = movesMade.replace("B B ", "B2 ");
        movesMade = movesMade.replace("B' B'", "B2");
        movesMade = movesMade.replace("E E ", "E2 ");
        movesMade = movesMade.replace("E' E'", "E2");
        movesMade = movesMade.replace("M M ", "M2 ");
        movesMade = movesMade.replace("M' M'", "M2");
        movesMade = movesMade.replace("S S ", "S2 ");
        movesMade = movesMade.replace("S' S'", "S2");

        movesMade = movesMade.replace("R R2", "R'");
        movesMade = movesMade.replace("R2 R ", "R' ");
        movesMade = movesMade.replace("L L2", "L'");
        movesMade = movesMade.replace("L2 L ", "L' ");
        movesMade = movesMade.replace("U U2", "U'");
        movesMade = movesMade.replace("U2 U ", "U' ");
        movesMade = movesMade.replace("D D2", "D'");
        movesMade = movesMade.replace("D2 D ", "D' ");
        movesMade = movesMade.replace("F F2", "F'");
        movesMade = movesMade.replace("F2 F ", "F' ");
        movesMade = movesMade.replace("B B2", "B'");
        movesMade = movesMade.replace("B2 B ", "B' ");
        movesMade = movesMade.replace("E E2", "E'");
        movesMade = movesMade.replace("E2 E ", "E'");
        movesMade = movesMade.replace("M M2", "M'");
        movesMade = movesMade.replace("M2 M ", "M' ");
        movesMade = movesMade.replace("S S2", "S'");
        movesMade = movesMade.replace("S2 S ", "S' ");

        movesMade = movesMade.replace("R' R2", "R");
        movesMade = movesMade.replace("R2 R'", "R");
        movesMade = movesMade.replace("L' L2", "L");
        movesMade = movesMade.replace("L2 L'", "L");
        movesMade = movesMade.replace("U' U2", "U");
        movesMade = movesMade.replace("U2 U'", "U");
        movesMade = movesMade.replace("D' D2", "D");
        movesMade = movesMade.replace("D2 D'", "D");
        movesMade = movesMade.replace("F' F2", "F");
        movesMade = movesMade.replace("F2 F'", "F");
        movesMade = movesMade.replace("B' B2", "B");
        movesMade = movesMade.replace("B2 B'", "B");
        movesMade = movesMade.replace("E' E2", "E");
        movesMade = movesMade.replace("E2 E'", "E");
        movesMade = movesMade.replace("M' M2", "M");
        movesMade = movesMade.replace("M2 M'", "M");
        movesMade = movesMade.replace("S' S2", "S");
        movesMade = movesMade.replace("S2 S'", "S");
    }
}
