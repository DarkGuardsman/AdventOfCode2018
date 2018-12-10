package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class Node {
    public final List<Node> nodes = new ArrayList();
    public final List<Integer> meta = new ArrayList();

    public int sumMeta()
    {
        int count = 0;
        for(Integer number : meta)
        {
            count += number;
        }
        for(Node node : nodes)
        {
            count += node.sumMeta();
        }
        return count;
    }
}
