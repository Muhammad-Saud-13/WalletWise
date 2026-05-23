package com.walletwise.Walletwise.service;

import com.walletwise.Walletwise.dto.BudgetStatusDto;
import com.walletwise.Walletwise.entity.Budget;
import com.walletwise.Walletwise.enums.Category;
import com.walletwise.Walletwise.service.impl.BudgetServiceImpl;

import java.util.List;

public interface BudgetService {

    String createBudget(Budget budget, String currentPrincipalName);

    String deleteBudget(String id, String currentPrincipalName);

    List<BudgetStatusDto> getBudgetStatus(String currentPrincipalName, String month);
    // called internally after every new transaction
    void checkBudgetAlert(String currentPrincipalName, Category category, String month);
}
