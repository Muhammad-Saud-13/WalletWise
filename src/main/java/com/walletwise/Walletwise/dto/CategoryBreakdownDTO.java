package com.walletwise.Walletwise.dto;

import com.walletwise.Walletwise.enums.Category;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBreakdownDTO {

    private Category category;

    private BigDecimal totalSpent;
}