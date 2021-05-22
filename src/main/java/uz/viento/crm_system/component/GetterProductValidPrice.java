package uz.viento.crm_system.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.entity.ProductPrice;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.payload.ReqCreateUserDto;
import uz.viento.crm_system.payload.ResponseApiWithObject;
import uz.viento.crm_system.repository.ProductPriceRepository;
import uz.viento.crm_system.repository.ProductRepository;
import uz.viento.crm_system.repository.UserRepository;

import java.util.List;

@Component
public class GetterProductValidPrice {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductPriceRepository productPriceRepository;
    @Autowired
    UserRepository userRepository;

    public Double findValidSelling(Product product) {
        List<ProductPrice> productPrices = product.getProductPrices();
        for (ProductPrice productPrice : productPrices) {
            if (productPrice.isValid()) {
                return productPrice.getSellingPrice();
            }
        }
        return null;
    }

    public ProductPrice findValidPrice(Product product) {
        List<ProductPrice> productPrices = product.getProductPrices();
        for (ProductPrice productPrice : productPrices) {
            if (productPrice.isValid()) {
                return productPrice;
            }
        }
        return null;
    }

    public ResponseApiWithObject createUserForCalled(String phoneNumber,ReqCreateUserDto createUserDto) {
        boolean existsByPhoneNumber = userRepository.existsByPhoneNumber(phoneNumber);
        if (existsByPhoneNumber) return new ResponseApiWithObject("User already exists", false, null);
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setAddress(createUserDto.getAddress());
        user.setFullName(createUserDto.getFullName());
        User savedUser = userRepository.save(user);
        return new ResponseApiWithObject("Successfully saved", true, savedUser);
    }


}
