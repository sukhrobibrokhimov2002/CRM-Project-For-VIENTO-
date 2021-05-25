package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.entity.OrderOutputService;
import uz.viento.crm_system.entity.enums.OrderOutputStatus;
import uz.viento.crm_system.payload.OrderOutputServiceDto;
import uz.viento.crm_system.payload.ResOutputServiceDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.payload.ResponseApiWithObject;
import uz.viento.crm_system.repository.OrderOutputServiceRepository;
import uz.viento.crm_system.repository.ServiceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderOutputServiceLogic {


    @Autowired
    OrderOutputServiceRepository orderOutputServiceRepository;
    @Autowired
    ServiceRepository serviceRepository;


    public ResponseApiWithObject saveOrderService(OrderOutputStatus orderOutputStatus, UUID id) {
        Optional<uz.viento.crm_system.entity.Service> optionalService = serviceRepository.findById(id);
        if (!optionalService.isPresent()) {
            return new ResponseApiWithObject("Service not found", false, null);
        }
        uz.viento.crm_system.entity.Service service = optionalService.get();
        OrderOutputService orderOutputService = new OrderOutputService(
                service,
                service.getServiceFee(),
                orderOutputStatus
        );
        OrderOutputService savedOrderOutputService = orderOutputServiceRepository.save(orderOutputService);

        return new ResponseApiWithObject("Successfully added", true, savedOrderOutputService);

    }


    public ResponseApi delete(UUID id) {
        try {
            orderOutputServiceRepository.deleteById(id);
            return new ResponseApi("Successfully deleted", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting", false);
        }
    }

    public Page<ResOutputServiceDto> getAll(int page) {
        List<ResOutputServiceDto> list = new ArrayList<>();

        List<OrderOutputService> all = orderOutputServiceRepository.findAll();
        for (OrderOutputService orderOutputService : all) {
            ResOutputServiceDto resOutputServiceDto = new ResOutputServiceDto(
                    orderOutputService.getService().getName(),
                    orderOutputService.getTotalPrice()
            );
            list.add(resOutputServiceDto);
        }
        PageRequest pageRequest = PageRequest.of(page, 15);
        PageImpl<ResOutputServiceDto> resOutputServiceDtos = new PageImpl<>(list, pageRequest, list.size());
        return resOutputServiceDtos;
    }

//    public Page<ResOutputServiceDto> getByOrderId(UUID orderId, int page) {
//        List<ResOutputServiceDto> list = new ArrayList<>();
//
//        List<OrderOutputService> all = orderOutputServiceRepository.findAllByOrderId(orderId);
//        for (OrderOutputService orderOutputService : all) {
//            ResOutputServiceDto resOutputServiceDto = new ResOutputServiceDto(
//                    orderOutputService.getService().getName(),
//                    orderOutputService.getTotalPrice()
//            );
//            list.add(resOutputServiceDto);
//        }
//        PageRequest pageRequest = PageRequest.of(page, 15);
//        PageImpl<ResOutputServiceDto> resOutputServiceDtos = new PageImpl<>(list, pageRequest, list.size());
//        return resOutputServiceDtos;
//    }
}
