package com.darkguardsman;


import com.darkguardsman.helpers.FileHelpers;
import com.darkguardsman.helpers.StringHelpers;
import com.darkguardsman.helpers.grid.GridChar;
import com.darkguardsman.helpers.image.ImageHelpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/18.
 */
public class Main
{
    public static final char WATER = '|';
    public static final char WATER_REST = '~';
    public static final char CLAY = '#';
    public static final char AIR = '.';

    public static void main(String... args)
    {
        final File file = new File(args[0]);

        final int springX = 500;
        final int springY = 0;

        System.out.println("File: " + file);

        System.out.println("\nReading lines: ");
        List<String> lines = readFile(file);

        System.out.println("\nConverting to objects: ");
        List<DrawLine> drawLines = getDrawLines(lines);
        drawLines.forEach(d -> System.out.println("\t" + d));

        System.out.println("\nGenerate Grid: ");
        final int minX = drawLines.stream().filter(d -> d.xAxis).min(Comparator.comparingInt(d -> d.axis)).get().axis - 3; //3 is Padding for visuals
        final int minY = 0; //drawLines.stream().filter(d -> !d.xAxis).min(Comparator.comparingInt(d -> d.axis)).get().axis;
        final int maxX = drawLines.stream().filter(d -> d.xAxis).max(Comparator.comparingInt(d -> d.axis)).get().axis + 3;
        final int maxY = drawLines.stream().filter(d -> !d.xAxis).max(Comparator.comparingInt(d -> d.axis)).get().axis;

        final int deltaX = maxX - minX + 1; //increase by 1 to include largest value
        final int deltaY = maxY - minY + 1; //  Ex: 0 to 10 for xAxis our largest value would be 10. Normalized 10 would still be 10 and outside our array of size 10, values(0-9)

        System.out.println("\t" + minX + "," + minY + " to " + maxX + "," + maxY);
        System.out.println("\t" + deltaX + "x" + deltaY);

        final GridChar gridChar = new GridChar(deltaX, deltaY, AIR);

        System.out.println("\nNormalizing Data to Grid: ");
        drawLines.forEach(d -> {
            if (d.xAxis)
            {
                d.axis -= minX;
                d.lineStart -= minY;
                d.lineEnd -= minY;
            }
            else
            {
                d.axis -= minY;
                d.lineStart -= minX;
                d.lineEnd -= minX;
            }
        });
        drawLines.forEach(d -> System.out.println("\t" + d));

        System.out.println("\nDraw Grid: ");
        drawLines.forEach(d -> {
            System.out.println("\t" + d);
            d.draw(gridChar);
        });
        gridChar.setData(springX - minX, springY, '+');
        gridChar.print();
        generateGridImage(new File(file.getParent(), "output0.png"), gridChar, 1);


        System.out.println("\nFilling water: ");
        gridChar.setData(springX - minX, springY + 1, '|');
        //gridChar.print();

        int y = 1;
        int runs = 0;
        int image = 1;
        while (y <= maxY)
        {
            System.out.println(runs + ": Searching " + y);
            //Find all stream ends and path down 1
            if (gridChar.forEachRow((g, gx, gy) -> handleLocation(g, gx, gy), y))
            {
                //Search from top again to find all streams we missed
                y = 0;
                //System.out.println(runs + ": Reset to top");
                generateGridImage(new File(file.getParent(), getFileName(image++)), gridChar, 1);
            }
            //Move down 1
            else
            {
                y++;
                //System.out.println(runs + ": Moved down 1");
            }

            runs++;
            //gridChar.print();
            //generateGridImage(new File(file.getParent(), "output-" + runs + ".png"), gridChar, 4);
        }

    }

    static String getFileName(int index)
    {
        return "output" + StringHelpers.padLeft("" + index, 6).replaceAll("\\s", "0") + ".png";
    }

    static boolean handleLocation(GridChar grid, int x, int y)
    {
        if (isStreamEnd(grid, x, y))
        {
            //System.out.println("\tFound Stream end " + x + "," + y);

            //Draw stream down 1
            y += 1;
            grid.setData(x, y, WATER);

            //Check what is under stream, If hit bottom try to fill container
            y += 1;
            char c = grid.getDataIfGrid(x, y);
            if (c == CLAY || c == WATER_REST)
            {
                //Keep filling container until we fall off an edge
                while (tryToFillContainer(grid, x, y))
                {
                    y--;
                    //System.out.println("\tFilled a row " + x + "," + y);
                }

                //Return true to reset Y search
                return true;
            }
            else if (c == 0)
            {
                return false;
            }
        }
        return false;
    }

    static boolean tryToFillContainer(GridChar grid, int gx, int gy)
    {
        int leftIndex = -1;
        int rightIndex = -1;

        //Search left
        for (int x = gx; x >= 0; x--)
        {
            if (grid.getData(x, gy) == CLAY)
            {
                leftIndex = x;
                break;
            }
            else
            {
                //Set to water
                grid.setData(x, gy, WATER);

                //Hit edge
                char c = grid.getDataIfGrid(x, gy + 1);
                if (c != CLAY && c != WATER_REST)
                {
                    break;
                }
            }
        }

        //Search right
        for (int x = gx; x < grid.sizeX; x++)
        {
            if (grid.getData(x, gy) == CLAY)
            {
                rightIndex = x;
                break;
            }
            else
            {
                //Set to water
                grid.setData(x, gy, WATER);

                //Hit edge
                char c = grid.getDataIfGrid(x, gy + 1);
                if (c != CLAY && c != WATER_REST)
                {
                    break;
                }
            }
        }


        //If wall on both sides fill
        if (leftIndex != -1 && rightIndex != -1)
        {
            for (int x = leftIndex + 1; x < rightIndex; x++)
            {
                grid.setData(x, gy, WATER_REST);
            }
            return true;
        }
        return false;
    }

    static boolean isStreamEnd(GridChar grid, int x, int y)
    {
        if (grid.getDataIfGrid(x, y) == WATER)
        {
            //Check that what is under us is not the end of the map or an clay block
            char c = grid.getDataIfGrid(x, y + 1);
            return (c == AIR || c == WATER_REST)
                    //We are only a steam end if both blocks to the side are not water

                    //EX:  .|.
                    && (grid.getDataIfGrid(x - 1, y) != WATER
                    && grid.getDataIfGrid(x + 1, y) != WATER

                    //If 1 air tile
                    //EX:    .||||||||#
                    || grid.getDataIfGrid(x - 1, y) == AIR

                    //If 1 air tile
                    //EX:    #||||||||.
                    || grid.getDataIfGrid(x + 1, y) == AIR);


            //Case 1: both air - valid
            //Case 2: both clay - valid
            //Case 3: Either is clay or air - valid
            //Case 4: Both are flowing water - invalid
            //Case 5: both are resting water - valid but should never happen, technically would be an error
            //Case 6: Either is flowing water, but 1 block is air - valid
        }
        return false;
    }

    static List<String> readFile(File file)
    {
        List<String> lines = FileHelpers.getLines(file);
        System.out.println("\tLines: " + lines.size());
        return lines;
    }

    static List<DrawLine> getDrawLines(List<String> lines)
    {
        return lines.stream().map(Main::parse).collect(Collectors.toList());
    }

    static DrawLine parse(String line)
    {
        final String[] split = line.split(",");
        final String firstValue = split[0].trim();
        final String secondValue = split[1].trim();

        final DrawLine drawLine = new DrawLine();


        if (firstValue.startsWith("x") || firstValue.startsWith("y"))
        {
            drawLine.axis = Integer.parseInt(firstValue.substring(2));
            drawLine.xAxis = firstValue.startsWith("x");
        }
        else
        {
            throw new IllegalArgumentException(firstValue + " contains unknown start character");
        }

        if (secondValue.startsWith("x") || secondValue.startsWith("y"))
        {
            drawLine.lineStart = Integer.parseInt(secondValue.substring(2, secondValue.indexOf(".")));
        }
        else
        {
            throw new IllegalArgumentException(secondValue + " contains unknown start character");
        }
        drawLine.lineEnd = Integer.parseInt(secondValue.substring(secondValue.lastIndexOf(".") + 1));

        if (drawLine.lineEnd < drawLine.lineStart)
        {
            int temp = drawLine.lineEnd;
            drawLine.lineEnd = drawLine.lineStart;
            drawLine.lineStart = temp;
            System.out.println("Inverted start and end: " + drawLine);
        }

        return drawLine;
    }

    static void generateGridImage(File file, GridChar gridDataMap, int scale)
    {
        BufferedImage rawImage = gridDataMap.generateImage();
        rawImage = ImageHelpers.scaleImage(rawImage, scale);
        ImageHelpers.savePNG(file, rawImage);
    }
}
