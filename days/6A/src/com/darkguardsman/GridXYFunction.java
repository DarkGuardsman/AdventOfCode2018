package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
 */
@FunctionalInterface
public interface GridXYFunction {

    /**
     * Callback function for use in {@link GridDataMap#forEachCell(GridXYFunction)}
     *
     * @param x     - world position
     * @param y     - world position
     * @return true to continue, false to exit early
     */
    boolean accept(int x, int y);
}
