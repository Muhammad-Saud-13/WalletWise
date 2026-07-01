package com.walletwise.Walletwise.service;

public interface FinancialScoreCalcService {
    int computeScore(String categoryTotalsJson);
}
