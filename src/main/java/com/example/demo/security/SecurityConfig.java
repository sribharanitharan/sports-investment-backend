package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // Define CORS Configuration Source Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (update for production)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173", 
            "http://localhost:8080",
            "https://yourdomain.com",
            "https://sports-investment-app.vercel.app"
        ));
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("=== SECURITY CONFIG LOADING ===");
        System.out.println("CORS Configuration Source: " + corsConfigurationSource());
        System.out.println("===============================");
        
        http
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Disable CSRF for REST APIs
            .csrf(csrf -> csrf.disable())
            
            // Set session management to stateless
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/sports/health").permitAll()  // Allow health check
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/sports/export/**").permitAll()  // Allow export endpoints for testing
                
                // ✅ UPDATED: Allow these endpoints for testing (no authentication required)
                .requestMatchers("/api/sports/schedules/**").permitAll()  // Allow schedules access
                .requestMatchers("/api/sports/records/**").permitAll()    // Allow records access  
                .requestMatchers("/api/sports/analytics/**").permitAll()  // Allow analytics access
                .requestMatchers("/actuator/**").permitAll()              // Allow actuator endpoints
                
                // ✅ CRITICAL FIX: Add debug endpoint to permitted list
                .requestMatchers("/api/debug/**").permitAll()             // Allow debug endpoints
                .requestMatchers("/debug/**").permitAll()                 // Allow debug endpoints (alternative path)
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        System.out.println("✓ Security configuration applied successfully");
        
        return http.build();
    }
}
