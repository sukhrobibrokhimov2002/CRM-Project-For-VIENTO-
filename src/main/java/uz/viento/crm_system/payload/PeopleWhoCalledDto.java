package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.entity.Service;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.entity.enums.Status;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeopleWhoCalledDto {
    private String phoneNumber;
    private ReqCreateUserDto reqCreateUserDto;

    private List<UUID> serviceId;
    private List<ReqOutputProductDto> reqOutputProductDtos;
    private LocalDate whenShouldCall;
    private String comment;

}
