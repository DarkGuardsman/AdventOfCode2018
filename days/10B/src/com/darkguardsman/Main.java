package com.darkguardsman;


import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/18.
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
        }

        //I Decided to try my hand at doing some math for this part
        //  https://blog.jle.im/entry/shifting-the-stars.html#.XA8NIfahQvQ.twitter
        //  Yes I know not very sportsman like using someone's answer for the problem...
        //  However, my method was off by % and took hours of runtime to get to that answer.
        //   Saw this on twitter, decided to give it a read and loved the method being used.
        //   It was fast and reusable... lacked the brute force method of my part 1 solution
        //  Plus this is a good chance to refresh/learn some math for future problems :)
        //  After all the purpose of these challenges is to learn. Honestly, I could use
        //   A good refresher on matrix math... and just math in general. As that is one
        //   of the problems I have when working with complex problems is finding a simple
        //   mathematical solution that could save performance.


        //Step 1: get the mean
        System.out.println("\nCalculating mean(s) of data:");
        final double meanX = getMean(points, p -> p.x); //Part 1 I should have used mean to make drawing the data easier
        final double meanY = getMean(points, p -> p.y);

        final double meanVX = getMean(points, p -> p.vx);
        final double meanVY = getMean(points, p -> p.vy);

        System.out.println("\tMeanX: " + meanX);
        System.out.println("\tMeanY: " + meanY);
        System.out.println("\tMeanVX: " + meanVX);
        System.out.println("\tMeanVY: " + meanVY);

        //Step 2: apply the mean
        System.out.println("\nOffsetting data by mean:");
        points.forEach(point ->
        {
            point.x -= meanX;
            point.y -= meanY;

            point.vx -= meanVX;
            point.vy -= meanVY;
        });
        System.out.println("\tDone");

        //Step 3: calculate variance
        System.out.println("\nCalculating variance:");
        double top = sumOf(points, p -> p.x * p.vx + p.y * p.vy);
        double bot = sumOf(points, p -> p.vx * p.vx + p.vy * p.vy);

        System.out.println("\tTop: " + top);
        System.out.println("\tBot: " + bot);

        double answer = top / bot;

        System.out.println("\nAnswer: " + -answer);
        System.out.println("Rounded: " + (int) Math.round(-answer));

    }

    static double sumOf(List<LightPoint> points, Function<LightPoint, Double> getFunction) {
        double sum = getFunction.apply(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            sum += getFunction.apply(points.get(i));
        }
        return sum;
    }

    static double getMean(List<LightPoint> points, Function<LightPoint, Double> getFunction) {
        double sum = sumOf(points, getFunction);
        return sum / (double) points.size();
    }

    static void outputMessage(List<LightPoint> points, long minX, long minY, long maxX, long maxY, int area) {

        final StringBuilder builder = new StringBuilder(area);

        //Line
        for (int i = 0; i < (maxX - minX); i++) {
            builder.append("-");
        }
        builder.append("\n");

        //Loop through points
        for (long y = minY; y < maxY; y++) {
            for (long x = minX; x < maxX; x++) {


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
        for (int i = 0; i < (maxX - minX); i++) {
            builder.append("-");
        }
        builder.append("\n");

        //Output
        System.out.print(builder.toString());
    }

    static boolean isPointAt(List<LightPoint> points, final long x, final long y) {
        return points.stream().anyMatch(point -> point.isAt(x, y));
    }
}
