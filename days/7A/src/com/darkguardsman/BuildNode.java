package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class BuildNode {
    public final char id;

    public final List<BuildNode> pathsInto = new ArrayList();
    public final List<BuildNode> pathsOutFrom = new ArrayList();

    public BuildNode(char id) {
        this.id = id;
    }

    public void addPath(BuildNode path, boolean intoThis) {
        if (intoThis) {
            if (!pathsInto.contains(path)) {
                pathsInto.add(path);
            } else {
                throw new RuntimeException("We already have a path into " + this + " from " + path);
            }
            if (!path.pathsOutFrom.contains(this)) {
                path.pathsOutFrom.add(this);
            } else {
                throw new RuntimeException("We already have a path from " + this + " into " + path);
            }
        } else {
            if (!pathsOutFrom.contains(path)) {
                pathsOutFrom.add(path);
            } else {
                throw new RuntimeException("We already have a path into " + this + " from " + path);
            }
            if (!path.pathsInto.contains(this)) {
                path.pathsInto.add(this);
            } else {
                throw new RuntimeException("We already have a path from " + this + " into " + path);
            }
        }

        //TODO check for circle path
    }

    @Override
    public String toString() {

        //Map nodes into this
        String into = "";
        for(BuildNode node : pathsInto)
        {
            into += " ";
            into += node.id;
        }

        //Map nodes out of this
        String out = "";
        for(BuildNode node : pathsOutFrom)
        {
            out += " ";
            out += node.id;
        }
        return "Node[" + into + " >> " + id + " >> " + out + "]";
    }
}
