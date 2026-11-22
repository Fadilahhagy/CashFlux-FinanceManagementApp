
package com.cashflux.category.service;

import com.cashflux.category.dto.CategoryRequest;
import com.cashflux.category.dto.CategoryResponse;
import com.cashflux.category.entity.Category;
import com.cashflux.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldReturnAllCategoriesBelongingToUser() {
        Category category = createCategory(
                1L, 10L, "Food", "EXPENSE"
        );

        when(categoryRepository.findAllByUserId(10L))
                .thenReturn(List.of(category));

        List<CategoryResponse> result =
                categoryService.getAllCategories(10L);

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getName());
        assertEquals("EXPENSE", result.get(0).getType());

        verify(categoryRepository).findAllByUserId(10L);
    }

    @Test
    void shouldReturnCategoryWhenItBelongsToUser() {
        Category category = createCategory(
                1L, 10L, "Salary", "INCOME"
        );

        when(categoryRepository.findByIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(category));

        CategoryResponse result =
                categoryService.getCategoryById(10L, 1L);

        assertEquals(1L, result.getId());
        assertEquals("Salary", result.getName());
        assertEquals("INCOME", result.getType());
    }

    @Test
    void shouldThrowExceptionWhenCategoryDoesNotBelongToUser() {
        when(categoryRepository.findByIdAndUserId(1L, 20L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> categoryService.getCategoryById(20L, 1L)
        );
    }

    @Test
    void shouldCreateCategoryForUser() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType("EXPENSE");

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        CategoryResponse result =
                categoryService.createCategory(10L, request);

        assertEquals(1L, result.getId());
        assertEquals("Food", result.getName());
        assertEquals("EXPENSE", result.getType());

        verify(categoryRepository).save(argThat(category ->
                category.getUserId().equals(10L)
                        && category.getName().equals("Food")
                        && category.getType().equals("EXPENSE")
                        && category.getCreatedAt() != null
                        && category.getUpdatedAt() != null
        ));
    }

    private Category createCategory(
            Long id,
            Long userId,
            String name,
            String type
    ) {
        Category category = new Category();
        category.setId(id);
        category.setUserId(userId);
        category.setName(name);
        category.setType(type);
        category.setCreatedAt(OffsetDateTime.now());
        category.setUpdatedAt(OffsetDateTime.now());

        return category;
    }
}