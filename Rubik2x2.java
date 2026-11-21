import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Rubik2x2 {
        static final int WHITE = 0b000;
        static final int YELLOW = 0b001;
        static final int BLUE = 0b010;
        static final int GREEN = 0b011;
        static final int RED = 0b100;
        static final int ORANGE = 0b101;

        static final int KEEP_ROW_1_MASK = 0b111_111_000_000;
        static final int CLEAR_ROW_1_MASK = 0b000_000_111_111;
        static final int KEEP_ROW_2_MASK = 0b000_000_111_111;
        static final int CLEAR_ROW_2_MASK = 0b111_111_000_000;
        static final int KEEP_COL_1_MASK = 0b111_000_111_000;
        static final int CLEAR_COL_1_MASK = 0b000_111_000_111;
        static final int KEEP_COL_2_MASK = 0b000_111_000_111;
        static final int CLEAR_COL_2_MASK = 0b111_000_111_000;
        static final int[][] stickerMasks = {
                        { 0b111_000_000_000, 0b000_111_000_000 },
                        { 0b000_000_111_000, 0b000_000_000_111 }, };
        static final Map<Integer, Integer> stickerToShifts = Map.of(stickerMasks[0][0], 3, stickerMasks[0][1], 2,
                        stickerMasks[1][0], 1, stickerMasks[1][1], 0);

        static int faceU = 0b001_001_001_001;// 0
        static int faceD = 0b000_000_000_000;// 1
        static int faceR = 0b011_011_011_011;// 2
        static int faceL = 0b010_010_010_010;// 3
        static int faceF = 0b100_100_100_100;// 4
        static int faceB = 0b101_101_101_101;// 5

        static int[] cube = { faceU, faceD, faceR, faceL, faceF, faceB }; // Shallow references throughout the code

        static HashMap<Integer, String> bitsToColorMap = new HashMap<>();

        static String[] allMovesList = {
                        "U", "U2", "U'",
                        "D", "D2", "D'",
                        "R", "R2", "R'",
                        "L", "L2", "L'",
                        "F", "F2", "F'",
                        "B", "B2", "B'",
                        // "x", "x2", "x'",
                        // "z", "z2", "z'",
                        // "y", "y2", "y'",
        };

        static String parentMoves = "";
        static HashSet<String> canonStates = new HashSet<>();
        static Queue<String[]> cubeStates = new ArrayDeque<>();

        public static void main(String[] args) {
                initColorMap();
                // long alltime = 0l;
                // long startTime = System.nanoTime();
                // for (long i = 0; i < 1000000000l; i++) { // Original 1000000000l, 4136, 4116,
                // 4168, 4156 millis,
                // // Optimized: 3357
                // U();
                // }
                // long endTime = System.nanoTime();
                // System.out.println((endTime - startTime) / 1000000);

                FileWriter myWriter;
                try {
                        myWriter = new FileWriter("rubik2x2lookup.txt", false);
                        writeToFile(turnStateToString(), "", myWriter);
                        generateLookupTable(turnStateToString(), "", myWriter);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        /**
         * BUG: Missing 2-3 states
         * 
         * @param state
         * @param parentMoves
         * @param level
         */
        static void generateLookupTable(String state, String parentMoves, FileWriter writer) {
                canonStates.add(state);
                cubeStates.add(new String[] { state, parentMoves, "" + 0 }); // String state, moves
                while (!cubeStates.isEmpty()) {
                        // Breadth first search method
                        String[] currentCube = cubeStates.poll();
                        int depth = Integer.parseInt(currentCube[2]);
                        System.out.println(depth);
                        if (depth >= 11) {
                                continue;
                        }

                        turnStringToState(currentCube[0]); // Reset to parent state
                        U();
                        String currState = turnStateToString();
                        String canonMoves = setCananicalState();
                        String currCanonState = turnStateToString();
                        String currentMoves = currentCube[1] + " U";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        Ui();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " U'";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        U2();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " U2";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        D();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " D";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        Di();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " D'";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        D2();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " D2";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        R();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " R";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        Ri();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " R'";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        R2();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " R2";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        L();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " L";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        Li();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " L'";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        L2();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " L2";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        F();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " F";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        Fi();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " F'";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        F2();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " F2";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        B();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " B";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        Bi();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " B'";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                        turnStringToState(currentCube[0]); // Reset to parent state
                        B2();
                        currState = turnStateToString();
                        canonMoves = setCananicalState();
                        currCanonState = turnStateToString();
                        currentMoves = currentCube[1] + " B2";
                        if (canonStates.add(currCanonState)) {
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new String[] { currState, currentMoves, "" + (depth + 1) });
                        }
                }
        }

        /**
         * Allow the lookup table to store only 1 of 24 possible same cube states
         * 
         * Setting the Yellow-Blue-Red Corner in U-L-F Corner
         */
        static String setCananicalState() {
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

        static String turnStateToString() {
                // U, D, R, L, F, B
                return faceU + " " + faceD + " " + faceR + " " + faceL + " " + faceF + " " + faceB;
        }

        static void turnStringToState(String state) {
                String[] faceStates = state.split(" ");
                faceU = Integer.parseInt(faceStates[0]);
                faceD = Integer.parseInt(faceStates[1]);
                faceR = Integer.parseInt(faceStates[2]);
                faceL = Integer.parseInt(faceStates[3]);
                faceF = Integer.parseInt(faceStates[4]);
                faceB = Integer.parseInt(faceStates[5]);
        }

        static void writeToFile(String state, String moves, FileWriter writer) {
                try {
                        writer.write(state + " " + moves + "\n");
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        static void initColorMap() {
                bitsToColorMap.put(WHITE, "\u001B[37mW");
                bitsToColorMap.put(YELLOW, "\u001B[33mY");
                bitsToColorMap.put(BLUE, "\u001B[34mB");
                bitsToColorMap.put(GREEN, "\u001B[32mG");
                bitsToColorMap.put(RED, "\u001B[31mR");
                bitsToColorMap.put(ORANGE, "\033[38;2;255;125;0mO");
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

        /*
         * Splitting up move functions to speed up runtime
         */
        static void U() {
                int temp = faceL;
                faceU = ((faceU >> 3) & 0b000_111_000_000) | ((faceU >> 6) & 0b000_000_000_111)
                                | ((faceU << 3) & 0b000_000_111_000) | ((faceU << 6) & 0b111_000_000_000);
                faceL = (faceL & CLEAR_ROW_1_MASK) | (faceF & KEEP_ROW_1_MASK);
                faceF = (faceF & CLEAR_ROW_1_MASK) | (faceR & KEEP_ROW_1_MASK);
                faceR = (faceR & CLEAR_ROW_1_MASK) | (faceB & KEEP_ROW_1_MASK);
                faceB = (faceB & CLEAR_ROW_1_MASK) | (temp & KEEP_ROW_1_MASK);
        }

        static void Ui() {
                int temp = faceL;
                faceU = ((faceU >> 6) & 0b000_000_111_000) | ((faceU << 3) & 0b111_000_000_000)
                                | ((faceU << 6) & 0b000_111_000_000) | ((faceU >> 3) & 0b000_000_000_111);
                faceL = (faceL & CLEAR_ROW_1_MASK) | (faceB & KEEP_ROW_1_MASK);
                faceB = (faceB & CLEAR_ROW_1_MASK) | (faceR & KEEP_ROW_1_MASK);
                faceR = (faceR & CLEAR_ROW_1_MASK) | (faceF & KEEP_ROW_1_MASK);
                faceF = (faceF & CLEAR_ROW_1_MASK) | (temp & KEEP_ROW_1_MASK);
        }

        static void U2() {
                int temp = faceL;
                faceU = ((faceU >> 9) & 0b000_000_000_111) | ((faceU >> 3) & 0b000_000_111_000)
                                | ((faceU << 9) & 0b111_000_000_000) | ((faceU << 3) & 0b000_111_000_000);
                faceL = (faceL & CLEAR_ROW_1_MASK) | (faceR & KEEP_ROW_1_MASK);
                faceR = (faceR & CLEAR_ROW_1_MASK) | (temp & KEEP_ROW_1_MASK);
                temp = faceF;
                faceF = (faceF & CLEAR_ROW_1_MASK) | (faceB & KEEP_ROW_1_MASK);
                faceB = (faceB & CLEAR_ROW_1_MASK) | (temp & KEEP_ROW_1_MASK);
        }

        static void D() {
                int temp = faceL;
                faceD = ((faceD >> 3) & 0b000_111_000_000) | ((faceD >> 6) & 0b000_000_000_111)
                                | ((faceD << 3) & 0b000_000_111_000) | ((faceD << 6) & 0b111_000_000_000);
                faceL = (faceL & CLEAR_ROW_2_MASK) | (faceB & KEEP_ROW_2_MASK);
                faceB = (faceB & CLEAR_ROW_2_MASK) | (faceR & KEEP_ROW_2_MASK);
                faceR = (faceR & CLEAR_ROW_2_MASK) | (faceF & KEEP_ROW_2_MASK);
                faceF = (faceF & CLEAR_ROW_2_MASK) | (temp & KEEP_ROW_2_MASK);
        }

        static void Di() {
                int temp = faceL;
                faceD = ((faceD >> 6) & 0b000_000_111_000) | ((faceD << 3) & 0b111_000_000_000)
                                | ((faceD << 6) & 0b000_111_000_000) | ((faceD >> 3) & 0b000_000_000_111);
                faceL = (faceL & CLEAR_ROW_2_MASK) | (faceF & KEEP_ROW_2_MASK);
                faceF = (faceF & CLEAR_ROW_2_MASK) | (faceR & KEEP_ROW_2_MASK);
                faceR = (faceR & CLEAR_ROW_2_MASK) | (faceB & KEEP_ROW_2_MASK);
                faceB = (faceB & CLEAR_ROW_2_MASK) | (temp & KEEP_ROW_2_MASK);
        }

        static void D2() {
                int temp = faceL;
                faceD = ((faceD >> 9) & 0b000_000_000_111) | ((faceD >> 3) & 0b000_000_111_000)
                                | ((faceD << 9) & 0b111_000_000_000) | ((faceD << 3) & 0b000_111_000_000);
                faceL = (faceL & CLEAR_ROW_2_MASK) | (faceR & KEEP_ROW_2_MASK);
                faceR = (faceR & CLEAR_ROW_2_MASK) | (temp & KEEP_ROW_2_MASK);
                temp = faceB;
                faceB = (faceB & CLEAR_ROW_2_MASK) | (faceF & KEEP_ROW_2_MASK);
                faceF = (faceF & CLEAR_ROW_2_MASK) | (temp & KEEP_ROW_2_MASK);
        }

        static void R() {
                int temp = faceU;
                faceR = ((faceR >> 3) & 0b000_111_000_000) | ((faceR >> 6) & 0b000_000_000_111)
                                | ((faceR << 3) & 0b000_000_111_000) | ((faceR << 6) & 0b111_000_000_000);
                faceU = (faceU & CLEAR_COL_2_MASK) | (faceF & KEEP_COL_2_MASK);
                faceF = (faceF & CLEAR_COL_2_MASK) | (faceD & KEEP_COL_2_MASK);
                faceD = (faceD & CLEAR_COL_2_MASK)
                                | ((((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                                                | ((faceB << 9) & 0b111_000_000_000)
                                                | ((faceB << 3) & 0b000_111_000_000)) & KEEP_COL_2_MASK);
                faceB = (faceB & CLEAR_COL_1_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_COL_1_MASK);
        }

        static void Ri() {
                int temp = faceU;
                faceR = ((faceR >> 6) & 0b000_000_111_000) | ((faceR << 3) & 0b111_000_000_000)
                                | ((faceR << 6) & 0b000_111_000_000) | ((faceR >> 3) & 0b000_000_000_111);
                faceU = (faceU & CLEAR_COL_2_MASK)
                                | ((((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                                                | ((faceB << 9) & 0b111_000_000_000)
                                                | ((faceB << 3) & 0b000_111_000_000)) & KEEP_COL_2_MASK);
                faceB = (faceB & CLEAR_COL_1_MASK)
                                | ((((faceD >> 9) & 0b000_000_000_111) | ((faceD >> 3) & 0b000_000_111_000)
                                                | ((faceD << 9) & 0b111_000_000_000)
                                                | ((faceD << 3) & 0b000_111_000_000)) & KEEP_COL_1_MASK);
                faceD = (faceD & CLEAR_COL_2_MASK) | (faceF & KEEP_COL_2_MASK);
                faceF = (faceF & CLEAR_COL_2_MASK) | (temp & KEEP_COL_2_MASK);
        }

        static void R2() {
                int temp = faceU;
                faceR = ((faceR >> 9) & 0b000_000_000_111) | ((faceR >> 3) & 0b000_000_111_000)
                                | ((faceR << 9) & 0b111_000_000_000) | ((faceR << 3) & 0b000_111_000_000);
                faceU = (faceU & CLEAR_COL_2_MASK) | (faceD & KEEP_COL_2_MASK);
                faceD = (faceD & CLEAR_COL_2_MASK) | (temp & KEEP_COL_2_MASK);
                temp = faceF;
                faceF = (faceF & CLEAR_COL_2_MASK)
                                | ((((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                                                | ((faceB << 9) & 0b111_000_000_000)
                                                | ((faceB << 3) & 0b000_111_000_000)) & KEEP_COL_2_MASK);
                faceB = (faceB & CLEAR_COL_1_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_COL_1_MASK);
        }

        static void L() {
                int temp = faceD;
                faceL = ((faceL >> 3) & 0b000_111_000_000) | ((faceL >> 6) & 0b000_000_000_111)
                                | ((faceL << 3) & 0b000_000_111_000) | ((faceL << 6) & 0b111_000_000_000);
                faceD = (faceD & CLEAR_COL_1_MASK) | (faceF & KEEP_COL_1_MASK);
                faceF = (faceF & CLEAR_COL_1_MASK) | (faceU & KEEP_COL_1_MASK);
                faceU = (faceU & CLEAR_COL_1_MASK)
                                | ((((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                                                | ((faceB << 9) & 0b111_000_000_000)
                                                | ((faceB << 3) & 0b000_111_000_000)) & KEEP_COL_1_MASK);
                faceB = (faceB & CLEAR_COL_2_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_COL_2_MASK);
        }

        static void Li() {
                int temp = faceD;
                faceL = ((faceL >> 6) & 0b000_000_111_000) | ((faceL << 3) & 0b111_000_000_000)
                                | ((faceL << 6) & 0b000_111_000_000) | ((faceL >> 3) & 0b000_000_000_111);
                faceD = (faceD & CLEAR_COL_1_MASK)
                                | ((((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                                                | ((faceB << 9) & 0b111_000_000_000)
                                                | ((faceB << 3) & 0b000_111_000_000)) & KEEP_COL_1_MASK);
                faceB = (faceB & CLEAR_COL_2_MASK)
                                | ((((faceU >> 9) & 0b000_000_000_111) | ((faceU >> 3) & 0b000_000_111_000)
                                                | ((faceU << 9) & 0b111_000_000_000)
                                                | ((faceU << 3) & 0b000_111_000_000)) & KEEP_COL_2_MASK);
                faceU = (faceU & CLEAR_COL_1_MASK) | (faceF & KEEP_COL_1_MASK);
                faceF = (faceF & CLEAR_COL_1_MASK) | (temp & KEEP_COL_1_MASK);
        }

        static void L2() {
                int temp = faceD;
                faceL = ((faceL >> 9) & 0b000_000_000_111) | ((faceL >> 3) & 0b000_000_111_000)
                                | ((faceL << 9) & 0b111_000_000_000) | ((faceL << 3) & 0b000_111_000_000);
                faceD = (faceD & CLEAR_COL_1_MASK) | (faceU & KEEP_COL_1_MASK);
                faceU = (faceU & CLEAR_COL_1_MASK) | (temp & KEEP_COL_1_MASK);
                temp = faceF;
                faceF = (faceF & CLEAR_COL_1_MASK)
                                | ((((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                                                | ((faceB << 9) & 0b111_000_000_000)
                                                | ((faceB << 3) & 0b000_111_000_000)) & KEEP_COL_1_MASK);
                faceB = (faceB & CLEAR_COL_2_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_COL_2_MASK);
        }

        static void F() {
                int temp = faceU;
                faceF = ((faceF >> 3) & 0b000_111_000_000) | ((faceF >> 6) & 0b000_000_000_111)
                                | ((faceF << 3) & 0b000_000_111_000) | ((faceF << 6) & 0b111_000_000_000);
                faceU = (faceU & CLEAR_ROW_2_MASK)
                                | ((((faceL >> 3) & 0b000_111_000_000) | ((faceL >> 6) & 0b000_000_000_111)
                                                | ((faceL << 3) & 0b000_000_111_000)
                                                | ((faceL << 6) & 0b111_000_000_000)) & KEEP_ROW_2_MASK);
                faceL = (faceL & CLEAR_COL_2_MASK)
                                | ((((faceD >> 3) & 0b000_111_000_000) | ((faceD >> 6) & 0b000_000_000_111)
                                                | ((faceD << 3) & 0b000_000_111_000)
                                                | ((faceD << 6) & 0b111_000_000_000)) & KEEP_COL_2_MASK);
                faceD = (faceD & CLEAR_ROW_1_MASK)
                                | ((((faceR >> 3) & 0b000_111_000_000) | ((faceR >> 6) & 0b000_000_000_111)
                                                | ((faceR << 3) & 0b000_000_111_000)
                                                | ((faceR << 6) & 0b111_000_000_000)) & KEEP_ROW_1_MASK);
                faceR = (faceR & CLEAR_COL_1_MASK)
                                | ((((temp >> 3) & 0b000_111_000_000) | ((temp >> 6) & 0b000_000_000_111)
                                                | ((temp << 3) & 0b000_000_111_000) | ((temp << 6) & 0b111_000_000_000))
                                                & KEEP_COL_1_MASK);
        }

        static void Fi() {
                int temp = faceU;
                faceF = ((faceF >> 6) & 0b000_000_111_000) | ((faceF << 3) & 0b111_000_000_000)
                                | ((faceF << 6) & 0b000_111_000_000) | ((faceF >> 3) & 0b000_000_000_111);
                faceU = (faceU & CLEAR_ROW_2_MASK)
                                | ((((faceR >> 6) & 0b000_000_111_000) | ((faceR << 3) & 0b111_000_000_000)
                                                | ((faceR << 6) & 0b000_111_000_000)
                                                | ((faceR >> 3) & 0b000_000_000_111)) & KEEP_ROW_2_MASK);
                faceR = (faceR & CLEAR_COL_1_MASK)
                                | ((((faceD >> 6) & 0b000_000_111_000) | ((faceD << 3) & 0b111_000_000_000)
                                                | ((faceD << 6) & 0b000_111_000_000)
                                                | ((faceD >> 3) & 0b000_000_000_111)) & KEEP_COL_1_MASK);
                faceD = (faceD & CLEAR_ROW_1_MASK)
                                | ((((faceL >> 6) & 0b000_000_111_000) | ((faceL << 3) & 0b111_000_000_000)
                                                | ((faceL << 6) & 0b000_111_000_000)
                                                | ((faceL >> 3) & 0b000_000_000_111)) & KEEP_ROW_1_MASK);
                faceL = (faceL & CLEAR_COL_2_MASK)
                                | ((((temp >> 6) & 0b000_000_111_000) | ((temp << 3) & 0b111_000_000_000)
                                                | ((temp << 6) & 0b000_111_000_000) | ((temp >> 3) & 0b000_000_000_111))
                                                & KEEP_COL_2_MASK);
        }

        static void F2() {
                int temp = faceU;
                faceF = ((faceF >> 9) & 0b000_000_000_111) | ((faceF >> 3) & 0b000_000_111_000)
                                | ((faceF << 9) & 0b111_000_000_000) | ((faceF << 3) & 0b000_111_000_000);
                faceU = (faceU & CLEAR_ROW_2_MASK)
                                | ((((faceD >> 9) & 0b000_000_000_111) | ((faceD >> 3) & 0b000_000_111_000)
                                                | ((faceD << 9) & 0b111_000_000_000)
                                                | ((faceD << 3) & 0b000_111_000_000)) & KEEP_ROW_2_MASK);
                faceD = (faceD & CLEAR_ROW_1_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_ROW_1_MASK);
                temp = faceL;
                faceL = (faceL & CLEAR_COL_2_MASK)
                                | ((((faceR >> 9) & 0b000_000_000_111) | ((faceR >> 3) & 0b000_000_111_000)
                                                | ((faceR << 9) & 0b111_000_000_000)
                                                | ((faceR << 3) & 0b000_111_000_000)) & KEEP_COL_2_MASK);
                faceR = (faceR & CLEAR_COL_1_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_COL_1_MASK);
        }

        static void B() {
                int temp = faceU;
                faceB = ((faceB >> 3) & 0b000_111_000_000) | ((faceB >> 6) & 0b000_000_000_111)
                                | ((faceB << 3) & 0b000_000_111_000) | ((faceB << 6) & 0b111_000_000_000);
                faceU = (faceU & CLEAR_ROW_1_MASK)
                                | ((((faceR >> 6) & 0b000_000_111_000) | ((faceR << 3) & 0b111_000_000_000)
                                                | ((faceR << 6) & 0b000_111_000_000)
                                                | ((faceR >> 3) & 0b000_000_000_111)) & KEEP_ROW_1_MASK);
                faceR = (faceR & CLEAR_COL_2_MASK)
                                | ((((faceD >> 6) & 0b000_000_111_000) | ((faceD << 3) & 0b111_000_000_000)
                                                | ((faceD << 6) & 0b000_111_000_000)
                                                | ((faceD >> 3) & 0b000_000_000_111)) & KEEP_COL_2_MASK);
                faceD = (faceD & CLEAR_ROW_2_MASK)
                                | ((((faceL >> 6) & 0b000_000_111_000) | ((faceL << 3) & 0b111_000_000_000)
                                                | ((faceL << 6) & 0b000_111_000_000)
                                                | ((faceL >> 3) & 0b000_000_000_111)) & KEEP_ROW_2_MASK);
                faceL = (faceL & CLEAR_COL_1_MASK)
                                | ((((temp >> 6) & 0b000_000_111_000) | ((temp << 3) & 0b111_000_000_000)
                                                | ((temp << 6) & 0b000_111_000_000) | ((temp >> 3) & 0b000_000_000_111))
                                                & KEEP_COL_1_MASK);
        }

        static void Bi() {
                int temp = faceU;
                faceB = ((faceB >> 6) & 0b000_000_111_000) | ((faceB << 3) & 0b111_000_000_000)
                                | ((faceB << 6) & 0b000_111_000_000) | ((faceB >> 3) & 0b000_000_000_111);
                faceU = (faceU & CLEAR_ROW_1_MASK)
                                | ((((faceL >> 3) & 0b000_111_000_000) | ((faceL >> 6) & 0b000_000_000_111)
                                                | ((faceL << 3) & 0b000_000_111_000)
                                                | ((faceL << 6) & 0b111_000_000_000)) & KEEP_ROW_1_MASK);
                faceL = (faceL & CLEAR_COL_1_MASK)
                                | ((((faceD >> 3) & 0b000_111_000_000) | ((faceD >> 6) & 0b000_000_000_111)
                                                | ((faceD << 3) & 0b000_000_111_000)
                                                | ((faceD << 6) & 0b111_000_000_000)) & KEEP_COL_1_MASK);
                faceD = (faceD & CLEAR_ROW_2_MASK)
                                | ((((faceR >> 3) & 0b000_111_000_000) | ((faceR >> 6) & 0b000_000_000_111)
                                                | ((faceR << 3) & 0b000_000_111_000)
                                                | ((faceR << 6) & 0b111_000_000_000)) & KEEP_ROW_2_MASK);
                faceR = (faceR & CLEAR_COL_2_MASK)
                                | ((((temp >> 3) & 0b000_111_000_000) | ((temp >> 6) & 0b000_000_000_111)
                                                | ((temp << 3) & 0b000_000_111_000) | ((temp << 6) & 0b111_000_000_000))
                                                & KEEP_COL_2_MASK);
        }

        static void B2() {
                int temp = faceU;
                faceB = ((faceB >> 9) & 0b000_000_000_111) | ((faceB >> 3) & 0b000_000_111_000)
                                | ((faceB << 9) & 0b111_000_000_000) | ((faceB << 3) & 0b000_111_000_000);
                faceU = (faceU & CLEAR_ROW_1_MASK)
                                | ((((faceD >> 9) & 0b000_000_000_111) | ((faceD >> 3) & 0b000_000_111_000)
                                                | ((faceD << 9) & 0b111_000_000_000)
                                                | ((faceD << 3) & 0b000_111_000_000)) & KEEP_ROW_1_MASK);
                faceD = (faceD & CLEAR_ROW_2_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_ROW_2_MASK);
                temp = faceR;
                faceR = (faceR & CLEAR_COL_2_MASK)
                                | ((((faceL >> 9) & 0b000_000_000_111) | ((faceL >> 3) & 0b000_000_111_000)
                                                | ((faceL << 9) & 0b111_000_000_000)
                                                | ((faceL << 3) & 0b000_111_000_000)) & KEEP_COL_2_MASK);
                faceL = (faceL & CLEAR_COL_1_MASK)
                                | ((((temp >> 9) & 0b000_000_000_111) | ((temp >> 3) & 0b000_000_111_000)
                                                | ((temp << 9) & 0b111_000_000_000) | ((temp << 3) & 0b000_111_000_000))
                                                & KEEP_COL_1_MASK);
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
