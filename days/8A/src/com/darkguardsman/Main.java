package com.darkguardsman;

import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        for(String s : split)
        {
            s = s.trim();
            numbers.add(Integer.parseInt(s));
        }

        Node root = captureNodes(numbers);

        int metaSum = root.sumMeta();
        System.out.println("Sum: " + metaSum);
    }

    static Node captureNodes(Queue<Integer> numbers)
    {
        final int nodeCount = numbers.poll();
        final int metaCount = numbers.poll();

        final Node node = new Node();

        //Collect nodes
        for(int i = 0; i < nodeCount; i++)
        {
            node.nodes.add(captureNodes(numbers));
        }

        //Capture meta at end of data
        for(int i = 0; i < metaCount; i++)
        {
            node.meta.add(numbers.poll());
        }
        return node;
    }
}
