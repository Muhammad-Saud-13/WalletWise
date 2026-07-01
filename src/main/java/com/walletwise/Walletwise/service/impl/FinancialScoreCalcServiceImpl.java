package com.walletwise.Walletwise.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletwise.Walletwise.service.FinancialScoreCalcService;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class FinancialScoreCalcServiceImpl implements FinancialScoreCalcService {

    private final ObjectMapper objectMapper;

    public FinancialScoreCalcServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public int computeScore(String summaryJson) {
        JsonNode root;
        try {
            root = objectMapper.readTree(summaryJson);
        } catch (Exception e) {
            return 50; // neutral default if data is unreadable
        }

        double totalIncome = root.path("totalIncome").asDouble(0.0);
        JsonNode expenseNode = root.path("expenseByCategory");

        double totalExpense = 0.0;
        double topCategoryAmount = 0.0;
        int categoryCount = 0;

        if (expenseNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = expenseNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                double amount = entry.getValue().asDouble(0.0);
                totalExpense += amount;
                topCategoryAmount = Math.max(topCategoryAmount, amount);
                categoryCount++;
            }
        }

        if (categoryCount == 0 && totalIncome == 0.0) {
            return 100; // no activity tracked yet, nothing to penalize
        }

        double score = 80.0;

        // --- Primary factor: savings rate (income vs expense) ---
        if (totalIncome > 0) {
            double savingsRate = (totalIncome - totalExpense) / totalIncome;

            if (savingsRate < 0) {
                score -= 35; // spending more than earning
            } else if (savingsRate < 0.10) {
                score -= 20; // saving less than 10% of income
            } else if (savingsRate < 0.20) {
                score -= 10; // below the commonly recommended 20% savings rate
            } else if (savingsRate >= 0.30) {
                score += 10; // healthy savings rate, reward it
            }
        } else if (totalExpense > 0) {
            // No income data available at all but expenses exist - can't judge sustainability
            score -= 15;
        }

        // --- Secondary factor: category concentration ---
        double topShare = topCategoryAmount / Math.max(totalExpense, 1.0);
        if (topShare > 0.5) {
            score -= 15;
        } else if (topShare > 0.35) {
            score -= 5;
        }

        // --- Minor reward for category diversification ---
        if (categoryCount >= 3) {
            score += 5;
        }

        return (int) Math.max(1, Math.min(100, score));
    }
}