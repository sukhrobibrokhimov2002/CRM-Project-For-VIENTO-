package uz.viento.crm_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class ReturningAuditingUser {

    @Bean
    AuditorAware<UUID> auditorAware() {
        return new WritingAuditingConfig();
    }
}
