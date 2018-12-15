package com.darkguardsman.helpers.grid;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public class GridPrefab
{
    public final int sizeX;
    public final int sizeY;

    public GridPrefab(int sizeX, int sizeY)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void checkBound(int x, int y)
    {
        if(!isInGrid(x, y))
        {
            throw new IllegalArgumentException("P[" + x + ", " + y + "] are outside the grid: " + sizeX + "x" + sizeY);
        }
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
}
