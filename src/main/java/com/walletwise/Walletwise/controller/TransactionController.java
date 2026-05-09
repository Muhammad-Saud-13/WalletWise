package com.walletwise.Walletwise.controller;

import com.walletwise.Walletwise.dto.TransactionDTO;
import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @GetMapping("/{id}")
    public ResponseEntity<com.walletwise.Walletwise.dto.TransactionDTO> getTransactionById(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        
        return ResponseEntity.ok(transactionService.getTransactionById(id, currentPrincipalName));
    }
    
    @GetMapping("/all")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return ResponseEntity.ok(transactionService.getAllTransaction(currentPrincipalName, pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addTransaction(@RequestBody Transaction transaction){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        transactionService.createTransaction(transaction, currentPrincipalName);
        return ResponseEntity.ok("Transaction added successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        transactionService.deleteTransaction(id, currentPrincipalName);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}
