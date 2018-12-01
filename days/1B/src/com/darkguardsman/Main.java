package com.darkguardsman;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/1/2018.
 */
public class Main
{
    public static void main(String... args)
    {
        //get args
        final File file = new File(args[0]);
        final int startHz = getNumber(args[1]);

        //Arg output
        System.out.println("File: " + file);
        System.out.println("Hz: " + startHz);


        System.out.println("\nReading File: ");
        List<Integer> inputNumbers = new ArrayList();

        //Read file
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                inputNumbers.add(getNumber(line));
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
        boolean foundMatch = false;
        int index = 0;
        int currentHz = startHz;

        Set<Integer> hzSet = new HashSet();
        while (!foundMatch)
        {
            int number = inputNumbers.get(index);
            int nextHz = currentHz + number;
            System.out.println(currentHz + " + " + number + " = " +  nextHz);

            //Check if nextHz is in set
            if(hzSet.contains(nextHz))
            {
                System.out.println("\n\nFound match: " + nextHz);
                foundMatch = true;
            }

            //Add to set
            hzSet.add(nextHz);

            //Assign
            currentHz = nextHz;

            //Loop to next number, move to start if end
            index++;
            if(index >= inputNumbers.size())
            {
                index = 0;
            }
        }
    }

    private static int getNumber(String line)
    {
        line = line.trim();
        return Integer.parseInt(line);
    }

}
