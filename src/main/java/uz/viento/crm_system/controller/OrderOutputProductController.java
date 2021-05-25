package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.enums.OrderOutputStatus;
import uz.viento.crm_system.payload.ReqChangeProductQuantity;
import uz.viento.crm_system.payload.ResOutputProduct;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.service.OrderOutputProductService;

import java.util.UUID;

@RestController
@RequestMapping("/output-product")
public class OrderOutputProductController {

    @Autowired
    OrderOutputProductService orderOutputProductService;


    @PutMapping("/change-quantity-of-product/{id}")
    public ResponseEntity<?> add(@PathVariable UUID id, @RequestBody ReqChangeProductQuantity reqChangeProductQuantity) {
        ResponseApi responseApi = orderOutputProductService.changeQuantity(id, reqChangeProductQuantity);
        if (responseApi.isSuccess())
            return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        ResponseApi responseApi = orderOutputProductService.delete(id);
        if (responseApi.isSuccess())
            return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);

    }

    @GetMapping("/get-all")
    public ResponseEntity<?> delete(@RequestParam int page) {
        Page<ResOutputProduct> all = orderOutputProductService.getAll(page);
        if (!all.isEmpty())
            return ResponseEntity.ok(all);
        return ResponseEntity.status(409).body(all);

    }


    @GetMapping("/get-all-by-order-id/{orderId}")
    public ResponseEntity<?> getAllByOrderId(@PathVariable UUID orderId, @RequestParam int page) {
        Page<ResOutputProduct> allByOrderId = orderOutputProductService.getAllByOrderId(orderId, page);
        if (!allByOrderId.isEmpty())
            return ResponseEntity.ok(allByOrderId);
        return ResponseEntity.status(409).body(allByOrderId);
    }

    @GetMapping("/get-all-in-waiting-status")
    public ResponseEntity<?> getWaitingStatus(@RequestParam int page){
        Page<ResOutputProduct> byStatus = orderOutputProductService.getAllByStatus(page, OrderOutputStatus.WAITING);
        if (!byStatus.isEmpty())
            return ResponseEntity.ok(byStatus);
        return ResponseEntity.status(409).body(byStatus);

    }
    @GetMapping("/get-all-in-accepted-status")
    public ResponseEntity<?> getAcceptedStatus(@RequestParam int page){
        Page<ResOutputProduct> byStatus = orderOutputProductService.getAllByStatus(page, OrderOutputStatus.ACCEPTED);
        if (!byStatus.isEmpty())
            return ResponseEntity.ok(byStatus);
        return ResponseEntity.status(409).body(byStatus);

    }
}
