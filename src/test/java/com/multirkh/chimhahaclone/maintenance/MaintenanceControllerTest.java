package com.multirkh.chimhahaclone.maintenance;

import static org.mockito.Mockito.verify;

import com.multirkh.chimhahaclone.maintenance.cleanup.DBCleanupTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaintenanceControllerTest {

    @Mock
    private DBCleanupTask cleanupTask;

    @InjectMocks
    private MaintenanceController controller;

    @Test
    @DisplayName("cleanUpOrphanImages: DBCleanupTask로 위임한다")
    void cleanUpOrphanImages_delegatesToTask() {
        controller.cleanUpOrphanImages();

        verify(cleanupTask).cleanUpOrphanImages();
    }
}
