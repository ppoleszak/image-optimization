package com.poleszak.imageoptimization.service;

import com.poleszak.imageoptimization.optimizer.ImageOptimizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageOptimizationService {

    private final ImageOptimizer imageOptimizer;

    public void optimize(String dirPath) throws Exception {
        try {
            imageOptimizer.optimizeAllImages(dirPath);
        } catch (Exception e) {
            throw new Exception("An error occurred while optimizing images", e);
        }
    }
}
