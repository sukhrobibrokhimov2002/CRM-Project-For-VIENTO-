package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.component.GetterProductValidPrice;
import uz.viento.crm_system.entity.*;
import uz.viento.crm_system.entity.enums.OrderOutputStatus;
import uz.viento.crm_system.entity.enums.RoleName;
import uz.viento.crm_system.entity.enums.Status;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.repository.*;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
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
    @Autowired
    OrderOutputProductService orderOutputProductService;
    @Autowired
    OrderOutputServiceLogic orderOutputServiceLogic;
    @Autowired
    OrderService orderService;
    @Autowired
    CurrencyRepository currencyRepository;
    @Autowired
    OrderOutputProductRepository orderOutputProductRepository;
    @Autowired
    OrderOutputServiceRepository orderOutputServiceRepository;

    public ResponseApi add(PeopleWhoCalledDto peopleWhoCalledDto) {
        User user;
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(peopleWhoCalledDto.getPhoneNumber());
        if (!byPhoneNumber.isPresent()) {

            ResponseApiWithObject userForCalled = getterProductValidPrice.createUserForCalled(peopleWhoCalledDto.getPhoneNumber(), peopleWhoCalledDto.getReqCreateUserDto());
            user = (User) userForCalled.getObject();
        } else {
            user = byPhoneNumber.get();
        }


        //it checks arrangement time is in future or not
        if (peopleWhoCalledDto.getWhenShouldCall().compareTo(LocalDate.now()) == -1) {
            return new ResponseApi("You can not arrange meeting for past)", false);
        }

        List<OrderOutputService> serviceList = new ArrayList<>();
        List<OrderOutputProduct> productList = new ArrayList<>();
        if (peopleWhoCalledDto.getServiceId() != null) {
            for (UUID serviceId : peopleWhoCalledDto.getServiceId()) {
                ResponseApiWithObject responseApiWithObject = orderOutputServiceLogic.saveOrderService(OrderOutputStatus.WAITING, serviceId);
                OrderOutputService outputService = (OrderOutputService) responseApiWithObject.getObject();
                serviceList.add(outputService);
            }
        }
        //product output ga saqlash uchun
        if (peopleWhoCalledDto.getReqOutputProductDtos() != null) {
            for (ReqOutputProductDto reqOutputProductDto : peopleWhoCalledDto.getReqOutputProductDtos()) {
                ResponseApiWithObject responseApiWithObject =
                        orderOutputProductService.saveOrderOutputProduct(OrderOutputStatus.WAITING, reqOutputProductDto);
                if (!responseApiWithObject.isSuccess())
                    return new ResponseApi("" + responseApiWithObject.getMessage(), false);
                OrderOutputProduct orderOutputProduct = (OrderOutputProduct) responseApiWithObject.getObject();

                productList.add(orderOutputProduct);
            }

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

    public ResponseApi makeOrderByPeopleWhoCalled(Long id, ReqOrderByPeopleWhoCalled reqOrder) {
        OrderDto orderDto = new OrderDto();

        Optional<PeopleWhoCalled> calledOptional = peopleWhoCalledRepository.findById(id);
        if (!calledOptional.isPresent()) return new ResponseApi("Not found", false);
        PeopleWhoCalled peopleWhoCalled = calledOptional.get();
        if (peopleWhoCalled.getStatus() != Status.WAITING) return new ResponseApi("this already ordered", false);



        List outputList = new ArrayList();

        if (peopleWhoCalled.getProduct() != null) {
            List<OrderOutputProduct> product = peopleWhoCalled.getProduct();
            for (OrderOutputProduct orderOutputProduct : product) {
                orderOutputProduct.setOrderOutputStatus(OrderOutputStatus.ACCEPTED);

                orderOutputProductRepository.save(orderOutputProduct);

                ReqOutputProductDto reqOutputProductDto =
                        new ReqOutputProductDto(
                                orderOutputProduct.getProduct().getId(),
                                orderOutputProduct.getAmount());
                outputList.add(reqOutputProductDto);

            }
            orderDto = new OrderDto(
                    reqOrder.getComment(),
                    peopleWhoCalled.getPhoneNumber(),
                    reqOrder.getAdditionalPhoneNumber(),
                    reqOrder.getAddress(),
                    reqOrder.getCurrencyId(),
                    false,
                    null,
                    outputList,
                    null
            );
        } else if (peopleWhoCalled.getService() != null) {
            List<OrderOutputService> service = peopleWhoCalled.getService();
            for (OrderOutputService orderOutputService : service) {
                orderOutputService.setOrderOutputStatus(OrderOutputStatus.ACCEPTED);

                orderOutputServiceRepository.save(orderOutputService);

                OrderOutputServiceDto orderOutputServiceDto =
                        new OrderOutputServiceDto(
                                orderOutputService.getId()
                        );
                outputList.add(orderOutputService);
            }
            orderDto = new OrderDto(
                    reqOrder.getComment(),
                    peopleWhoCalled.getPhoneNumber(),
                    reqOrder.getAdditionalPhoneNumber(),
                    reqOrder.getAddress(),
                    reqOrder.getCurrencyId(),
                    false,
                    null,
                    outputList,
                    null
            );
        }


        peopleWhoCalled.setStatus(Status.COMPLETED);
        peopleWhoCalledRepository.save(peopleWhoCalled);
        orderService.Order(orderDto);

        return new ResponseApi("Successfully ordered", true);


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
                    missedPerson.getUsers().getFullName(),
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
                    missedPerson.getUsers().getFullName(),
                    missedPerson.getUsers().getAdditionalPhoneNumber(),
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
                    shouldCall.getUsers().getFullName(),
                    shouldCall.getPhoneNumber(),
                    returnServiceName(shouldCall.getService()),
                    returnProductName(shouldCall.getProduct()),
                    shouldCall.getUsers().getAdditionalPhoneNumber(),
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
                    peopleWhoCalled.getUsers().getFullName(),
                    peopleWhoCalled.getUsers().getAdditionalPhoneNumber(),
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
    public List<String> returnProductName(List<OrderOutputProduct> list) {
        List<String> productList = new ArrayList<>();
        for (OrderOutputProduct orderOutputProduct : list) {
            productList.add(orderOutputProduct.getProduct().getNameEng());
        }
        return productList;
    }

    //method for extracting some properties from ServiceList
    public List<String> returnServiceName(List<OrderOutputService> list) {
        List<String> serviceList = new ArrayList<>();
        for (OrderOutputService orderOutputService : list) {
            String name = orderOutputService.getService().getName();
            serviceList.add(name);
        }
        return serviceList;
    }
}
