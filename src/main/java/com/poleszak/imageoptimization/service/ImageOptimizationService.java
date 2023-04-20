package com.poleszak.imageoptimization.service;

import com.poleszak.imageoptimization.optimizer.ImageOptimizer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Semaphore;

import static java.lang.Void.TYPE;

@Service
@RequiredArgsConstructor
public class ImageOptimizationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageOptimizationService.class);
    private static final int MAX_CONCURRENT_TASKS = 50;

    private final ImageOptimizer imageOptimizer;
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_TASKS);

    public CompletableFuture<Void> optimize(String dirPath) throws IOException {
        return imageOptimizer.optimizeAllImages(dirPath, semaphore)
                .exceptionally(e -> {
                    Throwable cause = e instanceof CompletionException ? e.getCause() : e;
                    LOGGER.error("An error occurred while optimizing images", cause);
                    return TYPE.cast(null);
                });
    }
}