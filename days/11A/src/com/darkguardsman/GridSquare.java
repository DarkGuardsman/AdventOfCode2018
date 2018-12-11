package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/18.
 */
public class GridSquare {
    public final int x;
    public final int y;
    int power;

    public GridSquare(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "GS[" + x + "," + y + "=" + power + "]";
    }
}
