package com.walletwise.Walletwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyComparisonDTO {
    private MonthlySummary currentMonth;
    private MonthlySummary lastMonth;
    private BigDecimal incomeChangePercentage;
    private BigDecimal expenseChangePercentage;
}

