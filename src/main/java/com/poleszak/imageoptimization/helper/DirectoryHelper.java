package com.poleszak.imageoptimization.helper;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DirectoryHelper {
    public File[] getFilesFromDirectory(String dirPath) {
        File folder = new File(dirPath);
        return folder.listFiles();
    }
}
