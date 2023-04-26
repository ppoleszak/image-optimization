package com.poleszak.imageoptimization.helper;

import com.drew.imaging.ImageProcessingException;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPWriteParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.luciad.imageio.webp.WebPWriteParam.LOSSY_COMPRESSION;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

@Component
@RequiredArgsConstructor
public class ImageWriterHelper {
    private static final float COMPRESSION_QUALITY = 0.4F;

    private final ImageRotateHelper imageRotateHelper;

    public void writeOptimizedImage(BufferedImage image, String outputFilePath, String imageType) throws ImageWriteException, IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ImageIO.write(image, imageType, byteArray);

        ImageWriter writer = new WebPImageWriterSpi().createWriterInstance();
        ImageWriteParam writeParam = new WebPWriteParam(writer.getLocale());

        if ("JPEG".equalsIgnoreCase(imageType) || "JPG".equalsIgnoreCase(imageType) || "PNG".equalsIgnoreCase(imageType)) {
            try {
                image = imageRotateHelper.prepareMetadataAndRotateImage(new ByteArrayInputStream(byteArray.toByteArray()), image);
            } catch (ImageProcessingException e) {
                throw new ImageWriteException("Problem with processing image: " + outputFilePath, e);
            }
        }

        writeParam.setCompressionMode(MODE_EXPLICIT);
        writeParam.setCompressionType(writeParam.getCompressionTypes()[LOSSY_COMPRESSION]);
        writeParam.setCompressionQuality(COMPRESSION_QUALITY);

        try (FileImageOutputStream outputStream = new FileImageOutputStream(new File(outputFilePath))) {
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } catch (IOException e) {
            throw new ImageWriteException("An error occurred while writing the optimized image", e);
        } finally {
            writer.dispose();
        }
    }

    public static class ImageWriteException extends Exception {
        public ImageWriteException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}