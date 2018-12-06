package com.darkguardsman;

/**
 * Map used to store data points from a 2D plane within a min-max bound
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
 */
public class GridDataMap {
    public final int x;
    public final int y;
    public final int sizeX;
    public final int sizeY;

    //Data in the map
    private GridDataPoint[] data;


    public static GridDataMap newMinMaxMap(int minX, int minY, int maxX, int maxY, int padding) {
        int x = minX - padding;
        int y = maxY - padding;
        int sizeX = (maxX - minX) + padding * 2;
        int sizeY = (maxY - minY) + padding * 2;

        return new GridDataMap(x, y, sizeX, sizeY);
    }

    public GridDataMap(int x, int y, int sizeX, int sizeY) {
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        data = new GridDataPoint[sizeX * sizeY];
    }

    /**
     * Converts the xy from world position to map
     * position then converts to index value
     * for data set.
     *
     * @param x - world position
     * @param y - world position
     * @return index in the {@link #data}
     */
    public int getIndex(int x, int y) {
        //Offset xy by lower corner
        int xx = x - this.x;
        int yy = y - this.y;

        //index = column start (column index * columns) + row
        return yy * sizeY + xx;
    }

    /**
     * Checks if the xy are inside the map
     * <p>
     * Wrappers to {@link #getIndex(int, int)}
     *
     * @param x
     * @param y
     * @return true if inside the map
     */
    public boolean insideMap(int x, int y) {
        return insideMap(getIndex(x, y));
    }

    public void checkIfInMap(int x, int y) {
        checkIfInMap(getIndex(x, y), x, y);
    }

    public void checkIfInMap(int index, int x, int y) {
        if (!insideMap(index)) {
            throw new RuntimeException("Value of [" + x + ", " + y + "] is outside of the map");
        }
    }

    /**
     * Checks if the index is inside the map data set
     *
     * @param index - index value from {@link #getIndex(int, int)}
     * @return true if inside the map data set
     */
    public boolean insideMap(int index) {
        return index >= 0 && index < data.length;
    }

    /**
     * Inserts data into the map
     *
     * @param x  - world position
     * @param y  - world position
     * @param id - data
     * @throws RuntimeException if xy are outside map
     */
    public void insert(int x, int y, GridDataPoint id) {
        int index = getIndex(x, y);
        checkIfInMap(index, x, y);
        data[index] = id;
    }

    /**
     * Gets the data stored in the map
     *
     * @param x - world position
     * @param y - world position
     * @return data in map
     * @throws RuntimeException if xy are outside map
     */
    public GridDataPoint getData(int x, int y) {
        int index = getIndex(x, y);
        checkIfInMap(index, x, y);
        return data[index];
    }
}
