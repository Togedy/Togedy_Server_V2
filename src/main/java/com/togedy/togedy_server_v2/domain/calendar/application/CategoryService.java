package com.togedy.togedy_server_v2.domain.calendar.application;

import com.togedy.togedy_server_v2.domain.calendar.dao.CategoryRepository;
import com.togedy.togedy_server_v2.domain.calendar.dto.PostCategoryRequest;
import com.togedy.togedy_server_v2.domain.calendar.entity.Category;
import com.togedy.togedy_server_v2.domain.user.dto.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void generateCategory(PostCategoryRequest request) {
        User user = new User("dummy", "dummy");
        Category category = new Category(user, request.getCategoryName(), request.getCategoryColor());
        categoryRepository.save(category);
    }


}
