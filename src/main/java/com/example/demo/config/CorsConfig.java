package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("=== CORS CONFIG LOADING ===");
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (update with your frontend domains)
        List<String> allowedOrigins = Arrays.asList(
            "http://localhost:3000",                               // React default port
            "http://localhost:5173",                               // Vite default port
            "http://localhost:8080",                               // Same origin (for testing)
            "https://sports-investment-frontend.onrender.com",     // Your actual frontend URL
            "https://*.onrender.com"                               // Allow all Render subdomains
        );
        
        configuration.setAllowedOriginPatterns(allowedOrigins);
        
        // Allow all HTTP methods
        List<String> allowedMethods = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        );
        configuration.setAllowedMethods(allowedMethods);
        
        // Allow specific headers (more secure than "*")
        List<String> allowedHeaders = Arrays.asList(
            "Authorization",
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        );
        configuration.setAllowedHeaders(allowedHeaders);
        
        // Expose headers that client can access
        List<String> exposedHeaders = Arrays.asList(
            "Authorization",
            "Content-Type"
        );
        configuration.setExposedHeaders(exposedHeaders);
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        // Register configuration for all API paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/error", configuration); // For error pages
        
        System.out.println("Allowed Origins: " + allowedOrigins);
        System.out.println("Allowed Methods: " + allowedMethods);
        System.out.println("Allowed Headers: " + allowedHeaders);
        System.out.println("Allow Credentials: " + configuration.getAllowCredentials());
        System.out.println("✓ CORS configuration applied successfully");
        System.out.println("============================");
        
        return source;
    }
}
