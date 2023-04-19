package com.poleszak.imageoptimization.service;

import com.poleszak.imageoptimization.optimizer.ImageOptimizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageOptimizationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageOptimizationService.class);
    private final ImageOptimizer imageOptimizer;

    public CompletableFuture<Void> optimize(String dirPath) throws IOException {
        return imageOptimizer.optimizeAllImages(dirPath)
                .exceptionally(e -> {
                    Throwable cause = e instanceof CompletionException ? e.getCause() : e;
                    LOGGER.error("An error occurred while optimizing images", cause);
                    return null;
                });
    }
}
