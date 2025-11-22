package com.metafit.security.filters;

import com.metafit.tenancy.TenantContext;
import com.metafit.tenancy.TenantIdentifierResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that runs before authentication to set up tenant context
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final TenantIdentifierResolver tenantResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String tenantId = request.getHeader("X-Tenant-ID");

            // Allow public endpoints without tenant
            String requestURI = request.getRequestURI();
            if (isPublicEndpoint(requestURI)) {
                log.debug("Public endpoint accessed: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            if (tenantId == null || tenantId.trim().isEmpty()) {
                log.warn("Missing tenant header for: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"X-Tenant-ID header is required\"}");
                return;
            }

            TenantContext.setTenantId(tenantId);
            log.debug("Tenant context set: {} for request: {}", tenantId, requestURI);

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
            log.debug("Tenant context cleared");
        }
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/") ||
                uri.startsWith("/api/public/") ||
                uri.equals("/health") ||
                uri.equals("/actuator/health");
    }
}