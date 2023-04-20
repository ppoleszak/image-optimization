package com.poleszak.imageoptimization.helper;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DirectoryHelper {
    public List<File> getFilesFromDirectory(String dirPath) throws IOException {
        Path folderPath = Paths.get(dirPath);
        FileFilter fileFilter = file -> {
            String name = file.getName().toLowerCase();
            return !name.endsWith(".webp") && name.matches(".*\\.[a-z0-9]+");
        };

        try (Stream<Path> paths = Files.list(folderPath)) {
            return paths.filter(path -> !Files.isDirectory(path) && fileFilter.accept(path.toFile()))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

}
