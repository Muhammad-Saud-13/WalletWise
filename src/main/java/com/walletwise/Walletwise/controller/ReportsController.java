package com.walletwise.Walletwise.controller;

import com.walletwise.Walletwise.dto.CategoryBreakdownDTO;
import com.walletwise.Walletwise.dto.MonthlySummary;
import com.walletwise.Walletwise.dto.MonthlyComparisonDTO;
import com.walletwise.Walletwise.dto.DailyTrendDTO;
import com.walletwise.Walletwise.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummary> getMonthlySummary(@RequestParam String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(reportService.getMonthlySummary(currentPrincipalName, month));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<List<CategoryBreakdownDTO>> monthlyCategoryBreakdown(@RequestParam String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(reportService.getCategoryBreakdown(currentPrincipalName, month));
    }

    @GetMapping("/monthly-comparison")
    public ResponseEntity<MonthlyComparisonDTO> monthlyComparison(@RequestParam String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(reportService.getMonthlyComparison(currentPrincipalName, month));
    }

    @GetMapping("/top-categories")
    public ResponseEntity<List<CategoryBreakdownDTO>> topCategories(@RequestParam String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(reportService.getTopCategories(currentPrincipalName, month));
    }

    @GetMapping("/daily-trend")
    public ResponseEntity<List<DailyTrendDTO>> dailyTrend(@RequestParam String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(reportService.getDailyTrend(currentPrincipalName, month));
    }

    @GetMapping("/six-month-trend")
    public ResponseEntity<List<MonthlySummary>> sixMonthTrend(@RequestParam String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(reportService.getSixMonthTrend(currentPrincipalName, month));
    }
}
