
package com.cashflux.category.controller;

import com.cashflux.category.dto.CategoryRequest;
import com.cashflux.category.dto.CategoryResponse;
import com.cashflux.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        List<CategoryResponse> response =
                categoryService.getAllCategories(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        CategoryResponse response =
                categoryService.getCategoryById(userId, id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        CategoryResponse response =
                categoryService.createCategory(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
