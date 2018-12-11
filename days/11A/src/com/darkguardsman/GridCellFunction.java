package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/18.
 */
@FunctionalInterface
public interface GridCellFunction {

    void apply(Grid grid, int x, int y, int data);
}
