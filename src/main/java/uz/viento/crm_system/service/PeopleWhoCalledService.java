package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.entity.PeopleWhoCalled;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.entity.enums.Status;
import uz.viento.crm_system.payload.PeopleWhoCalledDto;
import uz.viento.crm_system.payload.ReqPeopleWhoCalledToChangePhnOrDate;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.repository.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PeopleWhoCalledService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    PeopleWhoCalledRepository peopleWhoCalledRepository;

    public ResponseApi add(PeopleWhoCalledDto peopleWhoCalledDto) {
        List<uz.viento.crm_system.entity.Service> serviceList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        if (peopleWhoCalledDto.getServiceId() != null) {
            serviceList = serviceRepository.findAllById(peopleWhoCalledDto.getServiceId());
        }
        if (peopleWhoCalledDto.getProductId() != null) {
            productList = productRepository.findAllById(peopleWhoCalledDto.getProductId());
        }

        PeopleWhoCalled peopleWhoCalled = new PeopleWhoCalled();

        if (peopleWhoCalledDto.getUserId() != null) {
            Optional<User> optionalUser = userRepository.findByPhoneNumber(peopleWhoCalledDto.getPhoneNumber());
            if (optionalUser.isPresent()) {
                peopleWhoCalled.setUsers(optionalUser.get());
            }
        }


        peopleWhoCalled.setCalledDate(new Date(System.currentTimeMillis()));
        peopleWhoCalled.setService(serviceList);
        peopleWhoCalled.setComment(peopleWhoCalledDto.getComment());
        peopleWhoCalled.setProduct(productList);
        peopleWhoCalled.setStatus(Status.WAITING);
        peopleWhoCalled.setWhenShouldCall(peopleWhoCalledDto.getWhenShouldCall());
        peopleWhoCalled.setPhoneNumber(peopleWhoCalledDto.getPhoneNumber());
        peopleWhoCalledRepository.save(peopleWhoCalled);
        return new ResponseApi("Muvaffaqiyatli saqlandi", true);
    }

    public ResponseApi editPhoneNumberOrShouldCallDate(Long id, ReqPeopleWhoCalledToChangePhnOrDate peopleWhoCalledToChangePhnOrDate) {
        Optional<PeopleWhoCalled> optionalPeopleWhoCalled = peopleWhoCalledRepository.findById(id);
        if (!optionalPeopleWhoCalled.isPresent())
            return new ResponseApi("Not found", false);
        PeopleWhoCalled peopleWhoCalled = optionalPeopleWhoCalled.get();
        peopleWhoCalled.setWhenShouldCall(peopleWhoCalledToChangePhnOrDate.getShouldCallDate());
        peopleWhoCalled.setPhoneNumber(peopleWhoCalledToChangePhnOrDate.getPhoneNumber());
        peopleWhoCalledRepository.save(peopleWhoCalled);
        return new ResponseApi("Successfully changed", true);
    }

    public Page<PeopleWhoCalled> getMissedPeople(int page) {
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<PeopleWhoCalled> missedPeople = peopleWhoCalledRepository.getMissedPeople(pageRequest);
        return missedPeople;

    }

    public List<PeopleWhoCalled> getPeopleForCallingToday() {
        List<PeopleWhoCalled> peopleForCallingToday = peopleWhoCalledRepository.getPeopleForCallingToday();
        return peopleForCallingToday;
    }

    public Page<PeopleWhoCalled> getShouldCallPeople(int page) {
        PageRequest of = PageRequest.of(page, 15);
        Page<PeopleWhoCalled> shouldCallPeople = peopleWhoCalledRepository.getShouldCallPeople(of);
        return shouldCallPeople;
    }

    public Page<PeopleWhoCalled> getAll(int page) {
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<PeopleWhoCalled> peopleWhoCalledRepositoryAll = peopleWhoCalledRepository.findAll(pageRequest);
        return peopleWhoCalledRepositoryAll;
    }

    public ResponseApi delete(Long id) {
        try {
            peopleWhoCalledRepository.deleteById(id);
            return new ResponseApi("Successfully deleted", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting", false);
        }
    }


}
