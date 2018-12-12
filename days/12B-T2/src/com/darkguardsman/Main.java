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
    public static void main(String... args) throws InterruptedException {
        final File file = new File(args[0]);
        final long generations = Long.parseLong(args[1]);
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

        long lastSumDelta = 0;
        long lastSum = 0;
        int repeatCount = 0;
        long lastRepeatIndex = 0;

        //loop pots
        for (long generation = 0; generation <= generations; generation++) {

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

            long sum = getSum(pots, zeroStart, nextGeneration);
            long sumDelta = sum - lastSum;
            if(sumDelta == lastSumDelta)
            {
                repeatCount++;
                lastRepeatIndex = generation;
                lastSum = sum;
            }
            else
            {
                repeatCount = 0;
            }

            if(repeatCount > 5)
            {
                break;
            }

            //Save current as last
            lastSum = sum;
            lastSumDelta = sumDelta;
            prevGeneration = nextGeneration;

            //Output for debug
            outputPlantRow("" + generation, nextGeneration);
            System.out.println("\tSum: " + sum);
            System.out.println("\tDelta: " + sumDelta);
        }

        long answer = (generations - (lastRepeatIndex + 1)) * lastSumDelta + lastSum;

        System.out.println("\nCalculating: ");
        System.out.println("\tAnswer: " + answer);
        System.out.println("\tAnswer: " + String.format("%,.0f", (double)answer));
    }

    static long getSum(int pots, int zeroStart, char[] prevGeneration)
    {
        long sum = 0;
        for(int i = 0; i < pots; i++)
        {
            int potNumber = i - zeroStart;

            if(prevGeneration[i] == '#')
            {
                sum += potNumber;
            }
        }
        return sum;
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
