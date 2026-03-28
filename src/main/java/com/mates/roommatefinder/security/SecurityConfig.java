package com.mates.roommatefinder.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())  // Disable HTTP Basic auth
            .formLogin(form -> form.disable())     // Disable form login
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No sessions
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // Public endpoints
                .requestMatchers("/test/**").permitAll()      // Test endpoints (if any)
                .anyRequest().authenticated()                 // Everything else needs JWT
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
