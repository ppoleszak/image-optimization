package com.poleszak.imageoptimization.helper;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;

@Component
public class DirectoryHelper {
    public File[] getFilesFromDirectory(String dirPath) {
        File folder = new File(dirPath);
        FileFilter fileFilter = file -> {
            String name = file.getName().toLowerCase();
            return !name.endsWith(".webp") && name.matches(".*\\.[a-z0-9]+");
        };

        return folder.listFiles(fileFilter);
    }
}
