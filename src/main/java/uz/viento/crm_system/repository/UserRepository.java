package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID id);

    boolean existsByAdditionalPhoneNumber(String additionalPhoneNumber);


}
