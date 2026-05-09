package com.walletwise.Walletwise.entity;

import com.walletwise.Walletwise.enums.Category;
import com.walletwise.Walletwise.enums.PaymentMethod;
import com.walletwise.Walletwise.enums.TransactionType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @DBRef
    private User user;
    private TransactionType type;
    private Category transactionCategory;
    private double amount;
    private PaymentMethod paymentMethod;
    private String description;
    private LocalDate date;

    @CreatedDate
    private LocalDateTime createdAt;

}
