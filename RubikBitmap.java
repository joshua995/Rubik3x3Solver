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
*/

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

    static int faceU = 0b000_000_000_000_000_000_000_000_000;
    static int faceD = 0b001_001_001_001_001_001_001_001_001;
    static int faceR = 0b100_010_011_101_001_100_000_001_101;
    static int faceL = 0b010_010_010_010_010_010_010_010_010;
    static int faceF = 0b100_100_100_100_100_100_100_100_100;
    static int faceB = 0b101_101_101_101_101_101_101_101_101;

    static HashMap<Integer, String> bitsToColorMap = new HashMap<>();

    public static void main(String[] args) {
        initColorMap();
        displayCube();
        R();
        displayCube();
    }

    static void initColorMap() {
        bitsToColorMap.put(WHITE, "W");
        bitsToColorMap.put(YELLOW, "Y");
        bitsToColorMap.put(BLUE, "B");
        bitsToColorMap.put(GREEN, "G");
        bitsToColorMap.put(RED, "R");
        bitsToColorMap.put(ORANGE, "O");
    }

    static void displayCube() {

        int counter = 0;

        // Display U Face
        System.out.print("\n   ");
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
        faceU = rotate90(faceU, false);
        int temp = faceL & KEEP_ROW_1_MASK;
        // Clear the row, then replace with correct row
        faceL = helperU(faceL, CLEAR_ROW_1_MASK, faceF, KEEP_ROW_1_MASK);
        faceF = helperU(faceF, CLEAR_ROW_1_MASK, faceR, KEEP_ROW_1_MASK);
        faceR = helperU(faceR, CLEAR_ROW_1_MASK, faceB, KEEP_ROW_1_MASK);
        faceB = helperU(faceB, CLEAR_ROW_1_MASK, temp, KEEP_ROW_1_MASK);
    }

    static void D() {
        faceD = rotate90(faceD, false);
        int temp = faceL & KEEP_ROW_3_MASK;
        // Clear the row, then replace with correct row
        faceL = helperU(faceL, CLEAR_ROW_3_MASK, faceB, KEEP_ROW_3_MASK);
        faceB = helperU(faceB, CLEAR_ROW_3_MASK, faceR, KEEP_ROW_3_MASK);
        faceR = helperU(faceR, CLEAR_ROW_3_MASK, faceF, KEEP_ROW_3_MASK);
        faceF = helperU(faceF, CLEAR_ROW_3_MASK, temp, KEEP_ROW_3_MASK);
    }

    static void R() {
        faceR = rotate90(faceR, false);
        int temp = faceU & KEEP_COL_3_MASK;
        // Clear the row, then replace with correct row
        faceU = helperU(faceU, CLEAR_COL_3_MASK, faceF, KEEP_COL_3_MASK);
        faceF = helperU(faceF, CLEAR_COL_3_MASK, faceD, KEEP_COL_3_MASK);
        faceD = helperU(faceD, CLEAR_COL_3_MASK, faceB, KEEP_COL_3_MASK);
        faceB = helperU(faceB, CLEAR_COL_3_MASK, temp, KEEP_COL_3_MASK);
    }

    static int helperU(int currentFace, int currentMask, int otherFace, int otherMask) {
        return (currentFace & currentMask) | (otherFace & otherMask);
    }

    static int rotate90(int face, boolean inverse) {
        return inverse ? (((face >>> 0) & 7) << 18) |
                (((face >>> 3) & 7) << 9) |
                (((face >>> 6) & 7) << 0) |
                (((face >>> 9) & 7) << 21) |
                (((face >>> 12) & 7) << 12) |
                (((face >>> 15) & 7) << 3) |
                (((face >>> 18) & 7) << 24) |
                (((face >>> 21) & 7) << 15) |
                (((face >>> 24) & 7) << 6)
                : (((face >>> 0) & 7) << 6) |
                        (((face >>> 3) & 7) << 15) |
                        (((face >>> 6) & 7) << 24) |
                        (((face >>> 9) & 7) << 3) |
                        (((face >>> 12) & 7) << 12) |
                        (((face >>> 15) & 7) << 21) |
                        (((face >>> 18) & 7) << 0) |
                        (((face >>> 21) & 7) << 9) |
                        (((face >>> 24) & 7) << 18);
    }

}
