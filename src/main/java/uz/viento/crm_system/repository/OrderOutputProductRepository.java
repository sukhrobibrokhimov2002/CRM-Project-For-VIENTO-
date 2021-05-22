package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.OrderOutputProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutputProductRepository extends JpaRepository<OrderOutputProduct, UUID> {


}
