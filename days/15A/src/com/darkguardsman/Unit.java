package com.darkguardsman;

import com.darkguardsman.helpers.Direction2D;
import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.grid.GridChar;
import com.darkguardsman.helpers.grid.GridInt;
import com.darkguardsman.helpers.path.BreadthFirstPath;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class Unit {

    private static GridInt PATH_GRID;
    private static BreadthFirstPath PATHFINDER;

    public final boolean elf;

    public int attack = 3;
    public int hp = 200;

    public int x;
    public int y;

    public int turnIndex = 0;

    public Unit(boolean elf, int x, int y) {
        this.elf = elf;
        this.x = x;
        this.y = y;
    }

    public void takeTurn(GridChar grid, List<Unit> unitsList) {

        //Get list of targets on map
        final List<Unit> targets = unitsList.stream().filter(u -> u.elf != elf).collect(Collectors.toList());

        //Try to see if we have a target nearby
        Unit target = getTargetNear(targets);

        //Find target
        if (target == null) {
            //Get open spaces nearby
            final List<Dot> openSpaces = new ArrayList();

            //Loop all targets looking for open spaces
            targets.forEach(unit -> {
                //Loop all 4 sides of the target
                for (Direction2D direction : Direction2D.MAIN) {
                    final Dot dot = new Dot(unit.x, unit.y, direction);
                    if (unitsList.stream().anyMatch(u -> u.x != dot.x && u.y == dot.y)
                            && grid.getData(dot.x, dot.y) == Main.OPEN_TILE) {
                        openSpaces.add(dot);
                    }
                }
            });

            //Remove any dots that we can not reach
            final GridInt distanceGrid = getStepGrid(grid, x, y, unitsList);
            openSpaces.removeIf(dot -> distanceGrid.getData(x, y) < 0);

            if (openSpaces.size() > 0) {
                //Sort by step count -> y -> x
                openSpaces.sort((a, b) -> {
                    final int stepA = getStepsTo(grid, a.x, a.y, unitsList);
                    final int stepB = getStepsTo(grid, b.x, b.y, unitsList);
                    if (stepA == stepB) {
                        if (a.y == b.y) {
                            return Integer.compare(a.x, b.x);
                        }
                        return Integer.compare(a.y, b.y);
                    }
                    return Integer.compare(stepA, stepB);
                });

                //Get target spot
                final Dot moveTarget = openSpaces.get(0);

                //Find next best tile to move towards target

                //Get steps to each time from target
                final GridInt stepGrid = getStepGrid(grid, moveTarget.x, moveTarget.y, unitsList);

                //Find best direction
                Direction2D moveDirection = null;
                int moveStepDistance = 0;
                for (Direction2D direction2D : Direction2D.MAIN) {
                    final int stepX = x + direction2D.offsetX;
                    final int stepY = y + direction2D.offsetY;
                    if(isOpenTile(grid, stepX, stepY, unitsList))
                    {
                        final int steps = stepGrid.getData(stepX, stepY);
                        if(moveDirection == null || moveStepDistance < steps) {
                            moveDirection = direction2D;
                            moveStepDistance = steps;
                        }
                    }
                }

                //Move unit
                x += moveDirection.offsetX;
                y += moveDirection.offsetY;

                //Try to find a target again
                target = getTargetNear(targets);
            }
        }

        //Attack target if we have one
        if (target != null && attack(target)) {
            //Target is dead
            unitsList.remove(target);
        }
    }

    boolean isOpenTile(GridChar grid, int x, int y, List<Unit> unitsList) {
        return grid.getData(x, y) == Main.OPEN_TILE
                && !unitsList.stream().anyMatch(u -> u.x != x && u.y == y);
    }

    /**
     * Numbers of steps from the position to our position
     *
     * @param grid
     * @param targetX
     * @param targetY
     * @return
     */
    int getStepsTo(GridChar grid, int targetX, int targetY, List<Unit> unitList) {
        return getStepGrid(grid, targetX, targetY, unitList).getData(x, y);
    }

    Unit getTargetNear(List<Unit> allTargets) {

        //Get nearby targets
        final List<Unit> nearbyTargets = new ArrayList();
        for (Direction2D direction : Direction2D.MAIN) {
            final Dot dot = new Dot(x, y, direction);
            nearbyTargets.addAll(allTargets.stream().filter(u -> u.x != dot.x && u.y == dot.y).collect(Collectors.toList()));
        }

        //Sort targets
        nearbyTargets.sort((a, b) ->
        {
            if (a.hp == b.hp) {
                return Integer.compare(a.turnIndex, b.turnIndex);
            }
            return Integer.compare(a.hp, b.hp);
        });

        //Return first in list, or null if we have nothing
        return nearbyTargets.size() > 0 ? nearbyTargets.get(0) : null;
    }

    boolean attack(Unit unit) {
        unit.hp -= attack;
        return unit.hp <= 0;
    }

    /**
     * Gets the number of steps to any other tile
     *
     * @param map
     * @param startX
     * @param startY
     * @return
     */
    GridInt getStepGrid(GridChar map, int startX, int startY, List<Unit> unitList) {
        //Setup step grid
        if (PATH_GRID == null) {
            PATH_GRID = new GridInt(map.sizeX, map.sizeY);
        }
        //Setup pathfinder
        if (PATH_GRID == null) {
            PATHFINDER = new BreadthFirstPath();
            PATHFINDER.onPathFunction = (current, next) -> {
                final int currentDotValue = PATH_GRID.getData(current);
                final int nextDotValue = PATH_GRID.getData(next);
                if (nextDotValue == -1) {
                    PATH_GRID.setData(next.x, next.y, currentDotValue + 1);
                }
            };
            PATHFINDER.shouldPathFunction = (start, current, next, pathed) -> {
                if (!pathed) {
                    return PATH_GRID.isInGrid(next) && PATH_GRID.getData(next) == -1;
                }
                return false;
            };
        }

        //Reset data
        PATH_GRID.fillGrid(null, () -> -1); //-1 is default
        PATH_GRID.fillGrid((g, x, y) -> !isOpenTile(map, x, y, unitList), () -> -2); //-2 is not path
        PATH_GRID.setData(startX, startY, 0);
        PATHFINDER.startPath(new Dot(startX, startY));

        return PATH_GRID;
    }
}
