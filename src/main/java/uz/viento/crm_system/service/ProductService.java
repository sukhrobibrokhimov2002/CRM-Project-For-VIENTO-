package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.component.GenerateSerialNumber;
import uz.viento.crm_system.component.GetterProductValidPrice;
import uz.viento.crm_system.entity.*;
import uz.viento.crm_system.entity.attachment.Attachment;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.repository.*;

import java.sql.Date;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    MeasurementRepository measurementRepository;
    @Autowired
    CurrencyRepository currencyRepository;
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    GenerateSerialNumber generateSerialNumber;
    @Autowired
    ProductPriceRepository productPriceRepository;
    @Autowired
    GetterProductValidPrice getSellingPrice;

    public ResponseApi addProduct(AddingProductDto addingProductDto) {
        if (addingProductDto.getCategoryId() == null ||
                addingProductDto.getCurrencyId() == null ||
                addingProductDto.getMeasurementId() == null) {
            return new ResponseApi("Error in adding product", false);
        }
        Optional<Category> optionalCategory = categoryRepository.findById(addingProductDto.getCategoryId());
        if (!optionalCategory.isPresent()) return new ResponseApi("Category not found", false);

        Optional<Measurement> optionalMeasurement = measurementRepository.findById(addingProductDto.getMeasurementId());
        if (!optionalMeasurement.isPresent()) return new ResponseApi("Measurement not found", false);

        Optional<CurrencyType> optionalCurrencyType = currencyRepository.findById(addingProductDto.getCurrencyId());
        if (!optionalCurrencyType.isPresent()) return new ResponseApi("Currency not found", false);

        boolean exists = productRepository.existsByNameEngOrNameRuOrNameUz(
                addingProductDto.getNameEng(),
                addingProductDto.getNameRu(),
                addingProductDto.getNameUz()

        );
        if (exists)
            return new ResponseApi("This product already exists", false);

        List<Attachment> attachmentList = attachmentRepository.findAllById(addingProductDto.getAttachmentId());

        Product product = new Product();


        product.setAmount(addingProductDto.getAmount());
        product.setAvailable(addingProductDto.isAvailable());
        product.setCategory(optionalCategory.get());
        product.setAttachmentList(attachmentList);
        product.setCurrencyType(optionalCurrencyType.get());
        product.setExpireDate(addingProductDto.getExpireDate());
        product.setMeasurement(optionalMeasurement.get());
        product.setSerialNumber(generateSerialNumber.randomUniqueNumber());
        product.setNameUz(addingProductDto.getNameUz());
        product.setNameRu(addingProductDto.getNameRu());
        product.setNameEng(addingProductDto.getNameEng());
        product.setDescriptionUz(addingProductDto.getDescriptionUz());
        product.setDescriptionRu(addingProductDto.getDescriptionRu());
        product.setDescriptionEng(addingProductDto.getDescriptionEng());
        product.setMadeIn(addingProductDto.getMadeIn());
        product.setEnterDate(new Date(System.currentTimeMillis()));

        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(product);
        productPrice.setSellingPrice(addingProductDto.getSellingPrice());
        productPrice.setOriginalPrice(addingProductDto.getOriginalPrice());
        productPrice.setValid(true);
        productPrice.setChangedDate(new Date(System.currentTimeMillis()));

        product.setProductPrices(Collections.singletonList(productPrice));


        if (product.getExpireDate().after(new Date(System.currentTimeMillis()))) {
            product.setExpired(false);
            if (product.getAmount() > 0) {
                product.setAvailable(true);
            }
        } else {

            product.setExpired(true);
            product.setAvailable(false);
        }
        productRepository.save(product);
        return new ResponseApi("Product Successfully added", true);
    }

    public ResponseApi changeProductPrice(UUID id, ReqChangeProductPrice reqChangeProductPrice) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) return new ResponseApi("Product not found", false);
        Product oldProduct = optionalProduct.get();
        if (oldProduct.isExpired())
            return new ResponseApi("You can not change this product's price", false);

        Optional<ProductPrice> byValidAndProduct_id = productPriceRepository.findByValidAndProduct_Id(true, id);
        if (!byValidAndProduct_id.isPresent()) return new ResponseApi("Price not found", false);
        ProductPrice oldProductPrice = byValidAndProduct_id.get();
        oldProductPrice.setValid(false);

        ProductPrice newProductPrice = new ProductPrice();
        newProductPrice.setProduct(oldProductPrice.getProduct());
        newProductPrice.setOriginalPrice(oldProductPrice.getOriginalPrice());
        newProductPrice.setSellingPrice(reqChangeProductPrice.getNewPrice());
        newProductPrice.setChangedDate(new Date(System.currentTimeMillis()));
        newProductPrice.setValid(true);

        productPriceRepository.save(newProductPrice);
        productPriceRepository.save(oldProductPrice);
        return new ResponseApi("Successfully changed", true);
    }

    public ResponseApi addAdditionalProduct(UUID id, AddAdditionalProductDto addAdditionalProductDto) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) return new ResponseApi("Product not found", false);
        Product oldProduct = optionalProduct.get();

        if (addAdditionalProductDto.getExpireDate().before(new Date(System.currentTimeMillis())) ||
                addAdditionalProductDto.getAmount() < 1) {
            return new ResponseApi("Error", false);
        }
        oldProduct.setAmount(oldProduct.getAmount() + addAdditionalProductDto.getAmount());
        oldProduct.setEnterDate(addAdditionalProductDto.getExpireDate());
        if(oldProduct.isExpired()){
            oldProduct.setExpired(false);
        }

        ProductPrice validPrice = getSellingPrice.findValidPrice(oldProduct);
        validPrice.setValid(false);

        ProductPrice productPrice = new ProductPrice(
                addAdditionalProductDto.getOriginalPrice(),
                addAdditionalProductDto.getSellingPrice(),
                new Date(System.currentTimeMillis()),
                oldProduct,
                true
        );


        productRepository.save(oldProduct);
        //eski narx valid o'chirildi
        productPriceRepository.save(validPrice);
        //yangi narx belgilandi
        productPriceRepository.save(productPrice);
        return new ResponseApi("Successfully changed", true);
    }

    public ResponseApi deleteProduct(UUID id) {
        try {
            productRepository.deleteById(id);
            return new ResponseApi("Product successfully added", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting product", false);
        }

    }

    public Page<ResProduct> getAllProduct(int page) {

        //  every time you get information this method is working and if product is expired this set it expired true
        List<Product> allList = productRepository.findAll();
        for (Product product : allList) {
            if (product.getExpireDate().before(new Date(System.currentTimeMillis()))) {
                product.setExpired(true);
                product.setAvailable(false);
            } else {
                product.setExpired(false);
            }
            productRepository.save(product);
        }
        List<Product> all = productRepository.findAll();
        List<ResProduct> productList = new ArrayList<>();
        for (Product product : all) {
            ResProduct resProduct =
                    new ResProduct(
                            product.getNameUz(),
                            product.getNameRu(),
                            product.getNameEng(),
                            product.getDescriptionUz(),
                            product.getDescriptionRu(),
                            product.getDescriptionEng(),
                            product.getSerialNumber(),
                            product.getMadeIn(),
                            product.isAvailable(),
                            product.getAttachmentList(),
                            product.getCategory().getName(),
                            getSellingPrice.findValidSelling(product),
                            product.getExpireDate()
                    );
            productList.add(resProduct);
        }
        PageRequest pageRequest = PageRequest.of(page, 15);
        PageImpl<ResProduct> resProducts = new PageImpl<>(productList, pageRequest, productList.size());

        return resProducts;
    }

    public Page<ResProduct> getAvailableProducts(int page) {
        //every time you get information this method is working and if product is expired this set it expired true
        List<Product> allList = productRepository.findAll();
        for (Product product : allList) {
            if (product.getExpireDate().before(new Date(System.currentTimeMillis()))) {
                product.setExpired(true);
                product.setAvailable(false);
            } else {
                product.setExpired(false);

            }
            productRepository.save(product);
        }
        List<Product> allByAvailable = productRepository.findAllByAvailable(true);
        List<ResProduct> resProductList = new ArrayList<>();

        for (Product product : allByAvailable) {
            ResProduct resProduct = new ResProduct();
            resProduct.setNameUz(product.getNameUz());
            resProduct.setNameRu(product.getNameRu());
            resProduct.setNameEng(product.getNameEng());
            resProduct.setDescriptionUz(product.getDescriptionUz());
            resProduct.setDescriptionRu(product.getDescriptionRu());
            resProduct.setDescriptionEng(product.getDescriptionEng());
            resProduct.setAvailable(product.isAvailable());
            resProduct.setCategory(product.getCategory().getName());
            resProduct.setAttachmentList(product.getAttachmentList());
            resProduct.setMadeIn(product.getMadeIn());
            resProduct.setSerialNumber(product.getSerialNumber());
            resProduct.setExpireDate(product.getExpireDate());

            for (ProductPrice productPrice : product.getProductPrices()) {
                if (productPrice.isValid()) {
                    resProduct.setSellingPrice(productPrice.getSellingPrice());
                }
            }
            resProductList.add(resProduct);

        }

        Pageable pageable = PageRequest.of(page, 15);

        Page<ResProduct> pageResProduct = new PageImpl<ResProduct>(resProductList,
                pageable, resProductList.size());
        return pageResProduct;

    }

    public Page<ResProduct> getUnAvailableProduct(int page) {

        //every time you get information this method is working and if product is expired this set it expired true
        List<Product> allList = productRepository.findAll();
        for (Product product : allList) {
            if (product.getExpireDate().before(new Date(System.currentTimeMillis()))) {
                product.setExpired(true);
            } else {
                product.setExpired(false);
            }
        }

        List<Product> allByAvailable = productRepository.findAllByAvailable(false);
        List<ResProduct> resProductList = new ArrayList<>();

        for (Product product : allByAvailable) {
            ResProduct resProduct = new ResProduct();
            resProduct.setNameUz(product.getNameUz());
            resProduct.setNameRu(product.getNameRu());
            resProduct.setNameEng(product.getNameEng());
            resProduct.setDescriptionUz(product.getDescriptionUz());
            resProduct.setDescriptionRu(product.getDescriptionRu());
            resProduct.setDescriptionEng(product.getDescriptionEng());
            resProduct.setAvailable(product.isAvailable());
            resProduct.setCategory(product.getCategory().getName());
            resProduct.setAttachmentList(product.getAttachmentList());
            resProduct.setMadeIn(product.getMadeIn());
            resProduct.setSerialNumber(product.getSerialNumber());
            resProduct.setExpireDate(product.getExpireDate());

            for (ProductPrice productPrice : product.getProductPrices()) {
                if (productPrice.isValid()) {
                    resProduct.setSellingPrice(productPrice.getSellingPrice());
                }
            }
            resProductList.add(resProduct);

        }

        Pageable pageable = PageRequest.of(page, 15);

        Page<ResProduct> pageResProduct = new PageImpl<ResProduct>(resProductList,
                pageable, resProductList.size());
        return pageResProduct;

    }

    public ResProduct getOneProduct(UUID id) {
        //every time you get information this method is working and if product is expired this set it expired true
        List<Product> allList = productRepository.findAll();
        for (Product product : allList) {
            if (product.getExpireDate().before(new Date(System.currentTimeMillis()))) {
                product.setExpired(true);
                product.setAvailable(false);
            } else {
                product.setExpired(false);
                ;
            }
            productRepository.save(product);
        }


        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) return null;
        Product product = optionalProduct.get();


        ResProduct resProduct = new ResProduct();
        resProduct.setNameUz(product.getNameUz());
        resProduct.setNameRu(product.getNameRu());
        resProduct.setNameEng(product.getNameEng());
        resProduct.setDescriptionUz(product.getDescriptionUz());
        resProduct.setDescriptionRu(product.getDescriptionRu());
        resProduct.setDescriptionEng(product.getDescriptionEng());
        resProduct.setAvailable(product.isAvailable());
        resProduct.setCategory(product.getCategory().getName());
        resProduct.setAttachmentList(product.getAttachmentList());
        resProduct.setMadeIn(product.getMadeIn());
        resProduct.setSerialNumber(product.getSerialNumber());
        resProduct.setExpireDate(product.getExpireDate());

        for (ProductPrice productPrice : product.getProductPrices()) {
            if (productPrice.isValid()) {
                resProduct.setSellingPrice(productPrice.getSellingPrice());
            }
        }

        return resProduct;

    }

    public Page<ResProduct> getExpiredProducts(int page) {
        //every time you get information this method is working and if product is expired this set it expired true
        List<Product> allList = productRepository.findAll();
        for (Product product : allList) {
            if (product.getExpireDate().before(new Date(System.currentTimeMillis()))) {
                product.setExpired(true);
                product.setAvailable(false);
            } else {
                product.setExpired(false);
                ;
            }
            productRepository.save(product);
        }


        List<Product> allByAvailable = productRepository.findAllByExpired(true);
        List<ResProduct> resProductList = new ArrayList<>();

        for (Product product : allByAvailable) {
            ResProduct resProduct = new ResProduct();
            resProduct.setNameUz(product.getNameUz());
            resProduct.setNameRu(product.getNameRu());
            resProduct.setNameEng(product.getNameEng());
            resProduct.setDescriptionUz(product.getDescriptionUz());
            resProduct.setDescriptionRu(product.getDescriptionRu());
            resProduct.setDescriptionEng(product.getDescriptionEng());
            resProduct.setAvailable(product.isAvailable());
            resProduct.setCategory(product.getCategory().getName());
            resProduct.setAttachmentList(product.getAttachmentList());
            resProduct.setMadeIn(product.getMadeIn());
            resProduct.setSerialNumber(product.getSerialNumber());
            resProduct.setExpireDate(product.getExpireDate());

            for (ProductPrice productPrice : product.getProductPrices()) {
                if (productPrice.isValid()) {
                    resProduct.setSellingPrice(productPrice.getSellingPrice());
                }
            }
            resProductList.add(resProduct);

        }

        Pageable pageable = PageRequest.of(page, 15);

        Page<ResProduct> pageResProduct = new PageImpl<ResProduct>(resProductList,
                pageable, resProductList.size());
        return pageResProduct;

    }


    public List<ResProductPrice> getProductPrice(UUID id) {
        List<ProductPrice> allByProduct_id = productPriceRepository.findAllByProduct_Id(id);
        List<ResProductPrice> list = new ArrayList();
        for (ProductPrice productPrice : allByProduct_id) {
            ResProductPrice resProductPrice = new ResProductPrice(
                    productPrice.getOriginalPrice(),
                    productPrice.getSellingPrice(),
                    productPrice.getChangedDate(),
                    productPrice.isValid()

            );
            list.add(resProductPrice);
        }
        return list;
    }
}
