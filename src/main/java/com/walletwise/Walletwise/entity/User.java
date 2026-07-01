package com.walletwise.Walletwise.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String fullName;
    private String email;
    private String password;
    private List<String> roles;
    private boolean isActive;
    @CreatedDate
    private LocalDateTime createdAt;
    private UserProfile profile;
}