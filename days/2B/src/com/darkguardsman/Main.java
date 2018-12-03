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

        //Arg output
        System.out.println("File: " + file);

        System.out.println("\nReading File: ");
        List<String> lines = new ArrayList();
        //Read file
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

        System.out.println("\nProcessing: ");
        boolean found = false;
        for (int i = 0; i < (lines.size() - 1); i++) {
            String lineA = lines.get(i);

            for(int j = i + 1; j < lines.size(); j++) {
                String lineB = lines.get(j);

                String common = handle(lineA, lineB);
                if (common != null) {
                    found = true;

                    System.out.println("Match found: ");
                    System.out.println("\tLine A: " + lineA);
                    System.out.println("\tLine B:" + lineB);
                    System.out.println("\tShared: " + common);
                    break;
                }
            }

            if(found)
            {
                break;
            }
        }

        if (!found) {
            System.out.println("Failed to find 2 coded that are only off by 1 char");
        }

    }

    static String handle(String lineA, String lineB) {
        System.out.println("\nComparing: " + lineA + "--to--" + lineB);
        //Get chars
        final char[] charsA = lineA.toCharArray();
        final char[] charsB = lineB.toCharArray();

        //Check length
        if (charsA.length == charsB.length) {
            int sharePoint = -1;

            //Loop chars looking for point that doesn't match
            for (int i = 0; i < charsA.length; i++) {
                final char a = charsA[i];
                final char b = charsB[i];
                System.out.println("\t" + a + " " + b + " " + (a != b));
                if (a != b) {

                    //If we have more than 1 mismatch point then its invalid
                    if (sharePoint != -1) {
                        System.out.println("\tLines differ by more than 1 char");
                        return null;
                    }
                    sharePoint = i;
                }
            }

            //If we have a single mismatch point return substring without the char
            if (sharePoint != -1) {
                return lineA.substring(0, sharePoint) + lineA.substring(sharePoint + 1);
            }
        }
        return null;
    }


}
