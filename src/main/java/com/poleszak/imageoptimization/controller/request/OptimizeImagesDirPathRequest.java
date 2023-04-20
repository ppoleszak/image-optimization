package com.poleszak.imageoptimization.controller.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptimizeImagesDirPathRequest {

    @NotNull
    private String dirPath;
}
