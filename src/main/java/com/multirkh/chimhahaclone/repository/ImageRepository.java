package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByFileName(String fileName);

    @Query("SELECT i FROM Image i WHERE i.fileName IN :fileNames")
    Set<Image> findByFileNames(Set<String> fileNames);

    @Modifying
    @Query("DELETE FROM Image i WHERE i in :images")
    void deleteAllByImages(Set<Image> images);
}
