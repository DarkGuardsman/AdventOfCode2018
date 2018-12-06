package com.darkguardsman;

import com.darkguardsman.helpers.Dot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
 */
public class DotSource extends Dot {
    public final int id;

    public int count = 0;
    public boolean infinite = false;

    public DotSource(int x, int y, int id) {
        super(x, y);
        this.id = id;
    }

    @Override
    public String toString() {
        return super.toString() + " #" + id;
    }

}
