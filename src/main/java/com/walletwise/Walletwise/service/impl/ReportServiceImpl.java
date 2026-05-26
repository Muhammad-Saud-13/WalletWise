package com.walletwise.Walletwise.service.impl;

import com.walletwise.Walletwise.dto.CategoryBreakdownDTO;
import com.walletwise.Walletwise.dto.MonthlySummary;
import com.walletwise.Walletwise.dto.MonthlyComparisonDTO;
import com.walletwise.Walletwise.dto.DailyTrendDTO;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.enums.TransactionType;
import com.walletwise.Walletwise.repository.UserRepo;
import com.walletwise.Walletwise.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MongoTemplate mongoTemplate;
     
    @Override
    public MonthlySummary getMonthlySummary(String currentPrincipalName, String month) {
        User user= userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> tranxList =getTransactionsForMonth(user, month);
        return buildMonthlySummary(tranxList, month);
    }

    @Override
    public List<CategoryBreakdownDTO> getCategoryBreakdown(String currentPrincipalName, String month) {
        User user= userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

       List<Transaction> tranxList =getTransactionsForMonth(user, month);

        return tranxList.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(Transaction::getTransactionCategory,
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .map(e -> new CategoryBreakdownDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public MonthlyComparisonDTO getMonthlyComparison(String currentPrincipalName, String month) {
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        YearMonth currentYearMonth = YearMonth.parse(month);
        YearMonth lastYearMonth = currentYearMonth.minusMonths(1);

        MonthlySummary currentSummary = buildMonthlySummary(getTransactionsForMonth(user, currentYearMonth.toString()), currentYearMonth.toString());
        MonthlySummary lastSummary = buildMonthlySummary(getTransactionsForMonth(user, lastYearMonth.toString()), lastYearMonth.toString());

        MonthlyComparisonDTO comparison = new MonthlyComparisonDTO();
        comparison.setCurrentMonth(currentSummary);
        comparison.setLastMonth(lastSummary);

        BigDecimal currentIncome = currentSummary.getTotalIncome();
        BigDecimal currentExpense = currentSummary.getTotalExpense();
        BigDecimal lastIncome = lastSummary.getTotalIncome();
        BigDecimal lastExpense = lastSummary.getTotalExpense();

        BigDecimal incomeChange = BigDecimal.ZERO;
        if (lastIncome.compareTo(BigDecimal.ZERO) != 0) {
            incomeChange = currentIncome.subtract(lastIncome).divide(lastIncome, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        } else if (currentIncome.compareTo(BigDecimal.ZERO) > 0) {
            incomeChange = new BigDecimal("100");
        }

        BigDecimal expenseChange = BigDecimal.ZERO;
        if (lastExpense.compareTo(BigDecimal.ZERO) != 0) {
            expenseChange = currentExpense.subtract(lastExpense).divide(lastExpense, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        } else if (currentExpense.compareTo(BigDecimal.ZERO) > 0) {
            expenseChange = new BigDecimal("100");
        }

        comparison.setIncomeChangePercentage(incomeChange);
        comparison.setExpenseChangePercentage(expenseChange);

        return comparison;
    }

    @Override
    public List<CategoryBreakdownDTO> getTopCategories(String currentPrincipalName, String month) {
        List<CategoryBreakdownDTO> allCategories = getCategoryBreakdown(currentPrincipalName, month);
        return allCategories.stream()
                .sorted(Comparator.comparing(CategoryBreakdownDTO::getTotalSpent).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyTrendDTO> getDailyTrend(String currentPrincipalName, String month) {
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> tranxList = getTransactionsForMonth(user, month);

        Map<LocalDate, BigDecimal> dailyExpenses = tranxList.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(Transaction::getDate,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        Map<LocalDate, BigDecimal> dailyIncomes = tranxList.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(Transaction::getDate,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<DailyTrendDTO> trend = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            trend.add(new DailyTrendDTO(
                    date,
                    dailyExpenses.getOrDefault(date, BigDecimal.ZERO),
                    dailyIncomes.getOrDefault(date, BigDecimal.ZERO)
            ));
        }
        return trend;
    }

    @Override
    public List<MonthlySummary> getSixMonthTrend(String currentPrincipalName, String month) {
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        YearMonth endMonth = YearMonth.parse(month);
        List<MonthlySummary> trend = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = endMonth.minusMonths(i);
            List<Transaction> tranxList = getTransactionsForMonth(user, targetMonth.toString());
            trend.add(buildMonthlySummary(tranxList, targetMonth.toString()));
        }
        return trend;
    }

    public List<Transaction> getTransactionsForMonth(User user, String month){
        YearMonth yearMonth = YearMonth.parse(month); // e.g. "2026-05"
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));

        Criteria dateCriteria = Criteria.where("date");
        dateCriteria.gte(start);
        dateCriteria.lte(end);
        query.addCriteria(dateCriteria);

        return mongoTemplate.find(query, Transaction.class);
    }

    private MonthlySummary buildMonthlySummary(List<Transaction> transactions, String month) {

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        return new MonthlySummary(YearMonth.parse(month), totalIncome, totalExpense, netSavings);
    }
}
