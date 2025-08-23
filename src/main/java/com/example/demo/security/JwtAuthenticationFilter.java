package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            // DEBUG: Print detailed request information
            System.out.println("=== JWT AUTHENTICATION FILTER DEBUG ===");
            System.out.println("Request URI: " + request.getRequestURI());
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Authorization Header: " + request.getHeader("Authorization"));
            System.out.println("JWT Token present: " + (jwt != null));
            
            String requestURI = request.getRequestURI();
            
            // ✅ UPDATED: Handle endpoints with .permitAll() differently
            if (StringUtils.hasText(jwt)) {
                System.out.println("JWT Token (first 50 chars): " + jwt.substring(0, Math.min(jwt.length(), 50)) + "...");
                
                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromJWT(jwt);
                    System.out.println("✓ JWT validation successful");
                    System.out.println("✓ Extracted username: " + username);
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✓ Authentication set in SecurityContext");
                    System.out.println("✓ User '" + username + "' authenticated successfully");
                    
                } else {
                    System.out.println("❌ JWT token validation failed");
                    System.out.println("❌ Token might be expired, malformed, or invalid");
                    
                    // Clear any existing authentication
                    SecurityContextHolder.clearContext();
                }
                
            } else {
                // ✅ NEW: For permitAll endpoints, create anonymous authentication
                if (isPermitAllEndpoint(requestURI)) {
                    System.out.println("✓ PermitAll endpoint - creating anonymous authentication");
                    
                    // Create anonymous authentication to avoid null pointer
                    UsernamePasswordAuthenticationToken anonymousAuth = 
                        new UsernamePasswordAuthenticationToken("anonymous", null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
                    
                } else {
                    System.out.println("❌ No JWT token found in Authorization header");
                    System.out.println("❌ Expected format: 'Bearer <token>'");
                    
                    // Clear any existing authentication
                    SecurityContextHolder.clearContext();
                }
            }
            
            System.out.println("Current Authentication: " + SecurityContextHolder.getContext().getAuthentication());
            System.out.println("=======================================");
            
        } catch (Exception ex) {
            System.err.println("❌ JWT Authentication Filter Error:");
            System.err.println("Exception type: " + ex.getClass().getSimpleName());
            System.err.println("Exception message: " + ex.getMessage());
            ex.printStackTrace();
            
            // Clear authentication on error
            SecurityContextHolder.clearContext();
            
            logger.error("Could not set user authentication in security context", ex);
        }
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from the Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        System.out.println("Raw Authorization Header: " + bearerToken);
        
        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                System.out.println("Extracted token length: " + token.length());
                return token;
            } else {
                System.out.println("❌ Authorization header doesn't start with 'Bearer '");
            }
        } else {
            System.out.println("❌ Authorization header is null or empty");
        }
        
        return null;
    }
    
    /**
     * Check if the request URI is a public endpoint that doesn't require authentication
     */
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/auth/") || 
               requestURI.startsWith("/api/public/") ||
               requestURI.equals("/error") ||
               requestURI.equals("/") ||
               requestURI.startsWith("/actuator/");
    }
    
    /**
     * ✅ NEW: Check if endpoint is configured as permitAll in SecurityConfig
     */
    private boolean isPermitAllEndpoint(String requestURI) {
        return requestURI.startsWith("/api/sports/schedules") ||
               requestURI.startsWith("/api/sports/records") ||
               requestURI.startsWith("/api/sports/analytics") ||
               requestURI.startsWith("/api/sports/export") ||
               requestURI.startsWith("/api/sports/health");
    }
    
    /**
     * ✅ UPDATED: Don't skip JWT filter for permitAll endpoints anymore
     * We want the filter to run to set anonymous authentication
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        boolean shouldSkip = isPublicEndpoint(requestURI); // Only skip truly public endpoints
        
        if (shouldSkip) {
            System.out.println("=== JWT FILTER SKIPPED ===");
            System.out.println("Request URI: " + requestURI);
            System.out.println("Reason: Public endpoint");
            System.out.println("==========================");
        }
        
        return shouldSkip;
    }
}
