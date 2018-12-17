package com.darkguardsman;


import java.util.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/18.
 */
public class Main {
    public static void main(String... args) {
        final String start = args[0].trim(); //37
        final int numberOfRuns = Integer.parseInt(args[1]); //503761

        //Output args
        System.out.println("Start: " + start);
        System.out.println("Runs: " + numberOfRuns);

        System.out.println("\nParsing start args: ");

        //Convert start to data
        int[] recipeArray = new int[]{
                Character.getNumericValue(start.charAt(0)),
                Character.getNumericValue(start.charAt(1))};

        print(recipeArray, 0, 1);

        System.out.println("\nStarting runs: ");

        //Track elf index in recipe array
        int indexFirstElf = 0;
        int indexSecondElf = 1;

        while (recipeArray.length < (numberOfRuns + 11)) {

            //Get new recipe
            int newRecipe = recipeArray[indexFirstElf] + recipeArray[indexSecondElf];

            //Add to map
            recipeArray = addNewRecipe(recipeArray, newRecipe);

            //Get next index for recipe
            indexFirstElf = getNextRecipe(recipeArray, indexFirstElf);
            indexSecondElf = getNextRecipe(recipeArray, indexSecondElf);
            //System.out.println("First Elf: " + indexFirstElf + "=" + recipeArray[indexFirstElf]);
            //System.out.println("Second Elf: " + indexSecondElf + "=" + recipeArray[indexSecondElf]);

            //Debug
            //print(recipeArray, indexFirstElf, indexSecondElf);
        }

        System.out.println("\nResult: ");
        int index = numberOfRuns;
        int[] score = new int[10];
        for(int i = 0; i < 10; i++)
        {
            score[i] = recipeArray[index + i];
        }
        print(score, -1, -1);
    }

    static void print(int[] recipeArray, int indexFirstElf, int indexSecondElf) {
        StringBuilder builder = new StringBuilder(recipeArray.length * 2 + 4);
        for (int i = 0; i < recipeArray.length; i++) {
            if (indexFirstElf == i) {
                builder.append("(" + recipeArray[i] + ")");
            } else if (indexSecondElf == i) {
                builder.append("[" + recipeArray[i] + "]");
            } else {
                builder.append(" " + recipeArray[i] + " ");
            }
        }
        System.out.println(builder.toString().trim());
    }

    static int getNextRecipe(int[] array, int index) {
        return (index + (array[index] + 1)) % array.length;
    }

    static int[] addNewRecipe(int[] array, int recipe) {
        if (recipe >= 10) {
            String r = "" + recipe;
            int[] numbers = new int[r.length()];
            for (int i = 0; i < r.length(); i++) {
                numbers[i] = Character.getNumericValue(r.charAt(i));
            }
            return addRecipes(array, numbers);
        } else {
            return addRecipes(array, recipe);
        }
    }

    /**
     * Resizes the array and adds the numbers to the end
     *
     * @param array   - orginal array
     * @param numbers - numbers to add to ened
     * @return new array
     */
    static int[] addRecipes(int[] array, int... numbers) {
        array = Arrays.copyOf(array, array.length + numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            array[array.length - numbers.length + i] = numbers[i];
        }
        return array;
    }
}
