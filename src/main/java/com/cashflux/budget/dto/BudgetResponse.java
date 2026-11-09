package com.cashflux.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class BudgetResponse {

    private Long id;
    private Long categoryId;
    private BigDecimal amount;
    private Short month;
    private Short year;
    private OffsetDateTime createdAt;
}