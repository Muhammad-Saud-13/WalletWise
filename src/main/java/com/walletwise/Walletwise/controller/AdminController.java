package com.walletwise.Walletwise.controller;

import com.walletwise.Walletwise.dto.AdminStatsDto;
import com.walletwise.Walletwise.dto.UserAdminDto;
import com.walletwise.Walletwise.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDto>> viewAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable String id){
        adminService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/reactivate")
    public ResponseEntity<Void> reactivateUser(@PathVariable String id){
        adminService.reactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/make-admin")
    public ResponseEntity<Void> makeAdmin(@PathVariable String id){
        adminService.makeAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats(){
        return ResponseEntity.ok(adminService.getPlatformStats());
    }
}

