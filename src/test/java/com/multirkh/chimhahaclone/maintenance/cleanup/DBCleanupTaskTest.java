package com.multirkh.chimhahaclone.maintenance.cleanup;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.multirkh.chimhahaclone.api.post.PostRepository;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DBCleanupTaskTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private DBCleanupTask cleanupTask;

    @Test
    @DisplayName("cleanUpPost: deleted 상태의 post들을 hard delete 한다")
    void cleanUpPost_deletesPosts() {
        Post p1 = org.mockito.Mockito.mock(Post.class);
        Post p2 = org.mockito.Mockito.mock(Post.class);
        when(postRepository.findAllByStatus_Deleted()).thenReturn(List.of(p1, p2));

        cleanupTask.cleanUpPost();

        verify(postRepository).deleteAll(List.of(p1, p2));
    }

    @Test
    @DisplayName("cleanUpPost: deleted post가 없으면 빈 목록으로 deleteAll 한다")
    void cleanUpPost_noDeletedPosts_noOp() {
        when(postRepository.findAllByStatus_Deleted()).thenReturn(List.of());

        cleanupTask.cleanUpPost();

        verify(postRepository).deleteAll(List.of());
    }
}
