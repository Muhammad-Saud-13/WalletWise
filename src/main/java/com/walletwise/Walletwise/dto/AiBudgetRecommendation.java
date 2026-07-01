package com.walletwise.Walletwise.dto;

import lombok.Data;
import java.math.BigDecimal;
import com.walletwise.Walletwise.enums.Category;

@Data
public class AiBudgetRecommendation {
    private Category category;
    private BigDecimal recommendedLimit;
    private String reason;
}
