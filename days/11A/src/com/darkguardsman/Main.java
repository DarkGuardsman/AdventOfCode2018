package com.darkguardsman;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/18.
 */
public class Main {
    public static void main(String... args) {
        final int serialNumber = Integer.parseInt(args[0]); // 6042
        final int gridSize = Integer.parseInt(args[1]); // 300
        final int gridSquare = Integer.parseInt(args[2]); // 3

        System.out.print("Grid Serial Number: " + serialNumber);
        System.out.print("Grid Size: " + gridSize + "x" + gridSize);
        System.out.print("Grid Square: " + gridSquare + "x" + gridSquare);


        //Generate data based on puzzle's rules
        /*The power level in a given fuel cell can be found through the following process:
            -Find the fuel cell's rack ID, which is its X coordinate plus 10.
            -Begin with a power level of the rack ID times the Y coordinate.
            -Increase the power level by the value of the grid serial number (your puzzle input).
            -Set the power level to itself multiplied by the rack ID.
            -Keep only the hundreds digit of the power level (so 12345 becomes 3; numbers with no hundreds digit become 0).
            -Subtract 5 from the power level.
        */
        System.out.println("\nGenerating data: ");
        final Grid grid = new Grid(gridSize, gridSize);
        grid.forEachCell((g, x, y, data) -> {

            //Do a ton of random math, because the puzzle gods demand it
            int rackID = x + 10;
            int powerLevel = rackID * y;
            powerLevel += serialNumber;
            powerLevel *= rackID;

            //Anything under 100 turns to zero
            if (powerLevel <= 99) {
                powerLevel = 0;
            } else {
                //Get 100th int value
                String s = "" + powerLevel;
                s = "" + s.charAt(s.length() - 3);
                powerLevel = Integer.parseInt(s);
            }

            //Minus by 5... because puzzle
            powerLevel -= 5;

            //Set data
            g.setData(x, y, powerLevel);

            System.out.println("\t" + x + "," + y + "=" + powerLevel);
        });

        //Loop all possible square locations
        System.out.println("\nGenerating Squares: ");
        final List<GridSquare> squareList = new ArrayList((gridSize - 2) * (gridSize - 2));
        for (int x = 0; x < (gridSize - 2); x++) {
            for (int y = 0; y < (gridSize - 2); y++) {

                //Build squares
                GridSquare square = new GridSquare(x, y);
                for (int i = x; i < x + 3; i++) {
                    for (int j = y; j < y + 3; j++) {
                        square.power += grid.getData(i, j);
                    }
                }

                squareList.add(square);
                System.out.println("\t" + square);
            }
        }


        System.out.println("\nFinding Best: ");
        GridSquare best = squareList.stream().max(Comparator.comparingInt(s -> s.power)).get();
        System.out.println("\tBest: " + best);

    }
}
