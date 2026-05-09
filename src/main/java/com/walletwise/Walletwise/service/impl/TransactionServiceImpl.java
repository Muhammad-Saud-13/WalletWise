package com.walletwise.Walletwise.service.impl;

import com.walletwise.Walletwise.dto.TransactionDTO;
import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.exception.ResourceNotFoundException;
import com.walletwise.Walletwise.exception.UnauthorizedTransactionException;
import com.walletwise.Walletwise.repository.TransactionRepo;
import com.walletwise.Walletwise.repository.UserRepo;
import com.walletwise.Walletwise.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepo transactionRepo;
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public TransactionDTO getTransactionById(String id, String currentPrincipalName) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(currentPrincipalName)) {
            throw new UnauthorizedTransactionException("Unauthorized access to transaction");
        }

        return convertToDTO(transaction);
    }

    @Override
    public Page<TransactionDTO> getAllTransaction(String currentPrincipalName, Pageable pageable) {
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return transactionRepo.findByUser(user, pageable).map(this::convertToDTO);
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
            transaction.getId(),
            transaction.getType(),
            transaction.getTransactionCategory(),
            transaction.getAmount(),
            transaction.getPaymentMethod(),
            transaction.getDescription(),
            transaction.getDate(),
            transaction.getCreatedAt()
        );
    }

    @Override
    public Transaction createTransaction(Transaction transaction, String currentPrincipalName) {
        // Find the user by their authenticated email
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Associate the user with the transaction
        transaction.setUser(user);
        // Save and return the newly created transaction
        return transactionRepo.save(transaction);
    }
    

    @Override
    public void deleteTransaction(String id, String currentPrincipalName) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(currentPrincipalName)) {
            throw new UnauthorizedTransactionException("Unauthorized access to transaction");
        }

        transactionRepo.delete(transaction);
    }


}
