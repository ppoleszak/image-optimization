package com.poleszak.imageoptimization.optimizer;

import com.poleszak.imageoptimization.helper.DirectoryHelper;
import com.poleszak.imageoptimization.helper.ImageReaderHelper;
import com.poleszak.imageoptimization.helper.ImageWriterHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageOptimizer {

    private final ImageReaderHelper imageReaderHelper;
    private final ImageWriterHelper imageWriterHelper;
    private final DirectoryHelper directoryHelper;

    public void optimizeAllImages(String dirPath) throws IOException {
        File[] files = directoryHelper.getFilesFromDirectory(dirPath);
        validateFilesNotNull(files);

        for (File file : files) {
            if (file.isFile()) {
                String inputFilePath = file.getAbsolutePath();
                String outputFilePath = inputFilePath.replaceFirst("[.][^.]+$", "") + ".webp";
                optimizeImage(inputFilePath, outputFilePath);
            }
        }
    }

    private void optimizeImage(String inputFilePath, String outputFilePath) throws IOException {
        try {
            BufferedImage image = imageReaderHelper.readImage(inputFilePath);
            imageWriterHelper.writeOptimizedImage(image, outputFilePath);
        } catch (ImageReaderHelper.ImageReadException | ImageWriterHelper.ImageWriteException e) {
            throw new IOException("An error occurred while optimizing the image", e);
        }
    }

    private static void validateFilesNotNull(File[] files) throws IOException {
        if (files == null) {
            throw new IOException("Could not find any files in the specified directory");
        }
    }
}
