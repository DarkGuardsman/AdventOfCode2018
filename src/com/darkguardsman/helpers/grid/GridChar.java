package com.darkguardsman.helpers.grid;

import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.StringHelpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public class GridChar extends GridPrefab<GridChar>
{
    private char[][] data;
    private static final HashMap<Integer, Color> idToColor = new HashMap();
    private static final Random rand = new Random();

    public GridChar(int sizeX, int sizeY)
    {
        super(sizeX, sizeY);
        this.data = new char[sizeX][sizeY];
    }

    public GridChar(int sizeX, int sizeY, char fill)
    {
        this(sizeX, sizeY);
        fillSimple(fill);
    }

    public void setData(int x, int y, char value)
    {
        checkBound(x, y);
        data[x][y] = value;
    }

    public char getData(Dot dot)
    {
        return getData(dot.x, dot.y);
    }

    public char getData(int x, int y)
    {
        checkBound(x, y);
        return data[x][y];
    }

    public char getDataIfGrid(int x, int y)
    {
        if (isInGrid(x, y))
        {
            return data[x][y];
        }
        return '@';
    }

    public void fillSimple(char c)
    {
        forEach((g, x, y) -> {
            g.setData(x, y, c);
            return false;
        });
    }

    /**
     * Fills in the grid with data from lines.
     * <p>
     * The size of list need to match the sizeY.
     * Size of each line needs to match sizeX.
     *
     * @param lines - list of lines
     */
    public void fillFromLineData(List<String> lines)
    {
        fillFromLineData(lines, null);
    }

    /**
     * Fills in the grid with data from lines.
     * <p>
     * The size of list need to match the sizeY.
     * Size of each line needs to match sizeX.
     *
     * @param lines - list of lines
     */
    public void fillFromLineData(List<String> lines, GridCellFunction<GridChar> callback)
    {
        for (int y = 0; y < sizeY && y < lines.size(); y++)
        {
            //Get line chars
            final String line = lines.get(y);
            final char[] chars = line.toCharArray();

            //Slot each char into the grid
            for (int x = 0; x < sizeX; x++)
            {
                setData(x, y, chars[x]);
                if (callback != null)
                {
                    callback.onCell(this, x, y);
                }
            }
        }
    }

    public void print()
    {
        print(null);
    }

    public void print(BiFunction<Integer, Integer, String> renderOverride)
    {
        final StringBuilder builder = new StringBuilder();
        int padding = (sizeY + "").length() + 1;
        forEach((grid, x, y) -> {
            if (x == 0)
            {
                if (y != 0)
                {
                    builder.append("\n");
                }
                builder.append(StringHelpers.padRight("" + y, padding));
            }
            if (renderOverride != null)
            {
                String c2 = renderOverride.apply(x, y);
                if (c2 != null)
                {
                    builder.append(c2);
                    return false;
                }
            }
            builder.append(grid.getData(x, y));

            return false;
        });
        System.out.println(builder.toString());
    }


    public BufferedImage generateImage() //TODO abstract to super prefab
    {
        final BufferedImage rawImage = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB);

        //Set pixel to color based on data at xy
        forEach((g, x, y) ->
        {
            rawImage.setRGB(x, y, getColor(x, y).getRGB());
            return false;
        });

        return rawImage;
    }

    private Color getColor(int x, int y) //TODO abstract to super prefab
    {
        int id = getColorID(x, y);
        //Create random color
        if (!idToColor.containsKey(id))
        {
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            idToColor.put(id, new Color(r, g, b));
        }
        return idToColor.get(id);
    }

    private int getColorID(int x, int y) //TODO abstract to super prefab, use hashcode() as ID for colors
    {
        return (int) getData(x, y);
    }

    public GridChar copy()
    {
        GridChar gridChar = new GridChar(sizeX, sizeY);
        forEach((g, x, y) -> {
            gridChar.setData(x, y, g.getData(x, y));
            return false;
        });
        return gridChar;
    }
}
