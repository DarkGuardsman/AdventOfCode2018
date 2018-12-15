package com.darkguardsman.helpers;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
 */
public enum Direction2D
{
    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);
    //TODO add corners

    public final int offsetX;
    public final int offsetY;

    public static final Direction2D[] MAIN = {NORTH, EAST, SOUTH, WEST};

    Direction2D(int offsetX, int offsetY)
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Direction2D next()
    {
        int index = ordinal() + 1;
        if (index >= values().length)
        {
            index = 0;
        }
        return values()[index];
    }

    public Direction2D prev()
    {
        int index = ordinal() - 1;
        if (index < 0)
        {
            index = values().length - 1;
        }
        return values()[index];
    }

    public Direction2D left()
    {
        return prev();
    }

    public Direction2D right()
    {
        return next();
    }
}
