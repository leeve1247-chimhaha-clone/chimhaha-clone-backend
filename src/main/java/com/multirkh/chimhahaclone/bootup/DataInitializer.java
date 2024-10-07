package com.multirkh.chimhahaclone.bootup;

import com.multirkh.chimhahaclone.category.MAJOR_CATEGORY;
import com.multirkh.chimhahaclone.category.entity.PostCategory;
import com.multirkh.chimhahaclone.category.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.category.subCategory.HOBBY_CATEGORY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.multirkh.chimhahaclone.category.MAJOR_CATEGORY.Values.HOBBY;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PostCategoryRepository postCategoryRepository;

    @Autowired
    public DataInitializer(PostCategoryRepository postCategoryRepository) {
        this.postCategoryRepository = postCategoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 데이터베이스에 초기 데이터 추가
        for (MAJOR_CATEGORY majorCategory : MAJOR_CATEGORY.values()) {
            if (postCategoryRepository.findByName(majorCategory.name()) == null) {
                postCategoryRepository.save(new PostCategory(majorCategory));
            }
        }

        for (HOBBY_CATEGORY hobbyCategory : HOBBY_CATEGORY.values()) {
            if (postCategoryRepository.findByName(hobbyCategory.name()) == null) {
                postCategoryRepository.save(new PostCategory(hobbyCategory, postCategoryRepository.findByName(HOBBY)));
            }
        }
    }
}

