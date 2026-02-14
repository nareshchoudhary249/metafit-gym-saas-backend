package com.metafit.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.metafit.entity.master.Tenant;
import com.metafit.enums.TenantStatus;
import com.metafit.repository.master.TenantRepository;
import com.metafit.tenancy.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor to enforce subscription status
 * Blocks access if subscription is expired beyond grace period
 */
@Slf4j
@Component
@Order(2) // Run after TenantFilter
@RequiredArgsConstructor
public class SubscriptionInterceptor extends OncePerRequestFilter {

    private final TenantRepository tenantRepository;
    private final ObjectMapper objectMapper;

    @Value("${subscription.grace-period-days:7}")
    private int gracePeriodDays;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String tenantCode = TenantContext.getTenantId();

        if (tenantCode == null || tenantCode.equals("default")) {
            // No tenant context, let it pass (will be handled by TenantFilter)
            filterChain.doFilter(request, response);
            return;
        }

        // Check subscription status
        Tenant tenant = tenantRepository.findByCode(tenantCode).orElse(null);

        if (tenant == null) {
            log.error("Tenant not found in master DB: {}", tenantCode);
            sendErrorResponse(response, 400, "Invalid tenant");
            return;
        }

        // Check tenant status
        if (!tenant.getStatus().equals(TenantStatus.ACTIVE)) {
            log.warn("Tenant is not active: {} - Status: {}", tenantCode, tenant.getStatus());

            if (tenant.getStatus().equals(TenantStatus.SUSPENDED)) {
                sendErrorResponse(response, 402,
                        "Your subscription has been suspended. Please contact support or renew your subscription.");
            } else {
                sendErrorResponse(response, 403,
                        "Access denied. Account status: " + tenant.getStatus());
            }
            return;
        }

        // TODO: Check subscription expiry (when subscription entity is properly linked)
        // For now, just check tenant status

        log.debug("Subscription check passed for tenant: {}", tenantCode);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Don't filter these endpoints
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/public/") ||
                path.equals("/health") ||
                path.startsWith("/actuator/");
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", status);
        errorBody.put("error", status == 402 ? "Payment Required" : "Forbidden");
        errorBody.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}