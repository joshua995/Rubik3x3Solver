package rubik;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Tester {
    int[] cube = new int[54];

    @Before
    public void setup() {
        RubikSolver.initCube(cube);
    }

    @Test
    public void testRMove() {
        cube = RubikSolver.makeMoves(cube, "R");
        boolean isMatching = true;
        if (cube[2] != 20)
            isMatching = false;
        if (cube[5] != 23)
            isMatching = false;
        if (cube[8] != 26)
            isMatching = false;
        assertTrue("match", isMatching);
    }

    @After
    public void teardown() {

    }
}
