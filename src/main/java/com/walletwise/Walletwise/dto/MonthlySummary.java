package com.walletwise.Walletwise.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummary {
    
    YearMonth month;
    BigDecimal totalIncome;
    BigDecimal totalExpense;
    BigDecimal netSavings;
}
