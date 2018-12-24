package com.darkguardsman.helpers.grid;

import com.darkguardsman.helpers.Dot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public abstract class GridPrefab<G extends GridPrefab>
{
    public final int sizeX;
    public final int sizeY;

    public GridConditionalFunction<G> function;

    public GridPrefab(int sizeX, int sizeY)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void checkBound(int x, int y)
    {
        if (!isInGrid(x, y))
        {
            throw new IllegalArgumentException("P[" + x + ", " + y + "] are outside the grid: " + sizeX + "x" + sizeY);
        }
    }

    public boolean isInGrid(Dot dot)
    {
        return isInGrid(dot.x, dot.y);
    }

    public boolean isInGrid(int x, int y)
    {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY)
        {
            return false;
        }
        return true;
    }

    public int getSize()
    {
        return sizeX * sizeY;
    }

    public boolean hasData(int x, int y)
    {
        if (function != null)
        {
            return function.isTrue((G) this, x, y);
        }
        return true;
    }

    public boolean forEachRow(GridCellFunction<G> function, int y)
    {
        for (int x = 0; x < sizeX; x++)
        {
            if (function.onCell((G) this, x, y))
            {
                return true;
            }
        }
        return false;
    }

    public boolean forEach(GridCellFunction<G> function)
    {
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (function.onCell((G) this, x, y))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
