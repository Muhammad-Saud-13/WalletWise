package com.walletwise.Walletwise.entity;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private UserProfile profile;
}