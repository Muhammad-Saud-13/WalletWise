package com.walletwise.Walletwise.controller;

import com.walletwise.Walletwise.dto.AiBudgetRecommendation;
import com.walletwise.Walletwise.dto.AiHealthScoreResponse;
import com.walletwise.Walletwise.dto.AiInsightsResponse;
import com.walletwise.Walletwise.service.AiInsightsService;
import com.walletwise.Walletwise.service.FinancialScoreCalcService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiInsightsController {

    private final AiInsightsService aiInsightsService;

    public AiInsightsController(AiInsightsService aiInsightsService) {
        this.aiInsightsService = aiInsightsService;
    }

    @PostMapping("/check")
    public String checkHealth(@RequestBody String prompt){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return aiInsightsService.HealthCheckPrompt(prompt);
    }
    @GetMapping("/financial-insights")
    public ResponseEntity<AiInsightsResponse> getInsights() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(aiInsightsService.getFinancialInsights(currentPrincipalName));
    }

    @GetMapping("/budget-recommendations")
    public ResponseEntity<List<AiBudgetRecommendation>> getRecommendations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(aiInsightsService.getBudgetRecommendations(currentPrincipalName));
    }

    @GetMapping("/health-score")
    public ResponseEntity<Integer> getHealthScore() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(aiInsightsService.getQuickHealthScore(email));
    }

    @GetMapping("/health-score/insights")
    public ResponseEntity<AiHealthScoreResponse> getHealthScoreInsights() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok(aiInsightsService.getFinancialHealthScore(currentPrincipalName));
    }
}
