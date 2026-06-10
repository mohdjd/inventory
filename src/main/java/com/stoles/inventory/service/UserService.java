package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.exception.BusinessException;
import com.stoles.inventory.exception.ResourceNotFoundException;
import com.stoles.inventory.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public List<Dtos.UserResponse> findAll() {
        return userRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dtos.UserResponse create(Dtos.UserRequest req) {
        if (userRepo.existsByUsername(req.getUsername()))
            throw new BusinessException("Username already exists: " + req.getUsername());
        AppUser u = AppUser.builder()
                .username(req.getUsername()).password(encoder.encode(req.getPassword()))
                .fullName(req.getFullName()).role(req.getRole()).active(true).build();
        return toResponse(userRepo.save(u));
    }

    @Transactional
    public Dtos.UserResponse toggleActive(Long id) {
        AppUser u = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        u.setActive(!u.isActive());
        return toResponse(userRepo.save(u));
    }

    @Transactional
    public Dtos.UserResponse resetPassword(Long id, String newPassword) {
        AppUser u = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        u.setPassword(encoder.encode(newPassword));
        return toResponse(userRepo.save(u));
    }

    private Dtos.UserResponse toResponse(AppUser u) {
        return Dtos.UserResponse.builder().id(u.getId()).username(u.getUsername())
                .fullName(u.getFullName()).role(u.getRole().name())
                .active(u.isActive()).createdAt(u.getCreatedAt()).build();
    }
}
