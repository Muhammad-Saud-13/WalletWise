package com.walletwise.Walletwise.dto;

import com.walletwise.Walletwise.enums.Category;
import com.walletwise.Walletwise.enums.PaymentMethod;
import com.walletwise.Walletwise.enums.TransactionType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class TransactionFilter {
    private TransactionType type;
    private Category transactionCategory;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private PaymentMethod paymentMethod;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fromDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate toDate;
}
