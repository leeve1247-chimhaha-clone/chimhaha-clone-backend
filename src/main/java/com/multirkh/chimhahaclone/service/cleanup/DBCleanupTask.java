package com.multirkh.chimhahaclone.service.cleanup;

import com.multirkh.chimhahaclone.repository.CommentRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBCleanupTask {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    @Scheduled(cron = "0 0 0 * * *")
    public void cleanUpPost(){
        postRepository.deleteAllByStatus_Deleted();
    }
}
