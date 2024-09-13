package com.multirkh.chimhahaclone.redis;

import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({JwtDecoderTestConfig.class, DataInitializer.class})
public class RedisConnectionTest {
    @Autowired
    private ViewCountService viewCountService;

    @Test
    public void testIncrementViewCount(){
        viewCountService.incrementViewCount(31L);
    }

    @Test
    public void testSyncViewCountsToDB(){
        viewCountService.syncViewCountsToDB();
    }
}
