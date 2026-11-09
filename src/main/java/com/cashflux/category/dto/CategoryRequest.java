package com.cashflux.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Category type is required")
    @Pattern(
            regexp = "INCOME|EXPENSE",
            message = "Category type must be INCOME or EXPENSE"
    )
    private String type;
}
