package com.cashflux.category.controller;

import com.cashflux.category.dto.CategoryResponse;
import com.cashflux.category.service.CategoryService;
import com.cashflux.config.SecurityConfig;
import com.cashflux.security.JwtService;
import com.cashflux.common.exception.DuplicateResourceException;
import com.cashflux.common.exception.ResourceNotFoundException;
import com.cashflux.common.exception.GlobalExceptionHandler;
import com.cashflux.category.dto.CategoryRequest;

import org.mockito.ArgumentMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnCategoriesWhenTokenIsValid() throws Exception {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn(1L);

        when(categoryService.getAllCategories(1L))
                .thenReturn(List.of(
                        new CategoryResponse(
                                1L,
                                "Food",
                                "EXPENSE",
                                OffsetDateTime.now()
                        )
                ));

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"));
    }

    @Test
    void shouldCreateCategoryWhenRequestIsValid() throws Exception {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn(1L);

        when(categoryService.createCategory(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(
                new CategoryResponse(
                        1L,
                        "Salary",
                        "INCOME",
                        OffsetDateTime.now()
                )
        );

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Salary",
                                    "type": "INCOME"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Salary"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void shouldReturnBadRequestWhenCategoryNameIsEmpty() throws Exception {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn(1L);

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "type": "INCOME"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn(1L);

        when(categoryService.getCategoryById(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/api/v1/categories/999")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

    @Test
    void shouldReturnConflictWhenCategoryAlreadyExists() throws Exception {
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn(1L);

        when(categoryService.createCategory(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(CategoryRequest.class)
        )).thenThrow(
                new DuplicateResourceException("Category already exists")
        );

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Food",
                                "type": "EXPENSE"
                            }
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Category already exists"));
    }
}