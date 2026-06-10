package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.repository.AppUserRepository;
import com.stoles.inventory.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final AppUserRepository userRepo;

    public Dtos.AuthResponse login(Dtos.LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())

        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtUtils.generateToken(auth);
        AppUser user = userRepo.findByUsername(req.getUsername()).orElseThrow();
        return Dtos.AuthResponse.builder()
                .token(token).username(user.getUsername())
                .fullName(user.getFullName()).role(user.getRole().name())
                .build();
    }
}
