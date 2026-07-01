package com.walletwise.Walletwise.dto;

import lombok.Data;

@Data
public class AiHealthScoreResponse {
    private int score;
    private String explanation;
}
