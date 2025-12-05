package com.multirkh.chimhahaclone.common.redis;

import com.multirkh.chimhahaclone.api.post.PostRepository;
import com.multirkh.chimhahaclone.api.post.PostService;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostViewScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;

    private static final String COUNT_KEY_PREFIX = "post:view:";
    private static final String PENDING_SET_KEY = "post:view:pending";

    // 조회수 증가
    public void incrementViewCount(Long postId) {
        String key = "post:views:" + postId;
        // log.info("key = {}", key);
        redisTemplate.opsForValue().increment(key);
    }

    // 주기적으로 DB에 조회수를 반영하는 메소드
    @Scheduled(fixedRate = 3000) // 30초마다 실행
    @Transactional
    public void syncViewCountsToDB() {
        // log.info("syncViewCountsToDB() 실행");
        List<String> targetPostIds = redisTemplate.opsForSet().pop(PENDING_SET_KEY, 1000L);
        if (targetPostIds == null || targetPostIds.isEmpty()) {
            return;
        }

        for (String ids : targetPostIds) {
            Long contentId = Long.valueOf(ids);
            String countKey = COUNT_KEY_PREFIX + contentId;
            String viewCountStr = redisTemplate.opsForValue().getAndDelete(countKey);
            if (viewCountStr != null) {
                Post post = postRepository.findById(contentId).orElse(null);
                if (post == null) {
                    redisTemplate.delete(key);
                    continue;
                }
                post.setViews(post.getViews() + viewCount);
                postRepository.save(post);
                redisTemplate.delete(key); // DB에 반영한 후 Redis 에서 제거
            }
        }
    }
}
