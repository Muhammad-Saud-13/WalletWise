package com.walletwise.Walletwise.service.impl;

import com.walletwise.Walletwise.dto.TransactionDTO;
import com.walletwise.Walletwise.dto.TransactionFilter;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.PageImpl;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepo transactionRepo;
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

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
    public Page<TransactionDTO> getAllTransaction(String currentPrincipalName, Pageable pageable, TransactionFilter transactionFilter) {
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));

        if (transactionFilter.getType() != null) {
            query.addCriteria(Criteria.where("type").is(transactionFilter.getType()));
        }
        if (transactionFilter.getTransactionCategory() != null) {
            query.addCriteria(Criteria.where("transactionCategory").is(transactionFilter.getTransactionCategory()));
        }
        if (transactionFilter.getPaymentMethod() != null) {
            query.addCriteria(Criteria.where("paymentMethod").is(transactionFilter.getPaymentMethod()));
        }
        
        if (transactionFilter.getMinAmount() != null || transactionFilter.getMaxAmount() != null) {
            Criteria amountCriteria = Criteria.where("amount");
            if (transactionFilter.getMinAmount() != null) {
                amountCriteria.gte(transactionFilter.getMinAmount());
            }
            if (transactionFilter.getMaxAmount() != null) {
                amountCriteria.lte(transactionFilter.getMaxAmount());
            }
            query.addCriteria(amountCriteria);
        }

        if (transactionFilter.getFromDate() != null || transactionFilter.getToDate() != null) {
            Criteria dateCriteria = Criteria.where("createdAt");
            if (transactionFilter.getFromDate() != null) {
                dateCriteria.gte(transactionFilter.getFromDate().atStartOfDay());
            }
            if (transactionFilter.getToDate() != null) {
                dateCriteria.lte(transactionFilter.getToDate().atTime(java.time.LocalTime.MAX));
            }
            query.addCriteria(dateCriteria);
        }

        long total = mongoTemplate.count(query, Transaction.class);
        query.with(pageable);
        List<Transaction> transactions = mongoTemplate.find(query, Transaction.class);

        List<TransactionDTO> dtos = transactions.stream().map(this::convertToDTO).toList();
        return new PageImpl<>(dtos, pageable, total);
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
