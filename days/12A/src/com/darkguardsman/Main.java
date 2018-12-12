package com.darkguardsman;


import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/12/18.
 */
public class Main {
    public static void main(String... args) {
        final File file = new File(args[0]);
        final int generations = Integer.parseInt(args[1]);
        final int pots = Integer.parseInt(args[2]);
        final int zeroStart = Integer.parseInt(args[3]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        final List<String> lines = FileHelpers.getLines(file);
        System.out.println("\tLines: " + lines.size());

        System.out.println("\nConverting lines to data: ");

        //Get init state
        final String input = lines.get(0).replace("initial state:", "").trim();
        System.out.println("\tInitial state: " + input);

        //Get rules for growth
        System.out.println("\tRules: ");
        final List<GrowRule> rules = new ArrayList();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.trim().isEmpty()) {
                GrowRule rule = new GrowRule(line);
                rules.add(rule);
                System.out.println("\t\t" + rule);
            }
        }

        System.out.println("\nPlotting data: ");
        //Create init state
        char[] prevGeneration = new char[pots];
        final char[] inputChar = input.toCharArray();
        for (int i = 0; i < prevGeneration.length; i++) {
            if (i >= zeroStart && i < (inputChar.length + zeroStart)) {
                prevGeneration[i] = inputChar[i - zeroStart];
            } else {
                prevGeneration[i] = '.';
            }
        }

        //Print header
        char[] indexHeader = new char[pots];
        Arrays.fill(indexHeader, ' ');
        indexHeader[zeroStart] = '0';
        indexHeader[zeroStart + 10] = 'X';
        outputPlantRow("H", indexHeader);

        //Print start condition
        outputPlantRow("I", prevGeneration);

        //loop pots
        for (int generation = 0; generation < generations; generation++) {
            char[] nextGeneration = new char[pots];
            Arrays.fill(nextGeneration, '.');

            for (int pot = 0; pot < pots; pot++) {
                List<GrowRule> matches = new ArrayList();
                for (GrowRule rule : rules) {
                    if (rule.doesMatch(pot, prevGeneration)) {
                        matches.add(rule);
                    }
                }
                if (matches.size() > 0) {
                    nextGeneration[pot] = matches.get(matches.size() - 1).getResult();
                }
            }

            outputPlantRow("" + generation, nextGeneration);
            prevGeneration = nextGeneration;
        }

        System.out.println("\nCalculating: ");
        int sum = 0;
        for(int i = 0; i < pots; i++)
        {
            int potNumber = i - zeroStart;

            if(prevGeneration[i] == '#')
            {
                sum += potNumber;
            }
        }

        System.out.println("\tAnswer: " + sum);
    }

    static void outputPlantRow(String generation, char[] row) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%4s", generation) + ": ");
        for (int i = 0; i < row.length; i++) {
            builder.append(row[i]);
            if ((i + 1) % 5 == 0) {
                builder.append(" ");
            }
        }
        System.out.println(builder.toString());
    }
}
