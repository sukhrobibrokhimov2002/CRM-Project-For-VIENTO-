package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.viento.crm_system.entity.Category;
import uz.viento.crm_system.entity.Service;
import uz.viento.crm_system.payload.AddServiceDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.repository.CategoryRepository;
import uz.viento.crm_system.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ServiceRepository serviceRepository;

    public ResponseApi addService(AddServiceDto addServiceDto) {
        Optional<Category> optionalCategory = categoryRepository.findById(addServiceDto.getCategoryId());
        if (!optionalCategory.isPresent()) return new ResponseApi("Category not found", false);
        Category category = optionalCategory.get();

        Service service = new Service(
                addServiceDto.getName(),
                addServiceDto.getDescription(),
                addServiceDto.isAvailable(),
                addServiceDto.getServiceFee(),
                category
        );
        serviceRepository.save(service);
        return new ResponseApi("Service successfully created", true);
    }

    public ResponseApi changeServiceFee(Double newFee, UUID id) {
        Optional<Service> optionalService = serviceRepository.findById(id);
        if (!optionalService.isPresent()) return new ResponseApi("Service not found", false);

        Service service = optionalService.get();
        service.setServiceFee(newFee);
        serviceRepository.save(service);
        return new ResponseApi("Service fee successfully changed", true);
    }

    public ResponseApi deleteService(UUID id) {
        try {
            serviceRepository.deleteById(id);
            return new ResponseApi("Service successfully deleted", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting", false);
        }
    }

    public ResponseApi disableOrEnableService(UUID id) {
        Optional<Service> optionalService = serviceRepository.findById(id);
        if (!optionalService.isPresent()) return new ResponseApi("Service not found", false);
        Service service = optionalService.get();
        if (service.isAvailable()) {
            service.setAvailable(false);
        } else {
            service.setAvailable(true);
        }
        serviceRepository.save(service);
        return new ResponseApi("Service status successfully changed", true);
    }

    public Page<Service> getAllServices(int page) {
        PageRequest pageRequest = PageRequest.of(page, 15);

        Page<Service> repositoryAll = serviceRepository.findAll(pageRequest);
        return repositoryAll;

    }

    public Page<Service> getAvailableOrUnAvailableServices(boolean available, int page) {
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<Service> byAvailable = serviceRepository.findByAvailable(available, pageRequest);
        return byAvailable;
    }
}
