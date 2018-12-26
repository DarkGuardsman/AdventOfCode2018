package com.darkguardsman.helpers.grid;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
@FunctionalInterface
public interface GridCharCellValueFunction<G>
{
    /**
     * Called in foreach loop or other logic for each cell
     *
     * @param grid - grid to access or set data
     * @param x    - location in grid
     * @param y    - location in grid
     * @return true to stop loop or event
     */
    boolean onCell(G grid, int x, int y, char oldValue, char newValue);
}
