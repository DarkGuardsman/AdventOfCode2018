package com.darkguardsman;

import com.darkguardsman.helpers.Direction2D;
import com.darkguardsman.helpers.Dot;
import com.darkguardsman.helpers.FileHelpers;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/18.
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
        lines.clear(); //Free up RAM

        //Calculate map size
        System.out.println("\nCalculating grid size: ");
        final int minX = dotSources.stream().min(Comparator.comparingInt(dot -> dot.x)).get().x;
        final int maxX = dotSources.stream().max(Comparator.comparingInt(dot -> dot.x)).get().x;
        final int minY = dotSources.stream().min(Comparator.comparingInt(dot -> dot.y)).get().y;
        final int maxY = dotSources.stream().max(Comparator.comparingInt(dot -> dot.y)).get().y;

        final GridDataMap gridDataMap = GridDataMap.newMinMaxMap(minX, minY, maxX, maxY, 0);
        System.out.println("\t" + gridDataMap);

        //Map data sources to ID for lookup
        final Map<Integer, DotSource> idToSource = new HashMap();
        dotSources.forEach(dot -> idToSource.put(dot.id, dot));

        //Plot dots and path out there influence
        System.out.println("\nPlotting data: ");
        gridDataMap.forEachPosition((x, y) -> {

            int bestDistance = 0;
            List<DotSource> owners = new ArrayList();
            for (DotSource dot : dotSources) {

                //See if any of the dot sources owns the position
                if (x == dot.x && y == dot.y) {
                    System.out.println("\t" + dot + " home is (" + x + ", " + y + ")");
                    gridDataMap.insert(x, y, new GridDataPoint(dot.id, -1));
                    return true;
                }

                //Get distance
                int distance = dot.distanceMan(x, y);

                //Lower value claims
                if (distance < bestDistance) {
                    bestDistance = distance;
                    owners.clear();
                    owners.add(dot);
                }
                //Same value joins claim
                else if (distance == bestDistance) {
                    owners.add(dot);
                } else if (owners.isEmpty()) {
                    bestDistance = distance;
                    owners.add(dot);
                }
            }

            if (!owners.isEmpty()) {

                //Single owner
                if (owners.size() == 1) {
                    System.out.println("\t" + owners.get(0) + " claimed (" + x + ", " + y + ")");
                    gridDataMap.insert(x, y, new GridDataPoint(owners.get(0).id, bestDistance));
                }
                //Several owners
                else {
                    System.out.println("\t" + owners.size() + " dots claimed (" + x + ", " + y + ")");
                    gridDataMap.insert(x, y, new GridDataPoint(-1, bestDistance));
                    gridDataMap.getData(x, y).tied.addAll(owners);
                }
            }


            return true;
        });
        generateGridView(new File(file.getParent(), "grid_output.csv"), gridDataMap);
        generateGridImage(new File(file.getParent(), "grid_image.png"), gridDataMap, 4);

        //Convert map data into blobs
        // Decided this would take too much time, instead using a simple loop
        // its less effective but easier to code
        // original idea was to map all points back to the source
        // then visualize the shapes and calculate data related
        // hence the name blob since the shape would be like the blob

        //Get Largest without infinite
        System.out.println("\nProcessing: ");
        int largestSize = 0;
        DotSource largest = null;
        for (DotSource source : dotSources) {

            //Search all grid cells for data matching source
            if (gridDataMap.forEachCell((x, y, data, edge) -> {
                if (data != null && data.owner == source.id) {
                    source.count += 1;
                    if (edge) {
                        source.infinite = true;
                        return false;
                    }
                }
                return true;
            })) ;

            //Debug
            System.out.println("" + source + " size of " + source.count + " is infinite[" + source.infinite + "]");

            //Check if largest
            if (!source.infinite && (source.count > largestSize || largest == null)) {
                largestSize = source.count;
                largest = source;
            }
        }

        //Output result
        System.out.println("\nLargest non-infinite: " + largest);
        System.out.println("\nLargest size: " + largestSize);
    }

    static void generateGridImage(File file, GridDataMap gridDataMap, int scale) {
        Random rand = new Random();
        HashMap<Integer, Color> idToColor = new HashMap();
        BufferedImage rawImage = new BufferedImage(gridDataMap.sizeX, gridDataMap.sizeY, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < gridDataMap.sizeX; x++) {
            for (int y = 0; y < gridDataMap.sizeY; y++) {
                GridDataPoint data = gridDataMap.data[gridDataMap.getIndexInternal(x, y)];
                if (data != null) {

                    //Create random color
                    if (!idToColor.containsKey(data.owner)) {
                        float r = rand.nextFloat();
                        float g = rand.nextFloat();
                        float b = rand.nextFloat();
                        idToColor.put(data.owner, new Color(r, g, b));
                    }

                    //Set pixel to color
                    rawImage.setRGB(x, y, idToColor.get(data.owner).getRGB());
                } else {
                    //Set pixel to color
                    rawImage.setRGB(x, y, Color.magenta.getRGB());
                }
            }
        }


        try {
            ImageIO.write(rawImage, "png", file);
        } catch (IOException e) {
            System.out.println(e);
        }

        //Scale image
        if (scale > 1) {
            BufferedImage scaledImage = new BufferedImage(rawImage.getWidth() * scale, rawImage.getHeight() * scale, BufferedImage.TYPE_INT_ARGB);
            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            AffineTransformOp scaleOp =
                    new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            scaledImage = scaleOp.filter(rawImage, scaledImage);

            try {
                ImageIO.write(scaledImage, "png", new File(file.getParent(), file.getName().replace(".png", "") + "_scaled.png"));
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    static void generateGridView(File file, GridDataMap gridDataMap) {
        try (PrintWriter pw = new PrintWriter(file)) {

            StringBuilder sb = new StringBuilder();

            //Generate header line
            sb.append("y/x");
            for (int x = gridDataMap.x; x < (gridDataMap.x + gridDataMap.sizeX); x++) {
                sb.append(',');
                sb.append(x);
            }
            sb.append('\n');

            //Generate rows
            for (int y = gridDataMap.y; y < (gridDataMap.y + gridDataMap.sizeY); y++) {
                sb.append(y);
                for (int x = gridDataMap.x; x < (gridDataMap.x + gridDataMap.sizeX); x++) {
                    sb.append(',');
                    GridDataPoint point = gridDataMap.getData(x, y);
                    sb.append(point != null ? (point.owner == -1 ? "#" : "" + point.owner) : "@");
                }
                sb.append("\n");
            }

            pw.write(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Paths all points from the source adding new values to the map
     * <p>
     * Values are added when:
     * - Previous value was null
     * - Previous value was the source's position
     * - Previous value was greater in distance then new value
     * <p>
     * Values are updated:
     * - Overlap exists
     *
     * @param source
     * @param gridDataMap
     * @param idToSource
     */
    static void path(DotSource source, GridDataMap gridDataMap, Map<Integer, DotSource> idToSource) {

        //Honestly this could be replaced with two foreach loops and still get the same result... slightly slower total time

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

                System.out.println("\tPath: " + dot + " distance " + distance);

                boolean doPath = false;
                //Owners of point
                if (distance == 0) {
                    doPath = true;
                    gridDataMap.insert(dot.x, dot.y, new GridDataPoint(source.id, -1));
                    System.out.println("\t\t#" + source.id + " took its center point " + dot);
                }
                //No owner of point
                else if (gridDataMap.getData(dot.x, dot.y) == null) {
                    doPath = true;
                    gridDataMap.insert(dot.x, dot.y, new GridDataPoint(source.id, distance));
                    System.out.println("\t\t#" + source.id + " claimed a point " + dot);
                }
                //Has the high ground
                else {
                    final GridDataPoint point = gridDataMap.getData(dot.x, dot.y);

                    if (point.owner != source.id) {
                        //Less distance means the dot is closer
                        if (distance < point.distance) {
                            doPath = true;
                            gridDataMap.insert(dot.x, dot.y, new GridDataPoint(source.id, distance));
                            System.out.println("\t\t#" + source.id + " overpowered #" + point.owner + " for " + dot);
                        }
                        //Same power level
                        else if (point.distance == distance) {

                            if (point.owner != -1) {
                                gridDataMap.insert(dot.x, dot.y, new GridDataPoint(-1, distance));
                                System.out.println("\t\t#" + source.id + " tied with #" + point.owner + " for " + dot);

                                gridDataMap.getData(dot.x, dot.y).tied.add(source);
                                gridDataMap.getData(dot.x, dot.y).tied.add(idToSource.get(point.owner));
                            } else {
                                System.out.println("\t\t#" + source.id + " tied with server sources for " + dot);
                                point.tied.add(source);
                            }
                        } else {
                            System.out.println("\t\t#" + source.id + " failed to overpower tile owned by #" + point.owner + " " + point.distance);
                        }
                    } else {
                        System.out.println("\t\t#" + source.id + " pathed the same point");
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
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error while pathing node " + dot, e);
            }
        }
    }
}
