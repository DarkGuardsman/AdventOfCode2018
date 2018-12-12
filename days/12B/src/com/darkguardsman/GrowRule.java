package com.darkguardsman;

import java.util.ArrayList;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/12/18.
 */
public class GrowRule {

    char result;
    char[] match;

    public GrowRule(String input) {

        String[] split = input.split("=>");
        match = split[0].trim().toCharArray();
        result = split[1].trim().charAt(0);

        if (match.length != 5) {
            throw new IllegalArgumentException("Grow rule needs a match string of 5 chars");
        }
    }

    @Override
    public String toString() {
        return match + " => " + result;
    }

    public boolean doesMatch(int pot, ArrayList<Character> pos, ArrayList<Character> neg) {
        return hasMatch(pot - 2, match[0], pos, neg)
                && hasMatch(pot - 1, match[1], pos, neg)
                && hasMatch(pot, match[2], pos, neg)
                && hasMatch(pot + 1, match[3], pos, neg)
                && hasMatch(pot + 2, match[4], pos, neg);
    }

    boolean hasMatch(int index, char expected, ArrayList<Character> pos, ArrayList<Character> neg) {
        char atLocation;
        if (index < 0) {
            index = (-index) - 1;
            if (index < neg.size()) {
                atLocation = neg.get(index);
            } else {
                atLocation = '.';
            }
        } else if (index >= pos.size()) {
            atLocation = '.';
        } else {
            atLocation = pos.get(index);
        }

        return atLocation == expected;
    }

    public char getResult() {
        return result;
    }
}
