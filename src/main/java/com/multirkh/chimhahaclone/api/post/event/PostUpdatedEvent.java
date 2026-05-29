package com.multirkh.chimhahaclone.api.post.event;

import com.multirkh.chimhahaclone.api.post.domain.Post;
import java.time.Instant;
import java.util.List;

public record PostUpdatedEvent(
    String postId,
    List<String> imageFileNames,
    Instant occurredAt
) {
    public static PostUpdatedEvent from(Post post, List<String> imageFileNames) {
        return new PostUpdatedEvent(
            post.getId().toString(),
            imageFileNames,
            Instant.now()
        );
    }
}
