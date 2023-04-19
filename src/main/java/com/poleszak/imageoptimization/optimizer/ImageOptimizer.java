package com.poleszak.imageoptimization.optimizer;

import com.poleszak.imageoptimization.helper.DirectoryHelper;
import com.poleszak.imageoptimization.helper.ImageReaderHelper;
import com.poleszak.imageoptimization.helper.ImageWriterHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageOptimizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageOptimizer.class);

    private final ImageReaderHelper imageReaderHelper;
    private final ImageWriterHelper imageWriterHelper;
    private final DirectoryHelper directoryHelper;

    public void optimizeAllImages(String dirPath) throws IOException {
        LOGGER.info("Starting optimization for all images in directory {}", dirPath);
        File[] files = directoryHelper.getFilesFromDirectory(dirPath);
        validateFilesNotNull(files);

        for (File file : files) {
            if (file.isFile()) {
                String inputFilePath = file.getAbsolutePath();
                String outputFilePath = inputFilePath.replaceFirst("[.][^.]+$", "") + ".webp";
                optimizeImage(inputFilePath, outputFilePath);
            }
        }
        LOGGER.info("Finished optimization for all images in directory {}", dirPath);
    }

    private void optimizeImage(String inputFilePath, String outputFilePath) throws IOException {
        LOGGER.info("Starting optimization for image: {}", outputFilePath);
        try {
            BufferedImage image = imageReaderHelper.readImage(inputFilePath);
            imageWriterHelper.writeOptimizedImage(image, outputFilePath);
            LOGGER.info("Successfully optimized and saved image: {}", outputFilePath);
        } catch (ImageReaderHelper.ImageReadException | ImageWriterHelper.ImageWriteException e) {
            LOGGER.info("An error occurred while optimizing the image: {}", inputFilePath);
            throw new IOException("An error occurred while optimizing the image", e);
        }
    }

    private static void validateFilesNotNull(File[] files) throws IOException {
        if (files == null) {
            throw new IOException("Could not find any files in the specified directory");
        }
    }
}
