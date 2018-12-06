package com.darkguardsman.helpers;

/**
 * Simple object to store 2D points
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
 */
public class Dot {
    public final int x;
    public final int y;

    public Dot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Dot add(Direction2D direction2D) {
        return add(direction2D.offsetX, direction2D.offsetY);
    }

    public Dot add(int x, int y) {
        return new Dot(x + this.x, y + this.y);
    }

    /**
     * Distance between two points using
     * https://en.wikipedia.org/wiki/Taxicab_geometry
     *
     * @param dot
     * @return
     */
    public int distanceMan(Dot dot) {
        return distanceMan(dot.x, dot.y);
    }

    /**
     * Distance between two points using
     * https://en.wikipedia.org/wiki/Taxicab_geometry
     *
     * @param x
     * @param y
     * @return
     */
    public int distanceMan(int x, int y) {
        return Math.abs(x - this.x) + Math.abs(y - this.y);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof Dot) {
            return ((Dot) object).x == x && ((Dot) object).y == y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public String toString() {
        return "Dot[" + x + ", " + y + "]";
    }

    public Dot copy() {
        return new Dot(x, y);
    }
}
