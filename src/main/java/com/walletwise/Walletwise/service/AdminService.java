package com.walletwise.Walletwise.service;

import com.walletwise.Walletwise.dto.AdminStatsDto;
import com.walletwise.Walletwise.dto.UserAdminDto;

import java.util.List;

public interface AdminService {
    List<UserAdminDto> getAllUsers();
    void deactivateUser(String id);
    void reactivateUser(String id);
    void makeAdmin(String id);
    AdminStatsDto getPlatformStats();
}

