package com.multirkh.chimhahaclone.redis;

import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.repository.PostRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ViewCountService {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final PostRepository postRepository;

    public ViewCountService(RedisTemplate<String, Integer> redisTemplate, PostRepository postRepository) {
        this.redisTemplate = redisTemplate;
        this.postRepository = postRepository;
    }

    // 조회수 증가
    public void incrementViewCount(Long postId) {
        String key = "post:views:" + postId;
        redisTemplate.opsForValue().increment(key);
    }

    // 주기적으로 DB에 조회수를 반영하는 메소드
    @Scheduled(fixedRate = 3000) // 1초마다 실행
    public void syncViewCountsToDB() {
        Set<String> keys = redisTemplate.keys("post:views:*");
        if (keys != null) {
            for (String key : keys) {
                Long contentId = Long.valueOf(key.split(":")[2]);
                Integer viewCount = redisTemplate.opsForValue().get(key);

                if (viewCount != null) {
                    Post post = postRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));
                    post.setViews(post.getViews() + viewCount);
                    postRepository.save(post);
                    redisTemplate.delete(key); // DB에 반영한 후 Redis 에서 제거
                }
            }
        }
    }
}
