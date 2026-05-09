package com.walletwise.Walletwise.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfile {

    private String phoneNumber;
    private String currencyPreference;
    private String address;

}
