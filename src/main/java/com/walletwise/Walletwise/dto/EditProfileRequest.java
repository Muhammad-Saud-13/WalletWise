package com.walletwise.Walletwise.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {
    
    // phoneNumber is OPTIONAL - no @NotBlank
    // But IF provided, must match the pattern
    @Pattern(
            regexp = "^03\\d{9}$",
            message = "Invalid Pakistani phone number"
    )
    private String phoneNumber;
    
    // Other fields are also optional
    private String currencyPreference;
    private String address;
}

