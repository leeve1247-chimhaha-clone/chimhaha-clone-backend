package com.multirkh.chimhahaclone.api.post.event;

import com.multirkh.chimhahaclone.api.post.domain.Post;
import java.time.Instant;

public record PostCreatedEvent(
    String postId,
    String authorId,
    String categoryKey,
    String title,
    Instant occurredAt
) {
    public static PostCreatedEvent from(Post post) {
        return new PostCreatedEvent(
            post.getId().toString(),
            post.getUser().getUserAuthId(),
            post.getCategory().getKey(),
            post.getTitle(),
            Instant.now()
        );
    }
}
