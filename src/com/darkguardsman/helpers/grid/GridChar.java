package com.darkguardsman.helpers.grid;

import com.darkguardsman.helpers.Dot;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public class GridChar extends GridPrefab<GridChar>
{
    private char[][] data;

    public GridChar(int sizeX, int sizeY)
    {
        super(sizeX, sizeY);
        this.data = new char[sizeX][sizeY];
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
        forEach((grid, x, y) -> {
            if (x == 0 && y != 0)
            {
                builder.append("\n");
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
