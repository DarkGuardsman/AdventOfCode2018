package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class BuildPath {
    public final char start;
    public final char end;

    public BuildPath(String start, String end) {
        this.start = start.charAt(0);
        this.end = end.charAt(0);

        if(start == end)
        {
            throw new RuntimeException("Start can not equal end. Start: '" + start + "' End: '" + end + "'");
        }

        if (start.length() > 1 || end.length() > 1) {
            throw new RuntimeException("Start or end string is longer than 1. Start: '" + start + "' End: '" + end + "'");
        }
    }

    @Override
    public String toString()
    {
        return "Path[" + start + "]";
    }
}
