package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.component.GenerateSerialNumber;
import uz.viento.crm_system.component.GetterProductValidPrice;
import uz.viento.crm_system.entity.*;
import uz.viento.crm_system.entity.enums.OrderOutputStatus;
import uz.viento.crm_system.entity.enums.StatusOrder;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.repository.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GetterProductValidPrice getterProductValidPrice;
    @Autowired
    OrderOutputProductRepository orderOutputProductRepository;
    @Autowired
    OrderOutputServiceRepository orderOutputServiceRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    CurrencyRepository currencyRepository;
    @Autowired
    GenerateSerialNumber generateSerialNumber;
    @Autowired
    OrderOutputProductService orderOutputProductService;
    @Autowired
    OrderOutputServiceLogic orderOutputServiceLogic;


    public ResponseApi order(OrderDto orderDto) {
        User user;
        List orderOutputList = new ArrayList<>();
        Double summa = 0.0;

        //client ni yangi yoki eskiligini tekshiradi yangi bo'lsa bazaga yozadi eski bo'lsa ma'lumotlarni bazadan oladi
        if (orderDto.isNewClient()) {
            ResponseApiWithObject userForCalled =
                    getterProductValidPrice.createUserForCalled(orderDto.getPhoneNumber(), orderDto.getCreateUserDto());
            user = (User) userForCalled.getObject();
        } else {
            Optional<User> byPhoneNumber =
                    userRepository.findByPhoneNumber(orderDto.getPhoneNumber());
            if (!byPhoneNumber.isPresent())
                return new ResponseApi("User not found", false);
            user = byPhoneNumber.get();
        }

        if (orderDto.getReqOutputProductDtos() != null) {
            List<ReqOutputProductDto> reqOutputProductDtos = orderDto.getReqOutputProductDtos();

//work with products
            for (ReqOutputProductDto reqOutputProductDto : reqOutputProductDtos) {
                Optional<Product> optionalProduct = productRepository.findById(reqOutputProductDto.getProductId());
                if (!optionalProduct.isPresent()) return new ResponseApi("Product not found", false);
                Product product = optionalProduct.get();
                product.setAmount(product.getAmount() - reqOutputProductDto.getAmount());

                productRepository.save(product);
                //it checks product is expired or have enough quantity for sell
                if (product.isExpired() || product.getAmount() < reqOutputProductDto.getAmount()) {
                    product.setAvailable(false);
                    productRepository.save(product);
                    return new ResponseApi("Product not left", false);
                }

                ResponseApiWithObject responseApiWithObject = orderOutputProductService.saveOrderOutputProduct(OrderOutputStatus.ACCEPTED, reqOutputProductDto);
                OrderOutputProduct orderOutputProduct = (OrderOutputProduct) responseApiWithObject.getObject();
                orderOutputList.add(orderOutputProduct);
                summa += orderOutputProduct.getTotalPrice();
                productRepository.save(product);
            }

        } else if (orderDto.getOrderOutputServiceDtos() != null) {
            List<OrderOutputServiceDto> service = orderDto.getOrderOutputServiceDtos();
            for (OrderOutputServiceDto outputService : service) {
                Optional<uz.viento.crm_system.entity.Service> optionalService = serviceRepository.findById(outputService.getServiceId());
                if (!optionalService.isPresent()) return new ResponseApi("Service not found", false);

                ResponseApiWithObject responseApiWithObject = orderOutputServiceLogic.saveOrderService(OrderOutputStatus.ACCEPTED, optionalService.get().getId());
                OrderOutputService orderOutputService = (OrderOutputService) responseApiWithObject.getObject();


                OrderOutputService save = orderOutputServiceRepository.save(orderOutputService);
                orderOutputList.add(save);
                summa += save.getTotalPrice();
            }
        }

        Optional<CurrencyType> optionalCurrencyType = currencyRepository.findById(orderDto.getCurrencyId());
        if (!optionalCurrencyType.isPresent())
            return new ResponseApi("Currency not found", false);

        Order order = new Order();
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setStatusOrder(StatusOrder.ACCEPTED);
        order.setAddress(orderDto.getAddress());


        order.setCode(generateSerialNumber.randomUniqueNumber());
        order.setComment(orderDto.getComment());
        order.setSumma(summa);
        order.setUsers(user);
        order.setCurrencyType(optionalCurrencyType.get());
        order.setPhoneNumber(orderDto.getPhoneNumber());

        if (orderDto.getOrderOutputServiceDtos() != null) {
            order.setOrderOutputServiceList(orderOutputList);
        } else if (orderDto.getReqOutputProductDtos() != null) {
            order.setOrderOutputProducts(orderOutputList);
        }

        orderRepository.save(order);
        return new ResponseApi("Successfully ordered", true);

    }


    public ResponseApi rejectOrder(UUID id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent())
            return new ResponseApi("Order not found", false);
        Order order = optionalOrder.get();
        order.setStatusOrder(StatusOrder.REJECTED);
        orderRepository.save(order);
        return new ResponseApi("Order rejected ((", true);
    }

    public Page<ResAllOrders> getAll(int page) {
        List<Order> all = orderRepository.findAll();
        List<ResAllOrders> resAllOrders = new ArrayList<>();
        for (Order order : all) {
            ResAllOrders resOrders = new ResAllOrders(
                    order.getComment(),
                    order.getOrderDate(),
                    order.getCurrencyType().getAbbreviation(),
                    order.getUsers().getUsername(),
                    order.getPhoneNumber(),
                    order.getStatusOrder().name()
            );
            resAllOrders.add(resOrders);

        }
        PageImpl<ResAllOrders> ordersInPage =
                new PageImpl<>(resAllOrders, PageRequest.of(page, 15), resAllOrders.size());

        return ordersInPage;
    }

    public ResOneOrder getOneOrder(UUID id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        List<ResServiceOrProduct> serviceOrProducts = new ArrayList<>();

        if (!optionalOrder.isPresent())
            return null;
        Order order = optionalOrder.get();
        ResOneOrder resOneOrder = new ResOneOrder();
        resOneOrder.setOrderStatus(order.getStatusOrder().name());
        resOneOrder.setOrderDate(order.getOrderDate());
        resOneOrder.setComment(order.getComment());
        resOneOrder.setUsername(order.getUsers().getUsername());
        resOneOrder.setCurrencyAbbreviation(order.getCurrencyType().getAbbreviation());

        if (order.getOrderOutputServiceList() != null) {
            for (OrderOutputService orderOutputService : order.getOrderOutputServiceList()) {
                ResServiceOrProduct resServiceOrProduct = new ResServiceOrProduct(
                        orderOutputService.getService().getName(),
                        orderOutputService.getTotalPrice()
                );
                serviceOrProducts.add(resServiceOrProduct);
            }
        } else if (order.getOrderOutputProducts() != null) {
            for (OrderOutputProduct orderOutputProduct : order.getOrderOutputProducts()) {
                ResServiceOrProduct product = new ResServiceOrProduct(
                        orderOutputProduct.getProduct().getNameEng(),
                        orderOutputProduct.getTotalPrice()
                );
                serviceOrProducts.add(product);
            }
        }
        resOneOrder.setProductNames(serviceOrProducts);
        return resOneOrder;


    }


}
