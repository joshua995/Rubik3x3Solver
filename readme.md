**--RubikBitmap.java--**

**Colour Map**<br />
0b000 = White<br />
0b001 = Yellow<br />
0b010 = Blue<br />
0b011 = Green<br />
0b100 = Red<br />
0b101 = Orange<br />

**Cube map**<br />
0b001_001_001_001_001_001_001_001_001 faceU<br />
0b000_000_000_000_000_000_000_000_000 faceD<br />
0b011_011_011_011_011_011_011_011_011 faceR<br />
0b010_010_010_010_010_010_010_010_010 faceL<br />
0b100_100_100_100_100_100_100_100_100 faceF<br />
0b101_101_101_101_101_101_101_101_101 faceB<br />

**--Rubik2x2.java--**<br/>
Generates a 2x2 Rubik's cube's lookup file used in **Rubik2x2Solver.java**<br/>
**Cube map**<br />
faceU = 0b001_001_001_001;
faceD = 0b000_000_000_000;
faceR = 0b011_011_011_011;
faceL = 0b010_010_010_010;
faceF = 0b100_100_100_100;
faceB = 0b101_101_101_101;

