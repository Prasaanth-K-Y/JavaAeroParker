package com.demo.ecommerce.service;

import com.demo.ecommerce.dto.AuthResponse;
import com.demo.ecommerce.dto.LoginRequest;
import com.demo.ecommerce.dto.RefreshTokenRequest;
import com.demo.ecommerce.dto.RegisterRequest;
import com.demo.ecommerce.model.Role;
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

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.refreshToken());
        UserDetails userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found in token"));

        if (jwtService.isTokenValid(request.refreshToken(), userDetails)) {
            String newAccessToken = jwtService.generateToken(userDetails);
            // Re-issue a new refresh token (optional, but good for security)
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);
            return new AuthResponse(newAccessToken, newRefreshToken);
        } else {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
    }
}