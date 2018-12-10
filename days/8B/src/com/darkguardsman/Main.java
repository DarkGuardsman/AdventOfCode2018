package com.darkguardsman;

import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class Main {

    public static void main(String... args) {
        //get args
        final File file = new File(args[0]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        final String line = FileHelpers.getAsString(file);
        String[] split = line.split("\\s");
        Queue<Integer> numbers = new LinkedList();
        for (String s : split) {
            s = s.trim();
            numbers.add(Integer.parseInt(s));
        }

        //Build tree
        Node root = captureNodes(numbers, 0, 0);

        //Debug
        root.print();

        //Count output
        int metaSum = root.sumMeta();
        System.out.println("Sum: " + metaSum);
    }

    static Node captureNodes(Queue<Integer> numbers, int depth, int index) {

        final int nodeCount = numbers.poll();
        final int metaCount = numbers.poll();

        final Node node = new Node(index, depth);
        //Collect nodes
        for (int i = 0; i < nodeCount; i++) {
            node.nodes.add(captureNodes(numbers, depth + 1, i));
        }

        //Capture meta at end of data
        for (int i = 0; i < metaCount; i++) {
            node.meta.add(numbers.poll());
        }

        return node;
    }

    public static String prefixIndent(int depth) {
        if (depth > 0) {
            String s = "\t";
            for (int i = 1; i < depth; i++) {
                s += "\t";
            }
            return s;
        }
        return "";
    }
}
