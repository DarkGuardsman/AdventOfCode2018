package com.darkguardsman.helpers.path;

import com.darkguardsman.helpers.Dot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
@FunctionalInterface
public interface OnPathFunction {
    void apply(Dot current, Dot next);
}
