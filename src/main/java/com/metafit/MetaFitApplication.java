package com.metafit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

import java.util.TimeZone;

/**
 * MetaFit - Multi-Tenant Gym Management System
 *
 * Main Spring Boot Application class that bootstraps the entire application.
 *
 * Features:
 * - Multi-tenant architecture with database-per-tenant
 * - JWT-based authentication
 * - Role-based access control
 * - Scheduled tasks for membership expiry checks
 * - Async processing support
 *
 * @author MetaFit Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@Slf4j
public class MetaFitApplication {

    public static void main(String[] args) {
        // Set default timezone to IST (Indian Standard Time)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

        log.info("=================================================================");
        log.info("          Starting MetaFit Gym Management System                 ");
        log.info("                    Version 1.0.0                                ");
        log.info("=================================================================");
        log.info("Default Timezone: {}", TimeZone.getDefault().getID());

        SpringApplication.run(MetaFitApplication.class, args);

        log.info("=================================================================");
        log.info("       MetaFit Application Started Successfully! 🎉              ");
        log.info("=================================================================");
    }

    /**
     * Password Encoder Bean
     * Uses BCrypt with strength 10 for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Initializing BCrypt Password Encoder with strength 10");
        return new BCryptPasswordEncoder(10);
    }

    /**
     * CORS Configuration
     * Configures Cross-Origin Resource Sharing for frontend access
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                log.info("Configuring CORS mappings");
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "X-Tenant-ID")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}