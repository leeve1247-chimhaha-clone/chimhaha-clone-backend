package com.multirkh.chimhahaclone.maintenance.cleanup;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.multirkh.chimhahaclone.api.image.ImageRepository;
import com.multirkh.chimhahaclone.api.image.ImageService;
import com.multirkh.chimhahaclone.api.image.domain.Image;
import com.multirkh.chimhahaclone.api.post.PostRepository;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.common.minio.MinioService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
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

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private DBCleanupTask cleanupTask;

    @Test
    @DisplayName("cleanUpOrphanImages: 고아 raw image가 있으면 S3와 DB에서 모두 삭제한다")
    void cleanUpOrphanImages_orphanRawExists_deletesFromS3AndDB() {
        Image raw = new Image("orphan.png", "image/png", "url",
                ZonedDateTime.now().minusHours(72));
        when(imageRepository.findImagesEditedBefore(any(ZonedDateTime.class)))
                .thenReturn(Set.of(raw));

        cleanupTask.cleanUpOrphanImages();

        verify(minioService).deleteImages(Set.of("orphan.png"));
        verify(imageRepository).deleteAllByImages(Set.of(raw));
    }

    @Test
    @DisplayName("cleanUpOrphanImages: thumbnail(rawImage != null)은 정리 대상에서 제외된다")
    void cleanUpOrphanImages_thumbnailExcluded() {
        Image raw = new Image("raw.png", "image/png", "raw-url",
                ZonedDateTime.now().minusHours(72));
        Image thumbnail = new Image(raw, "thumb-url",
                ZonedDateTime.now().minusHours(72));
        when(imageRepository.findImagesEditedBefore(any(ZonedDateTime.class)))
                .thenReturn(Set.of(thumbnail));

        cleanupTask.cleanUpOrphanImages();

        verify(minioService, never()).deleteImages(anySet());
        verify(imageRepository, never()).deleteAllByImages(anySet());
    }

    @Test
    @DisplayName("cleanUpOrphanImages: 고아가 없으면 minio·DB 삭제를 호출하지 않는다")
    void cleanUpOrphanImages_empty_noCalls() {
        when(imageRepository.findImagesEditedBefore(any(ZonedDateTime.class)))
                .thenReturn(Set.of());

        cleanupTask.cleanUpOrphanImages();

        verify(minioService, never()).deleteImages(anySet());
        verify(imageRepository, never()).deleteAllByImages(anySet());
    }

    @Test
    @DisplayName("cleanUpPost: deleted 상태의 post들에 대해 image cascade 후 hard delete 한다")
    void cleanUpPost_deletesImagesAndPosts() {
        Post p1 = org.mockito.Mockito.mock(Post.class);
        Post p2 = org.mockito.Mockito.mock(Post.class);
        when(postRepository.findAllByStatus_Deleted()).thenReturn(List.of(p1, p2));

        cleanupTask.cleanUpPost();

        verify(imageService).deleteThumbnailImage(p1);
        verify(imageService).deletePostImage(p1);
        verify(imageService).deleteThumbnailImage(p2);
        verify(imageService).deletePostImage(p2);
        verify(postRepository).deleteAll(List.of(p1, p2));
    }

    @Test
    @DisplayName("cleanUpPost: deleted post가 없으면 image 정리·삭제도 호출되지 않는다")
    void cleanUpPost_noDeletedPosts_noOp() {
        when(postRepository.findAllByStatus_Deleted()).thenReturn(List.of());

        cleanupTask.cleanUpPost();

        verify(imageService, never()).deletePostImage(org.mockito.ArgumentMatchers.any());
        verify(imageService, never()).deleteThumbnailImage(org.mockito.ArgumentMatchers.any());
        verify(postRepository).deleteAll(List.of());
    }
}
