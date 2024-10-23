package rubik;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Tester {
    int[] cube = new int[54];

    @Before
    public void setup() {
        cube = RubikSolver.initCube(RubikSolver.deepCopy(cube));
        RubikSolver.initMoveMap();
    }

    @Test
    public void testRMove() {
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R"); // Make the move on the cube
        int[] cubeAfterMove = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53 };
        cubeAfterMove[2] = 20;
        cubeAfterMove[5] = 23;
        cubeAfterMove[8] = 26;
        cubeAfterMove[20] = 47;
        cubeAfterMove[23] = 50;
        cubeAfterMove[26] = 53;
        cubeAfterMove[47] = 42;
        cubeAfterMove[50] = 39;
        cubeAfterMove[53] = 36;
        cubeAfterMove[42] = 2;
        cubeAfterMove[39] = 5;
        cubeAfterMove[36] = 8;//
        cubeAfterMove[27] = 33;
        cubeAfterMove[33] = 35;
        cubeAfterMove[35] = 29;
        cubeAfterMove[29] = 27;//
        cubeAfterMove[28] = 30;
        cubeAfterMove[30] = 34;
        cubeAfterMove[34] = 32;
        cubeAfterMove[32] = 28;
        assertArrayEquals(cubeAfterMove, cube);
    }

    @Test
    public void testR2Move() {
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R2"); // Make the move on the cube
        int[] cubeAfterMove = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53 };
        cubeAfterMove[42] = 20;
        cubeAfterMove[39] = 23;
        cubeAfterMove[36] = 26;
        cubeAfterMove[2] = 47;
        cubeAfterMove[5] = 50;
        cubeAfterMove[8] = 53;
        cubeAfterMove[20] = 42;
        cubeAfterMove[23] = 39;
        cubeAfterMove[26] = 36;
        cubeAfterMove[47] = 2;
        cubeAfterMove[50] = 5;
        cubeAfterMove[53] = 8;//
        cubeAfterMove[29] = 33;
        cubeAfterMove[27] = 35;
        cubeAfterMove[33] = 29;
        cubeAfterMove[35] = 27;//
        cubeAfterMove[32] = 30;
        cubeAfterMove[28] = 34;
        cubeAfterMove[30] = 32;
        cubeAfterMove[34] = 28;
        assertArrayEquals(cubeAfterMove, cube);
    }

    @Test
    public void testRIMove() {
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R'"); // Make the move on the cube
        int[] cubeAfterMove = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53 };
        cubeAfterMove[47] = 20;
        cubeAfterMove[50] = 23;
        cubeAfterMove[53] = 26;
        cubeAfterMove[42] = 47;
        cubeAfterMove[39] = 50;
        cubeAfterMove[36] = 53;
        cubeAfterMove[2] = 42;
        cubeAfterMove[5] = 39;
        cubeAfterMove[8] = 36;
        cubeAfterMove[20] = 2;
        cubeAfterMove[23] = 5;
        cubeAfterMove[26] = 8;//
        cubeAfterMove[35] = 33;
        cubeAfterMove[29] = 35;
        cubeAfterMove[27] = 29;
        cubeAfterMove[33] = 27;//
        cubeAfterMove[34] = 30;
        cubeAfterMove[32] = 34;
        cubeAfterMove[28] = 32;
        cubeAfterMove[30] = 28;
        assertArrayEquals(cubeAfterMove, cube);
    }

    @Test
    public void testLMove() {
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R"); // Make the move on the cube
        int[] cubeAfterMove = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53 };
        cubeAfterMove[2] = 20;
        cubeAfterMove[5] = 23;
        cubeAfterMove[8] = 26;
        cubeAfterMove[20] = 47;
        cubeAfterMove[23] = 50;
        cubeAfterMove[26] = 53;
        cubeAfterMove[47] = 42;
        cubeAfterMove[50] = 39;
        cubeAfterMove[53] = 36;
        cubeAfterMove[42] = 2;
        cubeAfterMove[39] = 5;
        cubeAfterMove[36] = 8;//
        cubeAfterMove[27] = 33;
        cubeAfterMove[33] = 35;
        cubeAfterMove[35] = 29;
        cubeAfterMove[29] = 27;//
        cubeAfterMove[28] = 30;
        cubeAfterMove[30] = 34;
        cubeAfterMove[34] = 32;
        cubeAfterMove[32] = 28;
        assertArrayEquals(cubeAfterMove, cube);
    }

    // @After
    // public void teardown() {
    // //cube = RubikSolver.initCube(cube);
    // //RubikSolver.initMoveMap();
    // }
}
