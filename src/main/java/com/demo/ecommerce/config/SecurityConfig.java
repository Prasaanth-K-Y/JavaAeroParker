package com.demo.ecommerce.config;

import com.demo.ecommerce.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables role-based authorization (e.g., @PreAuthorize)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    // Inject the AuthenticationProvider BEAN defined in ApplicationConfig.java
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Public endpoints (no authentication needed)
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // 2. Authorization Rules (based on roles)
                        .requestMatchers("/api/items").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("/api/orders").hasAnyRole("ADMIN", "CUSTOMER")
                        
                        // 3. All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                // 4. Set session management to STATELESS (required for JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 5. Set the custom AuthenticationProvider (from ApplicationConfig)
                .authenticationProvider(authenticationProvider)
                
                // 6. Add the JWT filter to run before the standard login filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}