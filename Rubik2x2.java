/*
Joshua
2026
Programmed with runtime optimization in mind
Generates a txt file with the following format
        faceU faceD faceR faceL faceF faceB long-moves-depth long-canon-moves
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

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

        static final int U_MOVE = 0;
        static final int Ui_MOVE = 1;
        static final int U2_MOVE = 2;
        static final int D_MOVE = 3;
        static final int Di_MOVE = 4;
        static final int D2_MOVE = 5;
        static final int R_MOVE = 6;
        static final int Ri_MOVE = 7;
        static final int R2_MOVE = 8;
        static final int L_MOVE = 9;
        static final int Li_MOVE = 10;
        static final int L2_MOVE = 11;
        static final int F_MOVE = 12;
        static final int Fi_MOVE = 13;
        static final int F2_MOVE = 14;
        static final int B_MOVE = 15;
        static final int Bi_MOVE = 16;
        static final int B2_MOVE = 17;
        static final int X_MOVE = 18;
        static final int Xi_MOVE = 19;
        static final int X2_MOVE = 20;
        static final int Z_MOVE = 21;
        static final int Zi_MOVE = 22;
        static final int Z2_MOVE = 23;
        static final int Y_MOVE = 24;
        static final int Yi_MOVE = 25;
        static final int Y2_MOVE = 26;

        static final int MAX_SIZE = 3674160;

        static int faceU = 0b001_001_001_001;// 0
        static int faceD = 0b000_000_000_000;// 1
        static int faceR = 0b011_011_011_011;// 2
        static int faceL = 0b010_010_010_010;// 3
        static int faceF = 0b100_100_100_100;// 4
        static int faceB = 0b101_101_101_101;// 5

        static HashSet<String> canonStates = new HashSet<>(4900000, 0.5f);

        // State stored in primitive arrays
        static int[] faceUQueue = new int[MAX_SIZE];
        static int[] faceDQueue = new int[MAX_SIZE];
        static int[] faceRQueue = new int[MAX_SIZE];
        static int[] faceLQueue = new int[MAX_SIZE];
        static int[] faceFQueue = new int[MAX_SIZE];
        static int[] faceBQueue = new int[MAX_SIZE];
        static long[] movesQueue = new long[MAX_SIZE];
        static byte[] depthQueue = new byte[MAX_SIZE];

        static int stateQueueHead = 0;
        static int stateQueueTail = 0;

        public static void main(String[] args) {
                BufferedWriter myWriter;
                try {
                        long startT = System.nanoTime();
                        myWriter = new BufferedWriter(new FileWriter("rubik2x2lookup.txt", false), 1 << 20);
                        writeToFile(turnStateToString(), "0 0", myWriter);
                        generateLookupTable(faceU, faceD, faceR, faceL, faceF, faceB, myWriter);
                        myWriter.close();

                        long endT = System.nanoTime();
                        System.out.println("MilliSeconds " + ((endT - startT) / 1_000_000));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        static void enqueueState(int faceU, int faceD, int faceR, int faceL, int faceF, int faceB, long moves,
                        byte depth) {
                faceUQueue[stateQueueTail] = faceU;
                faceDQueue[stateQueueTail] = faceD;
                faceRQueue[stateQueueTail] = faceR;
                faceLQueue[stateQueueTail] = faceL;
                faceFQueue[stateQueueTail] = faceF;
                faceBQueue[stateQueueTail] = faceB;
                movesQueue[stateQueueTail] = moves;
                depthQueue[stateQueueTail] = depth;
                stateQueueTail++;
        }

        static void dequeueState() {

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
                enqueueState(ifaceU, ifaceD, ifaceR, ifaceL, ifaceF, ifaceB, 0l, (byte) 0);
                while (stateQueueHead < MAX_SIZE) {
                        // Breadth first search method
                        long currentMoves = movesQueue[stateQueueHead];
                        byte currentDepth = depthQueue[stateQueueHead];
                        if (currentDepth >= 11) {
                                stateQueueHead++;
                                continue;
                        }

                        faceU = faceUQueue[stateQueueHead];
                        faceD = faceDQueue[stateQueueHead];
                        faceR = faceRQueue[stateQueueHead];
                        faceL = faceLQueue[stateQueueHead];
                        faceF = faceFQueue[stateQueueHead];
                        faceB = faceBQueue[stateQueueHead];
                        int currFaceU = faceU;
                        int currFaceD = faceD;
                        int currFaceR = faceR;
                        int currFaceL = faceL;
                        int currFaceF = faceF;
                        int currFaceB = faceB;

                        U();
                        updateNewState(currentMoves, currentDepth, U_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Ui();
                        updateNewState(currentMoves, currentDepth, Ui_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        U2();
                        updateNewState(currentMoves, currentDepth, U2_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        D();
                        updateNewState(currentMoves, currentDepth, D_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Di();
                        updateNewState(currentMoves, currentDepth, Di_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        D2();
                        updateNewState(currentMoves, currentDepth, D2_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        R();
                        updateNewState(currentMoves, currentDepth, R_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Ri();
                        updateNewState(currentMoves, currentDepth, Ri_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        R2();
                        updateNewState(currentMoves, currentDepth, R2_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        L();
                        updateNewState(currentMoves, currentDepth, L_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Li();
                        updateNewState(currentMoves, currentDepth, Li_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        L2();
                        updateNewState(currentMoves, currentDepth, L2_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        F();
                        updateNewState(currentMoves, currentDepth, F_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Fi();
                        updateNewState(currentMoves, currentDepth, Fi_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        F2();
                        updateNewState(currentMoves, currentDepth, F2_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        B();
                        updateNewState(currentMoves, currentDepth, B_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        Bi();
                        updateNewState(currentMoves, currentDepth, Bi_MOVE, writer);

                        resetToParentState(currFaceU, currFaceD, currFaceR, currFaceL, currFaceF, currFaceB);
                        B2();
                        updateNewState(currentMoves, currentDepth, B2_MOVE, writer);
                        stateQueueHead++;
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

        static void updateNewState(long currentStateMoves, byte currentDepth, int nextMove, BufferedWriter writer) {
                // Store current state before canonicalization
                int tempFaceU = faceU;
                int tempFaceD = faceD;
                int tempFaceR = faceR;
                int tempFaceL = faceL;
                int tempFaceF = faceF;
                int tempFaceB = faceB;
                int canonMoves = setCanonicalState();
                String currCanonState = turnStateToString();
                if (canonStates.add(currCanonState)) {
                        long currentMoves = ((currentStateMoves << 5) | nextMove);
                        // Also encode the depth for decoding later
                        writeToFile(currCanonState,
                                        Long.toString((currentMoves << 5) | (currentDepth + 1)) + " "
                                                        + Integer.toString(canonMoves),
                                        writer);
                        enqueueState(tempFaceU, tempFaceD, tempFaceR, tempFaceL, tempFaceF,
                                        tempFaceB, currentMoves, (byte) (currentDepth + 1));
                }
        }

        /**
         * Allow the lookup table to store only 1 of 24 possible same cube states
         * 
         * Setting the Yellow-Blue-Red Corner in U-L-F Corner
         */
        static int setCanonicalState() {
                if (((faceU >> 3) & 0b111) == YELLOW && ((faceL >> 6) & 0b111) == BLUE
                                && ((faceF >> 9) & 0b111) == RED) {
                        return 0;
                }
                // Yellow on faceU
                else if (((faceU >> 9) & 0b111) == YELLOW && ((faceB >> 6) & 0b111) == BLUE
                                && ((faceL >> 9) & 0b111) == RED) {
                        yi();
                        return Yi_MOVE;
                } else if (((faceU >> 6) & 0b111) == YELLOW && ((faceR >> 6) & 0b111) == BLUE
                                && ((faceB >> 9) & 0b111) == RED) {
                        y2();
                        return Y2_MOVE;
                } else if ((faceU & 0b111) == YELLOW && ((faceF >> 6) & 0b111) == BLUE
                                && ((faceR >> 9) & 0b111) == RED) {
                        y();
                        return Y_MOVE;
                }
                // Yellow on faceL
                else if (((faceL >> 9) & 0b111) == YELLOW && ((faceU >> 9) & 0b111) == BLUE
                                && ((faceB >> 6) & 0b111) == RED) {
                        z();
                        y2();
                        return (Z_MOVE << 5) | Y2_MOVE;
                } else if (((faceL >> 6) & 0b111) == YELLOW && ((faceF >> 9) & 0b111) == BLUE
                                && ((faceU >> 3) & 0b111) == RED) {
                        z();
                        y();
                        return (Z_MOVE << 5) | Y_MOVE;
                } else if (((faceL >> 3) & 0b111) == YELLOW && (faceB & 0b111) == BLUE
                                && ((faceD >> 3) & 0b111) == RED) {
                        z();
                        yi();
                        return (Z_MOVE << 5) | Yi_MOVE;
                } else if ((faceL & 0b111) == YELLOW && ((faceD >> 9) & 0b111) == BLUE
                                && ((faceF >> 3) & 0b111) == RED) {
                        z();
                        return Z_MOVE;
                }
                // Yellow on faceF
                else if (((faceF >> 9) & 0b111) == YELLOW && ((faceU >> 3) & 0b111) == BLUE
                                && ((faceL >> 6) & 0b111) == RED) {
                        x();
                        yi();
                        return (X_MOVE << 5) | Yi_MOVE;
                } else if (((faceF >> 6) & 0b111) == YELLOW && ((faceR >> 9) & 0b111) == BLUE
                                && (faceU & 0b111) == RED) {
                        x();
                        y2();
                        return (X_MOVE << 5) | Y2_MOVE;
                } else if (((faceF >> 3) & 0b111) == YELLOW && (faceL & 0b111) == BLUE
                                && ((faceD >> 9) & 0b111) == RED) {
                        x();
                        return X_MOVE;
                } else if ((faceF & 0b111) == YELLOW && ((faceD >> 6) & 0b111) == BLUE
                                && ((faceR >> 3) & 0b111) == RED) {
                        x();
                        y();
                        return (X_MOVE << 5) | Y_MOVE;
                }
                // Yellow on faceR
                else if (((faceR >> 9) & 0b111) == YELLOW && (faceU & 0b111) == BLUE
                                && ((faceF >> 6) & 0b111) == RED) {
                        zi();
                        return Zi_MOVE;
                } else if (((faceR >> 6) & 0b111) == YELLOW && ((faceB >> 9) & 0b111) == BLUE
                                && ((faceU >> 6) & 0b111) == RED) {
                        zi();
                        yi();
                        return (Zi_MOVE << 5) | Yi_MOVE;
                } else if (((faceR >> 3) & 0b111) == YELLOW && (faceF & 0b111) == BLUE
                                && ((faceD >> 6) & 0b111) == RED) {
                        zi();
                        y();
                        return (Zi_MOVE << 5) | Y_MOVE;
                } else if ((faceR & 0b111) == YELLOW && (faceD & 0b111) == BLUE
                                && ((faceB >> 3) & 0b111) == RED) {
                        zi();
                        y2();
                        return (Zi_MOVE << 5) | Y2_MOVE;
                }
                // Yellow on faceB
                else if (((faceB >> 9) & 0b111) == YELLOW && ((faceU >> 6) & 0b111) == BLUE
                                && ((faceR >> 6) & 0b111) == RED) {
                        xi();
                        y();
                        return (Xi_MOVE << 5) | Y_MOVE;
                } else if (((faceB >> 6) & 0b111) == YELLOW && ((faceL >> 9) & 0b111) == BLUE
                                && ((faceU >> 9) & 0b111) == RED) {
                        xi();
                        return Xi_MOVE;
                } else if (((faceB >> 3) & 0b111) == YELLOW && (faceR & 0b111) == BLUE
                                && (faceD & 0b111) == RED) {
                        xi();
                        y2();
                        return (Xi_MOVE << 5) | Y2_MOVE;
                } else if ((faceB & 0b111) == YELLOW && ((faceD >> 3) & 0b111) == BLUE
                                && ((faceL >> 3) & 0b111) == RED) {
                        xi();
                        yi();
                        return (Xi_MOVE << 5) | Yi_MOVE;
                }
                // Yellow on faceD
                else if (((faceD >> 9) & 0b111) == YELLOW && ((faceF >> 3) & 0b111) == BLUE
                                && (faceL & 0b111) == RED) {
                        z2();
                        y();
                        return (Z2_MOVE << 5) | Y_MOVE;
                } else if (((faceD >> 6) & 0b111) == YELLOW && ((faceR >> 3) & 0b111) == BLUE
                                && (faceF & 0b111) == RED) {
                        z2();
                        return Z2_MOVE;
                } else if (((faceD >> 3) & 0b111) == YELLOW && ((faceL >> 3) & 0b111) == BLUE
                                && (faceB & 0b111) == RED) {
                        z2();
                        y2();
                        return (Z2_MOVE << 5) | Y2_MOVE;
                } else if ((faceD & 0b111) == YELLOW && ((faceB >> 3) & 0b111) == BLUE
                                && (faceR & 0b111) == RED) {
                        z2();
                        yi();
                        return (Z2_MOVE << 5) | Yi_MOVE;
                }
                return 0;
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
