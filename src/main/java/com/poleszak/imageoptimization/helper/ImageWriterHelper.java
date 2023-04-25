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
import static java.awt.image.AffineTransformOp.TYPE_BILINEAR;
import static java.lang.Math.PI;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

@Component
public class ImageWriterHelper {
    private static final float COMPRESSION_QUALITY = 0.4F;

    public void writeOptimizedImage(BufferedImage image, String outputFilePath, String imageType) throws ImageWriteException, IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        ImageWriter writer = new WebPImageWriterSpi().createWriterInstance();
        ImageWriteParam writeParam = new WebPWriteParam(writer.getLocale());

        if (imageType.equals("JPEG") || imageType.equals("JPG")) {

            try {
                image = prepareMetadataAndRotateImage(byteArray, image);
            } catch (ImageProcessingException e) {
                throw new ImageWriteException("Problem with process image: " + outputFilePath, e);
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

    private BufferedImage prepareMetadataAndRotateImage(ByteArrayOutputStream byteArray, BufferedImage image) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(byteArray.toByteArray()));
        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        int orientation = 1;
        if (exifIFD0Directory != null) {
            try {
                orientation = exifIFD0Directory.getInt(TAG_ORIENTATION);
            } catch (MetadataException e) {
                e.printStackTrace();
            }
        }
        if (orientation != 1) {
            image = rotateImage(image, orientation);
        }
        return image;
    }

    private BufferedImage rotateImage(BufferedImage image, int orientation) {
        double angle;
        switch (orientation) {
            case 3:
                angle = PI;
                break;
            case 6:
                angle = PI / 2;
                break;
            case 8:
                angle = -PI / 2;
                break;
            default:
                return image;
        }
        double centerX = image.getWidth() / 2.0;
        double centerY = image.getHeight() / 2.0;
        AffineTransform at = AffineTransform.getRotateInstance(angle, centerX, centerY);
        AffineTransformOp op = new AffineTransformOp(at, TYPE_BILINEAR);
        return op.filter(image, null);
    }

    public static class ImageWriteException extends Exception {
        public ImageWriteException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}