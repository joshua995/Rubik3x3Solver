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
    123    YYY     Top (00.01.02): Left.Middle.Right Array indices
    456 -> YYY  -> Mid (10.11.12)
    789    YYY     Bot (20.21.22)
BBB RRR GGG OOO
BBB RRR GGG OOO
BBB RRR GGG OOO
    WWW
    WWW
    WWW

Middle slice moves are simulated by rotating the sides and the whole cube to
*/

import java.util.HashMap;
import java.util.Map;
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
    static final int[][] stickerMasks = {
            { 0b111_000_000_000_000_000_000_000_000, 0b000_111_000_000_000_000_000_000_000,
                    0b000_000_111_000_000_000_000_000_000 },
            { 0b000_000_000_111_000_000_000_000_000, 0b000_000_000_000_111_000_000_000_000,
                    0b000_000_000_000_000_111_000_000_000 },
            { 0b000_000_000_000_000_000_111_000_000, 0b000_000_000_000_000_000_000_111_000,
                    0b000_000_000_000_000_000_000_000_111 }, };

    static final Map<Integer, Integer> stickerToShifts = Map.of(
            stickerMasks[0][0], 8, stickerMasks[0][1], 7, stickerMasks[0][2], 6,
            stickerMasks[1][0], 5, stickerMasks[1][1], 4, stickerMasks[1][2], 3,
            stickerMasks[2][0], 2, stickerMasks[2][1], 1, stickerMasks[2][2], 0);

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
            "B", "B2", "B'",
            "x", "x2", "x'",
            "M", "M2", "M'",
            "z", "z2", "z'",
            "S", "S2", "S'",
            "y", "y2", "y'",
            "E", "E2", "E'", };

    public static void main(String[] args) {
        initColorMap();
        displayCube();
        // long startTime = System.nanoTime();
        // scrambleCube(100000000);
        // long endTime = System.nanoTime();
        // System.out.println((endTime - startTime) / 1000000);
        // E();
        scrambleCube(5);
        // makeMoves("B2 E x' D' R2 D2 L U z2 R");
        displayCube();
        solveCube();
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
            // System.out.print(chosenMove + ", ");
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
        System.out.println(moves);
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
                case "x":
                    x();
                    break;
                case "x2":
                    x2();
                    break;
                case "x'":
                    xI();
                    break;
                case "M":
                    M();
                    break;
                case "M2":
                    M2();
                    break;
                case "M'":
                    MI();
                    break;
                case "y":
                    y();
                    break;
                case "y2":
                    y2();
                    break;
                case "y'":
                    yI();
                    break;
                case "E":
                    E();
                    break;
                case "E2":
                    E2();
                    break;
                case "E'":
                    EI();
                    break;
                case "z":
                    z();
                    break;
                case "z2":
                    z2();
                    break;
                case "z'":
                    zI();
                    break;
                case "S":
                    S();
                    break;
                case "S2":
                    S2();
                    break;
                case "S'":
                    SI();
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

    static void x() {
        // Rotate left and right face to simulate rotation
        cube[2] = rotate90(cube[2], false, false);
        cube[3] = rotate90(cube[3], true, false);
        // Face orientation (old to new pos) U->B, B->D, D->F, F->U
        int temp = cube[0];
        cube[0] = cube[4];
        cube[4] = cube[1];
        cube[1] = rotate90(cube[5], false, true);
        cube[5] = rotate90(temp, false, true);
    }

    static void x2() {
        // Rotate left and right face to simulate rotation
        cube[2] = rotate90(cube[2], false, true);
        cube[3] = rotate90(cube[3], false, true);
        // Face orientation (old to new pos) U->B, B->D, D->F, F->U
        int temp = cube[0];
        cube[0] = cube[1];
        cube[1] = temp;
        temp = cube[4];
        cube[4] = rotate90(cube[5], false, true);
        cube[5] = rotate90(temp, false, true);
    }

    static void xI() {
        // Rotate left and right face to simulate rotation
        cube[2] = rotate90(cube[2], true, false);
        cube[3] = rotate90(cube[3], false, false);
        // Face orientation (old to new pos) U->B, B->D, D->F, F->U
        int temp = cube[0];
        cube[0] = rotate90(cube[5], false, true);
        cube[5] = rotate90(cube[1], false, true);
        cube[1] = cube[4];
        cube[4] = temp;
    }

    static void M() {
        LI();
        R();
        xI();
    }

    static void M2() {
        L2();
        R2();
        x2();
    }

    static void MI() {
        L();
        RI();
        x();
    }

    static void z() {
        cube[4] = rotate90(cube[4], false, false);
        cube[5] = rotate90(cube[5], true, false);
        int temp = cube[0];
        cube[0] = rotate90(cube[3], false, false);
        cube[3] = rotate90(cube[1], false, false);
        cube[1] = rotate90(cube[2], false, false);
        cube[2] = rotate90(temp, false, false);
    }

    static void z2() {
        cube[4] = rotate90(cube[4], false, true);
        cube[5] = rotate90(cube[5], false, true);
        int temp = cube[0];
        cube[0] = rotate90(cube[1], false, true);
        cube[1] = rotate90(temp, false, true);
        temp = cube[2];
        cube[2] = rotate90(cube[3], false, true);
        cube[3] = rotate90(temp, false, true);
    }

    static void zI() {
        cube[4] = rotate90(cube[4], true, false);
        cube[5] = rotate90(cube[5], false, false);
        int temp = cube[0];
        cube[0] = rotate90(cube[2], true, false);
        cube[2] = rotate90(cube[1], true, false);
        cube[1] = rotate90(cube[3], true, false);
        cube[3] = rotate90(temp, true, false);
    }

    static void S() {
        B();
        FI();
        z();
    }

    static void S2() {
        B2();
        F2();
        z2();
    }

    static void SI() {
        BI();
        F();
        zI();
    }

    static void y() {
        cube[0] = rotate90(cube[0], false, false);
        cube[1] = rotate90(cube[1], true, false);
        int temp = cube[4];
        cube[4] = cube[2];
        cube[2] = cube[5];
        cube[5] = cube[3];
        cube[3] = temp;
    }

    static void y2() {
        cube[0] = rotate90(cube[0], false, true);
        cube[1] = rotate90(cube[1], false, true);
        int temp = cube[4];
        cube[4] = cube[5];
        cube[5] = temp;
        temp = cube[2];
        cube[2] = cube[3];
        cube[3] = temp;
    }

    static void yI() {
        cube[0] = rotate90(cube[0], true, false);
        cube[1] = rotate90(cube[1], false, false);
        int temp = cube[4];
        cube[4] = cube[3];
        cube[3] = cube[5];
        cube[5] = cube[2];
        cube[2] = temp;
    }

    static void E() {
        U();
        DI();
        yI();
    }

    static void E2() {
        U2();
        D2();
        y2();
    }

    static void EI() {
        UI();
        D();
        y();
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

    static void solveCube() {
        solveBlueCenter();
        solveBlueWhiteEdge();
    }

    static boolean isColorMatch(int sticker, int stickerMask, int color) {
        return ((((sticker & stickerMask) >> (3 * stickerToShifts.get(stickerMask))) & 0b111) == color);
    }

    static void solveBlueCenter() {
        if (isColorMatch(cube[3], stickerMasks[1][1], BLUE)) { // In position faceL
            return;
        } else if (isColorMatch(cube[0], stickerMasks[1][1], BLUE)) { // On faceU
            makeMoves("z'");
        } else if (isColorMatch(cube[1], stickerMasks[1][1], BLUE)) { // On faceD
            makeMoves("z");
        } else if (isColorMatch(cube[2], stickerMasks[1][1], BLUE)) { // On faceR
            makeMoves("y2");
        } else if (isColorMatch(cube[4], stickerMasks[1][1], BLUE)) { // On faceF
            makeMoves("y");
        } else if (isColorMatch(cube[5], stickerMasks[1][1], BLUE)) { // On faceB
            makeMoves("y'");
        }
    }

    // TODO Check over again
    static void solveBlueWhiteEdge() {
        // In position
        if (isColorMatch(cube[3], stickerMasks[2][1], BLUE) && isColorMatch(cube[1], stickerMasks[1][0], WHITE)) {
            return;
        } else if (isColorMatch(cube[3], stickerMasks[1][2], BLUE)
                && isColorMatch(cube[4], stickerMasks[1][0], WHITE)) { // Check blue on faceL
            makeMoves("L");
        } else if (isColorMatch(cube[3], stickerMasks[0][1], BLUE)
                && isColorMatch(cube[0], stickerMasks[1][0], WHITE)) {
            makeMoves("L2");
        } else if (isColorMatch(cube[3], stickerMasks[1][0], BLUE)
                && isColorMatch(cube[5], stickerMasks[1][2], WHITE)) {
            makeMoves("L'");
        } else if (isColorMatch(cube[0], stickerMasks[0][1], BLUE)
                && isColorMatch(cube[5], stickerMasks[0][1], WHITE)) { // Check blue on faceU
            makeMoves("B L'");
        } else if (isColorMatch(cube[0], stickerMasks[1][0], BLUE)
                && isColorMatch(cube[3], stickerMasks[0][1], WHITE)) {
            makeMoves("U B L'");
        } else if (isColorMatch(cube[0], stickerMasks[1][2], BLUE)
                && isColorMatch(cube[2], stickerMasks[0][1], WHITE)) {
            makeMoves("U' B L'");
        } else if (isColorMatch(cube[0], stickerMasks[2][1], BLUE)
                && isColorMatch(cube[4], stickerMasks[0][1], WHITE)) {
            makeMoves("F' L'");
        } else if (isColorMatch(cube[4], stickerMasks[0][1], BLUE)
                && isColorMatch(cube[0], stickerMasks[2][1], WHITE)) { // Check blue on faceF
            makeMoves("U L2");
        } else if (isColorMatch(cube[4], stickerMasks[1][0], BLUE)
                && isColorMatch(cube[3], stickerMasks[1][2], WHITE)) {
            makeMoves("F' D'");
        } else if (isColorMatch(cube[4], stickerMasks[1][2], BLUE)
                && isColorMatch(cube[2], stickerMasks[1][0], WHITE)) {
            makeMoves("F D'");
        } else if (isColorMatch(cube[4], stickerMasks[2][1], BLUE)
                && isColorMatch(cube[1], stickerMasks[0][1], WHITE)) {
            makeMoves("D'");
        } else if (isColorMatch(cube[2], stickerMasks[0][1], BLUE)
                && isColorMatch(cube[0], stickerMasks[1][2], WHITE)) { // Check blue on faceR
            makeMoves("U2 L2");
        } else if (isColorMatch(cube[2], stickerMasks[1][0], BLUE)
                && isColorMatch(cube[4], stickerMasks[1][2], WHITE)) {
            makeMoves("F2 L");
        } else if (isColorMatch(cube[2], stickerMasks[1][2], BLUE)
                && isColorMatch(cube[5], stickerMasks[1][0], WHITE)) {
            makeMoves("B2 L'");
        } else if (isColorMatch(cube[2], stickerMasks[2][1], BLUE)
                && isColorMatch(cube[1], stickerMasks[1][2], WHITE)) {
            makeMoves("D2");
        } else if (isColorMatch(cube[1], stickerMasks[0][1], BLUE)
                && isColorMatch(cube[4], stickerMasks[2][1], WHITE)) { // Check blue on faceD
            makeMoves("F L");
        } else if (isColorMatch(cube[1], stickerMasks[1][0], BLUE)
                && isColorMatch(cube[3], stickerMasks[2][1], WHITE)) {
            makeMoves("D F L");
        } else if (isColorMatch(cube[1], stickerMasks[1][2], BLUE)
                && isColorMatch(cube[2], stickerMasks[2][1], WHITE)) {
            makeMoves("D' F L");
        } else if (isColorMatch(cube[1], stickerMasks[2][1], BLUE)
                && isColorMatch(cube[5], stickerMasks[2][1], WHITE)) {
            makeMoves("B' L'");
        } else if (isColorMatch(cube[5], stickerMasks[0][1], BLUE)
                && isColorMatch(cube[0], stickerMasks[1][2], WHITE)) { // TODO Check blue on faceB
            makeMoves("");
        } else if (isColorMatch(cube[5], stickerMasks[1][0], BLUE)
                && isColorMatch(cube[4], stickerMasks[1][2], WHITE)) {
            makeMoves("");
        } else if (isColorMatch(cube[5], stickerMasks[1][2], BLUE)
                && isColorMatch(cube[5], stickerMasks[1][0], WHITE)) {
            makeMoves("");
        } else if (isColorMatch(cube[5], stickerMasks[2][1], BLUE)
                && isColorMatch(cube[1], stickerMasks[0][1], WHITE)) {
            makeMoves("");
        }
    }
}
