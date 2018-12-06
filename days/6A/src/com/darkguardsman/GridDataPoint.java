package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;

/**
 * Data points stored in {@link GridDataMap}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
 */
public class GridDataPoint {
    /**
     * ID of the object that owns the point
     */
    public int owner;
    /**
     * Distance from the object center using https://en.wikipedia.org/wiki/Taxicab_geometry
     */
    public int distance;

    public final List<DotSource> tied = new ArrayList();

    public GridDataPoint(int owner, int distance) {
        this.owner = owner;
        this.distance = distance;
    }
}
