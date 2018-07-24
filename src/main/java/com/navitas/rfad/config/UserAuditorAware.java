package com.navitas.rfad.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(getUserId());
    }

    private String getUserId() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return "system";
        }

        if (auth.getPrincipal() instanceof String) {
            return (String) auth.getPrincipal();
        } else {
            return auth.getPrincipal().toString();
        }
	}
}
