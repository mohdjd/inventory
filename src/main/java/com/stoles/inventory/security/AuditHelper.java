package com.stoles.inventory.security;

import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves the currently authenticated AppUser from the security context.
 * Inject this wherever you need to stamp created_by on an entity.
 */

@Component
@RequiredArgsConstructor
public class AuditHelper {

    private final AppUserRepository userRepo;

    /**
     * Returns the logged-in AppUser, or null if no auth context (e.g. during seeding).
     **/
    public AppUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String username = auth.getName();
        if ("anonymousUser".equals(username)) return null;
        return userRepo.findByUsername(username).orElse(null);
    }

    /**
     * Returns "FullName (@username)" for display, or "System" if no user.
     */
    public static String displayName(AppUser user) {
        if (user == null) return "System";
        return user.getFullName() + " (@" + user.getUsername() + ")";
    }
}