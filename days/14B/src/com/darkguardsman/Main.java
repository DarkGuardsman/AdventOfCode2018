package com.darkguardsman;


/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/18.
 */
public class Main {
    public static void main(String... args) {
        final String startString = args[0].trim(); //37
        final String matchString = args[1]; //503761

        //Output args
        System.out.println("Start: " + startString);
        System.out.println("Match: " + matchString);

        System.out.println("\nConverting to linked list: ");
        final RecipeList recipeList = new RecipeList();
        addStringRecipe(startString, recipeList);

        //Data to match against
        final int[] matchArray = matchArray(matchString);

        System.out.println("\nStarting runs: ");

        //Track elf index in recipe array
        Recipe firstElf = recipeList.firstRecipe;
        Recipe secondElf = recipeList.firstRecipe.next;

        int foundMatch = -1;

        while (foundMatch == -1) {

            //Get new recipe
            int newRecipe = firstElf.score + secondElf.score;

            //Add to map
            addRecipe(recipeList, newRecipe);

            //Get next index for recipe
            firstElf = getNextRecipe(firstElf);
            secondElf = getNextRecipe(secondElf);

            foundMatch = doesMatch(matchArray, recipeList);

            //recipeList.print(firstElf, secondElf);
        }

        System.out.println("\nRecipes Before Match: " + foundMatch);
    }

    static int[] matchArray(String match) {
        match = match.trim();
        int[] numbers = new int[match.length()];
        char[] chars = match.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            numbers[i] = Character.getNumericValue(chars[i]);
        }
        return numbers;
    }

    static void addStringRecipe(String scores, RecipeList recipeList) {
        final char[] chars = scores.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            recipeList.add(Character.getNumericValue(chars[i]));
        }
    }

    static void addRecipe(RecipeList list, int score) {
        if (score >= 10) {
            addStringRecipe("" + score, list);
        } else {
            list.add(score);
        }
    }

    static int doesMatch(int[] array, RecipeList list) {
        if (list.size() <= array.length) {
            return -1;
        }
        for (int i = 0; i < 2; i++) {
            Recipe recipe = list.fromLast(6 - i);
            final int index = recipe.index;
            for (int j = 0; j < array.length; j++) {
                if (recipe.score != array[j]) {
                    return -1;
                }
                recipe = recipe.next;
            }
            return index;
        }
        return -1;
    }

    static Recipe getNextRecipe(Recipe recipe) {
        int score = recipe.score;
        int steps = score  + 1;
        while (steps-- > 0)
        {
            recipe = recipe.next;
        }
        return recipe;
    }
}
