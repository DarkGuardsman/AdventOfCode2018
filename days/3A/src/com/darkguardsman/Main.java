package com.darkguardsman;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        List<String> lines = new ArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
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
        int overlapCells = area.countCells((c) -> c != null && c.size() > 1);
        System.out.println(overlapCells + " inches of area overlap");

    }

}
