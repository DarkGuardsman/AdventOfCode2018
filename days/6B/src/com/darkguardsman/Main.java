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
 * Created by Dark(DarkGuardsman, Robert) on 12/7/18.
 */
public class Main {

    public static void main(String... args) {
        //get args
        final File file = new File(args[0]);
        final int distanceCheck = Integer.parseInt(args[1]);

        //Arg output
        System.out.println("File: " + file);
        System.out.println("Distance Check: " + distanceCheck);

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

            int distance = 0;
            for (DotSource dot : dotSources) {

                //Get distance
                distance += dot.distanceMan(x, y);
            }
            gridDataMap.insert(x, y, new GridDataPoint(distance, distance < distanceCheck ? 1 : 0));

            return true;
        });
        generateGridView(new File(file.getParent(), "grid_output.csv"), gridDataMap);
        generateGridImage(new File(file.getParent(), "grid_image.png"), gridDataMap, 4);

        //Get Largest without infinite
        System.out.println("\nProcessing: ");

        int count = 0;
        for (int x = 0; x < gridDataMap.sizeX; x++) {
            for (int y = 0; y < gridDataMap.sizeY; y++) {
                GridDataPoint data = gridDataMap.data[gridDataMap.getIndexInternal(x, y)];
                if (data != null && data.distance == 1) {
                    count++; //Oh yes there is a totally better way to do this :P
                }
            }
        }

        //Output result
        System.out.println("\nCount: " + count);
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
}
