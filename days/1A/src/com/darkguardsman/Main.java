package com.darkguardsman;

import java.io.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/1/2018.
 */
public class Main
{
    public static void main(String... args)
    {
        //get args
        File file = new File(args[0]);
        int hz = getNumber(args[1]);

        //Arg output
        System.out.println("File: " + file);
        System.out.println("Hz: " + hz);

        //Read file
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                //Get number form line
                int number = getNumber(line);

                //Add
                int newHz = number + hz;

                //Debug
                System.out.println(hz + " + " +  number + " = " + newHz);

                //Asign
                hz = newHz;
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static int getNumber(String line)
    {
        line = line.trim();
        return Integer.parseInt(line);
    }

}
