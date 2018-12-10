package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class BuildNode {
    public final char id;

    public final List<BuildNode> prevNodes = new ArrayList();
    public final List<BuildNode> nextNodes = new ArrayList();

    public BuildNode(char id) {
        this.id = id;
    }

    public void addPath(BuildNode path) {

        if (!nextNodes.contains(path)) {
            nextNodes.add(path);
        } else {
            throw new RuntimeException("We already have a path into " + this + " from " + path);
        }
        if (!path.prevNodes.contains(this)) {
            path.prevNodes.add(this);
        } else {
            throw new RuntimeException("We already have a path from " + this + " into " + path);
        }
    }

    @Override
    public String toString() {

        //Map nodes into this
        String into = "";
        for (BuildNode node : prevNodes) {
            into += " ";
            into += node.id;
        }

        //Map nodes out of this
        String out = "";
        for (BuildNode node : nextNodes) {
            out += " ";
            out += node.id;
        }
        return "Node[" + into + " >> " + id + " >> " + out + "]";
    }
}
