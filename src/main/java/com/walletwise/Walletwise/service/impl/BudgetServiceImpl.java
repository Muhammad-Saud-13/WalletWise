package com.walletwise.Walletwise.service.impl;

import com.walletwise.Walletwise.dto.BudgetStatusDto;
import com.walletwise.Walletwise.entity.Budget;
import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.enums.Category;
import com.walletwise.Walletwise.enums.TransactionType;
import com.walletwise.Walletwise.exception.DuplicateBudgetException;
import com.walletwise.Walletwise.exception.ResourceNotFoundException;
import com.walletwise.Walletwise.repository.BudgetRepo;
import com.walletwise.Walletwise.repository.UserRepo;
import com.walletwise.Walletwise.service.BudgetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BudgetRepo budgetRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public String createBudget(Budget budget, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if budget already exists for this category + month
        Optional<Budget> existing = budgetRepo
                .findByUserAndCategoryAndMonth(user, budget.getCategory(), budget.getMonth());

        if (existing.isPresent()) {
            log.warn("Duplicate budget creation attempt for user: {}, category: {}, month: {}", email, budget.getCategory(), budget.getMonth());
            throw new DuplicateBudgetException("Budget for " + budget.getCategory() + " in " + budget.getMonth() + " already exists");
        }

        budget.setUser(user);
        budget.setCreatedAt(LocalDateTime.now());
        budgetRepo.save(budget);
        log.info("Budget Create successfully for user: {}, category: {}, month: {}", email, budget.getCategory(), budget.getMonth());
        return "Budget created successfully";
    }

    @Override
    public String deleteBudget(String id, String currentPrincipalName) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (!budget.getUser().getEmail().equals(currentPrincipalName)) {
            log.warn("Unauthorized budget deletion attempt by user: {} for budget id: {}", currentPrincipalName, id);
            throw new ResourceNotFoundException("Unauthorized access to budget");
        }

        budgetRepo.delete(budget);
        log.info("Budget deleted successfully for user: {}, category: {}, month: {}", currentPrincipalName, budget.getCategory(), budget.getMonth());
        return "Budget deleted successfully";
    }

    @Override
    public List<BudgetStatusDto> getBudgetStatus(String email, String month) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Budget> budgets = budgetRepo.findByUserAndMonth(user, month);
        log.info("Retrieved budgets status for user: {}, month: {}, total budgets: {}", email, month, budgets.size());
        return budgets.stream().map(budget -> {
            // Calculate total spent for this category this month from transactions
            BigDecimal spent = calculateSpentAmount(user, budget.getCategory(), month);
            BigDecimal percentage = BigDecimal.ZERO;
            if (budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
                percentage = spent.multiply(BigDecimal.valueOf(100)).divide(budget.getMonthlyLimit(), 2, RoundingMode.HALF_UP);
            }

            return new BudgetStatusDto(
                    budget.getId(),
                    budget.getCategory(),
                    budget.getMonthlyLimit(),
                    spent,
                    percentage.doubleValue(),
                    month
            );
        }).toList();
    }

    private BigDecimal calculateSpentAmount(User user, Category category, String month) {
        // Parse month string "2026-05" into date range
        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));
        query.addCriteria(Criteria.where("type").is(TransactionType.EXPENSE));
        query.addCriteria(Criteria.where("transactionCategory").is(category));
        query.addCriteria(Criteria.where("date").gte(start).lte(end));

        List<Transaction> transactions = mongoTemplate.find(query, Transaction.class);
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void checkBudgetAlert(String currentPrincipalName, Category category, String month) {
        User user = userRepo.findByEmail(currentPrincipalName).orElseThrow(()->new ResourceNotFoundException("User not found"));

        Optional<Budget> budgetOpt = budgetRepo.findByUserAndCategoryAndMonth(user, category, month);
        if (budgetOpt.isEmpty()) {
            log.info("No budget set for user: {}, category: {}, month: {}", currentPrincipalName, category, month);
            return;// no budget set for this category, skip
        }

        Budget budget = budgetOpt.get();
        BigDecimal spent = calculateSpentAmount(user, category, month);
        BigDecimal percentage = BigDecimal.ZERO;
        if (budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
            percentage = spent.multiply(BigDecimal.valueOf(100)).divide(budget.getMonthlyLimit(), 2, RoundingMode.HALF_UP);
        }

        if (percentage.doubleValue() >= 80) {
            // later: send email alert via SendGrid
            log.info("Budget alert for user: {}, category: {}, month: {} - {}% used", currentPrincipalName, category, month, percentage);
            System.out.println("ALERT: " + category + " budget " + percentage + "% used");
        }
    }
}
