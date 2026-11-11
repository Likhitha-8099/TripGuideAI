
package com.tripmind.backend.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tripmind.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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

        // 1. Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. Check whether Bearer token exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract actual token
        String token = authHeader.substring(7);

        // 4. Validate token
        if (jwtService.isTokenValid(token)
                && SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

            // 5. Extract user ID from token
            Long userId = jwtService.extractUserId(token);

            // 6. Create authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.emptyList()
                    );

            // 7. Store authentication in SecurityContext
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        // 8. Continue request processing
        filterChain.doFilter(request, response);
    }
}
