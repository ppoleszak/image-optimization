package com.poleszak.imageoptimization.controller;

import com.poleszak.imageoptimization.controller.request.OptimizeImagesDirPathRequest;
import com.poleszak.imageoptimization.service.ImageOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ImageOptimizationController {

    private final ImageOptimizationService imageOptimizationService;

    @PostMapping
    public CompletableFuture<Void> optimizeAllImagesInDirPath(@RequestBody OptimizeImagesDirPathRequest optimizeImagesDirPathRequest) throws IOException {
        return imageOptimizationService.optimize(optimizeImagesDirPathRequest.getDirPath());
    }
}
