package com.multirkh.chimhahaclone;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.multirkh.chimhahaclone.common.minio.MinioConfig;
import com.multirkh.chimhahaclone.common.minio.MinioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ChimhahaCloneApplicationTests {
    @MockitoBean
    MinioService minioService;
    @MockitoBean
    private MinioConfig minioConfig;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void contextLoads() {
    }

    @Test
    void testSomething() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn("mockedValue");
    }
}
