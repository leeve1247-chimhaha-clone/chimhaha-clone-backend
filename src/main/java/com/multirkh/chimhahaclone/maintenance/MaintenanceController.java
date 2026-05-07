package com.multirkh.chimhahaclone.maintenance;

import com.multirkh.chimhahaclone.maintenance.cleanup.DBCleanupTask;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MaintenanceController {
    private final DBCleanupTask cleanupTask;

    @RolesAllowed("ADMIN")
    @PostMapping("/cleanup-orphan-images")
    public void cleanUpOrphanImages() {
        cleanupTask.cleanUpOrphanImages();
    }
}
