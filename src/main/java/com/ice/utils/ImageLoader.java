package com.ice.utils;

import com.ice.net.utils.ServiceDebugLogger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    public static BufferedImage load(String name) {
        try {
            return ImageIO.read(new File("src/main/resources/images/" + name));
        } catch (IOException e) {
            e.printStackTrace();
            ServiceDebugLogger.log(ImageLoader.class, "Couldn't load image...");
        }
        return null;
    }
    public static BufferedImage loadFromFileSystem(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            ServiceDebugLogger.log(ImageLoader.class, "Couldn't load image...");
        }
        return null;
    }
}
