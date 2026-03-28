package com.mates.roommatefinder.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtils.validateJwtToken(token)) {
                    Long userId = jwtUtils.getUserIdFromJwt(token);
                    UserRole role = jwtUtils.getRoleFromJwt(token);
                    
                    // Optional - user might not exist, but token is valid
                    var userOpt = userRepository.findById(userId);
                    
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        
                        // Create authority from role
                        GrantedAuthority authority = new SimpleGrantedAuthority(role.getAuthority());
                        
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.warn("User not found for ID: {}", userId);
                    }
                } else {
                    log.warn("Invalid JWT token");
                }
            }
        } catch (Exception e) {
            log.error("JWT filter error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}