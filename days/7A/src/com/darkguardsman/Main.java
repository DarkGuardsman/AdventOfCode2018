package com.darkguardsman;

import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        for (BuildPath path : buildPaths) {
            //Create node for start if none
            if (!nodeMap.containsKey(path.start)) {
                nodeMap.put(path.start, new BuildNode(path.start));
            }
            //Create node for end if none
            if (!nodeMap.containsKey(path.end)) {
                nodeMap.put(path.end, new BuildNode(path.end));
            }
        }

        //Output data
        System.out.println("\tDots: " + nodeMap.size());
        for (BuildNode node : nodeMap.values()) {
            System.out.println("\t" + node.id);
        }

        //Connect all paths to each other
        System.out.println("\nConnecting the dots:");
        while (buildPaths.peek() != null) {
            BuildPath path = buildPaths.poll();

            BuildNode startNode = nodeMap.get(path.start);
            BuildNode endNode = nodeMap.get(path.end);

            startNode.addPath(endNode);
        }

        //Output data
        for (BuildNode node : nodeMap.values()) {
            System.out.println("\t" + node);
        }

        //Path dots find next step
        System.out.println("\nPathing dots:");
        final Queue<Character> orderOfCompletion = new LinkedList();
        List<BuildNode> nodes = nodeMap.values().stream().collect(Collectors.toList());
        while (!nodes.isEmpty()) {
            char c = findNextStep(nodes);
            orderOfCompletion.add(c);
            System.out.println(c);
        }

        //output result
        System.out.println("\nResult:");
        String result = "";
        for (Character c : orderOfCompletion) {
            result += c;
        }
        System.out.println("\t" + result);

        //Break down connections 1 at a time until all paths are completed
    }

    static char findNextStep(List<BuildNode> nodes) {
        System.out.println("\t\tFinding next Node: " + nodes.size());
        //If we only have 1 node left
        if (nodes.size() == 1) {
            BuildNode node = nodes.get(0);
            nodes.remove(node);
            return node.id;
        }

        //Find all nodes with no requirement steps left
        List<BuildNode> possibleNodes = new ArrayList();
        for (BuildNode node : nodes) {
            if (node.prevNodes.isEmpty()) {
                System.out.println("\t\t\tNext: " + node);
                possibleNodes.add(node);
            }
        }

        BuildNode returnNode = null;
        if (possibleNodes.size() == 1) {
            returnNode = possibleNodes.get(0);
        }
        else if(possibleNodes.size() == 0)
        {
            for(BuildNode node : nodes)
            {
                System.out.println(node);
            }
            throw new RuntimeException("Failed to find dots with no input paths");
        }
        else {

            //Find lowest value (A goes before B)
            returnNode = possibleNodes.get(0);
            for (int i = 1; i < possibleNodes.size(); i++) {
                BuildNode n = possibleNodes.get(i);
                if (returnNode.id > n.id) {
                    returnNode = n;
                }
            }
        }

        if (returnNode != null) {
            //Clear paths
            for (BuildNode n : returnNode.nextNodes) {
                n.prevNodes.remove(returnNode);
            }
            nodes.remove(returnNode);
        }
        return returnNode.id;
    }
}
