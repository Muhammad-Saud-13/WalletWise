package com.walletwise.Walletwise.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiInsightsResponse {
    private List<String> tips;
    private List<String> anomalyAlerts;
}
