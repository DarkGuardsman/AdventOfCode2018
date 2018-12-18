package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class Recipe
{
    Recipe next;
    Recipe prev;

    public final int score;
    public final int index;

    public Recipe(int score, int index) {
        this.score = score;
        this.index = index;
        next = this;
        prev = this;
    }

    public Recipe append(Recipe recipe)
    {
        next = recipe;
        recipe.prev = this;
        return recipe;
    }
}
