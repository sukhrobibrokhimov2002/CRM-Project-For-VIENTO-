package uz.viento.crm_system.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.viento.crm_system.entity.User;

import java.util.Optional;
import java.util.UUID;

public class WritingAuditingConfig implements AuditorAware<UUID> {


    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            User employee = (User) authentication.getPrincipal();
            return Optional.of(employee.getId());
        }
        return Optional.empty();
    }
}
