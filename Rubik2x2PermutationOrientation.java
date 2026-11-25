import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

public class Rubik2x2PermutationOrientation {
    static final byte ULB = 0b000;
    static final byte UBR = 0b001;
    static final byte URF = 0b010;
    static final byte UFL = 0b011;
    static final byte DLF = 0b100;
    static final byte DFR = 0b101;
    static final byte DRB = 0b110;
    static final byte DBL = 0b111;

    static final byte ORIENT0 = 0b00;
    static final byte ORIENT1 = 0b01; // i.e. ULB -> BUL
    static final byte ORIENT2 = 0b10; // i.e. ULB -> LBU

    static final int PERM_OFFSET = 16; // >> 14 to get list of perms MSB -> LSB 0, 1, 2, ... corners
    static final int ULB_OFFSET = 21; // * 3
    static final int UBR_OFFSET = 18;
    static final int URF_OFFSET = 15;
    static final int UFL_OFFSET = 12;
    static final int DLF_OFFSET = 9;
    static final int DFR_OFFSET = 6;
    static final int DRB_OFFSET = 3;
    static final int DBL_OFFSET = 0;

    static final int ULB_OFFSET_O = 14; // * 2
    static final int UBR_OFFSET_O = 12;
    static final int URF_OFFSET_O = 10;
    static final int UFL_OFFSET_O = 8;
    static final int DLF_OFFSET_O = 6;
    static final int DFR_OFFSET_O = 4;
    static final int DRB_OFFSET_O = 2;
    static final int DBL_OFFSET_O = 0;

    static final int WHITE = 0b000;
    static final int YELLOW = 0b001;
    static final int BLUE = 0b010;
    static final int GREEN = 0b011;
    static final int RED = 0b100;
    static final int ORANGE = 0b101;

    static final int R_CORNER_MASK = 0b000_111_111_000_000_111_111_000;
    static final int L_CORNER_MASK = 0b111_000_000_111_111_000_000_111;

    static final long CLEAR_PERM = 0b000_000_000_000_000_000_000_000_11_11_11_11_11_11_11_11l;

    static final long CLEAR_U_CORNERS = 0b000_000_000_000_111_111_111_111_11_11_11_11_11_11_11_11l;
    static final long CLEAR_D_CORNERS = 0b111_111_111_111_000_000_000_000_11_11_11_11_11_11_11_11l;
    static final long CLEAR_R_CORNERS = 0b111_000_000_111_111_000_000_111_11_11_11_11_11_11_11_11l;
    static final long CLEAR_L_CORNERS = 0b000_111_111_000_000_111_111_000_11_11_11_11_11_11_11_11l;

    static final long CLEAR_ORIENT = 0b111_111_111_111_111_111_111_111_00_00_00_00_00_00_00_00l;

    static final long CLEAR_U_CORNERS_O = 0b111_111_111_111_111_111_111_111_00_00_00_00_11_11_11_11l;
    static final long CLEAR_D_CORNERS_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_00_00_00_00l;
    static final long CLEAR_R_CORNERS_O = 0b111_111_111_111_111_111_111_111_11_00_00_11_11_00_00_11l;
    static final long CLEAR_L_CORNERS_O = 0b111_111_111_111_111_111_111_111_00_11_11_00_00_11_11_00l;

    static final long CLEAR_ULB_O = 0b111_111_111_111_111_111_111_111_00_11_11_11_11_11_11_11l;
    static final long CLEAR_UBR_O = 0b111_111_111_111_111_111_111_111_11_00_11_11_11_11_11_11l;
    static final long CLEAR_URF_O = 0b111_111_111_111_111_111_111_111_11_11_00_11_11_11_11_11l;
    static final long CLEAR_UFL_O = 0b111_111_111_111_111_111_111_111_11_11_11_00_11_11_11_11l;
    static final long CLEAR_DLF_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_00_11_11_11l;
    static final long CLEAR_DFR_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_11_00_11_11l;
    static final long CLEAR_DRB_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_11_11_00_11l;
    static final long CLEAR_DBL_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_11_11_11_00l;

    static final int[][] CORNER_COLORS = {
            { YELLOW, BLUE, ORANGE },
            { YELLOW, ORANGE, GREEN },
            { YELLOW, GREEN, RED },
            { YELLOW, RED, BLUE },
            { WHITE, BLUE, RED },
            { WHITE, RED, GREEN },
            { WHITE, GREEN, ORANGE },
            { WHITE, ORANGE, BLUE },
    };

    static HashMap<Integer, String> bitsToColor = new HashMap<>();

    // Permutation ULB UBR URF UFL DLF DFR DRB DBL
    // Orientation changes too
    // DBL orientation calculated
    static long state = 0b000_001_010_011_100_101_110_111_00_00_00_00_00_00_00_00l;

    public static void main(String[] args) {
        initColorMap();
        displayCube();
        // long startT = System.nanoTime();
        // for (int i = 0; i < 1_000_000_000; i++) {
        // U();
        // }
        // long endT = System.nanoTime();
        // System.out.println("MilliSeconds " + ((endT - startT) / 1_000_000));
        L2();
        // System.out.println(String.format("%40s",
        // Long.toBinaryString(state)).replace(" ", "0"));
        // Ri();
        // U();
        // R();
        // D2();
        // R2();
        // U2();
        // D();
        // R();
        // Ui();
        // System.out.println(String.format("%40s",
        // Long.toBinaryString(state)).replace(" ", "0"));
        // displayCube();
        // System.out.println(String.format("%40s",
        // Long.toBinaryString(state)).replace(" ", "0"));
        displayCube();
    }

    static void initColorMap() {
        bitsToColor.put(WHITE, "\u001B[37mW");
        bitsToColor.put(YELLOW, "\u001B[33mY");
        bitsToColor.put(BLUE, "\u001B[34mB");
        bitsToColor.put(GREEN, "\u001B[32mG");
        bitsToColor.put(RED, "\u001B[31mR");
        bitsToColor.put(ORANGE, "\033[38;2;255;125;0mO");

        // charToColorMap.put('W', "\u001B[37mW");
        // charToColorMap.put('Y', "\u001B[33mY");
        // charToColorMap.put('B', "\u001B[34mB");
        // charToColorMap.put('G', "\u001B[32mG");
        // charToColorMap.put('R', "\u001B[31mR");
        // charToColorMap.put('O', "\033[38;2;255;125;0mO");

        // charColorToBits.put('W', WHITE);
        // charColorToBits.put('Y', YELLOW);
        // charColorToBits.put('B', BLUE);
        // charColorToBits.put('G', GREEN);
        // charColorToBits.put('R', RED);
        // charColorToBits.put('O', ORANGE);
    }

    static void displayHelper(int perms, int cornerOffset, int orientOffset, int o0i, int o1i, int o2i) {

        int corner = ((perms >> (cornerOffset)) & 0b111);
        int[] colors = CORNER_COLORS[corner];
        if (((state >> orientOffset) & 0b11) == ORIENT0) {
            System.out.print(bitsToColor.get(colors[o0i]));
        } else if (((state >> orientOffset) & 0b11) == ORIENT1) {
            System.out.print(bitsToColor.get(colors[o1i]));
        } else { // ORIENT2
            System.out.print(bitsToColor.get(colors[o2i]));
        }
    }

    static void displayCube() {
        int perms = (int) ((state >> PERM_OFFSET) & 0b111_111_111_111_111_111_111_111);
        System.out.print("\n  ");
        // Print U of the ULB corner
        displayHelper(perms, ULB_OFFSET, ULB_OFFSET_O, 0, 2, 1);
        // Print U of the UBR corner
        displayHelper(perms, UBR_OFFSET, UBR_OFFSET_O, 0, 2, 1);
        System.out.print("\n  ");
        // Print U of the UFL corner
        displayHelper(perms, UFL_OFFSET, UFL_OFFSET_O, 0, 2, 1);
        // Print U of the URF corner
        displayHelper(perms, URF_OFFSET, URF_OFFSET_O, 0, 2, 1);

        System.out.println();

        // Print L of the ULB corner
        displayHelper(perms, ULB_OFFSET, ULB_OFFSET_O, 1, 0, 2);
        // Print L of the UFL corner
        displayHelper(perms, UFL_OFFSET, UFL_OFFSET_O, 2, 1, 0);
        // Print F of the UFL corner
        displayHelper(perms, UFL_OFFSET, UFL_OFFSET_O, 1, 0, 2);
        // Print F of the URF corner
        displayHelper(perms, URF_OFFSET, URF_OFFSET_O, 2, 1, 0);
        // Print R of the URF corner
        displayHelper(perms, URF_OFFSET, URF_OFFSET_O, 1, 0, 2);
        // Print R of the UBR corner
        displayHelper(perms, UBR_OFFSET, UBR_OFFSET_O, 2, 1, 0);
        // Print B of the UBR corner
        displayHelper(perms, UBR_OFFSET, UBR_OFFSET_O, 1, 0, 2);
        // Print B of the ULB corner
        displayHelper(perms, ULB_OFFSET, ULB_OFFSET_O, 2, 1, 0);
        System.out.println();
        // Print L of the DBL corner
        displayHelper(perms, DBL_OFFSET, DBL_OFFSET_O, 2, 1, 0);
        // Print L of the DLF corner
        displayHelper(perms, DLF_OFFSET, DLF_OFFSET_O, 1, 0, 2);
        // Print F of the DLF corner
        displayHelper(perms, DLF_OFFSET, DLF_OFFSET_O, 2, 1, 0);
        // Print F of the DFR corner
        displayHelper(perms, DFR_OFFSET, DFR_OFFSET_O, 1, 0, 2);
        // Print R of the DFR corner
        displayHelper(perms, DFR_OFFSET, DFR_OFFSET_O, 2, 1, 0);
        // Print R of the DRB corner
        displayHelper(perms, DRB_OFFSET, DRB_OFFSET_O, 1, 0, 2);
        // Print B of the DRB corner
        displayHelper(perms, DRB_OFFSET, DRB_OFFSET_O, 2, 1, 0);
        // Print B of the DBL corner
        displayHelper(perms, DBL_OFFSET, DBL_OFFSET_O, 1, 0, 2);
        System.out.print("\n  ");
        // Print D of the DLF corner
        displayHelper(perms, DLF_OFFSET, DLF_OFFSET_O, 0, 2, 1);
        // Print D of the DFR corner
        displayHelper(perms, DFR_OFFSET, DFR_OFFSET_O, 0, 2, 1);
        System.out.print("\n  ");
        // Print D of the DBL corner
        displayHelper(perms, DBL_OFFSET, DBL_OFFSET_O, 0, 2, 1);
        // Print D of the DRB corner
        displayHelper(perms, DRB_OFFSET, DRB_OFFSET_O, 0, 2, 1);
        System.out.println();
    }

    static int getLastOrientation() {
        int sum = 0;

        for (int i = 0; i < 7; i++) {
            sum += (int) ((state >>> (i * 2)) & 0b11);
        }

        return (3 - sum % 3) % 3;
    }

    static void U() {
        state = (((state & CLEAR_U_CORNERS)
                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 3) & 0b000_111_111_111)
                        | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 9)
                                & 0b111_000_000_000)) << (PERM_OFFSET + 12)))
                & CLEAR_U_CORNERS_O)
                | ((((((state >> 8) & 0b11_11_11_11) >> 2) & 0b00_11_11_11)
                        | ((((state >> 8) & 0b11_11_11_11) << 6) & 0b11_00_00_00)) << 8);
    }

    static void Ui() {
        state = (((state & CLEAR_U_CORNERS)
                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 3) & 0b111_111_111_000)
                        | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 9)
                                & 0b000_000_000_111)) << (PERM_OFFSET + 12)))
                & CLEAR_U_CORNERS_O)
                | ((((((state >> 8) & 0b11_11_11_11) << 2) & 0b11_11_11_00)
                        | ((((state >> 8) & 0b11_11_11_11) >> 6) & 0b00_00_00_11)) << 8);
    }

    static void U2() {
        state = (((state & CLEAR_U_CORNERS)
                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 6) & 0b111_111_000_000)
                        | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 6)
                                & 0b000_000_111_111)) << (PERM_OFFSET + 12)))
                & CLEAR_U_CORNERS_O)
                | ((((((state >> 8) & 0b11_11_11_11) << 4) & 0b11_11_00_00)
                        | ((((state >> 8) & 0b11_11_11_11) >> 4) & 0b00_00_11_11)) << 8);
    }

    static void D() {
        state = (((state & CLEAR_D_CORNERS)
                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 3) & 0b000_111_111_111)
                        | ((((state >> PERM_OFFSET) & 0b111_111_111_111) << 9) & 0b111_000_000_000)) << PERM_OFFSET))
                & CLEAR_D_CORNERS_O)
                | ((((state & 0b11_11_11_11) >> 2) & 0b00_11_11_11) | (((state & 0b11_11_11_11) << 6) & 0b11_00_00_00));
    }

    static void Di() {
        state = (((state & CLEAR_D_CORNERS)
                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) << 3) & 0b111_111_111_000)
                        | ((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 9) & 0b000_000_000_111)) << PERM_OFFSET))
                & CLEAR_D_CORNERS_O)
                | ((((state & 0b11_11_11_11) << 2) & 0b11_11_11_00) | (((state & 0b11_11_11_11) >> 6) & 0b00_00_00_11));
    }

    static void D2() {
        state = (((state & CLEAR_D_CORNERS)
                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) << 6) & 0b111_111_000_000)
                        | ((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 6) & 0b000_000_111_111)) << PERM_OFFSET))
                & CLEAR_D_CORNERS_O)
                | ((((state & 0b11_11_11_11) << 4) & 0b11_11_00_00) | (((state & 0b11_11_11_11) >> 4) & 0b00_00_11_11));
    }

    static void R() {
        long face = ((state >> PERM_OFFSET) & R_CORNER_MASK);
        state = (state & CLEAR_R_CORNERS) | ((((face >> 15) & 0b000_000_000_000_000_000_111_000)
                | ((face << 3) & 0b000_111_000_000_000_111_000_000)
                | ((face << 9) & 0b000_000_111_000_000_000_000_000)) << PERM_OFFSET);

        face = state & 0b00_11_11_00_00_11_11_00;
        state = (state & CLEAR_R_CORNERS_O) | (((face << 2) & 0b00_11_00_00_00_11_00_00)
                | ((face >> 10) & 0b00_00_00_00_00_00_11_00) | ((face << 6) & 0b00_00_11_00_00_00_00_00));

        // TODO make it faster
        byte orient = (byte) ((state >> URF_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_URF_O) | (orient << URF_OFFSET_O);

        orient = (byte) ((state >> UBR_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_UBR_O) | (orient << UBR_OFFSET_O);

        orient = (byte) ((state >> DRB_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_DRB_O) | (orient << DRB_OFFSET_O);

        orient = (byte) ((state >> DFR_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_DFR_O) | (orient << DFR_OFFSET_O);
    }

    static void Ri() {
        long face = ((state >> PERM_OFFSET) & R_CORNER_MASK);
        state = (state & CLEAR_R_CORNERS) | ((((face >> 9) & 0b000_000_000_000_000_111_000_000)
                | ((face >> 3) & 0b000_000_111_000_000_000_111_000)
                | ((face << 15) & 0b000_111_000_000_000_000_000_000)) << PERM_OFFSET);

        face = state & 0b00_11_11_00_00_11_11_00;
        state = (state & CLEAR_R_CORNERS_O)
                | (((face >> 2) & 0b00_00_11_00_00_00_11_00) | ((face << 10) & 0b00_11_00_00_00_00_00_00)
                        | ((face >> 6) & 0b00_00_00_00_00_11_00_00));

        // URF
        byte orient = (byte) ((state >> URF_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_URF_O) | (orient << URF_OFFSET_O);

        orient = (byte) ((state >> UBR_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_UBR_O) | (orient << UBR_OFFSET_O);

        orient = (byte) ((state >> DRB_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_DRB_O) | (orient << DRB_OFFSET_O);

        orient = (byte) ((state >> DFR_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_DFR_O) | (orient << DFR_OFFSET_O);
    }

    static void R2() {
        state = (((state & CLEAR_R_CORNERS) | ((((((state >> PERM_OFFSET) & R_CORNER_MASK) >> 12)
                & 0b000_000_000_000_000_111_111_000)
                | ((((state >> PERM_OFFSET) & R_CORNER_MASK) << 12)
                        & 0b000_111_111_000_000_000_000_000)) << PERM_OFFSET))
                & CLEAR_R_CORNERS_O)
                | ((((state & 0b00_11_11_00_00_11_11_00) << 8) & 0b00_11_11_00_00_00_00_00)
                        | (((state & 0b00_11_11_00_00_11_11_00) >> 8) & 0b00_00_00_00_00_11_11_00));
    }

    static void L() {
        long face = ((state >> PERM_OFFSET) & L_CORNER_MASK);
        state = (state & CLEAR_L_CORNERS) | ((((face >> 9) & 0b000_000_000_111_000_000_000_111)
                | ((face << 21) & 0b111_000_000_000_000_000_000_000)
                | ((face >> 3) & 0b000_000_000_000_111_000_000_000)) << PERM_OFFSET);

        face = state & 0b11_00_00_11_11_00_00_11;
        state = (state & CLEAR_L_CORNERS_O) | (((face >> 6) & 0b00_00_00_11_00_00_00_11)
                | ((face >> 2) & 0b00_00_00_00_11_00_00_00) | ((face << 14) & 0b11_00_00_00_00_00_00_00));

        byte orient = (byte) ((state >> ULB_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_ULB_O) | (orient << ULB_OFFSET_O);

        orient = (byte) ((state >> UFL_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_UFL_O) | (orient << UFL_OFFSET_O);

        orient = (byte) ((state >> DLF_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_DLF_O) | (orient << DLF_OFFSET_O);

        orient = (byte) ((state >> DBL_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_DBL_O) | (orient << DBL_OFFSET_O);
    }

    static void Li() {
        long face = ((state >> PERM_OFFSET) & L_CORNER_MASK);
        state = (state & CLEAR_L_CORNERS) | ((((face << 9) & 0b111_000_000_000_111_000_000_000)
                | ((face >> 21) & 0b000_000_000_000_000_000_000_111)
                | ((face << 3) & 0b000_000_000_111_000_000_000_000)) << PERM_OFFSET);

        face = state & 0b11_00_00_11_11_00_00_11;
        state = (state & CLEAR_L_CORNERS_O) | (((face << 6) & 0b11_00_00_00_11_00_00_00)
                | ((face << 2) & 0b00_00_00_11_00_00_00_00) | ((face >> 14) & 0b00_00_00_00_00_00_00_11));

        byte orient = (byte) ((state >> ULB_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_ULB_O) | (orient << ULB_OFFSET_O);

        orient = (byte) ((state >> UFL_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_UFL_O) | (orient << UFL_OFFSET_O);

        orient = (byte) ((state >> DLF_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT2 : orient == 1 ? ORIENT0 : ORIENT1;
        state = (state & CLEAR_DLF_O) | (orient << DLF_OFFSET_O);

        orient = (byte) ((state >> DBL_OFFSET_O) & 0b11);
        orient = orient == 0 ? ORIENT1 : orient == 1 ? ORIENT2 : ORIENT0;
        state = (state & CLEAR_DBL_O) | (orient << DBL_OFFSET_O);
    }

    static void L2() {
        state = (((state & CLEAR_L_CORNERS)
                | ((((((state >> PERM_OFFSET) & L_CORNER_MASK) >> 12) & 0b000_000_000_000_111_000_000_111)
                        | ((((state >> PERM_OFFSET) & L_CORNER_MASK) << 12)
                                & 0b111_000_000_111_000_000_000_000)) << PERM_OFFSET))
                & CLEAR_L_CORNERS_O)
                | ((((state & 0b11_00_00_11_11_00_00_11) >> 8) & 0b00_00_00_00_11_00_00_11)
                        | (((state & 0b11_00_00_11_11_00_00_11) << 8) & 0b11_00_00_11_00_00_00_00));
    }
}
