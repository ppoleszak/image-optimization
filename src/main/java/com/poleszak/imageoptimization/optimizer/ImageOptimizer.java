package com.poleszak.imageoptimization.optimizer;

import com.poleszak.imageoptimization.helper.DirectoryHelper;
import com.poleszak.imageoptimization.helper.ImageReaderHelper;
import com.poleszak.imageoptimization.helper.ImageWriterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Component
public class ImageOptimizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageOptimizer.class);

    private final ImageReaderHelper imageReaderHelper;
    private final ImageWriterHelper imageWriterHelper;
    private final DirectoryHelper directoryHelper;
    private final ExecutorService executorService;

    public ImageOptimizer(ImageReaderHelper imageReaderHelper, ImageWriterHelper imageWriterHelper, DirectoryHelper directoryHelper) {
        this.imageReaderHelper = imageReaderHelper;
        this.imageWriterHelper = imageWriterHelper;
        this.directoryHelper = directoryHelper;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public CompletableFuture<Void> optimizeAllImages(String dirPath, Semaphore semaphore) throws IOException {
        LOGGER.info("Starting optimization for all images in directory {}", dirPath);
        File[] files = directoryHelper.getFilesFromDirectory(dirPath);
        validateFilesNotNull(files);

        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                String inputFilePath = file.getAbsolutePath();
                String outputFilePath = inputFilePath.replaceFirst("[.][^.]+$", "") + ".webp";
                try {
                    semaphore.acquire();
                    tasks.add(optimizeImage(inputFilePath, outputFilePath)
                            .whenComplete((__, throwable) -> semaphore.release()));
                } catch (InterruptedException e) {
                    LOGGER.error("Failed to acquire semaphore for image optimization", e);
                }
            }
        }

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                .thenRun(() -> LOGGER.info("Finished optimization for all images in directory {}", dirPath));
    }

    private CompletableFuture<Void> optimizeImage(String inputFilePath, String outputFilePath) {
        return CompletableFuture.runAsync(() -> {
            LOGGER.info("Starting optimization for image: {}", outputFilePath);
            try {
                BufferedImage image = imageReaderHelper.readImage(inputFilePath);
                imageWriterHelper.writeOptimizedImage(image, outputFilePath);
                LOGGER.info("Successfully optimized and saved image: {}", outputFilePath);
            } catch (ImageReaderHelper.ImageReadException | ImageWriterHelper.ImageWriteException | IOException e) {
                LOGGER.error("An error occurred while optimizing the image: {}. Error: {}", inputFilePath, e.getMessage());
            }
        }, executorService);
    }


    private static void validateFilesNotNull(File[] files) throws IOException {
        if (files == null) {
            throw new IOException("Could not find any files in the specified directory");
        }
    }
}