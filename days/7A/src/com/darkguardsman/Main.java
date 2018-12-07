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
    }
}
