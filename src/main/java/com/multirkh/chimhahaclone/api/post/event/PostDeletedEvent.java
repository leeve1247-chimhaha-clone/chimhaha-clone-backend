package com.multirkh.chimhahaclone.api.post.event;

import com.multirkh.chimhahaclone.api.post.domain.Post;
import java.time.Instant;

public record PostDeletedEvent(
    String postId,
    Instant occurredAt
) {
    public static PostDeletedEvent from(Post post) {
        return new PostDeletedEvent(
            post.getId().toString(),
            Instant.now()
        );
    }
}
