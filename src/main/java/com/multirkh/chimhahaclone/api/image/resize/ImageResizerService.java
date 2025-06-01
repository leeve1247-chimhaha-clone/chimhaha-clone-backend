package com.multirkh.chimhahaclone.api.image.resize;

import net.coobird.thumbnailator.Thumbnailator;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageResizerService {
    static final int WIDTH = 350;
    static final int HEIGHT = 275;

    @NotNull
    public InputStream createResizedImage(InputStream rawImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(rawImage,outputStream, WIDTH, HEIGHT);
        byte[] bytes = outputStream.toByteArray();
        return new ByteArrayInputStream(bytes);
    }
}

