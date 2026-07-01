package com.walletwise.Walletwise.service;

import com.walletwise.Walletwise.dto.RegisterRequest;
import com.walletwise.Walletwise.dto.EditProfileRequest;
import com.walletwise.Walletwise.entity.User;

public interface UserService {
    String registerUser(RegisterRequest registerRequest);
    String editUserProfile(EditProfileRequest editProfileRequest, String currentPrincipalName);
    User getUserByEmail(String email);
}
