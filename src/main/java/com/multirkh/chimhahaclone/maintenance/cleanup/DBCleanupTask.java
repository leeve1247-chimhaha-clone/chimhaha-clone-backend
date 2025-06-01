package com.multirkh.chimhahaclone.maintenance.cleanup;

import com.multirkh.chimhahaclone.api.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBCleanupTask {
    private final PostRepository postRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanUpPost(){
        postRepository.deleteAllByStatus_Deleted();
    }
}
