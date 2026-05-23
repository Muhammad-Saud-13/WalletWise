package com.walletwise.Walletwise.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.walletwise.Walletwise.enums.Category;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "budgets")
public class Budget {
    @Id
    private String id;

    @DBRef
    private User user;

    private Category category; // reuse your existing enum

    private BigDecimal monthlyLimit;

    private String month; // format: "2026-05"

    @CreatedDate
    private LocalDateTime createdAt;
}
