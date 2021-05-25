package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.viento.crm_system.entity.OrderOutputProduct;
import uz.viento.crm_system.entity.enums.OrderOutputStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutputProductRepository extends JpaRepository<OrderOutputProduct, UUID> {

    @Query(value = "select * from order_output_product where order_id:=orderId", nativeQuery = true)
    List<OrderOutputProduct> findAllByOrderId(UUID orderId);

    List<OrderOutputProduct> findAllByOrderOutputStatus(OrderOutputStatus orderOutputStatus);

}
