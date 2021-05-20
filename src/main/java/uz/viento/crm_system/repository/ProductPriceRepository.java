package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.ProductPrice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    Optional<ProductPrice> findByValidAndProduct_Id(boolean valid, UUID product_id);

}

