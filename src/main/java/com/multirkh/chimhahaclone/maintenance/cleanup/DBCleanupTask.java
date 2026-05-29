package com.multirkh.chimhahaclone.maintenance.cleanup;

import com.multirkh.chimhahaclone.api.post.PostRepository;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBCleanupTask {

    private final PostRepository postRepository;

    /**
     * Hard-deletes posts marked DELETED. The PostDeleted event emitted at
     * deletion time drives image cleanup in the image service, so this task no
     * longer touches image data.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanUpPost() {
        List<Post> deletedPosts = postRepository.findAllByStatus_Deleted();
        postRepository.deleteAll(deletedPosts);
    }
}
