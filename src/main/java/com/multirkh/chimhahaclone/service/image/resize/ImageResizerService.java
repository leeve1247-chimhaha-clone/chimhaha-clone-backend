package com.multirkh.chimhahaclone.service.image.resize;

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

    public InputStreamAndLength resizeImage(InputStream inputStream, String contentType) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            int[] croppedSize = getCroppedSize(bufferedImage, new int[]{WIDTH, HEIGHT});
            ByteArrayOutputStream tempOs = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, contentType.split("/")[1], tempOs);
            Thumbnails.of(new ByteArrayInputStream(tempOs.toByteArray()))
                    .size(croppedSize[0], croppedSize[1])
                    .crop(Positions.CENTER)
                    .toOutputStream(os);
            return new InputStreamAndLength(os.toByteArray().length, new ByteArrayInputStream(os.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException("Error resizing image", e);
        }
    }

    @NotNull
    private int[] getCroppedSize(BufferedImage bufferedImage, int[] fixedDimension) {
        int[] imageDimensions = getInputStreamImageDimension(bufferedImage);
        return getMinimumCroppedSize(imageDimensions, fixedDimension);
    }

    @NotNull
    private int[] getInputStreamImageDimension(BufferedImage bufferedImage) {
        return new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
    }

    @NotNull
    private int[] getMinimumCroppedSize(@NotNull int[] dimensions, @NotNull int[] fixedSize) {
        int fixedWidth = fixedSize[0];
        int fixedHeight = fixedSize[1];

        int width = dimensions[0];
        int height = dimensions[1];
        int[] croppedDimension;
        if (width * fixedHeight > height * fixedWidth) {
            croppedDimension = new int[]{(height * fixedWidth / fixedHeight), height};
        } else {
            croppedDimension = new int[]{width, (width * 275 / fixedWidth)};
        }

        if (croppedDimension[0] < fixedWidth) {
            return croppedDimension;
        } else {
            return new int[]{fixedWidth, fixedHeight};
        }
    }

    private long getSize(ByteArrayOutputStream os) {
        return os.toByteArray().length;
    }
}

