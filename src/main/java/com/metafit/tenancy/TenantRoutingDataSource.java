package com.metafit.tenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic routing datasource that switches database connections based on tenant context
 */
@Slf4j
@Component
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> tenantDataSources = new HashMap<>();
    private final String datasourceUrlPrefix;
    private final String datasourceUsername;
    private final String datasourcePassword;

    public TenantRoutingDataSource(
            String urlPrefix,
            String username,
            String password) {
        this.datasourceUrlPrefix = urlPrefix;
        this.datasourceUsername = username;
        this.datasourcePassword = password;

        setTargetDataSources(tenantDataSources);
        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        log.debug("Determining datasource lookup key for tenant: {}", tenantId);
        return tenantId;
    }

    /**
     * Dynamically creates and registers a new tenant datasource
     */
    public void addTenant(String tenantCode, String dbName) {
        if (tenantDataSources.containsKey(tenantCode)) {
            log.info("Tenant datasource already exists: {}", tenantCode);
            return;
        }

        log.info("Creating new datasource for tenant: {} -> {}", tenantCode, dbName);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(datasourceUrlPrefix + dbName);
        config.setUsername(datasourceUsername);
        config.setPassword(datasourcePassword);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("HikariPool-" + tenantCode);

        DataSource dataSource = new HikariDataSource(config);
        tenantDataSources.put(tenantCode, dataSource);

        // Refresh the routing datasource
        setTargetDataSources(tenantDataSources);
        afterPropertiesSet();

        log.info("Successfully added tenant datasource: {}", tenantCode);
    }

    /**
     * Removes a tenant datasource
     */
    public void removeTenant(String tenantCode) {
        Object dataSourceObj = tenantDataSources.remove(tenantCode);

        if (dataSourceObj instanceof HikariDataSource hikariDataSource) {
            log.info("Closing datasource for tenant: {}", tenantCode);
            hikariDataSource.close();
        }

        setTargetDataSources(tenantDataSources);
        afterPropertiesSet();
    }

    public boolean tenantExists(String tenantCode) {
        return tenantDataSources.containsKey(tenantCode);
    }
}