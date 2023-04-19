package com.poleszak.imageoptimization.helper;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Component
public class ImageReaderHelper {
    public BufferedImage readImage(String inputFilePath) throws ImageReadException {
        BufferedImage image;
        try (ImageInputStream inputStream = ImageIO.createImageInputStream(new File(inputFilePath))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
            if (!readers.hasNext()) {
                throw new ImageReadException("No ImageReader found for the provided image file");
            }
            ImageReader reader = readers.next();
            reader.setInput(inputStream);
            image = reader.read(0);
        } catch (IOException e) {
            throw new ImageReadException("An error occurred while reading the image", e);
        }
        return image;
    }

    public static class ImageReadException extends Exception {
        public ImageReadException(String message) {
            super(message);
        }

        public ImageReadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
