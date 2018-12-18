package com.darkguardsman;

import com.darkguardsman.helpers.Direction2D;
import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.grid.GridChar;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class Unit {
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
            openSpaces.removeIf(dot -> !canReach(dot, grid, unitsList));

            //Sort by step count -> y -> x
            openSpaces.sort((a, b) -> {
                int stepA = getStepsTo(a.x, a.y);
                int stepB = getStepsTo(b.x, b.y);
                if (stepA == stepB) {
                    if (a.y == b.y) {
                        return Integer.compare(a.x, b.x);
                    }
                    return Integer.compare(a.y, b.y);
                }
                return Integer.compare(stepA, stepB);
            });
        }

        //Attack target if we have one
        if (target != null && attack(target)) {
            //Target is dead
            unitsList.remove(target);
        }
    }

    boolean canReach(Dot startDot, GridChar grid, List<Unit> unitsList) {

        final Queue<Dot> dots = new LinkedList();
        final Set<Dot> pathed = new HashSet();

        dots.add(startDot);

        while (dots.peek() != null) {
            //Get current path node
            final Dot current = dots.poll();

            //Mark as pathed, so we don't repath
            pathed.add(current);

            //Get next dots
            for (Direction2D direction2D : Direction2D.MAIN) {

                //Get next
                final Dot next = current.add(direction2D);

                //Ensure we have not pathed
                if (!pathed.contains(next)) {

                    //Make sure its not a wall or unit that can block our path
                    if (unitsList.stream().anyMatch(u -> u.x != next.x && u.y == next.y)
                            && grid.getData(next.x, next.y) == Main.OPEN_TILE) {
                        dots.offer(next);
                    }
                    //If it is a blocking tile, add to pathed so we don't recheck
                    else {
                        pathed.add(next);
                    }
                }
            }
        }

        return true;
    }

    int getStepsTo(int x, int y) {
        return 1;
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
}
