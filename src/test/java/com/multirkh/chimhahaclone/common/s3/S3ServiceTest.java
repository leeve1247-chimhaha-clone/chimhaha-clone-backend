package com.multirkh.chimhahaclone.common.s3;

import com.multirkh.chimhahaclone.api.image.dtos.PresignedPostDto;
import com.multirkh.chimhahaclone.api.image.resize.ImageResizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PostObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPostRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Client s3Client;

    @Mock
    private ImageResizerService imageResizerService;

    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(imageResizerService, s3Client, s3Presigner);
        ReflectionTestUtils.setField(s3Service, "s3BucketName", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "thumbnailBucketName", "test-thumbnail-bucket");
        ReflectionTestUtils.setField(s3Service, "exportUrl", "http://localhost:9000");
    }

    @Test
    @DisplayName("getPresignedPost - S3Presigner가 15분 duration으로 호출된다")
    void getPresignedPost_callsPresignerWith15MinuteDuration() throws MalformedURLException {
        // given
        PresignedPostRequest mockPresigned = mock(PresignedPostRequest.class);
        when(mockPresigned.url()).thenReturn(new URL("http://localhost:9000/test-bucket"));
        when(mockPresigned.signedFields()).thenReturn(Map.of(
                "x-amz-algorithm", "AWS4-HMAC-SHA256",
                "policy", "base64encodedpolicy",
                "x-amz-signature", "testsignature"
        ));
        when(s3Presigner.presignPostObject(any(PostObjectPresignRequest.class))).thenReturn(mockPresigned);

        // when
        PresignedPostRequest result = s3Service.getPresignedPost("/test-file.jpg");

        // then: S3Presigner가 15분 duration으로 호출됐는지 검증
        ArgumentCaptor<PostObjectPresignRequest> captor = ArgumentCaptor.forClass(PostObjectPresignRequest.class);
        verify(s3Presigner).presignPostObject(captor.capture());
        assertThat(captor.getValue().signatureDuration()).isEqualTo(Duration.ofMinutes(15));

        // then: 반환값 검증
        assertThat(result.url().toString()).isEqualTo("http://localhost:9000/test-bucket");
        assertThat(result.signedFields()).containsKey("x-amz-algorithm");
    }

    @Test
    @DisplayName("getPresignedPost - 올바른 버킷과 key로 요청된다")
    void getPresignedPost_usesCorrectBucketAndKey() throws MalformedURLException {
        // given
        PresignedPostRequest mockPresigned = mock(PresignedPostRequest.class);
        when(mockPresigned.url()).thenReturn(new URL("http://localhost:9000/test-bucket"));
        when(mockPresigned.signedFields()).thenReturn(Map.of());
        when(s3Presigner.presignPostObject(any(PostObjectPresignRequest.class))).thenReturn(mockPresigned);

        // when
        s3Service.getPresignedPost("/test-file.jpg");

        // then
        ArgumentCaptor<PostObjectPresignRequest> captor = ArgumentCaptor.forClass(PostObjectPresignRequest.class);
        verify(s3Presigner).presignPostObject(captor.capture());
        assertThat(captor.getValue().postObjectRequest().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().postObjectRequest().key()).isEqualTo("/test-file.jpg");
    }

    @Test
    @DisplayName("PresignedPostDto - unmodifiable Map을 넘겨도 key 필드가 추가된다")
    void presignedPostDto_handlesUnmodifiableMap() {
        // given: AWS SDK v2의 signedFields()는 unmodifiable Map을 반환
        Map<String, String> unmodifiableFields = Collections.unmodifiableMap(
                Map.of("policy", "abc", "x-amz-signature", "sig123")
        );

        // when & then: 예외 없이 생성되고 key 필드가 포함되어야 함
        assertThatNoException().isThrownBy(() -> {
            PresignedPostDto dto = new PresignedPostDto(
                    "/test.jpg",
                    "http://localhost:9000/test-bucket",
                    unmodifiableFields
            );
            assertThat(dto.getFields()).containsKey("key");
            assertThat(dto.getFields().get("key")).isEqualTo("/test.jpg");
            assertThat(dto.getFields()).containsKey("policy");
            assertThat(dto.getUrl()).isEqualTo("http://localhost:9000/test-bucket");
        });
    }

    @Test
    @DisplayName("getImageEndPointUrl - export URL과 버킷명이 결합된 경로를 반환한다")
    void getImageEndPointUrl_returnsCombinedPath() {
        assertThat(s3Service.getImageEndPointUrl()).isEqualTo("http://localhost:9000/test-bucket");
    }
}
