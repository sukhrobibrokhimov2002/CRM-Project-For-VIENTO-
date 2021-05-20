package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.CurrencyType;

public interface CurrencyRepository extends JpaRepository<CurrencyType, Long> {
    boolean existsByAbbreviation(String abbreviation);

    boolean existsByAbbreviationAndIdNot(String abbreviation, Long id);

}
