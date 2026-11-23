package com.capitec.deportment.appointment.config;

import com.capitec.deportment.appointment.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("JWT Authentication Filter invoked for: {}",
                request.getRequestURI());

        // Step 1: Extract Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: Check Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            log.debug("JWT token not found in request");

            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract token
        String token = authHeader.substring(7);

        try {

            // Step 4: Validate token
            if (!jwtService.isTokenValid(token)) {

                log.warn("Invalid or expired JWT token");

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid or expired JWT token"
                );
                return;
            }

            // Step 5: Extract username
            String username = jwtService.extractUsername(token);

            // Step 6: Set authentication in SecurityContext
            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                java.util.Collections.emptyList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                log.info("JWT authentication successful for user: {}",
                        username);
            }

        } catch (Exception ex) {

            log.warn("JWT authentication failed");

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid JWT token"
            );
            return;
        }

        // Step 7: Continue filter chain
        filterChain.doFilter(request, response);
    }
}