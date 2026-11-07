/*
Joshua
Colour Map
    0b000 = White
    0b001 = Yellow
    0b010 = Blue
    0b011 = Green
    0b100 = Red
    0b101 = Orange
Cube map
    Face: 123456789 -> bit truples
Representation when looking at faceU, top-down solved
    123    YYY
    456 -> YYY
    789    YYY
BBB RRR GGG OOO
BBB RRR GGG OOO
BBB RRR GGG OOO
    WWW
    WWW
    WWW
*/

import java.util.HashMap;
import java.util.Random;

public class RubikBitmap {
    static final int WHITE = 0b000;
    static final int YELLOW = 0b001;
    static final int BLUE = 0b010;
    static final int GREEN = 0b011;
    static final int RED = 0b100;
    static final int ORANGE = 0b101;

    static final int KEEP_ROW_1_MASK = 0b111_111_111_000_000_000_000_000_000;
    static final int CLEAR_ROW_1_MASK = 0b000_000_000_111_111_111_111_111_111;
    static final int KEEP_ROW_3_MASK = 0b000_000_000_000_000_000_111_111_111;
    static final int CLEAR_ROW_3_MASK = 0b111_111_111_111_111_111_000_000_000;
    static final int KEEP_COL_3_MASK = 0b000_000_111_000_000_111_000_000_111;
    static final int CLEAR_COL_3_MASK = 0b111_111_000_111_111_000_111_111_000;
    static final int KEEP_COL_1_MASK = 0b111_000_000_111_000_000_111_000_000;
    static final int CLEAR_COL_1_MASK = 0b000_111_111_000_111_111_000_111_111;

    static int faceU = 0b001_001_001_001_001_001_001_001_001;// 0
    static int faceD = 0b000_000_000_000_000_000_000_000_000;// 1
    static int faceR = 0b011_011_011_011_011_011_011_011_011;// 2
    static int faceL = 0b010_010_010_010_010_010_010_010_010;// 3
    static int faceF = 0b100_100_100_100_100_100_100_100_100;// 4
    static int faceB = 0b101_101_101_101_101_101_101_101_101;// 5

    static int[] cube = { faceU, faceD, faceR, faceL, faceF, faceB }; // Shallow references throughout the code

    static HashMap<Integer, String> bitsToColorMap = new HashMap<>();

    static String[] allMovesList = {
            "U", "U2", "U'",
            "D", "D2", "D'",
            "R", "R2", "R'",
            "L", "L2", "L'",
            "F", "F2", "F'",
            "B", "B2", "B'", };

    public static void main(String[] args) {
        initColorMap();
        displayCube();
        long startTime = System.nanoTime();
        scrambleCube(100000000);
        long endTime = System.nanoTime();
        System.out.println((endTime - startTime) / 1000000);
        B();
        // B2();
        // BI();
        // B2();
        // R();
        // R2();
        // RI();
        // R2();
        // L();
        // L2();
        // LI();
        // L2();
        // U();
        // U2();
        // UI();
        // U2();
        displayCube();
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
        System.out.print("\n\n   ");
        for (int i = 8; i >= 0; i--) {
            if (counter % 3 == 0 && counter > 0) {
                System.out.print("\n   ");
            }
            System.out.print(getColor(cube[0], i));
            counter++;
        }

        // Display LFRB Face
        counter = 8;// Row 1
        System.out.print("\n" +
                getColor(cube[3], counter) + getColor(cube[3], counter - 1) + getColor(cube[3], counter - 2)
                + getColor(cube[4], counter) + getColor(cube[4], counter - 1) + getColor(cube[4], counter - 2)
                + getColor(cube[2], counter) + getColor(cube[2], counter - 1) + getColor(cube[2], counter - 2)
                + getColor(cube[5], counter) + getColor(cube[5], counter - 1) + getColor(cube[5], counter - 2));
        counter = 5;// Row 2
        System.out.print("\n" +
                getColor(cube[3], counter) + getColor(cube[3], counter - 1) + getColor(cube[3], counter - 2)
                + getColor(cube[4], counter) + getColor(cube[4], counter - 1) + getColor(cube[4], counter - 2)
                + getColor(cube[2], counter) + getColor(cube[2], counter - 1) + getColor(cube[2], counter - 2)
                + getColor(cube[5], counter) + getColor(cube[5], counter - 1) + getColor(cube[5], counter - 2));
        counter = 2; // Row 3
        System.out.print("\n" +
                getColor(cube[3], counter) + getColor(cube[3], counter - 1) + getColor(cube[3], counter - 2)
                + getColor(cube[4], counter) + getColor(cube[4], counter - 1) + getColor(cube[4], counter - 2)
                + getColor(cube[2], counter) + getColor(cube[2], counter - 1) + getColor(cube[2], counter - 2)
                + getColor(cube[5], counter) + getColor(cube[5], counter - 1) + getColor(cube[5], counter - 2));

        // Display D Face
        counter = 0;
        System.out.print("\n   ");
        for (int i = 8; i >= 0; i--) {
            if (counter % 3 == 0 && counter > 0) {
                System.out.print("\n   ");
            }
            System.out.print(getColor(cube[1], i));
            counter++;
        }
        System.out.println();
    }

    static String getColor(int face, int shiftAmount) {
        return bitsToColorMap.get((face >> (3 * shiftAmount)) & 0b111);
    }

    public static void scrambleCube(int amt) {
        for (int i = 0; i < amt; i++) {
            String chosenMove = allMovesList[new Random().nextInt(allMovesList.length)];
            // System.out.println(chosenMove);
            makeMoves(chosenMove);
            // displayCube();
        }
    }

    /**
     * Slow
     * 
     * @param moves separated by a space
     */
    static void makeMoves(String moves) {
        String[] moveList = moves.split(" ");
        for (String move : moveList) {
            switch (move) {
                case "U":
                    U();
                    break;
                case "U2":
                    U2();
                    break;
                case "U'":
                    UI();
                    break;
                case "D":
                    D();
                    break;
                case "D2":
                    D2();
                    break;
                case "D'":
                    DI();
                    break;
                case "R":
                    R();
                    break;
                case "R2":
                    R2();
                    break;
                case "R'":
                    RI();
                    break;
                case "L":
                    L();
                    break;
                case "L2":
                    L2();
                    break;
                case "L'":
                    LI();
                    break;
                case "F":
                    F();
                    break;
                case "F2":
                    F2();
                    break;
                case "F'":
                    FI();
                    break;
                case "B":
                    B();
                    break;
                case "B2":
                    B2();
                    break;
                case "B'":
                    BI();
                    break;
                default:
                    break;
            }
        }
    }

    /*
     * U Move: 2x = U2, 3x = U'
     * faceU bitmap becomes
     * 741
     * 852
     * 963
     * 1st row of faceL becomes 1st row for faceB
     * 1st row of faceF becomes 1st row for faceL...
     */
    static void U() {
        cube[0] = rotate90(cube[0], false, false);
        int temp = cube[3];
        // Clear the row, then replace with correct row
        cube[3] = updatePos(cube[3], CLEAR_ROW_1_MASK, cube[4], KEEP_ROW_1_MASK);
        cube[4] = updatePos(cube[4], CLEAR_ROW_1_MASK, cube[2], KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_1_MASK, cube[5], KEEP_ROW_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
    }

    static void U2() {
        cube[0] = rotate90(cube[0], false, true);
        int temp = cube[3];
        cube[3] = updatePos(cube[3], CLEAR_ROW_1_MASK, cube[2], KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
        temp = cube[4];
        cube[4] = updatePos(cube[4], CLEAR_ROW_1_MASK, cube[5], KEEP_ROW_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
    }

    static void UI() {
        cube[0] = rotate90(cube[0], true, false);
        int temp = cube[3];
        // Clear the row, then replace with correct row
        cube[3] = updatePos(cube[3], CLEAR_ROW_1_MASK, cube[5], KEEP_ROW_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_ROW_1_MASK, cube[2], KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_1_MASK, cube[4], KEEP_ROW_1_MASK);
        cube[4] = updatePos(cube[4], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
    }

    static void D() {
        cube[1] = rotate90(cube[1], false, false);
        int temp = cube[3];
        // Clear the row, then replace with correct row
        cube[3] = updatePos(cube[3], CLEAR_ROW_3_MASK, cube[5], KEEP_ROW_3_MASK);
        cube[5] = updatePos(cube[5], CLEAR_ROW_3_MASK, cube[2], KEEP_ROW_3_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_3_MASK, cube[4], KEEP_ROW_3_MASK);
        cube[4] = updatePos(cube[4], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
    }

    static void D2() {
        cube[1] = rotate90(cube[1], false, true);
        int temp = cube[3];
        // Clear the row, then replace with correct row
        cube[3] = updatePos(cube[3], CLEAR_ROW_3_MASK, cube[2], KEEP_ROW_3_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
        temp = cube[5];
        cube[5] = updatePos(cube[5], CLEAR_ROW_3_MASK, cube[4], KEEP_ROW_3_MASK);
        cube[4] = updatePos(cube[4], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
    }

    static void DI() {
        cube[1] = rotate90(cube[1], true, false);
        int temp = cube[3] & KEEP_ROW_3_MASK;
        // Clear the row, then replace with correct row
        cube[3] = updatePos(cube[3], CLEAR_ROW_3_MASK, cube[4], KEEP_ROW_3_MASK);
        cube[4] = updatePos(cube[4], CLEAR_ROW_3_MASK, cube[2], KEEP_ROW_3_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_3_MASK, cube[5], KEEP_ROW_3_MASK);
        cube[5] = updatePos(cube[5], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
    }

    static void R() {
        cube[2] = rotate90(cube[2], false, false);
        int temp = cube[0] & KEEP_COL_3_MASK;
        // Clear the row, then replace with correct row
        cube[0] = updatePos(cube[0], CLEAR_COL_3_MASK, cube[4], KEEP_COL_3_MASK);
        cube[4] = updatePos(cube[4], CLEAR_COL_3_MASK, cube[1], KEEP_COL_3_MASK);
        // To match the back face to the rest of the cube
        cube[1] = updatePos(cube[1], CLEAR_COL_3_MASK, rotate90(cube[5], false, true), KEEP_COL_3_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
    }

    static void R2() {
        cube[2] = rotate90(cube[2], false, true);
        int temp = cube[0];
        // Clear the row, then replace with correct row
        cube[0] = updatePos(cube[0], CLEAR_COL_3_MASK, cube[1], KEEP_COL_3_MASK);
        cube[1] = updatePos(cube[1], CLEAR_COL_3_MASK, temp, KEEP_COL_3_MASK);
        temp = cube[4];
        // To match the back face to the rest of the cube
        cube[4] = updatePos(cube[4], CLEAR_COL_3_MASK, rotate90(cube[5], false, true), KEEP_COL_3_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
    }

    static void RI() {
        cube[2] = rotate90(cube[2], true, false);
        int temp = cube[0];
        // Clear the row, then replace with correct row
        cube[0] = updatePos(cube[0], CLEAR_COL_3_MASK, rotate90(cube[5], false, true), KEEP_COL_3_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_1_MASK, rotate90(cube[1], false, true), KEEP_COL_1_MASK);
        cube[1] = updatePos(cube[1], CLEAR_COL_3_MASK, cube[4], KEEP_COL_3_MASK);
        cube[4] = updatePos(cube[4], CLEAR_COL_3_MASK, temp, KEEP_COL_3_MASK);
    }

    static void L() {
        cube[3] = rotate90(cube[3], false, false);
        int temp = cube[1];
        // Clear the row, then replace with correct row
        cube[1] = updatePos(cube[1], CLEAR_COL_1_MASK, cube[4], KEEP_COL_1_MASK);
        cube[4] = updatePos(cube[4], CLEAR_COL_1_MASK, cube[0], KEEP_COL_1_MASK);
        // To match the back face to the rest of the cube
        cube[0] = updatePos(cube[0], CLEAR_COL_1_MASK, rotate90(cube[5], false, true), KEEP_COL_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_3_MASK, rotate90(temp, false, true), KEEP_COL_3_MASK);
    }

    static void L2() {
        cube[3] = rotate90(cube[3], false, true);
        int temp = cube[1];
        cube[1] = updatePos(cube[1], CLEAR_COL_1_MASK, cube[0], KEEP_COL_1_MASK);
        cube[0] = updatePos(cube[0], CLEAR_COL_1_MASK, temp, KEEP_COL_1_MASK);
        temp = cube[4];
        cube[4] = updatePos(cube[4], CLEAR_COL_1_MASK, rotate90(cube[5], false, true), KEEP_COL_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_3_MASK, rotate90(temp, false, true), KEEP_COL_3_MASK);
    }

    static void LI() {
        cube[3] = rotate90(cube[3], true, false);
        int temp = cube[1];
        cube[1] = updatePos(cube[1], CLEAR_COL_1_MASK, rotate90(cube[5], false, true), KEEP_COL_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_3_MASK, rotate90(cube[0], false, true), KEEP_COL_3_MASK);
        cube[0] = updatePos(cube[0], CLEAR_COL_1_MASK, cube[4], KEEP_COL_1_MASK);
        cube[4] = updatePos(cube[4], CLEAR_COL_1_MASK, temp, KEEP_COL_1_MASK);
    }

    static void F() {
        cube[4] = rotate90(cube[4], false, false);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_3_MASK, rotate90(cube[3], false, false), KEEP_ROW_3_MASK);
        cube[3] = updatePos(cube[3], CLEAR_COL_3_MASK, rotate90(cube[1], false, false), KEEP_COL_3_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_1_MASK, rotate90(cube[2], false, false), KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_COL_1_MASK, rotate90(temp, false, false), KEEP_COL_1_MASK);
    }

    static void F2() {
        cube[4] = rotate90(cube[4], false, true);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_3_MASK, rotate90(cube[1], false, true), KEEP_ROW_3_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_1_MASK, rotate90(temp, false, true), KEEP_ROW_1_MASK);
        temp = cube[3];
        cube[3] = updatePos(cube[3], CLEAR_COL_3_MASK, rotate90(cube[2], false, true), KEEP_COL_3_MASK);
        cube[2] = updatePos(cube[2], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
    }

    static void FI() {
        cube[4] = rotate90(cube[4], true, false);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_3_MASK, rotate90(cube[2], true, false), KEEP_ROW_3_MASK);
        cube[2] = updatePos(cube[2], CLEAR_COL_1_MASK, rotate90(cube[1], true, false), KEEP_COL_1_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_1_MASK, rotate90(cube[3], true, false), KEEP_ROW_1_MASK);
        cube[3] = updatePos(cube[3], CLEAR_COL_3_MASK, rotate90(temp, true, false), KEEP_COL_3_MASK);
    }

    static void B() {
        cube[5] = rotate90(cube[5], false, false);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_1_MASK, rotate90(cube[2], true, false), KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_COL_3_MASK, rotate90(cube[1], true, false), KEEP_COL_3_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_3_MASK, rotate90(cube[3], true, false), KEEP_ROW_3_MASK);
        cube[3] = updatePos(cube[3], CLEAR_COL_1_MASK, rotate90(temp, true, false), KEEP_COL_1_MASK);
    }

    static void B2() {
        cube[5] = rotate90(cube[5], false, true);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_1_MASK, rotate90(cube[1], false, true), KEEP_ROW_1_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_3_MASK, rotate90(temp, false, true), KEEP_ROW_3_MASK);
        temp = cube[2];
        cube[2] = updatePos(cube[2], CLEAR_COL_3_MASK, rotate90(cube[3], false, true), KEEP_COL_3_MASK);
        cube[3] = updatePos(cube[3], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
    }

    static void BI() {
        cube[5] = rotate90(cube[5], true, false);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_1_MASK, rotate90(cube[3], false, false), KEEP_ROW_1_MASK);
        cube[3] = updatePos(cube[3], CLEAR_COL_1_MASK, rotate90(cube[1], false, false), KEEP_COL_1_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_3_MASK, rotate90(cube[2], false, false), KEEP_ROW_3_MASK);
        cube[2] = updatePos(cube[2], CLEAR_COL_3_MASK, rotate90(temp, false, false), KEEP_COL_3_MASK);
    }

    /*
     * Replace old pos with new pos
     */
    static int updatePos(int currentFace, int currentMask, int otherFace, int otherMask) {
        return (currentFace & currentMask) | (otherFace & otherMask);
    }

    /*
     * false inverse
     * 123 -> 741
     * 456 -> 852
     * 789 -> 963
     */
    static int rotate90(int face, boolean inverse, boolean doubleRot) {
        return inverse
                ? (((face >>> 0) & 7) << 18) | (((face >>> 3) & 7) << 9) | (((face >>> 6) & 7) << 0)
                        | (((face >>> 9) & 7) << 21) | (((face >>> 12) & 7) << 12) | (((face >>> 15) & 7) << 3)
                        | (((face >>> 18) & 7) << 24) | (((face >>> 21) & 7) << 15) | (((face >>> 24) & 7) << 6)
                : doubleRot ? ((face >>> 0) & 7) << 24
                        | ((face >>> 3) & 7) << 21
                        | ((face >>> 6) & 7) << 18
                        | ((face >>> 9) & 7) << 15
                        | ((face >>> 12) & 7) << 12
                        | ((face >>> 15) & 7) << 9
                        | ((face >>> 18) & 7) << 6
                        | ((face >>> 21) & 7) << 3
                        | ((face >>> 24) & 7)
                        : (((face >>> 0) & 7) << 6) | (((face >>> 3) & 7) << 15) | (((face >>> 6) & 7) << 24)
                                | (((face >>> 9) & 7) << 3) | (((face >>> 12) & 7) << 12) | (((face >>> 15) & 7) << 21)
                                | (((face >>> 18) & 7) << 0) | (((face >>> 21) & 7) << 9) | (((face >>> 24) & 7) << 18);
    }
}
