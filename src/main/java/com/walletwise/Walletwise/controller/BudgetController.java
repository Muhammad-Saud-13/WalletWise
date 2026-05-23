package com.walletwise.Walletwise.controller;

import com.walletwise.Walletwise.dto.BudgetStatusDto;
import com.walletwise.Walletwise.entity.Budget;
import com.walletwise.Walletwise.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/create")
    public ResponseEntity<String> createBudget(@RequestBody Budget budget) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        String response = budgetService.createBudget(budget, currentPrincipalName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/status")
    public ResponseEntity<List<BudgetStatusDto>> getBudgetStatus(@RequestParam String month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(budgetService.getBudgetStatus(currentPrincipalName, month));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBudget(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        String response = budgetService.deleteBudget(id, currentPrincipalName);
        return ResponseEntity.ok(response);
    }
}