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
            "E", "E2", "E'",
            "u", "u2", "u'",
            "d", "d2", "d'",
            "r", "r2", "r'",
            "l", "l2", "l'",
            "f", "f2", "f'",
            "b", "b2", "b'", };

    public static void main(String[] args) {
        initColorMap();
        displayCube();
        // long startTime = System.nanoTime();
        // scrambleCube(100000000);
        // long endTime = System.nanoTime();
        // System.out.println((endTime - startTime) / 1000000);
        // E(1);
        scrambleCube(5);
        // makeMoves("M2 F2 x x' z");
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
        System.out.println();
    }

    /**
     * Slow
     * 
     * @param moves separated by a space
     */
    static void makeMoves(String moves) {
        System.out.print(moves + " ");
        String[] moveList = moves.split(" ");
        for (String move : moveList) {
            switch (move) {
                case "U":
                    U(1);
                    break;
                case "U2":
                    U(2);
                    break;
                case "U'":
                    U(3);
                    break;
                case "D":
                    D(1);
                    break;
                case "D2":
                    D(2);
                    break;
                case "D'":
                    D(3);
                    break;
                case "R":
                    R(1);
                    break;
                case "R2":
                    R(2);
                    break;
                case "R'":
                    R(3);
                    break;
                case "L":
                    L(1);
                    break;
                case "L2":
                    L(2);
                    break;
                case "L'":
                    L(3);
                    break;
                case "F":
                    F(1);
                    break;
                case "F2":
                    F(2);
                    break;
                case "F'":
                    F(3);
                    break;
                case "B":
                    B(1);
                    break;
                case "B2":
                    B(2);
                    break;
                case "B'":
                    B(3);
                    break;
                case "x":
                    x(1);
                    break;
                case "x2":
                    x(2);
                    break;
                case "x'":
                    x(3);
                    break;
                case "M":
                    M(1);
                    break;
                case "M2":
                    M(2);
                    break;
                case "M'":
                    M(3);
                    break;
                case "y":
                    y(1);
                    break;
                case "y2":
                    y(2);
                    break;
                case "y'":
                    y(3);
                    break;
                case "E":
                    E(1);
                    break;
                case "E2":
                    E(2);
                    break;
                case "E'":
                    E(3);
                    break;
                case "z":
                    z(1);
                    break;
                case "z2":
                    z(2);
                    break;
                case "z'":
                    z(3);
                    break;
                case "S":
                    S(1);
                    break;
                case "S2":
                    S(2);
                    break;
                case "S'":
                    S(3);
                    break;
                case "u":
                    u(1);
                    break;
                case "u2":
                    u(2);
                    break;
                case "u'":
                    u(3);
                    break;
                case "d":
                    d(1);
                    break;
                case "d2":
                    d(2);
                    break;
                case "d'":
                    d(3);
                    break;
                case "r":
                    r(1);
                    break;
                case "r2":
                    r(2);
                    break;
                case "r'":
                    r(3);
                    break;
                case "l":
                    l(1);
                    break;
                case "l2":
                    l(2);
                    break;
                case "l'":
                    l(3);
                    break;
                case "f":
                    f(1);
                    break;
                case "f2":
                    f(2);
                    break;
                case "f'":
                    f(3);
                    break;
                case "b":
                    b(1);
                    break;
                case "b2":
                    b(2);
                    break;
                case "b'":
                    b(3);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * U Move: 2x = U2, 3x = U'
     * faceU bitmap becomes
     * 741
     * 852
     * 963
     * 1st row of faceL becomes 1st row for faceB
     * 1st row of faceF becomes 1st row for faceL...
     * 
     * @param rotations 1=Clockwise, 2=double, 3=Inverse/Counterclock
     * @return
     */
    static void U(int rotations) {
        int temp = cube[3];
        if (rotations == 1) {
            cube[0] = rotate90(cube[0], false, false);
            // Clear the row, then replace with correct row
            cube[3] = updatePos(cube[3], CLEAR_ROW_1_MASK, cube[4], KEEP_ROW_1_MASK);
            cube[4] = updatePos(cube[4], CLEAR_ROW_1_MASK, cube[2], KEEP_ROW_1_MASK);
            cube[2] = updatePos(cube[2], CLEAR_ROW_1_MASK, cube[5], KEEP_ROW_1_MASK);
            cube[5] = updatePos(cube[5], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
        } else if (rotations == 2) {
            cube[0] = rotate90(cube[0], false, true);
            cube[3] = updatePos(cube[3], CLEAR_ROW_1_MASK, cube[2], KEEP_ROW_1_MASK);
            cube[2] = updatePos(cube[2], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
            temp = cube[4];
            cube[4] = updatePos(cube[4], CLEAR_ROW_1_MASK, cube[5], KEEP_ROW_1_MASK);
            cube[5] = updatePos(cube[5], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
        } else {
            cube[0] = rotate90(cube[0], true, false);
            // Clear the row, then replace with correct row
            cube[3] = updatePos(cube[3], CLEAR_ROW_1_MASK, cube[5], KEEP_ROW_1_MASK);
            cube[5] = updatePos(cube[5], CLEAR_ROW_1_MASK, cube[2], KEEP_ROW_1_MASK);
            cube[2] = updatePos(cube[2], CLEAR_ROW_1_MASK, cube[4], KEEP_ROW_1_MASK);
            cube[4] = updatePos(cube[4], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
        }
    }

    static void D(int rotations) {
        int temp = cube[3];
        if (rotations == 1) {
            cube[1] = rotate90(cube[1], false, false);
            // Clear the row, then replace with correct row
            cube[3] = updatePos(cube[3], CLEAR_ROW_3_MASK, cube[5], KEEP_ROW_3_MASK);
            cube[5] = updatePos(cube[5], CLEAR_ROW_3_MASK, cube[2], KEEP_ROW_3_MASK);
            cube[2] = updatePos(cube[2], CLEAR_ROW_3_MASK, cube[4], KEEP_ROW_3_MASK);
            cube[4] = updatePos(cube[4], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
        } else if (rotations == 2) {
            cube[1] = rotate90(cube[1], false, true);
            // Clear the row, then replace with correct row
            cube[3] = updatePos(cube[3], CLEAR_ROW_3_MASK, cube[2], KEEP_ROW_3_MASK);
            cube[2] = updatePos(cube[2], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
            temp = cube[5];
            cube[5] = updatePos(cube[5], CLEAR_ROW_3_MASK, cube[4], KEEP_ROW_3_MASK);
            cube[4] = updatePos(cube[4], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
        } else {
            cube[1] = rotate90(cube[1], true, false);
            // Clear the row, then replace with correct row
            cube[3] = updatePos(cube[3], CLEAR_ROW_3_MASK, cube[4], KEEP_ROW_3_MASK);
            cube[4] = updatePos(cube[4], CLEAR_ROW_3_MASK, cube[2], KEEP_ROW_3_MASK);
            cube[2] = updatePos(cube[2], CLEAR_ROW_3_MASK, cube[5], KEEP_ROW_3_MASK);
            cube[5] = updatePos(cube[5], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
        }
    }

    static void R(int rotations) {
        int temp = cube[0];
        if (rotations == 1) {
            cube[2] = rotate90(cube[2], false, false);
            // Clear the row, then replace with correct row
            cube[0] = updatePos(cube[0], CLEAR_COL_3_MASK, cube[4], KEEP_COL_3_MASK);
            cube[4] = updatePos(cube[4], CLEAR_COL_3_MASK, cube[1], KEEP_COL_3_MASK);
            // To match the back face to the rest of the cube
            cube[1] = updatePos(cube[1], CLEAR_COL_3_MASK, rotate90(cube[5], false, true), KEEP_COL_3_MASK);
            cube[5] = updatePos(cube[5], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
        } else if (rotations == 2) {
            cube[2] = rotate90(cube[2], false, true);
            cube[0] = updatePos(cube[0], CLEAR_COL_3_MASK, cube[1], KEEP_COL_3_MASK);
            cube[1] = updatePos(cube[1], CLEAR_COL_3_MASK, temp, KEEP_COL_3_MASK);
            temp = cube[4];
            cube[4] = updatePos(cube[4], CLEAR_COL_3_MASK, rotate90(cube[5], false, true), KEEP_COL_3_MASK);
            cube[5] = updatePos(cube[5], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
        } else {
            cube[2] = rotate90(cube[2], true, false);
            cube[0] = updatePos(cube[0], CLEAR_COL_3_MASK, rotate90(cube[5], false, true), KEEP_COL_3_MASK);
            cube[5] = updatePos(cube[5], CLEAR_COL_1_MASK, rotate90(cube[1], false, true), KEEP_COL_1_MASK);
            cube[1] = updatePos(cube[1], CLEAR_COL_3_MASK, cube[4], KEEP_COL_3_MASK);
            cube[4] = updatePos(cube[4], CLEAR_COL_3_MASK, temp, KEEP_COL_3_MASK);
        }
    }

    static void L(int rotations) {
        int temp = cube[1];
        if (rotations == 1) {
            cube[3] = rotate90(cube[3], false, false);
            // Clear the row, then replace with correct row
            cube[1] = updatePos(cube[1], CLEAR_COL_1_MASK, cube[4], KEEP_COL_1_MASK);
            cube[4] = updatePos(cube[4], CLEAR_COL_1_MASK, cube[0], KEEP_COL_1_MASK);
            // To match the back face to the rest of the cube
            cube[0] = updatePos(cube[0], CLEAR_COL_1_MASK, rotate90(cube[5], false, true), KEEP_COL_1_MASK);
            cube[5] = updatePos(cube[5], CLEAR_COL_3_MASK, rotate90(temp, false, true), KEEP_COL_3_MASK);
        } else if (rotations == 2) {
            cube[3] = rotate90(cube[3], false, true);
            cube[1] = updatePos(cube[1], CLEAR_COL_1_MASK, cube[0], KEEP_COL_1_MASK);
            cube[0] = updatePos(cube[0], CLEAR_COL_1_MASK, temp, KEEP_COL_1_MASK);
            temp = cube[4];
            cube[4] = updatePos(cube[4], CLEAR_COL_1_MASK, rotate90(cube[5], false, true), KEEP_COL_1_MASK);
            cube[5] = updatePos(cube[5], CLEAR_COL_3_MASK, rotate90(temp, false, true), KEEP_COL_3_MASK);
        } else {
            cube[3] = rotate90(cube[3], true, false);
            cube[1] = updatePos(cube[1], CLEAR_COL_1_MASK, rotate90(cube[5], false, true), KEEP_COL_1_MASK);
            cube[5] = updatePos(cube[5], CLEAR_COL_3_MASK, rotate90(cube[0], false, true), KEEP_COL_3_MASK);
            cube[0] = updatePos(cube[0], CLEAR_COL_1_MASK, cube[4], KEEP_COL_1_MASK);
            cube[4] = updatePos(cube[4], CLEAR_COL_1_MASK, temp, KEEP_COL_1_MASK);
        }
    }

    static void F(int rotations) {
        int temp = cube[0];
        if (rotations == 1) {
            cube[4] = rotate90(cube[4], false, false);
            cube[0] = updatePos(cube[0], CLEAR_ROW_3_MASK, rotate90(cube[3], false, false), KEEP_ROW_3_MASK);
            cube[3] = updatePos(cube[3], CLEAR_COL_3_MASK, rotate90(cube[1], false, false), KEEP_COL_3_MASK);
            cube[1] = updatePos(cube[1], CLEAR_ROW_1_MASK, rotate90(cube[2], false, false), KEEP_ROW_1_MASK);
            cube[2] = updatePos(cube[2], CLEAR_COL_1_MASK, rotate90(temp, false, false), KEEP_COL_1_MASK);
        } else if (rotations == 2) {
            cube[4] = rotate90(cube[4], false, true);
            cube[0] = updatePos(cube[0], CLEAR_ROW_3_MASK, rotate90(cube[1], false, true), KEEP_ROW_3_MASK);
            cube[1] = updatePos(cube[1], CLEAR_ROW_1_MASK, rotate90(temp, false, true), KEEP_ROW_1_MASK);
            temp = cube[3];
            cube[3] = updatePos(cube[3], CLEAR_COL_3_MASK, rotate90(cube[2], false, true), KEEP_COL_3_MASK);
            cube[2] = updatePos(cube[2], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
        } else {
            cube[4] = rotate90(cube[4], true, false);
            cube[0] = updatePos(cube[0], CLEAR_ROW_3_MASK, rotate90(cube[2], true, false), KEEP_ROW_3_MASK);
            cube[2] = updatePos(cube[2], CLEAR_COL_1_MASK, rotate90(cube[1], true, false), KEEP_COL_1_MASK);
            cube[1] = updatePos(cube[1], CLEAR_ROW_1_MASK, rotate90(cube[3], true, false), KEEP_ROW_1_MASK);
            cube[3] = updatePos(cube[3], CLEAR_COL_3_MASK, rotate90(temp, true, false), KEEP_COL_3_MASK);
        }
    }

    static void B(int rotations) {
        int temp = cube[0];
        if (rotations == 1) {
            cube[5] = rotate90(cube[5], false, false);
            cube[0] = updatePos(cube[0], CLEAR_ROW_1_MASK, rotate90(cube[2], true, false), KEEP_ROW_1_MASK);
            cube[2] = updatePos(cube[2], CLEAR_COL_3_MASK, rotate90(cube[1], true, false), KEEP_COL_3_MASK);
            cube[1] = updatePos(cube[1], CLEAR_ROW_3_MASK, rotate90(cube[3], true, false), KEEP_ROW_3_MASK);
            cube[3] = updatePos(cube[3], CLEAR_COL_1_MASK, rotate90(temp, true, false), KEEP_COL_1_MASK);
        } else if (rotations == 2) {
            cube[5] = rotate90(cube[5], false, true);
            cube[0] = updatePos(cube[0], CLEAR_ROW_1_MASK, rotate90(cube[1], false, true), KEEP_ROW_1_MASK);
            cube[1] = updatePos(cube[1], CLEAR_ROW_3_MASK, rotate90(temp, false, true), KEEP_ROW_3_MASK);
            temp = cube[2];
            cube[2] = updatePos(cube[2], CLEAR_COL_3_MASK, rotate90(cube[3], false, true), KEEP_COL_3_MASK);
            cube[3] = updatePos(cube[3], CLEAR_COL_1_MASK, rotate90(temp, false, true), KEEP_COL_1_MASK);
        } else {
            cube[5] = rotate90(cube[5], true, false);
            cube[0] = updatePos(cube[0], CLEAR_ROW_1_MASK, rotate90(cube[3], false, false), KEEP_ROW_1_MASK);
            cube[3] = updatePos(cube[3], CLEAR_COL_1_MASK, rotate90(cube[1], false, false), KEEP_COL_1_MASK);
            cube[1] = updatePos(cube[1], CLEAR_ROW_3_MASK, rotate90(cube[2], false, false), KEEP_ROW_3_MASK);
            cube[2] = updatePos(cube[2], CLEAR_COL_3_MASK, rotate90(temp, false, false), KEEP_COL_3_MASK);
        }
    }

    static void x(int rotations) {
        int temp = cube[0];
        if (rotations == 1) {
            // Face orientation (old to new pos) U->B, B->D, D->F, F->U
            // Rotate left and right face to simulate rotation
            cube[2] = rotate90(cube[2], false, false);
            cube[3] = rotate90(cube[3], true, false);
            cube[0] = cube[4];
            cube[4] = cube[1];
            cube[1] = rotate90(cube[5], false, true);
            cube[5] = rotate90(temp, false, true);
        } else if (rotations == 2) {
            cube[2] = rotate90(cube[2], false, true);
            cube[3] = rotate90(cube[3], false, true);
            cube[0] = cube[1];
            cube[1] = temp;
            temp = cube[4];
            cube[4] = rotate90(cube[5], false, true);
            cube[5] = rotate90(temp, false, true);
        } else {
            cube[2] = rotate90(cube[2], true, false);
            cube[3] = rotate90(cube[3], false, false);
            cube[0] = rotate90(cube[5], false, true);
            cube[5] = rotate90(cube[1], false, true);
            cube[1] = cube[4];
            cube[4] = temp;
        }
    }

    static void M(int rotations) {
        if (rotations == 1) {
            L(3);
            R(1);
            x(3);
        } else if (rotations == 2) {
            L(2);
            R(2);
            x(2);
        } else {
            L(1);
            R(3);
            x(1);
        }
    }

    static void z(int rotations) {
        int temp = cube[0];
        if (rotations == 1) {
            cube[4] = rotate90(cube[4], false, false);
            cube[5] = rotate90(cube[5], true, false);
            cube[0] = rotate90(cube[3], false, false);
            cube[3] = rotate90(cube[1], false, false);
            cube[1] = rotate90(cube[2], false, false);
            cube[2] = rotate90(temp, false, false);
        } else if (rotations == 2) {
            cube[4] = rotate90(cube[4], false, true);
            cube[5] = rotate90(cube[5], false, true);
            cube[0] = rotate90(cube[1], false, true);
            cube[1] = rotate90(temp, false, true);
            temp = cube[2];
            cube[2] = rotate90(cube[3], false, true);
            cube[3] = rotate90(temp, false, true);
        } else {
            cube[4] = rotate90(cube[4], true, false);
            cube[5] = rotate90(cube[5], false, false);
            cube[0] = rotate90(cube[2], true, false);
            cube[2] = rotate90(cube[1], true, false);
            cube[1] = rotate90(cube[3], true, false);
            cube[3] = rotate90(temp, true, false);
        }
    }

    static void S(int rotations) {
        if (rotations == 1) {
            B(1);
            F(3);
            z(1);
        } else if (rotations == 2) {
            B(2);
            F(2);
            z(2);
        } else {
            B(3);
            F(1);
            z(3);
        }
    }

    static void y(int rotations) {
        int temp = cube[4];
        if (rotations == 1) {
            cube[0] = rotate90(cube[0], false, false);
            cube[1] = rotate90(cube[1], true, false);
            cube[4] = cube[2];
            cube[2] = cube[5];
            cube[5] = cube[3];
            cube[3] = temp;
        } else if (rotations == 2) {
            cube[0] = rotate90(cube[0], false, true);
            cube[1] = rotate90(cube[1], false, true);
            cube[4] = cube[5];
            cube[5] = temp;
            temp = cube[2];
            cube[2] = cube[3];
            cube[3] = temp;
        } else {
            cube[0] = rotate90(cube[0], true, false);
            cube[1] = rotate90(cube[1], false, false);
            cube[4] = cube[3];
            cube[3] = cube[5];
            cube[5] = cube[2];
            cube[2] = temp;
        }
    }

    static void E(int rotations) {
        if (rotations == 1) {
            U(1);
            D(3);
            y(3);
        } else if (rotations == 2) {
            U(2);
            D(2);
            y(2);
        } else {
            U(3);
            D(1);
            y(1);
        }
    }

    static void u(int rotations) {
        if (rotations == 1) {
            U(1);
            E(3);
        } else if (rotations == 2) {
            U(2);
            E(2);
        } else {
            U(3);
            E(1);
        }
    }

    static void d(int rotations) {
        if (rotations == 1) {
            D(1);
            E(1);
        } else if (rotations == 2) {
            D(2);
            E(2);
        } else {
            D(3);
            E(3);
        }
    }

    static void r(int rotations) {
        if (rotations == 1) {
            R(1);
            M(3);
        } else if (rotations == 2) {
            R(2);
            M(2);
        } else {
            R(3);
            M(1);
        }
    }

    static void l(int rotations) {
        if (rotations == 1) {
            L(1);
            L(1);
        } else if (rotations == 2) {
            L(2);
            M(2);
        } else {
            L(3);
            M(3);
        }
    }

    static void f(int rotations) {
        if (rotations == 1) {
            F(1);
            S(1);
        } else if (rotations == 2) {
            F(2);
            S(2);
        } else {
            F(3);
            S(3);
        }
    }

    static void b(int rotations) {
        if (rotations == 1) {
            B(1);
            S(3);
        } else if (rotations == 2) {
            B(2);
            S(2);
        } else {
            B(3);
            S(1);
        }
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
        loadBlueRedEdge();
        solveBlueRedBlock();
    }

    static boolean colorMatch(int sticker, int stickerMask, int color) {
        return ((((sticker & stickerMask) >> (3 * stickerToShifts.get(stickerMask))) & 0b111) == color);
    }

    /**
     * Checks if an edge matches the search or a pair of sticker matches search
     * 
     * @param cubeI              1st face index
     * @param stickerMaskRow
     * @param stickerMaskCol     1st stickerMasks[stickerMaskRow][stickerMaskCol]
     * @param color              1st to match
     * @param cubeI1             2nd face index
     * @param stickerMaskRow1
     * @param stickerMaskCol1    2nd stickerMasks[stickerMaskRow1][stickerMaskCol1]
     * @param color1             2nd to match
     * @param checkFlippedColors return true if stickers match when colors are
     *                           flipped
     * @return Edge matches criteria
     */
    static boolean pairMatch(int cubeI, int stickerMaskRow, int stickerMaskCol, int color, int cubeI1,
            int stickerMaskRow1, int stickerMaskCol1, int color1, boolean checkFlippedColors) {
        return !checkFlippedColors ? colorMatch(cube[cubeI], stickerMasks[stickerMaskRow][stickerMaskCol], color)
                && colorMatch(cube[cubeI1], stickerMasks[stickerMaskRow1][stickerMaskCol1], color1)
                : (colorMatch(cube[cubeI], stickerMasks[stickerMaskRow][stickerMaskCol], color)
                        && colorMatch(cube[cubeI1], stickerMasks[stickerMaskRow1][stickerMaskCol1], color1))
                        || (colorMatch(cube[cubeI], stickerMasks[stickerMaskRow][stickerMaskCol], color1)
                                && colorMatch(cube[cubeI1], stickerMasks[stickerMaskRow1][stickerMaskCol1], color));
    }

    // NOTE might be redundant. Checking two sides will determine the 3rd color
    /**
     * Checks if a corner or trio of stickers match the colors (similar to
     * pairMatch)
     * 
     * @param cubeI
     * @param stickerMaskRow
     * @param stickerMaskCol
     * @param color
     * @param cubeI1
     * @param stickerMaskRow1
     * @param stickerMaskCol1
     * @param color1
     * @param cubeI2
     * @param stickerMaskRow2
     * @param stickerMaskCol2
     * @param color2
     * @return corner matches criteria
     */
    static boolean trioMatch(int cubeI, int stickerMaskRow, int stickerMaskCol, int color, int cubeI1,
            int stickerMaskRow1, int stickerMaskCol1, int color1, int cubeI2, int stickerMaskRow2, int stickerMaskCol2,
            int color2) {
        return pairMatch(cubeI, stickerMaskRow, stickerMaskCol, color, cubeI1, stickerMaskRow1, stickerMaskCol1, color1,
                false)
                && colorMatch(cube[cubeI2], stickerMasks[stickerMaskRow2][stickerMaskCol2], color2);
    }

    static void solveBlueCenter() {
        if (colorMatch(cube[3], stickerMasks[1][1], BLUE)) { // In position faceL
            return;
        } else if (colorMatch(cube[0], stickerMasks[1][1], BLUE)) { // On faceU
            makeMoves("z'");
        } else if (colorMatch(cube[1], stickerMasks[1][1], BLUE)) { // On faceD
            makeMoves("z");
        } else if (colorMatch(cube[2], stickerMasks[1][1], BLUE)) { // On faceR
            makeMoves("y2");
        } else if (colorMatch(cube[4], stickerMasks[1][1], BLUE)) { // On faceF
            makeMoves("y");
        } else if (colorMatch(cube[5], stickerMasks[1][1], BLUE)) { // On faceB
            makeMoves("y'");
        }
    }

    static void solveBlueWhiteEdge() {
        // In position
        if (pairMatch(3, 2, 1, BLUE, 1, 1, 0, WHITE, false)) {
            return;
        } // Check blue on faceL
        else if (pairMatch(3, 1, 2, BLUE, 4, 1, 0, WHITE, false)) {
            makeMoves("L");
        } else if (colorMatch(cube[3], stickerMasks[0][1], BLUE) && colorMatch(cube[0], stickerMasks[1][0], WHITE)) {
            makeMoves("L2");
        } else if (colorMatch(cube[3], stickerMasks[1][0], BLUE) && colorMatch(cube[5], stickerMasks[1][2], WHITE)) {
            makeMoves("L'");
        } // Check blue on faceU
        else if (colorMatch(cube[0], stickerMasks[0][1], BLUE) && colorMatch(cube[5], stickerMasks[0][1], WHITE)) {
            makeMoves("B L'");
        } else if (colorMatch(cube[0], stickerMasks[1][0], BLUE) && colorMatch(cube[3], stickerMasks[0][1], WHITE)) {
            makeMoves("U B L'");
        } else if (colorMatch(cube[0], stickerMasks[1][2], BLUE) && colorMatch(cube[2], stickerMasks[0][1], WHITE)) {
            makeMoves("U' B L'");
        } else if (colorMatch(cube[0], stickerMasks[2][1], BLUE) && colorMatch(cube[4], stickerMasks[0][1], WHITE)) {
            makeMoves("F' L'");
        } // Check blue on faceF
        else if (colorMatch(cube[4], stickerMasks[0][1], BLUE) && colorMatch(cube[0], stickerMasks[2][1], WHITE)) {
            makeMoves("U L2");
        } else if (colorMatch(cube[4], stickerMasks[1][0], BLUE) && colorMatch(cube[3], stickerMasks[1][2], WHITE)) {
            makeMoves("F' D'");
        } else if (colorMatch(cube[4], stickerMasks[1][2], BLUE) && colorMatch(cube[2], stickerMasks[1][0], WHITE)) {
            makeMoves("F D'");
        } else if (colorMatch(cube[4], stickerMasks[2][1], BLUE) && colorMatch(cube[1], stickerMasks[0][1], WHITE)) {
            makeMoves("D'");
        } // Check blue on faceR
        else if (colorMatch(cube[2], stickerMasks[0][1], BLUE) && colorMatch(cube[0], stickerMasks[1][2], WHITE)) {
            makeMoves("U2 L2");
        } else if (colorMatch(cube[2], stickerMasks[1][0], BLUE) && colorMatch(cube[4], stickerMasks[1][2], WHITE)) {
            makeMoves("F2 L");
        } else if (colorMatch(cube[2], stickerMasks[1][2], BLUE) && colorMatch(cube[5], stickerMasks[1][0], WHITE)) {
            makeMoves("B2 L'");
        } else if (colorMatch(cube[2], stickerMasks[2][1], BLUE) && colorMatch(cube[1], stickerMasks[1][2], WHITE)) {
            makeMoves("D2");
        } // Check blue on faceD
        else if (colorMatch(cube[1], stickerMasks[0][1], BLUE) && colorMatch(cube[4], stickerMasks[2][1], WHITE)) {
            makeMoves("F L");
        } else if (colorMatch(cube[1], stickerMasks[1][0], BLUE) && colorMatch(cube[3], stickerMasks[2][1], WHITE)) {
            makeMoves("D F L");
        } else if (colorMatch(cube[1], stickerMasks[1][2], BLUE) && colorMatch(cube[2], stickerMasks[2][1], WHITE)) {
            makeMoves("D' F L");
        } else if (colorMatch(cube[1], stickerMasks[2][1], BLUE) && colorMatch(cube[5], stickerMasks[2][1], WHITE)) {
            makeMoves("B' L'");
        } // Check blue on faceB
        else if (colorMatch(cube[5], stickerMasks[0][1], BLUE) && colorMatch(cube[0], stickerMasks[0][1], WHITE)) {
            makeMoves("U' L2");
        } else if (colorMatch(cube[5], stickerMasks[1][0], BLUE) && colorMatch(cube[2], stickerMasks[1][2], WHITE)) {
            makeMoves("B' D");
        } else if (colorMatch(cube[5], stickerMasks[1][2], BLUE) && colorMatch(cube[3], stickerMasks[1][0], WHITE)) {
            makeMoves("B D");
        } else if (colorMatch(cube[5], stickerMasks[2][1], BLUE) && colorMatch(cube[1], stickerMasks[2][1], WHITE)) {
            makeMoves("D");
        }
    }

    static void loadBlueRedEdge() {
        System.out.println("Called");
        if ((trioMatch(3, 2, 2, BLUE, 4, 2, 0, RED, 1, 0, 0, WHITE) && pairMatch(3, 1, 2, BLUE, 4, 1, 0, RED, false))
                || pairMatch(4, 2, 1, BLUE, 1, 0, 1, RED, true)) {
            return;
        } // FaceU
        else if (pairMatch(0, 0, 1, BLUE, 5, 0, 1, RED, true)) {
            makeMoves("M2");
        } else if (pairMatch(0, 1, 0, BLUE, 3, 0, 1, RED, true)) {
            makeMoves("U' M");
        } else if (pairMatch(0, 1, 1, BLUE, 2, 0, 1, RED, true)) {
            makeMoves("U M");
        } else if (pairMatch(0, 2, 1, BLUE, 4, 0, 1, RED, true)) {
            makeMoves("M");
        } // FaceF
        else if (pairMatch(4, 1, 0, BLUE, 3, 1, 2, RED, true)) {
            makeMoves("F'");
        } else if (pairMatch(4, 1, 2, BLUE, 2, 1, 0, RED, true)) {
            makeMoves("F");
        } // FaceR
        else if (pairMatch(2, 1, 2, BLUE, 5, 1, 0, RED, true)) {
            makeMoves("R2 F");
        } else if (pairMatch(2, 2, 1, BLUE, 1, 1, 2, RED, true)) {
            makeMoves("R F");
        } // FaceB
        else if (pairMatch(5, 1, 2, BLUE, 3, 1, 0, RED, true)) {
            makeMoves("B M'");
        } else if (pairMatch(5, 2, 1, BLUE, 1, 2, 1, RED, true)) {
            makeMoves("M'");
        }
    }

    static void solveBlueRedBlock() {
        if ((trioMatch(3, 2, 2, BLUE, 4, 2, 0, RED, 1, 0, 0, WHITE) && pairMatch(3, 1, 2, BLUE, 4, 1, 0, RED, false))) {
            return;
        } // faceF is BLUE
        else if (colorMatch(cube[4], stickerMasks[2][1], BLUE)) {
            System.out.println("Entered");
            // faceU BLUE for the corner, WHITE for the other sticker, RED will follow
            if (pairMatch(0, 0, 0, BLUE, 5, 0, 2, WHITE, false)) {
                makeMoves("U' M' F'");
            } else if (pairMatch(0, 0, 2, BLUE, 2, 0, 2, WHITE, false)) {
                makeMoves("U2 M' F'");
            } else if (pairMatch(0, 2, 0, BLUE, 3, 0, 2, WHITE, false)) {
                makeMoves("M' F'");
            } else if (pairMatch(0, 2, 2, BLUE, 4, 0, 2, WHITE, false)) {
                makeMoves("U M' F'");
            } // faceF BLUE...
            else if (pairMatch(4, 0, 0, BLUE, 0, 2, 0, WHITE, false)) {
                makeMoves("M F2 r U M' F'");
            } else if (pairMatch(4, 0, 2, BLUE, 2, 0, 0, WHITE, false)) {
                makeMoves("U2 M2 U2 r' F");
            } else if (pairMatch(4, 2, 0, BLUE, 3, 2, 2, WHITE, false)) {
                makeMoves("F' R U F'");
            } else if (pairMatch(4, 2, 2, BLUE, 1, 0, 2, WHITE, false)) {
                makeMoves("R U M' F'");
            } // faceR BLUE...
            else if (pairMatch(2, 0, 0, BLUE, 0, 2, 0, WHITE, false)) {
                makeMoves("M F' M2 F'");
            } else if (pairMatch(2, 0, 2, BLUE, 5, 0, 0, WHITE, false)) {
                makeMoves("r' F' M2 F'");
            } else if (pairMatch(2, 2, 0, BLUE, 4, 2, 2, WHITE, false)) {
                makeMoves("R M F' M2 F'");
            } else if (pairMatch(2, 2, 2, BLUE, 1, 2, 2, WHITE, false)) {
                makeMoves("R2 M F' M2 F'");
            } // faceB BLUE...
            else if (pairMatch(5, 0, 0, BLUE, 0, 0, 2, WHITE, false)) {
                makeMoves("R' U M' F'");
            } else if (pairMatch(5, 0, 2, BLUE, 3, 0, 0, WHITE, false)) {
                makeMoves("M2 U2 r' F");
            } else if (pairMatch(5, 2, 0, BLUE, 2, 2, 2, WHITE, false)) {
                makeMoves("R' U2 M' F'");
            } else if (pairMatch(5, 2, 2, BLUE, 1, 2, 0, WHITE, false)) {
                makeMoves("B2 R' U M' F'");
            } // faceL BLUE...
            else if (pairMatch(3, 0, 0, BLUE, 0, 0, 0, WHITE, false)) {
                makeMoves("B' U2 M' F'");
            } else if (pairMatch(3, 0, 2, BLUE, 4, 0, 0, WHITE, false)) {
                makeMoves("U M2 U2 r' F");
            } else if (pairMatch(3, 2, 0, BLUE, 5, 2, 2, WHITE, false)) {
                makeMoves("B' U' M' F'");
            } else if (pairMatch(3, 2, 2, BLUE, 1, 0, 0, WHITE, false)) {
                makeMoves("M F M2 F'");
            } // faceD BLUE...
            else if (pairMatch(1, 0, 0, BLUE, 4, 2, 0, WHITE, false)) {
                makeMoves("M F2 U M2 F'");
            } else if (pairMatch(1, 0, 2, BLUE, 2, 2, 0, WHITE, false)) {
                makeMoves("M F2 M2 F'");
            } else if (pairMatch(1, 2, 0, BLUE, 3, 2, 0, WHITE, false)) {
                makeMoves("M B2 U2 F'");
            } else if (pairMatch(1, 2, 2, BLUE, 5, 2, 0, WHITE, false)) {
                makeMoves("R2 U M2 F'");
            }
        }
    }
}
