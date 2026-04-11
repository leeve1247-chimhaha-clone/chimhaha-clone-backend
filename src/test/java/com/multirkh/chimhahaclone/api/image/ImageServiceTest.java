package com.multirkh.chimhahaclone.api.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.multirkh.chimhahaclone.api.image.domain.Image;
import com.multirkh.chimhahaclone.api.image.dtos.PresignedPostDto;
import com.multirkh.chimhahaclone.api.image.dtos.PresignedUrlDTO;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.dto.PostReceived;
import com.multirkh.chimhahaclone.api.post.image.PostImageRepository;
import com.multirkh.chimhahaclone.common.minio.MinioService;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private PostImageRepository postImageRepository;

    @InjectMocks
    private ImageService imageService;

    private static final String FILE_NAME = "test-image.jpg";
    private static final String ENDPOINT_URL = "http://test-export-url/test-bucket";
    private static final String PRESIGNED_URL = "http://presigned-url/test-image.jpg";

    // ─── getPresignedUrl ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getPresignedUrl: 고유한 파일명과 PUT용 presigned URL을 반환한다")
    void getPresignedUrl_shouldReturnPresignedUrlDto() {
        when(imageRepository.findByFileName(anyString())).thenReturn(null);
        when(minioService.getPresignedUrl(anyString())).thenReturn(PRESIGNED_URL);

        PresignedUrlDTO result = imageService.getPresignedUrl();

        assertThat(result.getUrl()).isEqualTo(PRESIGNED_URL);
        assertThat(result.getFileName()).isNotBlank();
        verify(minioService).getPresignedUrl(result.getFileName());
    }

    @Test
    @DisplayName("getPresignedPost: 임시 Image를 저장하고 PresignedPostDto를 반환한다")
    void getPresignedPost_shouldSaveTemporaryImageAndReturnDto() {
        when(imageRepository.findByFileName(anyString())).thenReturn(null);
        when(minioService.getImageEndPointUrl()).thenReturn(ENDPOINT_URL);
        Map<String, String> formFields = new HashMap<>(Map.of("policy", "test-policy"));
        when(minioService.getPresignedPost(anyString())).thenReturn(formFields);
        when(imageRepository.save(any(Image.class))).thenAnswer(i -> i.getArgument(0));

        PresignedPostDto result = imageService.getPresignedPost(
                org.springframework.util.MimeType.valueOf("image/jpeg"));

        assertThat(result.getUrl()).isEqualTo(ENDPOINT_URL);
        assertThat(result.getFields()).containsKey("key");
        assertThat(result.getFields().get("key")).endsWith(".jpeg");
        verify(imageRepository).save(any(Image.class));
    }

    // ─── getSrcUrl ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getSrcUrl: Image가 있고 만료가 충분히 남은 경우 기존 URL을 반환한다")
    void getSrcUrl_whenImageExistsAndNotExpired_shouldReturnExistingUrl() {
        Image image = new Image(FILE_NAME, "image/jpeg", PRESIGNED_URL,
                ZonedDateTime.now().plusHours(168));
        when(imageRepository.findByFileName(FILE_NAME)).thenReturn(image);

        String result = imageService.getSrcUrl(FILE_NAME);

        assertThat(result).isEqualTo(PRESIGNED_URL);
        verify(minioService, never()).createOrRenewUrl(anyString());
    }

    @Test
    @DisplayName("getSrcUrl: Image가 있고 곧 만료되는 경우 URL을 갱신한다")
    void getSrcUrl_whenImageExistsAndSoonExpired_shouldRenewUrl() {
        String newUrl = "http://new-presigned-url";
        Image image = new Image(FILE_NAME, "image/jpeg", PRESIGNED_URL,
                ZonedDateTime.now().minusMinutes(1));
        when(imageRepository.findByFileName(FILE_NAME)).thenReturn(image);
        when(minioService.createOrRenewUrl(FILE_NAME)).thenReturn(newUrl);

        String result = imageService.getSrcUrl(FILE_NAME);

        assertThat(result).isEqualTo(newUrl);
        verify(minioService).createOrRenewUrl(FILE_NAME);
    }

    @Test
    @DisplayName("getSrcUrl: Image가 없는 경우 새 Image를 저장하고 URL을 반환한다")
    void getSrcUrl_whenImageNotExists_shouldCreateNewImageAndReturnUrl() {
        when(imageRepository.findByFileName(FILE_NAME)).thenReturn(null);
        when(minioService.createOrRenewUrl(FILE_NAME)).thenReturn(PRESIGNED_URL);
        when(minioService.getType(FILE_NAME)).thenReturn("image/jpeg");
        when(imageRepository.save(any(Image.class))).thenAnswer(i -> i.getArgument(0));

        String result = imageService.getSrcUrl(FILE_NAME);

        assertThat(result).isEqualTo(PRESIGNED_URL);
        verify(minioService).createOrRenewUrl(FILE_NAME);
        verify(minioService).getType(FILE_NAME);
        verify(imageRepository).save(any(Image.class));
    }

    // ─── getOrCreateThumbnail ─────────────────────────────────────────────────

    @Test
    @DisplayName("getOrCreateThumbnail: 썸네일이 이미 있으면 기존 썸네일을 반환한다")
    void getOrCreateThumbnail_whenThumbnailExists_shouldReturnExistingThumbnail() {
        Image rawImage = new Image(FILE_NAME, "image/jpeg", PRESIGNED_URL,
                ZonedDateTime.now().plusHours(168));
        Image existingThumbnail = new Image(rawImage, "http://thumbnail-url",
                ZonedDateTime.now().plusHours(168));
        rawImage.setThumbNailImage(existingThumbnail);

        Image result = imageService.getOrCreateThumbnail(rawImage);

        assertThat(result).isEqualTo(existingThumbnail);
        verify(minioService, never()).createThumbnail(anyString());
    }

    @Test
    @DisplayName("getOrCreateThumbnail: 썸네일이 없으면 새로 생성하고 저장한다")
    void getOrCreateThumbnail_whenThumbnailNotExists_shouldCreateThumbnail() {
        Image rawImage = new Image(FILE_NAME, "image/jpeg", PRESIGNED_URL,
                ZonedDateTime.now().plusHours(168));
        String thumbnailUrl = "http://thumbnail-url";
        when(minioService.createThumbnail(FILE_NAME)).thenReturn(thumbnailUrl);
        when(imageRepository.save(any(Image.class))).thenAnswer(i -> i.getArgument(0));

        Image result = imageService.getOrCreateThumbnail(rawImage);

        assertThat(result.getUrl()).isEqualTo(thumbnailUrl);
        assertThat(result.getRawImage()).isEqualTo(rawImage);
        verify(minioService).createThumbnail(FILE_NAME);
        verify(imageRepository).save(any(Image.class));
    }

    // ─── createThumbnailImage ─────────────────────────────────────────────────

    @Test
    @DisplayName("createThumbnailImage: content에 이미지가 없으면 아무것도 하지 않는다")
    void createThumbnailImage_whenNoImageInContent_shouldDoNothing() {
        Post post = org.mockito.Mockito.mock(Post.class);
        ObjectNode emptyContent = new ObjectMapper().createObjectNode();
        when(post.getJsonContent()).thenReturn(emptyContent);

        imageService.createThumbnailImage(post);

        verify(minioService, never()).createThumbnail(anyString());
        verify(imageRepository, never()).save(any());
    }

    // ─── updateThumbnailImage ─────────────────────────────────────────────────

    @Test
    @DisplayName("updateThumbnailImage: 기존 썸네일이 있고 새 content에 이미지가 없으면 썸네일을 삭제한다")
    void updateThumbnailImage_whenOldExistsAndNewIsNull_shouldDeleteThumbnail() {
        Post post = org.mockito.Mockito.mock(Post.class);
        Image rawImage = new Image(FILE_NAME, "image/jpeg", PRESIGNED_URL,
                ZonedDateTime.now().plusHours(168));
        Image oldThumbnail = new Image(rawImage, "http://old-thumbnail-url",
                ZonedDateTime.now().plusHours(168));
        when(post.getThumbNailImage()).thenReturn(oldThumbnail);

        PostReceived request = new PostReceived();
        request.setContent(new ObjectMapper().createObjectNode());  // 이미지 없는 content

        imageService.updateThumbnailImage(post, request);

        verify(post).removeThumbNailImage();
        verify(minioService).deleteThumbnail(FILE_NAME);
        verify(imageRepository).delete(oldThumbnail);
    }

    @Test
    @DisplayName("updateThumbnailImage: 기존 썸네일 없고 새 content에 이미지가 있으면 썸네일을 생성한다")
    void updateThumbnailImage_whenOldIsNullAndNewExists_shouldCreateThumbnail() {
        Post post = org.mockito.Mockito.mock(Post.class);
        when(post.getThumbNailImage()).thenReturn(null);

        Image rawImage = new Image(FILE_NAME, "image/jpeg", PRESIGNED_URL,
                ZonedDateTime.now().plusHours(168));
        Image newThumbnail = new Image(rawImage, "http://new-thumbnail-url",
                ZonedDateTime.now().plusHours(168));
        rawImage.setThumbNailImage(newThumbnail);
        when(imageRepository.findByFileName(FILE_NAME)).thenReturn(rawImage);

        PostReceived request = new PostReceived();
        request.setContent(buildImageContentNode(ENDPOINT_URL + "/" + FILE_NAME));

        imageService.updateThumbnailImage(post, request);

        verify(post).addThumbNailImage(newThumbnail);
        verify(minioService, never()).createThumbnail(anyString());
    }

    // ─── deleteThumbnailImage ─────────────────────────────────────────────────

    @Test
    @DisplayName("deleteThumbnailImage: 썸네일이 없으면 아무것도 하지 않는다")
    void deleteThumbnailImage_whenNoThumbnail_shouldDoNothing() {
        Post post = org.mockito.Mockito.mock(Post.class);
        when(post.getThumbNailImage()).thenReturn(null);

        imageService.deleteThumbnailImage(post);

        verify(minioService, never()).deleteThumbnail(anyString());
    }

    @Test
    @DisplayName("deleteThumbnailImage: 썸네일이 있고 다른 포스트에서 사용 안 하면 deleteThumbnail을 호출한다")
    void deleteThumbnailImage_whenThumbnailExistsAndNotUsedElsewhere_shouldDeleteThumbnail() {
        Post post = org.mockito.Mockito.mock(Post.class);
        Image rawImage = new Image(FILE_NAME, "image/jpeg", PRESIGNED_URL,
                ZonedDateTime.now().plusHours(168));
        Image thumbnail = new Image(rawImage, "http://thumbnail-url",
                ZonedDateTime.now().plusHours(168));
        when(post.getThumbNailImage()).thenReturn(thumbnail);

        imageService.deleteThumbnailImage(post);

        verify(post).removeThumbNailImage();
        verify(minioService).deleteThumbnail(FILE_NAME);
    }

    // ─── 헬퍼 ─────────────────────────────────────────────────────────────────

    /** {"type": "image", "src": "<url>"} 형태의 JsonNode를 생성한다 */
    private ObjectNode buildImageContentNode(String srcUrl) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("type", "image");
        node.put("src", srcUrl);
        return node;
    }
}
