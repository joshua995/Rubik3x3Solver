/*
Programmed with runtime optimization in mind
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

class State {
        int faceU;
        int faceD;
        int faceR;
        int faceL;
        int faceF;
        int faceB;
        String moves;
        byte depth;

        public State(int faceU, int faceD, int faceR, int faceL, int faceF, int faceB, String moves,
                        byte depth) {
                this.faceU = faceU;
                this.faceD = faceD;
                this.faceR = faceR;
                this.faceL = faceL;
                this.faceF = faceF;
                this.faceB = faceB;
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

        static HashSet<String> canonStates = new HashSet<>(4900000, 0.5f);

        static State[] stateQueue = new State[3_674_160];
        static int stateQueueHead = 0;
        static int stateQueueTail = 0;

        // Enqueue stateQueue[stateQueueTail++]=State;

        // Dequeue State currentState = stateQueue[stateQueueHead++];

        public static void main(String[] args) {
                BufferedWriter myWriter;
                try {
                        long startT = System.nanoTime();
                        myWriter = new BufferedWriter(new FileWriter("rubik2x2lookup.txt", false), 1 << 20);
                        writeToFile(turnStateToString(), "", myWriter);
                        generateLookupTable(faceU, faceD, faceR, faceL, faceF, faceB, myWriter);
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
        static void generateLookupTable(int ifaceU, int ifaceD, int ifaceR, int ifaceL, int ifaceF, int ifaceB,
                        BufferedWriter writer) {
                canonStates.add(turnStateToString());
                // state, moves
                stateQueue[stateQueueTail++] = new State(ifaceU, ifaceD, ifaceR, ifaceL, ifaceF, ifaceB, "", (byte) 0);
                while (stateQueueHead < 3674160) {
                        // Breadth first search method
                        State currentCube = stateQueue[stateQueueHead++];
                        if (currentCube.depth >= 11) {
                                continue;
                        }

                        faceU = currentCube.faceU;
                        faceD = currentCube.faceD;
                        faceR = currentCube.faceR;
                        faceL = currentCube.faceL;
                        faceF = currentCube.faceF;
                        faceB = currentCube.faceB;
                        int currFaceU = faceU;
                        int currFaceD = faceD;
                        int currFaceR = faceR;
                        int currFaceL = faceL;
                        int currFaceF = faceF;
                        int currFaceB = faceB;

                        U();
                        updateNewState(currentCube, " U", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Ui();
                        updateNewState(currentCube, " U'", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        U2();
                        updateNewState(currentCube, " U2", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        D();
                        updateNewState(currentCube, " D", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Di();
                        updateNewState(currentCube, " D'", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        D2();
                        updateNewState(currentCube, " D2", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        R();
                        updateNewState(currentCube, " R", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Ri();
                        updateNewState(currentCube, " R'", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        R2();
                        updateNewState(currentCube, " R2", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        L();
                        updateNewState(currentCube, " L", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Li();
                        updateNewState(currentCube, " L'", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        L2();
                        updateNewState(currentCube, " L2", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        F();
                        updateNewState(currentCube, " F", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Fi();
                        updateNewState(currentCube, " F'", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        F2();
                        updateNewState(currentCube, " F2", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        B();
                        updateNewState(currentCube, " B", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Bi();
                        updateNewState(currentCube, " B'", writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        B2();
                        updateNewState(currentCube, " B2", writer);
                }
        }

        static void resetToParentState(int currFaceU, int currFaceD, int currFaceR, int currFaceL,
                        int currFaceF, int currFaceB) {
                faceU = currFaceU;
                faceD = currFaceD;
                faceR = currFaceR;
                faceL = currFaceL;
                faceF = currFaceF;
                faceB = currFaceB;
        }

        static void updateNewState(State currentCube, String nextMove, BufferedWriter writer) {
                // Store current state before canonicalization
                int tempFaceU = faceU;
                int tempFaceD = faceD;
                int tempFaceR = faceR;
                int tempFaceL = faceL;
                int tempFaceF = faceF;
                int tempFaceB = faceB;
                String canonMoves = setCanonicalState();
                String currCanonState = turnStateToString();
                if (canonStates.add(currCanonState)) {
                        String currentMoves = currentCube.moves + nextMove;
                        writeToFile(currCanonState, currentMoves + " " + canonMoves, writer);
                        stateQueue[stateQueueTail++] = new State(tempFaceU, tempFaceD, tempFaceR, tempFaceL, tempFaceF,
                                        tempFaceB, currentMoves, (byte) (currentCube.depth + 1));
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
