package com.multirkh.chimhahaclone;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;


@TestConfiguration // 테스트 환경에서만 스캔되는 설정
public class TestRedisConfiguration {

    private final RedisServer redisServer;

    public TestRedisConfiguration(@Value("${spring.data.redis.port}") int port) throws IOException {
        // 설정된 포트로 Redis 서버 객체 생성
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void startRedis() throws IOException {
        // 테스트 컨텍스트 로드 시 서버 시작
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        // 테스트 종료 시 서버 중지 (메모리 해제)
        redisServer.stop();
    }
}
