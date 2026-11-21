/*
Solver using the lookup file
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Rubik2x2Solver {
    static final int WHITE = 0b000;
    static final int YELLOW = 0b001;
    static final int BLUE = 0b010;
    static final int GREEN = 0b011;
    static final int RED = 0b100;
    static final int ORANGE = 0b101;

    static int faceU = 0b001_011_100_000;
    static int faceD = 0b010_000_101_001;
    static int faceR = 0b010_000_011_011;
    static int faceL = 0b101_001_010_000;
    static int faceF = 0b010_100_101_100;
    static int faceB = 0b101_011_100_001;

    static List<int[]> faceStates = new ArrayList<>();
    static List<String> movesSolvedToScrambled = new ArrayList<>();

    static HashMap<String, String> reverseOfMoves = new HashMap<>();
    static HashMap<Integer, String> bitsToColorMap = new HashMap<>();
    static HashMap<Character, String> charToColorMap = new HashMap<>();
    static HashMap<Character, Integer> charColorToBits = new HashMap<>();

    static char[] userInputCube = new char[24];
    static int currentInputIndex = 0;

    public static void main(String[] args) {
        System.out.println("Loading Solver File...");
        File myObj = new File("rubik2x2lookup.txt");

        try (Scanner myReader = new Scanner(myObj)) {
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                parseFileInputState(data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        initMoveReverseMap();
        initColorMap();

        getUserInput();
        convertUserInputToFaces();

        displayCube();
        System.out.println("\u001B[37m" +
                setCananicalStateInput() + ", to set the Yellow-Blue-Red corner on the U-L-F face respectively");
        displayCube();
        int index = getIndexOfState();
        System.out.println(index + " " + reverseMovesToSolveAlg(movesSolvedToScrambled.get(index)));
    }

    static void getUserInput() {
        Scanner scanner = new Scanner(System.in);
        while (currentInputIndex < 24) {
            displayInputCube();
            System.out.println("Enter a color for the current position ('u' to undo):");
            String input = scanner.nextLine();
            if (input.toUpperCase().charAt(0) == 'U') {
                currentInputIndex--;
                continue;
            }
            if (input.toUpperCase().charAt(0) != 'W' && input.toUpperCase().charAt(0) != 'Y'
                    && input.toUpperCase().charAt(0) != 'B' && input.toUpperCase().charAt(0) != 'G'
                    && input.toUpperCase().charAt(0) != 'R' && input.toUpperCase().charAt(0) != 'O') {
                continue;
            }
            userInputCube[currentInputIndex++] = input.toUpperCase().charAt(0);
        }
        scanner.close();
    }

    static void convertUserInputToFaces() {
        faceU = (charColorToBits.get(userInputCube[0]) << 9) | (charColorToBits.get(userInputCube[1]) << 6)
                | (charColorToBits.get(userInputCube[2]) << 3) | charColorToBits.get(userInputCube[3]);
        faceL = (charColorToBits.get(userInputCube[4]) << 9) | (charColorToBits.get(userInputCube[5]) << 6)
                | (charColorToBits.get(userInputCube[6]) << 3) | charColorToBits.get(userInputCube[7]);
        faceF = (charColorToBits.get(userInputCube[8]) << 9) | (charColorToBits.get(userInputCube[9]) << 6)
                | (charColorToBits.get(userInputCube[10]) << 3) | charColorToBits.get(userInputCube[11]);
        faceR = (charColorToBits.get(userInputCube[12]) << 9) | (charColorToBits.get(userInputCube[13]) << 6)
                | (charColorToBits.get(userInputCube[14]) << 3) | charColorToBits.get(userInputCube[15]);
        faceB = (charColorToBits.get(userInputCube[16]) << 9) | (charColorToBits.get(userInputCube[17]) << 6)
                | (charColorToBits.get(userInputCube[18]) << 3) | charColorToBits.get(userInputCube[19]);
        faceD = (charColorToBits.get(userInputCube[20]) << 9) | (charColorToBits.get(userInputCube[21]) << 6)
                | (charColorToBits.get(userInputCube[22]) << 3) | charColorToBits.get(userInputCube[23]);
    }

    static void displayInputCube() {
        if (currentInputIndex <= 0) {
            System.out.println("  _");
        } else if (currentInputIndex <= 1) {
            System.out.println("  " + getColor(userInputCube[0]) + "_");
        } else if (currentInputIndex <= 2) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  _");
        } else if (currentInputIndex <= 3) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + "_");
        } else if (currentInputIndex <= 4) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println("_");
        } else if (currentInputIndex <= 5) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(getColor(userInputCube[4]) + "_");
        } else if (currentInputIndex <= 6) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(getColor(userInputCube[4]) + getColor(userInputCube[5]));
            System.out.println("_");
        } else if (currentInputIndex <= 7) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(getColor(userInputCube[4]) + getColor(userInputCube[5]));
            System.out.println(getColor(userInputCube[6]) + "_");
        } else if (currentInputIndex <= 8) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(getColor(userInputCube[4]) + getColor(userInputCube[5]) + "_");
            System.out.println(getColor(userInputCube[6]) + getColor(userInputCube[7]));
        } else if (currentInputIndex <= 9) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8]) + "_");
            System.out.println(getColor(userInputCube[6]) + getColor(userInputCube[7]));
        } else if (currentInputIndex <= 10) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]));
            System.out.println(getColor(userInputCube[6]) + getColor(userInputCube[7]) + "_");
        } else if (currentInputIndex <= 11) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10]) + "_");
        } else if (currentInputIndex <= 12) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + "_");
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]));
        } else if (currentInputIndex <= 13) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + "_");
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]));
        } else if (currentInputIndex <= 14) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + "_");
        } else if (currentInputIndex <= 15) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + "_");
        } else if (currentInputIndex <= 16) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + "_");
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15]));
        } else if (currentInputIndex <= 17) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + "_");
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15]));
        } else if (currentInputIndex <= 18) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + getColor(userInputCube[17]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15])
                            + "_");
        } else if (currentInputIndex <= 19) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + getColor(userInputCube[17]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15])
                            + getColor(userInputCube[18]) + "_");
        } else if (currentInputIndex <= 20) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + getColor(userInputCube[17]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15])
                            + getColor(userInputCube[18]) + getColor(userInputCube[19]));
            System.out.println("  _");
        } else if (currentInputIndex <= 21) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + getColor(userInputCube[17]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15])
                            + getColor(userInputCube[18]) + getColor(userInputCube[19]));
            System.out.println("  " + getColor(userInputCube[20]) + "_");
        } else if (currentInputIndex <= 22) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + getColor(userInputCube[17]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15])
                            + getColor(userInputCube[18]) + getColor(userInputCube[19]));
            System.out.println("  " + getColor(userInputCube[20]) + getColor(userInputCube[21]));
            System.out.println("  _");
        } else if (currentInputIndex <= 23) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + getColor(userInputCube[17]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15])
                            + getColor(userInputCube[18]) + getColor(userInputCube[19]));
            System.out.println("  " + getColor(userInputCube[20]) + getColor(userInputCube[21]));
            System.out.println("  " + getColor(userInputCube[22]) + "_");
        } else if (currentInputIndex <= 24) {
            System.out.println("  " + getColor(userInputCube[0]) + getColor(userInputCube[1]));
            System.out.println("  " + getColor(userInputCube[2]) + getColor(userInputCube[3]));
            System.out.println(
                    getColor(userInputCube[4]) + getColor(userInputCube[5]) + getColor(userInputCube[8])
                            + getColor(userInputCube[9]) + getColor(userInputCube[12]) + getColor(userInputCube[13])
                            + getColor(userInputCube[16]) + getColor(userInputCube[17]));
            System.out.println(
                    getColor(userInputCube[6]) + getColor(userInputCube[7]) + getColor(userInputCube[10])
                            + getColor(userInputCube[11]) + getColor(userInputCube[14]) + getColor(userInputCube[15])
                            + getColor(userInputCube[18]) + getColor(userInputCube[19]));
            System.out.println("  " + getColor(userInputCube[20]) + getColor(userInputCube[21]));
            System.out.println("  " + getColor(userInputCube[22]) + getColor(userInputCube[23]));
        }
        System.out.println("\u001B[37m");
    }

    static void initMoveReverseMap() {
        reverseOfMoves.put("U", "U'");
        reverseOfMoves.put("U'", "U");
        reverseOfMoves.put("U2", "U2");
        reverseOfMoves.put("D", "D'");
        reverseOfMoves.put("D'", "D");
        reverseOfMoves.put("D2", "D2");
        reverseOfMoves.put("R", "R'");
        reverseOfMoves.put("R'", "R");
        reverseOfMoves.put("R2", "R2");
        reverseOfMoves.put("L", "L'");
        reverseOfMoves.put("L'", "L");
        reverseOfMoves.put("L2", "L2");
        reverseOfMoves.put("F", "F'");
        reverseOfMoves.put("F'", "F");
        reverseOfMoves.put("F2", "F2");
        reverseOfMoves.put("B", "B'");
        reverseOfMoves.put("B'", "B");
        reverseOfMoves.put("B2", "B2");
        reverseOfMoves.put("x", "x'");
        reverseOfMoves.put("x'", "x");
        reverseOfMoves.put("x2", "x2");
        reverseOfMoves.put("y", "y'");
        reverseOfMoves.put("y'", "y");
        reverseOfMoves.put("y2", "y2");
        reverseOfMoves.put("z", "z'");
        reverseOfMoves.put("z'", "z");
        reverseOfMoves.put("z2", "z2");
        reverseOfMoves.put(" ", "");
        reverseOfMoves.put("", "");
    }

    static void initColorMap() {
        bitsToColorMap.put(WHITE, "\u001B[37mW");
        bitsToColorMap.put(YELLOW, "\u001B[33mY");
        bitsToColorMap.put(BLUE, "\u001B[34mB");
        bitsToColorMap.put(GREEN, "\u001B[32mG");
        bitsToColorMap.put(RED, "\u001B[31mR");
        bitsToColorMap.put(ORANGE, "\033[38;2;255;125;0mO");

        charToColorMap.put('W', "\u001B[37mW");
        charToColorMap.put('Y', "\u001B[33mY");
        charToColorMap.put('B', "\u001B[34mB");
        charToColorMap.put('G', "\u001B[32mG");
        charToColorMap.put('R', "\u001B[31mR");
        charToColorMap.put('O', "\033[38;2;255;125;0mO");

        charColorToBits.put('W', WHITE);
        charColorToBits.put('Y', YELLOW);
        charColorToBits.put('B', BLUE);
        charColorToBits.put('G', GREEN);
        charColorToBits.put('R', RED);
        charColorToBits.put('O', ORANGE);
    }

    static void displayCube() {
        int counter = 0;

        // Display U Face
        System.out.print("\n\n  ");
        for (int i = 3; i >= 0; i--) {
            if (counter % 2 == 0 && counter > 0) {
                System.out.print("\n  ");
            }
            System.out.print(getColor(faceU, i));
            counter++;
        }

        // Display LFRB Face
        counter = 3;// Row 1
        System.out.print("\n" +
                getColor(faceL, counter) + getColor(faceL, counter - 1)
                + getColor(faceF, counter) + getColor(faceF, counter - 1)
                + getColor(faceR, counter) + getColor(faceR, counter - 1)
                + getColor(faceB, counter) + getColor(faceB, counter - 1));
        counter = 1; // Row 3
        System.out.print("\n" +
                getColor(faceL, counter) + getColor(faceL, counter - 1)
                + getColor(faceF, counter) + getColor(faceF, counter - 1)
                + getColor(faceR, counter) + getColor(faceR, counter - 1)
                + getColor(faceB, counter) + getColor(faceB, counter - 1));

        // Display D Face
        counter = 0;
        System.out.print("\n  ");
        for (int i = 3; i >= 0; i--) {
            if (counter % 2 == 0 && counter > 0) {
                System.out.print("\n  ");
            }
            System.out.print(getColor(faceD, i));
            counter++;
        }
        System.out.println();
    }

    static String getColor(int face, int shiftAmount) {
        return bitsToColorMap.get((face >> (3 * shiftAmount)) & 0b111);
    }

    static String getColor(char color) {
        return charToColorMap.get(color);
    }

    /**
     * Creates the faces of the cubes
     * Get the array of moves
     * 
     * @param line
     */
    static void parseFileInputState(String line) {
        String[] splitLine = line.split(" ");
        // First 6 elements are cube faces in order U,D,R,L,F,B
        faceStates.add(new int[] { Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[1]),
                Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]), Integer.parseInt(splitLine[4]),
                Integer.parseInt(splitLine[5]) });
        String moves = "";
        for (int i = 7; i < splitLine.length; i++) {
            if (splitLine[i] != " ") {
                moves += " " + splitLine[i];
            }
        }
        movesSolvedToScrambled.add(moves);
    }

    /**
     *
     */
    static int getIndexOfState() {
        int length = faceStates.size();
        for (int i = 0; i < length; i++) {
            if (faceU == faceStates.get(i)[0] && faceD == faceStates.get(i)[1]
                    && faceR == faceStates.get(i)[2] && faceL == faceStates.get(i)[3]
                    && faceF == faceStates.get(i)[4] && faceB == faceStates.get(i)[5]) {
                return i;
            }
        }
        return -1;
    }

    static String reverseMovesToSolveAlg(String movesToReverse) {
        String[] splitMoves = movesToReverse.split(" ");
        String reversedMoves = "";
        for (int i = splitMoves.length - 1; i >= 0; i--) {
            reversedMoves += " " + reverseOfMoves.get(splitMoves[i]);
        }
        return reversedMoves;
    }

    /**
     * Allow the lookup table to store only 1 of 24 possible same cube states
     * 
     * Setting the Yellow-Blue-Red Corner in U-L-F Corner
     */
    static String setCananicalStateInput() {
        if (((faceU >> 3) & 0b111) == YELLOW && ((faceL >> 6) & 0b111) == BLUE
                && ((faceF >> 9) & 0b111) == RED) {
            return "";
        }
        // Yellow on faceU
        else if (((faceU >> 9) & 0b111) == YELLOW && ((faceB >> 6) & 0b111) == BLUE
                && ((faceL >> 9) & 0b111) == RED) {
            yi();
            return "y'";
        } else if (((faceU >> 6) & 0b111) == YELLOW && ((faceR >> 6) & 0b111) == BLUE
                && ((faceB >> 9) & 0b111) == RED) {
            y2();
            return "y2";
        } else if ((faceU & 0b111) == YELLOW && ((faceF >> 6) & 0b111) == BLUE
                && ((faceR >> 9) & 0b111) == RED) {
            y();
            return "y";
        }
        // Yellow on faceL
        else if (((faceL >> 9) & 0b111) == YELLOW && ((faceU >> 9) & 0b111) == BLUE
                && ((faceB >> 6) & 0b111) == RED) {
            z();
            y2();
            return "z y2";
        } else if (((faceL >> 6) & 0b111) == YELLOW && ((faceF >> 9) & 0b111) == BLUE
                && ((faceU >> 3) & 0b111) == RED) {
            z();
            y();
            return "z y";
        } else if (((faceL >> 3) & 0b111) == YELLOW && (faceB & 0b111) == BLUE
                && ((faceD >> 3) & 0b111) == RED) {
            z();
            yi();
            return "z y'";
        } else if ((faceL & 0b111) == YELLOW && ((faceD >> 9) & 0b111) == BLUE
                && ((faceF >> 3) & 0b111) == RED) {
            z();
            return "z";
        }
        // Yellow on faceF
        else if (((faceF >> 9) & 0b111) == YELLOW && ((faceU >> 3) & 0b111) == BLUE
                && ((faceL >> 6) & 0b111) == RED) {
            x();
            yi();
            return "x y'";
        } else if (((faceF >> 6) & 0b111) == YELLOW && ((faceR >> 9) & 0b111) == BLUE
                && (faceU & 0b111) == RED) {
            x();
            y2();
            return "x y2";
        } else if (((faceF >> 3) & 0b111) == YELLOW && (faceL & 0b111) == BLUE
                && ((faceD >> 9) & 0b111) == RED) {
            x();
            return "x";
        } else if ((faceF & 0b111) == YELLOW && ((faceD >> 6) & 0b111) == BLUE
                && ((faceR >> 3) & 0b111) == RED) {
            x();
            y();
            return "x y";
        }
        // Yellow on faceR
        else if (((faceR >> 9) & 0b111) == YELLOW && (faceU & 0b111) == BLUE
                && ((faceF >> 6) & 0b111) == RED) {
            zi();
            return "z'";
        } else if (((faceR >> 6) & 0b111) == YELLOW && ((faceB >> 9) & 0b111) == BLUE
                && ((faceU >> 6) & 0b111) == RED) {
            zi();
            yi();
            return "z' y'";
        } else if (((faceR >> 3) & 0b111) == YELLOW && (faceF & 0b111) == BLUE
                && ((faceD >> 6) & 0b111) == RED) {
            zi();
            y();
            return "z' y";
        } else if ((faceR & 0b111) == YELLOW && (faceD & 0b111) == BLUE
                && ((faceB >> 3) & 0b111) == RED) {
            zi();
            y2();
            return "z' y2";
        }
        // Yellow on faceB
        else if (((faceB >> 9) & 0b111) == YELLOW && ((faceU >> 6) & 0b111) == BLUE
                && ((faceR >> 6) & 0b111) == RED) {
            xi();
            y();
            return "x' y";
        } else if (((faceB >> 6) & 0b111) == YELLOW && ((faceL >> 9) & 0b111) == BLUE
                && ((faceU >> 9) & 0b111) == RED) {
            xi();
            return "x'";
        } else if (((faceB >> 3) & 0b111) == YELLOW && (faceR & 0b111) == BLUE
                && (faceD & 0b111) == RED) {
            xi();
            y2();
            return "x' y2";
        } else if ((faceB & 0b111) == YELLOW && ((faceD >> 3) & 0b111) == BLUE
                && ((faceL >> 3) & 0b111) == RED) {
            xi();
            yi();
            return "x' y'";
        }
        // Yellow on faceD
        else if (((faceD >> 9) & 0b111) == YELLOW && ((faceF >> 3) & 0b111) == BLUE
                && (faceL & 0b111) == RED) {
            z2();
            y();
            return "z2 y";
        } else if (((faceD >> 6) & 0b111) == YELLOW && ((faceR >> 3) & 0b111) == BLUE
                && (faceF & 0b111) == RED) {
            z2();
            return "z2";
        } else if (((faceD >> 3) & 0b111) == YELLOW && ((faceL >> 3) & 0b111) == BLUE
                && (faceB & 0b111) == RED) {
            z2();
            y2();
            return "z2 y2";
        } else if ((faceD & 0b111) == YELLOW && ((faceB >> 3) & 0b111) == BLUE
                && (faceR & 0b111) == RED) {
            z2();
            yi();
            return "z2 y'";
        }
        return "";
    }

    static void x() {
        int temp = faceU;
        faceR = ((faceR >> 3) & 0b000_111_000_000) | ((faceR >> 6) & 0b000_000_000_111)
                | ((faceR << 3) & 0b000_000_111_000) | ((faceR << 6) & 0b111_000_000_000);
        faceL = ((faceL >> 6) & 0b000_000_111_000) | ((faceL << 3) & 0b111_000_000_000)
                | ((faceL << 6) & 0b000_111_000_000) | ((faceL >> 3) & 0b000_000_000_111);
        faceU = faceF;
        faceF = faceD;
        faceD = ((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                | ((faceB << 9) & 0b111_000_000_000) | ((faceB << 3) & 0b000_111_000_000);
        faceB = ((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000);
    }

    static void xi() {
        int temp = faceU;
        faceR = ((faceR >> 6) & 0b000_000_111_000) | ((faceR << 3) & 0b111_000_000_000)
                | ((faceR << 6) & 0b000_111_000_000) | ((faceR >> 3) & 0b000_000_000_111);
        faceL = ((faceL >> 3) & 0b000_111_000_000) | ((faceL >> 6) & 0b000_000_000_111)
                | ((faceL << 3) & 0b000_000_111_000) | ((faceL << 6) & 0b111_000_000_000);
        faceU = ((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                | ((faceB << 9) & 0b111_000_000_000) | ((faceB << 3) & 0b000_111_000_000);
        faceB = ((faceD >> 9) & 0b000_000_000_111) | ((faceD >> 3) & 0b000_000_111_000)
                | ((faceD << 9) & 0b111_000_000_000) | ((faceD << 3) & 0b000_111_000_000);
        faceD = faceF;
        faceF = temp;
    }

    static void x2() {
        int temp = faceU;
        faceR = ((faceR >> 9) & 0b000_000_000_111) | ((faceR >> 3) & 0b000_000_111_000)
                | ((faceR << 9) & 0b111_000_000_000) | ((faceR << 3) & 0b000_111_000_000);
        faceL = ((faceL >> 9) & 0b000_000_000_111) | ((faceL >> 3) & 0b000_000_111_000)
                | ((faceL << 9) & 0b111_000_000_000) | ((faceL << 3) & 0b000_111_000_000);
        faceU = faceD;
        faceD = temp;
        temp = faceF;
        faceF = ((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                | ((faceB << 9) & 0b111_000_000_000) | ((faceB << 3) & 0b000_111_000_000);
        faceB = ((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000);
    }

    static void z() {
        int temp = faceU;
        faceF = ((faceF >> 3) & 0b000_111_000_000) | ((faceF >> 6) & 0b000_000_000_111)
                | ((faceF << 3) & 0b000_000_111_000) | ((faceF << 6) & 0b111_000_000_000);
        faceB = ((faceB >> 6) & 0b000_000_111_000) | ((faceB << 3) & 0b111_000_000_000)
                | ((faceB << 6) & 0b000_111_000_000) | ((faceB >> 3) & 0b000_000_000_111);
        faceU = ((faceL >> 3) & 0b000_111_000_000) | ((faceL >> 6) & 0b000_000_000_111)
                | ((faceL << 3) & 0b000_000_111_000) | ((faceL << 6) & 0b111_000_000_000);
        faceL = ((faceD >> 3) & 0b000_111_000_000) | ((faceD >> 6) & 0b000_000_000_111)
                | ((faceD << 3) & 0b000_000_111_000) | ((faceD << 6) & 0b111_000_000_000);
        faceD = ((faceR >> 3) & 0b000_111_000_000) | ((faceR >> 6) & 0b000_000_000_111)
                | ((faceR << 3) & 0b000_000_111_000) | ((faceR << 6) & 0b111_000_000_000);
        faceR = ((temp >> 3) & 0b000_111_000_000) | ((temp >> 6) & 0b000_000_000_111)
                | ((temp << 3) & 0b000_000_111_000) | ((temp << 6) & 0b111_000_000_000);
    }

    static void zi() {
        int temp = faceU;
        faceF = ((faceF >> 6) & 0b000_000_111_000) | ((faceF << 3) & 0b111_000_000_000)
                | ((faceF << 6) & 0b000_111_000_000) | ((faceF >> 3) & 0b000_000_000_111);
        faceB = ((faceB >> 3) & 0b000_111_000_000) | ((faceB >> 6) & 0b000_000_000_111)
                | ((faceB << 3) & 0b000_000_111_000) | ((faceB << 6) & 0b111_000_000_000);
        faceU = ((faceR >> 6) & 0b000_000_111_000) | ((faceR << 3) & 0b111_000_000_000)
                | ((faceR << 6) & 0b000_111_000_000) | ((faceR >> 3) & 0b000_000_000_111);
        faceR = ((faceD >> 6) & 0b000_000_111_000) | ((faceD << 3) & 0b111_000_000_000)
                | ((faceD << 6) & 0b000_111_000_000) | ((faceD >> 3) & 0b000_000_000_111);
        faceD = ((faceL >> 6) & 0b000_000_111_000) | ((faceL << 3) & 0b111_000_000_000)
                | ((faceL << 6) & 0b000_111_000_000) | ((faceL >> 3) & 0b000_000_000_111);
        faceL = ((temp >> 6) & 0b000_000_111_000) | ((temp << 3) & 0b111_000_000_000)
                | ((temp << 6) & 0b000_111_000_000) | ((temp >> 3) & 0b000_000_000_111);
    }

    static void z2() {
        int temp = faceU;
        faceF = ((faceF >> 9) & 0b000_000_000_111) | ((faceF >> 3) & 0b000_000_111_000)
                | ((faceF << 9) & 0b111_000_000_000) | ((faceF << 3) & 0b000_111_000_000);
        faceB = ((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                | ((faceB << 9) & 0b111_000_000_000) | ((faceB << 3) & 0b000_111_000_000);
        faceU = ((faceD >> 9) & 0b000_000_000_111) | ((faceD >> 3) & 0b000_000_111_000)
                | ((faceD << 9) & 0b111_000_000_000) | ((faceD << 3) & 0b000_111_000_000);
        faceD = ((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000);
        temp = faceR;
        faceR = ((faceL >> 9) & 0b000_000_000_111) | ((faceL >> 3) & 0b000_000_111_000)
                | ((faceL << 9) & 0b111_000_000_000) | ((faceL << 3) & 0b000_111_000_000);
        faceL = ((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000);
    }

    static void y() {
        int temp = faceF;
        faceU = ((faceU >> 3) & 0b000_111_000_000) | ((faceU >> 6) & 0b000_000_000_111)
                | ((faceU << 3) & 0b000_000_111_000) | ((faceU << 6) & 0b111_000_000_000);
        faceD = ((faceD >> 6) & 0b000_000_111_000) | ((faceD << 3) & 0b111_000_000_000)
                | ((faceD << 6) & 0b000_111_000_000) | ((faceD >> 3) & 0b000_000_000_111);
        faceF = faceR;
        faceR = faceB;
        faceB = faceL;
        faceL = temp;
    }

    static void yi() {
        int temp = faceF;
        faceU = ((faceU >> 6) & 0b000_000_111_000) | ((faceU << 3) & 0b111_000_000_000)
                | ((faceU << 6) & 0b000_111_000_000) | ((faceU >> 3) & 0b000_000_000_111);
        faceD = ((faceD >> 3) & 0b000_111_000_000) | ((faceD >> 6) & 0b000_000_000_111)
                | ((faceD << 3) & 0b000_000_111_000) | ((faceD << 6) & 0b111_000_000_000);
        faceF = faceL;
        faceL = faceB;
        faceB = faceR;
        faceR = temp;
    }

    static void y2() {
        int temp = faceF;
        faceU = ((faceU >> 9) & 0b000_000_000_111) | ((faceU >> 3) & 0b000_000_111_000)
                | ((faceU << 9) & 0b111_000_000_000) | ((faceU << 3) & 0b000_111_000_000);
        faceD = ((faceD >> 9) & 0b000_000_000_111) | ((faceD >> 3) & 0b000_000_111_000)
                | ((faceD << 9) & 0b111_000_000_000) | ((faceD << 3) & 0b000_111_000_000);
        faceF = faceB;
        faceB = temp;
        temp = faceR;
        faceR = faceL;
        faceL = temp;
    }

}
