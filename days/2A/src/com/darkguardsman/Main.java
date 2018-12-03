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

    public static void main(String... args)
    {
        //get args
        final File file = new File(args[0]);

        //Arg output
        System.out.println("File: " + file);

        System.out.println("\nReading File: ");
        List<String> lines = new ArrayList();
        //Read file
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                lines.add(line.trim());
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("\nProcessing: ");
        int count2 = 0;
        int count3 = 0;
        for(String line : lines)
        {
            char[] chars = line.toCharArray();
            HashMap<Character, Integer> map = new HashMap();
            for(char c : chars)
            {
                if(map.containsKey(c))
                {
                    map.put(c, map.get(c) + 1);
                }
                else
                {
                    map.put(c, 1);
                }
            }

            if(map.entrySet().stream().anyMatch(e -> e.getValue() == 3))
            {
                count3++;
            }
            if(map.entrySet().stream().anyMatch(e -> e.getValue() == 2))
            {
                count2++;
            }
        }

        System.out.println("Found " + count2 + " entries with 2 letters");
        System.out.println("Found " + count3 + " entries with 3 letters");
        int checksum = count2 * count3;
        System.out.println("Checksum: " + checksum);
    }


}
