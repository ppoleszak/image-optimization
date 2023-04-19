package com.poleszak.imageoptimization.helper;

import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPWriteParam;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.luciad.imageio.webp.WebPWriteParam.LOSSY_COMPRESSION;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

@Component
public class ImageWriterHelper {
    public void writeOptimizedImage(BufferedImage image, String outputFilePath) throws ImageWriteException, IOException {
        ImageWriter writer = new WebPImageWriterSpi().createWriterInstance();
        ImageWriteParam writeParam = new WebPWriteParam(writer.getLocale());

        writeParam.setCompressionMode(MODE_EXPLICIT);
        writeParam.setCompressionType(writeParam.getCompressionTypes()[LOSSY_COMPRESSION]);
        writeParam.setCompressionQuality(0.4F);

        try (FileImageOutputStream outputStream = new FileImageOutputStream(new File(outputFilePath))) {
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } catch (IOException e) {
            throw new ImageWriteException("An error occurred while writing the optimized image", e);
        }
    }

    public static class ImageWriteException extends Exception {
        public ImageWriteException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}