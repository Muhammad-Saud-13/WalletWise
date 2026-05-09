package com.walletwise.Walletwise.service;

import com.walletwise.Walletwise.dto.RegisterRequest;
import com.walletwise.Walletwise.dto.EditProfileRequest;

public interface UserService {
    String registerUser(RegisterRequest registerRequest);
    String editUserProfile(EditProfileRequest editProfileRequest, String currentPrincipalName);
}
