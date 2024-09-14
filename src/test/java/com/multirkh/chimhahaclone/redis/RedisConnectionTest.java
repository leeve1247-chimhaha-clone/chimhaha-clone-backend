package com.multirkh.chimhahaclone.redis;

import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

@SpringBootTest
@Slf4j
@Import({JwtDecoderTestConfig.class, DataInitializer.class})
public class RedisConnectionTest {
    @Autowired
    private ViewCountService viewCountService;
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Test
    public void testSetViewCount() {
    }

    @Test
    public void testIncrementViewCount() {
        viewCountService.incrementViewCount(1L);
        viewCountService.incrementViewCount(2L);
        viewCountService.incrementViewCount(3L);

        Objects.requireNonNull(redisTemplate.keys("post:views:*")).forEach(key -> {
            Integer value = redisTemplate.opsForValue().get(key);
            Long contendId = Long.valueOf(key.split(":")[2]);
            log.info("key: {}, value: {}, contendId: {}", key, value, contendId);
            redisTemplate.delete(key);
        });

    }

    @Test
    public void testSyncViewCountsToDB() {
        viewCountService.syncViewCountsToDB();
    }
}
