package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class Node {
    public final int index;
    public final int depth;

    public final List<Node> nodes = new ArrayList();
    public final List<Integer> meta = new ArrayList();

    public Node(int index, int depth) {
        this.index = index;
        this.depth = depth;
    }

    public int sumMeta() {
        int count = 0;
        for (Integer n : meta) {
            final int number = n;
            if (nodes.isEmpty()) {
                count += number;
                System.out.println(Main.prefixIndent(depth) + "+" + number + "#");
            } else if (number > 0 && number <= nodes.size()) {
                int value = nodes.get(number - 1).sumMeta();
                System.out.println(Main.prefixIndent(depth) + "+" + value + "n  " + nodes.get(number - 1));
                count += value;
            }
        }
        return count;
    }

    public void print() {
        System.out.println(Main.prefixIndent(depth) + this);
        for (Node node : nodes) {
            node.print();
        }
    }

    @Override
    public String toString() {
        String metaString = " Meta: [ ";
        for (Integer i : meta) {
            metaString += i + " ";
        }
        metaString += "]";
        return "Node[" + depth + "-" + index + "] Nodes: " + nodes.size() + metaString;
    }
}
