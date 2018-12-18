package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class RecipeList {
    public Recipe firstRecipe;
    public Recipe lastRecipe;

    private int runningIndex = 0;
    private int size = 0;


    public Recipe add(int score)
    {
        final int index = runningIndex++;
        final Recipe recipe = new Recipe(score, index);
        if(firstRecipe == null)
        {
            firstRecipe = recipe;
            lastRecipe = recipe;
        }
        else {
            lastRecipe = lastRecipe.append(recipe);
            firstRecipe.prev = lastRecipe;
            lastRecipe.next = firstRecipe;
        }
        size++;
        return recipe;
    }

    public Recipe fromLast(int number)
    {
        Recipe recipe = lastRecipe;
        for(int i = 0; i < number; i++)
        {
            recipe = recipe.prev;
        }
        return recipe;
    }

    public int size()
    {
        return size;
    }

    public void print(Recipe elf1, Recipe elf2) {
        final StringBuilder builder = new StringBuilder();
        Recipe recipe = firstRecipe;
        builder.append(recipe.score);

        while(recipe != lastRecipe)
        {
            recipe = recipe.next;
            if(recipe == elf1)
            {
                builder.append("(" + recipe.score + ")");
            }
            else if(recipe == elf2)
            {
                builder.append("[" + recipe.score + "]");
            }
            else {
                builder.append(" " + recipe.score + " ");
            }
        }

        System.out.println(builder.toString());
    }
}
