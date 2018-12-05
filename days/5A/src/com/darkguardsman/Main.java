package com.darkguardsman;

import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/5/18.
 */
public class Main {

    public static void main(String... args) {
        //get args
        final File file = new File(args[0]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        final String polymerCode = FileHelpers.getAsString(file);
        if (polymerCode == null) {
            System.out.println("Failed to read file as a single line polymer");
            System.exit(-1);
        }
        System.out.println("\n" + polymerCode + "\n\n");

        int run = 0;
        String prev;
        String polymer = polymerCode;
        do {
            //Debug
            System.out.println("Processing Collisions:   run #" + run);

            //Handle
            prev = polymer;
            polymer = processCollisions(polymer);

            if(polymer != null) {
                //Debug
                System.out.println("Collision reduction of " + (prev.length() - polymer.length()));
                run++;
            }
        }
        while (polymer != null);

        //Output
        System.out.println("Left over: " + prev);
        System.out.println("Length: " + prev.length());

    }

    static String processCollisions(final String polymer) {
        final String edit = polymer;

        char[] chars = polymer.toCharArray();
        for(int i = 0; i < (chars.length - 1); i++)
        {
            char a = chars[i];
            char b = chars[i + 1];

            if(a != b && Character.toLowerCase(a) == Character.toLowerCase(b))
            {
                return edit.substring(0, i) + edit.substring(i + 2, edit.length());
            }
        }

        //If no edit, return null to exist loop
        return null;
    }
}
