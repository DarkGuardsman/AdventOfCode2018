package com.darkguardsman;


import com.darkguardsman.helpers.FileHelpers;
import com.darkguardsman.helpers.grid.GridChar;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class Main
{
    public static final char ELF = 'E';
    public static final char GOBLIN = 'G';
    public static final char OPEN_TILE = '.';
    public static final char WALL_TILE = '#';

    public static void main(String... args)
    {
        final File file = new File(args[0]);
        System.out.println("File: " + file);


        System.out.println("\nReading lines: ");
        List<String> lines = FileHelpers.getLines(file);
        System.out.println("\tLines: " + lines.size());

        System.out.println("\nConverting to grid: ");
        final int sizeY = lines.size();
        final int sizeX = lines.get(0).length();

        final GridChar grid = new GridChar(sizeX, sizeY);
        final List<Unit> units = new ArrayList();

        //Fill in grid from loaded data, for each cell replace units with ground tiles
        grid.fillFromLineData(lines, (g, x, y) -> {
            char c = g.getData(x, y);
            if (c == ELF)
            {
                units.add(new Unit(true, x, y, 3));
                g.setData(x, y, OPEN_TILE);
            }
            else if (c == GOBLIN)
            {
                units.add(new Unit(false, x, y, 3));
                g.setData(x, y, OPEN_TILE);
            }
            return false;
        });

        int attackPower = 3;

        boolean lost;

        do
        {
            //Debug
            System.out.println("\nAttackPower: " + attackPower);
            Unit.idCounter = 0;
            List<Unit> unitList = units.stream().map(u -> u.copy()).collect(Collectors.toList());
            for(Unit unit : unitList)
            {
                if(unit.elf)
                {
                    unit.setAttack(attackPower);
                }
            }

            lost = runSim(grid.copy(), unitList);
            attackPower++;
        }
        while(lost == false);
    }

    public static boolean runSim(GridChar grid, List<Unit> units)
    {
        System.out.println("\nStarting battle: ");
        print(grid, units);

        final int startElfs = (int) units.stream().filter(u -> u.elf).count();

        int runs = 0;
        //Loop until there are no enemies
        exitWhile:
        while (true)
        {
            //Initiative
            sort(units);

            //See if we have enemies
            int elfsLeft = (int) units.stream().filter(u -> u.elf).count();
            int goblinsLeft = (int) units.stream().filter(u -> !u.elf).count();
            if (elfsLeft == 0 || goblinsLeft == 0)
            {
                System.out.println("end-----------------------------------------");
                break;
            }

            //Take turns
            Queue<Unit> turnOrder = new LinkedList();
            turnOrder.addAll(units);

            while (turnOrder.peek() != null)
            {
                final Unit unit = turnOrder.poll();
                //If not in list it died before its turn
                if (units.contains(unit) && unit.hp > 0)
                {
                    if (!unit.takeTurn(grid, units))
                    {
                        break exitWhile;
                    }
                }
            }

            //Remove the dead
            units.removeIf(u -> u.hp <= 0);

            //Debug
            //print(grid, units);
            runs++;
        }

        //If we lost 1 elf
        if(((int) units.stream().filter(u -> u.elf).count()) < startElfs)
        {
            return false;
        }


        System.out.println("\nResult: ");


        int sum = units.stream().mapToInt(u -> u.hp).sum();
        int result = sum * runs;

        System.out.println("\tSum: " + sum);
        System.out.println("\tRuns: " + runs);
        System.out.println("\tScore: " + result);

        return true;
    }

    static void sort(List<Unit> units)
    {
        units.sort((a, b) -> {
            if (a.y == b.y)
            {
                return Integer.compare(a.x, b.x);
            }
            return Integer.compare(a.y, b.y);
        });

        //Set turnIndex value, used by target sorter
        for (int i = 0; i < units.size(); i++)
        {
            units.get(i).turnIndex = i;
        }
    }

    static void print(GridChar grid, List<Unit> units)
    {
        grid.print((x, y) -> {
            for (Unit unit : units)
            {
                if (unit.x == x && unit.y == y)
                {
                    return "" + (unit.elf ? ELF : GOBLIN);
                }

                if (x == (grid.sizeX - 1))
                {
                    return grid.getData(x, y) + "\t" + units.stream().filter(u -> u.y == y).map(u -> u.toString()).collect(Collectors.joining(", "));
                }
            }
            return null;
        });
    }
}
