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

/* 100000000 iters
867
888
889
885
896
891
900
889
896
898
886
893
886
886
883
884
884
894
895
891
885
884
887
887
884
891
948
953
935
914
912
910
932
970
944
941
938
920
931
946
923
922
928
945
939
961
921
913
904
897
901
888
885
906
904
911 */

import java.util.HashMap;

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

    static int[] cube = { faceU, faceD, faceR, faceL, faceF, faceB };

    static HashMap<Integer, String> bitsToColorMap = new HashMap<>();

    public static void main(String[] args) {
        initColorMap();
        displayCube();
        for (int g = 0; g < 100; g++) {
            long startTime = System.nanoTime();
            for (int i = 0; i < 100000000; i++) {
                U();
            }
            long endTime = System.nanoTime();
            System.out.println((endTime - startTime) / 1000000);
        }
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
            System.out.print(getColor(faceU, i));
            counter++;
        }

        // Display LFRB Face
        counter = 8;// Row 1
        System.out.print("\n" +
                getColor(faceL, counter) + getColor(faceL, counter - 1) + getColor(faceL, counter - 2)
                + getColor(faceF, counter) + getColor(faceF, counter - 1) + getColor(faceF, counter - 2)
                + getColor(faceR, counter) + getColor(faceR, counter - 1) + getColor(faceR, counter - 2)
                + getColor(faceB, counter) + getColor(faceB, counter - 1) + getColor(faceB, counter - 2));
        counter = 5;// Row 2
        System.out.print("\n" +
                getColor(faceL, counter) + getColor(faceL, counter - 1) + getColor(faceL, counter - 2)
                + getColor(faceF, counter) + getColor(faceF, counter - 1) + getColor(faceF, counter - 2)
                + getColor(faceR, counter) + getColor(faceR, counter - 1) + getColor(faceR, counter - 2)
                + getColor(faceB, counter) + getColor(faceB, counter - 1) + getColor(faceB, counter - 2));
        counter = 2; // Row 3
        System.out.print("\n" +
                getColor(faceL, counter) + getColor(faceL, counter - 1) + getColor(faceL, counter - 2)
                + getColor(faceF, counter) + getColor(faceF, counter - 1) + getColor(faceF, counter - 2)
                + getColor(faceR, counter) + getColor(faceR, counter - 1) + getColor(faceR, counter - 2)
                + getColor(faceB, counter) + getColor(faceB, counter - 1) + getColor(faceB, counter - 2));

        // Display D Face
        counter = 0;
        System.out.print("\n   ");
        for (int i = 8; i >= 0; i--) {
            if (counter % 3 == 0 && counter > 0) {
                System.out.print("\n   ");
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
     * U Move: 2x = U2, 3x = U'
     * faceU bitmap becomes
     * 741
     * 852
     * 963
     * 1st row of faceL becomes 1st row for faceB
     * 1st row of faceF becomes 1st row for faceL...
     */
    static void U() {
        cube[0] = rotate90(cube[0], false);
        int temp = cube[3] & KEEP_ROW_1_MASK;
        // Clear the row, then replace with correct row
        cube[3] = updatePos(cube[3], CLEAR_ROW_1_MASK, cube[4], KEEP_ROW_1_MASK);
        cube[4] = updatePos(cube[4], CLEAR_ROW_1_MASK, cube[2], KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_1_MASK, cube[5], KEEP_ROW_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
    }

    static void D() {
        cube[1] = rotate90(cube[1], false);
        int temp = cube[3] & KEEP_ROW_3_MASK;
        // Clear the row, then replace with correct row
        cube[3] = updatePos(cube[3], CLEAR_ROW_3_MASK, cube[5], KEEP_ROW_3_MASK);
        cube[5] = updatePos(cube[5], CLEAR_ROW_3_MASK, cube[2], KEEP_ROW_3_MASK);
        cube[2] = updatePos(cube[2], CLEAR_ROW_3_MASK, cube[4], KEEP_ROW_3_MASK);
        cube[4] = updatePos(cube[4], CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
    }

    static void R() {
        cube[2] = rotate90(cube[2], false);
        int temp = cube[0] & KEEP_COL_3_MASK;
        // Clear the row, then replace with correct row
        cube[0] = updatePos(cube[0], CLEAR_COL_3_MASK, cube[4], KEEP_COL_3_MASK);
        cube[4] = updatePos(cube[4], CLEAR_COL_3_MASK, cube[1], KEEP_COL_3_MASK);
        // To match the back face to the rest of the cube
        cube[1] = updatePos(cube[1], CLEAR_COL_3_MASK, rotate90(rotate90(cube[5], false), false), KEEP_COL_3_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_1_MASK, rotate90(rotate90(temp, false), false), KEEP_COL_1_MASK);
    }

    static void L() {
        cube[3] = rotate90(cube[3], false);
        int temp = cube[1] & KEEP_COL_1_MASK;
        // Clear the row, then replace with correct row
        cube[1] = updatePos(cube[1], CLEAR_COL_1_MASK, cube[4], KEEP_COL_1_MASK);
        cube[4] = updatePos(cube[4], CLEAR_COL_1_MASK, cube[0], KEEP_COL_1_MASK);
        // To match the back face to the rest of the cube
        cube[0] = updatePos(cube[0], CLEAR_COL_1_MASK, rotate90(rotate90(cube[5], false), false), KEEP_COL_1_MASK);
        cube[5] = updatePos(cube[5], CLEAR_COL_3_MASK, rotate90(rotate90(temp, false), false), KEEP_COL_3_MASK);
    }

    static void F() {
        cube[4] = rotate90(cube[4], false);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_3_MASK, rotate90(cube[3], false), KEEP_ROW_3_MASK);
        cube[3] = updatePos(cube[3], CLEAR_COL_3_MASK, rotate90(cube[1], false), KEEP_COL_3_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_1_MASK, rotate90(cube[2], false), KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_COL_1_MASK, rotate90(temp, false), KEEP_COL_1_MASK);
    }

    static void B() {
        cube[5] = rotate90(cube[5], false);
        int temp = cube[0];
        cube[0] = updatePos(cube[0], CLEAR_ROW_1_MASK, rotate90(cube[2], true), KEEP_ROW_1_MASK);
        cube[2] = updatePos(cube[2], CLEAR_COL_3_MASK, rotate90(cube[1], true), KEEP_COL_3_MASK);
        cube[1] = updatePos(cube[1], CLEAR_ROW_3_MASK, rotate90(cube[3], true), KEEP_ROW_3_MASK);
        cube[3] = updatePos(cube[3], CLEAR_COL_1_MASK, rotate90(temp, true), KEEP_COL_1_MASK);
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
    static int rotate90(int face, boolean inverse) {
        return inverse ? (((face >>> 0) & 7) << 18) | (((face >>> 3) & 7) << 9) | (((face >>> 6) & 7) << 0) |
                (((face >>> 9) & 7) << 21) | (((face >>> 12) & 7) << 12) | (((face >>> 15) & 7) << 3)
                | (((face >>> 18) & 7) << 24) | (((face >>> 21) & 7) << 15) | (((face >>> 24) & 7) << 6)
                : (((face >>> 0) & 7) << 6) | (((face >>> 3) & 7) << 15) | (((face >>> 6) & 7) << 24) |
                        (((face >>> 9) & 7) << 3) | (((face >>> 12) & 7) << 12) | (((face >>> 15) & 7) << 21) |
                        (((face >>> 18) & 7) << 0) | (((face >>> 21) & 7) << 9) | (((face >>> 24) & 7) << 18);
    }
}
