package com.darkguardsman;


import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/12/18.
 */
public class Main {
    public static void main(String... args) {
        final File file = new File(args[0]);
        final long generations = Integer.parseInt(args[1]);

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
        ArrayList<Character> prevPotsPos = growNew(null, false);
        ArrayList<Character> prevPotsNeg = growNew(null, true);
        for (char c : input.toCharArray()) {
            prevPotsPos.add(c);
        }

        outputPlantRow("I", prevPotsNeg, prevPotsPos);

        //loop pots
        for (long generation = 0; generation <= generations; generation++) {
            ArrayList<Character> positivePots = growNew(prevPotsPos, false);
            ArrayList<Character> negativePots = growNew(prevPotsNeg, true);

            for (int pot = -prevPotsNeg.size(); pot < prevPotsPos.size(); pot++) {
                List<GrowRule> matches = findMatches(pot, rules, prevPotsPos, prevPotsNeg);
                if (matches.size() > 0) {
                    char result = matches.get(matches.size() - 1).getResult();
                    if (pot >= 0) {
                        positivePots.set(pot, result);
                    } else {
                        negativePots.set(-pot - 1, result);
                    }
                }
            }

            outputPlantRow("" + generation, negativePots, positivePots);
            prevPotsPos = positivePots;
            prevPotsNeg = negativePots;
        }

        System.out.println("\nCalculating: ");
        long sum = 0;
        for (int pot = -prevPotsNeg.size(); pot < prevPotsPos.size(); pot++) {

            char result;
            if (pot >= 0) {
                result = prevPotsPos.get(pot);
            } else {
                result = prevPotsNeg.get(-pot - 1);
            }

            if (result == '#') {
                sum += pot;
            }
        }

        System.out.println("\tAnswer: " + sum);
    }

    static ArrayList<Character> growNew(ArrayList<Character> prevPots, boolean neg) {

        int size = 3;
        if(prevPots != null && prevPots.size() > 0) {
            size = prevPots.size();
            for (int i = 0; i < 5; i++) {

                //Negative handling
                if (neg && (i >= prevPots.size() || (char) prevPots.get(i) == '#')) {
                    size += 5;
                    break;
                }
                //Positive handling
                else if (i >= prevPots.size() || (char) prevPots.get(prevPots.size() - 1 - i) == '#') {
                    size += 5;
                    break;
                }
            }
        }

        final ArrayList<Character> re = new ArrayList(size);

        for (int i = 0; i < size; i++) {
            re.add('.');
        }

        return re;
    }

    static List<GrowRule> findMatches(int pot, List<GrowRule> rules, ArrayList<Character> prevPotsPos, ArrayList<Character> prevPotsNeg) {
        List<GrowRule> matches = new ArrayList();
        for (GrowRule rule : rules) {
            if (rule.doesMatch(pot, prevPotsPos, prevPotsNeg)) {
                matches.add(rule);
            }
        }
        return matches;
    }



    static void outputPlantRow(String generation, List<Character> neg, List<Character> pos) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%4s", generation) + ": ");
        int spacer = 0;
        for (int pot = -neg.size(); pot < pos.size(); pot++) {

            char result;
            if (pot >= 0) {
                result = pos.get(pot);
            } else {
                result = neg.get(-pot - 1);
            }
            builder.append(result);

            if((spacer++ + 1) % 5 == 0)
            {
                builder.append(' ');
            }
        }
        System.out.println(builder.toString());
    }
}
