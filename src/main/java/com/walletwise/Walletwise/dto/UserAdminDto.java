package com.walletwise.Walletwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UserAdminDto {
    private String id;
    private String fullName;
    private String email;
    private LocalDateTime registrationDate;
    private long transactionCount;
    private boolean isActive;
    private List<String> roles;
}
