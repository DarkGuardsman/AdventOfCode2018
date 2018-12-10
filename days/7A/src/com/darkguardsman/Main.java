package com.darkguardsman;

import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/7/18.
 */
public class Main {

    public static void main(String... args) {
        //get args
        final File file = new File(args[0]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        final List<String> lines = FileHelpers.getLines(file);

        //Pattern data to regex find start and end points
        final String patternString = "\\s+[A-Z](?=\\s+)";
        final Pattern pattern = Pattern.compile(patternString);

        //Convert lines to paths
        System.out.println("\nConverting lines into paths:");
        final Queue<BuildPath> buildPaths = new LinkedList();
        for (String string : lines) {
            System.out.println("\t" + string);
            final Matcher matcher = pattern.matcher(string);
            String start = null;
            String end = null;

            if (matcher.find()) {
                start = matcher.group();
            }
            if (matcher.find()) {
                end = matcher.group();
            }

            System.out.println("\t\tStart: '" + start + "'  End: '" + end + "'");
            buildPaths.add(new BuildPath(start.trim(), end.trim()));
        }

        //Created nodes for connections
        System.out.println("\nCreating dots:");
        HashMap<Character, BuildNode> nodeMap = new HashMap();
        for(BuildPath path : buildPaths)
        {
            //Create node for start if none
            if(!nodeMap.containsKey(path.start)) {
                nodeMap.put(path.start, new BuildNode(path.start));
            }
            //Create node for end if none
            if (!nodeMap.containsKey(path.end)) {
                nodeMap.put(path.end, new BuildNode(path.end));
            }
        }

        //Output data
        System.out.println("\tDots: " + nodeMap.size());
        for(BuildNode node : nodeMap.values())
        {
            System.out.println("\t" + node.id);
        }

        //Connect all paths to each other
        System.out.println("\nConnecting the dots:");
        while(buildPaths.peek() != null)
        {
            BuildPath path = buildPaths.poll();

            BuildNode startNode = nodeMap.get(path.start);
            BuildNode endNode = nodeMap.get(path.end);

            startNode.addPath(endNode, false);
        }

        //Output data
        for(BuildNode node : nodeMap.values())
        {
            System.out.println("\t" + node);
        }


        //Find start of path

        //Break down connections 1 at a time until all paths are completed
    }
}
