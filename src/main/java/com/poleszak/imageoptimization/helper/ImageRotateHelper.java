package com.poleszak.imageoptimization.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.springframework.stereotype.Component;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.drew.metadata.exif.ExifDirectoryBase.TAG_ORIENTATION;
import static java.lang.Math.PI;

@Component
public class ImageRotateHelper {

    public BufferedImage prepareMetadataAndRotateImage(ByteArrayInputStream byteArray, BufferedImage image) throws ImageProcessingException, IOException {
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
}