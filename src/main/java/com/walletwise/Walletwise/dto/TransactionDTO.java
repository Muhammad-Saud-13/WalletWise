package com.walletwise.Walletwise.dto;

import com.walletwise.Walletwise.enums.Category;
import com.walletwise.Walletwise.enums.PaymentMethod;
import com.walletwise.Walletwise.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    
    private String id;
    private TransactionType type;
    private Category transactionCategory;
    private double amount;
    private PaymentMethod paymentMethod;
    private String description;
    private LocalDate date;
    private LocalDateTime createdAt;
}

