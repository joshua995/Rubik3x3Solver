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
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R");
        boolean isMatching = true;
        int[] sameVal = { 0, 1, 3, 4, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 22, 24, 25, 31, 37, 38, 40,
                41, 43, 44, 45, 46, 48, 49, 51, 52 };
        for (int same : sameVal) {
            if (cube[same] != same)
                isMatching = false;
        }
        int[][] changeVal = { { 2, 20 }, { 5, 23 }, { 8, 26 },
                { 20, 47 }, { 23, 50 }, { 26, 53 },
                { 47, 42 }, { 50, 39 }, { 53, 36 },
                { 42, 2 }, { 39, 5 }, { 36, 8 },
                { 27, 33 }, { 33, 35 }, { 35, 29 }, { 29, 27 },
                { 28, 30 }, { 30, 34 }, { 34, 32 }, { 32, 28 } };
        for (int[] change : changeVal) {
            if (cube[change[0]] != change[1])
                isMatching = false;
        }
        assertTrue(isMatching);
    }

    @Test
    public void testR2Move() {
        cube = RubikSolver.makeMoves(RubikSolver.deepCopy(cube), "R2");
        boolean isMatching = true;
        int[] sameVal = { 0, 1, 3, 4, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 22, 24, 25, 31, 37, 38, 40,
                41, 43, 44, 45, 46, 48, 49, 51, 52 };
        for (int same : sameVal) {
            if (cube[same] != same)
                isMatching = false;
        }
        int[][] changeVal = { { 2, 47 }, { 5, 50 }, { 8, 53 },
                { 20, 42 }, { 23, 39 }, { 26, 36 },
                { 47, 2 }, { 50, 5 }, { 53, 8 },
                { 42, 20 }, { 39, 23 }, { 36, 26 },
                { 27, 35 }, { 33, 29 }, { 35, 27 }, { 29, 33 },
                { 28, 34 }, { 30, 32 }, { 34, 28 }, { 32, 30 } };
        for (int[] change : changeVal) {
            if (cube[change[0]] != change[1])
                isMatching = false;
        }
        assertTrue(isMatching);
    }

    // @After
    // public void teardown() {
    //     //cube = RubikSolver.initCube(cube);
    //     //RubikSolver.initMoveMap();
    // }
}
