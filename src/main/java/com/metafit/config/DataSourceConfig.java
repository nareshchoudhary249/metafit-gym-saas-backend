package com.metafit.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for multi-tenant datasources
 * Master DB for tenant metadata + Dynamic tenant DBs
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.master.url}")
    private String masterUrl;

    @Value("${spring.datasource.master.username}")
    private String masterUsername;

    @Value("${spring.datasource.master.password}")
    private String masterPassword;

    @Value("${tenant.datasource.url-prefix}")
    private String tenantUrlPrefix;

    @Value("${tenant.datasource.username}")
    private String tenantUsername;

    @Value("${tenant.datasource.password}")
    private String tenantPassword;

    /**
     * Master DataSource for tenant management
     */
    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        log.info("Configuring master datasource: {}", masterUrl);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(masterUrl);
        config.setUsername(masterUsername);
        config.setPassword(masterPassword);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("MasterHikariPool");

        return new HikariDataSource(config);
    }

    /**
     * Tenant Routing DataSource (dynamic switching)
     */
    @Bean(name = "tenantDataSource")
    @Primary
    public DataSource tenantDataSource() {
        log.info("Configuring tenant routing datasource");

        return new TenantRoutingDataSource(
                tenantUrlPrefix,
                tenantUsername,
                tenantPassword
        );
    }

    /**
     * Entity Manager Factory for tenant databases
     */
    @Bean(name = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(tenantDataSource());
        em.setPackagesToScan("com.mygymapp.entity"); // Tenant entities

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.jdbc.time_zone", "Asia/Kolkata");

        em.setJpaPropertyMap(properties);

        return em;
    }

    /**
     * Master Entity Manager Factory for master database
     */
    @Bean(name = "masterEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(masterDataSource());
        em.setPackagesToScan("com.mygymapp.entity.master"); // Master entities

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        em.setJpaPropertyMap(properties);

        return em;
    }

    /**
     * Transaction Manager for tenant databases
     */
    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    /**
     * Transaction Manager for master database
     */
    @Bean(name = "masterTransactionManager")
    public PlatformTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }
}