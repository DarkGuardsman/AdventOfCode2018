package com.darkguardsman;

import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.StringHelpers;
import com.darkguardsman.helpers.grid.GridInt;
import com.darkguardsman.helpers.path.BreadthFirstPath;

import java.util.Random;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class TestBreadth {

    public static void main(String... args) {
        final int sizeX = Integer.parseInt(args[0]);
        final int sizeY = Integer.parseInt(args[1]);

        final int x = Integer.parseInt(args[2]);
        final int y = Integer.parseInt(args[3]);

        System.out.println("Grid: " + sizeX + "x" + sizeY);
        System.out.println("Start: " + x + ", " + y);

        System.out.println("\nSetting up path and grid: ");
        GridInt grid = new GridInt(sizeX, sizeY);

        //Setup pathfinder
        BreadthFirstPath path = new BreadthFirstPath();
        path.onPathFunction = (current, next) -> {
            final int currentDotValue = grid.getData(current);
            final int nextDotValue = grid.getData(next);
            if (nextDotValue == -1) {
                grid.setData(next.x, next.y, currentDotValue + 1);
            }
        };
        path.shouldPathFunction = (start, current, next, pathed) -> {
            if (!pathed) {
                return grid.isInGrid(next) && grid.getData(next) == -1;
            }
            return false;
        };
        System.out.println("\tDone...");


        System.out.println("\nSimple Test: ");
        //------------------------------------------
        clearGrid(grid, x, y);

        //Trigger pathfinder
        path.startPath(new Dot(x, y));


        //Output results
        grid.print((gx, gy) -> StringHelpers.padLeft("" + grid.getData(gx, gy), 3));

        //------------------------------------------
        System.out.println("\nRandom Walls: ");
        clearGrid(grid, x, y);

        //Fill random blockers
        final Random random = new Random();
        grid.fillGrid((g, gx, gy) -> gx != x && gy != y && random.nextFloat() > 0.67f, () -> -2);

        //Trigger pathfinder
        path.startPath(new Dot(x, y));

        //Output results
        grid.print((gx, gy) -> {
            if (grid.getData(gx, gy) == -2) {
                return StringHelpers.padLeft("#", 3);
            } else if (grid.getData(gx, gy) < 0) {
                return StringHelpers.padLeft(".", 3);
            }
            return StringHelpers.padLeft("" + grid.getData(gx, gy), 3);
        });

    }

    static void clearGrid(GridInt grid, int x, int y) {
        grid.fillGrid((g, gx, gy) -> true, () -> -1);
        grid.setData(x, y, 0);
    }
}
