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
        int[] cubeAfterR = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53 }; // Cube array after R
        cubeAfterR[2] = 20;
        cubeAfterR[5] = 23;
        cubeAfterR[8] = 26;
        cubeAfterR[20] = 47;
        cubeAfterR[23] = 50;
        cubeAfterR[26] = 53;
        cubeAfterR[47] = 42;
        cubeAfterR[50] = 39;
        cubeAfterR[53] = 36;
        cubeAfterR[42] = 2;
        cubeAfterR[39] = 5;
        cubeAfterR[36] = 8;//
        cubeAfterR[27] = 33;
        cubeAfterR[33] = 35;
        cubeAfterR[35] = 29;
        cubeAfterR[29] = 27;//
        cubeAfterR[28] = 30;
        cubeAfterR[30] = 34;
        cubeAfterR[34] = 32;
        cubeAfterR[32] = 28;
        assertArrayEquals(cubeAfterR, cube);
    }

    @Test
    public void testR2Move() {
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R2"); // Make the move on the cube
        int[] cubeAfterR = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53 }; // Cube array after R
        cubeAfterR[42] = 20;
        cubeAfterR[39] = 23;
        cubeAfterR[36] = 26;
        cubeAfterR[2] = 47;
        cubeAfterR[5] = 50;
        cubeAfterR[8] = 53;
        cubeAfterR[20] = 42;
        cubeAfterR[23] = 39;
        cubeAfterR[26] = 36;
        cubeAfterR[47] = 2;
        cubeAfterR[50] = 5;
        cubeAfterR[53] = 8;//
        cubeAfterR[29] = 33;
        cubeAfterR[27] = 35;
        cubeAfterR[33] = 29;
        cubeAfterR[35] = 27;//
        cubeAfterR[32] = 30;
        cubeAfterR[28] = 34;
        cubeAfterR[30] = 32;
        cubeAfterR[34] = 28;
        assertArrayEquals(cubeAfterR, cube);
    }

    @Test
    public void testRIMove() {
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R'"); // Make the move on the cube
        int[] cubeAfterR = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53 }; // Cube array after R
        cubeAfterR[47] = 20;
        cubeAfterR[50] = 23;
        cubeAfterR[53] = 26;
        cubeAfterR[42] = 47;
        cubeAfterR[39] = 50;
        cubeAfterR[36] = 53;
        cubeAfterR[2] = 42;
        cubeAfterR[5] = 39;
        cubeAfterR[8] = 36;
        cubeAfterR[20] = 2;
        cubeAfterR[23] = 5;
        cubeAfterR[26] = 8;//
        cubeAfterR[35] = 33;
        cubeAfterR[29] = 35;
        cubeAfterR[27] = 29;
        cubeAfterR[33] = 27;//
        cubeAfterR[34] = 30;
        cubeAfterR[32] = 34;
        cubeAfterR[28] = 32;
        cubeAfterR[30] = 28;
        assertArrayEquals(cubeAfterR, cube);
    }

    // @After
    // public void teardown() {
    // //cube = RubikSolver.initCube(cube);
    // //RubikSolver.initMoveMap();
    // }
}
