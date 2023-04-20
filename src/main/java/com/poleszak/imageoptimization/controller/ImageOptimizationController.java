package com.poleszak.imageoptimization.controller;

import com.poleszak.imageoptimization.controller.request.OptimizeImagesDirPathRequest;
import com.poleszak.imageoptimization.service.ImageOptimizationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ImageOptimizationController {

    private final ImageOptimizationService imageOptimizationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageOptimizationController.class);

    @PostMapping
    public DeferredResult<ResponseEntity<?>> optimizeAllImagesInDirPath(@RequestBody OptimizeImagesDirPathRequest optimizeImagesDirPathRequest) throws IOException {
        LOGGER.info("Optimization request received for directory: {}", optimizeImagesDirPathRequest.getDirPath());
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();

        CompletableFuture<Void> optimizationTask = imageOptimizationService.optimize(optimizeImagesDirPathRequest.getDirPath());

        optimizationTask.thenRun(() -> {
            LOGGER.info("Optimization successfully completed for directory: {}", optimizeImagesDirPathRequest.getDirPath());
            deferredResult.setResult(ResponseEntity.ok().build());
        }).exceptionally(ex -> {
            LOGGER.error("Optimization failed for directory: {}", optimizeImagesDirPathRequest.getDirPath(), ex);
            deferredResult.setErrorResult(ResponseEntity.status(500).build());
            return null;
        });

        return deferredResult;
    }
}