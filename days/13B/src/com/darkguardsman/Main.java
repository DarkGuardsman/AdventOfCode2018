package com.darkguardsman;


import com.darkguardsman.helpers.Direction2D;
import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.FileHelpers;
import com.darkguardsman.helpers.grid.GridChar;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/18.
 */
public class Main
{
    public static char[] CART_DIRECTIONS = {'^', '>', 'v', '<'};

    public static void main(String... args)
    {
        final File file = new File(args[0]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        final List<String> lines = FileHelpers.getLinesNoTrim(file);
        System.out.println("\tLines: " + lines.size());

        System.out.println("\nConverting lines to data: ");

        //get grid size
        final int sizeY = lines.size();
        final int sizeX = lines.get(0).length();

        //Create grid and fill with data
        GridChar grid = new GridChar(sizeX, sizeY);
        grid.fillFromLineData(lines);

        System.out.println("\nFinding carts: ");

        final List<Cart> carts = new ArrayList();

        //Find all carts and remove from grid, replace with tracks
        grid.forEach((g, x, y) -> {
            final char c = g.getData(x, y);
            if (isCart(c))
            {
                final Direction2D direction = getDirection(c);
                final Cart cart = new Cart(x, y, direction);
                carts.add(cart);

                //Replace cart in grid with line
                if (direction == Direction2D.NORTH || direction == Direction2D.SOUTH)
                {
                    g.setData(x, y, '|');
                }
                else
                {
                    g.setData(x, y, '-');
                }
            }
            return false;
        });

        System.out.println("\nMoving Carts: ");

        while (carts.size() > 1)
        {
            //Sort carts
            sortCarts(carts);

            Queue<Cart> cartsToMove = new LinkedList();
            cartsToMove.addAll(carts);

            //Loop carts moving each
            while (cartsToMove.peek() != null)
            {
                final Cart cart = cartsToMove.poll();
                if (carts.contains(cart))
                {
                    //Move cart 1 step
                    moveCart(cart, grid);

                    //Check for collision
                    checkForCollisions(carts);
                }
            }
        }

        System.out.println("\nLast Cart: " + carts.get(0));
    }

    static void moveCart(Cart cart, GridChar grid)
    {
        //Get next position
        final Dot nextPosition = new Dot(cart.x + cart.direction.offsetX, cart.y - cart.direction.offsetY);

        //printCartLocation(cart, grid, 2);

        //Update cart position
        cart.x += cart.direction.offsetX;
        cart.y -= cart.direction.offsetY;

        //Get next rail
        final char nextRail = grid.getData(nextPosition);

        //Update direction based on rail
        if (nextRail == '+')
        {
            if (cart.intersectionStep == MoveSteps.LEFT)
            {
                cart.direction = cart.direction.left();
            }
            else if (cart.intersectionStep == MoveSteps.RIGHT)
            {
                cart.direction = cart.direction.right();
            }
            cart.intersectionStep = cart.intersectionStep.next();
        }
        else if (nextRail == 92) // '\' https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html
        {
            //   --\
            //     |
            if (cart.direction == Direction2D.NORTH)
            {
                cart.direction = Direction2D.WEST;
            }
            else if (cart.direction == Direction2D.EAST)
            {
                cart.direction = Direction2D.SOUTH;
            }
            //    |
            //    \--
            else if (cart.direction == Direction2D.SOUTH)
            {
                cart.direction = Direction2D.EAST;
            }
            else if (cart.direction == Direction2D.WEST)
            {
                cart.direction = Direction2D.NORTH;
            }
            else
            {
                throw new RuntimeException("Invalid direction for \\ " + cart);
            }
        }
        else if (nextRail == '/')
        {
            //     /--
            //     |
            if (cart.direction == Direction2D.NORTH)
            {
                cart.direction = Direction2D.EAST;
            }
            else if (cart.direction == Direction2D.WEST)
            {
                cart.direction = Direction2D.SOUTH;
            }
            //      |
            //    --/
            else if (cart.direction == Direction2D.SOUTH)
            {
                cart.direction = Direction2D.WEST;
            }
            else if (cart.direction == Direction2D.EAST)
            {
                cart.direction = Direction2D.NORTH;
            }
            else
            {
                throw new RuntimeException("Invalid direction for / " + cart);
            }
        }
        else if (nextRail != '-' && nextRail != '|')
        {
            throw new RuntimeException("Invalid rail detected " + cart);
        }
    }

    static void checkForCollisions(List<Cart> carts)
    {
        for (int i = 0; i < carts.size(); i++)
        {
            final Cart cartA = carts.get(i);
            for (int j = i + 1; j < carts.size(); j++)
            {
                final Cart cartB = carts.get(j);

                if (cartA.x == cartB.x && cartA.y == cartB.y)
                {
                    carts.remove(cartA);
                    carts.remove(cartB);
                    return;
                }
            }
        }
    }


    static void sortCarts(List<Cart> carts)
    {
        carts.sort((a, b) -> {
            if (a.y == b.y)
            {
                return Integer.compare(a.x, b.x);
            }
            return Integer.compare(a.y, b.y);
        });
    }

    static Direction2D getDirection(char c)
    {
        for (int i = 0; i < CART_DIRECTIONS.length; i++)
        {
            char cd = CART_DIRECTIONS[i];
            if (cd == c)
            {
                return Direction2D.values()[i];
            }
        }
        return null;
    }

    static boolean isCart(char c)
    {
        return getDirection(c) != null;
    }
}
