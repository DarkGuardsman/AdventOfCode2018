package com.darkguardsman;

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

    public boolean doesMatch(int pot, char[] prevGeneration) {
        return hasMatch(pot - 2, match[0], prevGeneration)
                && hasMatch(pot - 1, match[1], prevGeneration)
                && hasMatch(pot, match[2], prevGeneration)
                && hasMatch(pot + 1, match[3], prevGeneration)
                && hasMatch(pot + 2, match[4], prevGeneration);
    }

    boolean hasMatch(int index, char expected, char[] array) {
        char atLocation = 0;
        if (index < 0) {
            atLocation = array[array.length + index];
        } else if (index >= array.length) {
            atLocation = array[index - array.length];
        } else {
            atLocation = array[index];
        }

        return atLocation == expected;
    }

    public char getResult() {
        return result;
    }
}
