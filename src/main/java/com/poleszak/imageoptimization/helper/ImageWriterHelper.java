package com.poleszak.imageoptimization.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPWriteParam;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.drew.metadata.exif.ExifDirectoryBase.TAG_ORIENTATION;
import static com.luciad.imageio.webp.WebPWriteParam.LOSSY_COMPRESSION;
import static java.lang.Math.PI;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

@Component
public class ImageWriterHelper {
    private static final float COMPRESSION_QUALITY = 0.4F;

    public void writeOptimizedImage(BufferedImage image, String outputFilePath, String imageType) throws ImageWriteException, IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ImageIO.write(image, imageType, byteArray);

        ImageWriter writer = new WebPImageWriterSpi().createWriterInstance();
        ImageWriteParam writeParam = new WebPWriteParam(writer.getLocale());

        if ("JPEG".equalsIgnoreCase(imageType)) {
            try {
                image = prepareMetadataAndRotateImage(new ByteArrayInputStream(byteArray.toByteArray()), image);
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


    private BufferedImage prepareMetadataAndRotateImage(ByteArrayInputStream byteArray, BufferedImage image) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(byteArray);
        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        if (exifIFD0Directory == null) {
            return image;
        }

        int orientation;
        try {
            orientation = exifIFD0Directory.getInt(TAG_ORIENTATION);
        } catch (MetadataException e) {
            return image;
        }

        return rotateImage(image, orientation);
    }


    private BufferedImage rotateImage(BufferedImage image, int orientation) {
        int width = image.getWidth();
        int height = image.getHeight();
        AffineTransform at = new AffineTransform();

        switch (orientation) {
            case 3:
                at.translate(width, height);
                at.rotate(PI);
                break;
            case 6:
                at.translate(height, 0);
                at.rotate(PI / 2);
                break;
            case 8:
                at.translate(0, width);
                at.rotate(-PI / 2);
                break;
            default:
                return image;
        }

        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
        return op.filter(image, rotatedImage);
    }


    public static class ImageWriteException extends Exception {
        public ImageWriteException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}