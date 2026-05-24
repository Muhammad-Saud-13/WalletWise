package com.walletwise.Walletwise.controller;

import com.walletwise.Walletwise.dto.TransactionDTO;
import com.walletwise.Walletwise.dto.TransactionFilter;
import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;

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

    // Endpoint to get all transactions with pagination and filtering
    // e.g: http://localhost:8080/api/transactions/all?page=1&size=2&type=EXPENSE&transactionCategory=HEALTHCARE
    @GetMapping("/all")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            TransactionFilter transactionFilter
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return ResponseEntity.ok(transactionService.getAllTransaction(currentPrincipalName, pageable, transactionFilter));
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

    @GetMapping("/export/csv")
    public void exportTransactionsToCSV(
            @RequestParam(required = true) String fromDate,
            @RequestParam(required = true) String toDate,
            HttpServletResponse response
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions_history.csv\"");

        transactionService.exportTransactionsToCSV(currentPrincipalName, fromDate, toDate, response.getWriter());
    }
}
