package com.darkguardsman.helpers.grid;

import com.darkguardsman.helpers.Dot;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public class GridChar extends GridPrefab
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
        for (int y = 0; y < sizeY && y < lines.size(); y++)
        {
            //Get line chars
            final String line = lines.get(y);
            final char[] chars = line.toCharArray();

            //Slot each char into the grid
            for (int x = 0; x < sizeX; x++)
            {
                setData(x, y, chars[x]);
            }
        }
    }

    public void print()
    {
        print(null);
    }

    public void print(BiFunction<Integer, Integer, Character> renderOverride)
    {
        final StringBuilder builder = new StringBuilder();
        forEach((grid, x, y) -> {
            if (x == 0 && y != 0)
            {
                builder.append("\n");
            }
            char c = grid.getData(x, y);
            if (renderOverride != null)
            {
                Character c2 = renderOverride.apply(x, y);
                if (c2 != null)
                {
                    c = c2;
                }
            }
            builder.append(c);
            return false;
        });
        System.out.println(builder.toString());
    }

    public void forEach(GridCellFunction<GridChar> function)
    {
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (function.onCell(this, x, y))
                {
                    return;
                }
            }
        }
    }
}
