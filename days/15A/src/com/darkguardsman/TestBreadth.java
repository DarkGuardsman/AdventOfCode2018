package com.darkguardsman;

import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.StringHelpers;
import com.darkguardsman.helpers.grid.GridInt;
import com.darkguardsman.helpers.path.BreadthFirstPath;

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

        System.out.println("\nGenerating: ");
        GridInt grid = new GridInt(sizeX, sizeY);

        //Fill grid with default data
        grid.fillGrid((g, gx, gy) -> true, () -> -1);
        grid.setData(x, y, 0);

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
                return grid.isInGrid(next);
            }
            return false;
        };

        //Trigger pathfinder
        path.startPath(new Dot(x, y));


        //Output results
        grid.print((gx, gy) -> StringHelpers.padLeft("" + grid.getData(gx, gy), 3));

    }
}
