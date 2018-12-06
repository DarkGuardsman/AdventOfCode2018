package com.darkguardsman;

import com.darkguardsman.helpers.Direction2D;
import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.*;

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
        final List<String> lines = FileHelpers.getLines(file);

        //Read file
        System.out.println("\nProcess File: ");
        final List<DotSource> dotSources = new ArrayList();
        int id = 0;
        for (String string : lines) {

            //Split
            String[] split = string.split(",");

            //Parse
            int x = Integer.parseInt(split[0].trim());
            int y = Integer.parseInt(split[1].trim());

            //Create
            DotSource dot = new DotSource(x, y, id++);
            dotSources.add(dot);

            //Debug
            System.out.println(dot);
        }

        //Calculate map size
        final int minX = dotSources.stream().min(Comparator.comparingInt(dot -> dot.x)).get().x;
        final int maxX = dotSources.stream().max(Comparator.comparingInt(dot -> dot.x)).get().x;
        final int minY = dotSources.stream().min(Comparator.comparingInt(dot -> dot.y)).get().y;
        final int maxY = dotSources.stream().max(Comparator.comparingInt(dot -> dot.y)).get().y;

        final GridDataMap gridDataMap = GridDataMap.newMinMaxMap(minX, minY, maxX, maxY, 100);

        //Map data sources to ID for lookup
        final Map<Integer, DotSource> idToSource = new HashMap();
        dotSources.forEach(dot -> idToSource.put(dot.id, dot));

        //Plot dots and path out there influence
        for (DotSource source : dotSources) {
            try {
                path(source, gridDataMap, idToSource);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unexpected error while path was running for " + source, e);
            }
        }

        //Convert map data into blobs

        //Get Largest without infinite
    }

    static void path(DotSource source, GridDataMap gridDataMap, Map<Integer, DotSource> idToSource)
    {
        //Breadth first pathfinder
        // Map out all places we can take
        // Each run will replace entries of last run
        // The idea is that we use distance as a power counter
        // Tiles with weaker power are replaced with tiers of higher power
        final Queue<Dot> nextPath = new LinkedList();
        final Set<Dot> alreadyPathed = new HashSet();

        //Add center
        nextPath.offer(source.copy());

        //Path until done
        while (nextPath.peek() != null) {
            //Get next dot
            final Dot dot = nextPath.poll();
            alreadyPathed.add(dot);

            try {
                //Get distance
                int distance = dot.distanceMan(source);

                System.out.println("Path: " + dot + " distance " + distance);

                boolean doPath = false;
                //Owners of point
                if (distance == 0) {
                    doPath = true;
                    gridDataMap.insert(dot.x, dot.y, new GridDataPoint(source.id, -1));
                    System.out.println("\t#" + source.id + " took its center point " + dot);
                }
                //No owner of point
                else if (gridDataMap.getData(dot.x, dot.y) == null) {
                    doPath = true;
                    gridDataMap.insert(dot.x, dot.y, new GridDataPoint(source.id, distance));
                    System.out.println("\t#" + source.id + " claimed a point " + dot);
                }
                //Has the high ground
                else {
                    final GridDataPoint point = gridDataMap.getData(dot.x, dot.y);

                    if (point.owner != source.id) {
                        if (point.distance < distance) {
                            doPath = true;
                            gridDataMap.insert(dot.x, dot.y, new GridDataPoint(source.id, distance));
                            System.out.println("\t#" + source.id + " overpowered #" + point.owner + " for " + dot);
                        }
                        //Same power level
                        else if (point.distance == distance) {

                            if (point.owner != -1) {
                                gridDataMap.insert(dot.x, dot.y, new GridDataPoint(-1, distance));
                                System.out.println("\t#" + source.id + " tied with #" + point.owner + " for " + dot);

                                gridDataMap.getData(dot.x, dot.y).tied.add(source);
                                gridDataMap.getData(dot.x, dot.y).tied.add(idToSource.get(point.owner));
                            } else {
                                System.out.println("\t#" + source.id + " tied with server sources for " + dot);
                                point.tied.add(source);
                            }
                        }
                    } else {
                        System.out.println("\t#" + source.id + " pathed the same point");
                    }
                }

                if (doPath) {
                    //Path its 4 sides
                    for (Direction2D direction2D : Direction2D.MAIN) {
                        final Dot next = dot.add(direction2D);

                        //Ignore sides outside of the map and already pathed
                        if (!alreadyPathed.contains(next) && gridDataMap.insideMap(next.x, next.y)) {
                            nextPath.offer(next);
                        }
                    }
                }
            }
            catch ()
        }
    }
}
