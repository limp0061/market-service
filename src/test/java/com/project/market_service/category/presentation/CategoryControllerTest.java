package com.project.market_service.category.presentation;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.category.presentation.dto.CategorySaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    Long parentId;
    Long categoryId;

    @BeforeEach
    void setUp() throws Exception {

        Category parent = Category.create("parent_product");
        categoryRepository.save(parent);
        parentId = parent.getId();

        Category category = Category.create("test_product");
        categoryRepository.save(category);
        categoryId = category.getId();

        parent.addChildCategory(category);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 등록 성공")
    void saveCategory_success() throws Exception {
        CategorySaveRequest request = new CategorySaveRequest("가전제품_test", parentId);

        mockMvc.perform(post("/api/v1/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.parentId").value(parentId))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 등록 실패 - 중복된 이름이 존재할 경우")
    void saveCategory_duplicate_categoryName_fail() throws Exception {
        CategorySaveRequest request = new CategorySaveRequest("test_product", parentId);

        mockMvc.perform(post("/api/v1/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CategoryErrorCode.DUPLICATE_CATEGORY.name()))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("카테고리 등록 실패 - 관리자 권한이 없는 경우")
    void saveCategory_forbidden_fail() throws Exception {
        CategorySaveRequest request = new CategorySaveRequest("일반유저카테고리", null);

        mockMvc.perform(post("/api/v1/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.AUTH_FORBIDDEN.name()))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 수정 성공")
    void updateCategory_success() throws Exception {
        String updateName = "가전제품_업데이트";
        CategorySaveRequest request = new CategorySaveRequest(updateName, parentId);

        mockMvc.perform(put("/api/v1/admin/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.categoryName").value(updateName))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 수정 실패 - 순환 참조")
    void updateCategory_circular_reference_fail() throws Exception {
        String updateName = "가전제품_업데이트2";
        CategorySaveRequest request = new CategorySaveRequest(updateName, categoryId);

        mockMvc.perform(put("/api/v1/admin/category/" + parentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CategoryErrorCode.CATEGORY_CIRCULAR_REFERENCE.name()))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_success() throws Exception {

        mockMvc.perform(delete("/api/v1/admin/category/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }
}