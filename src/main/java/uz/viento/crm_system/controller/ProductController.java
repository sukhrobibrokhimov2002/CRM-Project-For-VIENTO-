package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.payload.*;
import uz.viento.crm_system.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;


    @PostMapping("/add-new")
    public ResponseEntity<?> addNewProduct(@RequestBody AddingProductDto addingProductDto) {
        ResponseApi responseApi = productService.addProduct(addingProductDto);

        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }

    @PostMapping("/add-additional/{id}")
    public ResponseEntity<?> addAdditionalProduct(@PathVariable UUID id, @RequestBody AddAdditionalProductDto addAdditionalProductDto) {

        ResponseApi responseApi = productService.addAdditionalProduct(id, addAdditionalProductDto);

        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }

    @PatchMapping("/change-product-price/{id}")
    public ResponseEntity<?> changeProductPrice(@PathVariable UUID id, @RequestBody ReqChangeProductPrice reqChangeProductPrice) {
        ResponseApi responseApi = productService.changeProductPrice(id, reqChangeProductPrice);

        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        ResponseApi responseApi = productService.deleteProduct(id);

        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }


    @GetMapping("/get-all")
    public ResponseEntity<?> getAllProducts(@RequestParam int page) {
        Page<ResProduct> allProduct = productService.getAllProduct(page);
        return ResponseEntity.ok(allProduct);
    }

    @GetMapping("/get-available-products")
    public ResponseEntity<?> getAvailableProducts(@RequestParam int page) {
        Page<ResProduct> availableProducts = productService.getAvailableProducts(page);
        return ResponseEntity.ok(availableProducts);
    }

    @GetMapping("/get-unavailable-products")
    public ResponseEntity<?> getUnAvailableProducts(@RequestParam int page) {
        Page<ResProduct> unAvailableProduct = productService.getUnAvailableProduct(page);
        return ResponseEntity.ok(unAvailableProduct);
    }

    @GetMapping("/get-one-by-id/{id}")
    public ResponseEntity<?> getOneProductById(@PathVariable UUID id) {
        ResProduct oneProduct = productService.getOneProduct(id);
        if (oneProduct == null) return ResponseEntity.status(409).body(oneProduct);
        return ResponseEntity.ok(oneProduct);

    }

    @GetMapping("/get-expired-products")
    public ResponseEntity<?> getExpiredProducts(@RequestParam int page) {
        Page<ResProduct> unAvailableProduct = productService.getExpiredProducts(page);
        return ResponseEntity.ok(unAvailableProduct);
    }

}
