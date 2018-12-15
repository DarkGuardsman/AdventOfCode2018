package com.darkguardsman;

import com.darkguardsman.helpers.Direction2D;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public class Cart
{
    private static int CART_INDEX = 0;
    public final int index;
    public int x;
    public int y;
    public Direction2D direction;

    public MoveSteps intersectionStep = MoveSteps.LEFT;

    public Cart(int x, int y, Direction2D direction)
    {
        this.index = CART_INDEX++;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public String toString()
    {
        return "Cart[" + index + "| " + x + ", " + y + " | " + direction + "]";
    }
}
