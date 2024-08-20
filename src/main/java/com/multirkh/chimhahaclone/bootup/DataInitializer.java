package com.multirkh.chimhahaclone.bootup;

import com.multirkh.chimhahaclone.category.MAJOR_CATEGORY;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.category.repository.CategoryRepository;
import com.multirkh.chimhahaclone.category.subCategory.HOBBY_CATEGORY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.multirkh.chimhahaclone.category.MAJOR_CATEGORY.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Autowired
    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 데이터베이스에 초기 데이터 추가
        for (MAJOR_CATEGORY majorCategory : MAJOR_CATEGORY.values()) {
            if (categoryRepository.findByName(majorCategory.name()) == null) {
                categoryRepository.save(new PostCategory(majorCategory));
            }
        }

        for (HOBBY_CATEGORY hobbyCategory : HOBBY_CATEGORY.values()) {
            if (categoryRepository.findByName(hobbyCategory.name()) == null) {
                categoryRepository.save(new PostCategory(hobbyCategory, categoryRepository.findByName(Values.HOBBY)));
            }
        }
    }
}

