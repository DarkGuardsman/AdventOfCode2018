package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/18.
 */
public class Grid {
    private final int[] gridData;

    public final int xSize;
    public final int ySize;

    public Grid(int xSize, int ySize) {
        gridData = new int[xSize * ySize];
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public int getIndex(int x, int y) {
        return y * ySize + x;
    }

    public boolean isInsideGrid(int x, int y) {
        if (x < 0 || y < 0 || x >= xSize || y >= ySize) {
            return false;
        }
        int index = getIndex(x, y);
        return index >= 0 && index < gridData.length;
    }

    public void checkBounds(int x, int y) {
        if (!isInsideGrid(x, y)) {
            throw new RuntimeException("Pos[" + x + ", " + y + "] are outside the grid bounds Size[" + xSize + "x" + ySize + "]");
        }
    }

    public void setData(int x, int y, int data) {
        checkBounds(x, y);
        int index = getIndex(x, y);
        gridData[index] = data;
    }

    public int getData(int x, int y) {
        checkBounds(x, y);
        int index = getIndex(x, y);
        return gridData[index];
    }

    public void forEachCell(GridCellFunction function) {
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                function.apply(this, x, y, getData(x, y));
            }
        }
    }

}
