package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.component.GetterProductValidPrice;
import uz.viento.crm_system.entity.OrderOutputProduct;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.repository.OrderOutputProductRepository;
import uz.viento.crm_system.repository.OrderRepository;
import uz.viento.crm_system.repository.ProductPriceRepository;
import uz.viento.crm_system.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderOutputProductService {

    @Autowired
    OrderOutputProductRepository orderOutputProductRepository;
    @Autowired
    ProductPriceRepository productPriceRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    GetterProductValidPrice getterProductValidPrice;
    @Autowired
    OrderRepository orderRepository;


    public ResponseApi delete(UUID id) {
        try {
            orderOutputProductRepository.deleteById(id);
            return new ResponseApi("Successfully deleted", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting", false);
        }

    }

    public Page<ResOutputProduct> getAll(int page) {
        List<ResOutputProduct> resOutputProductList = new ArrayList<>();
        List<OrderOutputProduct> all = orderOutputProductRepository.findAll();
        for (OrderOutputProduct orderOutputProduct : all) {
            ResOutputProduct resOutputProduct = new ResOutputProduct(
                    orderOutputProduct.getProduct().getNameEng(),
                    orderOutputProduct.getAmount(),
                    orderOutputProduct.getTotalPrice()
            );
            resOutputProductList.add(resOutputProduct);
        }
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<ResOutputProduct> products = new PageImpl<>(resOutputProductList, pageRequest, resOutputProductList.size());
        return products;
    }

//    public Page<ResOutputProduct> getOneOrderId(UUID orderId, int page) {
//        List<ResOutputProduct> resOutputProductList = new ArrayList<>();
//        List<OrderOutputProduct> all = orderOutputProductRepository.findAllByOrderId(orderId);
//        for (OrderOutputProduct orderOutputProduct : all) {
//            ResOutputProduct resOutputProduct = new ResOutputProduct(
//                    orderOutputProduct.getProduct().getNameEng(),
//                    orderOutputProduct.getAmount(),
//                    orderOutputProduct.getTotalPrice(),
//                    orderOutputProduct.getOrder().getCode()
//            );
//            resOutputProductList.add(resOutputProduct);
//        }
//        PageRequest pageRequest = PageRequest.of(page, 15);
//        Page<ResOutputProduct> products = new PageImpl<>(resOutputProductList, pageRequest, resOutputProductList.size());
//        return products;
//    }


}
