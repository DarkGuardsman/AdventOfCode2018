package com.darkguardsman;


import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.FileHelpers;
import com.darkguardsman.helpers.StringHelpers;
import com.darkguardsman.helpers.grid.GridChar;
import com.darkguardsman.helpers.image.ImageHelpers;
import javafx.util.Pair;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
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

    private static int imageSaveCounter = 1;
    private static File file;
    private static GridChar gridChar;

    private static BufferedImage imageBuffer;

    public static void main(String... args)
    {
        file = new File(args[0]);
        boolean outputFrames = args.length > 1 ? Boolean.parseBoolean(args[1]) : false;

        final int springX = 500;
        final int springY = 0;

        System.out.println("File: " + file);

        System.out.println("\nReading lines: ");
        List<String> lines = readFile(file);

        System.out.println("\nConverting to objects: ");
        List<DrawLine> drawLines = getDrawLines(lines);
        drawLines.forEach(d -> System.out.println("\t" + d));

        ///Get size of the grid and create
        System.out.println("\nGenerate Grid: ");
        final int minX = drawLines.stream().filter(d -> d.xAxis).min(Comparator.comparingInt(d -> d.axis)).get().axis - 3; //3 is Padding for visuals
        final int minY = 0; //drawLines.stream().filter(d -> !d.xAxis).min(Comparator.comparingInt(d -> d.axis)).get().axis;
        final int maxX = drawLines.stream().filter(d -> d.xAxis).max(Comparator.comparingInt(d -> d.axis)).get().axis + 3;
        final int maxY = drawLines.stream().filter(d -> !d.xAxis).max(Comparator.comparingInt(d -> d.axis)).get().axis;

        final int deltaX = maxX - minX + 1; //increase by 1 to include largest value
        final int deltaY = maxY - minY + 1; //  Ex: 0 to 10 for xAxis our largest value would be 10. Normalized 10 would still be 10 and outside our array of size 10, values(0-9)

        System.out.println("\t" + minX + "," + minY + " to " + maxX + "," + maxY);
        System.out.println("\t" + deltaX + "x" + deltaY);

        gridChar = new GridChar(deltaX, deltaY, AIR);

        //Offset all data to min so it can be measured from zero to make it easier to draw
        System.out.println("\nNormalizing Data to Grid: ");
        drawLines.forEach(d ->
        {
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

        //For each line draw
        System.out.println("\nDraw Grid: ");
        drawLines.forEach(d ->
        {
            System.out.println("\t" + d);
            d.draw(gridChar);
        });
        //Add the spring
        gridChar.setData(springX - minX, springY, '+');

        //Output start conditions for debug
        gridChar.print();

        //Clear old folder and remake
        final File imgFolder = new File(file.getParent(), "out/outputInit.png");
        if (!imgFolder.exists())
        {
            imgFolder.mkdirs();
        }

        //Generate start image
        imageBuffer = new BufferedImage(gridChar.sizeX, gridChar.sizeY, BufferedImage.TYPE_INT_ARGB);
        ImageHelpers.savePNG(new File(file.getParent(), "out/outputInit.png"), gridChar.generateImage(imageBuffer));

        //Add the first water source
        System.out.println("\nFilling water: ");
        gridChar.setData(springX - minX, springY + 1, '|');

        final Queue<Pair<Dot, Character>> edits;

        if (outputFrames)
        {
            edits = new LinkedList();

            //Each time we change the grid save an image, it massively slows down the program but makes for a good result
            gridChar.onChangeFunction = (g, gx, gy, ov, nv) ->
            {
                if (ov != nv)
                {
                    edits.offer(new Pair(new Dot(gx, gy), nv));
                }
                return true;
            };
        }
        else
        {
            edits = null;
        }

        //Count runs, for debug reasons
        int runs = 0;

        //Move through y level from top (zero) to bottom (maxY)... rests to top several times
        int y = 1;
        while (y <= maxY)
        {
            System.out.println(runs + ": Searching " + y);

            //Find all stream ends and path down 1
            if (gridChar.forEachRow((g, gx, gy) -> handleLocation(gx, gy), y))
            {
                //Search from top again to find all streams we missed
                y = 0;
            }
            //Move down 1
            else
            {
                y++;
                //System.out.println(runs + ": Moved down 1");
            }

            //Increase run
            runs++;
        }

        System.out.println("\nGenerating images of edits: ");
        if (outputFrames)
        {
            //Generate images
            System.out.println("\tEdits: " + edits.size());
            while (edits.peek() != null)
            {
                Pair<Dot, Character> edit = edits.poll();
                generateGridImage(new File(file.getParent(), getFileName(imageSaveCounter++)), edit.getKey().x, edit.getKey().y, edit.getValue());
            }
        }

        ImageHelpers.savePNG(new File(file.getParent(), "out/outputEnd.png"), gridChar.generateImage(imageBuffer));

        System.out.println("\nCounting water: ");
        final List<Character> list = new ArrayList();
        gridChar.forEach((g, gx, gy) ->
        {
            char value = gridChar.getDataIfGrid(gx, gy);
            if (value == WATER || value == WATER_REST)
            {
                list.add(value);
            }
            return false;
        });
        System.out.println("\tCount: " + list.size());
    }

    static String getFileName(int index)
    {
        return "out/output" + StringHelpers.padLeft("" + index, 6).replaceAll("\\s", "0") + ".png";
    }

    static boolean handleLocation(int x, int y)
    {
        if (isStreamEnd(x, y))
        {
            //System.out.println("\tFound Stream end " + x + "," + y);

            //Draw stream down 1
            y += 1;
            gridChar.setData(x, y, WATER);

            //Check what is under stream, If hit bottom try to fill container
            y += 1;
            char c = gridChar.getDataIfGrid(x, y);
            if (c == CLAY || c == WATER_REST)
            {
                //Keep filling container until we fall off an edge
                while (tryToFillContainer(x, y))
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

    static boolean tryToFillContainer(int gx, int gy)
    {
        int leftIndex = -1;
        int rightIndex = -1;

        //Search left
        for (int x = gx; x >= 0; x--)
        {
            if (gridChar.getData(x, gy) == CLAY)
            {
                leftIndex = x;
                break;
            }
            else
            {
                //Set to water
                gridChar.setData(x, gy, WATER);

                //Hit edge
                char c = gridChar.getDataIfGrid(x, gy + 1);
                if (c != CLAY && c != WATER_REST)
                {
                    break;
                }
            }
        }

        //Search right
        for (int x = gx; x < gridChar.sizeX; x++)
        {
            if (gridChar.getData(x, gy) == CLAY)
            {
                rightIndex = x;
                break;
            }
            else
            {
                //Set to water
                gridChar.setData(x, gy, WATER);

                //Hit edge
                char c = gridChar.getDataIfGrid(x, gy + 1);
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
                gridChar.setData(x, gy, WATER_REST);
            }
            return true;
        }
        return false;
    }

    static boolean isStreamEnd(int x, int y)
    {
        if (gridChar.getDataIfGrid(x, y) == WATER)
        {
            //Check that what is under us is not the end of the map or an clay block
            char c = gridChar.getDataIfGrid(x, y + 1);
            return (c == AIR || c == WATER_REST)
                    //We are only a steam end if both blocks to the side are not water

                    //EX:  .|.
                    && (gridChar.getDataIfGrid(x - 1, y) != WATER
                    && gridChar.getDataIfGrid(x + 1, y) != WATER

                    //If 1 air tile
                    //EX:    .||||||||#
                    || gridChar.getDataIfGrid(x - 1, y) == AIR

                    //If 1 air tile
                    //EX:    #||||||||.
                    || gridChar.getDataIfGrid(x + 1, y) == AIR);


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

    static void generateGridImage(File file, int x, int y, char value)
    {
        //Update image
        gridChar.setPixel(imageBuffer, x, y, value);

        //Save updated version
        ImageHelpers.savePNG(file, imageBuffer);
    }
}
