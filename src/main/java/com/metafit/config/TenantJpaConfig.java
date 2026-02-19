package com.metafit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.metafit.repository",
        excludeFilters = @Filter(type = FilterType.REGEX, pattern = "com\\.metafit\\.repository\\.master\\..*"),
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class TenantJpaConfig {
}
