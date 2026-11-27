import java.util.HashMap;

//TODO change set of bitwise operations if repeating 3 or more times

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

        static final byte PERM_OFFSET = 16; // >> 14 to get list of perms MSB -> LSB 0, 1, 2, ... corners
        static final byte ULB_OFFSET = 21; // * 3
        static final byte UBR_OFFSET = 18;
        static final byte URF_OFFSET = 15;
        static final byte UFL_OFFSET = 12;
        static final byte DLF_OFFSET = 9;
        static final byte DFR_OFFSET = 6;
        static final byte DRB_OFFSET = 3;
        static final byte DBL_OFFSET = 0;

        static final byte ULB_OFFSET_O = 14; // * 2
        static final byte UBR_OFFSET_O = 12;
        static final byte URF_OFFSET_O = 10;
        static final byte UFL_OFFSET_O = 8;
        static final byte DLF_OFFSET_O = 6;
        static final byte DFR_OFFSET_O = 4;
        static final byte DRB_OFFSET_O = 2;
        static final byte DBL_OFFSET_O = 0;

        static final byte WHITE = 0b000;
        static final byte YELLOW = 0b001;
        static final byte BLUE = 0b010;
        static final byte GREEN = 0b011;
        static final byte RED = 0b100;
        static final byte ORANGE = 0b101;

        static final int R_CORNER_MASK = 0b000_111_111_000_000_111_111_000;
        static final int L_CORNER_MASK = 0b111_000_000_111_111_000_000_111;
        static final int F_CORNER_MASK = 0b000_000_111_111_111_111_000_000;
        static final int B_CORNER_MASK = 0b111_111_000_000_000_000_111_111;

        static final int R_CORNER_O_MASK = 0b00_11_11_00_00_11_11_00;
        static final int L_CORNER_O_MASK = 0b11_00_00_11_11_00_00_11;
        static final int F_CORNER_O_MASK = 0b00_00_11_11_11_11_00_00;
        static final int B_CORNER_O_MASK = 0b11_11_00_00_00_00_11_11;

        static final long CLEAR_PERM = 0b000_000_000_000_000_000_000_000_11_11_11_11_11_11_11_11l;

        static final long CLEAR_U_CORNERS = 0b000_000_000_000_111_111_111_111_11_11_11_11_11_11_11_11l;
        static final long CLEAR_D_CORNERS = 0b111_111_111_111_000_000_000_000_11_11_11_11_11_11_11_11l;
        static final long CLEAR_R_CORNERS = 0b111_000_000_111_111_000_000_111_11_11_11_11_11_11_11_11l;
        static final long CLEAR_L_CORNERS = 0b000_111_111_000_000_111_111_000_11_11_11_11_11_11_11_11l;
        static final long CLEAR_F_CORNERS = 0b111_111_000_000_000_000_111_111_11_11_11_11_11_11_11_11l;
        static final long CLEAR_B_CORNERS = 0b000_000_111_111_111_111_000_000_11_11_11_11_11_11_11_11l;

        static final long CLEAR_ORIENT = 0b111_111_111_111_111_111_111_111_00_00_00_00_00_00_00_00l;

        static final long CLEAR_U_CORNERS_O = 0b111_111_111_111_111_111_111_111_00_00_00_00_11_11_11_11l;
        static final long CLEAR_D_CORNERS_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_00_00_00_00l;
        static final long CLEAR_R_CORNERS_O = 0b111_111_111_111_111_111_111_111_11_00_00_11_11_00_00_11l;
        static final long CLEAR_L_CORNERS_O = 0b111_111_111_111_111_111_111_111_00_11_11_00_00_11_11_00l;
        static final long CLEAR_F_CORNERS_O = 0b111_111_111_111_111_111_111_111_11_11_00_00_00_00_11_11l;
        static final long CLEAR_B_CORNERS_O = 0b111_111_111_111_111_111_111_111_00_00_11_11_11_11_00_00l;

        static final long CLEAR_ULB_O = 0b111_111_111_111_111_111_111_111_00_11_11_11_11_11_11_11l;
        static final long CLEAR_UBR_O = 0b111_111_111_111_111_111_111_111_11_00_11_11_11_11_11_11l;
        static final long CLEAR_URF_O = 0b111_111_111_111_111_111_111_111_11_11_00_11_11_11_11_11l;
        static final long CLEAR_UFL_O = 0b111_111_111_111_111_111_111_111_11_11_11_00_11_11_11_11l;
        static final long CLEAR_DLF_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_00_11_11_11l;
        static final long CLEAR_DFR_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_11_00_11_11l;
        static final long CLEAR_DRB_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_11_11_00_11l;
        static final long CLEAR_DBL_O = 0b111_111_111_111_111_111_111_111_11_11_11_11_11_11_11_00l;

        static final byte[][] CORNER_COLORS = {
                        { YELLOW, BLUE, ORANGE },
                        { YELLOW, ORANGE, GREEN },
                        { YELLOW, GREEN, RED },
                        { YELLOW, RED, BLUE },
                        { WHITE, BLUE, RED },
                        { WHITE, RED, GREEN },
                        { WHITE, GREEN, ORANGE },
                        { WHITE, ORANGE, BLUE },
        };

        static HashMap<Byte, String> bitsToColor = new HashMap<>();

        // Permutation ULB UBR URF UFL DLF DFR DRB DBL
        // Orientation changes too
        // DBL orientation calculated
        static long state = 0b000_001_010_011_100_101_110_111_00_00_00_00_00_00_00_00l;

        public static void main(String[] args) {
                initColorMap();
                displayCube();
                for (int i = 0; i < 1_000_000; i++) {
                        U();
                        Ui();
                        U2();
                        D();
                        Di();
                        D2();
                        R();
                        Ri();
                        R2();
                        L();
                        Li();
                        L2();
                        F();
                        Fi();
                        F2();
                        B();
                        Bi();
                        B2();
                }
                long startT = System.nanoTime();
                for (int i = 0; i < 33_000_000; i++) {
                        U();
                        Ui();
                        U2();
                        D();
                        Di();
                        D2();
                        R();
                        Ri();
                        R2();
                        L();
                        Li();
                        L2();
                        F();
                        Fi();
                        F2();
                        B();
                        Bi();
                        B2();
                }
                long endT = System.nanoTime();
                System.out.println("MilliSeconds " + ((endT - startT) / 1_000_000));
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
                byte corner = (byte) ((perms >> (cornerOffset)) & 0b111);
                byte[] colors = CORNER_COLORS[corner];
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

        static void U() {
                state = (state & CLEAR_U_CORNERS & CLEAR_U_CORNERS_O)
                                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 3) & 0b000_111_111_111)
                                                | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 9)
                                                                & 0b111_000_000_000)) << (PERM_OFFSET + 12))
                                | ((((((state >> 8) & 0b11_11_11_11) >> 2) & 0b00_11_11_11)
                                                | ((((state >> 8) & 0b11_11_11_11) << 6) & 0b11_00_00_00)) << 8);
        }

        static void Ui() {
                state = (state & CLEAR_U_CORNERS & CLEAR_U_CORNERS_O)
                                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 3) & 0b111_111_111_000)
                                                | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 9)
                                                                & 0b000_000_000_111)) << (PERM_OFFSET + 12))
                                | ((((((state >> 8) & 0b11_11_11_11) << 2) & 0b11_11_11_00)
                                                | ((((state >> 8) & 0b11_11_11_11) >> 6) & 0b00_00_00_11)) << 8);
        }

        static void U2() {
                state = (state & CLEAR_U_CORNERS & CLEAR_U_CORNERS_O)
                                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 6) & 0b111_111_000_000)
                                                | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 6)
                                                                & 0b000_000_111_111)) << (PERM_OFFSET + 12))
                                | ((((((state >> 8) & 0b11_11_11_11) << 4) & 0b11_11_00_00)
                                                | ((((state >> 8) & 0b11_11_11_11) >> 4) & 0b00_00_11_11)) << 8);
        }

        static void D() {
                state = (state & CLEAR_D_CORNERS & CLEAR_D_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 3) & 0b000_111_111_111)
                                                | ((((state >> PERM_OFFSET) & 0b111_111_111_111) << 9)
                                                                & 0b111_000_000_000)) << PERM_OFFSET)
                                | ((((state & 0b11_11_11_11) >> 2) & 0b00_11_11_11)
                                                | (((state & 0b11_11_11_11) << 6) & 0b11_00_00_00));
        }

        static void Di() {
                state = (state & CLEAR_D_CORNERS & CLEAR_D_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) << 3) & 0b111_111_111_000)
                                                | ((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 9)
                                                                & 0b000_000_000_111)) << PERM_OFFSET)
                                | ((((state & 0b11_11_11_11) << 2) & 0b11_11_11_00)
                                                | (((state & 0b11_11_11_11) >> 6) & 0b00_00_00_11));
        }

        static void D2() {
                state = (state & CLEAR_D_CORNERS & CLEAR_D_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) << 6) & 0b111_111_000_000)
                                                | ((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 6)
                                                                & 0b000_000_111_111)) << PERM_OFFSET)
                                | ((((state & 0b11_11_11_11) << 4) & 0b11_11_00_00)
                                                | (((state & 0b11_11_11_11) >> 4) & 0b00_00_11_11));
        }

        static void R() {
                long faceR = ((state >> PERM_OFFSET) & R_CORNER_MASK);
                long faceRO = (state & R_CORNER_O_MASK);
                state = (state & CLEAR_R_CORNERS & CLEAR_R_CORNERS_O)
                                | ((((faceR >> 15)
                                                & 0b000_000_000_000_000_000_111_000)
                                                | ((faceR << 3)
                                                                & 0b000_111_000_000_000_111_000_000)
                                                | ((faceR << 9)
                                                                & 0b000_000_111_000_000_000_000_000)) << PERM_OFFSET)
                                | (((faceRO << 2) & 0b00_11_00_00_00_11_00_00)
                                                | ((faceRO >> 10) & 0b00_00_00_00_00_00_11_00)
                                                | ((faceRO << 6) & 0b00_00_11_00_00_00_00_00));
                state = (state & CLEAR_URF_O & CLEAR_UBR_O & CLEAR_DRB_O & CLEAR_DFR_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << URF_OFFSET_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UBR_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DRB_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DFR_OFFSET_O);

        }

        static void Ri() {
                long faceR = ((state >> PERM_OFFSET) & R_CORNER_MASK);
                long faceRO = (state & R_CORNER_O_MASK);
                state = (state & CLEAR_R_CORNERS & CLEAR_R_CORNERS_O)
                                | ((((faceR >> 9) & 0b000_000_000_000_000_111_000_000)
                                                | ((faceR >> 3) & 0b000_000_111_000_000_000_111_000)
                                                | ((faceR << 15) & 0b000_111_000_000_000_000_000_000)) << PERM_OFFSET)
                                | (((faceRO >> 2) & 0b00_00_11_00_00_00_11_00)
                                                | ((faceRO << 10) & 0b00_11_00_00_00_00_00_00)
                                                | ((faceRO >> 6) & 0b00_00_00_00_00_11_00_00));

                state = (state & CLEAR_URF_O & CLEAR_UBR_O & CLEAR_DRB_O
                                & CLEAR_DFR_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << URF_OFFSET_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UBR_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DRB_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DFR_OFFSET_O);
        }

        static void R2() {
                long faceR = ((state >> PERM_OFFSET) & R_CORNER_MASK);
                long faceRO = (state & R_CORNER_O_MASK);
                state = (state & CLEAR_R_CORNERS & CLEAR_R_CORNERS_O)
                                | ((((faceR >> 12) & 0b000_000_000_000_000_111_111_000)
                                                | ((faceR << 12) & 0b000_111_111_000_000_000_000_000)) << PERM_OFFSET)
                                | (((faceRO << 8) & 0b00_11_11_00_00_00_00_00)
                                                | ((faceRO >> 8) & 0b00_00_00_00_00_11_11_00));
        }

        static void L() {
                long faceL = ((state >> PERM_OFFSET) & L_CORNER_MASK);
                long faceLO = (state & L_CORNER_O_MASK);
                state = (state & CLEAR_L_CORNERS & CLEAR_L_CORNERS_O)
                                | ((((faceL >> 9) & 0b000_000_000_111_000_000_000_111)
                                                | ((faceL << 21) & 0b111_000_000_000_000_000_000_000)
                                                | ((faceL >> 3) & 0b000_000_000_000_111_000_000_000)) << PERM_OFFSET)
                                | (((faceLO >> 6) & 0b00_00_00_11_00_00_00_11)
                                                | ((faceLO >> 2)
                                                                & 0b00_00_00_00_11_00_00_00)
                                                | ((faceLO << 14)
                                                                & 0b11_00_00_00_00_00_00_00));
                state = (state & CLEAR_ULB_O & CLEAR_UFL_O & CLEAR_DLF_O
                                & CLEAR_DBL_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << ULB_OFFSET_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UFL_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DLF_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DBL_OFFSET_O);

        }

        static void Li() {
                long faceL = ((state >> PERM_OFFSET) & L_CORNER_MASK);
                long faceLO = (state & L_CORNER_O_MASK);
                state = (state & CLEAR_L_CORNERS & CLEAR_L_CORNERS_O)
                                | ((((faceL << 9) & 0b111_000_000_000_111_000_000_000)
                                                | ((faceL >> 21) & 0b000_000_000_000_000_000_000_111)
                                                | ((faceL << 3) & 0b000_000_000_111_000_000_000_000)) << PERM_OFFSET)
                                | (((faceLO << 6) & 0b11_00_00_00_11_00_00_00)
                                                | ((faceLO << 2) & 0b00_00_00_11_00_00_00_00)
                                                | ((faceLO >> 14) & 0b00_00_00_00_00_00_00_11));
                state = (state & CLEAR_ULB_O & CLEAR_UFL_O & CLEAR_DLF_O
                                & CLEAR_DBL_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << ULB_OFFSET_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UFL_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DLF_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DBL_OFFSET_O);

        }

        static void L2() {
                state = (state & CLEAR_L_CORNERS & CLEAR_L_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & L_CORNER_MASK) >> 12)
                                                & 0b000_000_000_000_111_000_000_111)
                                                | ((((state >> PERM_OFFSET) & L_CORNER_MASK) << 12)
                                                                & 0b111_000_000_111_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & L_CORNER_O_MASK) >> 8) & 0b00_00_00_00_11_00_00_11)
                                                | (((state & L_CORNER_O_MASK) << 8)
                                                                & 0b11_00_00_11_00_00_00_00));
        }

        static void F() {
                state = (state & CLEAR_F_CORNERS & CLEAR_F_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & F_CORNER_MASK) << 3)
                                                & 0b000_000_111_111_111_000_000_000)
                                                | ((((state >> PERM_OFFSET) & F_CORNER_MASK) >> 9)
                                                                & 0b000_000_000_000_000_111_000_000)) << PERM_OFFSET)
                                | ((((state & F_CORNER_O_MASK) << 2) & 0b00_00_11_11_11_00_00_00)
                                                | (((state & F_CORNER_O_MASK) >> 6)
                                                                & 0b00_00_00_00_00_11_00_00));
                state = (state & CLEAR_URF_O & CLEAR_UFL_O & CLEAR_DLF_O
                                & CLEAR_DFR_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UFL_OFFSET_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << URF_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DFR_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DLF_OFFSET_O);
        }

        static void Fi() {
                state = (state & CLEAR_F_CORNERS & CLEAR_F_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & F_CORNER_MASK) >> 3)
                                                & 0b000_000_000_111_111_111_000_000)
                                                | ((((state >> PERM_OFFSET) & F_CORNER_MASK) << 9)
                                                                & 0b000_000_111_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & F_CORNER_O_MASK) >> 2) & 0b00_00_00_11_11_11_00_00)
                                                | (((state & F_CORNER_O_MASK) << 6)
                                                                & 0b00_00_11_00_00_00_00_00));
                state = (state & CLEAR_URF_O & CLEAR_UFL_O & CLEAR_DLF_O
                                & CLEAR_DFR_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UFL_OFFSET_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << URF_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DFR_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DLF_OFFSET_O);
        }

        static void F2() {
                state = (state & CLEAR_F_CORNERS & CLEAR_F_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & F_CORNER_MASK) >> 6)
                                                & 0b000_000_000_000_111_111_000_000)
                                                | ((((state >> PERM_OFFSET) & F_CORNER_MASK) << 6)
                                                                & 0b000_000_111_111_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & F_CORNER_O_MASK) >> 4) & 0b00_00_00_00_11_11_00_00)
                                                | (((state & F_CORNER_O_MASK) << 4)
                                                                & 0b00_11_11_00_00_00_00));
        }

        static void B() {
                long faceB = ((state >> PERM_OFFSET) & B_CORNER_MASK);
                long faceBO = (state & B_CORNER_O_MASK);
                state = (state & CLEAR_B_CORNERS & CLEAR_B_CORNERS_O)
                                | ((((faceB << 3)
                                                & 0b111_000_000_000_000_000_111_000)
                                                | ((faceB >> 21)
                                                                & 0b000_000_000_000_000_000_000_111)
                                                | ((faceB << 15)
                                                                & 0b000_111_000_000_000_000_000_000)) << PERM_OFFSET)
                                | (((faceBO << 2) & 0b11_00_00_00_00_00_11_00)
                                                | ((faceBO >> 14)
                                                                & 0b00_00_00_00_00_00_00_11)
                                                | ((faceBO << 10)
                                                                & 0b00_11_00_00_00_00_00_00));
                state = (state & CLEAR_ULB_O & CLEAR_UBR_O & CLEAR_DBL_O
                                & CLEAR_DRB_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UBR_OFFSET_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << ULB_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DBL_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DRB_OFFSET_O);
        }

        static void Bi() {
                long faceB = ((state >> PERM_OFFSET) & B_CORNER_MASK);
                long faceBO = (state & B_CORNER_O_MASK);
                state = (state & CLEAR_B_CORNERS & CLEAR_B_CORNERS_O)
                                | ((((faceB >> 3)
                                                & 0b000_111_000_000_000_000_000_111)
                                                | ((faceB << 21)
                                                                & 0b111_000_000_000_000_000_000_000)
                                                | ((faceB >> 15)
                                                                & 0b000_000_000_000_000_000_111_000)) << PERM_OFFSET)
                                | (((faceBO >> 2) & 0b00_11_00_00_00_00_00_11)
                                                | ((faceBO << 14)
                                                                & 0b11_00_00_00_00_00_00_00)
                                                | ((faceBO >> 10)
                                                                & 0b00_00_00_00_00_00_11_00));
                state = (state & CLEAR_ULB_O & CLEAR_UBR_O & CLEAR_DBL_O
                                & CLEAR_DRB_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UBR_OFFSET_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << ULB_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DBL_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DRB_OFFSET_O);
        }

        static void B2() {
                state = (state & CLEAR_B_CORNERS & CLEAR_B_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & B_CORNER_MASK) >> 18)
                                                & 0b000_000_000_000_000_000_111_111)
                                                | ((((state >> PERM_OFFSET) & B_CORNER_MASK) << 18)
                                                                & 0b111_111_000_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & B_CORNER_O_MASK) >> 12) & 0b00_00_00_00_00_00_11_11)
                                                | (((state & B_CORNER_O_MASK) << 12)
                                                                & 0b11_11_00_00_00_00_00));
        }

        static void y() {
                state = (state & CLEAR_U_CORNERS & CLEAR_U_CORNERS_O & CLEAR_D_CORNERS & CLEAR_D_CORNERS_O)
                                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 3) & 0b000_111_111_111)
                                                | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 9)
                                                                & 0b111_000_000_000)) << (PERM_OFFSET + 12))
                                | ((((((state >> 8) & 0b11_11_11_11) >> 2) & 0b00_11_11_11)
                                                | ((((state >> 8) & 0b11_11_11_11) << 6) & 0b11_00_00_00)) << 8)
                                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) << 3) & 0b111_111_111_000)
                                                | ((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 9)
                                                                & 0b000_000_000_111)) << PERM_OFFSET)
                                | ((((state & 0b11_11_11_11) << 2) & 0b11_11_11_00)
                                                | (((state & 0b11_11_11_11) >> 6) & 0b00_00_00_11));
        }

        static void yi() {
                state = (state & CLEAR_U_CORNERS & CLEAR_U_CORNERS_O & CLEAR_D_CORNERS & CLEAR_D_CORNERS_O)
                                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 3) & 0b111_111_111_000)
                                                | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 9)
                                                                & 0b000_000_000_111)) << (PERM_OFFSET + 12))
                                | ((((((state >> 8) & 0b11_11_11_11) << 2) & 0b11_11_11_00)
                                                | ((((state >> 8) & 0b11_11_11_11) >> 6) & 0b00_00_00_11)) << 8)
                                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 3) & 0b000_111_111_111)
                                                | ((((state >> PERM_OFFSET) & 0b111_111_111_111) << 9)
                                                                & 0b111_000_000_000)) << PERM_OFFSET)
                                | ((((state & 0b11_11_11_11) >> 2) & 0b00_11_11_11)
                                                | (((state & 0b11_11_11_11) << 6) & 0b11_00_00_00));
        }

        static void y2() {
                state = (state & CLEAR_U_CORNERS & CLEAR_U_CORNERS_O & CLEAR_D_CORNERS & CLEAR_D_CORNERS_O)
                                | ((((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) << 6) & 0b111_111_000_000)
                                                | ((((state >> (PERM_OFFSET + 12)) & 0b111_111_111_111) >> 6)
                                                                & 0b000_000_111_111)) << (PERM_OFFSET + 12))
                                | ((((((state >> 8) & 0b11_11_11_11) << 4) & 0b11_11_00_00)
                                                | ((((state >> 8) & 0b11_11_11_11) >> 4) & 0b00_00_11_11)) << 8)
                                | ((((((state >> PERM_OFFSET) & 0b111_111_111_111) << 6) & 0b111_111_000_000)
                                                | ((((state >> PERM_OFFSET) & 0b111_111_111_111) >> 6)
                                                                & 0b000_000_111_111)) << PERM_OFFSET)
                                | ((((state & 0b11_11_11_11) << 4) & 0b11_11_00_00)
                                                | (((state & 0b11_11_11_11) >> 4) & 0b00_00_11_11));
        }

        static void z() {
                state = (state & CLEAR_F_CORNERS & CLEAR_F_CORNERS_O & CLEAR_B_CORNERS & CLEAR_B_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & F_CORNER_MASK) << 3)
                                                & 0b000_000_111_111_111_000_000_000)
                                                | ((((state >> PERM_OFFSET) & F_CORNER_MASK) >> 9)
                                                                & 0b000_000_000_000_000_111_000_000)) << PERM_OFFSET)
                                | ((((state & F_CORNER_O_MASK) << 2) & 0b00_00_11_11_11_00_00_00)
                                                | (((state & F_CORNER_O_MASK) >> 6)
                                                                & 0b00_00_00_00_00_11_00_00))
                                | ((((((state >> PERM_OFFSET) & B_CORNER_MASK) >> 3)
                                                & 0b000_111_000_000_000_000_000_111)
                                                | ((((state >> PERM_OFFSET) & B_CORNER_MASK) << 21)
                                                                & 0b111_000_000_000_000_000_000_000)
                                                | ((((state >> PERM_OFFSET) & B_CORNER_MASK) >> 15)
                                                                & 0b000_000_000_000_000_000_111_000)) << PERM_OFFSET)
                                | ((((state & B_CORNER_O_MASK) >> 2) & 0b00_11_00_00_00_00_00_11)
                                                | (((state & B_CORNER_O_MASK) << 14)
                                                                & 0b11_00_00_00_00_00_00_00)
                                                | (((state & B_CORNER_O_MASK) >> 10)
                                                                & 0b00_00_00_00_00_00_11_00));
                state = (state & CLEAR_URF_O & CLEAR_UFL_O & CLEAR_DLF_O & CLEAR_DFR_O & CLEAR_ULB_O & CLEAR_UBR_O
                                & CLEAR_DBL_O & CLEAR_DRB_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UFL_OFFSET_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << URF_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DFR_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DLF_OFFSET_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UBR_OFFSET_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << ULB_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DBL_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DRB_OFFSET_O);
        }

        static void zi() {
                state = (state & CLEAR_F_CORNERS & CLEAR_F_CORNERS_O & CLEAR_B_CORNERS & CLEAR_B_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & F_CORNER_MASK) >> 3)
                                                & 0b000_000_000_111_111_111_000_000)
                                                | ((((state >> PERM_OFFSET) & F_CORNER_MASK) << 9)
                                                                & 0b000_000_111_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & F_CORNER_O_MASK) >> 2) & 0b00_00_00_11_11_11_00_00)
                                                | (((state & F_CORNER_O_MASK) << 6)
                                                                & 0b00_00_11_00_00_00_00_00))
                                | ((((((state >> PERM_OFFSET) & B_CORNER_MASK) << 3)
                                                & 0b111_000_000_000_000_000_111_000)
                                                | ((((state >> PERM_OFFSET) & B_CORNER_MASK) >> 21)
                                                                & 0b000_000_000_000_000_000_000_111)
                                                | ((((state >> PERM_OFFSET) & B_CORNER_MASK) << 15)
                                                                & 0b000_111_000_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & B_CORNER_O_MASK) << 2) & 0b11_00_00_00_00_00_11_00)
                                                | (((state & B_CORNER_O_MASK) >> 14)
                                                                & 0b00_00_00_00_00_00_00_11)
                                                | (((state & B_CORNER_O_MASK) << 10)
                                                                & 0b00_11_00_00_00_00_00_00));
                state = (state & CLEAR_URF_O & CLEAR_UFL_O & CLEAR_DLF_O & CLEAR_DFR_O & CLEAR_ULB_O & CLEAR_UBR_O
                                & CLEAR_DBL_O & CLEAR_DRB_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UFL_OFFSET_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << URF_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DFR_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DLF_OFFSET_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << UBR_OFFSET_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << ULB_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DBL_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DRB_OFFSET_O);
        }

        static void z2() {
                state = (state & CLEAR_F_CORNERS & CLEAR_F_CORNERS_O & CLEAR_B_CORNERS & CLEAR_B_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & F_CORNER_MASK) >> 6)
                                                & 0b000_000_000_000_111_111_000_000)
                                                | ((((state >> PERM_OFFSET) & F_CORNER_MASK) << 6)
                                                                & 0b000_000_111_111_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & F_CORNER_O_MASK) >> 4) & 0b00_00_00_00_11_11_00_00)
                                                | (((state & F_CORNER_O_MASK) << 4)
                                                                & 0b00_11_11_00_00_00_00))
                                | ((((((state >> PERM_OFFSET) & B_CORNER_MASK) >> 18)
                                                & 0b000_000_000_000_000_000_111_111)
                                                | ((((state >> PERM_OFFSET) & B_CORNER_MASK) << 18)
                                                                & 0b111_111_000_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & B_CORNER_O_MASK) >> 12) & 0b00_00_00_00_00_00_11_11)
                                                | (((state & B_CORNER_O_MASK) << 12)
                                                                & 0b11_11_00_00_00_00_00));
        }

        static void x() {
                state = (state & CLEAR_R_CORNERS & CLEAR_R_CORNERS_O & CLEAR_L_CORNERS & CLEAR_L_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & R_CORNER_MASK) >> 15)
                                                & 0b000_000_000_000_000_000_111_000)
                                                | ((((state >> PERM_OFFSET) & R_CORNER_MASK) << 3)
                                                                & 0b000_111_000_000_000_111_000_000)
                                                | ((((state >> PERM_OFFSET) & R_CORNER_MASK) << 9)
                                                                & 0b000_000_111_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & R_CORNER_O_MASK) << 2) & 0b00_11_00_00_00_11_00_00)
                                                | (((state & R_CORNER_O_MASK) >> 10) & 0b00_00_00_00_00_00_11_00)
                                                | (((state & R_CORNER_O_MASK) << 6) & 0b00_00_11_00_00_00_00_00))
                                | ((((((state >> PERM_OFFSET) & L_CORNER_MASK) << 9)
                                                & 0b111_000_000_000_111_000_000_000)
                                                | ((((state >> PERM_OFFSET) & L_CORNER_MASK) >> 21)
                                                                & 0b000_000_000_000_000_000_000_111)
                                                | ((((state >> PERM_OFFSET) & L_CORNER_MASK) << 3)
                                                                & 0b000_000_000_111_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & L_CORNER_O_MASK) << 6)
                                                & 0b11_00_00_00_11_00_00_00)
                                                | (((state & L_CORNER_O_MASK) << 2)
                                                                & 0b00_00_00_11_00_00_00_00)
                                                | (((state & L_CORNER_O_MASK) >> 14)
                                                                & 0b00_00_00_00_00_00_00_11));
                state = (state & CLEAR_URF_O & CLEAR_UBR_O & CLEAR_DRB_O & CLEAR_DFR_O & CLEAR_ULB_O & CLEAR_UFL_O
                                & CLEAR_DLF_O & CLEAR_DBL_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << URF_OFFSET_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UBR_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DRB_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DFR_OFFSET_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << ULB_OFFSET_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UFL_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DLF_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DBL_OFFSET_O);
        }

        static void xi() {
                state = (state & CLEAR_R_CORNERS & CLEAR_R_CORNERS_O & CLEAR_L_CORNERS & CLEAR_L_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & R_CORNER_MASK) >> 9)
                                                & 0b000_000_000_000_000_111_000_000)
                                                | ((((state >> PERM_OFFSET) & R_CORNER_MASK) >> 3)
                                                                & 0b000_000_111_000_000_000_111_000)
                                                | ((((state >> PERM_OFFSET) & R_CORNER_MASK) << 15)
                                                                & 0b000_111_000_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & R_CORNER_O_MASK) >> 2) & 0b00_00_11_00_00_00_11_00)
                                                | (((state & R_CORNER_O_MASK) << 10) & 0b00_11_00_00_00_00_00_00)
                                                | (((state & R_CORNER_O_MASK) >> 6) & 0b00_00_00_00_00_11_00_00))
                                | ((((((state >> PERM_OFFSET) & L_CORNER_MASK) >> 9)
                                                & 0b000_000_000_111_000_000_000_111)
                                                | ((((state >> PERM_OFFSET) & L_CORNER_MASK) << 21)
                                                                & 0b111_000_000_000_000_000_000_000)
                                                | ((((state >> PERM_OFFSET) & L_CORNER_MASK) >> 3)
                                                                & 0b000_000_000_000_111_000_000_000)) << PERM_OFFSET)
                                | ((((state & L_CORNER_O_MASK) >> 6) & 0b00_00_00_11_00_00_00_11)
                                                | (((state & L_CORNER_O_MASK) >> 2)
                                                                & 0b00_00_00_00_11_00_00_00)
                                                | (((state & L_CORNER_O_MASK) << 14)
                                                                & 0b11_00_00_00_00_00_00_00));

                state = (state & CLEAR_URF_O & CLEAR_UBR_O & CLEAR_DRB_O & CLEAR_DFR_O & CLEAR_ULB_O & CLEAR_UFL_O
                                & CLEAR_DLF_O & CLEAR_DBL_O)
                                | ((((state >> URF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> URF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << URF_OFFSET_O)
                                | ((((state >> UBR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UBR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UBR_OFFSET_O)
                                | ((((state >> DRB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DRB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DRB_OFFSET_O)
                                | ((((state >> DFR_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DFR_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DFR_OFFSET_O)
                                | ((((state >> ULB_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> ULB_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << ULB_OFFSET_O)
                                | ((((state >> UFL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> UFL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << UFL_OFFSET_O)
                                | ((((state >> DLF_OFFSET_O) & 0b11) == 0 ? ORIENT2
                                                : ((state >> DLF_OFFSET_O) & 0b11) == 1 ? ORIENT0
                                                                : ORIENT1) << DLF_OFFSET_O)
                                | ((((state >> DBL_OFFSET_O) & 0b11) == 0 ? ORIENT1
                                                : ((state >> DBL_OFFSET_O) & 0b11) == 1 ? ORIENT2
                                                                : ORIENT0) << DBL_OFFSET_O);
        }

        static void x2() {
                state = (state & CLEAR_R_CORNERS & CLEAR_R_CORNERS_O & CLEAR_L_CORNERS & CLEAR_L_CORNERS_O)
                                | ((((((state >> PERM_OFFSET) & R_CORNER_MASK) >> 12)
                                                & 0b000_000_000_000_000_111_111_000)
                                                | ((((state >> PERM_OFFSET) & R_CORNER_MASK) << 12)
                                                                & 0b000_111_111_000_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & R_CORNER_O_MASK) << 8) & 0b00_11_11_00_00_00_00_00)
                                                | (((state & R_CORNER_O_MASK) >> 8) & 0b00_00_00_00_00_11_11_00))
                                | ((((((state >> PERM_OFFSET) & L_CORNER_MASK) >> 12)
                                                & 0b000_000_000_000_111_000_000_111)
                                                | ((((state >> PERM_OFFSET) & L_CORNER_MASK) << 12)
                                                                & 0b111_000_000_111_000_000_000_000)) << PERM_OFFSET)
                                | ((((state & L_CORNER_O_MASK) >> 8) & 0b00_00_00_00_11_00_00_11)
                                                | (((state & L_CORNER_O_MASK) << 8)
                                                                & 0b11_00_00_11_00_00_00_00));
        }
}
