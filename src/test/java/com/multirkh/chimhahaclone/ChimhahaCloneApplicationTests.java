package com.multirkh.chimhahaclone;

import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({JwtDecoderTestConfig.class, DataInitializer.class})
class ChimhahaCloneApplicationTests {

    @Test
    void contextLoads() {
    }

}
