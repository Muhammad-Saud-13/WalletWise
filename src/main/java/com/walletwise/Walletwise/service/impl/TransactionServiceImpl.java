package com.walletwise.Walletwise.service.impl;

import com.walletwise.Walletwise.dto.TransactionDTO;
import com.walletwise.Walletwise.dto.TransactionFilter;
import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.enums.TransactionType;
import com.walletwise.Walletwise.exception.ResourceNotFoundException;
import com.walletwise.Walletwise.exception.UnauthorizedTransactionException;
import com.walletwise.Walletwise.repository.TransactionRepo;
import com.walletwise.Walletwise.repository.UserRepo;
import com.walletwise.Walletwise.service.BudgetService;
import com.walletwise.Walletwise.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.PageImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepo transactionRepo;
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BudgetService budgetService;

    @Override
    public TransactionDTO getTransactionById(String id, String currentPrincipalName) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getEmail().equals(currentPrincipalName)) {
            throw new UnauthorizedTransactionException("Unauthorized access to transaction");
        }
        log.info("Transaction retrieved for user: {}, transaction id: {}", currentPrincipalName, id);
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
            Criteria dateCriteria = Criteria.where("date");
            if (transactionFilter.getFromDate() != null) {
                dateCriteria.gte(transactionFilter.getFromDate());
            }
            if (transactionFilter.getToDate() != null) {
                dateCriteria.lte(transactionFilter.getToDate());
            }
            query.addCriteria(dateCriteria);
        }

        long total = mongoTemplate.count(query, Transaction.class);
        query.with(pageable);
        List<Transaction> transactions = mongoTemplate.find(query, Transaction.class);

        List<TransactionDTO> dtos = transactions.stream().map(this::convertToDTO).toList();
        log.info("Retrieved transactions for user: {}", currentPrincipalName);
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
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        transaction.setUser(user);
        transaction.setCreatedAt(java.time.LocalDateTime.now()); // Manually set createdAt
        Transaction saved = transactionRepo.save(transaction);

        // Check budget alert after every new expense
        if (transaction.getType() == TransactionType.EXPENSE) {
            String month = transaction.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            budgetService.checkBudgetAlert(currentPrincipalName, transaction.getTransactionCategory(), month);
        }
        log.info("Transaction created for user: {}, amount: {}, category: {}, date: {}", currentPrincipalName, transaction.getAmount(), transaction.getTransactionCategory(), transaction.getDate());

        return saved;
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

    @Override
    public void exportTransactionsToCSV(String currentPrincipalName, String fromDate, String toDate, Writer writer) {
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if ((fromDate != null && !fromDate.isEmpty()) || (toDate != null && !toDate.isEmpty())) {
            Criteria dateCriteria = Criteria.where("date");
            if (fromDate != null && !fromDate.isEmpty()) {
                dateCriteria.gte(LocalDate.parse(fromDate, formatter));
            }
            if (toDate != null && !toDate.isEmpty()) {
                dateCriteria.lte(LocalDate.parse(toDate, formatter));
            }
            query.addCriteria(dateCriteria);
        }

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "Type", "Category", "Amount", "Payment Method", "Description", "Date"));
             Stream<Transaction> transactionStream = mongoTemplate.stream(query, Transaction.class)) {

            transactionStream.forEach(transaction -> {
                try {
                    csvPrinter.printRecord(
                            transaction.getId(),
                            transaction.getType(),
                            transaction.getTransactionCategory(),
                            transaction.getAmount(),
                            transaction.getPaymentMethod(),
                            transaction.getDescription(),
                            transaction.getDate()
                    );
                } catch (IOException e) {
                    log.error("Error writing CSV record for transaction {}", transaction.getId(), e);
                }
            });
        } catch (IOException e) {
            log.error("Error while writing CSV file", e);
            throw new RuntimeException("Failed to export data to CSV");
        }
        log.info("Transactions exported successfully to CSV for user: {}, from date: {}, to date: {}", currentPrincipalName, fromDate, toDate);
    }
}
