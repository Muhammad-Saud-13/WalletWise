package com.walletwise.Walletwise.controller;

import com.walletwise.Walletwise.entity.UserProfile;
import com.walletwise.Walletwise.dto.EditProfileRequest;
import com.walletwise.Walletwise.entity.UserProfile;
import com.walletwise.Walletwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class    UserController {

    @Autowired
    private UserService userService;


    @PutMapping("edit-profile")
    public ResponseEntity<String> updateUserProfilePut(@Valid @RequestBody EditProfileRequest editProfileRequest) {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        String response = userService.editUserProfile(editProfileRequest, currentPrincipalName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
