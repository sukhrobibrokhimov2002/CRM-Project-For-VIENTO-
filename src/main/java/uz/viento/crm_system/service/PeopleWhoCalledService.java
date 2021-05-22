package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.component.GetterProductValidPrice;
import uz.viento.crm_system.entity.PeopleWhoCalled;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.entity.Roles;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.entity.enums.RoleName;
import uz.viento.crm_system.entity.enums.Status;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.repository.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    GetterProductValidPrice getterProductValidPrice;

    public ResponseApi add(PeopleWhoCalledDto peopleWhoCalledDto) {
        User user;
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(peopleWhoCalledDto.getPhoneNumber());
        if (!byPhoneNumber.isPresent()) {

            ResponseApiWithObject userForCalled = getterProductValidPrice.createUserForCalled(peopleWhoCalledDto.getPhoneNumber(),peopleWhoCalledDto.getReqCreateUserDto());
            user = (User) userForCalled.getObject();
        } else {
            user = byPhoneNumber.get();
        }


        //it checks arrangement time is in future or not
        if (peopleWhoCalledDto.getWhenShouldCall().compareTo(LocalDate.now()) == -1) {
            return new ResponseApi("You can not arrange meeting for past)", false);
        }

        List<uz.viento.crm_system.entity.Service> serviceList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        if (peopleWhoCalledDto.getServiceId() != null) {
            serviceList = serviceRepository.findAllById(peopleWhoCalledDto.getServiceId());
        }
        if (peopleWhoCalledDto.getProductId() != null) {
            productList = productRepository.findAllById(peopleWhoCalledDto.getProductId());
        }
        PeopleWhoCalled peopleWhoCalled = new PeopleWhoCalled(
                user,
                serviceList,
                productList,
                peopleWhoCalledDto.getPhoneNumber(),
                new Date(System.currentTimeMillis()),
                peopleWhoCalledDto.getWhenShouldCall(),
                peopleWhoCalledDto.getComment(),
                Status.WAITING
        );
        peopleWhoCalledRepository.save(peopleWhoCalled);


        return new ResponseApi("Muvaffaqiyatli saqlandi", true);
    }


    public ResponseApi editPhoneNumberOrShouldCallDate(Long id, ReqPeopleWhoCalledToChangePhnOrDate reqPeopleWhoCalledToChangePhnOrDate) {
        Optional<PeopleWhoCalled> optionalPeopleWhoCalled = peopleWhoCalledRepository.findById(id);
        if (!optionalPeopleWhoCalled.isPresent())
            return new ResponseApi("Not found", false);
        PeopleWhoCalled peopleWhoCalled = optionalPeopleWhoCalled.get();
        if (reqPeopleWhoCalledToChangePhnOrDate.getShouldCallDate() != null) {
            peopleWhoCalled.setWhenShouldCall(reqPeopleWhoCalledToChangePhnOrDate.getShouldCallDate());
        }
        if (reqPeopleWhoCalledToChangePhnOrDate.getPhoneNumber() != null) {
            peopleWhoCalled.setPhoneNumber(reqPeopleWhoCalledToChangePhnOrDate.getPhoneNumber());
        }
        peopleWhoCalledRepository.save(peopleWhoCalled);
        return new ResponseApi("Successfully changed", true);
    }

    /*
    returns people who did not recieve a call on arranged time
     */
    public Page<ResPeopleWhoCalled> getMissedPeople(int page) {

        List<ResPeopleWhoCalled> resPeopleWhoCalledList = new ArrayList<>();

        List<PeopleWhoCalled> missedPeople = peopleWhoCalledRepository.getMissedPeople();
        for (PeopleWhoCalled missedPerson : missedPeople) {
            ResPeopleWhoCalled resPeopleWhoCalled = new ResPeopleWhoCalled(
                    returnServiceName(missedPerson.getService()),
                    returnProductName(missedPerson.getProduct()),
                    missedPerson.getPhoneNumber(),
                    missedPerson.getWhenShouldCall(),
                    missedPerson.getComment(),
                    missedPerson.getStatus()
            );

            if (missedPerson.getUsers() != null) {
                resPeopleWhoCalled.setUserPhoneNumber(missedPerson.getPhoneNumber());
            }
            resPeopleWhoCalledList.add(resPeopleWhoCalled);


        }
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<ResPeopleWhoCalled> pageList = new PageImpl<ResPeopleWhoCalled>(resPeopleWhoCalledList, pageRequest, resPeopleWhoCalledList.size());

        return pageList;
    }

    //returns people for calling today
    public List<ResPeopleWhoCalled> getPeopleForCallingToday() {
        List<ResPeopleWhoCalled> resPeopleWhoCalledList = new ArrayList<>();

        List<PeopleWhoCalled> missedPeople = peopleWhoCalledRepository.getPeopleForCallingToday();
        for (PeopleWhoCalled missedPerson : missedPeople) {
            ResPeopleWhoCalled resPeopleWhoCalled = new ResPeopleWhoCalled(
                    returnServiceName(missedPerson.getService()),
                    returnProductName(missedPerson.getProduct()),
                    missedPerson.getPhoneNumber(),
                    missedPerson.getWhenShouldCall(),
                    missedPerson.getComment(),
                    missedPerson.getStatus()
            );

            if (missedPerson.getUsers() != null) {
                resPeopleWhoCalled.setUserPhoneNumber(missedPerson.getPhoneNumber());
            }
            resPeopleWhoCalledList.add(resPeopleWhoCalled);


        }

        return resPeopleWhoCalledList;
    }

    //returns people for calling future not today
    public Page<ResPeopleWhoCalled> getShouldCallPeople(int page) {
        List<ResPeopleWhoCalled> resPeopleWhoCalledList = new ArrayList<>();

        List<PeopleWhoCalled> shouldCallPeople = peopleWhoCalledRepository.getShouldCallPeople();
        for (PeopleWhoCalled shouldCall : shouldCallPeople) {
            ResPeopleWhoCalled resPeopleWhoCalled = new ResPeopleWhoCalled(
                    returnServiceName(shouldCall.getService()),
                    returnProductName(shouldCall.getProduct()),
                    shouldCall.getPhoneNumber(),
                    shouldCall.getWhenShouldCall(),
                    shouldCall.getComment(),
                    shouldCall.getStatus()
            );

            if (shouldCall.getUsers() != null) {
                resPeopleWhoCalled.setUserPhoneNumber(shouldCall.getPhoneNumber());
            }
            resPeopleWhoCalledList.add(resPeopleWhoCalled);


        }
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<ResPeopleWhoCalled> pageList = new PageImpl<ResPeopleWhoCalled>(resPeopleWhoCalledList, pageRequest, resPeopleWhoCalledList.size());

        return pageList;
    }


    //returns all list
    public Page<ResPeopleWhoCalled> getAll(int page) {
        List<ResPeopleWhoCalled> resPeopleWhoCalledList = new ArrayList<>();

        List<PeopleWhoCalled> getall = peopleWhoCalledRepository.findAll();
        for (PeopleWhoCalled peopleWhoCalled : getall) {
            ResPeopleWhoCalled resPeopleWhoCalled = new ResPeopleWhoCalled(
                    returnServiceName(peopleWhoCalled.getService()),
                    returnProductName(peopleWhoCalled.getProduct()),
                    peopleWhoCalled.getPhoneNumber(),
                    peopleWhoCalled.getWhenShouldCall(),
                    peopleWhoCalled.getComment(),
                    peopleWhoCalled.getStatus()
            );
            resPeopleWhoCalledList.add(resPeopleWhoCalled);


        }
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<ResPeopleWhoCalled> pageList = new PageImpl<ResPeopleWhoCalled>(resPeopleWhoCalledList, pageRequest, resPeopleWhoCalledList.size());

        return pageList;
    }


    public ResponseApi delete(Long id) {
        try {
            peopleWhoCalledRepository.deleteById(id);
            return new ResponseApi("Successfully deleted", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting", false);
        }
    }


    //method for extracting some properties from productList
    public List<String> returnProductName(List<Product> list) {
        List<String> productList = new ArrayList<>();
        for (Product product : list) {
            productList.add(product.getNameEng());
        }
        return productList;
    }

    //method for extracting some properties from ServiceList
    public List<String> returnServiceName(List<uz.viento.crm_system.entity.Service> list) {
        List<String> serviceList = new ArrayList<>();
        for (uz.viento.crm_system.entity.Service service : list) {
            serviceList.add(service.getName());
        }
        return serviceList;
    }
}
