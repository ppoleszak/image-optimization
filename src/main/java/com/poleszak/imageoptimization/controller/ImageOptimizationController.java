package com.poleszak.imageoptimization.controller;

import com.poleszak.imageoptimization.controller.request.OptimizeImagesDirPathRequest;
import com.poleszak.imageoptimization.service.ImageOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageOptimizationController {

    private final ImageOptimizationService imageOptimizationService;

    @PostMapping
    public void optimizeAllImagesInDirPath(@RequestBody OptimizeImagesDirPathRequest optimizeImagesDirPathRequest) throws Exception {
        imageOptimizationService.optimize(optimizeImagesDirPathRequest.getDirPath());
    }
}
