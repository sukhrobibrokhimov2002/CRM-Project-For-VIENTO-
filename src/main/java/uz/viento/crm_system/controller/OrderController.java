package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.payload.OrderDto;
import uz.viento.crm_system.payload.ResAllOrders;
import uz.viento.crm_system.payload.ResOneOrder;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.service.OrderService;

import javax.xml.ws.Response;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PostMapping("/make-order")
    public ResponseEntity<?> makeOrder(@RequestBody OrderDto orderDto) {
        ResponseApi order = orderService.Order(orderDto);
        if (order.isSuccess()) return ResponseEntity.ok(order);
        return ResponseEntity.status(409).body(order);
    }

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PutMapping("/reject-order/{id}")
    public ResponseEntity<?> makeOrder(@PathVariable UUID id) {
        ResponseApi order = orderService.rejectOrder(id);
        if (order.isSuccess()) return ResponseEntity.ok(order);
        return ResponseEntity.status(409).body(order);
    }

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(@RequestParam int page) {
        Page<ResAllOrders> orderServiceAll = orderService.getAll(page);
        if (!orderServiceAll.isEmpty()) return ResponseEntity.ok(orderServiceAll);
        return ResponseEntity.status(409).body(orderServiceAll);
    }

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @GetMapping("/get-one-by-id/{id}")
    public ResponseEntity<?> getOneById(@PathVariable UUID id) {
        ResOneOrder oneOrder = orderService.getOneOrder(id);
        if (oneOrder != null) return ResponseEntity.ok(oneOrder);
        return ResponseEntity.status(409).body(oneOrder);
    }

    @PutMapping("/reject-order")
    public ResponseEntity<?> rejectOrder(@PathVariable UUID id) {
        ResponseApi responseApi = orderService.rejectOrder(id);
        if (responseApi.isSuccess())
            return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }
}
