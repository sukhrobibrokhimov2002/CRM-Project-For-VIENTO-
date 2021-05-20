package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.Service;
import uz.viento.crm_system.payload.AddServiceDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.service.ServiceService;

import java.util.UUID;

@RestController
@RequestMapping("/service")
public class ServiceController {


    @Autowired
    ServiceService service;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody AddServiceDto addServiceDto) {
        ResponseApi responseApi = service.addService(addServiceDto);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @PatchMapping("/change-service-fee/{id}")
    public ResponseEntity<?> changeServiceFee(@RequestBody Double newFee, @PathVariable UUID id) {
        ResponseApi responseApi = service.changeServiceFee(newFee, id);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        ResponseApi responseApi = service.deleteService(id);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @PatchMapping("/disable-or-enable-service/{id}")
    public ResponseEntity<?> disableOrEnable(@PathVariable UUID id) {
        ResponseApi responseApi = service.disableOrEnableService(id);
        if (responseApi.isSuccess()) return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }

    @GetMapping("/get-all-services")
    public ResponseEntity<?> getAllServices(@RequestParam int page) {
        Page<Service> allServices = service.getAllServices(page);
        return ResponseEntity.ok(allServices);
    }

    @GetMapping("/get-available-services")
    public ResponseEntity<?> getAvailableServices(@RequestParam int page) {
        Page<Service> availableOrUnAvailableServices = service.getAvailableOrUnAvailableServices(true, page);
        return ResponseEntity.ok(availableOrUnAvailableServices);
    }

    @GetMapping("/get-unAvailable-services")
    public ResponseEntity<?> getUnAvailableServices(@RequestParam int page) {
        Page<Service> availableOrUnAvailableServices = service.getAvailableOrUnAvailableServices(false, page);
        return ResponseEntity.ok(availableOrUnAvailableServices);
    }


}
