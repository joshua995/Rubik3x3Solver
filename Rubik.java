/*
 * Joshua Liu
 * Rubik's cube 
 * TODO make cube representation one full bitstring and a,b,c,... as int[] of substring start and end indices
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Rubik {
    static final int FACE_BITS = 3 * 4;
    static final int FACE_EDGE_BITS = 3 * 8;
    static final String YELLOW = "000";
    static final String BLUE = "001";
    static final String GREEN = "010";
    static final String ORANGE = "011";
    static final String RED = "100";
    static final String WHITE = "101";
    static final String[] MOVES = { "R", "R2", "R'", "L", "L2", "L'", "U", "U2", "U'",
            "D", "D2", "D'", "F", "F2", "F'", "B", "B2", "B'" };

    static final Map<String, String> colourMap = Map.of(
            YELLOW, "Y", BLUE, "B", RED, "R", GREEN, "G", ORANGE, "O", WHITE, "W");

    static String cube = YELLOW + YELLOW + YELLOW + YELLOW + BLUE + BLUE + BLUE + BLUE + RED + RED + RED + RED
            + GREEN + GREEN + GREEN + GREEN + ORANGE + ORANGE + ORANGE + ORANGE + WHITE + WHITE + WHITE + WHITE;

    static final Map<String, int[]> cubeMap = new HashMap<>();
    // // Representation of the cube a-x per sticker
    // static String a = YELLOW, b = YELLOW, c = YELLOW, d = YELLOW;
    // static String e = BLUE, f = BLUE, g = BLUE, h = BLUE;
    // static String i = RED, j = RED, k = RED, l = RED;
    // static String m = GREEN, n = GREEN, o = GREEN, p = GREEN;
    // static String q = ORANGE, r = ORANGE, s = ORANGE, t = ORANGE;
    // static String u = WHITE, v = WHITE, w = WHITE, x = WHITE;

    static String movesUsed = "";

    public static void main(String[] args) {
        initCubeMap();
        displayCube(cube);
        R(1);
        // scrambleCube(5);
        displayCube(cube);
        System.out.println(movesUsed);
    }

    static void initCubeMap() {
        int start = 0, end = start + 3;
        for (int ch = 65; ch <= 88; ch++) { // A-X
            cubeMap.put(Character.toString(ch), new int[] { start, end });
            start = end;
            end = start + 3;
        }
    }

    static int leftCircularShift(int number, int shift, int length) {
        shift = shift % length; // where k is the amount of bits in the number to shift
        int andVar = length == FACE_BITS ? 0b111111111111 : 0b111111111111111111111111;
        return ((number << shift) | (number >> (length - shift))) & andVar;
    }

    static int rightCircularShift(int number, int shift, int length) {
        shift = shift % length; // where k is the amount of bits in the number to shift
        int andVar = length == FACE_BITS ? 0b111111111111 : 0b111111111111111111111111;
        return (number >> shift) | (number << (length - shift)) & andVar;
    }

    static void displayCube(String cube) {
        System.out.printf("%4s|", " ");
        System.out.println(
                colourMap.get(cube.substring(cubeMap.get("A")[0], cubeMap.get("A")[1])) + "|"
                        + colourMap.get(cube.substring(cubeMap.get("B")[0], cubeMap.get("B")[1])) + "|");
        System.out.printf("%4s|", " ");
        System.out.println(colourMap.get(cube.substring(cubeMap.get("D")[0], cubeMap.get("D")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("C")[0], cubeMap.get("C")[1])) + "|");
        System.out.print("|" + colourMap.get(cube.substring(cubeMap.get("E")[0], cubeMap.get("E")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("F")[0], cubeMap.get("F")[1])) + "|");
        System.out.print(colourMap.get(cube.substring(cubeMap.get("I")[0], cubeMap.get("I")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("J")[0], cubeMap.get("J")[1])) + "|");
        System.out.print(colourMap.get(cube.substring(cubeMap.get("M")[0], cubeMap.get("M")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("N")[0], cubeMap.get("N")[1])) + "|");
        System.out.println(colourMap.get(cube.substring(cubeMap.get("Q")[0], cubeMap.get("Q")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("R")[0], cubeMap.get("R")[1])) + "|");
        System.out.print("|" + colourMap.get(cube.substring(cubeMap.get("H")[0], cubeMap.get("H")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("G")[0], cubeMap.get("G")[1])) + "|");
        System.out.print(colourMap.get(cube.substring(cubeMap.get("L")[0], cubeMap.get("L")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("K")[0], cubeMap.get("K")[1])) + "|");
        System.out.print(colourMap.get(cube.substring(cubeMap.get("P")[0], cubeMap.get("P")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("O")[0], cubeMap.get("O")[1])) + "|");
        System.out.println(colourMap.get(cube.substring(cubeMap.get("T")[0], cubeMap.get("T")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("S")[0], cubeMap.get("S")[1])) + "|");
        System.out.printf("%4s|", " ");
        System.out.println(colourMap.get(cube.substring(cubeMap.get("U")[0], cubeMap.get("U")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("V")[0], cubeMap.get("V")[1])) + "|");
        System.out.printf("%4s|", " ");
        System.out.println(colourMap.get(cube.substring(cubeMap.get("X")[0], cubeMap.get("X")[1])) + "|"
                + colourMap.get(cube.substring(cubeMap.get("W")[0], cubeMap.get("W")[1])) + "|");
    }

    static void updateCubeHelper(String sticker, String newValues, int start, int end) {
        cube = cube.substring(0, cubeMap.get(sticker)[0]) + newValues.substring(start, end)
                + cube.substring(cubeMap.get(sticker)[0] + 3);
    }

    static void moveHelper(String m1st, String m2nd, String m3rd, String m4th,
            String e1st, String e2nd, String e3rd, String e4th, String e5th, String e6th, String e7th, String e8th,
            int shiftFactor) {
        String format = "%" + FACE_BITS / 4 + "s";
        String mainFace = String.format(format,
                cube.substring(cubeMap.get(m1st)[0], cubeMap.get(m1st)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(m2nd)[0], cubeMap.get(m2nd)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(m3rd)[0], cubeMap.get(m3rd)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(m4th)[0], cubeMap.get(m4th)[1])).replace(" ", "0");
        format = "%" + FACE_BITS + "s";
        String mainFaceAfterMove = String.format(
                format,
                Integer.toBinaryString(rightCircularShift(Integer.parseInt(mainFace, 2), 3 * shiftFactor, FACE_BITS)))
                .replace(" ", "0");
        updateCubeHelper(m1st, mainFaceAfterMove, 0, 3);
        updateCubeHelper(m2nd, mainFaceAfterMove, 3, 6);
        updateCubeHelper(m3rd, mainFaceAfterMove, 6, 9);
        updateCubeHelper(m4th, mainFaceAfterMove, 9, 12);

        format = "%" + FACE_EDGE_BITS / 8 + "s";
        String edgeFace = String.format(format,
                cube.substring(cubeMap.get(e1st)[0], cubeMap.get(e1st)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(e2nd)[0], cubeMap.get(e2nd)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(e3rd)[0], cubeMap.get(e3rd)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(e4th)[0], cubeMap.get(e4th)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(e5th)[0], cubeMap.get(e5th)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(e6th)[0], cubeMap.get(e6th)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(e7th)[0], cubeMap.get(e7th)[1])).replace(" ", "0")
                + String.format(format, cube.substring(cubeMap.get(e8th)[0], cubeMap.get(e8th)[1])).replace(" ", "0");
        format = "%" + FACE_EDGE_BITS + "s";
        String edgeFaceAfterMove = String.format(
                format,
                Integer.toBinaryString(
                        rightCircularShift(Integer.parseInt(edgeFace, 2), 3 * shiftFactor * 2, FACE_EDGE_BITS)))
                .replace(" ", "0");

        updateCubeHelper(e1st, edgeFaceAfterMove, 0, 3);
        updateCubeHelper(e2nd, edgeFaceAfterMove, 3, 6);
        updateCubeHelper(e3rd, edgeFaceAfterMove, 6, 9);
        updateCubeHelper(e4th, edgeFaceAfterMove, 9, 12);
        updateCubeHelper(e5th, edgeFaceAfterMove, 12, 15);
        updateCubeHelper(e6th, edgeFaceAfterMove, 15, 18);
        updateCubeHelper(e7th, edgeFaceAfterMove, 18, 21);
        updateCubeHelper(e8th, edgeFaceAfterMove, 21, 24);
    }

    /*
     * R move
     * param: direction -> 1 = R, 2 = R2, 3 = R'
     */
    static void R(int direction) {
        moveHelper("M", "N", "O", "P", "Q", "T", "W", "V", "K", "J", "C", "B", direction);
    }

    /*
     * L move
     * param: direction -> 1 = L, 2 = L2, 3 = L'
     */
    static void L(int direction) {
        moveHelper("e", "f", "g", h, a, d, i, l, u, x, s, r, direction);
    }

    /*
     * U move
     * param: direction -> 1 = U, 2 = U2, 3 = U'
     */
    static void U(int direction) {
        String[] move = moveHelper(a, b, c, d, r, q, n, m, j, i, f, e, direction);
        // Set Main face a, b, c, d
        a = move[0].substring(0, 3);
        b = move[0].substring(3, 6);
        c = move[0].substring(6, 9);
        d = move[0].substring(9);

        // Set Edge r, q, n, m, j, i, f, e
        r = move[1].substring(0, 3);
        q = move[1].substring(3, 6);
        n = move[1].substring(6, 9);
        m = move[1].substring(9, 12);
        j = move[1].substring(12, 15);
        i = move[1].substring(15, 18);
        f = move[1].substring(18, 21);
        e = move[1].substring(21);
    }

    /*
     * D move
     * param: direction -> 1 = D, 2 = D2, 3 = D'
     */
    static void D(int direction) {
        String[] move = moveHelper(u, v, w, x, h, g, l, k, p, o, t, s, direction);
        // Set Main face u, v, w, x
        u = move[0].substring(0, 3);
        v = move[0].substring(3, 6);
        w = move[0].substring(6, 9);
        x = move[0].substring(9);

        // Set Edge h, g, l, k, p, o, t, s
        h = move[1].substring(0, 3);
        g = move[1].substring(3, 6);
        l = move[1].substring(6, 9);
        k = move[1].substring(9, 12);
        p = move[1].substring(12, 15);
        o = move[1].substring(15, 18);
        t = move[1].substring(18, 21);
        s = move[1].substring(21);
    }

    /*
     * F move
     * param: direction -> 1 = F, 2 = F2, 3 = F'
     */
    static void F(int direction) {
        String[] move = moveHelper(i, j, k, l, d, c, m, p, v, u, g, f, direction);
        // Set Main face i, j, k, l
        i = move[0].substring(0, 3);
        j = move[0].substring(3, 6);
        k = move[0].substring(6, 9);
        l = move[0].substring(9);

        // Set Edge d, c, m, p, v, u, g, f
        d = move[1].substring(0, 3);
        c = move[1].substring(3, 6);
        m = move[1].substring(6, 9);
        p = move[1].substring(9, 12);
        v = move[1].substring(12, 15);
        u = move[1].substring(15, 18);
        g = move[1].substring(18, 21);
        f = move[1].substring(21);
    }

    /*
     * B move
     * param: direction -> 1 = B, 2 = B2, 3 = B'
     */
    static void B(int direction) {
        String[] move = moveHelper(q, r, s, t, b, a, e, h, x, w, o, n, direction);
        // Set Main face q, r, s, t
        q = move[0].substring(0, 3);
        r = move[0].substring(3, 6);
        s = move[0].substring(6, 9);
        t = move[0].substring(9);

        // Set Edge b, a, e, h, x, w, o, n,
        b = move[1].substring(0, 3);
        a = move[1].substring(3, 6);
        e = move[1].substring(6, 9);
        h = move[1].substring(9, 12);
        x = move[1].substring(12, 15);
        w = move[1].substring(15, 18);
        o = move[1].substring(18, 21);
        n = move[1].substring(21);
    }

    static void makeMoves(String moves) {
        String[] splitMoves = moves.split(" ");
        movesUsed += moves + " ";
        for (String move : splitMoves) {
            switch (move) {
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
                default:
                    break;
            }
        }
    }

    static void scrambleCube(int amount) {
        for (int i = 0; i < amount; i++) {
            makeMoves(MOVES[new Random().nextInt(MOVES.length)]);
        }
    }

    static void generateStates() {

    }
}
