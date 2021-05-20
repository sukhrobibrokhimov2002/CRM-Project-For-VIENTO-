package uz.viento.crm_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.Service;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {

    Page<Service> findByAvailable(boolean available, Pageable pageable);
}
