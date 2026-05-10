package com.walletwise.Walletwise.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.walletwise.Walletwise.enums.Category;
import com.walletwise.Walletwise.enums.PaymentMethod;
import com.walletwise.Walletwise.enums.TransactionType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
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
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @CreatedDate
    private LocalDateTime createdAt;

}
