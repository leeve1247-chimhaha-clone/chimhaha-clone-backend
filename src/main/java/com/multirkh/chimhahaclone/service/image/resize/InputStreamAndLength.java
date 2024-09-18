package com.multirkh.chimhahaclone.service.image.resize;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@RequiredArgsConstructor
public class InputStreamAndLength {
    private int size;
    private InputStream inputStream;
}
