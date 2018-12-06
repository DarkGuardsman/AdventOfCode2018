package com.darkguardsman.helpers;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
 */
public enum Direction2D {
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
}
