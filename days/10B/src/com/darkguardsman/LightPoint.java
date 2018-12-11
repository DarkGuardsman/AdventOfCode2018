package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class LightPoint {
    public double x;
    public double y;

    public double vx;
    public double vy;

    @Override
    public String toString() {
        return "LP[" + x + ", " + y + "| " + vx + ", " + vy + "]";
    }

    public void move() {
        x += vx;
        y += vy;
    }

    public boolean isAt(long x, long y)
    {
        return this.x == x && this.y == y;
    }
}
