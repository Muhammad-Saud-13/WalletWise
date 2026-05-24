package com.walletwise.Walletwise.service;

import com.walletwise.Walletwise.dto.TransactionDTO;
import com.walletwise.Walletwise.dto.TransactionFilter;
import com.walletwise.Walletwise.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Writer;
import java.util.List;

public interface TransactionService {
    public com.walletwise.Walletwise.dto.TransactionDTO getTransactionById(String id, String currentPrincipalName);
    public Transaction createTransaction(Transaction transaction, String currentPrincipalName);
    public Page<TransactionDTO> getAllTransaction(String currentPrincipalName, Pageable pageable, TransactionFilter transactionFilter);
    public void deleteTransaction(String id, String currentPrincipalName);

    void exportTransactionsToCSV(String currentPrincipalName, String fromDate, String toDate, java.io.Writer writer);
}
