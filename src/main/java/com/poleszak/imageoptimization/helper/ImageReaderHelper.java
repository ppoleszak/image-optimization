package com.poleszak.imageoptimization.helper;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class ImageReaderHelper {
    public BufferedImage readImage(String inputFilePath) throws ImageReadException {
        try (ImageInputStream inputStream = ImageIO.createImageInputStream(new File(inputFilePath))) {
            if (inputStream == null) {
                throw new ImageReadException("Cannot read the image file: " + inputFilePath);
            }
            Optional<ImageReader> readerOptional = ofNullable(ImageIO.getImageReaders(inputStream).next());

            if (!readerOptional.isPresent()) {
                throw new ImageReadException("No ImageReader found for the provided image file");
            }
            ImageReader reader = readerOptional.get();
            reader.setInput(inputStream);
            return reader.read(0);
        } catch (IOException e) {
            throw new ImageReadException("An error occurred while reading the image", e);
        }
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
