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
        final int helpers = Integer.parseInt(args[1]);
        final int defaultWorkTime = Integer.parseInt(args[2]);

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

        System.out.println("\nWorking steps:");

        //Get all steps
        List<BuildNode> nodes = nodeMap.values().stream().collect(Collectors.toList());

        //Track work by task & time arrays
        BuildNode[] helperTasks = new BuildNode[helpers]; //Tasks currently running
        int[] helperTimer = new int[helpers]; //Time left on task

        //Track number of works for time loop
        int workdersWorking = 0;

        //Track current tasks so we do not assign a task twice
        List<BuildNode> currentTasks = new ArrayList();

        int totalTime = 0;
        while (!nodes.isEmpty() || workdersWorking > 0) {

            //Output data start
            outputTaskData(totalTime, helperTasks, helperTimer);

            //Reset worker count
            workdersWorking = 0;

            //Loop workers
            for (int i = 0; i < helpers; i++) {

                //If no task find one
                if (helperTasks[i] == null) {
                    tryToGetStep(i, helperTasks, helperTimer, nodes, currentTasks, defaultWorkTime);
                }
            }

            //Loop workers
            for (int i = 0; i < helpers; i++) {

                //Check if has task
                if (helperTasks[i] != null) {
                    //Count down task
                    helperTimer[i] = helperTimer[i] - 1;

                    //Clear task if timer is 0
                    if (helperTimer[i] <= 0) {

                        //Mark as completed
                        completeStep(helperTasks[i], nodes, currentTasks);
                        helperTasks[i] = null;
                    }
                }

                //Count workers working
                if (helperTasks[i] != null) {
                    workdersWorking++;
                }
            }

            //Output data end
            outputTaskData(totalTime, helperTasks, helperTimer);
            System.out.println("-----------------------------------");

            //Increase time
            totalTime++;
        }

        //Output total time
        System.out.println("\nTotalTime: " + totalTime);
    }

    static void outputTaskData(int totalTime,  BuildNode[] helperTasks, int[] helperTimer)
    {
        //Generate output for debug
        String output = "\t" + totalTime + ": ";
        for (int i = 0; i < helperTasks.length; i++) {
            output += "[";
            output += helperTasks[i] == null ? "#" : helperTasks[i].id;
            output += " ";
            output += helperTimer[i];
            output += "]";
        }
        System.out.println(output);
    }

    static void tryToGetStep(int index,
                             BuildNode[] helperTasks, int[] helperTimer,
                             List<BuildNode> nodes, List<BuildNode> currentTasks,
                             int workTime) {

        //Try to find a step, this can be null often
        helperTasks[index] = findNextStep(nodes, currentTasks);

        //If task was found make sure to mark it as current
        if (helperTasks[index] != null) {
            currentTasks.add(helperTasks[index]);

            //Calculate work time
            helperTimer[index] = workTime + letterToIndex(helperTasks[index].id);
            System.out.println("\t Task assigned: " + helperTasks[index].id + " for " + helperTimer[index]);
        }
    }

    static void completeStep(BuildNode node, List<BuildNode> nodes, List<BuildNode> currentTasks) {

        //Remove from current
        currentTasks.remove(node);

        //Remove from nodes list
        nodes.remove(node);

        //Clear step so more nodes will be ready for crafting
        for (BuildNode n : node.nextNodes) {
            n.prevNodes.remove(node);
        }
    }

    static BuildNode findNextStep(List<BuildNode> nodes, List<BuildNode> currentTasks) {
        System.out.println("\t\tFinding next Node: " + nodes.size());

        //Find all nodes we can use
        List<BuildNode> possibleNodes = new ArrayList();
        for (BuildNode node : nodes) {

            //Only get tasks that have no prev steps and are not already in progress
            if (node.prevNodes.isEmpty() && !currentTasks.contains(node)) {
                System.out.println("\t\t\tPossible: " + node);
                possibleNodes.add(node);
            }
        }

        BuildNode returnNode = null;

        //If single node return
        if (possibleNodes.size() == 1) {
            returnNode = possibleNodes.get(0);
        }
        //if several nodes find best
        else if (!possibleNodes.isEmpty()) {

            //Find lowest value (A goes before B)
            returnNode = possibleNodes.get(0);
            for (int i = 1; i < possibleNodes.size(); i++) {
                BuildNode n = possibleNodes.get(i);
                if (returnNode.id > n.id) {
                    returnNode = n;
                }
            }
        }
        System.out.println("\t\t\tReturn: " + returnNode);
        return returnNode;
    }

    static int letterToIndex(char letter) {
        char lower = Character.toLowerCase(letter);
        return lower - 'a' + 1;
    }
}
