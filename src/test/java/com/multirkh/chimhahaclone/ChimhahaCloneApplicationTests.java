package com.multirkh.chimhahaclone;

import static org.assertj.core.api.Assertions.assertThat;

import com.multirkh.chimhahaclone.common.minio.MinioConfig;
import com.multirkh.chimhahaclone.common.minio.MinioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import(TestRedisConfiguration.class)
class ChimhahaCloneApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private MinioConfig minioConfig;
    @MockitoBean
    private MinioService minioService;

    @Test
    @DisplayName("Embedded Redis에 값을 저장하고 조회할 수 있다")
    void testRedisSaveAndGet() {
        String key = "testKey";
        String value = "Hello, Embedded Redis!";
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        valueOperations.set(key, value);
        String result = valueOperations.get(key);

        assertThat(result).isEqualTo(value);
    }
}
