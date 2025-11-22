package com.metafit.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class RevenueReportResponse {
    private BigDecimal totalRevenue;
    private Long transactionCount;
    private Map<String, BigDecimal> breakdown; // By payment method
}