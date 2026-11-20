import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

class Cube {
        int faceU = 0;
        int faceD = 0;
        int faceR = 0;
        int faceL = 0;
        int faceF = 0;
        int faceB = 0;
        String parentMoves = "";

        public Cube(int faceU, int faceD, int faceR, int faceL, int faceF, int faceB, String parentMoves) {
                this.faceU = faceU;
                this.faceD = faceD;
                this.faceR = faceR;
                this.faceL = faceL;
                this.faceF = faceF;
                this.faceB = faceB;
                this.parentMoves = parentMoves;
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
        static HashSet<String> states = new HashSet<>();
        static Queue<Cube> cubeNodes = new ArrayDeque<>();

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
                        myWriter = new FileWriter("rubik2x2lookup.txt", true);
                        writeToFile(turnStateToString(), "", myWriter);
                        generateLookupTable(turnStateToString(), "", myWriter);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        static void setCubeState(Cube cube) {
                faceU = cube.faceU;
                faceD = cube.faceD;
                faceL = cube.faceL;
                faceR = cube.faceR;
                faceF = cube.faceF;
                faceB = cube.faceB;
        }

        /**
         * BUG: Out of memory error
         * @param state
         * @param parentMoves
         * @param level
         */
        static void generateLookupTable(String state, String parentMoves, FileWriter writer) {
                states.add(state);
                cubeNodes.add(new Cube(faceU, faceD, faceR, faceL, faceF, faceB, ""));
                while (!cubeNodes.isEmpty()) {
                        Cube currentCube = cubeNodes.poll();
                        if (currentCube.parentMoves.split(" ").length >= 11) {
                                continue;
                        }
                        setCubeState(currentCube);
                        U();
                        String currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " U";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceU, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        Ui();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " U'";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceU, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        U2();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " U2";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceU, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        D();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " D";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceD, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        Di();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " D'";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceD, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        D2();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " D2";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceD, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        R();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " R";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceR, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        Ri();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " R'";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceR, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        R2();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " R2";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceR, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        L();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " L";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceL, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        Li();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " L'";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceL, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        L2();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " L2";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceL, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        F();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " F";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceF, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        Fi();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " F'";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceF, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        F2();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " F2";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceF, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        B();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " B";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceB, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        Bi();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " B'";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceB, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                        setCubeState(currentCube);
                        B2();
                        currState = turnStateToString();

                        // Only process previously unseen states
                        if (states.add(currState)) {
                                String currentMoves = currentCube.parentMoves + " B2";
                                // Save the state and its shortest move sequence
                                writeToFile(currState, currentMoves, writer);

                                // Add the new state to the queue
                                cubeNodes.add(new Cube(faceB, faceD, faceR, faceL, faceF, faceB, currentMoves));
                        }
                }
        }

        static String turnStateToString() {
                // U, D, R, L, F, B
                return Integer.toBinaryString(faceU) + " " + Integer.toBinaryString(faceD) + " " +
                                Integer.toBinaryString(faceR) + " " + Integer.toBinaryString(faceL) + " " +
                                Integer.toBinaryString(faceF) + " " + Integer.toBinaryString(faceB);
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

        // static void x(int rotations) {
        // int temp = cube[0];
        // if (rotations == 1) {
        // // Face orientation (old to new pos) U->B, B->D, D->F, F->U
        // // Rotate left and right face to simulate rotation
        // cube[2] = rotate90(cube[2], false, false);
        // cube[3] = rotate90(cube[3], true, false);
        // cube[0] = cube[4];
        // cube[4] = cube[1];
        // cube[1] = rotate90(cube[5], false, true);
        // cube[5] = rotate90(temp, false, true);
        // } else if (rotations == 2) {
        // cube[2] = rotate90(cube[2], false, true);
        // cube[3] = rotate90(cube[3], false, true);
        // cube[0] = cube[1];
        // cube[1] = temp;
        // temp = cube[4];
        // cube[4] = rotate90(cube[5], false, true);
        // cube[5] = rotate90(temp, false, true);
        // } else {
        // cube[2] = rotate90(cube[2], true, false);
        // cube[3] = rotate90(cube[3], false, false);
        // cube[0] = rotate90(cube[5], false, true);
        // cube[5] = rotate90(cube[1], false, true);
        // cube[1] = cube[4];
        // cube[4] = temp;
        // }
        // }

        // static void z(int rotations) {
        // int temp = cube[0];
        // if (rotations == 1) {
        // cube[4] = rotate90(cube[4], false, false);
        // cube[5] = rotate90(cube[5], true, false);
        // cube[0] = rotate90(cube[3], false, false);
        // cube[3] = rotate90(cube[1], false, false);
        // cube[1] = rotate90(cube[2], false, false);
        // cube[2] = rotate90(temp, false, false);
        // } else if (rotations == 2) {
        // cube[4] = rotate90(cube[4], false, true);
        // cube[5] = rotate90(cube[5], false, true);
        // cube[0] = rotate90(cube[1], false, true);
        // cube[1] = rotate90(temp, false, true);
        // temp = cube[2];
        // cube[2] = rotate90(cube[3], false, true);
        // cube[3] = rotate90(temp, false, true);
        // } else {
        // cube[4] = rotate90(cube[4], true, false);
        // cube[5] = rotate90(cube[5], false, false);
        // cube[0] = rotate90(cube[2], true, false);
        // cube[2] = rotate90(cube[1], true, false);
        // cube[1] = rotate90(cube[3], true, false);
        // cube[3] = rotate90(temp, true, false);
        // }
        // }

        // static void y(int rotations) {
        // int temp = cube[4];
        // if (rotations == 1) {
        // cube[0] = rotate90(cube[0], false, false);
        // cube[1] = rotate90(cube[1], true, false);
        // cube[4] = cube[2];
        // cube[2] = cube[5];
        // cube[5] = cube[3];
        // cube[3] = temp;
        // } else if (rotations == 2) {
        // cube[0] = rotate90(cube[0], false, true);
        // cube[1] = rotate90(cube[1], false, true);
        // cube[4] = cube[5];
        // cube[5] = temp;
        // temp = cube[2];
        // cube[2] = cube[3];
        // cube[3] = temp;
        // } else {
        // cube[0] = rotate90(cube[0], true, false);
        // cube[1] = rotate90(cube[1], false, false);
        // cube[4] = cube[3];
        // cube[3] = cube[5];
        // cube[5] = cube[2];
        // cube[2] = temp;
        // }
        // }
}
