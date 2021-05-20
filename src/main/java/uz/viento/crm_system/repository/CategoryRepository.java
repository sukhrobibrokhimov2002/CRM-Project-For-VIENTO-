package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
