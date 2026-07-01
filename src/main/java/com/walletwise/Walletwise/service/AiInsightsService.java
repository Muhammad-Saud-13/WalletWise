package com.walletwise.Walletwise.service;

import com.walletwise.Walletwise.dto.AiBudgetRecommendation;
import com.walletwise.Walletwise.dto.AiHealthScoreResponse;
import com.walletwise.Walletwise.dto.AiInsightsResponse;

import java.util.List;

public interface AiInsightsService {
    public int getQuickHealthScore(String email);
    AiInsightsResponse getFinancialInsights(String email);
    List<AiBudgetRecommendation> getBudgetRecommendations(String email);
    AiHealthScoreResponse getFinancialHealthScore(String email);
    String HealthCheckPrompt(String prompt);
}
