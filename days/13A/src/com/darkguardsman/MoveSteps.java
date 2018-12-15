package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public enum MoveSteps
{
    LEFT,
    STRAIT,
    RIGHT;

    public MoveSteps next()
    {
        int index = ordinal() + 1;
        if(index >= values().length)
        {
            index = 0;
        }
        return values()[index];
    }
}
