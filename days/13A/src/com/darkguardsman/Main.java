package com.darkguardsman;


import com.darkguardsman.helpers.Direction2D;
import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.FileHelpers;
import com.darkguardsman.helpers.grid.GridChar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        //Output for debug
        System.out.println();
        System.out.println();
        grid.print();
        System.out.println();
        System.out.println();

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

                //Debug
                System.out.println(cart);
                System.out.println("\t " + g.getDataIfGrid(x, y - 1) + " ");
                System.out.println("\t" + g.getDataIfGrid(x - 1, y) + c + g.getDataIfGrid(x + 1, y));
                System.out.println("\t " + g.getDataIfGrid(x, y + 1) + " \n");

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
        Dot firstCollision = null;

        while (firstCollision == null)
        {
            //Sort carts
            sortCarts(carts);

            //Loop carts moving each
            for (Cart cart : carts)
            {
                //Move cart 1 step
                moveCart(cart, grid);

                //Check for collision
                Dot collisionPoint = checkForCollisions(carts);
                if (collisionPoint != null)
                {
                    firstCollision = collisionPoint;
                    break;
                }
            }

            System.out.println();
            grid.print((x, y) -> {
                for (Cart cart : carts)
                {
                    if (cart.x == x && cart.y == y)
                    {
                        return CART_DIRECTIONS[cart.direction.ordinal()];
                    }
                }
                return null;
            });
        }

        System.out.println("\nCollision: " + firstCollision);
    }

    static void printCartLocation(Cart cart, GridChar g, int size)
    {
        int cartX = cart.x;
        int cartY = cart.y;

        System.out.println("Cart: " + cart);
        for (int y = -size; y <= size; y++)
        {
            System.out.print("\t\t");
            for (int x = -size; x <= size; x++)
            {
                int xx = cartX + x;
                int yy = cartY + y;
                if (xx == cartX && yy == cartY)
                {
                    System.out.print(CART_DIRECTIONS[cart.direction.ordinal()]);
                }
                else
                {
                    char c = g.getDataIfGrid(xx, yy);
                    if (c == ' ')
                    {
                        c = '#';
                    }
                    System.out.print(c);
                }
            }
            System.out.println();
        }
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
            System.out.println(cart + " hit curve \\");
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
            System.out.println(cart + " hit curve /");
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
        else if(nextRail != '-' && nextRail != '|')
        {
            throw new RuntimeException("Invalid rail detected " + cart);
        }
    }

    static Dot checkForCollisions(List<Cart> carts)
    {
        for (int i = 0; i < carts.size(); i++)
        {
            final Cart cartA = carts.get(i);
            for (int j = i + 1; j < carts.size(); j++)
            {
                final Cart cartB = carts.get(j);

                if (cartA.x == cartB.x && cartA.y == cartB.y)
                {
                    return new Dot(cartA.x, cartA.y);
                }
            }
        }
        return null;
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
