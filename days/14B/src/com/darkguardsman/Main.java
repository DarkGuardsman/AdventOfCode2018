package com.darkguardsman;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/18.
 */
public class Main {
    public static void main(String... args) {
        final String start = args[0].trim(); //37
        final String match = args[1]; //503761

        //Output args
        System.out.println("Start: " + start);
        System.out.println("Match: " + match);

        System.out.println("\nStarting runs: ");

        //Track elf index in recipe array
        int indexFirstElf = 0;
        int indexSecondElf = 1;

        int foundMatch = -1;

        String recipe = start;

        while (foundMatch == -1){

            //Get new recipe
            int newRecipe = Character.getNumericValue(recipe.charAt(indexFirstElf)) + Character.getNumericValue(recipe.charAt(indexSecondElf));

            //Add to map
            recipe += "" + newRecipe;

            //Get next index for recipe
            indexFirstElf = getNextRecipe(recipe, indexFirstElf);
            indexSecondElf = getNextRecipe(recipe, indexSecondElf);

            foundMatch = doesMatch(match, recipe);
        }

        System.out.println("\nRecipes Before Match: " + foundMatch);
    }

    static int doesMatch(String match, String recipe)
    {
        return recipe.indexOf(match);
    }

    static int getNextRecipe(String recipe, int index) {
        int value = Character.getNumericValue(recipe.charAt(index));
        return (index + (value + 1)) % recipe.length();
    }
}
