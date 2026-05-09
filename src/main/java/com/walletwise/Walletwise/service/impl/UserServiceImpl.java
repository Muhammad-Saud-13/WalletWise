package com.walletwise.Walletwise.service.impl;

import com.walletwise.Walletwise.dto.RegisterRequest;
import com.walletwise.Walletwise.dto.EditProfileRequest;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.entity.UserProfile;
import com.walletwise.Walletwise.exception.ResourceNotFoundException;
import com.walletwise.Walletwise.exception.UserAlreadyExistsException;
import com.walletwise.Walletwise.repository.UserRepo;
import com.walletwise.Walletwise.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String registerUser(RegisterRequest registerRequest) {
        if (!isUserExist(registerRequest.getEmail())) {
            User user = new User();
            user.setFullName(registerRequest.getFullName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setActive(true);
            user.setRoles(java.util.Arrays.asList("ROLE_USER"));
            
            userRepo.save(user);
            log.info("New user registered with email: {}", user.getEmail());
            return "User registered successfully";
        } else {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
    }

    @Override
    public String editUserProfile(EditProfileRequest editProfileRequest, String currentPrincipalName) {
        User user = userRepo.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        UserProfile existingProfile = user.getProfile();
        if (existingProfile == null) {
            existingProfile = new UserProfile();
            user.setProfile(existingProfile);
        }

        if (editProfileRequest.getCurrencyPreference() != null) {
            existingProfile.setCurrencyPreference(editProfileRequest.getCurrencyPreference());
        }
        if (editProfileRequest.getPhoneNumber() != null) {
            existingProfile.setPhoneNumber(editProfileRequest.getPhoneNumber());
        }
        if (editProfileRequest.getAddress() != null) {
            existingProfile.setAddress(editProfileRequest.getAddress());
        }

        userRepo.save(user);
        log.info("User profile updated for user: {}", currentPrincipalName);
        return "User profile updated successfully";
    }


    public boolean isUserExist(String email){
        return userRepo.findByEmail(email).isPresent();
    }
}
