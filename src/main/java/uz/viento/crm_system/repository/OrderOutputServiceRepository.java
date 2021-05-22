package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.OrderOutputService;

import java.util.List;
import java.util.UUID;

public interface OrderOutputServiceRepository extends JpaRepository<OrderOutputService, UUID> {


}

