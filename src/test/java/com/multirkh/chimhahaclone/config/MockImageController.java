package com.multirkh.chimhahaclone.config;

import com.multirkh.chimhahaclone.minio.MinioConfig;
import com.multirkh.chimhahaclone.minio.MinioService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;

@Service
public class MockImageController {
    @MockBean
    private MinioConfig minoConfig;
    @MockBean
    private MinioService minioService;
}
