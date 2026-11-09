package com.cashflux.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private Long categoryId;
    private String type;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private OffsetDateTime createdAt;
}