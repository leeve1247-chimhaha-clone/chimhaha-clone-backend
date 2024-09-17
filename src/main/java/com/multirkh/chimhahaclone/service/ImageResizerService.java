package com.multirkh.chimhahaclone.service;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageResizerService {
    static final int WIDTH = 350;
    static final int HEIGHT = 275;

    public InputStream resizeImage(InputStream inputStream) {
        int[] croppedSize = getCroppedSize(inputStream, new int[]{WIDTH, HEIGHT});
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            Thumbnails.of(inputStream)
                    .size(croppedSize[0], croppedSize[1])
                    .crop(Positions.CENTER)
                    .toOutputStream(os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error resizing image", e);
        }
    }

    @NotNull
    private int[] getCroppedSize(InputStream inputStream, int[] fixedDimension){
        int[] imageDimensions = getInputStreamImageDimension(inputStream);
        return getCroppedSize(imageDimensions, fixedDimension);
    }

    @NotNull
    private int[] getInputStreamImageDimension(InputStream inputStream) {
        try {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            return new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
        } catch (IOException e) {
            throw new RuntimeException("Error getting image dimensions", e);
        }
    }

    @NotNull
    private int[] getCroppedSize(@NotNull int[] dimensions, @NotNull int[] fixedSize) {
        int fixedWidth = fixedSize[0];
        int fixedHeight = fixedSize[1];

        int width = dimensions[0];
        int height = dimensions[1];
        int[] croppedDimension;
        if (width *  fixedHeight > height * fixedWidth){
            croppedDimension = new int[]{(height * fixedWidth / fixedHeight), height};
        } else {
            croppedDimension = new int[]{width,(width * 275 / fixedWidth)};
        }

        if (croppedDimension[0] < fixedWidth) {
            return croppedDimension;
        } else {
            return new int[]{fixedWidth, fixedHeight};
        }
    }
}

