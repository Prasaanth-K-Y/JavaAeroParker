package com.demo.ecommerce.service;

import com.demo.ecommerce.dto.AccessTokenResponse;
import com.demo.ecommerce.dto.AuthResponse;
import com.demo.ecommerce.dto.LoginRequest;
import com.demo.ecommerce.dto.RefreshTokenRequest;
import com.demo.ecommerce.dto.RegisterRequest;
import com.demo.ecommerce.model.User;
import com.demo.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User();
        user.setName(request.name());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAddress(request.address());
        user.setRole(request.role()); 

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        // If execution reaches here, user is authenticated
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(); 

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AccessTokenResponse refreshToken(RefreshTokenRequest request) {
        // Check if refresh token is blacklisted
        if (jwtService.isTokenBlacklisted(request.refreshToken())) {
            throw new IllegalArgumentException("Refresh token has been invalidated");
        }

        String username = jwtService.extractUsername(request.refreshToken());
        UserDetails userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found in token"));

        if (jwtService.isTokenValid(request.refreshToken(), userDetails)) {
            String newAccessToken = jwtService.generateToken(userDetails);
            return new AccessTokenResponse(newAccessToken);
        } else {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
    }

    public void logout(String accessToken, String refreshToken) {
        // Validate and blacklist access token
        String username = jwtService.extractUsername(accessToken);
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Invalid access token");
        }

        // Check if access token is already blacklisted
        if (jwtService.isTokenBlacklisted(accessToken)) {
            throw new IllegalArgumentException("Access token already invalidated");
        }

        // Add access token to blacklist
        jwtService.blacklistToken(accessToken);

        // Blacklist refresh token if provided
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                // Validate refresh token
                String refreshUsername = jwtService.extractUsername(refreshToken);
                if (refreshUsername != null && !refreshUsername.isEmpty()) {
                    // Check if refresh token is already blacklisted
                    if (!jwtService.isTokenBlacklisted(refreshToken)) {
                        jwtService.blacklistToken(refreshToken);
                    }
                }
            } catch (Exception e) {
                // If refresh token is invalid, just continue (access token is already blacklisted)
                // This ensures logout succeeds even with invalid refresh token
            }
        }
    }
}