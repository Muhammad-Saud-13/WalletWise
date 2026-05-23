package com.walletwise.Walletwise.dto;

import com.walletwise.Walletwise.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class BudgetStatusDto {
    private String id;
    private Category category;
    private BigDecimal monthlyLimit;
    private BigDecimal spent;
    private Double percentageUsed;
    private String month;
    // constructor, getters
}