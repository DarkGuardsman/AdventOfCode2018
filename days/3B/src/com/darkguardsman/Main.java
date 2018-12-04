package com.darkguardsman;

import com.darkguardsman.helpers.FileHelpers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/3/18.
 */
public class Main {

    public static void main(String... args) {
        //get args
        final File file = new File(args[0]);
        final int width = Integer.parseInt(args[1]);
        final int height = Integer.parseInt(args[2]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        List<String> lines = FileHelpers.getLines(file);
        if (lines == null) {
            System.exit(-1);
        }

        //Convert strings to objects
        System.out.println("\nConverting: ");
        List<Claim> claims = new ArrayList(lines.size());
        for (String line : lines) {
            claims.add(new Claim(line));
        }

        //Map objects to space
        System.out.println("\nMapping: ");
        ClaimArea area = new ClaimArea(width, height);
        for (Claim claim : claims) {
            area.map(claim);
        }


        //Read space for overlap
        System.out.println("\nProcessing: ");
        List<Claim> filterResults = new ArrayList();

        for (Claim claim : claims) {
            if (!claim.overlaps) {
                filterResults.add(claim);
            }
        }

        if (filterResults.size() > 1) {
            System.out.println("Seems there are more 1 claim that do not overlap");
        } else if (filterResults.size() == 0) {
            System.out.println("No claims found that do not overlap");
        } else {
            System.out.println("Non-overlap claim: " + filterResults.get(0));
        }
    }

}
