package com.multirkh.chimhahaclone.maintenance.cleanup;

import com.multirkh.chimhahaclone.api.image.ImageRepository;
import com.multirkh.chimhahaclone.api.image.domain.Image;
import com.multirkh.chimhahaclone.api.post.PostRepository;
import com.multirkh.chimhahaclone.common.minio.MinioService;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBCleanupTask {
    static final long ORPHAN_IMAGE_GRACE_HOURS = 48;

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final MinioService minioService;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanUpPost() {
        postRepository.deleteAllByStatus_Deleted();
    }

    /**
     * Removes raw images that have not been referenced by any post for at
     * least {@link #ORPHAN_IMAGE_GRACE_HOURS} hours. Thumbnails (rawImage != null)
     * are skipped here — they live and die with their raw image.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void cleanUpOrphanImages() {
        ZonedDateTime threshold = ZonedDateTime.now().minusHours(ORPHAN_IMAGE_GRACE_HOURS);
        Set<Image> orphans = imageRepository.findImagesEditedBefore(threshold).stream()
                .filter(image -> image.getRawImage() == null)
                .collect(Collectors.toSet());
        if (orphans.isEmpty()) {
            return;
        }
        Set<String> fileNames = orphans.stream()
                .map(Image::getFileName)
                .collect(Collectors.toSet());
        minioService.deleteImages(fileNames);
        imageRepository.deleteAllByImages(orphans);
        log.info("[CLEANUP] removed {} orphan image(s)", orphans.size());
    }
}
