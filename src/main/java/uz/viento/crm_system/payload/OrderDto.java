package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String comment;
    private String phoneNumber;
    private String additonalPhoneNumber;
    private String address;
    private long currencyId;
    private boolean isNewClient;
    private List<OrderOutputServiceDto> orderOutputServiceDtos;
    private List<ReqOutputProductDto> reqOutputProductDtos;
    private ReqCreateUserDto createUserDto;


}
