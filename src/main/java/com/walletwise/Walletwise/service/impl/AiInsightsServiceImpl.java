package com.walletwise.Walletwise.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletwise.Walletwise.dto.AiBudgetRecommendation;
import com.walletwise.Walletwise.dto.AiHealthScoreResponse;
import com.walletwise.Walletwise.dto.AiInsightsResponse;
import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.enums.TransactionType;
import com.walletwise.Walletwise.repository.TransactionRepo;
import com.walletwise.Walletwise.repository.UserRepo;
import com.walletwise.Walletwise.service.AiInsightsService;
import com.walletwise.Walletwise.service.FinancialScoreCalcService;
import com.walletwise.Walletwise.service.GrokAiService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiInsightsServiceImpl implements AiInsightsService {

    private final TransactionRepo transactionRepo;
    private final GrokAiService grokAiService;
    private final ObjectMapper objectMapper;
    private final UserRepo userRepo;
    private final FinancialScoreCalcService financialScoreCalcService;

    public AiInsightsServiceImpl(TransactionRepo transactionRepo, GrokAiService grokAiService,
                                 ObjectMapper objectMapper, UserRepo userRepo, FinancialScoreCalcService financialScoreCalcService) {
        this.transactionRepo = transactionRepo;
        this.grokAiService = grokAiService;
        this.objectMapper = objectMapper;
        this.userRepo = userRepo;
        this.financialScoreCalcService=financialScoreCalcService;
    }

    @Override
    public String HealthCheckPrompt(String prompt) {
        return grokAiService.generateContent(prompt);
    }

    @Override
    public int getQuickHealthScore(String email) {
        String data = getAggregateTransactionData(email);
        return financialScoreCalcService.computeScore(data);
    }

    @Cacheable(value = "userTransactionAggregation", key = "#email")
    public String getAggregateTransactionData(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Transaction> transactions = transactionRepo.findByUserAndDateAfter(user, thirtyDaysAgo);

        Map<String, Double> expenseByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionCategory().name(),
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ));

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        Map<String, Object> summary = Map.of(
                "totalIncome", totalIncome,
                "expenseByCategory", expenseByCategory
        );

        try {
            return objectMapper.writeValueAsString(summary);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
    private String getCleanJsonFromResponse(String response) {
        if (response.startsWith("```json")) {
            response = response.substring(7);
        } else if (response.startsWith("```")) {
            response = response.substring(3);
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.length() - 3);
        }
        return response.trim();
    }

    @Override
    public AiInsightsResponse getFinancialInsights(String email) {
        String data = getAggregateTransactionData(email);
        String prompt = "You are a highly skilled financial advisor. Analyze this user's last 30 days of financial activity: "
                + data + " (totalIncome is their income, expenseByCategory shows spend per category). "
                + "Provide exactly 3 concise saving tips, considering their income level and savings rate, "
                + "and up to 2 unusual spending alerts if any category amount seems disproportionate. "
                + "Return strictly a JSON object with this shape: {\"tips\": [\"tip1\", \"tip2\"], \"anomalyAlerts\": [\"alert1\"]}. "
                + "Do not use markdown blocks, just raw JSON.";

        String aiResponse = grokAiService.generateContent(prompt);
        try {
            return objectMapper.readValue(getCleanJsonFromResponse(aiResponse), AiInsightsResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to decode AI response", e);
        }
    }

    @Override
    public List<AiBudgetRecommendation> getBudgetRecommendations(String email) {
        String data = getAggregateTransactionData(email);
        String prompt = "You are a financial planner. Analyze this user's last 30 days: " + data
                + " (totalIncome is their income, expenseByCategory shows spend per category). "
                + "Recommend a monthly budget limit per expense category. If total expenses approach or exceed income, "
                + "prioritize recommending cuts in the largest categories. If savings rate is healthy, limits can stay close to current spend. "
                + "Provide strictly a JSON array of objects with shape: [{\"category\": \"FOOD\", \"recommendedLimit\": 150.00, \"reason\": \"Because you spent...\"}]. "
                + "The category must match exactly one of their spent categories. No markdown, raw JSON array only.";

        String aiResponse = grokAiService.generateContent(prompt);
        try {
            return objectMapper.readValue(getCleanJsonFromResponse(aiResponse), new TypeReference<List<AiBudgetRecommendation>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to decode AI response", e);
        }
    }

    @Override
    public AiHealthScoreResponse getFinancialHealthScore(String email) {
        String data = getAggregateTransactionData(email);
        int score = financialScoreCalcService.computeScore(data);

        String prompt = "A user's financial health score is " + score + "/100, computed from this income and spending data: "
                + data + " (totalIncome is their income, expenseByCategory shows spend per category). "
                + "The score primarily reflects their savings rate (income minus expenses, relative to income), "
                + "with a smaller penalty for spending concentrated in one category. "
                + "Write a 2-3 sentence explanation of what's driving this score and one concrete suggestion to improve it. Plain text, no JSON.";

        String explanation = grokAiService.generateContent(prompt);

        AiHealthScoreResponse resp = new AiHealthScoreResponse();
        resp.setScore(score);
        resp.setExplanation(explanation.trim());
        return resp;
    }
}