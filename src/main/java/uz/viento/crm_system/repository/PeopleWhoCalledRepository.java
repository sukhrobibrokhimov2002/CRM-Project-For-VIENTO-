package uz.viento.crm_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.viento.crm_system.entity.PeopleWhoCalled;

import java.util.List;

public interface PeopleWhoCalledRepository extends JpaRepository<PeopleWhoCalled, Long> {


    @Query(value = "select * from people_who_called where when_should_call<CAST( NOW() AS Date )", nativeQuery = true)
    List<PeopleWhoCalled> getMissedPeople();

    @Query(value = "select * from people_who_called where when_should_call=CAST( NOW() AS Date )", nativeQuery = true)
    List<PeopleWhoCalled> getPeopleForCallingToday();

    @Query(value = "select * from people_who_called where when_should_call>CAST( NOW() AS Date );", nativeQuery = true)
    List<PeopleWhoCalled> getShouldCallPeople();

    boolean existsByPhoneNumber(String phoneNumber);

    PeopleWhoCalled findTopByPhoneNumber(String phoneNumber);


}
