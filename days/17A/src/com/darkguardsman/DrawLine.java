package com.darkguardsman;

import com.darkguardsman.helpers.grid.GridChar;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/2018.
 */
public class DrawLine
{
    public int axis;
    public boolean xAxis = false;

    public int lineStart;
    public int lineEnd;

    public final int ID;

    private static int idCounter;

    public DrawLine()
    {
        ID = idCounter++;
    }

    public void draw(GridChar grid)
    {
        if(xAxis)
        {
            for(int y = lineStart; y <= lineEnd; y++)
            {
                grid.setData(axis, y, '#');
            }
        }
        else
        {
            for(int x = lineStart; x <= lineEnd; x++)
            {
                grid.setData(x, axis, '#');
            }
        }
    }

    @Override
    public String toString()
    {
        return "DrawLine[" + ID + "| "
                + (xAxis ? "x=" : "y=") + axis + ", "
                + (!xAxis ? "x=" : "y=") + lineStart + ".." + lineEnd
                + "]";
    }
}
