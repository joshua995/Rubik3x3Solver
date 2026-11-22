/*
Programmed with runtime optimization in mind
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Queue;

class State {
        String state;
        String moves;
        byte depth;

        public State(String state, String moves, byte depth) {
                this.state = state;
                this.moves = moves;
                this.depth = depth;
        }
}

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

        static int faceU = 0b001_001_001_001;// 0
        static int faceD = 0b000_000_000_000;// 1
        static int faceR = 0b011_011_011_011;// 2
        static int faceL = 0b010_010_010_010;// 3
        static int faceF = 0b100_100_100_100;// 4
        static int faceB = 0b101_101_101_101;// 5

        static HashSet<String> canonStates = new HashSet<>();
        static Queue<State> cubeStates = new ArrayDeque<>();

        static String[] MOVES = {
                        "U", "U2", "U'",
                        "D", "D2", "D'",
                        "R", "R2", "R'",
                        "L", "L2", "L'",
                        "F", "F2", "F'",
                        "B", "B2", "B'",
        };

        public static void main(String[] args) {
                BufferedWriter myWriter;
                try {
                        long startT = System.nanoTime();
                        myWriter = new BufferedWriter(new FileWriter("rubik2x2lookup.txt", false), 1 << 20);
                        writeToFile(turnStateToString(), "", myWriter);
                        generateLookupTable(turnStateToString(), myWriter);
                        myWriter.close();

                        long endT = System.nanoTime();
                        System.out.println("Seconds " + ((endT - startT) / 1_000_000_000));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        /**
         * 
         * @param state
         * @param level
         */
        static void generateLookupTable(String state, BufferedWriter writer) {
                canonStates.add(state);
                cubeStates.add(new State(state, "", (byte) 0)); // String state, moves
                while (!cubeStates.isEmpty()) {
                        // Breadth first search method
                        State currentCube = cubeStates.poll();
                        if (currentCube.depth >= 11) {
                                continue;
                        }
                        // Reset to parent state
                        turnStringToState(currentCube.state);
                        int currentFaceU = faceU;
                        int currentFaceD = faceD;
                        int currentFaceR = faceR;
                        int currentFaceL = faceL;
                        int currentFaceF = faceF;
                        int currentFaceB = faceB;

                        U();
                        String currState = turnStateToString();
                        String canonMoves = setCanonicalState();
                        String currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " U";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        Ui();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " U'";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        U2();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " U2";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        D();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " D";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        Di();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " D'";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        D2();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " D2";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        R();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " R";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        Ri();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " R'";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        R2();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " R2";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        L();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " L";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        Li();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " L'";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        L2();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " L2";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        F();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " F";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        Fi();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " F'";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        F2();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " F2";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        B();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " B";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        Bi();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " B'";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                        // Reset to parent state
                        faceU = currentFaceU;
                        faceD = currentFaceD;
                        faceR = currentFaceR;
                        faceL = currentFaceL;
                        faceF = currentFaceF;
                        faceB = currentFaceB;
                        B2();
                        currState = turnStateToString();
                        canonMoves = setCanonicalState();
                        currCanonState = turnStateToString();
                        if (canonStates.add(currCanonState)) {
                                String currentMoves = currentCube.moves + " B2";
                                writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                                cubeStates.add(new State(currState, currentMoves, (byte) (currentCube.depth + 1)));
                        }
                }
        }

        /**
         * Allow the lookup table to store only 1 of 24 possible same cube states
         * 
         * Setting the Yellow-Blue-Red Corner in U-L-F Corner
         */
        static String setCanonicalState() {
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
                int startOfSearch = 0;
                int endOfSearch = state.indexOf(" ", startOfSearch);
                faceU = Integer.parseInt(state.substring(startOfSearch, endOfSearch));
                startOfSearch = endOfSearch + 1;
                endOfSearch = state.indexOf(" ", startOfSearch);
                faceD = Integer.parseInt(state.substring(startOfSearch, endOfSearch));
                startOfSearch = endOfSearch + 1;
                endOfSearch = state.indexOf(" ", startOfSearch);
                faceR = Integer.parseInt(state.substring(startOfSearch, endOfSearch));
                startOfSearch = endOfSearch + 1;
                endOfSearch = state.indexOf(" ", startOfSearch);
                faceL = Integer.parseInt(state.substring(startOfSearch, endOfSearch));
                startOfSearch = endOfSearch + 1;
                endOfSearch = state.indexOf(" ", startOfSearch);
                faceF = Integer.parseInt(state.substring(startOfSearch, endOfSearch));
                startOfSearch = endOfSearch + 1;
                faceB = Integer.parseInt(state.substring(startOfSearch));
        }

        static void writeToFile(String state, String moves, BufferedWriter writer) {
                try {
                        writer.write(state);
                        writer.write(' ');
                        writer.write(moves);
                        writer.newLine();
                } catch (IOException e) {
                        e.printStackTrace();
                }
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
