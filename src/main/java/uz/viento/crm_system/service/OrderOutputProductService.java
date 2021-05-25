package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.component.GetterProductValidPrice;
import uz.viento.crm_system.entity.OrderOutputProduct;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.entity.enums.OrderOutputStatus;
import uz.viento.crm_system.entity.enums.StatusOrder;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.repository.OrderOutputProductRepository;
import uz.viento.crm_system.repository.OrderRepository;
import uz.viento.crm_system.repository.ProductPriceRepository;
import uz.viento.crm_system.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public ResponseApiWithObject saveOrderOutputProduct(OrderOutputStatus orderOutputStatus, ReqOutputProductDto reqOutputProductDto) {
        Optional<Product> optionalProduct = productRepository.findById(reqOutputProductDto.getProductId());
        if (!optionalProduct.isPresent()) return new ResponseApiWithObject("Product not found", false, null);
        Product product = optionalProduct.get();
        //it checks product have in enough quantity or not
        if (product.isExpired() || product.getAmount() < reqOutputProductDto.getAmount()) {
            return new ResponseApiWithObject("Product is not available", false, null);
        }


        OrderOutputProduct orderOutputProduct = new OrderOutputProduct(
                product,
                getterProductValidPrice.findValidPrice(product),
                reqOutputProductDto.getAmount(),
                reqOutputProductDto.getAmount() * getterProductValidPrice.findValidSelling(product),
                orderOutputStatus
        );
        OrderOutputProduct outputProduct = orderOutputProductRepository.save(orderOutputProduct);
        return new ResponseApiWithObject("Successfully saved", true, outputProduct);


    }


    public ResponseApi changeQuantity(UUID id, ReqChangeProductQuantity reqChangeProductQuantity) {
        Optional<OrderOutputProduct> productOptional = orderOutputProductRepository.findById(id);
        if (!productOptional.isPresent())
            return new ResponseApi("Not found", false);
        OrderOutputProduct orderOutputProduct = productOptional.get();
        orderOutputProduct.setAmount(reqChangeProductQuantity.getNewAmount());
        orderOutputProductRepository.save(orderOutputProduct);
        return new ResponseApi("Successfully changed", true);

    }

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

    public Page<ResOutputProduct> getAllByOrderId(UUID orderId, int page) {
        List<OrderOutputProduct> outputProducts = orderOutputProductRepository.findAllByOrderId(orderId);
        if (outputProducts.isEmpty())
            return null;
        List list = new ArrayList();
        for (OrderOutputProduct outputProduct : outputProducts) {
            ResOutputProduct resOutputProduct = new ResOutputProduct(
                    outputProduct.getProduct().getNameEng(),
                    outputProduct.getAmount(),
                    outputProduct.getTotalPrice()
            );
            list.add(resOutputProduct);
        }
        PageImpl<ResOutputProduct> resOutputProducts = new PageImpl<ResOutputProduct>(list, PageRequest.of(page, 15), list.size());
        return resOutputProducts;

    }


    public Page<ResOutputProduct> getAllByStatus(int page, OrderOutputStatus orderOutputStatus) {
        List<OrderOutputProduct> productList = orderOutputProductRepository.findAllByOrderOutputStatus(orderOutputStatus);
        List list = new ArrayList();

        for (OrderOutputProduct orderOutputProduct : productList) {
            ResOutputProduct resOutputProduct = new ResOutputProduct(
                    orderOutputProduct.getProduct().getNameEng(),
                    orderOutputProduct.getAmount(),
                    orderOutputProduct.getTotalPrice());
            list.add(resOutputProduct);
        }
        return new PageImpl<ResOutputProduct>(list, PageRequest.of(page, 15), list.size());
    }


}
