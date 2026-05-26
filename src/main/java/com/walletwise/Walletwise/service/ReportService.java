package com.walletwise.Walletwise.service;


import com.walletwise.Walletwise.dto.CategoryBreakdownDTO;
import com.walletwise.Walletwise.dto.MonthlySummary;
import com.walletwise.Walletwise.dto.MonthlyComparisonDTO;
import com.walletwise.Walletwise.dto.DailyTrendDTO;

import java.util.List;

public interface ReportService {
    public MonthlySummary getMonthlySummary(String currentPrincipalName, String month);

    public List<CategoryBreakdownDTO> getCategoryBreakdown(String currentPrincipalName, String month);

    public MonthlyComparisonDTO getMonthlyComparison(String currentPrincipalName, String month);

    public List<CategoryBreakdownDTO> getTopCategories(String currentPrincipalName, String month);

    public List<DailyTrendDTO> getDailyTrend(String currentPrincipalName, String month);

    public List<MonthlySummary> getSixMonthTrend(String currentPrincipalName, String month);
}
