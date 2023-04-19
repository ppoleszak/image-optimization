package com.poleszak.imageoptimization.controller.request;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptimizeImagesDirPathRequest {
    @NotNull
    private String dirPath;
}
