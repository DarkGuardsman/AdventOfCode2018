package com.darkguardsman.helpers.grid;

import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.functions.Int2StringFunction;

import java.util.function.IntSupplier;

/**
 * Integer based verison of {@link GridPrefab}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class GridInt extends GridPrefab<GridInt> {
    private int[][] data;

    public GridInt(int sizeX, int sizeY) {
        super(sizeX, sizeY);
        this.data = new int[sizeX][sizeY];
    }

    public void setData(int x, int y, int value) {
        checkBound(x, y);
        data[x][y] = value;
    }

    public int getData(Dot dot) {
        return getData(dot.x, dot.y);
    }

    public int getData(int x, int y) {
        checkBound(x, y);
        return data[x][y];
    }

    public int getDataIfGrid(int x, int y) {
        if (isInGrid(x, y)) {
            return data[x][y];
        }
        return 0;
    }

    public void fillGrid(GridConditionalFunction<GridInt> conditionalFunction, IntSupplier supplier) {
        forEach((g, x, y) -> {
            if (conditionalFunction.isTrue(g, x, y)) {
                g.setData(x, y, supplier.getAsInt());
            }
            return false;
        });
    }


    public void print() {
        print(null);
    }

    public void print(Int2StringFunction renderOverride) {
        final StringBuilder builder = new StringBuilder();
        forEach((grid, x, y) -> {
            if (x == 0 && y != 0) {
                builder.append("\n");
            }
            if (renderOverride != null) {
                String s = renderOverride.apply(x, y);
                if (s != null) {
                    builder.append(s);
                    return false;
                }
            }
            builder.append(grid.getData(x, y));
            return false;
        });
        System.out.println(builder.toString());
    }
}
