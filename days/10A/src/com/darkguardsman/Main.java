package com.darkguardsman;


import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class Main {
    public static void main(String... args) {
        final File file = new File(args[0]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        final List<String> lines = FileHelpers.getLines(file);
        System.out.println("\tLines: " + lines.size());

        System.out.println("\nConverting to objects: ");
        List<LightPoint> points = new ArrayList();
        for (String line : lines) {
            int index = line.indexOf("<");
            int index2 = line.indexOf(">");

            //Parse out pos data
            String pos = line.substring(index + 1, index2);
            line = line.substring(index2 + 1);

            //Parse out vel data
            index = line.indexOf("<");
            index2 = line.indexOf(">");

            String vel = line.substring(index + 1, index2);

            //Parse pos data
            String[] split = pos.split(",");
            int x = Integer.parseInt(split[0].trim());
            int y = Integer.parseInt(split[1].trim());

            //Parse pos data
            split = vel.split(",");
            int vx = Integer.parseInt(split[0].trim());
            int vy = Integer.parseInt(split[1].trim());

            LightPoint point = new LightPoint();
            point.x = x;
            point.y = y;
            point.vx = vx;
            point.vy = vy;

            points.add(point);

            System.out.println(point);
        }

        System.out.println("\nConverting to objects: ");
        Scanner sc = new Scanner(System.in);
        int run = 0;
        while (true) {
            //Ask user if they want to quit
            System.out.println("Continue? Q to quit or integer stop size...");
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("q")) {
                break;
            }
            int stopArea = Integer.parseInt(line.trim());

            //Spacer
            System.out.println();

            while(true) {
                //Count run
                System.out.println("Run: " + run);

                //Move
                points.forEach(point -> point.move());

                //Calculate draw bound for data
                int minX = points.stream().min(Comparator.comparingInt(p -> p.x)).get().x;
                int maxX = points.stream().max(Comparator.comparingInt(p -> p.x)).get().x;

                int minY = points.stream().min(Comparator.comparingInt(p -> p.y)).get().y;
                int maxY = points.stream().max(Comparator.comparingInt(p -> p.y)).get().y;

                int sizeX = (maxX - minX);
                int sizeY = (maxY - minY);
                int area = sizeX * sizeY;

                System.out.print(sizeX + "x" + sizeY + "=" + area);

                if (area < 0 || area >= points.size() * points.size()) {
                    System.out.print("area is too large for any usable data");
                } else if(area <= stopArea * stopArea) {

                    //Spacer
                    System.out.println();

                    //Output
                    outputMessage(points, minX, minY, maxX, maxY, area);
                    break;
                }
            }

            //Increase run
            run++;
        }
    }

    static void outputMessage(List<LightPoint> points, int minX, int minY, int maxX, int maxY, int area) {

        final StringBuilder builder = new StringBuilder(area);

        //Line
        for(int i = 0; i < (maxX - minX); i++)
        {
            builder.append("-");
        }
        builder.append("\n");

        //Loop through points
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {


                //If point out print #
                if (isPointAt(points, x, y)) {
                    builder.append("#");
                }
                //Not print spacer
                else {
                    builder.append(".");
                }
            }
            //Next line since we are print one char at a time above
            builder.append("\n");
        }

        //Line
        for(int i = 0; i < (maxX - minX); i++)
        {
            builder.append("-");
        }
        builder.append("\n");

        //Output
        System.out.print(builder.toString());
    }

    static boolean isPointAt(List<LightPoint> points, final int x, final int y) {
        return points.stream().anyMatch(point -> point.isAt(x, y));
    }
}
