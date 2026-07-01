package com.walletwise.Walletwise.service.impl;

import com.walletwise.Walletwise.dto.AdminStatsDto;
import com.walletwise.Walletwise.dto.UserAdminDto;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.exception.ResourceNotFoundException;
import com.walletwise.Walletwise.repository.TransactionRepo;
import com.walletwise.Walletwise.repository.UserRepo;
import com.walletwise.Walletwise.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Override
    public List<UserAdminDto> getAllUsers() {
        return userRepo.findAll().stream().map(u -> new UserAdminDto(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getCreatedAt(),
                transactionRepo.countByUser_Id(u.getId()),
                u.isActive(),
                u.getRoles()
        )).collect(Collectors.toList());
    }

    @Override
    public void deactivateUser(String id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        userRepo.save(user);
    }

    @Override
    public void reactivateUser(String id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(true);
        userRepo.save(user);
    }

    @Override
    public void makeAdmin(String id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<String> roles = user.getRoles() == null ? new ArrayList<>() : new ArrayList<>(user.getRoles());
        if (!roles.contains("ROLE_ADMIN")) {
            roles.add("ROLE_ADMIN");
        }
        user.setRoles(roles);
        userRepo.save(user);
    }

    @Override
    public AdminStatsDto getPlatformStats() {
        long totalUsers = userRepo.count();
        long activeUsers = userRepo.findAll().stream().filter(User::isActive).count();
        long deactivatedUsers = totalUsers - activeUsers;
        long totalTransactions = transactionRepo.count();
        return new AdminStatsDto(totalUsers, activeUsers, deactivatedUsers, totalTransactions);
    }
}

