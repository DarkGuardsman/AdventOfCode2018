package com.darkguardsman.helpers.image;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/2018.
 */
public class ImageHelpers
{

    public static boolean savePNG(File file, BufferedImage rawImage)
    {
        try
        {
            ImageIO.write(rawImage, "png", file);
            return true;
        }
        catch (IOException e)
        {
            System.out.println(e);
            return false;
        }
    }

    public static BufferedImage scaleImage(BufferedImage rawImage, int scale)
    {
        if (scale > 1)
        {
            BufferedImage scaledImage = new BufferedImage(rawImage.getWidth() * scale, rawImage.getHeight() * scale, BufferedImage.TYPE_INT_ARGB);
            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            AffineTransformOp scaleOp =
                    new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            scaledImage = scaleOp.filter(rawImage, scaledImage);

            return scaledImage;
        }
        return rawImage;
    }
}
