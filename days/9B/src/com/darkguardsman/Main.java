package com.darkguardsman;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class Main {

    public static final int REMOVE_STEPS = 7;
    public static final int SCORE_NUMBER = 23;

    public static void main(String... args) {
        final int numberOfPlayers = Integer.parseInt(args[0]);
        final long lastMarbleWorth = Long.parseLong(args[1]);

        System.out.println("Players: " + numberOfPlayers);

        //Init
        System.out.println("[-] (0)");

        //Player scores
        Map<Integer, Long> scoreBoard = new TreeMap();

        //Tracking data
        Marble startMarble = new Marble(0);
        Marble currentMarble = startMarble;
        int nextMarble = 1;
        int currentPlayer = 0;

        //Loop until complete
        while (nextMarble <= lastMarbleWorth) {

            //If multiple score
            if (isMultiple(nextMarble)) {

                System.out.println("Multiple: " + nextMarble);

                //get marble value
                long value = nextMarble;
                Marble removed = removeMarble(currentMarble);
                System.out.println("Removed: " + removed.number);
                value += removed.number;

                //Make sure we know were the start is
                if (removed == startMarble) {
                    startMarble = removed.next();
                }
                currentMarble = removed.next();

                //Increase player score
                long prevScore = 0;
                if (scoreBoard.containsKey(currentPlayer)) {
                    prevScore = scoreBoard.get(currentPlayer);
                }
                scoreBoard.put(currentPlayer, prevScore + value);

                System.out.println("Player[" + currentPlayer + "] " + prevScore + " + " + value + " = " + scoreBoard.get(currentPlayer));
            } else {

                //Add marble
                currentMarble = addMarble(currentMarble, nextMarble);
                //printRound(currentPlayer, currentMarble, startMarble);
            }

            //Increment marble count
            nextMarble++;

            //Increment player
            currentPlayer++;
            if (currentPlayer >= numberOfPlayers) {
                currentPlayer = 0;
            }
        }

        //Get highest score
        long bestPoints = 0;
        int bestPlayer = 0;
        for (Map.Entry<Integer, Long> entry : scoreBoard.entrySet()) {
            if (entry.getValue() > bestPoints) {
                bestPlayer = entry.getKey();
                bestPoints = entry.getValue();
            }
        }

        System.out.println("Highest Score: " + bestPoints);
        System.out.println("Player: " + bestPlayer);
    }

    static void printRound(int player, Marble current, Marble start) {
        String output = "[" + player + "] ";
        Marble index = start;
        do {
            if (index == current) {
                output += " (" + index.number + ")";
            } else {
                output += " " + index.number;
            }
            index = index.next();
        } while (start != index);

        System.out.println(output);
    }

    static Marble addMarble(Marble current, int marbleNumber) {
        Marble marble = new Marble(marbleNumber);
        current.next().insertAfter(marble);
        return marble;
    }

    static Marble removeMarble(Marble current) {

        int steps = REMOVE_STEPS;

        //Find the marble backwards through the linked list
        Marble selected = current;
        while (steps > 0) {
            selected = selected.prev();
            steps--;

        }
        selected.remove();
        return selected;
    }

    static boolean isMultiple(int number) {
        return number % SCORE_NUMBER == 0;
    }
}
