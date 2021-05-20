package uz.viento.crm_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByAvailable(boolean available);

    List<Product> findAllByExpired(boolean expired);

    boolean existsByNameEngOrNameRuOrNameUz(String nameEng, String nameRu, String nameUz);
}
