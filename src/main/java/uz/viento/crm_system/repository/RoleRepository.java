package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.Roles;
import uz.viento.crm_system.entity.enums.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRoleName(RoleName roleName);
}
